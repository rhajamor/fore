package org.fore.material;

import org.fore.shader.ShaderProgram;
import org.fore.texture.Texture2D;
import org.joml.Vector3f;

public class Material {

    private Vector3f albedo = new Vector3f(0.8f, 0.8f, 0.8f);
    private float metallic = 0.0f;
    private float roughness = 0.5f;
    private float ao = 1.0f;
    private Vector3f emissive = new Vector3f(0, 0, 0);

    private Texture2D albedoMap;
    private Texture2D normalMap;
    private Texture2D metallicRoughnessMap;
    private Texture2D aoMap;
    private Texture2D emissiveMap;

    private boolean useAlbedoMap;
    private boolean useNormalMap;
    private boolean useMetallicRoughnessMap;
    private boolean useAoMap;
    private boolean useEmissiveMap;

    public Material() {}

    public Material(Vector3f albedo, float metallic, float roughness) {
        this.albedo.set(albedo);
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public void apply(ShaderProgram shader) {
        shader.setVec3("material.albedo", albedo);
        shader.setFloat("material.metallic", metallic);
        shader.setFloat("material.roughness", roughness);
        shader.setFloat("material.ao", ao);
        shader.setVec3("material.emissive", emissive);

        shader.setInt("material.useAlbedoMap", useAlbedoMap ? 1 : 0);
        shader.setInt("material.useNormalMap", useNormalMap ? 1 : 0);
        shader.setInt("material.useMetallicRoughnessMap", useMetallicRoughnessMap ? 1 : 0);
        shader.setInt("material.useAoMap", useAoMap ? 1 : 0);
        shader.setInt("material.useEmissiveMap", useEmissiveMap ? 1 : 0);

        int texUnit = 3;
        if (useAlbedoMap && albedoMap != null) {
            albedoMap.bind(texUnit);
            shader.setInt("materialAlbedoMap", texUnit++);
        }
        if (useNormalMap && normalMap != null) {
            normalMap.bind(texUnit);
            shader.setInt("materialNormalMap", texUnit++);
        }
        if (useMetallicRoughnessMap && metallicRoughnessMap != null) {
            metallicRoughnessMap.bind(texUnit);
            shader.setInt("materialMetallicRoughnessMap", texUnit++);
        }
        if (useAoMap && aoMap != null) {
            aoMap.bind(texUnit);
            shader.setInt("materialAoMap", texUnit++);
        }
        if (useEmissiveMap && emissiveMap != null) {
            emissiveMap.bind(texUnit);
            shader.setInt("materialEmissiveMap", texUnit);
        }
    }

    public Material setAlbedo(float r, float g, float b) {
        albedo.set(r, g, b);
        return this;
    }

    public Material setAlbedo(Vector3f color) {
        albedo.set(color);
        return this;
    }

    public Material setMetallic(float metallic) {
        this.metallic = metallic;
        return this;
    }

    public Material setRoughness(float roughness) {
        this.roughness = roughness;
        return this;
    }

    public Material setAo(float ao) {
        this.ao = ao;
        return this;
    }

    public Material setEmissive(float r, float g, float b) {
        emissive.set(r, g, b);
        return this;
    }

    public Material setAlbedoMap(Texture2D map) {
        this.albedoMap = map;
        this.useAlbedoMap = map != null;
        return this;
    }

    public Material setNormalMap(Texture2D map) {
        this.normalMap = map;
        this.useNormalMap = map != null;
        return this;
    }

    public Material setMetallicRoughnessMap(Texture2D map) {
        this.metallicRoughnessMap = map;
        this.useMetallicRoughnessMap = map != null;
        return this;
    }

    public Material setAoMap(Texture2D map) {
        this.aoMap = map;
        this.useAoMap = map != null;
        return this;
    }

    public Material setEmissiveMap(Texture2D map) {
        this.emissiveMap = map;
        this.useEmissiveMap = map != null;
        return this;
    }

    public Vector3f getAlbedo() { return albedo; }
    public float getMetallic() { return metallic; }
    public float getRoughness() { return roughness; }
    public float getAo() { return ao; }
    public Vector3f getEmissive() { return emissive; }
}
