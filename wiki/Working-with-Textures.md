# Working with Textures

FORE supports PBR texture maps for detailed surface rendering. This guide covers loading textures, applying them to materials, and working with the bundled texture sets.

## Texture Types

FORE supports five PBR texture channels:

| Channel | File Convention | Loading Method | Description |
|---------|----------------|----------------|-------------|
| Albedo | `albedo.png` | `Texture2D.fromFile()` | Base color (sRGB) |
| Normal | `normal.png` | `Texture2D.fromFileLinear()` | Surface detail via tangent-space normals |
| Metallic/Roughness | Combined or separate | `TextureUtil.combineMetallicRoughness()` | B=metallic, G=roughness |
| AO | `ao.png` | `Texture2D.fromFileLinear()` | Ambient occlusion (R channel) |
| Emissive | `emissive.png` | `Texture2D.fromFile()` | Self-illumination (sRGB) |

### sRGB vs. Linear

Color textures (albedo, emissive) store pixel values in sRGB color space. Use `Texture2D.fromFile()` — it tells OpenGL to automatically convert to linear on sampling.

Data textures (normal, metallic, roughness, AO) store raw data values. Use `Texture2D.fromFileLinear()` — no color space conversion is applied.

Using the wrong method will cause visual artifacts: albedo loaded as linear looks washed out; normals loaded as sRGB produce incorrect lighting.

## Loading Textures Manually

```java
import org.fore.texture.Texture2D;

// Color textures — sRGB (auto-linearized by OpenGL)
Texture2D albedo = Texture2D.fromFile("assets/textures/rusted-iron/albedo.png");

// Data textures — linear (no conversion)
Texture2D normal = Texture2D.fromFileLinear("assets/textures/rusted-iron/normal.png");
Texture2D ao = Texture2D.fromFileLinear("assets/textures/rusted-iron/ao.png");
```

## Applying Textures to Materials

```java
Material mat = new Material()
        .setAlbedoMap(Texture2D.fromFile("assets/textures/wood-planks/albedo.png"))
        .setNormalMap(Texture2D.fromFileLinear("assets/textures/wood-planks/normal.png"))
        .setAoMap(Texture2D.fromFileLinear("assets/textures/wood-planks/ao.png"))
        .setMetallic(0.0f)
        .setRoughness(0.7f);
```

You can mix texture maps with scalar values. If a map is set, it overrides the scalar for that channel. Scalars remain as fallbacks for channels without maps.

## Combining Separate Metallic and Roughness Maps

Some texture sources (like ambientCG) provide metallic and roughness as separate grayscale images. FORE's shader expects them combined in one texture (Blue=metallic, Green=roughness). Use the utility:

```java
import org.fore.texture.TextureUtil;

Texture2D mrMap = TextureUtil.combineMetallicRoughness(
        "assets/textures/rusted-iron/metallic.png",
        "assets/textures/rusted-iron/roughness.png");
mat.setMetallicRoughnessMap(mrMap);
```

## Loading a Full Material from a Directory

The convenience method `TextureUtil.loadPBRMaterial()` loads all available maps from a directory:

```java
Material rustedIron = TextureUtil.loadPBRMaterial("assets/textures/rusted-iron");
```

This checks for `albedo.png`, `normal.png`, `metallic.png`, `roughness.png`, and `ao.png` in the directory. Missing files are skipped — the material falls back to its scalar defaults for those channels.

## Asset Directory Convention

```
assets/textures/<set-name>/
├── albedo.png          (required — base color, sRGB)
├── normal.png          (recommended — tangent-space normals)
├── metallic.png        (optional — grayscale metalness)
├── roughness.png       (optional — grayscale roughness)
└── ao.png              (optional — ambient occlusion)
```

## Bundled Texture Sets

FORE ships with 6 CC0-licensed texture sets from [ambientCG](https://ambientcg.com/):

| Set | Type | Best For |
|-----|------|----------|
| `rusted-iron` | Metal | Weathered metal surfaces |
| `brushed-aluminum` | Metal | Clean metallic surfaces |
| `wood-planks` | Non-metal | Floors, furniture |
| `stone` | Non-metal | Walls, terrain |
| `concrete` | Non-metal | Floors, walls |
| `fabric` | Non-metal | Soft surfaces, upholstery |

All textures are 1024×1024 (1K resolution).

## Demo Scene

Press **5** to view the Textured Scene, which displays all bundled texture sets on spheres and cubes.

## Tips

- **Normal maps must be in OpenGL convention** (Y-up / green-up). DirectX-convention normals will have inverted lighting. ambientCG provides `NormalGL` variants.
- **Albedo textures are the biggest visual impact.** Start with just albedo + normal maps — add metallic/roughness/AO for polish.
- **Set scalar metallic/roughness even when using texture maps.** The scalars serve as fallbacks and help `TextureUtil.loadPBRMaterial()` produce reasonable results even when maps are missing.

## API Reference

- [`Texture2D`](https://rhajamor.github.io/fore/org/fore/texture/Texture2D.html) — texture loading methods
- [`TextureUtil`](https://rhajamor.github.io/fore/org/fore/texture/TextureUtil.html) — convenience loaders
- [`Material`](https://rhajamor.github.io/fore/org/fore/material/Material.html) — texture map setters

## Further Reading

- [[Materials-Guide]] — all material properties
- [[Understanding-PBR]] — PBR theory
- [[Your-First-Scene]] — creating scenes with materials
