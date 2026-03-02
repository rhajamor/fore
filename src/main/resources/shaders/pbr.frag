#version 410 core

const float PI = 3.14159265359;
const int MAX_LIGHTS = 16;

struct MaterialData {
    vec3 albedo;
    float metallic;
    float roughness;
    float ao;
    vec3 emissive;

    int useAlbedoMap;
    int useNormalMap;
    int useMetallicRoughnessMap;
    int useAoMap;
    int useEmissiveMap;
};

struct LightData {
    int type;       // 0=directional, 1=point, 2=spot
    vec3 position;
    vec3 direction;
    vec3 color;
    float intensity;
    float range;
    float innerCutoff;
    float outerCutoff;
};

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord;
in vec4 FragPosLightSpace;
in mat3 TBN;

out vec4 FragColor;

uniform MaterialData material;
uniform sampler2D materialAlbedoMap;
uniform sampler2D materialNormalMap;
uniform sampler2D materialMetallicRoughnessMap;
uniform sampler2D materialAoMap;
uniform sampler2D materialEmissiveMap;

uniform LightData lights[MAX_LIGHTS];
uniform int lightCount;
uniform vec3 viewPos;
uniform sampler2D shadowMap;

// GGX/Trowbridge-Reitz normal distribution
float distributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;
    float denom = NdotH2 * (a2 - 1.0) + 1.0;
    return a2 / (PI * denom * denom);
}

// Schlick-GGX geometry function
float geometrySchlickGGX(float NdotV, float roughness) {
    float r = roughness + 1.0;
    float k = (r * r) / 8.0;
    return NdotV / (NdotV * (1.0 - k) + k);
}

float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    return geometrySchlickGGX(NdotV, roughness) * geometrySchlickGGX(NdotL, roughness);
}

// Fresnel-Schlick approximation
vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

float shadowCalculation(vec4 fragPosLightSpace, vec3 normal, vec3 lightDir) {
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;

    if (projCoords.z > 1.0) return 0.0;

    float currentDepth = projCoords.z;
    float bias = max(0.005 * (1.0 - dot(normal, lightDir)), 0.001);

    // PCF soft shadows
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        }
    }
    return shadow / 25.0;
}

float attenuate(float distance, float range) {
    float att = clamp(1.0 - pow(distance / range, 4.0), 0.0, 1.0);
    return att * att / (distance * distance + 1.0);
}

void main() {
    vec3 albedo = material.albedo;
    if (material.useAlbedoMap == 1) {
        albedo = pow(texture(materialAlbedoMap, TexCoord).rgb, vec3(2.2));
    }

    float metallic = material.metallic;
    float roughness = material.roughness;
    if (material.useMetallicRoughnessMap == 1) {
        vec2 mr = texture(materialMetallicRoughnessMap, TexCoord).bg;
        metallic = mr.x;
        roughness = mr.y;
    }
    roughness = max(roughness, 0.04);

    float ao = material.ao;
    if (material.useAoMap == 1) {
        ao = texture(materialAoMap, TexCoord).r;
    }

    vec3 emissive = material.emissive;
    if (material.useEmissiveMap == 1) {
        emissive = texture(materialEmissiveMap, TexCoord).rgb;
    }

    vec3 N = normalize(Normal);
    if (material.useNormalMap == 1) {
        N = texture(materialNormalMap, TexCoord).rgb * 2.0 - 1.0;
        N = normalize(TBN * N);
    }

    vec3 V = normalize(viewPos - FragPos);
    vec3 F0 = mix(vec3(0.04), albedo, metallic);

    vec3 Lo = vec3(0.0);

    for (int i = 0; i < lightCount && i < MAX_LIGHTS; i++) {
        vec3 L;
        float attenuation = 1.0;

        if (lights[i].type == 0) {
            // Directional
            L = normalize(-lights[i].direction);
        } else {
            // Point or Spot
            L = lights[i].position - FragPos;
            float dist = length(L);
            L = normalize(L);
            attenuation = attenuate(dist, lights[i].range);

            if (lights[i].type == 2) {
                // Spot
                float theta = dot(L, normalize(-lights[i].direction));
                float epsilon = lights[i].innerCutoff - lights[i].outerCutoff;
                float spotIntensity = clamp((theta - lights[i].outerCutoff) / epsilon, 0.0, 1.0);
                attenuation *= spotIntensity;
            }
        }

        vec3 H = normalize(V + L);
        vec3 radiance = lights[i].color * lights[i].intensity * attenuation;

        float NDF = distributionGGX(N, H, roughness);
        float G = geometrySmith(N, V, L, roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

        vec3 numerator = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular = numerator / denominator;

        vec3 kS = F;
        vec3 kD = (1.0 - kS) * (1.0 - metallic);

        float NdotL = max(dot(N, L), 0.0);

        // Shadow (only first directional light)
        float shadow = 0.0;
        if (i == 0 && lights[i].type == 0) {
            shadow = shadowCalculation(FragPosLightSpace, N, L);
        }

        Lo += (1.0 - shadow) * (kD * albedo / PI + specular) * radiance * NdotL;
    }

    vec3 ambient = vec3(0.03) * albedo * ao;
    vec3 color = ambient + Lo + emissive;

    FragColor = vec4(color, 1.0);
}
