package org.fore.loader;

import org.fore.material.Material;
import org.fore.mesh.MeshData;
import org.fore.texture.Texture2D;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class ObjLoader {

    public static class ObjMesh {
        private final String name;
        private final MeshData meshData;
        private final Material material;

        public ObjMesh(String name, MeshData meshData, Material material) {
            this.name = name;
            this.meshData = meshData;
            this.material = material;
        }

        public String getName() { return name; }
        public MeshData getMeshData() { return meshData; }
        public Material getMaterial() { return material; }
    }

    public static List<ObjMesh> load(String objPath) {
        File objFile = new File(objPath);
        String baseDir = objFile.getParent() != null ? objFile.getParent() + File.separator : "";

        List<Vector3f> positions = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();

        Map<String, Material> materials = new HashMap<>();
        List<ObjMesh> meshes = new ArrayList<>();

        MeshData currentMesh = new MeshData();
        String currentName = "default";
        Material currentMaterial = new Material();
        Map<String, Integer> vertexCache = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(objFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "mtllib" -> {
                        String mtlPath = baseDir + parts[1];
                        materials.putAll(loadMtl(mtlPath, baseDir));
                    }
                    case "o", "g" -> {
                        if (currentMesh.getVertexCount() > 0) {
                            currentMesh.computeTangents();
                            meshes.add(new ObjMesh(currentName, currentMesh, currentMaterial));
                        }
                        currentName = parts.length > 1 ? parts[1] : "unnamed";
                        currentMesh = new MeshData();
                        vertexCache.clear();
                    }
                    case "usemtl" -> {
                        if (materials.containsKey(parts[1])) {
                            currentMaterial = materials.get(parts[1]);
                        }
                    }
                    case "v" -> positions.add(new Vector3f(
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])));
                    case "vn" -> normals.add(new Vector3f(
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])));
                    case "vt" -> texCoords.add(new Vector2f(
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2])));
                    case "f" -> {
                        int[] faceIndices = new int[parts.length - 1];
                        for (int i = 1; i < parts.length; i++) {
                            faceIndices[i - 1] = resolveVertex(
                                    parts[i], positions, normals, texCoords,
                                    currentMesh, vertexCache);
                        }
                        for (int i = 2; i < faceIndices.length; i++) {
                            currentMesh.addTriangle(faceIndices[0], faceIndices[i - 1], faceIndices[i]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load OBJ: " + objPath, e);
        }

        if (currentMesh.getVertexCount() > 0) {
            currentMesh.computeTangents();
            meshes.add(new ObjMesh(currentName, currentMesh, currentMaterial));
        }

        return meshes;
    }

    private static int resolveVertex(String vertexDef, List<Vector3f> positions,
                                     List<Vector3f> normals, List<Vector2f> texCoords,
                                     MeshData mesh, Map<String, Integer> cache) {
        if (cache.containsKey(vertexDef)) {
            return cache.get(vertexDef);
        }

        String[] parts = vertexDef.split("/");
        int posIdx = Integer.parseInt(parts[0]) - 1;
        Vector3f pos = positions.get(posIdx);

        Vector2f uv = new Vector2f();
        if (parts.length > 1 && !parts[1].isEmpty()) {
            uv = texCoords.get(Integer.parseInt(parts[1]) - 1);
        }

        Vector3f normal = new Vector3f(0, 1, 0);
        if (parts.length > 2 && !parts[2].isEmpty()) {
            normal = normals.get(Integer.parseInt(parts[2]) - 1);
        }

        int index = mesh.getVertexCount();
        mesh.addVertex(new Vector3f(pos), new Vector3f(normal), new Vector2f(uv));
        cache.put(vertexDef, index);
        return index;
    }

    private static Map<String, Material> loadMtl(String mtlPath, String baseDir) {
        Map<String, Material> materials = new HashMap<>();
        Material current = null;
        String currentName = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(mtlPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "newmtl" -> {
                        if (current != null) materials.put(currentName, current);
                        currentName = parts[1];
                        current = new Material();
                    }
                    case "Kd" -> {
                        if (current != null) {
                            current.setAlbedo(
                                    Float.parseFloat(parts[1]),
                                    Float.parseFloat(parts[2]),
                                    Float.parseFloat(parts[3]));
                        }
                    }
                    case "Ns" -> {
                        if (current != null) {
                            float shininess = Float.parseFloat(parts[1]);
                            current.setRoughness(1.0f - Math.min(shininess / 1000.0f, 1.0f));
                        }
                    }
                    case "d", "Tr" -> {
                        // Transparency — ignored for now
                    }
                    case "map_Kd" -> {
                        if (current != null) {
                            String texPath = baseDir + parts[parts.length - 1];
                            if (new File(texPath).exists()) {
                                current.setAlbedoMap(Texture2D.fromFile(texPath));
                            }
                        }
                    }
                    case "map_Bump", "bump", "map_bump" -> {
                        if (current != null) {
                            String texPath = baseDir + parts[parts.length - 1];
                            if (new File(texPath).exists()) {
                                current.setNormalMap(Texture2D.fromFileLinear(texPath));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // MTL file is optional
        }

        if (current != null) materials.put(currentName, current);
        return materials;
    }
}
