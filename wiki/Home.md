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
