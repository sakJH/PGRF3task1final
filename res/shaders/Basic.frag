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

uniform int u_LightMode;

vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1);
vec4 diffuseColor = vec4(0.5, 0.5, 0.2, 1);
vec4 specularColor = vec4(0.5, 0.5, 0.2, 1);

in float dist;
uniform float constantAttenuation, linearAttenuation, quadraticAttenuation;

//secondObj
in vec3 seconObjDir;
in float secondObjDis;
uniform vec3 u_secondObj;

//Osvětlení reflektorem
uniform float spotCutOff;

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
    float linearAttenuation = 0.2;
    float quadraticAttenuation = 0.05;

    vec3 spotCutOffDir = - u_secondObj;

    att = 1.0 / (constantAttenuation + linearAttenuation * dist + quadraticAttenuation * dist * dist);

    //Osvětlení reflektorem
    float spotEffect = max(dot(normalize(spotCutOffDir), normalize(-ld)), 0);

    float blend = clamp((spotEffect - spotCutOff) / (1 - spotCutOff), 0., 1.);

    //Módy osvětlení
    if (u_LightMode == 0) {
        outColor = (ambient + diffuse + specular) * baseColor; }

    if (u_LightMode == 1) {
        //ambientni složka + textura
        outColor = ambient * baseColor; }

    if (u_LightMode == 2) {
        // difuzni složka + textura
        outColor = (vec4(0.5, 0.5, 0.5, 1)) * baseColor;  }

    if (u_LightMode == 3) {
        outColor = (ambient  + att * (specular + diffuse) + att * (specular + diffuse)) * baseColor; }

    if (u_LightMode == 4) {
        //bez osvětlovacího modelu
        outColor = baseColor; }

    if (u_LightMode == 5) {
        // všechny složky bez textura
        outColor = (ambient + diffuse + specular); }

    if (u_LightMode == 6) {
        // Útlum složka
        outColor = (ambient + att * (diffuse + specular) ) * baseColor; }

    if (u_LightMode == 7) { }

    if (u_LightMode == 8) {
        //spotEffect
        if(spotEffect > spotCutOff) { outColor = ambient + att * (diffuse + specular); }
        else { outColor = ambient; }
    }

    if (u_LightMode == 9) {
        //Blend
        outColor = (mix(ambient, ambient + att * (diffuse + specular),blend)) * baseColor;
    }

    if (u_LightMode == 10) outColor = vec4(vec3(1, 1, 1), 1.0f);

    //gl_FragDepth;
}
