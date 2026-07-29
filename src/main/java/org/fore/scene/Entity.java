package org.fore.scene;

import org.fore.material.Material;
import org.fore.mesh.Mesh;
import org.joml.Matrix4f;

/** A visible object in the scene: a mesh paired with a material, attached to a scene node. */
public class Entity {

    private final String name;
    private Mesh mesh;
    private Material material;
    private SceneNode node;
    private boolean castsShadow = true;
    private boolean receivesShadow = true;

    public Entity(String name, Mesh mesh, Material material) {
        this.name = name;
        this.mesh = mesh;
        this.material = material;
    }

    public Entity(String name, Mesh mesh) {
        this(name, mesh, new Material());
    }

    public Matrix4f getWorldMatrix() {
        if (node != null) {
            return node.getTransform().getWorldMatrix();
        }
        return new Matrix4f();
    }

    public String getName() { return name; }
    public Mesh getMesh() { return mesh; }
    public Material getMaterial() { return material; }
    public SceneNode getNode() { return node; }

    public void setMesh(Mesh mesh) { this.mesh = mesh; }
    public void setMaterial(Material material) { this.material = material; }
    void setNode(SceneNode node) { this.node = node; }

    public boolean isCastsShadow() { return castsShadow; }
    public void setCastsShadow(boolean castsShadow) { this.castsShadow = castsShadow; }
    public boolean isReceivesShadow() { return receivesShadow; }
    public void setReceivesShadow(boolean receivesShadow) { this.receivesShadow = receivesShadow; }
}
