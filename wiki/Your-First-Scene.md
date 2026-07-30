# Your First Scene

This tutorial walks you through creating a custom FORE scene from scratch. By the end, you'll have a lit, shaded scene with geometry you placed yourself.

## How Scenes Work

FORE scenes implement the [`ExampleScene`](https://rhajamor.github.io/fore/org/fore/examples/ExampleScene.html) interface:

```java
public interface ExampleScene {
    void setup(Scene scene);
}
```

The engine calls `setup()` once, passing you a [`Scene`](https://rhajamor.github.io/fore/org/fore/scene/Scene.html) object. You populate it with geometry, materials, and lights. The engine handles rendering, camera, and input from there.

## Step 1: Create the Class

Create `src/main/java/org/fore/examples/MyScene.java`:

```java
package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class MyScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        // We'll fill this in step by step
    }
}
```

## Step 2: Add a Light

Every scene needs at least one light, or everything will be black. Add a directional light — it simulates sunlight with a direction, color, and intensity:

```java
@Override
public void setup(Scene scene) {
    // Direction (x, y, z), Color (r, g, b), Intensity
    scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.0f)
            .setCastsShadow(true);
}
```

The direction `(-0.5, -1.0, -0.3)` means light comes from the upper-left. The color `(1.0, 0.95, 0.9)` is warm white. Intensity `3.0` controls brightness before tone mapping.

## Step 3: Add a Floor

A floor provides context and receives shadows:

```java
Entity floor = scene.createEntity("floor",
        GeometryGenerator.plane(20, 20, 1, 1),
        new Material(new Vector3f(0.4f, 0.4f, 0.4f), 0.0f, 0.8f));
floor.setCastsShadow(false);
```

This creates a 20×20 unit plane with:
- **Albedo:** gray `(0.4, 0.4, 0.4)`
- **Metallic:** `0.0` (non-metal)
- **Roughness:** `0.8` (mostly rough)

We disable shadow casting on the floor since it's a receiver, not a caster.

## Step 4: Add Some Objects

Place a red sphere and a blue cube:

```java
// Red sphere
Entity sphere = scene.createEntity("sphere",
        GeometryGenerator.sphere(1.0f, 48, 48),
        new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
sphere.getNode().setPosition(0, 1.0f, 0);

// Blue cube
Entity cube = scene.createEntity("cube",
        GeometryGenerator.box(1.5f, 1.5f, 1.5f),
        new Material(new Vector3f(0.1f, 0.3f, 0.9f), 0.5f, 0.2f));
cube.getNode().setPosition(-3.0f, 0.75f, 0);
```

`GeometryGenerator.sphere(radius, sectors, stacks)` produces a UV sphere. Higher sector/stack counts make it smoother. `GeometryGenerator.box(width, height, depth)` produces a box centered at the origin.

Each entity gets a node in the scene graph. Use `getNode().setPosition(x, y, z)` to place it in world space.

## Step 5: Add a Point Light

Add a colored point light for more visual interest:

```java
scene.addPointLight(3.0f, 3.0f, 3.0f,    // position
        0.3f, 0.5f, 1.0f,                 // color (blueish)
        15.0f,                             // intensity
        20.0f);                            // range
```

Point lights emit in all directions from a position, with intensity falling off over the specified range.

## Step 6: Register Your Scene

Open `src/main/java/org/fore/core/Engine.java` and add your scene to the `createScene` switch:

```java
private Scene createScene(String name) {
    ExampleScene example = switch (name.toLowerCase()) {
        case "basic" -> new BasicScene();
        case "lighting" -> new LightingDemo();
        case "shapes" -> new ShapesShowcase();
        case "myscene" -> new MyScene();       // Add this line
        default -> new PBRShowcase();
    };

    Scene scene = new Scene(name);
    example.setup(scene);
    return scene;
}
```

## Step 7: Run It

Set your scene as the default in `src/main/resources/application.properties`:

```properties
fore.scene=myscene
```

Or launch the engine and press a number key to switch scenes, then add a key binding in `handleInput()`.

Build and run:
```bash
./gradlew runEngine
```

## Complete Code

```java
package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class MyScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        // Directional light (sunlight)
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.0f)
                .setCastsShadow(true);

        // Point light (blue accent)
        scene.addPointLight(3.0f, 3.0f, 3.0f, 0.3f, 0.5f, 1.0f, 15.0f, 20.0f);

        // Floor
        Entity floor = scene.createEntity("floor",
                GeometryGenerator.plane(20, 20, 1, 1),
                new Material(new Vector3f(0.4f, 0.4f, 0.4f), 0.0f, 0.8f));
        floor.setCastsShadow(false);

        // Red sphere
        Entity sphere = scene.createEntity("sphere",
                GeometryGenerator.sphere(1.0f, 48, 48),
                new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
        sphere.getNode().setPosition(0, 1.0f, 0);

        // Blue cube
        Entity cube = scene.createEntity("cube",
                GeometryGenerator.box(1.5f, 1.5f, 1.5f),
                new Material(new Vector3f(0.1f, 0.3f, 0.9f), 0.5f, 0.2f));
        cube.getNode().setPosition(-3.0f, 0.75f, 0);
    }
}
```

## Next Steps

- [[Materials-Guide]] — learn about all PBR material properties
- [[Lighting-Guide]] — explore the three light types in depth
- [[Geometry-Generator]] — discover all built-in shapes
