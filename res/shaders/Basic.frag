#version 330
in vec2 texCoords;
in vec3 toLightVetor;
in vec3 normalVector;

uniform float u_ColorR;

uniform sampler2D textureBricks;

out vec4 outColor;

vec4 diffuseColor = vec4(0.9, 0.9, 0.9, 1.);
vec4 abmientColor = ()

void main() {
    vec4 baseColor = texture(textureBricks, texCoords);

    // outColor = vec4(u_ColorR, 0.1f, 0.1f, 1.f);

    //normalizové vektory
    // pak ještě něco
    //vypočet difusní složky
    vec3 ld = normalize(toLightVetor);
    vec3 nd = normalize(normalVector);
    float NDotL = max(dot(nd, ld), 0.);


    vec4 diffuse = NDotL * diffuseColor;
    vec4 abmient = abmientColor;
    vec4 specular = specularColor;

    outColor = (abmientColor + diffuse + specular) * baseColor;
}
