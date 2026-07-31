package org.fore.examples;

import org.fore.geometry.GeometryGenerator;
import org.fore.loader.GltfLoader;
import org.fore.material.Material;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.joml.Vector3f;

import java.io.File;
import java.util.List;

public class ModelShowcase implements ExampleScene {

    @Override
    public void setup(Scene scene) {
        scene.addDirectionalLight(-0.5f, -1.0f, -0.3f, 1.0f, 0.95f, 0.9f, 4.0f)
                .setCastsShadow(true);
        scene.addPointLight(4.0f, 5.0f, 4.0f, 0.6f, 0.8f, 1.0f, 15.0f, 30.0f);
        scene.addPointLight(-3.0f, 4.0f, -2.0f, 1.0f, 0.6f, 0.4f, 12.0f, 25.0f);

        // Floor
        Material floorMat = new Material(new Vector3f(0.4f, 0.4f, 0.4f), 0.0f, 0.8f);
        Entity floor = scene.createEntity("floor", GeometryGenerator.plane(25, 25, 1, 1), floorMat);
        floor.setCastsShadow(false);

        float xOffset = 0;

        // Load DamagedHelmet
        xOffset = loadModel(scene, "assets/models/DamagedHelmet.glb", "helmet", xOffset, 2.0f, 1.5f);

        // Load second model (Lantern or Avocado)
        if (new File("assets/models/Lantern.glb").exists()) {
            loadModel(scene, "assets/models/Lantern.glb", "lantern", xOffset, 0.4f, 0.0f);
        } else if (new File("assets/models/Avocado.glb").exists()) {
            loadModel(scene, "assets/models/Avocado.glb", "avocado", xOffset, 30.0f, 1.5f);
        }
    }

    private float loadModel(Scene scene, String path, String prefix, float xPos, float scale, float yPos) {
        if (!new File(path).exists()) return xPos;

        try {
            List<GltfLoader.GltfMesh> meshes = GltfLoader.load(path);
            for (int i = 0; i < meshes.size(); i++) {
                GltfLoader.GltfMesh gm = meshes.get(i);
                Entity entity = scene.createEntity(
                        prefix + "_" + gm.getName(),
                        gm.getMeshData(),
                        gm.getMaterial());
                entity.getNode().setPosition(xPos, yPos, 0);
                entity.getNode().setScale(scale);
            }
            return xPos + 5.0f;
        } catch (Exception e) {
            System.err.println("Failed to load model: " + path + " - " + e.getMessage());
            return xPos;
        }
    }
}
