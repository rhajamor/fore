package org.fore.loader;

import jakarta.json.*;
import org.fore.material.Material;
import org.fore.mesh.MeshData;
import org.fore.texture.Texture2D;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GltfLoader {

    public static class GltfMesh {
        private final String name;
        private final MeshData meshData;
        private final Material material;

        public GltfMesh(String name, MeshData meshData, Material material) {
            this.name = name;
            this.meshData = meshData;
            this.material = material;
        }

        public String getName() { return name; }
        public MeshData getMeshData() { return meshData; }
        public Material getMaterial() { return material; }
    }

    public static List<GltfMesh> load(String path) {
        File file = new File(path);
        String baseDir = file.getParent() != null ? file.getParent() + File.separator : "";

        if (path.toLowerCase().endsWith(".glb")) {
            return loadGlb(file, baseDir);
        } else {
            return loadGltf(file, baseDir);
        }
    }

    private static List<GltfMesh> loadGltf(File file, String baseDir) {
        JsonObject root;
        try (JsonReader reader = Json.createReader(new FileReader(file))) {
            root = reader.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read glTF: " + file, e);
        }

        List<ByteBuffer> buffers = loadBuffers(root, baseDir);
        return parseMeshes(root, buffers, baseDir);
    }

    private static List<GltfMesh> loadGlb(File file, String baseDir) {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

            int magic = buf.getInt();
            if (magic != 0x46546C67) {
                throw new RuntimeException("Invalid GLB magic number");
            }
            int version = buf.getInt();
            int length = buf.getInt();

            // Chunk 0: JSON
            int jsonLength = buf.getInt();
            int jsonType = buf.getInt();
            byte[] jsonBytes = new byte[jsonLength];
            buf.get(jsonBytes);

            JsonObject root;
            try (JsonReader reader = Json.createReader(new ByteArrayInputStream(jsonBytes))) {
                root = reader.readObject();
            }

            // Chunk 1: BIN (optional)
            List<ByteBuffer> buffers = new ArrayList<>();
            if (buf.hasRemaining()) {
                int binLength = buf.getInt();
                int binType = buf.getInt();
                byte[] binData = new byte[binLength];
                buf.get(binData);
                buffers.add(ByteBuffer.wrap(binData).order(ByteOrder.LITTLE_ENDIAN));
            }

            return parseMeshes(root, buffers, baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read GLB: " + file, e);
        }
    }

    private static List<ByteBuffer> loadBuffers(JsonObject root, String baseDir) {
        List<ByteBuffer> buffers = new ArrayList<>();
        if (!root.containsKey("buffers")) return buffers;

        JsonArray bufferArray = root.getJsonArray("buffers");
        for (int i = 0; i < bufferArray.size(); i++) {
            JsonObject bufObj = bufferArray.getJsonObject(i);
            String uri = bufObj.getString("uri", null);
            if (uri != null) {
                try {
                    byte[] data = Files.readAllBytes(Path.of(baseDir + uri));
                    buffers.add(ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load buffer: " + uri, e);
                }
            }
        }
        return buffers;
    }

    private static List<GltfMesh> parseMeshes(JsonObject root, List<ByteBuffer> buffers, String baseDir) {
        List<GltfMesh> result = new ArrayList<>();
        if (!root.containsKey("meshes")) return result;

        JsonArray meshArray = root.getJsonArray("meshes");
        JsonArray accessors = root.getJsonArray("accessors");
        JsonArray bufferViews = root.getJsonArray("bufferViews");

        List<Material> materials = parseMaterials(root, buffers, baseDir);

        for (int m = 0; m < meshArray.size(); m++) {
            JsonObject mesh = meshArray.getJsonObject(m);
            String meshName = mesh.getString("name", "mesh_" + m);

            JsonArray primitives = mesh.getJsonArray("primitives");
            for (int p = 0; p < primitives.size(); p++) {
                JsonObject prim = primitives.getJsonObject(p);
                JsonObject attrs = prim.getJsonObject("attributes");

                MeshData meshData = new MeshData();

                float[] positions = readAccessorFloat(accessors, bufferViews, buffers, attrs.getInt("POSITION"));
                float[] normals = attrs.containsKey("NORMAL") ?
                        readAccessorFloat(accessors, bufferViews, buffers, attrs.getInt("NORMAL")) : null;
                float[] texCoords = attrs.containsKey("TEXCOORD_0") ?
                        readAccessorFloat(accessors, bufferViews, buffers, attrs.getInt("TEXCOORD_0")) : null;

                int vertexCount = positions.length / 3;
                for (int v = 0; v < vertexCount; v++) {
                    Vector3f pos = new Vector3f(positions[v * 3], positions[v * 3 + 1], positions[v * 3 + 2]);
                    Vector3f norm = normals != null ?
                            new Vector3f(normals[v * 3], normals[v * 3 + 1], normals[v * 3 + 2]) :
                            new Vector3f(0, 1, 0);
                    Vector2f uv = texCoords != null ?
                            new Vector2f(texCoords[v * 2], texCoords[v * 2 + 1]) :
                            new Vector2f();
                    meshData.addVertex(pos, norm, uv);
                }

                if (prim.containsKey("indices")) {
                    int[] indices = readAccessorInt(accessors, bufferViews, buffers, prim.getInt("indices"));
                    for (int i = 0; i < indices.length; i += 3) {
                        meshData.addTriangle(indices[i], indices[i + 1], indices[i + 2]);
                    }
                } else {
                    for (int i = 0; i < vertexCount; i += 3) {
                        meshData.addTriangle(i, i + 1, i + 2);
                    }
                }

                meshData.computeTangents();

                Material mat = new Material();
                if (prim.containsKey("material") && prim.getInt("material") < materials.size()) {
                    mat = materials.get(prim.getInt("material"));
                }

                String name = primitives.size() > 1 ? meshName + "_" + p : meshName;
                result.add(new GltfMesh(name, meshData, mat));
            }
        }

        return result;
    }

    private static List<Material> parseMaterials(JsonObject root, List<ByteBuffer> buffers, String baseDir) {
        List<Material> materials = new ArrayList<>();
        if (!root.containsKey("materials")) return materials;

        JsonArray matArray = root.getJsonArray("materials");
        JsonArray textures = root.containsKey("textures") ? root.getJsonArray("textures") : null;
        JsonArray images = root.containsKey("images") ? root.getJsonArray("images") : null;
        JsonArray accessors = root.containsKey("accessors") ? root.getJsonArray("accessors") : null;
        JsonArray bufferViews = root.containsKey("bufferViews") ? root.getJsonArray("bufferViews") : null;

        for (int i = 0; i < matArray.size(); i++) {
            JsonObject matObj = matArray.getJsonObject(i);
            Material mat = new Material();

            if (matObj.containsKey("pbrMetallicRoughness")) {
                JsonObject pbr = matObj.getJsonObject("pbrMetallicRoughness");

                if (pbr.containsKey("baseColorFactor")) {
                    JsonArray c = pbr.getJsonArray("baseColorFactor");
                    mat.setAlbedo(
                            (float) c.getJsonNumber(0).doubleValue(),
                            (float) c.getJsonNumber(1).doubleValue(),
                            (float) c.getJsonNumber(2).doubleValue());
                }

                if (pbr.containsKey("metallicFactor")) {
                    mat.setMetallic((float) pbr.getJsonNumber("metallicFactor").doubleValue());
                }

                if (pbr.containsKey("roughnessFactor")) {
                    mat.setRoughness((float) pbr.getJsonNumber("roughnessFactor").doubleValue());
                }

                if (pbr.containsKey("baseColorTexture") && textures != null && images != null) {
                    String texPath = resolveTexturePath(pbr.getJsonObject("baseColorTexture"), textures, images, buffers, bufferViews, baseDir);
                    if (texPath != null) {
                        mat.setAlbedoMap(Texture2D.fromFile(texPath));
                    }
                }

                if (pbr.containsKey("metallicRoughnessTexture") && textures != null && images != null) {
                    String texPath = resolveTexturePath(pbr.getJsonObject("metallicRoughnessTexture"), textures, images, buffers, bufferViews, baseDir);
                    if (texPath != null) {
                        mat.setMetallicRoughnessMap(Texture2D.fromFileLinear(texPath));
                    }
                }
            }

            if (matObj.containsKey("normalTexture") && textures != null && images != null) {
                String texPath = resolveTexturePath(matObj.getJsonObject("normalTexture"), textures, images, buffers, bufferViews, baseDir);
                if (texPath != null) {
                    mat.setNormalMap(Texture2D.fromFileLinear(texPath));
                }
            }

            if (matObj.containsKey("occlusionTexture") && textures != null && images != null) {
                String texPath = resolveTexturePath(matObj.getJsonObject("occlusionTexture"), textures, images, buffers, bufferViews, baseDir);
                if (texPath != null) {
                    mat.setAoMap(Texture2D.fromFileLinear(texPath));
                }
            }

            if (matObj.containsKey("emissiveTexture") && textures != null && images != null) {
                String texPath = resolveTexturePath(matObj.getJsonObject("emissiveTexture"), textures, images, buffers, bufferViews, baseDir);
                if (texPath != null) {
                    mat.setEmissiveMap(Texture2D.fromFile(texPath));
                }
            }

            if (matObj.containsKey("emissiveFactor")) {
                JsonArray e = matObj.getJsonArray("emissiveFactor");
                mat.setEmissive(
                        (float) e.getJsonNumber(0).doubleValue(),
                        (float) e.getJsonNumber(1).doubleValue(),
                        (float) e.getJsonNumber(2).doubleValue());
            }

            materials.add(mat);
        }

        return materials;
    }

    private static String resolveTexturePath(JsonObject texInfo, JsonArray textures, JsonArray images,
                                              List<ByteBuffer> buffers, JsonArray bufferViews, String baseDir) {
        int texIndex = texInfo.getInt("index");
        if (texIndex >= textures.size()) return null;

        JsonObject tex = textures.getJsonObject(texIndex);
        if (!tex.containsKey("source")) return null;

        int imgIndex = tex.getInt("source");
        if (imgIndex >= images.size()) return null;

        JsonObject img = images.getJsonObject(imgIndex);

        if (img.containsKey("uri")) {
            String uri = img.getString("uri");
            if (uri.startsWith("data:")) return null;
            String path = baseDir + uri;
            return new File(path).exists() ? path : null;
        }

        if (img.containsKey("bufferView") && bufferViews != null) {
            int bvIndex = img.getInt("bufferView");
            JsonObject bv = bufferViews.getJsonObject(bvIndex);
            int bufIndex = bv.getInt("buffer");
            int offset = bv.getInt("byteOffset", 0);
            int length = bv.getInt("byteLength");

            String mimeType = img.getString("mimeType", "image/png");
            String ext = mimeType.contains("jpeg") ? ".jpg" : ".png";

            try {
                File tmp = File.createTempFile("gltf_tex_", ext);
                tmp.deleteOnExit();
                ByteBuffer buf = buffers.get(bufIndex).duplicate().order(ByteOrder.LITTLE_ENDIAN);
                byte[] imgData = new byte[length];
                buf.position(offset);
                buf.get(imgData);
                Files.write(tmp.toPath(), imgData);
                return tmp.getAbsolutePath();
            } catch (IOException e) {
                return null;
            }
        }

        return null;
    }

    private static float[] readAccessorFloat(JsonArray accessors, JsonArray bufferViews,
                                              List<ByteBuffer> buffers, int accessorIndex) {
        JsonObject accessor = accessors.getJsonObject(accessorIndex);
        int count = accessor.getInt("count");
        int componentType = accessor.getInt("componentType");
        String type = accessor.getString("type");

        int components = switch (type) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4" -> 4;
            default -> throw new RuntimeException("Unsupported accessor type: " + type);
        };

        int bvIndex = accessor.getInt("bufferView");
        JsonObject bv = bufferViews.getJsonObject(bvIndex);
        int bufIndex = bv.getInt("buffer");
        int byteOffset = bv.getInt("byteOffset", 0) + accessor.getInt("byteOffset", 0);
        int byteStride = bv.getInt("byteStride", 0);

        ByteBuffer buf = buffers.get(bufIndex).duplicate().order(ByteOrder.LITTLE_ENDIAN);

        float[] result = new float[count * components];
        int elementSize = components * 4;
        int stride = byteStride > 0 ? byteStride : elementSize;

        for (int i = 0; i < count; i++) {
            buf.position(byteOffset + i * stride);
            for (int c = 0; c < components; c++) {
                result[i * components + c] = buf.getFloat();
            }
        }

        return result;
    }

    private static int[] readAccessorInt(JsonArray accessors, JsonArray bufferViews,
                                          List<ByteBuffer> buffers, int accessorIndex) {
        JsonObject accessor = accessors.getJsonObject(accessorIndex);
        int count = accessor.getInt("count");
        int componentType = accessor.getInt("componentType");

        int bvIndex = accessor.getInt("bufferView");
        JsonObject bv = bufferViews.getJsonObject(bvIndex);
        int bufIndex = bv.getInt("buffer");
        int byteOffset = bv.getInt("byteOffset", 0) + accessor.getInt("byteOffset", 0);

        ByteBuffer buf = buffers.get(bufIndex).duplicate().order(ByteOrder.LITTLE_ENDIAN);
        buf.position(byteOffset);

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = switch (componentType) {
                case 5121 -> buf.get() & 0xFF;                    // UNSIGNED_BYTE
                case 5123 -> buf.getShort() & 0xFFFF;             // UNSIGNED_SHORT
                case 5125 -> buf.getInt();                         // UNSIGNED_INT
                default -> throw new RuntimeException("Unsupported index type: " + componentType);
            };
        }

        return result;
    }
}
