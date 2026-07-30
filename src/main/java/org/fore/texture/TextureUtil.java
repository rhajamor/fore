package org.fore.texture;

import org.fore.material.Material;
import org.lwjgl.system.MemoryStack;
import org.joml.Vector3f;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureUtil {

    public static Texture2D combineMetallicRoughness(String metallicPath, String roughnessPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer mW = stack.mallocInt(1), mH = stack.mallocInt(1), mC = stack.mallocInt(1);
            IntBuffer rW = stack.mallocInt(1), rH = stack.mallocInt(1), rC = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer metalData = stbi_load(metallicPath, mW, mH, mC, 1);
            ByteBuffer roughData = stbi_load(roughnessPath, rW, rH, rC, 1);

            if (metalData == null || roughData == null) {
                if (metalData != null) stbi_image_free(metalData);
                if (roughData != null) stbi_image_free(roughData);
                throw new RuntimeException("Failed to load metallic/roughness textures");
            }

            int width = mW.get(0);
            int height = mH.get(0);

            ByteBuffer combined = ByteBuffer.allocateDirect(width * height * 4);
            for (int i = 0; i < width * height; i++) {
                int metallic = metalData.get(i) & 0xFF;
                int roughness = roughData.get(i) & 0xFF;
                combined.put((byte) 0);           // R: unused
                combined.put((byte) roughness);   // G: roughness
                combined.put((byte) metallic);    // B: metallic
                combined.put((byte) 255);         // A: unused
            }
            combined.flip();

            stbi_image_free(metalData);
            stbi_image_free(roughData);

            int tex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, tex);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, combined);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);

            return new Texture2D(tex, width, height);
        }
    }

    public static Material loadPBRMaterial(String directory) {
        Material mat = new Material();

        File dir = new File(directory);
        File albedo = new File(dir, "albedo.png");
        File normal = new File(dir, "normal.png");
        File metallic = new File(dir, "metallic.png");
        File roughness = new File(dir, "roughness.png");
        File ao = new File(dir, "ao.png");

        if (albedo.exists()) {
            mat.setAlbedoMap(Texture2D.fromFile(albedo.getAbsolutePath()));
        }
        if (normal.exists()) {
            mat.setNormalMap(Texture2D.fromFileLinear(normal.getAbsolutePath()));
        }
        if (metallic.exists() && roughness.exists()) {
            mat.setMetallicRoughnessMap(combineMetallicRoughness(
                    metallic.getAbsolutePath(), roughness.getAbsolutePath()));
        }
        if (ao.exists()) {
            mat.setAoMap(Texture2D.fromFileLinear(ao.getAbsolutePath()));
        }

        return mat;
    }
}
