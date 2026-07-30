# Materials Guide

Materials define how a surface looks. FORE uses PBR (Physically-Based Rendering) materials with the metallic-roughness workflow. See [[Understanding-PBR]] for the theory.

## Creating a Material

The [`Material`](https://rhajamor.github.io/fore/org/fore/material/Material.html) class has two constructors:

```java
// Default: gray, non-metal, medium roughness
Material mat = new Material();

// With albedo, metallic, roughness
Material copper = new Material(new Vector3f(0.72f, 0.45f, 0.20f), 1.0f, 0.3f);
```

All setters return `this` for chaining:

```java
Material mat = new Material()
        .setAlbedo(0.9f, 0.1f, 0.1f)
        .setMetallic(0.0f)
        .setRoughness(0.3f)
        .setAo(1.0f)
        .setEmissive(0, 0, 0);
```

## Properties

| Property | Type | Default | Shader Uniform |
|----------|------|---------|----------------|
| `albedo` | `Vector3f` | `(0.8, 0.8, 0.8)` | `material.albedo` |
| `metallic` | `float` | `0.0` | `material.metallic` |
| `roughness` | `float` | `0.5` | `material.roughness` |
| `ao` | `float` | `1.0` | `material.ao` |
| `emissive` | `Vector3f` | `(0, 0, 0)` | `material.emissive` |

### Albedo

The base color of the surface in linear RGB. For metals, this also defines the reflection color. For non-metals, this is the diffuse color.

```java
mat.setAlbedo(0.9f, 0.1f, 0.1f);         // Red
mat.setAlbedo(new Vector3f(0.72f, 0.45f, 0.20f)); // Copper
```

### Metallic

`0.0` = non-metal (plastic, wood, stone). `1.0` = metal (gold, iron, aluminum). Values between 0 and 1 are physically meaningless but can be used for artistic effect or to represent mixed surfaces.

### Roughness

`0.0` = perfectly smooth (mirror). `1.0` = completely rough (chalk). The shader internally clamps this to a minimum of `0.04`.

### Ambient Occlusion (AO)

`1.0` = fully lit. `0.0` = fully occluded. This multiplies the ambient light term in the shader. Typically comes from a baked AO map in production use.

### Emissive

Self-illumination color. Added directly to the final pixel color after PBR shading. Use for glowing objects:

```java
Material glowing = new Material()
        .setAlbedo(1.0f, 0.5f, 0.0f)
        .setEmissive(3.0f, 1.5f, 0.0f)   // Values > 1.0 for HDR glow
        .setMetallic(0).setRoughness(1);
```

## Texture Maps

Each scalar property has an optional texture map override. When a map is set, the shader samples the texture instead of using the scalar value.

```java
Material mat = new Material()
        .setAlbedoMap(Texture2D.fromFile("textures/albedo.png"))
        .setNormalMap(Texture2D.fromFile("textures/normal.png"))
        .setMetallicRoughnessMap(Texture2D.fromFile("textures/mr.png"))
        .setAoMap(Texture2D.fromFile("textures/ao.png"))
        .setEmissiveMap(Texture2D.fromFile("textures/emissive.png"));
```

| Map | Setter | Shader Sampling |
|-----|--------|----------------|
| Albedo | `setAlbedoMap()` | sRGB → linear conversion (`pow(tex, 2.2)`) |
| Normal | `setNormalMap()` | Tangent-space normal mapping via TBN matrix |
| Metallic/Roughness | `setMetallicRoughnessMap()` | Blue=metallic, Green=roughness (glTF convention) |
| AO | `setAoMap()` | Red channel only |
| Emissive | `setEmissiveMap()` | RGB color added to output |

Setting a map to `null` reverts to the scalar value:
```java
mat.setAlbedoMap(null); // Back to scalar albedo
```

## Common Material Recipes

```java
// Polished gold
new Material(new Vector3f(1.0f, 0.76f, 0.34f), 1.0f, 0.2f);

// Brushed copper
new Material(new Vector3f(0.72f, 0.45f, 0.20f), 1.0f, 0.3f);

// Chrome
new Material(new Vector3f(0.55f, 0.55f, 0.55f), 1.0f, 0.05f);

// Red plastic
new Material(new Vector3f(0.8f, 0.2f, 0.2f), 0.0f, 0.4f);

// Rubber
new Material(new Vector3f(0.1f, 0.1f, 0.12f), 0.0f, 0.95f);

// Concrete
new Material(new Vector3f(0.5f, 0.5f, 0.5f), 0.0f, 0.85f);
```

These are taken from the built-in demo scenes — see `ShapesShowcase.java` for reference.

## How Materials Are Applied

When you call `scene.createEntity(name, meshData, material)`, the material is stored on the [`Entity`](https://rhajamor.github.io/fore/org/fore/scene/Entity.html). During rendering, `RenderSystem` calls `material.apply(shader)` which sets all uniforms and binds texture maps to sequential texture units starting from unit 3 (units 0–2 are reserved for the shadow map and other engine textures).

## Further Reading

- [[Understanding-PBR]] — the theory behind these properties
- [[Lighting-Guide]] — how lights interact with materials
- [Material API (Javadoc)](https://rhajamor.github.io/fore/org/fore/material/Material.html)
