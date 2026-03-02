package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class LightingDemo implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.3f, -1.0f, -0.5f, 0.4f, 0.45f, 0.6f, 1.5f)
                .setCastsShadow(true);

        float[][] pointLights = {
                {4, 3, 0, 1.0f, 0.2f, 0.2f, 25, 20},
                {-4, 3, 0, 0.2f, 1.0f, 0.2f, 25, 20},
                {0, 3, 4, 0.2f, 0.2f, 1.0f, 25, 20},
                {0, 3, -4, 1.0f, 1.0f, 0.2f, 25, 20},
        };

        for (float[] pl : pointLights) {
            scene.addPointLight(pl[0], pl[1], pl[2], pl[3], pl[4], pl[5], pl[6], pl[7]);
        }

        scene.addSpotLight(0, 8, 0, 0, -1, 0,
                1.0f, 0.9f, 0.8f, 40.0f, 30.0f, 15.0f, 25.0f);

        Material floorMat = new Material(new Vector3f(0.3f, 0.3f, 0.35f), 0.0f, 0.7f);
        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(30, 30, 1, 1), floorMat);
        floor.setCastsShadow(false);

        Material wallMat = new Material(new Vector3f(0.5f, 0.5f, 0.55f), 0.0f, 0.8f);

        Entity backWall = scene.createEntity("back_wall", GeometryGenerator.box(20, 6, 0.3f), wallMat);
        backWall.getNode().setPosition(0, 3, -8);

        Entity leftWall = scene.createEntity("left_wall", GeometryGenerator.box(0.3f, 6, 16), wallMat);
        leftWall.getNode().setPosition(-10, 3, 0);

        Material whiteMat = new Material(new Vector3f(0.9f, 0.9f, 0.9f), 0.0f, 0.4f);

        Entity central = scene.createEntity("pedestal", GeometryGenerator.cylinder(0.8f, 1.5f, 32),
                new Material(new Vector3f(0.6f, 0.6f, 0.6f), 0.0f, 0.6f));
        central.getNode().setPosition(0, 0.75f, 0);

        Entity statue = scene.createEntity("statue", GeometryGenerator.sphere(1.0f, 64, 64), whiteMat);
        statue.getNode().setPosition(0, 2.5f, 0);

        for (int i = 0; i < 4; i++) {
            float angle = (float) (i * Math.PI * 0.5);
            float x = (float) Math.cos(angle) * 5.0f;
            float z = (float) Math.sin(angle) * 5.0f;

            Entity pillar = scene.createEntity("pillar_" + i,
                    GeometryGenerator.cylinder(0.3f, 4.0f, 16),
                    new Material(new Vector3f(0.7f, 0.7f, 0.7f), 0.0f, 0.5f));
            pillar.getNode().setPosition(x, 2.0f, z);

            Entity cap = scene.createEntity("cap_" + i,
                    GeometryGenerator.sphere(0.15f, 16, 16),
                    new Material()
                            .setAlbedo(pointLights[i][3], pointLights[i][4], pointLights[i][5])
                            .setEmissive(pointLights[i][3] * 3, pointLights[i][4] * 3, pointLights[i][5] * 3)
                            .setMetallic(0).setRoughness(1));
            cap.getNode().setPosition(x, 4.2f, z);
            cap.setCastsShadow(false);
        }
    }
}
