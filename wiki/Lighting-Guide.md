# Lighting Guide

FORE supports three light types: directional, point, and spot. Up to 16 lights can be active simultaneously, with shadow mapping from the first directional light.

## Light Types

### Directional Light

Simulates a distant light source like the sun. All rays are parallel in the given direction.

```java
Light sun = scene.addDirectionalLight(
        -0.5f, -1.0f, -0.3f,   // direction (x, y, z)
        1.0f, 0.95f, 0.9f,     // color (warm white)
        3.0f                    // intensity
);
sun.setCastsShadow(true);
```

**Parameters:**
- **Direction:** where the light points. `(0, -1, 0)` is straight down. The vector is normalized internally.
- **Color:** RGB light color in linear space.
- **Intensity:** multiplier for the light's contribution. HDR values (>1.0) are expected.

### Point Light

Emits light in all directions from a position, with distance-based attenuation.

```java
scene.addPointLight(
        3.0f, 3.0f, 3.0f,      // position (x, y, z)
        0.3f, 0.5f, 1.0f,      // color (blueish)
        15.0f,                  // intensity
        20.0f                   // range
);
```

**Attenuation formula** (from `pbr.frag`):
```glsl
float att = clamp(1.0 - pow(distance / range, 4.0), 0.0, 1.0);
return att * att / (distance * distance + 1.0);
```

The `range` parameter defines where the light's influence drops to zero.

### Spot Light

A cone of light from a position in a direction, with inner and outer cutoff angles.

```java
scene.addSpotLight(
        0, 8, 0,                // position
        0, -1, 0,               // direction (pointing down)
        1.0f, 0.9f, 0.8f,      // color
        40.0f,                  // intensity
        30.0f,                  // range
        15.0f,                  // inner cone angle (degrees)
        25.0f                   // outer cone angle (degrees)
);
```

Light is full intensity within the inner cone and fades to zero at the outer cone. This soft falloff prevents hard edges on the spot boundary.

## Shadow Mapping

FORE renders shadows using a 2048×2048 shadow map with PCF (Percentage-Closer Filtering) soft shadows. Only the **first directional light** with `setCastsShadow(true)` casts shadows.

```java
Light sun = scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 1.0f, 1.0f, 3.0f);
sun.setCastsShadow(true);
```

### Shadow Pass

The render pipeline has three passes:

1. **Shadow Pass:** Renders scene depth from the directional light's perspective into a 2048×2048 depth texture. Uses front-face culling to reduce shadow acne.
2. **Geometry Pass:** Renders the scene to an HDR framebuffer with PBR shading and shadow lookup.
3. **Post-Process Pass:** ACES tone mapping + gamma correction.

### Controlling Shadows Per Entity

```java
entity.setCastsShadow(true);    // This entity appears in the shadow map (default: true)
entity.setCastsShadow(false);   // Exclude from shadow map (e.g., floors, transparent objects)
```

## Exposure Control

FORE renders to an HDR framebuffer (RGBA16F). The post-process pass applies ACES tone mapping with an exposure multiplier:

- **Runtime:** press `+`/`-` keys to adjust
- **Configuration:** `fore.render.exposure=1.0` in `application.properties`
- **Code:** `renderSystem.setExposure(float)`

Higher exposure brightens the scene; lower exposure darkens it. The default `1.0` works well for most scenes.

## Ambient Light

There is no explicit ambient light object. The PBR shader adds a fixed ambient term:

```glsl
vec3 ambient = vec3(0.03) * albedo * ao;
```

This prevents completely black surfaces in unlit areas. The `ao` (ambient occlusion) property on materials modulates this term.

## Tips

- **Start with one directional light** and shadows enabled, then add point/spot lights for accents.
- **Keep intensity in HDR range** (1.0–50.0). The tone mapper handles bringing values back to displayable range.
- **Range matters:** a point light with range=10 has no effect at distance 11. Set range generously.
- **Max 16 lights:** the shader array has a fixed size of 16. Additional lights are ignored.

## API Reference

- [`Light`](https://rhajamor.github.io/fore/org/fore/light/Light.html) — static factory methods and property setters
- [`Scene.addDirectionalLight()`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html)
- [`Scene.addPointLight()`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html)
- [`Scene.addSpotLight()`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html)

## Further Reading

- [[Understanding-PBR]] — how light interacts with materials
- [[Materials-Guide]] — material properties that affect lighting
- [[Architecture-Overview]] — the three-pass render pipeline
