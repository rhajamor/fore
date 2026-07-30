package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.material.Material;
import org.fore.mesh.Mesh;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.fore.texture.TextureUtil;
import org.joml.Vector3f;

import java.io.File;

public class TexturedScene implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 3.5f)
                .setCastsShadow(true);
        scene.addPointLight(5.0f, 4.0f, 5.0f, 0.5f, 0.7f, 1.0f, 15.0f, 25.0f);
        scene.addPointLight(-4.0f, 3.0f, -3.0f, 1.0f, 0.5f, 0.3f, 12.0f, 20.0f);

        // Textured floor
        Material floorMat = loadMaterialOrFallback("assets/textures/concrete",
                new Vector3f(0.5f, 0.5f, 0.5f), 0.0f, 0.85f);
        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(20, 20, 1, 1), floorMat);
        floor.setCastsShadow(false);

        Mesh sphereMesh = scene.createSharedMesh(GeometryGenerator.sphere(1.0f, 64, 64));
        Mesh cubeMesh = scene.createSharedMesh(GeometryGenerator.box(1.8f, 1.8f, 1.8f));

        // Textured objects in a row
        String[] sets = {"rusted-iron", "brushed-aluminum", "wood-planks", "stone", "concrete", "fabric"};
        Vector3f[] fallbackColors = {
                new Vector3f(0.56f, 0.29f, 0.15f),  // rusted iron
                new Vector3f(0.77f, 0.78f, 0.78f),   // aluminum
                new Vector3f(0.55f, 0.35f, 0.18f),   // wood
                new Vector3f(0.5f, 0.5f, 0.48f),     // stone
                new Vector3f(0.6f, 0.6f, 0.6f),      // concrete
                new Vector3f(0.3f, 0.15f, 0.45f)     // fabric
        };
        float[] metallicDefaults = {1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] roughnessDefaults = {0.6f, 0.25f, 0.7f, 0.8f, 0.9f, 0.85f};

        float startX = -(sets.length - 1) * 2.5f / 2.0f;

        for (int i = 0; i < sets.length; i++) {
            Material mat = loadMaterialOrFallback("assets/textures/" + sets[i],
                    fallbackColors[i], metallicDefaults[i], roughnessDefaults[i]);

            // Sphere on top
            Entity sphere = scene.createEntity(sets[i] + "_sphere", sphereMesh, mat);
            sphere.getNode().setPosition(startX + i * 2.5f, 2.8f, 0);

            // Cube below
            Entity cube = scene.createEntity(sets[i] + "_cube", cubeMesh, mat);
            cube.getNode().setPosition(startX + i * 2.5f, 0.9f, 0);
            cube.getNode().rotate(0.3f, 0, 1, 0);
        }

        // Large textured back wall
        Material wallMat = loadMaterialOrFallback("assets/textures/stone",
                new Vector3f(0.5f, 0.5f, 0.48f), 0.0f, 0.8f);
        Entity wall = scene.createEntity("wall", GeometryGenerator.plane(20, 6, 1, 1), wallMat);
        wall.getNode().setPosition(0, 3, -5);
        wall.getNode().rotate((float) Math.toRadians(90), 1, 0, 0);
    }

    private Material loadMaterialOrFallback(String directory, Vector3f fallbackAlbedo,
                                             float fallbackMetallic, float fallbackRoughness) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            Material mat = TextureUtil.loadPBRMaterial(directory);
            mat.setMetallic(fallbackMetallic);
            mat.setRoughness(fallbackRoughness);
            return mat;
        }
        return new Material(fallbackAlbedo, fallbackMetallic, fallbackRoughness);
    }
}
