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
