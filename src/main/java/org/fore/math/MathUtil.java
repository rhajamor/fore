package org.fore.math;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MathUtil {

    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = PI * 2.0f;
    public static final float HALF_PI = PI * 0.5f;
    public static final float DEG_TO_RAD = PI / 180.0f;
    public static final float RAD_TO_DEG = 180.0f / PI;
    public static final float EPSILON = 1e-6f;

    private MathUtil() {}

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float smoothStep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    public static float toRadians(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public static float toDegrees(float radians) {
        return radians * RAD_TO_DEG;
    }

    public static boolean isPowerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }

    public static int nextPowerOfTwo(int value) {
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    public static Matrix4f createLookAt(Vector3f eye, Vector3f center, Vector3f up) {
        return new Matrix4f().lookAt(eye, center, up);
    }

    public static Matrix4f createPerspective(float fovY, float aspect, float zNear, float zFar) {
        return new Matrix4f().perspective(fovY, aspect, zNear, zFar);
    }

    public static Matrix4f createOrtho(float left, float right, float bottom, float top, float zNear, float zFar) {
        return new Matrix4f().ortho(left, right, bottom, top, zNear, zFar);
    }
}
