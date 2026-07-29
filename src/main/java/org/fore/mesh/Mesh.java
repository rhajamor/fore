package org.fore.mesh;

import static org.lwjgl.opengl.GL41.*;

/** GPU-resident mesh using VAO/VBO/EBO. Uploads vertex data from {@link MeshData} and issues indexed draw calls. */
public class Mesh implements AutoCloseable {

    private int vao;
    private int vbo;
    private int ebo;
    private int indexCount;
    private int vertexCount;
    private VertexLayout layout;

    protected Mesh() {}

    private Mesh(int vao, int vbo, int ebo, int indexCount, int vertexCount, VertexLayout layout) {
        this.vao = vao;
        this.vbo = vbo;
        this.ebo = ebo;
        this.indexCount = indexCount;
        this.vertexCount = vertexCount;
        this.layout = layout;
    }

    public static Mesh create(MeshData data, VertexLayout layout) {
        float[] vertices = data.toInterleavedArray(layout);
        int[] indices = data.toIndexArray();

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        layout.apply();

        int ebo = 0;
        if (indices.length > 0) {
            ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }

        glBindVertexArray(0);

        return new Mesh(vao, vbo, ebo, indices.length, data.getVertexCount(), layout);
    }

    public static Mesh createScreenQuad() {
        float[] quadVertices = {
                -1.0f,  1.0f, 0.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f,
                 1.0f, -1.0f, 1.0f, 0.0f,

                -1.0f,  1.0f, 0.0f, 1.0f,
                 1.0f, -1.0f, 1.0f, 0.0f,
                 1.0f,  1.0f, 1.0f, 1.0f,
        };

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2L * Float.BYTES);

        glBindVertexArray(0);

        return new Mesh(vao, vbo, 0, 0, 6, null);
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void draw() {
        glBindVertexArray(vao);
        if (indexCount > 0) {
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        }
        glBindVertexArray(0);
    }

    public void drawInstanced(int instanceCount) {
        glBindVertexArray(vao);
        if (indexCount > 0) {
            glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, instanceCount);
        } else {
            glDrawArraysInstanced(GL_TRIANGLES, 0, vertexCount, instanceCount);
        }
        glBindVertexArray(0);
    }

    public int getIndexCount() { return indexCount; }
    public int getVertexCount() { return vertexCount; }
    public VertexLayout getLayout() { return layout; }

    @Override
    public void close() {
        if (vao != 0) {
            glDeleteVertexArrays(vao);
            vao = 0;
        }
        if (vbo != 0) {
            glDeleteBuffers(vbo);
            vbo = 0;
        }
        if (ebo != 0) {
            glDeleteBuffers(ebo);
            ebo = 0;
        }
    }
}
