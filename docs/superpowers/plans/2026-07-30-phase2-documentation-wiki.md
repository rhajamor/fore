# Phase 2: Documentation & Wiki — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A learner can go from zero to rendering their first custom scene. A library user can find any API entry point.

**Architecture:** 11 GitHub Wiki pages stored in `wiki/` directory in the repo (pushed to the wiki via `git`). Each page is standalone Markdown with cross-links to other wiki pages and Javadoc where relevant. The `_Sidebar.md` file provides persistent navigation.

**Tech Stack:** GitHub Wiki (Markdown), Javadoc (already deployed via Phase 1 `pages.yml`)

## Global Constraints

- Wiki pages live in `wiki/` directory at the repo root, pushed to the GitHub Wiki repo.
- Javadoc URL base: `https://rhajamor.github.io/fore/`
- Source repository: `https://github.com/rhajamor/fore`
- All code examples must compile against FORE 0.1.0 API — use the exact method signatures from the current source.
- All `fore.*` properties referenced must match `application.properties` defaults.
- Wiki cross-links use `[[Page-Name]]` GitHub Wiki syntax.
- No engine code changes in this phase.
- Java 21, Quarkus 3.17.5, LWJGL 3.3.4, JOML 1.10.8.

---

### Task 1: Wiki Skeleton and Navigation Sidebar

**Files:**
- Create: `wiki/_Sidebar.md`
- Create: `wiki/Home.md`

**Interfaces:**
- Consumes: Nothing
- Produces: `_Sidebar.md` navigation structure used by all subsequent wiki pages. `Home.md` overview page with links to all other wiki pages.

- [ ] **Step 1: Create `wiki/` directory and `_Sidebar.md`**

Create `wiki/_Sidebar.md` with the navigation for all 11 wiki pages:

```markdown
### FORE Engine Wiki

**Getting Started**
- [[Home]]
- [[Getting-Started]]
- [[Your-First-Scene]]

**Guides**
- [[Understanding-PBR]]
- [[Materials-Guide]]
- [[Lighting-Guide]]
- [[Camera-System]]
- [[Scene-Graph]]
- [[Geometry-Generator]]

**Reference**
- [[Architecture-Overview]]
- [[Configuration-Reference]]

**Links**
- [API Reference (Javadoc)](https://rhajamor.github.io/fore/)
- [Source Code](https://github.com/rhajamor/fore)
- [Contributing](https://github.com/rhajamor/fore/blob/master/CONTRIBUTING.md)
```

- [ ] **Step 2: Create `Home.md`**

Create `wiki/Home.md`:

```markdown
# FORE — Free OpenGL Rendering Engine

FORE is a modern, Ogre3D-inspired 3D rendering engine built with Java 21, LWJGL 3, and JOML. It implements a physically-based rendering (PBR) pipeline with Cook-Torrance BRDF, shadow mapping, and HDR tone mapping — all in ~3,000 lines of readable source code.

## What FORE Is

- A **learning engine** — small enough to read and understand completely
- A **PBR renderer** — Cook-Torrance with GGX distribution, Fresnel-Schlick, and Smith geometry
- A **lightweight core** — 14 packages, clean API, Quarkus CDI integration
- A **modern OpenGL 4.1** application with core profile, VAO/VBO/EBO, GLSL 410

## What FORE Is Not

- Not a full game engine (no audio, physics, UI, networking)
- Not an editor (no visual scene builder)
- Not production-grade (designed for learning and prototyping)

## Quick Links

| Resource | Description |
|----------|-------------|
| [[Getting-Started]] | Install prerequisites, build, and run FORE |
| [[Your-First-Scene]] | Write your first custom scene from scratch |
| [[Understanding-PBR]] | Learn how PBR works in FORE's shader pipeline |
| [[Materials-Guide]] | Create and configure PBR materials |
| [[Lighting-Guide]] | Add directional, point, and spot lights |
| [[Camera-System]] | Control the orbit and fly cameras |
| [[Scene-Graph]] | Build hierarchical node trees |
| [[Geometry-Generator]] | Generate built-in shapes (box, sphere, torus, etc.) |
| [[Architecture-Overview]] | Understand the render pipeline and package layout |
| [[Configuration-Reference]] | All `fore.*` configuration properties |
| [API Reference](https://rhajamor.github.io/fore/) | Full Javadoc |
```

- [ ] **Step 3: Verify links and structure**

Review both files for:
- All 11 wiki page names appear in the sidebar
- All 11 wiki page names are linked from Home.md
- Javadoc URL is correct: `https://rhajamor.github.io/fore/`
- Source URL is correct: `https://github.com/rhajamor/fore`

- [ ] **Step 4: Commit**

```bash
git add wiki/_Sidebar.md wiki/Home.md
git commit -m "docs(wiki): add Home page and navigation sidebar"
```

---

### Task 2: Getting Started

**Files:**
- Create: `wiki/Getting-Started.md`

**Interfaces:**
- Consumes: Sidebar from Task 1
- Produces: Standalone getting-started guide. Referenced by Home.md and Your-First-Scene.

- [ ] **Step 1: Create `Getting-Started.md`**

Create `wiki/Getting-Started.md`:

```markdown
# Getting Started

This guide walks you through installing prerequisites, building FORE, and running the default PBR showcase scene.

## Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java | 21+ (LTS) | [Adoptium Temurin](https://adoptium.net/) recommended |
| Gradle | 8.x | Included via wrapper (`./gradlew`) |
| GPU | OpenGL 4.1+ | Most GPUs from 2012 onwards |

### Installing Java 21

**macOS (Homebrew):**
```bash
brew install openjdk@21
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

**Linux (SDKMAN):**
```bash
sdk install java 21-open
```

**Windows (winget):**
```bash
winget install EclipseAdoptium.Temurin.21.JDK
```

Verify your installation:
```bash
java -version
# Should show: openjdk version "21.x.x"
```

## Clone and Build

```bash
git clone https://github.com/rhajamor/fore.git
cd fore
./gradlew quarkusBuild
```

The build produces an uber-jar at `build/fore-engine-0.1.0-runner.jar`.

## Run

The simplest way to run FORE:
```bash
./gradlew runEngine
```

This Gradle task automatically adds `-XstartOnFirstThread` on macOS (required by GLFW).

To run the jar directly:
```bash
# macOS:
java -XstartOnFirstThread -jar build/fore-engine-0.1.0-runner.jar

# Linux / Windows:
java -jar build/fore-engine-0.1.0-runner.jar
```

## What You Should See

The default scene is the **PBR Showcase** — a 7×7 grid of spheres demonstrating metallic and roughness values across the color spectrum, with a polished torus centerpiece.

## Controls

| Input | Action |
|-------|--------|
| Left Mouse + Drag | Orbit camera |
| Middle Mouse + Drag | Pan camera |
| Scroll Wheel | Zoom in/out |
| Tab | Toggle orbit/fly camera mode |
| W/A/S/D | Move (fly mode) |
| Space/Ctrl | Up/Down (fly mode) |
| Right Mouse + Drag | Look around (fly mode) |
| Shift | Sprint (fly mode) |
| 1–4 | Switch demo scenes |
| G | Toggle ground grid |
| +/− | Adjust exposure |
| Escape | Quit |

## Demo Scenes

| Key | Scene | Description |
|-----|-------|-------------|
| 1 | Basic | Sphere, cube, torus, cylinder with directional + point light |
| 2 | PBR Showcase | 7×7 metallic/roughness sphere grid (default) |
| 3 | Lighting Demo | Interior with 4 colored point lights, spotlight, pillars |
| 4 | Shapes | All built-in geometry types with metallic materials |

## Next Steps

- [[Your-First-Scene]] — write your own scene from scratch
- [[Understanding-PBR]] — learn how the PBR pipeline works
- [[Architecture-Overview]] — understand the engine internals
```

- [ ] **Step 2: Verify all code commands and paths are accurate**

Check that:
- `build/fore-engine-0.1.0-runner.jar` matches `build.gradle.kts` version
- `./gradlew runEngine` task exists in `build.gradle.kts`
- `./gradlew quarkusBuild` is the correct build command
- All keyboard controls match `Engine.java` `handleInput()` method
- Scene names match `Engine.createScene()` switch cases

- [ ] **Step 3: Commit**

```bash
git add wiki/Getting-Started.md
git commit -m "docs(wiki): add Getting Started guide"
```

---

### Task 3: Your First Scene

**Files:**
- Create: `wiki/Your-First-Scene.md`

**Interfaces:**
- Consumes: `ExampleScene` interface, `Scene` API, `GeometryGenerator`, `Material`, `Light` factory methods
- Produces: Step-by-step tutorial for creating a custom scene. Referenced by Getting-Started.

- [ ] **Step 1: Create `Your-First-Scene.md`**

Create `wiki/Your-First-Scene.md`:

```markdown
# Your First Scene

This tutorial walks you through creating a custom FORE scene from scratch. By the end, you'll have a lit, shaded scene with geometry you placed yourself.

## How Scenes Work

FORE scenes implement the [`ExampleScene`](https://rhajamor.github.io/fore/org/fore/examples/ExampleScene.html) interface:

```java
public interface ExampleScene {
    void setup(Scene scene);
}
```

The engine calls `setup()` once, passing you a [`Scene`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html) object. You populate it with geometry, materials, and lights. The engine handles rendering, camera, and input from there.

## Step 1: Create the Class

Create `src/main/java/org/fore/examples/MyScene.java`:

```java
package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class MyScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        // We'll fill this in step by step
    }
}
```

## Step 2: Add a Light

Every scene needs at least one light, or everything will be black. Add a directional light — it simulates sunlight with a direction, color, and intensity:

```java
@Override
public void setup(Scene scene) {
    // Direction (x, y, z), Color (r, g, b), Intensity
    scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.0f)
            .setCastsShadow(true);
}
```

The direction `(-0.5, -1.0, -0.3)` means light comes from the upper-left. The color `(1.0, 0.95, 0.9)` is warm white. Intensity `3.0` controls brightness before tone mapping.

## Step 3: Add a Floor

A floor provides context and receives shadows:

```java
Entity floor = scene.createEntity("floor",
        GeometryGenerator.plane(20, 20, 1, 1),
        new Material(new Vector3f(0.4f, 0.4f, 0.4f), 0.0f, 0.8f));
floor.setCastsShadow(false);
```

This creates a 20×20 unit plane with:
- **Albedo:** gray `(0.4, 0.4, 0.4)`
- **Metallic:** `0.0` (non-metal)
- **Roughness:** `0.8` (mostly rough)

We disable shadow casting on the floor since it's a receiver, not a caster.

## Step 4: Add Some Objects

Place a red sphere and a blue cube:

```java
// Red sphere
Entity sphere = scene.createEntity("sphere",
        GeometryGenerator.sphere(1.0f, 48, 48),
        new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
sphere.getNode().setPosition(0, 1.0f, 0);

// Blue cube
Entity cube = scene.createEntity("cube",
        GeometryGenerator.box(1.5f, 1.5f, 1.5f),
        new Material(new Vector3f(0.1f, 0.3f, 0.9f), 0.5f, 0.2f));
cube.getNode().setPosition(-3.0f, 0.75f, 0);
```

`GeometryGenerator.sphere(radius, sectors, stacks)` produces a UV sphere. Higher sector/stack counts make it smoother. `GeometryGenerator.box(width, height, depth)` produces a box centered at the origin.

Each entity gets a node in the scene graph. Use `getNode().setPosition(x, y, z)` to place it in world space.

## Step 5: Add a Point Light

Add a colored point light for more visual interest:

```java
scene.addPointLight(3.0f, 3.0f, 3.0f,    // position
        0.3f, 0.5f, 1.0f,                 // color (blueish)
        15.0f,                             // intensity
        20.0f);                            // range
```

Point lights emit in all directions from a position, with intensity falling off over the specified range.

## Step 6: Register Your Scene

Open `src/main/java/org/fore/core/Engine.java` and add your scene to the `createScene` switch:

```java
private Scene createScene(String name) {
    ExampleScene example = switch (name.toLowerCase()) {
        case "basic" -> new BasicScene();
        case "lighting" -> new LightingDemo();
        case "shapes" -> new ShapesShowcase();
        case "myscene" -> new MyScene();       // Add this line
        default -> new PBRShowcase();
    };

    Scene scene = new Scene(name);
    example.setup(scene);
    return scene;
}
```

## Step 7: Run It

Set your scene as the default in `src/main/resources/application.properties`:

```properties
fore.scene=myscene
```

Or launch the engine and press a number key to switch scenes, then add a key binding in `handleInput()`.

Build and run:
```bash
./gradlew runEngine
```

## Complete Code

```java
package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class MyScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        // Directional light (sunlight)
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.0f)
                .setCastsShadow(true);

        // Point light (blue accent)
        scene.addPointLight(3.0f, 3.0f, 3.0f, 0.3f, 0.5f, 1.0f, 15.0f, 20.0f);

        // Floor
        Entity floor = scene.createEntity("floor",
                GeometryGenerator.plane(20, 20, 1, 1),
                new Material(new Vector3f(0.4f, 0.4f, 0.4f), 0.0f, 0.8f));
        floor.setCastsShadow(false);

        // Red sphere
        Entity sphere = scene.createEntity("sphere",
                GeometryGenerator.sphere(1.0f, 48, 48),
                new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
        sphere.getNode().setPosition(0, 1.0f, 0);

        // Blue cube
        Entity cube = scene.createEntity("cube",
                GeometryGenerator.box(1.5f, 1.5f, 1.5f),
                new Material(new Vector3f(0.1f, 0.3f, 0.9f), 0.5f, 0.2f));
        cube.getNode().setPosition(-3.0f, 0.75f, 0);
    }
}
```

## Next Steps

- [[Materials-Guide]] — learn about all PBR material properties
- [[Lighting-Guide]] — explore the three light types in depth
- [[Geometry-Generator]] — discover all built-in shapes
```

- [ ] **Step 2: Verify all method signatures match source code**

Cross-check against the actual source:
- `ExampleScene.setup(Scene)` — matches `ExampleScene.java`
- `scene.addDirectionalLight(dx, dy, dz, r, g, b, intensity)` — matches `Scene.java:78`
- `Light.setCastsShadow(boolean)` — matches `Light.java:112`
- `scene.createEntity(name, meshData, material)` — matches `Scene.java:54`
- `GeometryGenerator.plane(width, depth, segW, segD)` — matches `GeometryGenerator.java:96`
- `GeometryGenerator.sphere(radius, sectors, stacks)` — matches `GeometryGenerator.java:57`
- `GeometryGenerator.box(width, height, depth)` — matches `GeometryGenerator.java:17`
- `Entity.setCastsShadow(boolean)` — matches `Entity.java:44`
- `Entity.getNode().setPosition(x, y, z)` — matches `SceneNode.java:82`
- `scene.addPointLight(x, y, z, r, g, b, intensity, range)` — matches `Scene.java:88`
- `Engine.createScene` switch statement — matches `Engine.java:91-98`

- [ ] **Step 3: Commit**

```bash
git add wiki/Your-First-Scene.md
git commit -m "docs(wiki): add Your First Scene tutorial"
```

---

### Task 4: Understanding PBR

**Files:**
- Create: `wiki/Understanding-PBR.md`

**Interfaces:**
- Consumes: PBR shader source (`pbr.frag`), `Material` class API
- Produces: Conceptual PBR guide. Referenced by Materials-Guide and Your-First-Scene.

- [ ] **Step 1: Create `Understanding-PBR.md`**

Create `wiki/Understanding-PBR.md`:

```markdown
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
```

- [ ] **Step 2: Verify shader code snippets match `pbr.frag`**

Cross-check:
- `distributionGGX` — matches `pbr.frag:52-59`
- `geometrySmith` — matches `pbr.frag:68-72`
- `fresnelSchlick` — matches `pbr.frag:75-77`
- `F0 = mix(vec3(0.04), albedo, metallic)` — matches `pbr.frag:137`
- Energy conservation `kS`/`kD` — matches `pbr.frag:175-176`
- Roughness clamping `max(roughness, 0.04)` — matches `pbr.frag:118`
- Material defaults — matches `Material.java` field initializers

- [ ] **Step 3: Commit**

```bash
git add wiki/Understanding-PBR.md
git commit -m "docs(wiki): add Understanding PBR guide"
```

---

### Task 5: Materials Guide and Lighting Guide

**Files:**
- Create: `wiki/Materials-Guide.md`
- Create: `wiki/Lighting-Guide.md`

**Interfaces:**
- Consumes: `Material` class API, `Light` class API, `Scene` light factory methods, `RenderSystem` shadow/exposure API
- Produces: Two reference guides. Cross-linked by Understanding-PBR and Your-First-Scene.

- [ ] **Step 1: Create `Materials-Guide.md`**

Create `wiki/Materials-Guide.md`:

```markdown
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
```

- [ ] **Step 2: Create `Lighting-Guide.md`**

Create `wiki/Lighting-Guide.md`:

```markdown
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
```

- [ ] **Step 3: Verify all code and parameters match source**

Cross-check against actual source:
- `Scene.addDirectionalLight(dx, dy, dz, r, g, b, intensity)` — 7 params, matches `Scene.java:78`
- `Scene.addPointLight(x, y, z, r, g, b, intensity, range)` — 8 params, matches `Scene.java:88`
- `Scene.addSpotLight(px, py, pz, dx, dy, dz, r, g, b, intensity, range, innerAngle, outerAngle)` — 13 params, matches `Scene.java:98`
- Shadow map size `2048` — matches `RenderSystem.SHADOW_MAP_SIZE`
- Max lights `16` — matches `RenderSystem.MAX_LIGHTS`
- Attenuation formula — matches `pbr.frag:100-103`
- Ambient `vec3(0.03) * albedo * ao` — matches `pbr.frag:189`
- `Entity.setCastsShadow()` — matches `Entity.java:44`
- `Light.setCastsShadow(boolean)` — matches `Light.java:112`

- [ ] **Step 4: Commit**

```bash
git add wiki/Materials-Guide.md wiki/Lighting-Guide.md
git commit -m "docs(wiki): add Materials Guide and Lighting Guide"
```

---

### Task 6: Camera System and Scene Graph

**Files:**
- Create: `wiki/Camera-System.md`
- Create: `wiki/Scene-Graph.md`

**Interfaces:**
- Consumes: `Camera`, `CameraController`, `Scene`, `SceneNode`, `Entity`, `Transform` APIs
- Produces: Two reference guides. Cross-linked by Your-First-Scene and Architecture-Overview.

- [ ] **Step 1: Create `Camera-System.md`**

Create `wiki/Camera-System.md`:

```markdown
# Camera System

FORE provides a perspective [`Camera`](https://rhajamor.github.io/fore/org/fore/camera/Camera.html) with two control modes managed by [`CameraController`](https://rhajamor.github.io/fore/org/fore/camera/CameraController.html): orbit and fly.

## Camera Properties

| Property | Default | Setter |
|----------|---------|--------|
| Position | `(0, 2, 5)` | `setPosition(x, y, z)` |
| Field of View | 60° | `setFov(degrees)` |
| Aspect Ratio | 16:9 | `setAspectRatio(float)` |
| Near Plane | 0.1 | `setClipPlanes(near, far)` |
| Far Plane | 500.0 | `setClipPlanes(near, far)` |

The camera produces two matrices:
- **View Matrix** — transforms world space to camera space (`getViewMatrix()`)
- **Projection Matrix** — perspective projection (`getProjectionMatrix()`)

Both use lazy recomputation — the matrix is only recalculated when a property changes.

## Orbit Mode (Default)

In orbit mode, the camera revolves around a target point. The user controls the orbit with mouse input.

| Input | Action |
|-------|--------|
| Left Mouse + Drag | Rotate around the target |
| Middle Mouse + Drag | Pan the target point |
| Scroll Wheel | Zoom in/out (changes orbit distance) |

### Programmatic Control

The engine creates the camera controller in `Engine.initialize()`:

```java
cameraController = new CameraController(camera, input);
cameraController.setMode(CameraController.Mode.ORBIT);
cameraController.setOrbitDistance(12.0f);
cameraController.setOrbitTarget(0, 1, 0);
```

**Orbit parameters:**
- `setOrbitDistance(float)` — distance from the target. Clamped between 1.0 and 100.0.
- `setOrbitTarget(float x, float y, float z)` — the point to orbit around.

## Fly Mode

Press **Tab** to switch to fly mode. The camera moves freely like a first-person game.

| Input | Action |
|-------|--------|
| W/A/S/D | Move forward/left/backward/right |
| Space | Move up |
| Left Ctrl | Move down |
| Right Mouse + Drag | Look around |
| Shift | Sprint (3× speed) |

### Programmatic Control

```java
cameraController.setMode(CameraController.Mode.FLY);
cameraController.setMoveSpeed(8.0f);          // Default: 8.0
cameraController.setLookSensitivity(0.003f);  // Default: 0.003
```

## Direct Camera Control

You can bypass the controller and manipulate the camera directly:

```java
camera.setPosition(5, 3, 10);
camera.lookAt(0, 0, 0);          // Point at the origin
camera.rotate(yawDelta, pitchDelta);  // Relative rotation
camera.move(dx, dy, dz);         // Move relative to camera axes
camera.setFov(45.0f);            // Narrower field of view
```

**`lookAt(x, y, z)`** — points the camera at a world position, computing yaw and pitch from the direction vector.

**`move(dx, dy, dz)`** — moves along the camera's local axes: dx = right, dy = up, dz = forward.

**`rotate(yawDelta, pitchDelta)`** — rotates around the world Y axis (yaw) and the camera's right axis (pitch). Pitch is clamped to prevent flipping.

## How Camera Integrates with the Engine

1. `Engine.initialize()` creates a `Camera` and `CameraController`
2. Each frame, `cameraController.update(deltaTime)` processes input and updates the camera
3. `RenderSystem.render()` reads `scene.getActiveCamera()` to get view/projection matrices
4. The PBR shader receives `view`, `projection`, and `viewPos` uniforms

## API Reference

- [`Camera`](https://rhajamor.github.io/fore/org/fore/camera/Camera.html) — position, orientation, projection
- [`CameraController`](https://rhajamor.github.io/fore/org/fore/camera/CameraController.html) — orbit and fly mode input handling
```

- [ ] **Step 2: Create `Scene-Graph.md`**

Create `wiki/Scene-Graph.md`:

```markdown
# Scene Graph

FORE uses a hierarchical scene graph inspired by Ogre3D. The graph organizes objects in a tree of nodes, where each node has a transform that's relative to its parent.

## Core Concepts

| Class | Role |
|-------|------|
| [`Scene`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html) | Container for the node tree, lights, and entities |
| [`SceneNode`](https://rhajamor.github.io/fore/org/fore/scene/SceneNode.html) | A position in the hierarchy with a local transform |
| [`Entity`](https://rhajamor.github.io/fore/org/fore/scene/Entity.html) | A visible object (mesh + material) attached to a node |
| [`Transform`](https://rhajamor.github.io/fore/org/fore/scene/Transform.html) | Position, rotation (quaternion), and scale |

## Creating Entities

The simplest way to add objects is through `Scene.createEntity()`:

```java
Entity sphere = scene.createEntity("sphere",
        GeometryGenerator.sphere(1.0f, 48, 48),
        new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
```

This does three things:
1. Creates a `Mesh` from the `MeshData` with the `POS_NORMAL_UV_TANGENT` vertex layout
2. Creates an `Entity` with that mesh and material
3. Creates a `SceneNode` named `"sphere_node"` and attaches the entity to it

## Positioning Entities

Every entity is attached to a node. Use the node to set position, scale, and rotation:

```java
entity.getNode().setPosition(3.0f, 1.0f, 0);
entity.getNode().setScale(2.0f);                    // Uniform scale
entity.getNode().setScale(1.0f, 2.0f, 1.0f);       // Non-uniform scale
entity.getNode().rotate(0.5f, 0, 1, 0);             // Rotate 0.5 rad around Y axis
```

## Node Hierarchy

Child nodes inherit their parent's transform. Moving a parent moves all children with it.

### Creating Child Nodes Manually

```java
// Create nodes under the root
SceneNode platform = scene.createNode("platform");
platform.setPosition(0, 0, 0);

// Create a child under "platform"
SceneNode turret = scene.createChildNode("platform", "turret");
turret.setPosition(0, 2, 0);    // 2 units above the platform in local space
```

### Attaching and Detaching Entities

```java
SceneNode node = scene.createNode("mount_point");
node.attachEntity(entity);
node.detachEntity(entity);
```

## Transform Details

Each [`Transform`](https://rhajamor.github.io/fore/org/fore/scene/Transform.html) has:

| Component | Type | Default | Methods |
|-----------|------|---------|---------|
| Position | `Vector3f` | `(0, 0, 0)` | `setPosition()`, `translate()` |
| Rotation | `Quaternionf` | identity | `setRotation()`, `setRotationEuler()`, `rotate()` |
| Scale | `Vector3f` | `(1, 1, 1)` | `setScale(uniform)`, `setScale(x, y, z)` |

### World vs. Local Matrices

- **Local Matrix** — `getLocalMatrix()` — combines position, rotation, and scale relative to the parent
- **World Matrix** — `getWorldMatrix()` — the final matrix used for rendering, computed as `parent.worldMatrix * local`

The world matrix is recomputed each frame when `scene.update()` traverses the tree (called by `RenderSystem.render()`).

### Rotation

Rotation uses quaternions internally. The convenience methods accept angle-axis:

```java
node.rotate(MathUtil.toRadians(45), 0, 1, 0);  // 45° around Y axis
```

For Euler angles:
```java
node.getTransform().setRotationEuler(
        MathUtil.toRadians(pitch),
        MathUtil.toRadians(yaw),
        MathUtil.toRadians(roll)
);
```

## Visibility

Nodes can be hidden, which also hides all children:

```java
node.setVisible(false);   // This node and all descendants are skipped during rendering
```

The `collectVisibleEntities()` traversal skips invisible nodes entirely.

## Shared Meshes

For many entities with the same geometry (e.g., a grid of spheres), create the mesh once and reuse it:

```java
Mesh sphereMesh = scene.createSharedMesh(GeometryGenerator.sphere(0.6f, 48, 48));

for (int i = 0; i < 10; i++) {
    Entity e = scene.createEntity("sphere_" + i, sphereMesh, material);
    e.getNode().setPosition(i * 2.0f, 0, 0);
}
```

`createSharedMesh()` returns a `Mesh` that the scene manages (disposes on `scene.dispose()`). Using the `createEntity(name, Mesh, Material)` overload avoids creating duplicate GPU buffers.

## Disposal

Call `scene.dispose()` to clean up all GPU resources (meshes). The engine does this automatically when switching scenes or shutting down.

## API Reference

- [`Scene`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html) — entity/node/light factory methods
- [`SceneNode`](https://rhajamor.github.io/fore/org/fore/scene/SceneNode.html) — hierarchy and transforms
- [`Entity`](https://rhajamor.github.io/fore/org/fore/scene/Entity.html) — mesh + material pairing
- [`Transform`](https://rhajamor.github.io/fore/org/fore/scene/Transform.html) — position, rotation, scale
```

- [ ] **Step 3: Verify method signatures and defaults**

Cross-check against actual source:
- Camera defaults: position `(0, 2, 5)`, fov `60°`, aspect `16/9`, near `0.1`, far `500` — matches `Camera.java`
- `CameraController.Mode.ORBIT` / `FLY` — matches `CameraController.java`
- `setOrbitDistance`, `setOrbitTarget`, `setMoveSpeed`, `setLookSensitivity` — matches `CameraController.java`
- `camera.lookAt`, `camera.move`, `camera.rotate` — matches `Camera.java`
- `scene.createEntity` auto-creates node named `entityName + "_node"` — matches `Scene.java:59`
- `scene.createNode`, `scene.createChildNode` — matches `Scene.java:34,40`
- `SceneNode.setPosition`, `setScale`, `rotate` — matches `SceneNode.java:82-100`
- `Transform` defaults and methods — matches `Transform.java`
- `scene.createSharedMesh` — matches `Scene.java:72`
- Orbit distance clamped between 1.0 and 100.0 — matches `CameraController.java:25-26`
- Sprint 3× speed — matches `CameraController.java:77`

- [ ] **Step 4: Commit**

```bash
git add wiki/Camera-System.md wiki/Scene-Graph.md
git commit -m "docs(wiki): add Camera System and Scene Graph guides"
```

---

### Task 7: Geometry Generator

**Files:**
- Create: `wiki/Geometry-Generator.md`

**Interfaces:**
- Consumes: `GeometryGenerator` static methods, `MeshData` class
- Produces: Shape reference guide. Cross-linked by Your-First-Scene and Scene-Graph.

- [ ] **Step 1: Create `Geometry-Generator.md`**

Create `wiki/Geometry-Generator.md`:

```markdown
# Geometry Generator

FORE includes procedural mesh generators for six built-in shapes. All generators are static methods on [`GeometryGenerator`](https://rhajamor.github.io/fore/org/fore/geometry/GeometryGenerator.html) and return [`MeshData`](https://rhajamor.github.io/fore/org/fore/mesh/MeshData.html) with positions, normals, texture coordinates, and tangents.

## Available Shapes

### Box

```java
MeshData box = GeometryGenerator.box(float width, float height, float depth);
```

A rectangular box centered at the origin. Each face has its own set of vertices with face-aligned normals and `[0,1]` UV mapping.

```java
// Unit cube
GeometryGenerator.box(1, 1, 1);

// Tall pillar
GeometryGenerator.box(0.5f, 3.0f, 0.5f);
```

### Sphere

```java
MeshData sphere = GeometryGenerator.sphere(float radius, int sectors, int stacks);
```

A UV sphere centered at the origin.

| Parameter | Description | Typical Value |
|-----------|-------------|---------------|
| `radius` | Sphere radius | 1.0 |
| `sectors` | Longitude divisions (horizontal) | 32–64 |
| `stacks` | Latitude divisions (vertical) | 16–64 |

Higher values produce smoother spheres at the cost of more triangles:
- `sphere(1, 16, 16)` — low-poly (480 tris)
- `sphere(1, 48, 48)` — smooth (4,512 tris)
- `sphere(1, 128, 128)` — very smooth (32,512 tris)

### Plane

```java
MeshData plane = GeometryGenerator.plane(float width, float depth, int segW, int segD);
```

A flat plane on the XZ plane (Y=0), facing up (normal = `(0, 1, 0)`).

| Parameter | Description | Typical Value |
|-----------|-------------|---------------|
| `width` | Size along X axis | 10–40 |
| `depth` | Size along Z axis | 10–40 |
| `segW` | Subdivisions along X | 1–10 |
| `segD` | Subdivisions along Z | 1–10 |

Subdivisions are useful if you plan to deform the mesh. For flat floors, `segW=1, segD=1` is sufficient.

### Torus

```java
MeshData torus = GeometryGenerator.torus(float majorRadius, float minorRadius,
                                         int majorSegments, int minorSegments);
```

A donut shape centered at the origin on the XZ plane.

| Parameter | Description | Typical Value |
|-----------|-------------|---------------|
| `majorRadius` | Distance from center to tube center | 1.0–2.0 |
| `minorRadius` | Tube thickness | 0.2–0.5 |
| `majorSegments` | Ring divisions around the torus | 32–64 |
| `minorSegments` | Tube cross-section divisions | 16–32 |

```java
// Standard torus
GeometryGenerator.torus(1.0f, 0.35f, 48, 24);

// Fat donut
GeometryGenerator.torus(1.5f, 0.5f, 96, 48);
```

### Cylinder

```java
MeshData cylinder = GeometryGenerator.cylinder(float radius, float height, int segments);
```

A capped cylinder centered vertically at the origin.

| Parameter | Description | Typical Value |
|-----------|-------------|---------------|
| `radius` | Cylinder radius | 0.5–2.0 |
| `height` | Total height | 1.0–3.0 |
| `segments` | Circumference divisions | 16–48 |

The cylinder includes top and bottom caps with disc UVs.

### Cone

```java
MeshData cone = GeometryGenerator.cone(float radius, float height, int segments);
```

A capped cone centered vertically at the origin, tip pointing up.

| Parameter | Description | Typical Value |
|-----------|-------------|---------------|
| `radius` | Base radius | 0.5–2.0 |
| `height` | Total height | 1.0–3.0 |
| `segments` | Circumference divisions | 16–48 |

The cone includes a bottom disc cap.

## Using Generated Meshes

### With `Scene.createEntity()`

The most common path — creates the GPU mesh, entity, and scene node in one call:

```java
Entity sphere = scene.createEntity("sphere",
        GeometryGenerator.sphere(1.0f, 48, 48),
        new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
sphere.getNode().setPosition(0, 1.0f, 0);
```

### With Shared Meshes

For many instances of the same shape, create the mesh once:

```java
Mesh sphereMesh = scene.createSharedMesh(GeometryGenerator.sphere(0.6f, 48, 48));

for (int i = 0; i < 49; i++) {
    Entity e = scene.createEntity("s_" + i, sphereMesh, materials[i]);
    e.getNode().setPosition(x, 0.8f, z);
}
```

## MeshData Internals

`MeshData` is a CPU-side builder that accumulates vertices and indices:

```java
MeshData data = new MeshData();
data.addVertex(position, normal, uv);        // Add a vertex
data.addTriangle(i0, i1, i2);                // Add a triangle by vertex indices
data.computeTangents();                       // Compute tangent vectors for normal mapping
```

When passed to `scene.createEntity()` or `Mesh.create()`, the data is uploaded to the GPU as interleaved vertex buffers (VAO/VBO/EBO) with the `POS_NORMAL_UV_TANGENT` layout.

## API Reference

- [`GeometryGenerator`](https://rhajamor.github.io/fore/org/fore/geometry/GeometryGenerator.html) — all shape factory methods
- [`MeshData`](https://rhajamor.github.io/fore/org/fore/mesh/MeshData.html) — CPU-side vertex/index data
- [`Mesh`](https://rhajamor.github.io/fore/org/fore/mesh/Mesh.html) — GPU-side mesh handle
```

- [ ] **Step 2: Verify method signatures match source**

Cross-check:
- `box(float, float, float)` — matches `GeometryGenerator.java:17`
- `sphere(float, int, int)` — matches `GeometryGenerator.java:57`
- `plane(float, float, int, int)` — matches `GeometryGenerator.java:96`
- `torus(float, float, int, int)` — matches `GeometryGenerator.java:132`
- `cylinder(float, float, int)` — matches `GeometryGenerator.java:174`
- `cone(float, float, int)` — matches `GeometryGenerator.java:222`
- `MeshData.addVertex`, `addTriangle`, `computeTangents` — matches `MeshData.java`
- `VertexLayout.POS_NORMAL_UV_TANGENT` — used in `Scene.createEntity` at `Scene.java:55`

- [ ] **Step 3: Commit**

```bash
git add wiki/Geometry-Generator.md
git commit -m "docs(wiki): add Geometry Generator reference"
```

---

### Task 8: Architecture Overview and Configuration Reference

**Files:**
- Create: `wiki/Architecture-Overview.md`
- Create: `wiki/Configuration-Reference.md`

**Interfaces:**
- Consumes: `Engine`, `RenderSystem`, `ForeApplication` boot sequence, `application.properties`, all package-info.java files
- Produces: Two reference pages that complete the 11-page wiki. Cross-linked by all other guides.

- [ ] **Step 1: Create `Architecture-Overview.md`**

Create `wiki/Architecture-Overview.md`:

```markdown
# Architecture Overview

FORE is organized into 14 packages totaling ~3,000 lines of Java. This page describes the render pipeline, package responsibilities, and the frame loop.

## Package Map

| Package | Responsibility | Key Classes |
|---------|---------------|-------------|
| `org.fore.app` | Quarkus bootstrap, GLFW thread management | `ForeApplication` |
| `org.fore.core` | Engine main loop, frame timing | `Engine`, `TimeStep` |
| `org.fore.window` | GLFW window creation, input system | `Window`, `InputSystem` |
| `org.fore.render` | OpenGL render pipeline, framebuffers | `RenderSystem`, `Framebuffer` |
| `org.fore.shader` | GLSL shader compilation, uniform management | `ShaderProgram` |
| `org.fore.scene` | Scene graph: nodes, entities, transforms | `Scene`, `SceneNode`, `Entity`, `Transform` |
| `org.fore.camera` | Perspective camera, orbit/fly controllers | `Camera`, `CameraController` |
| `org.fore.material` | PBR material system | `Material` |
| `org.fore.texture` | Texture loading and generation | `Texture2D` |
| `org.fore.mesh` | GPU mesh management (VAO/VBO/EBO) | `Mesh`, `MeshData`, `VertexLayout` |
| `org.fore.geometry` | Procedural geometry generators | `GeometryGenerator` |
| `org.fore.light` | Light types (directional, point, spot) | `Light` |
| `org.fore.math` | Math utilities (supplements JOML) | `MathUtil` |
| `org.fore.examples` | Built-in demo scenes | `BasicScene`, `PBRShowcase`, `LightingDemo`, `ShapesShowcase` |

## Boot Sequence

FORE must run the GLFW event loop on the main thread (Thread 0), which is a macOS requirement. Quarkus normally runs `QuarkusApplication.run()` on a separate thread, so FORE uses a custom bootstrap:

1. `ForeApplication.main()` launches Quarkus on a daemon thread
2. A `CountDownLatch` blocks until Quarkus CDI is ready (15s timeout)
3. `Engine` is retrieved from the CDI container via `Arc.container().instance(Engine.class)`
4. `engine.run()` executes on the main thread (Thread 0)

This ensures GLFW has the Cocoa main thread on macOS while Quarkus CDI manages dependency injection.

## Engine Lifecycle

```
Engine.run()
├── initialize()
│   ├── Create Window (GLFW, OpenGL 4.1 core, 4x MSAA)
│   ├── Create InputSystem
│   ├── Create RenderSystem (compile shaders, create framebuffers)
│   ├── Create Camera + CameraController (orbit mode, distance 12)
│   └── Create and setup active Scene
├── mainLoop()
│   └── while (!window.shouldClose())
│       ├── timeStep.update()          — compute delta time
│       ├── input.update()             — snapshot input state
│       ├── handleInput()              — scene switching, exposure, grid toggle
│       ├── handle window resize       — resize framebuffers, update aspect ratio
│       ├── cameraController.update()  — process orbit/fly input
│       ├── renderSystem.render()      — three-pass render (see below)
│       ├── window.swapBuffers()       — present frame
│       └── window.pollEvents()        — process OS events
└── shutdown()
    ├── scene.dispose()       — free GPU meshes
    ├── renderSystem.close()  — free shaders, framebuffers
    └── window.close()        — destroy GLFW context
```

## Render Pipeline

`RenderSystem.render(scene)` executes three passes per frame:

### Pass 1: Shadow Map

- **Target:** 2048×2048 depth-only framebuffer
- **Shader:** `shadow.vert` / `shadow.frag` (depth only)
- **Camera:** orthographic projection from the first directional light's perspective
- **Trick:** front-face culling (`GL_FRONT`) to reduce shadow acne
- **Output:** depth texture bound to texture unit 0 for the geometry pass

### Pass 2: PBR Geometry

- **Target:** HDR framebuffer (RGBA16F color + depth)
- **Shader:** `pbr.vert` / `pbr.frag`
- **Input:** scene entities, up to 16 lights, shadow map, camera matrices
- **Steps:**
  1. Render ground grid (if enabled) with alpha blending
  2. For each entity: set model/normal matrices, apply material uniforms + texture maps, draw mesh
- **BRDF:** Cook-Torrance with GGX distribution, Smith geometry, Fresnel-Schlick (see [[Understanding-PBR]])

### Pass 3: Post-Process

- **Target:** default framebuffer (screen)
- **Shader:** `postprocess.vert` / `postprocess.frag`
- **Input:** HDR color texture from pass 2
- **Operations:**
  1. ACES tone mapping with exposure multiplier
  2. Gamma correction (γ = 2.2)
- **Geometry:** full-screen quad

## Shader Programs

| Shader Pair | Purpose |
|-------------|---------|
| `pbr.vert` / `pbr.frag` | PBR lighting with Cook-Torrance BRDF |
| `shadow.vert` / `shadow.frag` | Depth-only pass for shadow map generation |
| `postprocess.vert` / `postprocess.frag` | ACES tone mapping + gamma correction |
| `grid.vert` / `grid.frag` | Ground reference grid with distance fade |

Shaders are loaded from `src/main/resources/shaders/` at engine initialization via `ShaderProgram.fromResources()`.

## Vertex Layout

All geometry uses the `POS_NORMAL_UV_TANGENT` interleaved layout:

| Attribute | Location | Components | Bytes |
|-----------|----------|------------|-------|
| Position | 0 | 3 (xyz) | 12 |
| Normal | 1 | 3 (xyz) | 12 |
| TexCoord | 2 | 2 (uv) | 8 |
| Tangent | 3 | 3 (xyz) | 12 |
| **Total** | | **11** | **44** |

Tangent vectors are computed by `MeshData.computeTangents()` using the Gram-Schmidt process, enabling tangent-space normal mapping in the PBR shader.

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 21 (LTS) |
| Framework | Quarkus | 3.17.5 |
| Graphics | LWJGL (OpenGL 4.1, GLFW) | 3.3.4 |
| Math | JOML | 1.10.8 |
| Build | Gradle (Kotlin DSL) | 8.x |
| Shading | GLSL | 410 Core |

## Further Reading

- [[Configuration-Reference]] — all configurable properties
- [[Understanding-PBR]] — deep dive into the Cook-Torrance BRDF
- [API Reference (Javadoc)](https://rhajamor.github.io/fore/)
```

- [ ] **Step 2: Create `Configuration-Reference.md`**

Create `wiki/Configuration-Reference.md`:

```markdown
# Configuration Reference

FORE uses Quarkus configuration via `src/main/resources/application.properties`. All engine properties use the `fore.*` prefix.

## Engine Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `fore.window.width` | `int` | `1600` | Window width in pixels |
| `fore.window.height` | `int` | `900` | Window height in pixels |
| `fore.window.title` | `String` | `FORE Engine — Free OpenGL Rendering Engine` | Window title bar text |
| `fore.render.exposure` | `float` | `1.0` | HDR exposure multiplier for tone mapping |
| `fore.render.vsync` | `boolean` | `true` | Enable vertical sync (swap interval 1) |
| `fore.scene` | `String` | `pbr` | Default scene to load on startup |

### Scene Names

The `fore.scene` property accepts these values:

| Value | Scene | Description |
|-------|-------|-------------|
| `basic` | `BasicScene` | Sphere, cube, torus, cylinder with two lights |
| `pbr` | `PBRShowcase` | 7×7 metallic/roughness grid (default) |
| `lighting` | `LightingDemo` | Interior with colored point lights and spotlight |
| `shapes` | `ShapesShowcase` | All geometry types with metallic materials |

## Quarkus Properties

These Quarkus properties are set in the default `application.properties`:

| Property | Value | Description |
|----------|-------|-------------|
| `quarkus.banner.enabled` | `false` | Suppress Quarkus startup banner |
| `quarkus.log.level` | `INFO` | Root log level |
| `quarkus.log.category."org.fore".level` | `INFO` | Engine-specific log level |
| `quarkus.package.jar.type` | `uber-jar` | Package as single executable jar |
| `quarkus.package.main-class` | `org.fore.app.ForeApplication` | Custom main class for GLFW thread control |

## Default Configuration File

```properties
# FORE Engine Configuration

# Window
fore.window.width=1600
fore.window.height=900
fore.window.title=FORE Engine — Free OpenGL Rendering Engine

# Rendering
fore.render.exposure=1.0
fore.render.vsync=true

# Scene (basic, pbr, lighting, shapes)
fore.scene=pbr

# Quarkus
quarkus.banner.enabled=false
quarkus.log.level=INFO
quarkus.log.category."org.fore".level=INFO

# Use uber-jar so we can control the main class (GLFW needs Thread 0 on macOS)
quarkus.package.jar.type=uber-jar
quarkus.package.main-class=org.fore.app.ForeApplication
```

## Overriding at Runtime

Quarkus supports property overrides via:

1. **System properties:** `-Dfore.window.width=1920`
2. **Environment variables:** `FORE_WINDOW_WIDTH=1920`
3. **`.env` file** in the working directory

Example:
```bash
java -XstartOnFirstThread -Dfore.scene=lighting -Dfore.render.exposure=1.5 \
     -jar build/fore-engine-0.1.0-runner.jar
```
```

- [ ] **Step 3: Verify all properties and values match source**

Cross-check:
- All `@ConfigProperty` annotations in `Engine.java:29-45` match the table
- `application.properties` content matches verbatim
- Scene name → class mapping matches `Engine.createScene()` switch at `Engine.java:91-98`
- Quarkus override mechanisms are accurate per Quarkus 3.x docs

- [ ] **Step 4: Commit**

```bash
git add wiki/Architecture-Overview.md wiki/Configuration-Reference.md
git commit -m "docs(wiki): add Architecture Overview and Configuration Reference"
```

---

### Task 9: Push Wiki to GitHub and Update README Links

**Files:**
- Modify: `README.md` — update wiki link if needed

**Interfaces:**
- Consumes: All 11 wiki pages from Tasks 1–8
- Produces: Wiki deployed to GitHub, README links verified

- [ ] **Step 1: Initialize the wiki repository**

GitHub Wiki is a separate git repository. Clone it and copy the wiki files:

```bash
# Clone the wiki repo (creates it if it doesn't exist — you may need to
# create the first wiki page via GitHub UI first, or push directly)
git clone https://github.com/rhajamor/fore.wiki.git /tmp/fore-wiki

# Copy all wiki files
cp wiki/*.md /tmp/fore-wiki/

# Commit and push
cd /tmp/fore-wiki
git add .
git commit -m "docs: add complete wiki with 11 tutorial and reference pages"
git push origin master
```

If the wiki repo doesn't exist yet, create the first page via GitHub UI (Settings → Wiki → create a page), then clone and overwrite.

- [ ] **Step 2: Verify wiki is accessible**

Open `https://github.com/rhajamor/fore/wiki` and verify:
- Sidebar navigation appears with all 11 pages
- Home page renders with the quick links table
- Cross-links between pages work (e.g., `[[Materials-Guide]]`)
- Javadoc links point to `https://rhajamor.github.io/fore/`

- [ ] **Step 3: Verify README wiki link**

Check that the README's Documentation section points to the correct wiki URL:

```markdown
- [Wiki — Tutorials & Guides](https://github.com/rhajamor/fore/wiki)
```

This was already set in Phase 1. If the link is correct, no change needed.

- [ ] **Step 4: Commit any README changes (if needed)**

```bash
git add README.md
git commit -m "docs: update README wiki link"
```

If no changes were needed, skip this step.
