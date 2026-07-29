package org.fore.scene;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A node in the scene graph hierarchy. Holds a local transform and computes world transforms from parent chain. */
public class SceneNode {

    private final String name;
    private final Transform transform;
    private SceneNode parent;
    private final List<SceneNode> children = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();
    private boolean visible = true;

    public SceneNode(String name) {
        this.name = name;
        this.transform = new Transform();
    }

    public SceneNode addChild(SceneNode child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        child.parent = this;
        children.add(child);
        return this;
    }

    public SceneNode removeChild(SceneNode child) {
        if (children.remove(child)) {
            child.parent = null;
        }
        return this;
    }

    public SceneNode createChild(String childName) {
        SceneNode child = new SceneNode(childName);
        addChild(child);
        return child;
    }

    public void attachEntity(Entity entity) {
        entities.add(entity);
        entity.setNode(this);
    }

    public void detachEntity(Entity entity) {
        if (entities.remove(entity)) {
            entity.setNode(null);
        }
    }

    public void update(Matrix4f parentWorld) {
        transform.updateWorldMatrix(parentWorld);

        Matrix4f world = transform.getWorldMatrix();
        for (SceneNode child : children) {
            child.update(world);
        }
    }

    public void collectVisibleEntities(List<Entity> out) {
        if (!visible) return;
        out.addAll(entities);
        for (SceneNode child : children) {
            child.collectVisibleEntities(out);
        }
    }

    public String getName() { return name; }
    public Transform getTransform() { return transform; }
    public SceneNode getParent() { return parent; }
    public List<SceneNode> getChildren() { return Collections.unmodifiableList(children); }
    public List<Entity> getEntities() { return Collections.unmodifiableList(entities); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public SceneNode setPosition(float x, float y, float z) {
        transform.setPosition(x, y, z);
        return this;
    }

    public SceneNode setScale(float s) {
        transform.setScale(s);
        return this;
    }

    public SceneNode setScale(float x, float y, float z) {
        transform.setScale(x, y, z);
        return this;
    }

    public SceneNode rotate(float angle, float ax, float ay, float az) {
        transform.rotate(angle, ax, ay, az);
        return this;
    }
}
