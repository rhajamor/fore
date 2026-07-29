package org.fore.scene;

import org.fore.camera.Camera;
import org.fore.geometry.GeometryGenerator;
import org.fore.light.Light;
import org.fore.material.Material;
import org.fore.mesh.Mesh;
import org.fore.mesh.MeshData;
import org.fore.mesh.VertexLayout;

import java.util.*;

/**
 * Container for a scene graph, lights, and entities. Manages the node hierarchy,
 * shared meshes, light list, and provides factory methods for creating entities
 * and lights.
 */
public class Scene {

    private final String name;
    private final SceneNode rootNode;
    private Camera activeCamera;
    private final List<Light> lights = new ArrayList<>();
    private final Map<String, SceneNode> nodeMap = new HashMap<>();
    private final List<Entity> allEntities = new ArrayList<>();
    private final List<Mesh> ownedMeshes = new ArrayList<>();

    public Scene(String name) {
        this.name = name;
        this.rootNode = new SceneNode("root");
        this.activeCamera = new Camera();
    }

    public SceneNode createNode(String nodeName) {
        SceneNode node = rootNode.createChild(nodeName);
        nodeMap.put(nodeName, node);
        return node;
    }

    public SceneNode createChildNode(String parentName, String childName) {
        SceneNode parent = nodeMap.get(parentName);
        if (parent == null) {
            throw new IllegalArgumentException("Parent node not found: " + parentName);
        }
        SceneNode child = parent.createChild(childName);
        nodeMap.put(childName, child);
        return child;
    }

    public Entity createEntity(String entityName, MeshData meshData) {
        return createEntity(entityName, meshData, new Material());
    }

    public Entity createEntity(String entityName, MeshData meshData, Material material) {
        Mesh mesh = Mesh.create(meshData, VertexLayout.POS_NORMAL_UV_TANGENT);
        ownedMeshes.add(mesh);
        Entity entity = new Entity(entityName, mesh, material);
        SceneNode node = createNode(entityName + "_node");
        node.attachEntity(entity);
        allEntities.add(entity);
        return entity;
    }

    public Entity createEntity(String entityName, Mesh mesh, Material material) {
        Entity entity = new Entity(entityName, mesh, material);
        SceneNode node = createNode(entityName + "_node");
        node.attachEntity(entity);
        allEntities.add(entity);
        return entity;
    }

    public Mesh createSharedMesh(MeshData meshData) {
        Mesh mesh = Mesh.create(meshData, VertexLayout.POS_NORMAL_UV_TANGENT);
        ownedMeshes.add(mesh);
        return mesh;
    }

    public Light addDirectionalLight(float dx, float dy, float dz, float r, float g, float b, float intensity) {
        Light light = Light.directional(
                new org.joml.Vector3f(dx, dy, dz),
                new org.joml.Vector3f(r, g, b),
                intensity
        );
        lights.add(light);
        return light;
    }

    public Light addPointLight(float x, float y, float z, float r, float g, float b, float intensity, float range) {
        Light light = Light.point(
                new org.joml.Vector3f(x, y, z),
                new org.joml.Vector3f(r, g, b),
                intensity, range
        );
        lights.add(light);
        return light;
    }

    public Light addSpotLight(float px, float py, float pz, float dx, float dy, float dz,
                              float r, float g, float b, float intensity, float range,
                              float innerAngle, float outerAngle) {
        Light light = Light.spot(
                new org.joml.Vector3f(px, py, pz),
                new org.joml.Vector3f(dx, dy, dz),
                new org.joml.Vector3f(r, g, b),
                intensity, range, innerAngle, outerAngle
        );
        lights.add(light);
        return light;
    }

    public void update() {
        rootNode.update(null);
    }

    public List<Entity> getVisibleEntities() {
        List<Entity> visible = new ArrayList<>();
        rootNode.collectVisibleEntities(visible);
        return visible;
    }

    public String getName() { return name; }
    public SceneNode getRootNode() { return rootNode; }
    public Camera getActiveCamera() { return activeCamera; }
    public void setActiveCamera(Camera camera) { this.activeCamera = camera; }
    public List<Light> getLights() { return lights; }
    public SceneNode getNode(String name) { return nodeMap.get(name); }
    public List<Entity> getAllEntities() { return allEntities; }

    public void dispose() {
        for (Mesh mesh : ownedMeshes) {
            mesh.close();
        }
        ownedMeshes.clear();
    }
}
