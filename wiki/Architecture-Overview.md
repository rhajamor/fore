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
