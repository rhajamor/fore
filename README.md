# FORE — Free OpenGL Rendering Engine

[![CI](https://github.com/rhajamor/fore/actions/workflows/ci.yml/badge.svg)](https://github.com/rhajamor/fore/actions/workflows/ci.yml)
[![License: EPL-1.0](https://img.shields.io/badge/License-EPL--1.0-blue.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)

A modern, Ogre3D-inspired 3D rendering engine built with Java 21, LWJGL 3, and JOML.

<!-- TODO: Replace with actual screenshot after Phase 4 -->

## Why FORE?

| | FORE | Raw LWJGL | jMonkeyEngine | LibGDX |
|---|---|---|---|---|
| **PBR out of the box** | Yes (Cook-Torrance) | No (write your own) | Plugin-based | No |
| **Learning curve** | Low — clean API | Very high | Medium | Medium |
| **Code you can read** | ~3k lines, 14 packages | N/A | Large codebase | Large codebase |
| **Modern Java** | Java 21, Quarkus CDI | Any | Java 11+ | Java 11+ |
| **Shadow mapping** | Built-in PCF | DIY | Built-in | DIY |

FORE is designed to be **small enough to understand completely** while being
**capable enough to render real PBR scenes**. If you want to learn how a
3D engine works by reading its source, or you need a lightweight Java
rendering core to build on, FORE is for you.

## Features

- **PBR Rendering** — Cook-Torrance BRDF with GGX distribution, Fresnel-Schlick, and Smith geometry
- **Shadow Mapping** — PCF soft shadows from directional lights
- **HDR Pipeline** — RGBA16F render target with ACES tone mapping and gamma correction
- **Scene Graph** — Hierarchical node system inspired by Ogre3D
- **Multiple Light Types** — Directional, point, and spot lights (up to 16 simultaneous)
- **Built-in Geometry** — Box, sphere, plane, torus, cylinder, cone generators with tangent computation
- **Orbit & Fly Camera** — Smooth orbit camera with pan/zoom, switchable to FPS fly mode
- **Quarkus Integration** — CDI-managed engine lifecycle with externalized configuration
- **Modern OpenGL 4.1** — Core profile, VAO/VBO/EBO, GLSL 410

## Requirements

- **Java 21+** (LTS)
- **Gradle 8.x** (or use the included wrapper)
- **OpenGL 4.1+** capable GPU

### macOS Note

On macOS, GLFW requires the `-XstartOnFirstThread` JVM argument. This is configured
automatically in the Gradle run tasks.

## Documentation

- [Wiki — Tutorials & Guides](https://github.com/rhajamor/fore/wiki)
- [API Reference (Javadoc)](https://rhajamor.github.io/fore/)
- [Contributing](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)

## Quick Start

```bash
# Install Java 21 (if not already installed)
# macOS:
brew install openjdk@21
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Linux:
# sdk install java 21-open

# Build the engine
./gradlew quarkusBuild

# Run (Gradle task handles -XstartOnFirstThread on macOS)
./gradlew runEngine

# Or run directly:
# macOS:
java -XstartOnFirstThread -jar build/fore-engine-0.1.0-runner.jar
# Linux/Windows:
java -jar build/fore-engine-0.1.0-runner.jar
```

## Controls

| Input | Action |
|---|---|
| Left Mouse + Drag | Orbit camera |
| Middle Mouse + Drag | Pan camera |
| Scroll Wheel | Zoom in/out |
| Tab | Toggle orbit/fly camera mode |
| W/A/S/D | Move (fly mode) |
| Space/Ctrl | Up/Down (fly mode) |
| Right Mouse + Drag | Look around (fly mode) |
| Shift | Sprint (fly mode) |
| 1 | Basic scene |
| 2 | PBR material showcase |
| 3 | Lighting demo |
| 4 | Shapes showcase |
| G | Toggle ground grid |
| +/- | Adjust exposure |
| Escape | Quit |

## Example Scenes

### Basic Scene (Key: 1)
Simple scene with sphere, cube, torus, and cylinder under directional + point lighting.

### PBR Showcase (Key: 2)
A 7x7 grid of spheres demonstrating the full range of metallic and roughness values
across the color spectrum. Default scene on startup.

### Lighting Demo (Key: 3)
Interior scene with 4 colored point lights, a spotlight, and a directional light.
Pillars, walls, and a central statue demonstrate complex light interactions.

### Shapes Showcase (Key: 4)
All built-in geometry types displayed with various metallic materials: copper, gold,
silver, chrome, rubber, and plastic.

## Architecture

```
org.fore
├── app/             Quarkus application entry point
├── core/            Engine lifecycle, frame timing
├── window/          GLFW window management, input system
├── render/          OpenGL render pipeline, framebuffers
├── shader/          GLSL shader compilation and uniform management
├── scene/           Scene graph (nodes, entities, transforms)
├── camera/          Camera and orbit/fly controllers
├── material/        PBR material system
├── texture/         Texture loading and generation
├── mesh/            GPU mesh management (VAO/VBO/EBO)
├── geometry/        Procedural geometry generators
├── light/           Light types (directional, point, spot)
├── math/            Math utilities (supplements JOML)
└── examples/        Built-in example scenes
```

### Render Pipeline

1. **Shadow Pass** — Render depth from directional light's perspective (2048x2048)
2. **Geometry Pass** — Render scene to HDR framebuffer (RGBA16F) with PBR shading
3. **Post-Process Pass** — ACES tone mapping + gamma correction to screen

### Technology Stack

| Component | Technology |
|---|---|
| Language | Java 21 (LTS) |
| Framework | Quarkus 3.17 (CDI, Configuration) |
| Graphics | LWJGL 3.3.4 (OpenGL 4.1, GLFW) |
| Math | JOML 1.10.8 |
| Build | Gradle (Kotlin DSL) |
| Shading | GLSL 410 Core |

## Configuration

Edit `src/main/resources/application.properties`:

```properties
fore.window.width=1600
fore.window.height=900
fore.window.title=FORE Engine
fore.render.exposure=1.0
fore.scene=pbr
```

## License

Eclipse Public License v1.0 — see [LICENSE](LICENSE).
