#version 330
in vec2 texCoords;
in vec3 toLightVector;
//CV7
//in vec3 normalVector;

in vec3 normal;

uniform float u_ColorR;

//CV7
//uniform sampler2D textureBricks;
uniform sampler2D textureBase;
uniform sampler2D textureNormal;


out vec4 outColor;

uniform int lightMode;

vec4 ambientColor = vec4(0.9, 0.1, 0.1, 0.1);
vec4 diffuseColor = vec4(0.9, 0.9, 0.9, 0.1);

void main() {

    // Diffuse
    vec3 ld = normalize(toLightVector);
    vec3 nd = normalize(normalVector);
    float NDotL = max(dot(nd, ld), 0.);

    vec4 ambient = ambientColor;
    vec4 diffuse = NDotL * diffuseColor;
    vec4 specular = vec4(0);

    /*float NdotH = dot(normalize(normalIO), halfVector);
    vec4 specular = vec4(pow(NdotH, 16) * vec3(1.0), 1);
    totalSpecular = specular * ( pow( NDotH, specularPower*4.0 ) );*/


    if (lightMode == 0) {
        outColor = (ambient + diffuse + specular) * baseColor;

    } else if (lightMode == 1) {
        //ambientni složka
        outColor = ambient * baseColor;

    } else if (lightMode == 2) {
        // difuzni složka
        outColor = diffuse * baseColor;

    } else if (lightMode == 3) {
        // zrcadlova složka
        outColor = specular + baseColor;
    }

    //outColor = (ambient + diffuse + specular) * baseColor;
}


//CV7
/*
#version 330
in vec2 texCoords;
in vec3 toLightVector;

uniform float u_ColorR;

uniform sampler2D textureBase;
uniform sampler2D textureNormal;

out vec4 outColor;

vec4 ambientColor = vec4(0.9, 0.1, 0.1, 1.);
vec4 diffuseColor = vec4(0.9, 0.9, 0.9, 1.);

void main() {
    vec4 baseColor = texture(textureBase, texCoords);

    vec4 textureColor = texture(textureNormal, texCoords);
    // Tangent space
    vec3 normal = textureColor.rgb * 2.f - 1.f;

    // Diffuse
    vec3 ld = normalize(toLightVector);
    vec3 nd = normalize(normal);
    float NDotL = max(dot(nd, ld), 0.);

    vec4 ambient = ambientColor;
    vec4 diffuse = NDotL * diffuseColor;
    vec4 specular = vec4(0);

    //outColor = (ambient + diffuse + specular) * baseColor;
    outColor = baseColor;
}

*/