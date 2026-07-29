package org.fore.camera;

import org.fore.math.MathUtil;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Perspective camera with configurable FOV, near/far planes, and aspect ratio. Computes view and projection matrices. */
public class Camera {

    private final Vector3f position = new Vector3f(0, 2, 5);
    private final Vector3f front = new Vector3f(0, 0, -1);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final Vector3f right = new Vector3f(1, 0, 0);
    private final Vector3f worldUp = new Vector3f(0, 1, 0);

    private float yaw = -MathUtil.HALF_PI;
    private float pitch = 0;

    private float fov = MathUtil.toRadians(60.0f);
    private float aspectRatio = 16.0f / 9.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 500.0f;

    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();
    private boolean viewDirty = true;
    private boolean projDirty = true;

    public Camera() {
        updateVectors();
    }

    public Camera(Vector3f position, float yaw, float pitch) {
        this.position.set(position);
        this.yaw = yaw;
        this.pitch = pitch;
        updateVectors();
    }

    private void updateVectors() {
        float cosPitch = (float) Math.cos(pitch);
        front.set(
                (float) Math.cos(yaw) * cosPitch,
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * cosPitch
        ).normalize();

        front.cross(worldUp, right);
        right.normalize();
        right.cross(front, up);
        up.normalize();

        viewDirty = true;
    }

    public Matrix4f getViewMatrix() {
        if (viewDirty) {
            Vector3f target = new Vector3f(position).add(front);
            viewMatrix.identity().lookAt(position, target, up);
            viewDirty = false;
        }
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        if (projDirty) {
            projectionMatrix.identity().perspective(fov, aspectRatio, nearPlane, farPlane);
            projDirty = false;
        }
        return projectionMatrix;
    }

    public void lookAt(float x, float y, float z) {
        Vector3f dir = new Vector3f(x, y, z).sub(position).normalize();
        pitch = (float) Math.asin(dir.y);
        yaw = (float) Math.atan2(dir.z, dir.x);
        updateVectors();
    }

    public void lookAt(Vector3f target) {
        lookAt(target.x, target.y, target.z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        viewDirty = true;
    }

    public void setPosition(Vector3f pos) {
        position.set(pos);
        viewDirty = true;
    }

    public void move(float dx, float dy, float dz) {
        position.add(
                right.x * dx + up.x * dy + front.x * dz,
                right.y * dx + up.y * dy + front.y * dz,
                right.z * dx + up.z * dy + front.z * dz
        );
        viewDirty = true;
    }

    public void rotate(float yawDelta, float pitchDelta) {
        yaw += yawDelta;
        pitch += pitchDelta;
        pitch = MathUtil.clamp(pitch, -MathUtil.HALF_PI + 0.01f, MathUtil.HALF_PI - 0.01f);
        updateVectors();
    }

    public void setFov(float fovDegrees) {
        this.fov = MathUtil.toRadians(fovDegrees);
        projDirty = true;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        projDirty = true;
    }

    public void setClipPlanes(float near, float far) {
        this.nearPlane = near;
        this.farPlane = far;
        projDirty = true;
    }

    public Vector3f getPosition() { return position; }
    public Vector3f getFront() { return front; }
    public Vector3f getUp() { return up; }
    public Vector3f getRight() { return right; }
    public float getFov() { return fov; }
    public float getAspectRatio() { return aspectRatio; }
    public float getNearPlane() { return nearPlane; }
    public float getFarPlane() { return farPlane; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}
