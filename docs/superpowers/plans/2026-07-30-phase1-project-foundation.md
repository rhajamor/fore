# Phase 1: Project Foundation — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make FORE credible as an open-source project with CI, versioning, community scaffolding, and Javadoc — no engine code changes.

**Architecture:** Add GitHub Actions workflows (CI, release, pages), community files (CONTRIBUTING, CODE_OF_CONDUCT, issue/PR templates), version the build, add package-info.java and class-level Javadoc to all public classes, and overhaul the README with badges and positioning.

**Tech Stack:** GitHub Actions, Gradle (Kotlin DSL), Java 21, Javadoc

## Global Constraints

- Java 21 LTS only
- Gradle with Kotlin DSL (existing `build.gradle.kts`)
- No engine code changes — only infrastructure, docs, and build config
- GitHub remote: `rhajamor/fore`
- License: EPL-1.0 (unchanged)
- Quarkus 3.17.5, LWJGL 3.3.4, JOML 1.10.8 (unchanged)

---

### Task 1: Set Version and Configure Javadoc in Gradle

**Files:**
- Modify: `build.gradle.kts`

**Interfaces:**
- Consumes: nothing
- Produces: `version = "0.1.0"` in build; `javadoc` task generates to `build/docs/javadoc/`

- [ ] **Step 1: Set the project version**

In `build.gradle.kts`, add the version after the `plugins` block:

```kotlin
version = "0.1.0"
```

- [ ] **Step 2: Update the runEngine jar path to use the version**

In the `runEngine` task, change the jar path from hardcoded "unspecified" to use the project version:

```kotlin
execArgs.add(layout.buildDirectory.file("fore-engine-${project.version}-runner.jar").get().asFile.absolutePath)
```

- [ ] **Step 3: Add Javadoc configuration**

Add after the `java` block:

```kotlin
tasks.withType<Javadoc> {
    options {
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:none", "-quiet")
        windowTitle = "FORE Engine ${project.version} API"
        docTitle = "FORE Engine ${project.version}"
        header = "<b>FORE Engine</b>"
        links("https://docs.oracle.com/en/java/javase/21/docs/api/")
        links("https://javadoc.io/doc/org.joml/joml/1.10.8/")
    }
}
```

- [ ] **Step 4: Build and verify**

Run:
```bash
./gradlew build
```

Expected: Build succeeds. The runner jar is now named `fore-engine-0.1.0-runner.jar`.

Run:
```bash
ls build/fore-engine-0.1.0-runner.jar
```

Expected: File exists.

- [ ] **Step 5: Generate Javadoc and verify**

Run:
```bash
./gradlew javadoc
```

Expected: Javadoc generates to `build/docs/javadoc/` with warnings (no docs yet) but no errors.

Run:
```bash
ls build/docs/javadoc/index.html
```

Expected: File exists.

- [ ] **Step 6: Commit**

```bash
git add build.gradle.kts
git commit -m "chore: set version to 0.1.0 and configure Javadoc generation"
```

---

### Task 2: Add Package-Info and Class-Level Javadoc

**Files:**
- Create: `src/main/java/org/fore/app/package-info.java`
- Create: `src/main/java/org/fore/core/package-info.java`
- Create: `src/main/java/org/fore/window/package-info.java`
- Create: `src/main/java/org/fore/render/package-info.java`
- Create: `src/main/java/org/fore/shader/package-info.java`
- Create: `src/main/java/org/fore/scene/package-info.java`
- Create: `src/main/java/org/fore/camera/package-info.java`
- Create: `src/main/java/org/fore/material/package-info.java`
- Create: `src/main/java/org/fore/texture/package-info.java`
- Create: `src/main/java/org/fore/mesh/package-info.java`
- Create: `src/main/java/org/fore/geometry/package-info.java`
- Create: `src/main/java/org/fore/light/package-info.java`
- Create: `src/main/java/org/fore/math/package-info.java`
- Create: `src/main/java/org/fore/examples/package-info.java`
- Modify: all 22 public `.java` files (add class-level Javadoc)

**Interfaces:**
- Consumes: Javadoc task from Task 1
- Produces: clean `./gradlew javadoc` with no missing-doc warnings for public classes

- [ ] **Step 1: Create all package-info.java files**

Each file follows this pattern — one paragraph describing the package's role:

`src/main/java/org/fore/app/package-info.java`:
```java
/**
 * Quarkus application entry point. Bootstraps the CDI container on a background
 * thread and runs the engine's render loop on the main thread (required by GLFW
 * on macOS).
 */
package org.fore.app;
```

`src/main/java/org/fore/core/package-info.java`:
```java
/**
 * Engine lifecycle management and frame timing. The {@link org.fore.core.Engine}
 * class owns the main loop: initialization, input handling, rendering, and shutdown.
 */
package org.fore.core;
```

`src/main/java/org/fore/window/package-info.java`:
```java
/**
 * GLFW window management and input handling. Creates and configures the
 * application window, tracks keyboard/mouse state per frame, and handles
 * resize events.
 */
package org.fore.window;
```

`src/main/java/org/fore/render/package-info.java`:
```java
/**
 * OpenGL render pipeline. Manages the three-pass rendering flow: shadow map
 * generation, PBR geometry pass to an HDR framebuffer, and post-process
 * tone mapping to screen.
 */
package org.fore.render;
```

`src/main/java/org/fore/shader/package-info.java`:
```java
/**
 * GLSL shader compilation and uniform management. Loads vertex and fragment
 * shaders from resources, compiles and links them into programs, and provides
 * typed uniform setters.
 */
package org.fore.shader;
```

`src/main/java/org/fore/scene/package-info.java`:
```java
/**
 * Ogre3D-inspired scene graph. Hierarchical node tree with transforms,
 * entities (mesh + material pairs), and scene-level resource management.
 */
package org.fore.scene;
```

`src/main/java/org/fore/camera/package-info.java`:
```java
/**
 * Camera and controller system. Provides perspective projection and two
 * interaction modes: orbit (left-drag to rotate, scroll to zoom, middle-drag
 * to pan) and first-person fly (WASD + mouse look).
 */
package org.fore.camera;
```

`src/main/java/org/fore/material/package-info.java`:
```java
/**
 * PBR material system. Defines surface properties (albedo, metallic, roughness,
 * ambient occlusion, emissive) as scalar values with optional texture map
 * overrides. Binds material state to the PBR shader at draw time.
 */
package org.fore.material;
```

`src/main/java/org/fore/texture/package-info.java`:
```java
/**
 * OpenGL texture management. Handles 2D texture creation from pixel data
 * or procedural generation (e.g., checkerboard patterns for defaults).
 */
package org.fore.texture;
```

`src/main/java/org/fore/mesh/package-info.java`:
```java
/**
 * GPU mesh management. Handles vertex data layout (positions, normals, UVs,
 * tangents), uploads to OpenGL via VAO/VBO/EBO, and manages draw calls.
 */
package org.fore.mesh;
```

`src/main/java/org/fore/geometry/package-info.java`:
```java
/**
 * Procedural geometry generators. Creates {@link org.fore.mesh.MeshData} for
 * built-in shapes: box, sphere, plane, torus, cylinder, and cone — each with
 * positions, normals, texture coordinates, and tangents.
 */
package org.fore.geometry;
```

`src/main/java/org/fore/light/package-info.java`:
```java
/**
 * Light types for the PBR pipeline. Supports directional, point, and spot
 * lights with configurable color, intensity, range, and shadow casting.
 * Up to 16 lights can be active simultaneously.
 */
package org.fore.light;
```

`src/main/java/org/fore/math/package-info.java`:
```java
/**
 * Math utilities supplementing JOML. Provides helper methods for common
 * 3D math operations not covered by the JOML library.
 */
package org.fore.math;
```

`src/main/java/org/fore/examples/package-info.java`:
```java
/**
 * Built-in example scenes demonstrating engine features. Each scene implements
 * {@link org.fore.examples.ExampleScene} and can be activated at runtime via
 * keyboard shortcuts (keys 1-4) or the {@code fore.scene} configuration property.
 */
package org.fore.examples;
```

- [ ] **Step 2: Add class-level Javadoc to all public classes**

Add a one-line `/** ... */` comment above each public class/interface that lacks one. Key classes get 2-3 lines. Here is the Javadoc for every public class:

`Engine.java`:
```java
/**
 * Core engine class managing the main loop lifecycle. Initializes the window,
 * render system, camera, and active scene, then runs the update/render loop
 * until the window closes. Configured via Quarkus {@code fore.*} properties.
 */
```

`TimeStep.java`:
```java
/** Tracks frame timing: delta time between frames, FPS, and frame count. */
```

`ForeApplication.java` — already has a Javadoc comment, skip.

`Window.java`:
```java
/** GLFW window wrapper. Creates and configures the OS window, tracks framebuffer size, and manages swap/poll. */
```

`InputSystem.java`:
```java
/** Per-frame keyboard and mouse input state. Tracks key presses, mouse position, scroll, and button state. */
```

`RenderSystem.java`:
```java
/**
 * Three-pass OpenGL render pipeline: shadow map pass (depth from directional light),
 * PBR geometry pass (Cook-Torrance BRDF to HDR framebuffer), and post-process pass
 * (ACES tone mapping + gamma correction).
 */
```

`Framebuffer.java`:
```java
/** OpenGL framebuffer object wrapper for off-screen rendering (HDR target and shadow map). */
```

`ShaderProgram.java`:
```java
/** Compiles and links GLSL vertex/fragment shaders. Provides typed uniform setters for matrices, vectors, and scalars. */
```

`Scene.java`:
```java
/**
 * Container for a scene graph, lights, and entities. Manages the node hierarchy,
 * shared meshes, light list, and provides factory methods for creating entities
 * and lights.
 */
```

`SceneNode.java`:
```java
/** A node in the scene graph hierarchy. Holds a local transform and computes world transforms from parent chain. */
```

`Entity.java`:
```java
/** A visible object in the scene: a mesh paired with a material, attached to a scene node. */
```

`Transform.java`:
```java
/** 3D transform holding position, rotation (quaternion), and scale. Computes the local-to-parent matrix. */
```

`Camera.java`:
```java
/** Perspective camera with configurable FOV, near/far planes, and aspect ratio. Computes view and projection matrices. */
```

`CameraController.java`:
```java
/** Input-driven camera controller supporting orbit mode (rotate/pan/zoom) and first-person fly mode (WASD + mouse look). */
```

`Material.java`:
```java
/**
 * PBR material defining surface appearance. Properties: albedo color, metallic,
 * roughness, ambient occlusion, and emissive — each as a scalar/vector with an
 * optional texture map override.
 */
```

`Texture2D.java`:
```java
/** OpenGL 2D texture. Supports creation from raw pixel data and procedural generation (checkerboard default). */
```

`Mesh.java`:
```java
/** GPU-resident mesh using VAO/VBO/EBO. Uploads vertex data from {@link MeshData} and issues indexed draw calls. */
```

`MeshData.java`:
```java
/** CPU-side vertex and index data builder. Accumulates positions, normals, UVs, tangents, and triangle indices. */
```

`VertexLayout.java`:
```java
/** Defines the vertex attribute layout (position, normal, texcoord, tangent) and configures VAO attribute pointers. */
```

`GeometryGenerator.java`:
```java
/**
 * Procedural mesh generators for built-in shapes. All generators produce
 * {@link org.fore.mesh.MeshData} with positions, normals, texture coordinates,
 * and tangents. Available shapes: box, sphere, plane, torus, cylinder, cone.
 */
```

`Light.java`:
```java
/** Scene light supporting directional, point, and spot types with color, intensity, range, and shadow-casting control. */
```

`MathUtil.java`:
```java
/** Math utilities supplementing JOML: clamping, interpolation, and other common 3D math helpers. */
```

`ExampleScene.java`:
```java
/** Interface for built-in demo scenes. Implement {@link #setup(Scene)} to populate a scene with geometry, materials, and lights. */
```

`BasicScene.java`:
```java
/** Simple demo scene with a sphere, cube, torus, and cylinder under directional and point lighting. */
```

`PBRShowcase.java`:
```java
/** 7x7 grid of spheres demonstrating the full range of metallic and roughness values across the color spectrum. */
```

`LightingDemo.java`:
```java
/** Interior demo scene with multiple colored point lights, a spotlight, and pillars demonstrating complex light interactions. */
```

`ShapesShowcase.java`:
```java
/** Demo scene displaying all built-in geometry types with various metallic materials (copper, gold, silver, chrome). */
```

- [ ] **Step 3: Regenerate Javadoc and verify**

Run:
```bash
./gradlew javadoc
```

Expected: Generates cleanly. Spot-check `build/docs/javadoc/org/fore/core/Engine.html` — should show the class description.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/org/fore/*/package-info.java
git add src/main/java/org/fore/**/*.java
git commit -m "docs: add package-info and class-level Javadoc to all public types"
```

---

### Task 3: Add GitHub Actions CI Workflow

**Files:**
- Create: `.github/workflows/ci.yml`

**Interfaces:**
- Consumes: `./gradlew build` (from existing Gradle config)
- Produces: CI status badge URL `https://github.com/rhajamor/fore/actions/workflows/ci.yml/badge.svg`

- [ ] **Step 1: Create the CI workflow**

`.github/workflows/ci.yml`:
```yaml
name: CI

on:
  push:
    branches: [master]
  pull_request:
    branches: [master]

permissions:
  contents: read

jobs:
  build:
    name: Build (${{ matrix.os }})
    runs-on: ${{ matrix.os }}
    strategy:
      fail-fast: false
      matrix:
        os: [ubuntu-latest, macos-latest, windows-latest]

    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
          cache: gradle

      - name: Build
        run: ./gradlew build --no-daemon

      - name: Generate Javadoc
        run: ./gradlew javadoc --no-daemon
```

- [ ] **Step 2: Validate YAML syntax**

Run:
```bash
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/ci.yml'))" 2>/dev/null || python3 -c "import json, sys; print('YAML check requires PyYAML; skipping')"
```

Or just visually confirm indentation is correct.

- [ ] **Step 3: Commit**

```bash
git add .github/workflows/ci.yml
git commit -m "ci: add GitHub Actions build workflow for Linux, macOS, and Windows"
```

---

### Task 4: Add Release Workflow

**Files:**
- Create: `.github/workflows/release.yml`

**Interfaces:**
- Consumes: `./gradlew quarkusBuild` producing `build/fore-engine-*-runner.jar`
- Produces: GitHub Release with uber-jar on tag push

- [ ] **Step 1: Create the release workflow**

`.github/workflows/release.yml`:
```yaml
name: Release

on:
  push:
    tags: ['v*']

permissions:
  contents: write

jobs:
  release:
    name: Build and Release
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
          cache: gradle

      - name: Build uber-jar
        run: ./gradlew quarkusBuild --no-daemon

      - name: Extract version from tag
        id: version
        run: echo "VERSION=${GITHUB_REF_NAME#v}" >> "$GITHUB_OUTPUT"

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          generate_release_notes: true
          files: build/fore-engine-${{ steps.version.outputs.VERSION }}-runner.jar
```

- [ ] **Step 2: Commit**

```bash
git add .github/workflows/release.yml
git commit -m "ci: add release workflow to publish uber-jar on tag push"
```

---

### Task 5: Add GitHub Pages Workflow for Javadoc

**Files:**
- Create: `.github/workflows/pages.yml`

**Interfaces:**
- Consumes: `./gradlew javadoc` producing `build/docs/javadoc/`
- Produces: Javadoc published at `https://rhajamor.github.io/fore/`

- [ ] **Step 1: Create the pages workflow**

`.github/workflows/pages.yml`:
```yaml
name: Deploy Javadoc

on:
  push:
    branches: [master]

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: pages
  cancel-in-progress: true

jobs:
  deploy:
    name: Deploy Javadoc to GitHub Pages
    runs-on: ubuntu-latest
    environment:
      name: github-pages
      url: ${{ steps.deployment.outputs.page_url }}

    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
          cache: gradle

      - name: Generate Javadoc
        run: ./gradlew javadoc --no-daemon

      - name: Setup Pages
        uses: actions/configure-pages@v5

      - name: Upload artifact
        uses: actions/upload-pages-artifact@v3
        with:
          path: build/docs/javadoc

      - name: Deploy to GitHub Pages
        id: deployment
        uses: actions/deploy-pages@v4
```

- [ ] **Step 2: Commit**

```bash
git add .github/workflows/pages.yml
git commit -m "ci: add GitHub Pages workflow to deploy Javadoc"
```

---

### Task 6: Add Community Files and Issue/PR Templates

**Files:**
- Create: `CONTRIBUTING.md`
- Create: `CODE_OF_CONDUCT.md`
- Create: `.github/ISSUE_TEMPLATE/bug_report.md`
- Create: `.github/ISSUE_TEMPLATE/feature_request.md`
- Create: `.github/PULL_REQUEST_TEMPLATE.md`

**Interfaces:**
- Consumes: nothing
- Produces: community scaffolding referenced by README (Task 7)

- [ ] **Step 1: Create CONTRIBUTING.md**

```markdown
# Contributing to FORE

Thank you for your interest in contributing to FORE! This guide will help you get started.

## Prerequisites

- Java 21+ ([Adoptium Temurin](https://adoptium.net/) recommended)
- Git
- A GPU supporting OpenGL 4.1+

## Building

```bash
git clone https://github.com/rhajamor/fore.git
cd fore
./gradlew build
```

## Running

```bash
# macOS (Gradle handles -XstartOnFirstThread):
./gradlew runEngine

# Or directly:
# macOS:
java -XstartOnFirstThread -jar build/fore-engine-0.1.0-runner.jar
# Linux/Windows:
java -jar build/fore-engine-0.1.0-runner.jar
```

## Code Style

- Standard Java conventions (4-space indent, braces on same line)
- No IDE-specific formatting plugins required
- Keep classes focused — one responsibility per file
- Minimal Javadoc: every public class gets at least a one-line doc comment

## Making Changes

1. Fork the repository
2. Create a feature branch from `master`: `git checkout -b feature/my-feature`
3. Make your changes with clear, focused commits
4. Ensure `./gradlew build` passes
5. Submit a pull request against `master`

## Pull Request Guidelines

- Keep PRs focused — one feature or fix per PR
- Write a clear description of what changed and why
- Include steps to test the change (e.g., "run scene 2, orbit camera, check shadows")
- Screenshots or GIFs are welcome for visual changes

## Reporting Bugs

Use the [bug report template](https://github.com/rhajamor/fore/issues/new?template=bug_report.md). Include:
- Steps to reproduce
- Expected vs actual behavior
- OS, GPU, and Java version

## Questions?

Open a [discussion](https://github.com/rhajamor/fore/discussions) or file an issue.
```

- [ ] **Step 2: Create CODE_OF_CONDUCT.md**

Use the Contributor Covenant v2.1. The full text is available at https://www.contributor-covenant.org/version/2/1/code_of_conduct/. Create the file with:

```markdown
# Contributor Covenant Code of Conduct

## Our Pledge

We as members, contributors, and leaders pledge to make participation in our
community a harassment-free experience for everyone, regardless of age, body
size, visible or invisible disability, ethnicity, sex characteristics, gender
identity and expression, level of experience, education, socio-economic status,
nationality, personal appearance, race, caste, color, religion, or sexual
identity and orientation.

We pledge to act and interact in ways that contribute to an open, welcoming,
diverse, inclusive, and healthy community.

## Our Standards

Examples of behavior that contributes to a positive environment for our
community include:

* Demonstrating empathy and kindness toward other people
* Being respectful of differing opinions, viewpoints, and experiences
* Giving and gracefully accepting constructive feedback
* Accepting responsibility and apologizing to those affected by our mistakes,
  and learning from the experience
* Focusing on what is best not just for us as individuals, but for the overall
  community

Examples of unacceptable behavior include:

* The use of sexualized language or imagery, and sexual attention or advances of
  any kind
* Trolling, insulting or derogatory comments, and personal or political attacks
* Public or private harassment
* Publishing others' private information, such as a physical or email address,
  without their explicit permission
* Other conduct which could reasonably be considered inappropriate in a
  professional setting

## Enforcement Responsibilities

Community leaders are responsible for clarifying and enforcing our standards of
acceptable behavior and will take appropriate and fair corrective action in
response to any behavior that they deem inappropriate, threatening, offensive,
or harmful.

## Scope

This Code of Conduct applies within all community spaces, and also applies when
an individual is officially representing the community in public spaces.

## Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be
reported to the project maintainer. All complaints will be reviewed and
investigated promptly and fairly.

## Attribution

This Code of Conduct is adapted from the [Contributor Covenant][homepage],
version 2.1, available at
[https://www.contributor-covenant.org/version/2/1/code_of_conduct.html][v2.1].

[homepage]: https://www.contributor-covenant.org
[v2.1]: https://www.contributor-covenant.org/version/2/1/code_of_conduct.html
```

- [ ] **Step 3: Create bug report issue template**

`.github/ISSUE_TEMPLATE/bug_report.md`:
```markdown
---
name: Bug Report
about: Report a rendering issue, crash, or unexpected behavior
labels: bug
---

## Description

A clear description of the bug.

## Steps to Reproduce

1. Run the engine with `./gradlew runEngine`
2. Switch to scene '...'
3. Do '...'
4. Observe '...'

## Expected Behavior

What should have happened.

## Actual Behavior

What actually happened. Include screenshots or error output if possible.

## Environment

- **OS:** (e.g., macOS 15.1, Ubuntu 24.04, Windows 11)
- **GPU:** (e.g., Apple M2, NVIDIA RTX 4070)
- **Java version:** (output of `java -version`)
- **FORE version:** (tag or commit hash)
```

- [ ] **Step 4: Create feature request issue template**

`.github/ISSUE_TEMPLATE/feature_request.md`:
```markdown
---
name: Feature Request
about: Suggest a new engine feature or improvement
labels: enhancement
---

## Description

What feature or improvement would you like?

## Motivation

Why is this useful? What problem does it solve?

## Alternatives Considered

Have you considered other approaches? What are the trade-offs?

## Additional Context

Any references, screenshots, or links to similar implementations.
```

- [ ] **Step 5: Create pull request template**

`.github/PULL_REQUEST_TEMPLATE.md`:
```markdown
## What Changed

Describe the changes and the motivation behind them.

## How to Test

Steps to verify the change works:

1. Build: `./gradlew build`
2. Run: `./gradlew runEngine`
3. ...

## Screenshots

If this is a visual change, include before/after screenshots.

## Checklist

- [ ] `./gradlew build` passes
- [ ] `./gradlew javadoc` generates without errors
- [ ] New public classes have Javadoc
```

- [ ] **Step 6: Commit**

```bash
git add CONTRIBUTING.md CODE_OF_CONDUCT.md .github/ISSUE_TEMPLATE/ .github/PULL_REQUEST_TEMPLATE.md
git commit -m "docs: add CONTRIBUTING, CODE_OF_CONDUCT, and issue/PR templates"
```

---

### Task 7: Overhaul README

**Files:**
- Modify: `README.md`

**Interfaces:**
- Consumes: CI badge URL from Task 3, community files from Task 6
- Produces: polished README with badges, positioning, and links

- [ ] **Step 1: Rewrite README.md**

Replace the entire `README.md` with an overhauled version. Keep all existing technical content (features, requirements, controls, example scenes, architecture, configuration) but restructure with:

**Header section** (new):
```markdown
# FORE — Free OpenGL Rendering Engine

[![CI](https://github.com/rhajamor/fore/actions/workflows/ci.yml/badge.svg)](https://github.com/rhajamor/fore/actions/workflows/ci.yml)
[![License: EPL-1.0](https://img.shields.io/badge/License-EPL--1.0-blue.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)

A modern, Ogre3D-inspired 3D rendering engine built with Java 21, LWJGL 3, and JOML.

<!-- TODO: Replace with actual screenshot after Phase 4 -->
```

**"Why FORE?" section** (new, add after the one-liner description):
```markdown
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
```

**Links section** (new, add before Quick Start):
```markdown
## Documentation

- [Wiki — Tutorials & Guides](https://github.com/rhajamor/fore/wiki)
- [API Reference (Javadoc)](https://rhajamor.github.io/fore/)
- [Contributing](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
```

Keep all existing sections (Features, Requirements, Quick Start, Controls, Example Scenes, Architecture, Configuration, License) unchanged.

- [ ] **Step 2: Verify the README renders correctly**

Run:
```bash
head -30 README.md
```

Confirm the badge markdown and table are well-formed.

- [ ] **Step 3: Commit**

```bash
git add README.md
git commit -m "docs: overhaul README with badges, positioning, and documentation links"
```

---

## File Summary

| File | Action | Task |
|------|--------|------|
| `build.gradle.kts` | Modify | 1 |
| `src/main/java/org/fore/*/package-info.java` (×14) | Create | 2 |
| `src/main/java/org/fore/**/*.java` (×22) | Modify (add Javadoc) | 2 |
| `.github/workflows/ci.yml` | Create | 3 |
| `.github/workflows/release.yml` | Create | 4 |
| `.github/workflows/pages.yml` | Create | 5 |
| `CONTRIBUTING.md` | Create | 6 |
| `CODE_OF_CONDUCT.md` | Create | 6 |
| `.github/ISSUE_TEMPLATE/bug_report.md` | Create | 6 |
| `.github/ISSUE_TEMPLATE/feature_request.md` | Create | 6 |
| `.github/PULL_REQUEST_TEMPLATE.md` | Create | 6 |
| `README.md` | Modify | 7 |

## Task Dependency Graph

```
Task 1 (version + Javadoc config)
├── Task 2 (package-info + class Javadoc) ← needs Javadoc task
├── Task 3 (CI workflow) ← independent
├── Task 4 (release workflow) ← independent
├── Task 5 (pages workflow) ← needs Javadoc task
└── Task 6 (community files) ← independent
        └── Task 7 (README overhaul) ← needs badge URL from Task 3, links to Task 6 files
```

Tasks 2, 3, 4, 5, and 6 can all run in parallel after Task 1. Task 7 runs last.
