#version 410 core

in vec3 FragPos;
out vec4 FragColor;

uniform vec3 viewPos;

void main() {
    float dist = length(FragPos.xz - viewPos.xz);
    float alpha = 1.0 - smoothstep(10.0, 40.0, dist);

    vec3 color = vec3(0.3);

    // Highlight X and Z axes
    if (abs(FragPos.x) < 0.05) color = vec3(0.2, 0.2, 0.8);
    if (abs(FragPos.z) < 0.05) color = vec3(0.8, 0.2, 0.2);

    FragColor = vec4(color, alpha * 0.4);
}
