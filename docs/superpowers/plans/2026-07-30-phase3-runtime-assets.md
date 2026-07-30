# Phase 3: Runtime Assets & Textured Demos — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Demo scenes look professional with real PBR textures. The engine correctly loads and applies texture maps from disk.

**Architecture:** Fix the existing texture loading pipeline (sRGB double-correction bug), add a linear loading variant for data textures, bundle CC0 texture sets from ambientCG, create textured demo scenes, and document the workflow in the wiki.

**Tech Stack:** Java 21, LWJGL 3.3.4 (STB image, OpenGL 4.1), JOML 1.10.8, Quarkus 3.17.5

## Global Constraints

- CRITICAL: Do NOT add any Co-Authored-By lines, AI attribution, or "Generated with" disclaimers to commits or files.
- All texture assets must be CC0 licensed (ambientCG).
- Total bundled texture size target: under 50 MB.
- Asset directory convention: `assets/textures/<set-name>/` with `albedo.png`, `normal.png`, `roughness.png`, `metallic.png`, `ao.png`.
- Backwards compatible — existing scenes using scalar-only materials must continue to work unchanged.
- Existing `Material` API already supports all 5 texture map channels. The PBR shader already samples them. Do NOT redesign these — extend only.
- `.gitattributes` must track PNG files with Git LFS or mark as binary.

## Existing State (do not duplicate)

The engine already has:
- `Texture2D.fromFile(String path)` — loads via STB, but uses `GL_SRGB8_ALPHA8` for all textures (BUG: double gamma with shader's manual `pow(2.2)`)
- `Material` — has `setAlbedoMap()`, `setNormalMap()`, `setMetallicRoughnessMap()`, `setAoMap()`, `setEmissiveMap()` with boolean flags
- `pbr.frag` — samples all 5 maps when flags are set: albedo with `pow(2.2)`, metallicRoughness `.bg` channels, normal via TBN, AO `.r` channel

---

### Task 1: Fix Texture2D sRGB Handling and Add Linear Loading

**Files:**
- Modify: `src/main/java/org/fore/texture/Texture2D.java`
- Modify: `src/main/resources/shaders/pbr.frag` (line 108)

**Interfaces:**
- Consumes: Nothing
- Produces: `Texture2D.fromFile(String path)` uses `GL_SRGB8_ALPHA8` (sRGB, auto-linearized by OpenGL). `Texture2D.fromFileLinear(String path)` uses `GL_RGBA8` (linear, for data textures). Shader removes manual `pow(2.2)` for albedo since `GL_SRGB8_ALPHA8` handles it.

- [ ] **Step 1: Add `fromFileLinear()` method to Texture2D**

In `src/main/java/org/fore/texture/Texture2D.java`, add after the existing `fromFile()` method (line 41):

```java
public static Texture2D fromFileLinear(String path) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer channels = stack.mallocInt(1);

        stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = stbi_load(path, w, h, channels, 4);
        if (data == null) {
            throw new RuntimeException("Failed to load texture: " + path + " - " + stbi_failure_reason());
        }

        int texId = createTexture(w.get(0), h.get(0), data, GL_RGBA8);
        stbi_image_free(data);

        return new Texture2D(texId, w.get(0), h.get(0));
    }
}
```

This loads data textures (normal maps, metallic, roughness, AO) without sRGB conversion. Color textures (albedo, emissive) continue using `fromFile()` which uses `GL_SRGB8_ALPHA8`.

- [ ] **Step 2: Fix shader double-gamma correction**

In `src/main/resources/shaders/pbr.frag`, line 107-109, change:

```glsl
if (material.useAlbedoMap == 1) {
    albedo = pow(texture(materialAlbedoMap, TexCoord).rgb, vec3(2.2));
}
```

to:

```glsl
if (material.useAlbedoMap == 1) {
    albedo = texture(materialAlbedoMap, TexCoord).rgb;
}
```

Since `fromFile()` uses `GL_SRGB8_ALPHA8`, OpenGL automatically linearizes on sampling. The manual `pow(2.2)` was double-correcting. Removing it fixes the color accuracy.

- [ ] **Step 3: Verify build compiles**

```bash
./gradlew compileJava
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/org/fore/texture/Texture2D.java src/main/resources/shaders/pbr.frag
git commit -m "fix: correct sRGB handling in texture loading and PBR shader"
```

---

### Task 2: Add Texture Combining Utility

**Files:**
- Create: `src/main/java/org/fore/texture/TextureUtil.java`

**Interfaces:**
- Consumes: `Texture2D.fromFileLinear()` from Task 1
- Produces: `TextureUtil.combineMetallicRoughness(String metallicPath, String roughnessPath)` returns a `Texture2D` with B=metallic, G=roughness (matching shader's `.bg` sampling). `TextureUtil.loadPBRMaterial(String directory)` loads a full material from an `assets/textures/<name>/` directory.

- [ ] **Step 1: Create TextureUtil.java**

Create `src/main/java/org/fore/texture/TextureUtil.java`:

```java
package org.fore.texture;

import org.fore.material.Material;
import org.lwjgl.system.MemoryStack;
import org.joml.Vector3f;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureUtil {

    public static Texture2D combineMetallicRoughness(String metallicPath, String roughnessPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer mW = stack.mallocInt(1), mH = stack.mallocInt(1), mC = stack.mallocInt(1);
            IntBuffer rW = stack.mallocInt(1), rH = stack.mallocInt(1), rC = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer metalData = stbi_load(metallicPath, mW, mH, mC, 1);
            ByteBuffer roughData = stbi_load(roughnessPath, rW, rH, rC, 1);

            if (metalData == null || roughData == null) {
                if (metalData != null) stbi_image_free(metalData);
                if (roughData != null) stbi_image_free(roughData);
                throw new RuntimeException("Failed to load metallic/roughness textures");
            }

            int width = mW.get(0);
            int height = mH.get(0);

            ByteBuffer combined = ByteBuffer.allocateDirect(width * height * 4);
            for (int i = 0; i < width * height; i++) {
                int metallic = metalData.get(i) & 0xFF;
                int roughness = roughData.get(i) & 0xFF;
                combined.put((byte) 0);           // R: unused
                combined.put((byte) roughness);   // G: roughness
                combined.put((byte) metallic);    // B: metallic
                combined.put((byte) 255);         // A: unused
            }
            combined.flip();

            stbi_image_free(metalData);
            stbi_image_free(roughData);

            int tex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, tex);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, combined);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);

            return new Texture2D(tex, width, height);
        }
    }

    public static Material loadPBRMaterial(String directory) {
        Material mat = new Material();

        File dir = new File(directory);
        File albedo = new File(dir, "albedo.png");
        File normal = new File(dir, "normal.png");
        File metallic = new File(dir, "metallic.png");
        File roughness = new File(dir, "roughness.png");
        File ao = new File(dir, "ao.png");

        if (albedo.exists()) {
            mat.setAlbedoMap(Texture2D.fromFile(albedo.getAbsolutePath()));
        }
        if (normal.exists()) {
            mat.setNormalMap(Texture2D.fromFileLinear(normal.getAbsolutePath()));
        }
        if (metallic.exists() && roughness.exists()) {
            mat.setMetallicRoughnessMap(combineMetallicRoughness(
                    metallic.getAbsolutePath(), roughness.getAbsolutePath()));
        }
        if (ao.exists()) {
            mat.setAoMap(Texture2D.fromFileLinear(ao.getAbsolutePath()));
        }

        return mat;
    }
}
```

Note: `Texture2D` constructor is private. The `combineMetallicRoughness` method needs the package-private constructor. Since `TextureUtil` is in the same package (`org.fore.texture`), change `Texture2D`'s constructor from `private` to package-private:

In `Texture2D.java` line 18, change:
```java
private Texture2D(int id, int width, int height) {
```
to:
```java
Texture2D(int id, int width, int height) {
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew compileJava
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/fore/texture/TextureUtil.java src/main/java/org/fore/texture/Texture2D.java
git commit -m "feat: add texture combining utility and PBR material loader"
```

---

### Task 3: Download and Bundle CC0 Texture Sets

**Files:**
- Create: `assets/textures/` directory with 6 texture sets
- Create: `assets/textures/CREDITS.md` (CC0 attribution)
- Modify: `.gitattributes` (track PNGs as binary)

**Interfaces:**
- Consumes: Nothing
- Produces: 6 texture set directories under `assets/textures/`, each with `albedo.png`, `normal.png`, `metallic.png`, `roughness.png`, `ao.png`.

The texture sets to download from ambientCG (1K resolution, CC0):

| Set Name | ambientCG ID | Type |
|----------|-------------|------|
| `rusted-iron` | `Metal007` | Metal |
| `brushed-aluminum` | `Metal034` | Metal |
| `wood-planks` | `Wood023` | Non-metal |
| `stone` | `Rock020` | Non-metal |
| `concrete` | `Concrete034` | Non-metal |
| `fabric` | `Fabric030` | Non-metal |

- [ ] **Step 1: Create `.gitattributes`**

Create `.gitattributes` at the repo root:

```
# Binary assets
*.png binary
*.jpg binary
*.hdr binary
```

- [ ] **Step 2: Create assets directory structure**

```bash
mkdir -p assets/textures
```

- [ ] **Step 3: Download texture sets from ambientCG**

For each texture set, download the 1K PNG zip from ambientCG, extract, and rename files to the standard naming convention:

```bash
cd assets/textures

download_set() {
    local id=$1
    local name=$2
    mkdir -p "$name"
    curl -L -o "/tmp/${id}.zip" "https://ambientcg.com/get?file=${id}_1K-PNG.zip"
    unzip -o "/tmp/${id}.zip" -d "/tmp/${id}"
    
    # Map ambientCG naming to our convention
    cp "/tmp/${id}/${id}_1K-Color.png"              "$name/albedo.png"    2>/dev/null || true
    cp "/tmp/${id}/${id}_1K_Color.png"               "$name/albedo.png"    2>/dev/null || true
    cp "/tmp/${id}/${id}_1K-NormalGL.png"            "$name/normal.png"    2>/dev/null || true
    cp "/tmp/${id}/${id}_1K_NormalGL.png"             "$name/normal.png"    2>/dev/null || true
    cp "/tmp/${id}/${id}_1K-Metalness.png"           "$name/metallic.png"  2>/dev/null || true
    cp "/tmp/${id}/${id}_1K_Metalness.png"            "$name/metallic.png"  2>/dev/null || true
    cp "/tmp/${id}/${id}_1K-Roughness.png"           "$name/roughness.png" 2>/dev/null || true
    cp "/tmp/${id}/${id}_1K_Roughness.png"            "$name/roughness.png" 2>/dev/null || true
    cp "/tmp/${id}/${id}_1K-AmbientOcclusion.png"    "$name/ao.png"        2>/dev/null || true
    cp "/tmp/${id}/${id}_1K_AmbientOcclusion.png"     "$name/ao.png"        2>/dev/null || true
    
    rm -rf "/tmp/${id}" "/tmp/${id}.zip"
    echo "Downloaded: $name"
}

download_set Metal007 rusted-iron
download_set Metal034 brushed-aluminum
download_set Wood023 wood-planks
download_set Rock020 stone
download_set Concrete034 concrete
download_set Fabric030 fabric
```

If ambientCG naming convention has changed, adapt by listing the zip contents first:
```bash
unzip -l "/tmp/${id}.zip"
```

For non-metal texture sets (wood, stone, concrete, fabric) that don't have a metallic map, create a 1×1 black PNG as metallic (metallic=0):
```bash
# If metallic.png is missing for non-metal sets, the TextureUtil.loadPBRMaterial()
# method handles this gracefully — it just won't set the metallicRoughnessMap,
# and the Material defaults (metallic=0.0, roughness=0.5) will be used.
# No placeholder needed.
```

- [ ] **Step 4: Create CREDITS.md**

Create `assets/textures/CREDITS.md`:

```markdown
# Texture Credits

All textures in this directory are licensed under CC0 1.0 Universal (Public Domain).

Source: [ambientCG](https://ambientcg.com/) by Lennart Demes

| Directory | Source ID | Description |
|-----------|----------|-------------|
| rusted-iron | Metal007 | Rusted iron surface |
| brushed-aluminum | Metal034 | Brushed aluminum |
| wood-planks | Wood023 | Wooden planks |
| stone | Rock020 | Natural stone |
| concrete | Concrete034 | Concrete surface |
| fabric | Fabric030 | Woven fabric |

Resolution: 1K (1024×1024)
License: CC0 1.0 — https://creativecommons.org/publicdomain/zero/1.0/
```

- [ ] **Step 5: Verify all sets have at minimum albedo and normal maps**

```bash
for dir in assets/textures/*/; do
    echo "=== $(basename $dir) ==="
    ls "$dir"
done
```

Every set must have at least `albedo.png` and `normal.png`. Roughness and AO are optional (the loader handles missing files).

- [ ] **Step 6: Commit**

```bash
git add .gitattributes assets/textures/
git commit -m "assets: bundle 6 CC0 texture sets from ambientCG (1K resolution)"
```

---

### Task 4: Create TexturedScene Example and Register in Engine

**Files:**
- Create: `src/main/java/org/fore/examples/TexturedScene.java`
- Modify: `src/main/java/org/fore/core/Engine.java` (lines 92-96, 148-151)

**Interfaces:**
- Consumes: `TextureUtil.loadPBRMaterial(String dir)` from Task 2. `GeometryGenerator` shapes. `Scene.createEntity()`, `Scene.addDirectionalLight()`, `Scene.addPointLight()`. Texture sets in `assets/textures/` from Task 3.
- Produces: `TexturedScene` class implementing `ExampleScene`. New scene registered as `"textured"` in `Engine.createScene()` switch, bound to key `5`.

- [ ] **Step 1: Create TexturedScene.java**

Create `src/main/java/org/fore/examples/TexturedScene.java`:

```java
package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.mesh.Mesh;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.fore.texture.TextureUtil;
import org.joml.Vector3f;

import java.io.File;

public class TexturedScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.5f)
                .setCastsShadow(true);
        scene.addPointLight(5.0f, 4.0f, 5.0f, 0.5f, 0.7f, 1.0f, 15.0f, 25.0f);
        scene.addPointLight(-4.0f, 3.0f, -3.0f, 1.0f, 0.5f, 0.3f, 12.0f, 20.0f);

        // Textured floor
        Material floorMat = loadMaterialOrFallback("assets/textures/concrete",
                new Vector3f(0.5f, 0.5f, 0.5f), 0.0f, 0.85f);
        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(20, 20, 1, 1), floorMat);
        floor.setCastsShadow(false);

        Mesh sphereMesh = scene.createSharedMesh(GeometryGenerator.sphere(1.0f, 64, 64));
        Mesh cubeMesh = scene.createSharedMesh(GeometryGenerator.box(1.8f, 1.8f, 1.8f));

        // Textured objects in a row
        String[] sets = {"rusted-iron", "brushed-aluminum", "wood-planks", "stone", "concrete", "fabric"};
        Vector3f[] fallbackColors = {
                new Vector3f(0.56f, 0.29f, 0.15f),  // rusted iron
                new Vector3f(0.77f, 0.78f, 0.78f),   // aluminum
                new Vector3f(0.55f, 0.35f, 0.18f),   // wood
                new Vector3f(0.5f, 0.5f, 0.48f),     // stone
                new Vector3f(0.6f, 0.6f, 0.6f),      // concrete
                new Vector3f(0.3f, 0.15f, 0.45f)     // fabric
        };
        float[] metallicDefaults = {1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] roughnessDefaults = {0.6f, 0.25f, 0.7f, 0.8f, 0.9f, 0.85f};

        float startX = -(sets.length - 1) * 2.5f / 2.0f;

        for (int i = 0; i < sets.length; i++) {
            Material mat = loadMaterialOrFallback("assets/textures/" + sets[i],
                    fallbackColors[i], metallicDefaults[i], roughnessDefaults[i]);

            // Sphere on top
            Entity sphere = scene.createEntity(sets[i] + "_sphere", sphereMesh, mat);
            sphere.getNode().setPosition(startX + i * 2.5f, 2.8f, 0);

            // Cube below
            Entity cube = scene.createEntity(sets[i] + "_cube", cubeMesh, mat);
            cube.getNode().setPosition(startX + i * 2.5f, 0.9f, 0);
            cube.getNode().rotate(0.3f, 0, 1, 0);
        }

        // Large textured back wall
        Material wallMat = loadMaterialOrFallback("assets/textures/stone",
                new Vector3f(0.5f, 0.5f, 0.48f), 0.0f, 0.8f);
        Entity wall = scene.createEntity("wall", GeometryGenerator.plane(20, 6, 1, 1), wallMat);
        wall.getNode().setPosition(0, 3, -5);
        wall.getNode().rotate((float) Math.toRadians(90), 1, 0, 0);
    }

    private Material loadMaterialOrFallback(String directory, Vector3f fallbackAlbedo,
                                             float fallbackMetallic, float fallbackRoughness) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            Material mat = TextureUtil.loadPBRMaterial(directory);
            mat.setMetallic(fallbackMetallic);
            mat.setRoughness(fallbackRoughness);
            return mat;
        }
        return new Material(fallbackAlbedo, fallbackMetallic, fallbackRoughness);
    }
}
```

- [ ] **Step 2: Register scene in Engine.java**

In `src/main/java/org/fore/core/Engine.java`, modify the `createScene` switch (line 92-96):

```java
ExampleScene example = switch (name.toLowerCase()) {
    case "basic" -> new BasicScene();
    case "lighting" -> new LightingDemo();
    case "shapes" -> new ShapesShowcase();
    case "textured" -> new TexturedScene();
    default -> new PBRShowcase();
};
```

Add key binding in `handleInput()` (after line 151):

```java
if (input.isKeyPressed(GLFW_KEY_5)) switchScene("textured");
```

- [ ] **Step 3: Verify build compiles**

```bash
./gradlew compileJava
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/org/fore/examples/TexturedScene.java src/main/java/org/fore/core/Engine.java
git commit -m "feat: add TexturedScene example with 6 material types"
```

---

### Task 5: Update PBRShowcase with Textured Sphere Row

**Files:**
- Modify: `src/main/java/org/fore/examples/PBRShowcase.java`

**Interfaces:**
- Consumes: `TextureUtil.loadPBRMaterial()` from Task 2. `Mesh` from `scene.createSharedMesh()`. Texture sets from Task 3.
- Produces: Updated `PBRShowcase` with an additional row of textured spheres above the procedural grid.

- [ ] **Step 1: Add textured sphere row to PBRShowcase**

In `src/main/java/org/fore/examples/PBRShowcase.java`, add an import for `TextureUtil`:

```java
import org.fore.texture.TextureUtil;
import java.io.File;
```

After the centerpiece entity (after line 56), add:

```java
// Textured sphere row above the grid
String[] texSets = {"rusted-iron", "brushed-aluminum", "wood-planks", "stone", "concrete", "fabric"};
float texStartX = -(texSets.length - 1) * 2.0f / 2.0f;

for (int i = 0; i < texSets.length; i++) {
    String dir = "assets/textures/" + texSets[i];
    Material texMat;
    if (new File(dir).exists()) {
        texMat = TextureUtil.loadPBRMaterial(dir);
    } else {
        texMat = new Material(new Vector3f(0.5f, 0.5f, 0.5f), 0.0f, 0.5f);
    }
    Entity texSphere = scene.createEntity("tex_" + texSets[i], sphereMesh, texMat);
    texSphere.getNode().setPosition(texStartX + i * 2.0f, 4.5f, 0);
}
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew compileJava
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/org/fore/examples/PBRShowcase.java
git commit -m "feat: add textured sphere row to PBR showcase"
```

---

### Task 6: Working with Textures Wiki Page

**Files:**
- Create: `wiki/Working-with-Textures.md`
- Modify: `wiki/_Sidebar.md`
- Modify: `wiki/Home.md`

**Interfaces:**
- Consumes: `Texture2D.fromFile()`, `Texture2D.fromFileLinear()`, `TextureUtil.loadPBRMaterial()`, `Material` texture setters, asset directory convention.
- Produces: Wiki page documenting texture workflow. Sidebar and Home updated.

- [ ] **Step 1: Create `Working-with-Textures.md`**

Create `wiki/Working-with-Textures.md`:

```markdown
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
```

- [ ] **Step 2: Update `_Sidebar.md`**

In `wiki/_Sidebar.md`, add `[[Working-with-Textures]]` under the Guides section, after `[[Geometry-Generator]]`:

```markdown
**Guides**
- [[Understanding-PBR]]
- [[Materials-Guide]]
- [[Lighting-Guide]]
- [[Camera-System]]
- [[Scene-Graph]]
- [[Geometry-Generator]]
- [[Working-with-Textures]]
```

- [ ] **Step 3: Update `Home.md`**

In `wiki/Home.md`, add a row to the Quick Links table after Geometry-Generator:

```markdown
| [[Working-with-Textures]] | Load and apply PBR texture maps |
```

- [ ] **Step 4: Commit**

```bash
git add wiki/Working-with-Textures.md wiki/_Sidebar.md wiki/Home.md
git commit -m "docs(wiki): add Working with Textures guide"
```

---

### Task 7: Push Updated Wiki to GitHub

**Files:**
- No source changes — pushes wiki content to the GitHub wiki repo.

**Interfaces:**
- Consumes: All wiki files from `wiki/` directory
- Produces: Updated GitHub wiki at `https://github.com/rhajamor/fore/wiki`

- [ ] **Step 1: Clone wiki repo and update**

```bash
git clone https://github.com/rhajamor/fore.wiki.git /tmp/fore-wiki
cp wiki/*.md /tmp/fore-wiki/
cd /tmp/fore-wiki
git add .
git commit -m "docs: add Working with Textures page and update sidebar"
git push
rm -rf /tmp/fore-wiki
```

- [ ] **Step 2: Verify wiki is accessible**

Confirm the new page appears at `https://github.com/rhajamor/fore/wiki/Working-with-Textures`.
