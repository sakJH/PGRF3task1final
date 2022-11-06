#version 330
in vec2 texCoords;
in vec3 toLightVec;
//CV7
in vec3 normalVec;

in vec3 normal;
in vec3 eyeVec;

uniform float u_ColorR;

//CV7
//uniform sampler2D textureBricks;
uniform sampler2D textureBase;
uniform sampler2D textureNormal;


out vec4 outColor;

uniform int lightMode;

vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1);
vec4 diffuseColor = vec4(0.5, 0.5, 0.2, 1);
vec4 specularColor = vec4(0.5, 0.5, 0.2, 1);

//secondObj
in vec3 seconObjDir;
in float seconObjDis;
uniform vec3 u_secondObj;

void main() {
    vec4 baseColor = texture(textureBase, texCoords);

    // Diffuse
    vec3 ld = normalize(toLightVec);
    vec3 nd = normalize(normalVec);
    vec3 vd = normalize(eyeVec);

    float NDotL = max(dot(nd, ld), 0.);

    vec3 halfVec = normalize(ld + vd);

    float NDotH = pow(max(0, dot(nd, halfVec)), 15.);

    vec4 ambient = ambientColor.rgba;
    vec4 diffuse = NDotL * diffuseColor.rgba;
    vec4 specular = NDotH * specularColor.rgba;

    vec3 reflection = normalize( ( ( 2.0 * nd ) * NDotL ) - ld );

    //Útlum prostředí
    float constantAttenuation, linearAttenuation, quadraticAttenuation, att;

    constantAttenuation = 1.0;
    linearAttenuation = 0.3;
    quadraticAttenuation = 0.01;

    att = 1.0 / (constantAttenuation + linearAttenuation * seconObjDis + quadraticAttenuation * seconObjDis * seconObjDis);

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
        outColor = specular * baseColor;

    } else if (lightMode == 4) {
        // zrcadlova složka
        outColor = baseColor;

    } else if (lightMode == 5) {
        // zrcadlova složka
        outColor = (ambient + diffuse + specular);
    } else if (lightMode == 6) {
        // zrcadlova složka
        outColor = (ambient + att * (diffuse + specular)) * baseColor;
    }


    gl_FragDepth;
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