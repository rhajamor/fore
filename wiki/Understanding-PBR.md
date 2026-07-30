# Understanding PBR

Physically-Based Rendering (PBR) is a shading approach that models how light interacts with surfaces in a way that approximates real-world physics. FORE implements the **metallic-roughness** PBR workflow using a Cook-Torrance BRDF.

## The Two Key Parameters

Every PBR material in FORE is primarily controlled by two parameters:

### Metallic (0.0 – 1.0)

Controls whether a surface behaves like a metal or a non-metal (dielectric).

| Value | Behavior | Examples |
|-------|----------|----------|
| 0.0 | Non-metal (dielectric) | Wood, plastic, stone, skin |
| 1.0 | Metal (conductor) | Gold, copper, iron, aluminum |

**What metallic does in the shader:**
- **Metals** reflect light as their albedo color (colored reflections). Almost no diffuse light.
- **Non-metals** reflect light as white/gray. Most light is diffuse (albedo color).

In code:
```java
new Material(new Vector3f(0.72f, 0.45f, 0.20f), 1.0f, 0.3f)  // Copper (metal)
new Material(new Vector3f(0.8f, 0.2f, 0.2f), 0.0f, 0.4f)      // Red plastic (non-metal)
```

### Roughness (0.0 – 1.0)

Controls how blurry or sharp reflections appear.

| Value | Appearance | Examples |
|-------|-----------|----------|
| 0.0 – 0.1 | Mirror-like, sharp reflections | Polished chrome, still water |
| 0.3 – 0.5 | Soft reflections, some sheen | Brushed metal, glazed ceramic |
| 0.7 – 1.0 | No visible reflections, matte | Concrete, rubber, chalk |

The shader clamps roughness to a minimum of `0.04` to avoid division-by-zero artifacts.

## How Cook-Torrance Works in FORE

FORE's PBR fragment shader (`pbr.frag`) computes the outgoing light using the Cook-Torrance reflectance equation:

```
Lo = (kD * albedo/π + specular) * radiance * NdotL
```

This equation has three main BRDF functions:

### 1. Normal Distribution Function — GGX/Trowbridge-Reitz

Controls the concentration of microfacet normals around the halfway vector. Higher roughness spreads the highlight wider.

```glsl
float distributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;
    float denom = NdotH2 * (a2 - 1.0) + 1.0;
    return a2 / (PI * denom * denom);
}
```

### 2. Geometry Function — Smith with Schlick-GGX

Accounts for self-shadowing of microfacets. Rough surfaces lose more light to occlusion.

```glsl
float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    return geometrySchlickGGX(NdotV, roughness) * geometrySchlickGGX(NdotL, roughness);
}
```

### 3. Fresnel — Schlick Approximation

At grazing angles, all surfaces become more reflective. F0 is the reflectance at normal incidence — `0.04` for non-metals, the albedo color for metals.

```glsl
vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}
```

In FORE's shader, F0 is computed as:
```glsl
vec3 F0 = mix(vec3(0.04), albedo, metallic);
```

## Energy Conservation

The shader enforces energy conservation: the sum of diffuse and specular energy never exceeds the incoming light.

```glsl
vec3 kS = F;                           // Specular contribution = Fresnel
vec3 kD = (1.0 - kS) * (1.0 - metallic); // Remaining goes to diffuse
```

Metals have no diffuse component (`kD = 0` when `metallic = 1.0`).

## Additional Material Properties

Beyond metallic and roughness, FORE's `Material` class supports:

| Property | Default | Description |
|----------|---------|-------------|
| `albedo` | `(0.8, 0.8, 0.8)` | Base color (RGB, linear space) |
| `metallic` | `0.0` | Metal vs. non-metal |
| `roughness` | `0.5` | Surface smoothness |
| `ao` | `1.0` | Ambient occlusion (1.0 = no occlusion) |
| `emissive` | `(0, 0, 0)` | Self-illumination color |

Each property also accepts an optional texture map — see [[Materials-Guide]] for details.

## HDR and Tone Mapping

FORE renders to an HDR framebuffer (RGBA16F) so light values can exceed 1.0. The post-process pass applies ACES tone mapping and gamma correction:

```
color_mapped = ACES(color_hdr * exposure)
color_final = pow(color_mapped, 1/2.2)
```

Adjust exposure at runtime with `+`/`-` keys, or set `fore.render.exposure` in `application.properties`.

## Further Reading

- [[Materials-Guide]] — creating and configuring materials in code
- [[Lighting-Guide]] — how the three light types interact with PBR
- [Real-Time Rendering](https://www.realtimerendering.com/) — the reference for PBR theory
- [LearnOpenGL PBR](https://learnopengl.com/PBR/Theory) — excellent interactive PBR tutorial
