# Loading Models

FORE supports loading 3D models in glTF 2.0 and Wavefront OBJ formats. This guide covers how to load models, what features are supported, and how to display them in scenes.

## Supported Formats

| Format | Extensions | Features |
|--------|-----------|----------|
| glTF 2.0 | `.gltf`, `.glb` | Meshes, PBR materials, textures (file + embedded), normals, UVs |
| Wavefront OBJ | `.obj` + `.mtl` | Meshes, basic materials, diffuse textures, normal maps |

glTF 2.0 is the recommended format — it's the standard for PBR content and supports all of FORE's material features.

## Loading a glTF Model

```java
import org.fore.loader.GltfLoader;

List<GltfLoader.GltfMesh> meshes = GltfLoader.load("assets/models/DamagedHelmet.glb");

for (GltfLoader.GltfMesh gm : meshes) {
    Entity entity = scene.createEntity(gm.getName(), gm.getMeshData(), gm.getMaterial());
    entity.getNode().setScale(2.0f);
}
```

Both `.gltf` (JSON + separate .bin and textures) and `.glb` (single binary) files are supported. The loader automatically detects the format from the file extension.

## Loading an OBJ Model

```java
import org.fore.loader.ObjLoader;

List<ObjLoader.ObjMesh> meshes = ObjLoader.load("assets/models/chair/chair.obj");

for (ObjLoader.ObjMesh om : meshes) {
    scene.createEntity(om.getName(), om.getMeshData(), om.getMaterial());
}
```

OBJ files reference an `.mtl` file for materials. Place both files in the same directory.

## What the Loaders Extract

**glTF loader reads:**
- Mesh positions, normals, texture coordinates
- PBR materials: base color, metallic, roughness, emissive
- Textures: albedo, normal, metallic-roughness, AO, emissive
- Both file-referenced and buffer-embedded textures

**OBJ loader reads:**
- Vertex positions, normals, texture coordinates
- Material properties: diffuse color (Kd), shininess (Ns)
- Textures: diffuse map (map_Kd), normal/bump map (map_Bump)
- Triangulation of n-gon faces

## Asset Directory Convention

```
assets/models/<model-name>/
├── model.gltf (or model.glb)
├── model.bin          (for non-binary glTF)
└── textures/          (referenced by the model)
```

Or for single-file GLB models:

```
assets/models/ModelName.glb
```

## Bundled Models

FORE ships with sample models from the [Khronos glTF Sample Assets](https://github.com/KhronosGroup/glTF-Sample-Assets):

| Model | Format | Description |
|-------|--------|-------------|
| DamagedHelmet.glb | GLB | Classic PBR test model with detailed textures |
| Lantern.glb | GLB | Detailed lantern model with multiple materials |

Press **6** to view the Model Showcase scene.

## Tips

- **GLB is easiest** — everything is packed in one file. Use `.gltf` only when you need to edit textures separately.
- **Scale varies widely** — models from different sources use different units. Use `entity.getNode().setScale()` to adjust.
- **Tangents are computed** — if the model doesn't include tangents, the loader computes them from positions and UVs via `MeshData.computeTangents()`.
- **Materials map directly** — glTF PBR metallic-roughness maps exactly to FORE's `Material` properties.

## Limitations

- No skeletal animation support
- No morph targets
- No multi-UV-set support (only TEXCOORD_0)
- No sparse accessors
- Scene hierarchy is flattened — all meshes are loaded at the same level

## API Reference

- [`GltfLoader`](https://rhajamor.github.io/fore/org/fore/loader/GltfLoader.html) — glTF 2.0 loader
- [`ObjLoader`](https://rhajamor.github.io/fore/org/fore/loader/ObjLoader.html) — Wavefront OBJ loader

## Further Reading

- [[Working-with-Textures]] — texture loading details
- [[Materials-Guide]] — PBR material properties
- [[Your-First-Scene]] — creating scenes
