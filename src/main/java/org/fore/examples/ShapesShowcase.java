package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class ShapesShowcase implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.0f)
                .setCastsShadow(true);
        scene.addPointLight(6, 5, 6, 0.6f, 0.8f, 1.0f, 20, 30);
        scene.addPointLight(-6, 5, -6, 1.0f, 0.6f, 0.4f, 15, 25);

        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(40, 40, 1, 1),
                new Material(new Vector3f(0.2f, 0.2f, 0.2f), 0.0f, 0.85f));
        floor.setCastsShadow(false);

        Material copper = new Material(new Vector3f(0.72f, 0.45f, 0.20f), 1.0f, 0.3f);
        Material gold = new Material(new Vector3f(1.0f, 0.76f, 0.34f), 1.0f, 0.2f);
        Material silver = new Material(new Vector3f(0.95f, 0.93f, 0.88f), 1.0f, 0.15f);
        Material chrome = new Material(new Vector3f(0.55f, 0.55f, 0.55f), 1.0f, 0.05f);
        Material rubber = new Material(new Vector3f(0.1f, 0.1f, 0.12f), 0.0f, 0.95f);
        Material plastic = new Material(new Vector3f(0.8f, 0.2f, 0.2f), 0.0f, 0.4f);

        float[][] positions = {
                {-6, 0, -3}, {-2, 0, -3}, {2, 0, -3}, {6, 0, -3},
                {-4, 0, 3}, {0, 0, 3}, {4, 0, 3}
        };

        Entity box = scene.createEntity("box", GeometryGenerator.box(2, 2, 2), copper);
        box.getNode().setPosition(positions[0][0], positions[0][1] + 1, positions[0][2]);
        box.getNode().rotate(0.3f, 0, 1, 0);

        Entity sphere = scene.createEntity("sphere", GeometryGenerator.sphere(1.2f, 64, 64), chrome);
        sphere.getNode().setPosition(positions[1][0], positions[1][1] + 1.2f, positions[1][2]);

        Entity cyl = scene.createEntity("cylinder", GeometryGenerator.cylinder(0.8f, 2.5f, 48), gold);
        cyl.getNode().setPosition(positions[2][0], positions[2][1] + 1.25f, positions[2][2]);

        Entity cone = scene.createEntity("cone", GeometryGenerator.cone(1.0f, 2.5f, 48), silver);
        cone.getNode().setPosition(positions[3][0], positions[3][1] + 1.25f, positions[3][2]);

        Entity torus = scene.createEntity("torus", GeometryGenerator.torus(1.0f, 0.4f, 48, 24), rubber);
        torus.getNode().setPosition(positions[4][0], positions[4][1] + 1.2f, positions[4][2]);
        torus.getNode().rotate(0.4f, 1, 0, 0);

        Entity plane = scene.createEntity("quad", GeometryGenerator.plane(2.5f, 2.5f, 4, 4), plastic);
        plane.getNode().setPosition(positions[5][0], positions[5][1] + 1.5f, positions[5][2]);
        plane.getNode().rotate(-0.5f, 1, 0, 0);

        Entity detailedSphere = scene.createEntity("hi_sphere", GeometryGenerator.sphere(1.0f, 128, 128),
                new Material(new Vector3f(0.2f, 0.6f, 0.9f), 0.5f, 0.25f));
        detailedSphere.getNode().setPosition(positions[6][0], positions[6][1] + 1.0f, positions[6][2]);

        Entity pedestal1 = scene.createEntity("pedestal_center",
                GeometryGenerator.cylinder(2.0f, 0.3f, 48),
                new Material(new Vector3f(0.3f, 0.3f, 0.3f), 0.0f, 0.6f));
        pedestal1.getNode().setPosition(0, 0.15f, 0);

        Entity centerpiece = scene.createEntity("centerpiece",
                GeometryGenerator.torus(1.5f, 0.5f, 96, 48), gold);
        centerpiece.getNode().setPosition(0, 1.5f, 0);
        centerpiece.getNode().rotate(1.2f, 1, 0.5f, 0);

        Entity innerSphere = scene.createEntity("inner_sphere",
                GeometryGenerator.sphere(0.7f, 48, 48), chrome);
        innerSphere.getNode().setPosition(0, 1.5f, 0);
    }
}
