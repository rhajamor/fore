# FORE Engine Productization Design

**Date:** 2026-07-30
**Approach:** Engine-First (phased rollout)

## Context

FORE is a ~3,000-line PBR 3D rendering engine in Java 21 with Quarkus, LWJGL 3, and JOML. It has a solid technical core (Cook-Torrance PBR, shadow mapping, HDR pipeline, scene graph, procedural geometry, orbit/fly camera) but no surrounding product infrastructure — no CI, no docs beyond a README, no assets, no community scaffolding.

## Target Audience

- **Graphics learners** — students and hobbyists learning PBR, OpenGL, and 3D rendering
- **Open-source community** — contributors who extend and improve the engine
- **Java game/graphics developers** — using FORE as a library in their own projects

## Platform

GitHub-native: GitHub Wiki for guides, GitHub Pages for Javadoc and landing page, GitHub Releases for versioned artifacts.

## License

Eclipse Public License v1.0 (existing, unchanged).

---

## Phase 1: Project Foundation

**Goal:** Make FORE credible as an open-source project. Anyone landing on the repo sees it's maintained, buildable, and welcoming.

**No engine code changes in this phase.**

### 1.1 GitHub Actions CI

- **ci.yml** — Triggers on push to `master` and on pull requests.
- Matrix build: Ubuntu, macOS, Windows × Java 21.
- Runs `./gradlew build`.
- No GPU/integration tests (headless CI can't run OpenGL). Compilation and future unit tests only.

### 1.2 Versioning & Releases

- Adopt semantic versioning starting at `0.1.0`.
- Set `version = "0.1.0"` in `build.gradle.kts` (currently `unspecified`).
- **release.yml** — Triggers on tag push matching `v*`. Runs `./gradlew quarkusBuild`, creates a GitHub Release with the uber-jar attached.

### 1.3 README Overhaul

Keep existing technical content. Add:
- Hero screenshot at the top (placeholder until Phase 4 provides proper captures).
- Badges: build status, license (EPL-1.0), Java 21.
- "Why FORE?" section positioning it against alternatives (LWJGL raw, jMonkeyEngine, LibGDX).
- Links to wiki (once Phase 2 creates it).

### 1.4 Community Files

- **CONTRIBUTING.md** — How to build, code style conventions, PR process, where to ask questions.
- **CODE_OF_CONDUCT.md** — Contributor Covenant v2.1.
- **.github/ISSUE_TEMPLATE/bug_report.md** — Steps to reproduce, expected vs actual, OS/GPU info.
- **.github/ISSUE_TEMPLATE/feature_request.md** — Description, motivation, alternatives considered.
- **.github/PULL_REQUEST_TEMPLATE.md** — What changed, why, how to test.

### 1.5 Javadoc Setup

- Add `javadoc` task to Gradle build.
- Write `package-info.java` for each `org.fore.*` package with a one-paragraph description.
- **pages.yml** — Deploys generated Javadoc to GitHub Pages on push to `master`.
- Every public class gets at minimum a one-line class-level doc.
- Key classes (Engine, Scene, Material, RenderSystem, Light, Camera, GeometryGenerator) get fuller descriptions.

---

## Phase 2: Documentation & Wiki

**Goal:** A learner can go from zero to rendering their first custom scene. A library user can find any API.

### 2.1 GitHub Wiki — Tutorial Series

| # | Page | Content |
|---|------|---------|
| 1 | Home | Overview, quick links, what FORE is and isn't |
| 2 | Getting Started | Prerequisites (Java 21, GPU), clone, build, run, see PBR showcase |
| 3 | Your First Scene | Implement `ExampleScene`, add geometry, set a material, add a light |
| 4 | Understanding PBR | What metallic/roughness mean, how Cook-Torrance works in FORE's shader |
| 5 | Materials Guide | Creating materials, setting properties, how they map to shader uniforms |
| 6 | Lighting Guide | Directional, point, spot lights. Shadow mapping. Exposure control |
| 7 | Camera System | Orbit vs fly mode, programmatic camera control |
| 8 | Scene Graph | Nodes, entities, transforms, parent-child hierarchies |
| 9 | Geometry Generator | All built-in shapes, parameters, how to add custom geometry |
| 10 | Architecture Overview | Render pipeline diagram, package responsibilities, frame loop |
| 11 | Configuration Reference | All `fore.*` properties with defaults and descriptions |

### 2.2 API Reference

- Javadoc published to `<user>.github.io/fore/javadoc/`.
- Cross-linked from wiki pages where relevant (e.g., Materials Guide links to `Material` Javadoc).

---

## Phase 3: Runtime Assets & Textured Demos

**Goal:** Demo scenes look professional with real PBR textures. The engine can load textures from disk.

### 3.1 Engine Changes

**Texture loading from files:**
- `Texture2D.load(String path)` using STB image (already in LWJGL deps).
- Support PNG, JPG, HDR formats.
- Load albedo, normal, metallic, roughness, and AO maps.

**Material enhancement:**
- `Material` accepts optional `Texture2D` references for each PBR channel.
- PBR fragment shader updated: sample texture when bound, fall back to uniform scalar when not.
- Backwards compatible — existing code using scalar-only materials continues to work.

### 3.2 Asset Directory Convention

```
assets/textures/<set-name>/
├── albedo.png
├── normal.png
├── metallic.png
├── roughness.png
└── ao.png
```

### 3.3 Bundled Texture Sets

All CC0 licensed, sourced from ambientCG or Poly Haven:
- **Metals:** rusted iron, brushed aluminum, copper (2-3 sets)
- **Non-metals:** wood, stone/marble, concrete (2-3 sets)
- **Organic:** fabric or leather (1 set)

Total bundle size target: under 50 MB.

### 3.4 Enhanced Demo Scenes

- Update `PBRShowcase` to include a row of textured spheres alongside the procedural grid.
- New `TexturedScene` example demonstrating a textured environment (floor, walls, objects).
- Wiki updated with a "Working with Textures" tutorial page.

---

## Phase 4: Model Loading & Showcase

**Goal:** FORE loads standard 3D models, ships with sample models, and has impressive showcase media for the README and landing page.

### 4.1 Engine Changes

**glTF 2.0 loader:**
- Load `.gltf` (JSON + separate binary) and `.glb` (single binary) files.
- Support: meshes (positions, normals, texcoords, tangents), PBR materials, scene hierarchy.
- Embedded textures supported.
- Implementation: use a minimal JSON parser (e.g., `com.google.code.gson` or the built-in `jakarta.json`) to read the glTF JSON, then map binary buffer accessors manually. No heavy glTF-specific library dependency — keeps the engine lightweight.

**OBJ loader:**
- Load `.obj` + `.mtl` files as a simpler fallback.
- Many free assets are distributed as OBJ.

**New package:** `org.fore.loader` with `GltfLoader` and `ObjLoader`.

### 4.2 Asset Directory Extension

```
assets/
├── textures/       # (from Phase 3)
├── models/
│   └── <name>/
│       ├── model.gltf (or .glb / .obj)
│       └── textures/
└── screenshots/    # Showcase media
```

### 4.3 Bundled Models

CC0 licensed, 2-3 models:
- Classic PBR test models (e.g., damaged helmet, lantern, or similar)
- Drive a new `ModelShowcase` demo scene

### 4.4 Showcase Media

- High-resolution screenshots of each demo scene → `assets/screenshots/`.
- 2-3 GIF recordings of camera orbits around best-looking scenes.
- README updated with hero image gallery.

### 4.5 GitHub Pages Landing Page

Single-page site in `docs/site/`:
- Hero image/GIF
- Feature highlights with screenshots
- Quick start instructions
- Links to wiki, Javadoc, releases
- Deployed via `pages.yml` workflow alongside Javadoc.

---

## Repository Structure (Final State)

```
fore/
├── .github/
│   ├── workflows/
│   │   ├── ci.yml                    # Build + test on push/PR
│   │   ├── release.yml               # Build + publish on tag
│   │   └── pages.yml                 # Deploy Javadoc + landing page
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   └── feature_request.md
│   └── PULL_REQUEST_TEMPLATE.md
├── assets/
│   ├── textures/                     # PBR texture sets (Phase 3)
│   │   ├── rusted-iron/
│   │   ├── brushed-aluminum/
│   │   └── ...
│   ├── models/                       # 3D models (Phase 4)
│   │   ├── damaged-helmet/
│   │   └── ...
│   └── screenshots/                  # Showcase media (Phase 4)
├── docs/
│   └── site/                         # GitHub Pages landing page (Phase 4)
│       └── index.html
├── src/main/java/org/fore/
│   ├── app/                          # Quarkus entry point
│   ├── core/                         # Engine lifecycle, timing
│   ├── window/                       # GLFW window, input
│   ├── render/                       # OpenGL pipeline, framebuffers
│   ├── shader/                       # GLSL compilation, uniforms
│   ├── scene/                        # Scene graph
│   ├── camera/                       # Camera + controllers
│   ├── material/                     # PBR materials
│   ├── texture/                      # Texture loading
│   ├── mesh/                         # GPU mesh management
│   ├── geometry/                     # Procedural generators
│   ├── light/                        # Light types
│   ├── loader/                       # glTF + OBJ loaders (Phase 4)
│   ├── math/                         # Math utilities
│   └── examples/                     # Demo scenes
├── src/main/resources/
│   ├── shaders/                      # GLSL shaders
│   └── application.properties
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── LICENSE
└── README.md
```

## Phase Dependencies

```
Phase 1 (Foundation) ← no prerequisites
Phase 2 (Docs/Wiki) ← Phase 1 (needs CI, version, community files to reference)
Phase 3 (Textures)  ← Phase 1 (needs CI for testing engine changes)
Phase 4 (Models)    ← Phase 3 (builds on texture loading)
```

Phases 2 and 3 can run in parallel after Phase 1 completes.

## Out of Scope

- Audio/sound system
- Physics engine
- GUI/UI framework
- Networking/multiplayer
- Editor/tool application
- Vulkan or other non-OpenGL backends
- Animation system (skeletal, keyframe)
- Particle system

These are valid future directions but not part of this productization effort.
