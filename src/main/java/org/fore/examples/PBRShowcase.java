package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.mesh.Mesh;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

public class PBRShowcase implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.4f, -0.8f, -0.4f, 1.0f, 0.98f, 0.95f, 3.5f)
                .setCastsShadow(true);
        scene.addPointLight(5.0f, 5.0f, 5.0f, 0.5f, 0.7f, 1.0f, 20.0f, 30.0f);
        scene.addPointLight(-5.0f, 4.0f, -3.0f, 1.0f, 0.4f, 0.3f, 15.0f, 25.0f);

        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(30, 30, 1, 1),
                new Material(new Vector3f(0.15f, 0.15f, 0.15f), 0.0f, 0.9f));
        floor.setCastsShadow(false);

        Mesh sphereMesh = scene.createSharedMesh(GeometryGenerator.sphere(0.6f, 48, 48));

        int rows = 7;
        int cols = 7;
        float spacing = 1.8f;
        float startX = -(cols - 1) * spacing * 0.5f;
        float startZ = -(rows - 1) * spacing * 0.5f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float metallic = (float) col / (cols - 1);
                float roughness = Math.max((float) row / (rows - 1), 0.05f);

                float hue = (float) col / cols * 360.0f;
                Vector3f albedo = hsvToRgb(hue, 0.7f, 0.9f);

                Material mat = new Material(albedo, metallic, roughness);

                String name = "sphere_" + row + "_" + col;
                Entity entity = scene.createEntity(name, sphereMesh, mat);
                entity.getNode().setPosition(
                        startX + col * spacing,
                        0.8f,
                        startZ + row * spacing
                );
            }
        }

        Entity centerPiece = scene.createEntity("centerpiece",
                GeometryGenerator.torus(1.5f, 0.4f, 64, 32),
                new Material(new Vector3f(0.95f, 0.93f, 0.88f), 1.0f, 0.1f));
        centerPiece.getNode().setPosition(0, 2.5f, 0);
        centerPiece.getNode().rotate(0.5f, 1, 0, 0);
    }

    private static Vector3f hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60.0f) % 2 - 1));
        float m = v - c;

        float r, g, b;
        if (h < 60)      { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else              { r = c; g = 0; b = x; }

        return new Vector3f(r + m, g + m, b + m);
    }
}
