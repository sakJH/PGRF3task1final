#version 330
in vec2 texCoords;
in vec3 toLightVector;
in vec3 normalVector;

uniform float u_ColorR;

uniform sampler2D textureBricks;

out vec4 outColor;

vec4 ambientColor = vec4(0.9, 0.1, 0.1, 1.);
vec4 diffuseColor = vec4(0.9, 0.9, 0.9, 1.);

void main() {
    vec4 baseColor = texture(textureBricks, texCoords);

    // Diffuse
    vec3 ld = normalize(toLightVector);
    vec3 nd = normalize(normalVector);
    float NDotL = max(dot(nd, ld), 0.);

    vec4 ambient = ambientColor;
    vec4 diffuse = NDotL * diffuseColor;
    vec4 specular = vec4(0);

    outColor = (ambient + diffuse + specular) * baseColor;
}
