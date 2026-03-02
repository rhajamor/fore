package org.fore.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f(1, 1, 1);
    private final Matrix4f localMatrix = new Matrix4f();
    private final Matrix4f worldMatrix = new Matrix4f();
    private boolean dirty = true;

    public Transform() {}

    public Transform(Vector3f position) {
        this.position.set(position);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Transform setPosition(float x, float y, float z) {
        position.set(x, y, z);
        dirty = true;
        return this;
    }

    public Transform setPosition(Vector3f pos) {
        position.set(pos);
        dirty = true;
        return this;
    }

    public Transform translate(float x, float y, float z) {
        position.add(x, y, z);
        dirty = true;
        return this;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Transform setRotation(Quaternionf rot) {
        rotation.set(rot);
        dirty = true;
        return this;
    }

    public Transform setRotationEuler(float pitch, float yaw, float roll) {
        rotation.identity().rotateYXZ(yaw, pitch, roll);
        dirty = true;
        return this;
    }

    public Transform rotate(float angle, float ax, float ay, float az) {
        rotation.rotateAxis(angle, ax, ay, az);
        dirty = true;
        return this;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Transform setScale(float x, float y, float z) {
        scale.set(x, y, z);
        dirty = true;
        return this;
    }

    public Transform setScale(float uniform) {
        scale.set(uniform, uniform, uniform);
        dirty = true;
        return this;
    }

    public Matrix4f getLocalMatrix() {
        if (dirty) {
            localMatrix.identity()
                    .translate(position)
                    .rotate(rotation)
                    .scale(scale);
            dirty = false;
        }
        return localMatrix;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public void updateWorldMatrix(Matrix4f parentWorld) {
        getLocalMatrix();
        if (parentWorld != null) {
            parentWorld.mul(localMatrix, worldMatrix);
        } else {
            worldMatrix.set(localMatrix);
        }
    }

    public Vector3f getForward() {
        return rotation.positiveZ(new Vector3f()).negate();
    }

    public Vector3f getRight() {
        return rotation.positiveX(new Vector3f());
    }

    public Vector3f getUp() {
        return rotation.positiveY(new Vector3f());
    }

    public void markDirty() {
        dirty = true;
    }
}
