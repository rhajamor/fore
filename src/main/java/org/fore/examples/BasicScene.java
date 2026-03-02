package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class BasicScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.0f)
                .setCastsShadow(true);
        scene.addPointLight(3.0f, 3.0f, 3.0f, 0.3f, 0.5f, 1.0f, 15.0f, 20.0f);

        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(30, 30, 1, 1),
                new Material(new Vector3f(0.4f, 0.4f, 0.4f), 0.0f, 0.8f));
        floor.setCastsShadow(false);

        Entity sphere = scene.createEntity("sphere", GeometryGenerator.sphere(1.0f, 48, 48),
                new Material(new Vector3f(0.9f, 0.1f, 0.1f), 0.0f, 0.3f));
        sphere.getNode().setPosition(0, 1.0f, 0);

        Entity cube = scene.createEntity("cube", GeometryGenerator.box(1.5f, 1.5f, 1.5f),
                new Material(new Vector3f(0.1f, 0.7f, 0.2f), 0.0f, 0.5f));
        cube.getNode().setPosition(-3.5f, 0.75f, 0);

        Entity torus = scene.createEntity("torus", GeometryGenerator.torus(1.0f, 0.35f, 48, 24),
                new Material(new Vector3f(0.1f, 0.3f, 0.9f), 0.5f, 0.2f));
        torus.getNode().setPosition(3.5f, 1.0f, 0);

        Entity cylinder = scene.createEntity("cylinder", GeometryGenerator.cylinder(0.6f, 2.0f, 32),
                new Material(new Vector3f(0.9f, 0.7f, 0.1f), 0.8f, 0.2f));
        cylinder.getNode().setPosition(0, 1.0f, -3.5f);
    }
}
