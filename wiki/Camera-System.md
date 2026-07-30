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
