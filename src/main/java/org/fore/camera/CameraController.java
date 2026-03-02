package org.fore.camera;

import org.fore.math.MathUtil;
import org.fore.window.InputSystem;

import static org.lwjgl.glfw.GLFW.*;

public class CameraController {

    public enum Mode { ORBIT, FLY }

    private final Camera camera;
    private final InputSystem input;
    private Mode mode = Mode.ORBIT;

    private float moveSpeed = 8.0f;
    private float lookSensitivity = 0.003f;
    private float scrollSensitivity = 1.5f;
    private float orbitDistance = 10.0f;
    private float orbitYaw = -MathUtil.HALF_PI;
    private float orbitPitch = MathUtil.toRadians(30);
    private float targetX = 0, targetY = 0, targetZ = 0;
    private float minOrbitDistance = 1.0f;
    private float maxOrbitDistance = 100.0f;

    public CameraController(Camera camera, InputSystem input) {
        this.camera = camera;
        this.input = input;
    }

    public void update(float deltaTime) {
        if (mode == Mode.ORBIT) {
            updateOrbit(deltaTime);
        } else {
            updateFly(deltaTime);
        }
    }

    private void updateOrbit(float deltaTime) {
        if (input.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            orbitYaw -= input.getMouseDeltaX() * lookSensitivity;
            orbitPitch -= input.getMouseDeltaY() * lookSensitivity;
            orbitPitch = MathUtil.clamp(orbitPitch, MathUtil.toRadians(-89), MathUtil.toRadians(89));
        }

        if (input.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            float panSpeed = orbitDistance * 0.002f;
            float dx = -input.getMouseDeltaX() * panSpeed;
            float dy = input.getMouseDeltaY() * panSpeed;

            float cosYaw = (float) Math.cos(orbitYaw);
            float sinYaw = (float) Math.sin(orbitYaw);

            targetX += dx * cosYaw;
            targetZ += dx * sinYaw;
            targetY += dy;
        }

        float scroll = input.getScrollY();
        if (scroll != 0) {
            orbitDistance -= scroll * scrollSensitivity;
            orbitDistance = MathUtil.clamp(orbitDistance, minOrbitDistance, maxOrbitDistance);
        }

        float cosPitch = (float) Math.cos(orbitPitch);
        float camX = targetX + orbitDistance * cosPitch * (float) Math.cos(orbitYaw);
        float camY = targetY + orbitDistance * (float) Math.sin(orbitPitch);
        float camZ = targetZ + orbitDistance * cosPitch * (float) Math.sin(orbitYaw);

        camera.setPosition(camX, camY, camZ);
        camera.lookAt(targetX, targetY, targetZ);
    }

    private void updateFly(float deltaTime) {
        float speed = moveSpeed * deltaTime;
        if (input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) speed *= 3.0f;

        if (input.isKeyDown(GLFW_KEY_W)) camera.move(0, 0, speed);
        if (input.isKeyDown(GLFW_KEY_S)) camera.move(0, 0, -speed);
        if (input.isKeyDown(GLFW_KEY_A)) camera.move(-speed, 0, 0);
        if (input.isKeyDown(GLFW_KEY_D)) camera.move(speed, 0, 0);
        if (input.isKeyDown(GLFW_KEY_SPACE)) camera.move(0, speed, 0);
        if (input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) camera.move(0, -speed, 0);

        if (input.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            camera.rotate(
                    -input.getMouseDeltaX() * lookSensitivity,
                    -input.getMouseDeltaY() * lookSensitivity
            );
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setOrbitTarget(float x, float y, float z) {
        targetX = x;
        targetY = y;
        targetZ = z;
    }

    public void setOrbitDistance(float distance) {
        this.orbitDistance = MathUtil.clamp(distance, minOrbitDistance, maxOrbitDistance);
    }

    public void setMoveSpeed(float speed) { this.moveSpeed = speed; }
    public void setLookSensitivity(float sensitivity) { this.lookSensitivity = sensitivity; }
    public Mode getMode() { return mode; }
    public Camera getCamera() { return camera; }
}
