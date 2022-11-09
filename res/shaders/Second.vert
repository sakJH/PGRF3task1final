#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;
uniform mat4 u_Model;

out vec3 objectPos;
out vec3 normalDir;

uniform vec3 eyePos;
uniform  float spotCutOff;

out vec3 eyeVec;
out vec2 texCoords;
out vec3 toLightVec;
out vec3 normalVec;

vec3 lightSoure = vec3(0.5, 0.5, 0.1);

const float PI = 3.1415926;

uniform int selectedModel;
out vec4 objectPosition;

out float dist;

uniform vec3 u_secondObj;
uniform float u_time;
out vec3 secondObjDir;
out float secondObjDis;


vec3 getNormal(vec2 vec) {
    float azim = vec.x * PI * 2.;
    float zen = vec.y * PI - PI / 2.;

    vec3 dx = vec3(-3*sin(azim)*cos(zen)*PI*2., 2.*cos(azim)*cos(zen)*2.*PI, 0.);
    vec3 dy = vec3(-3.*cos(azim)*sin(zen)*PI, -2.*sin(azim)*sin(zen)*PI, cos(zen)*PI);

    return cross(dx,dy);
}

vec3 getSecondSphere(vec2 vec) {
    float s = PI * 0.5 - PI * vec.y;
    float t = 2 * PI * vec.x;
    float r = 2;

    float x = sin(t) * cos(s) * r;
    float y = cos(t) * cos(s) * r;
    float z = sin(s) * r;

    return vec3( x, y, z);
}

void main() {
    texCoords = inPosition;

    vec2 position = inPosition - 0.1;

    vec3 normal;
    vec3 finalPos;

    normal = getNormal(position);
    finalPos = getSecondSphere(position);


    // Osvětlovací model
    vec4 lightPosition = u_View * vec4(lightSoure, 1.);
    toLightVec = lightPosition.xyz - objectPosition.xyz;
    normalVector = transpose(inverse(mat3(u_View))) * getNormal();

    vec2 a = vec2(float (2));

    objectPos = finalPos;
    normalDir = inverse(transpose(mat3(u_Model))) * normal;

    vec4 finalPos4 = u_Model * vec4(finalPos, 1.0);

    dist = length(lightPosition);

    //secondObj
    secondObjDir = normalize(u_secondObj - finalPos4.xyz);
    eyeVec = normalize(eyePos - finalPos4.xyz);
    secondObjDis = length(u_secondObj - finalPos4.xyz);

    vec4 pos4 = vec4(finalPos, 1.0);
    //kamera -> NDC souřadnic
    gl_Position = u_Proj * u_View * u_Model * pos4;
}