package org.fore.texture;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.*;

/** OpenGL 2D texture. Supports creation from raw pixel data and procedural generation (checkerboard default). */
public class Texture2D implements AutoCloseable {

    private int id;
    private int width;
    private int height;

    Texture2D(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public static Texture2D fromFile(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = stbi_load(path, w, h, channels, 4);
            if (data == null) {
                throw new RuntimeException("Failed to load texture: " + path + " - " + stbi_failure_reason());
            }

            int texId = createTexture(w.get(0), h.get(0), data, GL_SRGB8_ALPHA8);
            stbi_image_free(data);

            return new Texture2D(texId, w.get(0), h.get(0));
        }
    }

    public static Texture2D fromFileLinear(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = stbi_load(path, w, h, channels, 4);
            if (data == null) {
                throw new RuntimeException("Failed to load texture: " + path + " - " + stbi_failure_reason());
            }

            int texId = createTexture(w.get(0), h.get(0), data, GL_RGBA8);
            stbi_image_free(data);

            return new Texture2D(texId, w.get(0), h.get(0));
        }
    }

    public static Texture2D fromColor(float r, float g, float b, float a) {
        ByteBuffer pixel = ByteBuffer.allocateDirect(4);
        pixel.put((byte) (r * 255));
        pixel.put((byte) (g * 255));
        pixel.put((byte) (b * 255));
        pixel.put((byte) (a * 255));
        pixel.flip();

        int texId = createTexture(1, 1, pixel, GL_RGBA8);
        return new Texture2D(texId, 1, 1);
    }

    public static Texture2D fromColor(float r, float g, float b) {
        return fromColor(r, g, b, 1.0f);
    }

    public static Texture2D checkerboard(int size, int divisions, float r1, float g1, float b1, float r2, float g2, float b2) {
        ByteBuffer pixels = ByteBuffer.allocateDirect(size * size * 4);
        int cellSize = size / divisions;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                boolean isWhite = ((x / cellSize) + (y / cellSize)) % 2 == 0;
                float r = isWhite ? r1 : r2;
                float g = isWhite ? g1 : g2;
                float b = isWhite ? b1 : b2;
                pixels.put((byte) (r * 255));
                pixels.put((byte) (g * 255));
                pixels.put((byte) (b * 255));
                pixels.put((byte) 255);
            }
        }
        pixels.flip();

        int texId = createTexture(size, size, pixels, GL_RGBA8);
        return new Texture2D(texId, size, size);
    }

    private static int createTexture(int width, int height, ByteBuffer data, int internalFormat) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, 0);
        return tex;
    }

    public void bind(int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getId() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    @Override
    public void close() {
        if (id != 0) {
            glDeleteTextures(id);
            id = 0;
        }
    }
}
