#version 330
in vec2 texCoords;
in vec3 toLightVec;
//CV7
in vec3 normalVec;

in vec3 normal;
in vec3 eyeVec;

uniform float u_ColorR;

uniform sampler2D textureBase;
uniform sampler2D textureNormal;

out vec4 outColor;

uniform int lightMode;

vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1);
vec4 diffuseColor = vec4(0.5, 0.5, 0.2, 1);
vec4 specularColor = vec4(0.5, 0.5, 0.2, 1);

varying float dist;
uniform float constantAttenuation, linearAttenuation, quadraticAttenuation;

//secondObj
in vec3 seconObjDir;
in float secondObjDis;
uniform vec3 u_secondObj;

//Osvětlení reflektorem
uniform float spotCutOff;
uniform vec3 spotCutOffDir;


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

    vec3 reflection = normalize(((2.0 * nd) * NDotL) - ld);

    //Útlum prostředí
    float att;

    float constantAttenuation = 1.0;
    float linearAttenuation = 0.1;
    float quadraticAttenuation = 0.1;

    att = 1.0 / (constantAttenuation + linearAttenuation * dist + quadraticAttenuation * dist * dist);

    //Osvětlení reflektorem
    float spotEffect = max(dot(normalize(spotCutOffDir), normalize(-ld)),0);

    float blend = clamp((spotEffect - spotCutOff) / (1 - spotCutOff), 0., 1.);


    if(spotEffect > spotCutOff) { outColor = ambient + att * (diffuse + specular); }
    else { outColor = ambient; }

}