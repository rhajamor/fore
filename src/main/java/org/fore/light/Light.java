package org.fore.light;

import org.fore.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Light {

    public enum Type {
        DIRECTIONAL(0),
        POINT(1),
        SPOT(2);

        public final int value;
        Type(int value) { this.value = value; }
    }

    private Type type;
    private final Vector3f position = new Vector3f();
    private final Vector3f direction = new Vector3f(0, -1, 0);
    private final Vector3f color = new Vector3f(1, 1, 1);
    private float intensity = 1.0f;
    private float range = 50.0f;
    private float innerCutoff = (float) Math.cos(Math.toRadians(12.5));
    private float outerCutoff = (float) Math.cos(Math.toRadians(17.5));
    private boolean castsShadow = false;

    private Light(Type type) {
        this.type = type;
    }

    public static Light directional(Vector3f direction, Vector3f color, float intensity) {
        Light l = new Light(Type.DIRECTIONAL);
        l.direction.set(direction).normalize();
        l.color.set(color);
        l.intensity = intensity;
        return l;
    }

    public static Light point(Vector3f position, Vector3f color, float intensity, float range) {
        Light l = new Light(Type.POINT);
        l.position.set(position);
        l.color.set(color);
        l.intensity = intensity;
        l.range = range;
        return l;
    }

    public static Light spot(Vector3f position, Vector3f direction, Vector3f color,
                             float intensity, float range, float innerAngleDeg, float outerAngleDeg) {
        Light l = new Light(Type.SPOT);
        l.position.set(position);
        l.direction.set(direction).normalize();
        l.color.set(color);
        l.intensity = intensity;
        l.range = range;
        l.innerCutoff = (float) Math.cos(Math.toRadians(innerAngleDeg));
        l.outerCutoff = (float) Math.cos(Math.toRadians(outerAngleDeg));
        return l;
    }

    public void apply(ShaderProgram shader, int index) {
        String prefix = "lights[" + index + "].";
        shader.setInt(prefix + "type", type.value);
        shader.setVec3(prefix + "position", position);
        shader.setVec3(prefix + "direction", direction);
        shader.setVec3(prefix + "color", color);
        shader.setFloat(prefix + "intensity", intensity);
        shader.setFloat(prefix + "range", range);
        shader.setFloat(prefix + "innerCutoff", innerCutoff);
        shader.setFloat(prefix + "outerCutoff", outerCutoff);
    }

    public Matrix4f getLightSpaceMatrix(float orthoSize, float nearPlane, float farPlane) {
        if (type != Type.DIRECTIONAL) {
            throw new UnsupportedOperationException("Light space matrix only supported for directional lights");
        }
        Vector3f lightPos = new Vector3f(direction).negate().mul(farPlane * 0.5f);
        Matrix4f lightView = new Matrix4f().lookAt(lightPos, new Vector3f(0), new Vector3f(0, 1, 0));
        Matrix4f lightProj = new Matrix4f().ortho(-orthoSize, orthoSize, -orthoSize, orthoSize, nearPlane, farPlane);
        return lightProj.mul(lightView);
    }

    public Type getType() { return type; }

    public Light setPosition(float x, float y, float z) {
        position.set(x, y, z);
        return this;
    }

    public Light setDirection(float x, float y, float z) {
        direction.set(x, y, z).normalize();
        return this;
    }

    public Light setColor(float r, float g, float b) {
        color.set(r, g, b);
        return this;
    }

    public Light setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }

    public Light setRange(float range) {
        this.range = range;
        return this;
    }

    public Light setCastsShadow(boolean castsShadow) {
        this.castsShadow = castsShadow;
        return this;
    }

    public Vector3f getPosition() { return position; }
    public Vector3f getDirection() { return direction; }
    public Vector3f getColor() { return color; }
    public float getIntensity() { return intensity; }
    public float getRange() { return range; }
    public boolean isCastsShadow() { return castsShadow; }
}
