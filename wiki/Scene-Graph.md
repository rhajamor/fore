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
