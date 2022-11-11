#version 330
in vec2 texCoords;
in vec3 toLightVec;
in vec3 normalVec;

in vec3 normal;
in vec3 eyeVec;
in float dist;
//secondObj
in vec3 seconObjDir;
in float secondObjDis;

out vec4 outColor;

uniform float u_ColorR;

uniform sampler2D textureBase;
uniform sampler2D textureNormal;

uniform int u_LightMode;
uniform float constantAttenuation, linearAttenuation, quadraticAttenuation;
uniform vec3 u_secondObj;
//Osvětlení reflektorem
uniform float spotCutOff;

void main() {
    vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1);
    vec4 diffuseColor = vec4(0.5, 0.5, 0.5, 1);
    vec4 specularColor = vec4(0.5, 0.5, 0.5, 1);

    vec4 baseColor = texture(textureBase, texCoords);

    vec3 ld = normalize(toLightVec);
    vec3 nd = normalize(normalVec);
    vec3 vd = normalize(eyeVec);

    float NDotL = max(dot(nd, ld), 0.);

    vec3 halfVec = normalize(ld + vd);

    float NDotH = pow(max(0, dot(nd, halfVec)), 15.);

    vec4 ambient = ambientColor.rgba;
    vec4 diffuse = NDotL * diffuseColor.rgba * baseColor;
    vec4 specular = NDotH * specularColor.rgba;
    //float specularPower = 3; vec4 specular = specularColor * ( pow(NDotH, specularPower * 4) );

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
    if (u_LightMode == 0) { outColor = (ambient + diffuse + specular) * baseColor; }

    if (u_LightMode == 1) { outColor = ambient * baseColor; }

    if (u_LightMode == 2) { outColor = (vec4(0.5, 0.5, 0.5, 1)) * baseColor;  }

    if (u_LightMode == 3) { outColor = (ambient  + att * (specular + diffuse) + att * (specular + diffuse)) * baseColor; }

    if (u_LightMode == 4) { outColor = baseColor; }

    if (u_LightMode == 5) { outColor = (ambient + diffuse + specular) * baseColor; }

    if (u_LightMode == 6) { outColor = (ambient + att * (diffuse + specular) ) * baseColor; }

    if (u_LightMode == 7) { outColor =  (ambient + blend) * baseColor;}

    if (u_LightMode == 8) { if(spotEffect > spotCutOff) { outColor = mix(ambient , ambient + att * (diffuse + specular), blend); }  else { outColor = ambient; } }

    if (u_LightMode == 9) { outColor = mix(ambient, ambient + att * (diffuse + specular), blend) ; }

    if (u_LightMode == 10) outColor = vec4(vec3(1, 1, 1), 1.0f);

}
