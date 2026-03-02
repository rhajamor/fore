package org.fore.render;

import org.fore.camera.Camera;
import org.fore.light.Light;
import org.fore.material.Material;
import org.fore.mesh.Mesh;
import org.fore.scene.Entity;
import org.fore.scene.Scene;
import org.fore.shader.ShaderProgram;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL41.*;

public class RenderSystem implements AutoCloseable {

    private static final int SHADOW_MAP_SIZE = 2048;
    private static final int MAX_LIGHTS = 16;

    private ShaderProgram pbrShader;
    private ShaderProgram shadowShader;
    private ShaderProgram postProcessShader;
    private ShaderProgram gridShader;

    private Framebuffer hdrFramebuffer;
    private Framebuffer shadowFramebuffer;
    private Mesh screenQuad;

    private boolean showGrid = true;
    private Mesh gridMesh;

    private int viewportWidth;
    private int viewportHeight;

    private float exposure = 1.0f;
    private float gamma = 2.2f;

    private final Matrix4f lightSpaceMatrix = new Matrix4f();

    public void initialize(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;

        pbrShader = ShaderProgram.fromResources("shaders/pbr.vert", "shaders/pbr.frag");
        shadowShader = ShaderProgram.fromResources("shaders/shadow.vert", "shaders/shadow.frag");
        postProcessShader = ShaderProgram.fromResources("shaders/postprocess.vert", "shaders/postprocess.frag");
        gridShader = ShaderProgram.fromResources("shaders/grid.vert", "shaders/grid.frag");

        hdrFramebuffer = Framebuffer.createHDR(width, height);
        shadowFramebuffer = Framebuffer.createShadowMap(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
        screenQuad = Mesh.createScreenQuad();

        gridMesh = createGridMesh(40, 1.0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_MULTISAMPLE);
    }

    public void render(Scene scene) {
        scene.update();
        Camera camera = scene.getActiveCamera();
        List<Entity> entities = scene.getVisibleEntities();
        List<Light> lights = scene.getLights();

        shadowPass(entities, lights);
        geometryPass(entities, lights, camera);
        postProcessPass();
    }

    private void shadowPass(List<Entity> entities, List<Light> lights) {
        Light shadowCaster = findShadowCaster(lights);
        if (shadowCaster != null) {
            lightSpaceMatrix.set(shadowCaster.getLightSpaceMatrix(20.0f, 0.1f, 80.0f));
        } else if (!lights.isEmpty()) {
            Light firstDir = lights.stream()
                    .filter(l -> l.getType() == Light.Type.DIRECTIONAL)
                    .findFirst().orElse(null);
            if (firstDir != null) {
                lightSpaceMatrix.set(firstDir.getLightSpaceMatrix(20.0f, 0.1f, 80.0f));
            } else {
                lightSpaceMatrix.identity();
            }
        }

        shadowFramebuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT);
        glCullFace(GL_FRONT);

        shadowShader.bind();
        shadowShader.setMat4("lightSpaceMatrix", lightSpaceMatrix);

        for (Entity entity : entities) {
            if (!entity.isCastsShadow()) continue;
            shadowShader.setMat4("model", entity.getWorldMatrix());
            entity.getMesh().draw();
        }

        glCullFace(GL_BACK);
        shadowFramebuffer.unbind();
    }

    private void geometryPass(List<Entity> entities, List<Light> lights, Camera camera) {
        hdrFramebuffer.bind();
        glViewport(0, 0, viewportWidth, viewportHeight);
        glClearColor(0.05f, 0.05f, 0.08f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (showGrid) {
            renderGrid(camera);
        }

        pbrShader.bind();

        pbrShader.setMat4("view", camera.getViewMatrix());
        pbrShader.setMat4("projection", camera.getProjectionMatrix());
        pbrShader.setVec3("viewPos", camera.getPosition());
        pbrShader.setMat4("lightSpaceMatrix", lightSpaceMatrix);

        int lightCount = Math.min(lights.size(), MAX_LIGHTS);
        pbrShader.setInt("lightCount", lightCount);
        for (int i = 0; i < lightCount; i++) {
            lights.get(i).apply(pbrShader, i);
        }

        shadowFramebuffer.bindDepthTexture(0);
        pbrShader.setInt("shadowMap", 0);

        for (Entity entity : entities) {
            Matrix4f model = entity.getWorldMatrix();
            pbrShader.setMat4("model", model);

            Matrix3f normalMatrix = new Matrix3f();
            model.normal(normalMatrix);
            pbrShader.setMat3("normalMatrix", normalMatrix);

            entity.getMaterial().apply(pbrShader);
            entity.getMesh().draw();
        }

        pbrShader.unbind();
        hdrFramebuffer.unbind();
    }

    private void renderGrid(Camera camera) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);

        gridShader.bind();
        gridShader.setMat4("view", camera.getViewMatrix());
        gridShader.setMat4("projection", camera.getProjectionMatrix());
        gridShader.setMat4("model", new Matrix4f());
        gridShader.setVec3("viewPos", camera.getPosition());

        gridMesh.draw();

        gridShader.unbind();
        glDepthMask(true);
        glDisable(GL_BLEND);
    }

    private void postProcessPass() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, viewportWidth, viewportHeight);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        postProcessShader.bind();
        hdrFramebuffer.bindColorTexture(0);
        postProcessShader.setInt("hdrBuffer", 0);
        postProcessShader.setFloat("exposure", exposure);
        postProcessShader.setFloat("gamma", gamma);

        screenQuad.draw();

        postProcessShader.unbind();
        glEnable(GL_DEPTH_TEST);
    }

    private Light findShadowCaster(List<Light> lights) {
        for (Light l : lights) {
            if (l.isCastsShadow()) return l;
        }
        return null;
    }

    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewportWidth = width;
        viewportHeight = height;
        hdrFramebuffer.resize(width, height);
    }

    private Mesh createGridMesh(int halfSize, float spacing) {
        int lineCount = (halfSize * 2 + 1) * 2;
        float[] vertices = new float[lineCount * 2 * 3];
        int idx = 0;

        for (int i = -halfSize; i <= halfSize; i++) {
            float pos = i * spacing;
            float extent = halfSize * spacing;

            vertices[idx++] = pos; vertices[idx++] = 0; vertices[idx++] = -extent;
            vertices[idx++] = pos; vertices[idx++] = 0; vertices[idx++] = extent;

            vertices[idx++] = -extent; vertices[idx++] = 0; vertices[idx++] = pos;
            vertices[idx++] = extent;  vertices[idx++] = 0; vertices[idx++] = pos;
        }

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glBindVertexArray(0);

        return new GridLineMesh(vao, vbo, lineCount * 2);
    }

    public void setExposure(float exposure) { this.exposure = exposure; }
    public void setGamma(float gamma) { this.gamma = gamma; }
    public void setShowGrid(boolean showGrid) { this.showGrid = showGrid; }
    public float getExposure() { return exposure; }
    public boolean isShowGrid() { return showGrid; }

    @Override
    public void close() {
        if (pbrShader != null) pbrShader.close();
        if (shadowShader != null) shadowShader.close();
        if (postProcessShader != null) postProcessShader.close();
        if (gridShader != null) gridShader.close();
        if (hdrFramebuffer != null) hdrFramebuffer.close();
        if (shadowFramebuffer != null) shadowFramebuffer.close();
        if (screenQuad != null) screenQuad.close();
        if (gridMesh != null) gridMesh.close();
    }

    private static class GridLineMesh extends Mesh {
        private final int vao;
        private final int vbo;
        private final int vertexCount;

        GridLineMesh(int vao, int vbo, int vertexCount) {
            this.vao = vao;
            this.vbo = vbo;
            this.vertexCount = vertexCount;
        }

        @Override
        public void draw() {
            glBindVertexArray(vao);
            glDrawArrays(GL_LINES, 0, vertexCount);
            glBindVertexArray(0);
        }

        @Override
        public void close() {
            glDeleteVertexArrays(vao);
            glDeleteBuffers(vbo);
        }
    }
}
