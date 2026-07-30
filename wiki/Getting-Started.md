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
