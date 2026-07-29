package org.fore.shader;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL41.*;

/** Compiles and links GLSL vertex/fragment shaders. Provides typed uniform setters for matrices, vectors, and scalars. */
public class ShaderProgram implements AutoCloseable {

    private int programId;
    private final Map<String, Integer> uniformLocations = new HashMap<>();

    private ShaderProgram(int programId) {
        this.programId = programId;
    }

    public static ShaderProgram fromResources(String vertexPath, String fragmentPath) {
        String vertSrc = loadResource(vertexPath);
        String fragSrc = loadResource(fragmentPath);
        return fromSource(vertSrc, fragSrc);
    }

    public static ShaderProgram fromSource(String vertexSource, String fragmentSource) {
        int vertShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        int program = glCreateProgram();
        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(program);
            glDeleteProgram(program);
            glDeleteShader(vertShader);
            glDeleteShader(fragShader);
            throw new RuntimeException("Shader link error:\n" + log);
        }

        glDetachShader(program, vertShader);
        glDetachShader(program, fragShader);
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);

        return new ShaderProgram(program);
    }

    private static int compileShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            String typeName = type == GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT";
            throw new RuntimeException(typeName + " shader compile error:\n" + log);
        }
        return shader;
    }

    private static String loadResource(String path) {
        try (InputStream is = ShaderProgram.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Shader resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read shader: " + path, e);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    private int getUniformLocation(String name) {
        return uniformLocations.computeIfAbsent(name, n -> glGetUniformLocation(programId, n));
    }

    public void setInt(String name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(getUniformLocation(name), value);
    }

    public void setVec2(String name, float x, float y) {
        glUniform2f(getUniformLocation(name), x, y);
    }

    public void setVec2(String name, Vector2f v) {
        glUniform2f(getUniformLocation(name), v.x, v.y);
    }

    public void setVec3(String name, float x, float y, float z) {
        glUniform3f(getUniformLocation(name), x, y, z);
    }

    public void setVec3(String name, Vector3f v) {
        glUniform3f(getUniformLocation(name), v.x, v.y, v.z);
    }

    public void setVec4(String name, float x, float y, float z, float w) {
        glUniform4f(getUniformLocation(name), x, y, z, w);
    }

    public void setVec4(String name, Vector4f v) {
        glUniform4f(getUniformLocation(name), v.x, v.y, v.z, v.w);
    }

    public void setMat3(String name, Matrix3f mat) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(9);
            mat.get(buf);
            glUniformMatrix3fv(getUniformLocation(name), false, buf);
        }
    }

    public void setMat4(String name, Matrix4f mat) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            mat.get(buf);
            glUniformMatrix4fv(getUniformLocation(name), false, buf);
        }
    }

    public int getProgramId() {
        return programId;
    }

    @Override
    public void close() {
        if (programId != 0) {
            glDeleteProgram(programId);
            programId = 0;
        }
    }
}
