package org.fore.core;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.fore.camera.Camera;
import org.fore.camera.CameraController;
import org.fore.examples.*;
import org.fore.render.RenderSystem;
import org.fore.scene.Scene;
import org.fore.window.InputSystem;
import org.fore.window.Window;

import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Core engine class managing the main loop lifecycle. Initializes the window,
 * render system, camera, and active scene, then runs the update/render loop
 * until the window closes. Configured via Quarkus {@code fore.*} properties.
 */
@ApplicationScoped
@Unremovable
public class Engine {

    private static final Logger LOG = Logger.getLogger(Engine.class.getName());

    @ConfigProperty(name = "fore.window.width", defaultValue = "1600")
    int windowWidth;

    @ConfigProperty(name = "fore.window.height", defaultValue = "900")
    int windowHeight;

    @ConfigProperty(name = "fore.window.title", defaultValue = "FORE Engine — Free OpenGL Rendering Engine")
    String windowTitle;

    @ConfigProperty(name = "fore.scene", defaultValue = "pbr")
    String sceneName;

    @ConfigProperty(name = "fore.render.exposure", defaultValue = "1.0")
    float exposure;

    @ConfigProperty(name = "fore.render.vsync", defaultValue = "true")
    boolean vsync;

    private Window window;
    private InputSystem input;
    private RenderSystem renderSystem;
    private Camera camera;
    private CameraController cameraController;
    private Scene activeScene;
    private TimeStep timeStep;

    public void run() {
        initialize();
        try {
            mainLoop();
        } finally {
            shutdown();
        }
    }

    private void initialize() {
        LOG.info("Initializing FORE Engine...");

        window = new Window(windowWidth, windowHeight, windowTitle);
        input = new InputSystem(window);
        renderSystem = new RenderSystem();

        renderSystem.initialize(window.getFramebufferWidth(), window.getFramebufferHeight());
        renderSystem.setExposure(exposure);

        camera = new Camera();
        camera.setAspectRatio(window.getAspectRatio());

        cameraController = new CameraController(camera, input);
        cameraController.setMode(CameraController.Mode.ORBIT);
        cameraController.setOrbitDistance(12.0f);
        cameraController.setOrbitTarget(0, 1, 0);

        timeStep = new TimeStep();

        activeScene = createScene(sceneName);
        activeScene.setActiveCamera(camera);

        LOG.info("FORE Engine initialized. Scene: " + sceneName);
        LOG.info("Controls: LMB=orbit, MMB=pan, Scroll=zoom, 1-5=scenes, G=grid, ESC=quit");
    }

    private Scene createScene(String name) {
        ExampleScene example = switch (name.toLowerCase()) {
            case "basic" -> new BasicScene();
            case "lighting" -> new LightingDemo();
            case "shapes" -> new ShapesShowcase();
            case "textured" -> new TexturedScene();
            default -> new PBRShowcase();
        };

        Scene scene = new Scene(name);
        example.setup(scene);
        return scene;
    }

    private void switchScene(String name) {
        if (activeScene != null) {
            activeScene.dispose();
        }
        activeScene = createScene(name);
        activeScene.setActiveCamera(camera);
        LOG.info("Switched to scene: " + name);
    }

    private void mainLoop() {
        while (!window.shouldClose()) {
            timeStep.update();
            input.update();

            handleInput();

            if (window.wasResized()) {
                int fbW = window.getFramebufferWidth();
                int fbH = window.getFramebufferHeight();
                renderSystem.resize(fbW, fbH);
                camera.setAspectRatio(window.getAspectRatio());
            }

            cameraController.update(timeStep.getDeltaTime());
            renderSystem.render(activeScene);

            window.swapBuffers();
            window.pollEvents();

            if (timeStep.getFrameCount() % 60 == 0) {
                window.setTitle(String.format("%s — %.0f FPS", windowTitle, timeStep.getFps()));
            }
        }
    }

    private void handleInput() {
        if (input.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getHandle(), true);
        }

        if (input.isKeyPressed(GLFW_KEY_G)) {
            renderSystem.setShowGrid(!renderSystem.isShowGrid());
        }

        if (input.isKeyPressed(GLFW_KEY_1)) switchScene("basic");
        if (input.isKeyPressed(GLFW_KEY_2)) switchScene("pbr");
        if (input.isKeyPressed(GLFW_KEY_3)) switchScene("lighting");
        if (input.isKeyPressed(GLFW_KEY_4)) switchScene("shapes");
        if (input.isKeyPressed(GLFW_KEY_5)) switchScene("textured");

        if (input.isKeyPressed(GLFW_KEY_TAB)) {
            CameraController.Mode current = cameraController.getMode();
            cameraController.setMode(
                    current == CameraController.Mode.ORBIT
                            ? CameraController.Mode.FLY
                            : CameraController.Mode.ORBIT
            );
        }

        if (input.isKeyPressed(GLFW_KEY_EQUAL)) {
            renderSystem.setExposure(renderSystem.getExposure() + 0.1f);
        }
        if (input.isKeyPressed(GLFW_KEY_MINUS)) {
            renderSystem.setExposure(Math.max(0.1f, renderSystem.getExposure() - 0.1f));
        }
    }

    private void shutdown() {
        LOG.info("Shutting down FORE Engine...");
        if (activeScene != null) activeScene.dispose();
        if (renderSystem != null) renderSystem.close();
        if (window != null) window.close();
        LOG.info("FORE Engine shut down.");
    }
}
