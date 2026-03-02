package org.fore.window;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class InputSystem {

    private final long windowHandle;
    private final boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] prevKeys = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private final boolean[] prevMouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private double mouseX, mouseY;
    private double prevMouseX, prevMouseY;
    private double scrollX, scrollY;
    private boolean firstMouse = true;

    public InputSystem(Window window) {
        this.windowHandle = window.getHandle();

        glfwSetKeyCallback(windowHandle, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key <= GLFW_KEY_LAST) {
                keys[key] = action != GLFW_RELEASE;
            }
        });

        glfwSetMouseButtonCallback(windowHandle, (win, button, action, mods) -> {
            if (button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST) {
                mouseButtons[button] = action != GLFW_RELEASE;
            }
        });

        glfwSetCursorPosCallback(windowHandle, (win, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = ypos;
            if (firstMouse) {
                prevMouseX = xpos;
                prevMouseY = ypos;
                firstMouse = false;
            }
        });

        glfwSetScrollCallback(windowHandle, (win, xoffset, yoffset) -> {
            scrollX += xoffset;
            scrollY += yoffset;
        });
    }

    public void update() {
        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
        System.arraycopy(mouseButtons, 0, prevMouseButtons, 0, mouseButtons.length);
        prevMouseX = mouseX;
        prevMouseY = mouseY;
        scrollX = 0;
        scrollY = 0;
    }

    public boolean isKeyDown(int key) {
        return key >= 0 && key <= GLFW_KEY_LAST && keys[key];
    }

    public boolean isKeyPressed(int key) {
        return key >= 0 && key <= GLFW_KEY_LAST && keys[key] && !prevKeys[key];
    }

    public boolean isKeyReleased(int key) {
        return key >= 0 && key <= GLFW_KEY_LAST && !keys[key] && prevKeys[key];
    }

    public boolean isMouseButtonDown(int button) {
        return button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST && mouseButtons[button];
    }

    public boolean isMouseButtonPressed(int button) {
        return button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST && mouseButtons[button] && !prevMouseButtons[button];
    }

    public Vector2f getMousePosition() {
        return new Vector2f((float) mouseX, (float) mouseY);
    }

    public float getMouseDeltaX() {
        return (float) (mouseX - prevMouseX);
    }

    public float getMouseDeltaY() {
        return (float) (mouseY - prevMouseY);
    }

    public float getScrollX() {
        return (float) scrollX;
    }

    public float getScrollY() {
        return (float) scrollY;
    }

    public void setCursorMode(int mode) {
        glfwSetInputMode(windowHandle, GLFW_CURSOR, mode);
    }
}
