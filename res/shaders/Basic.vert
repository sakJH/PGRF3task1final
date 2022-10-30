#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;
uniform mat4 u_Model;

out vec2 texCoords;
out vec3 toLightVector;
out vec3 normalVector;

vec3 lightSoure = vec3(0.5, 0.5, 0.1);

const float PI = 3.1415926;

uniform int selectedModel;
out vec4 objectPos;


//vec3 getNormal(float x, float y) {
vec3 getNormal(vec2 vec) {    // vec2 vec
    // TODO: korektně implementovat
    float azim = vec.x * PI * 2.;
    float zen = vec.y * PI - PI / 2.;

    vec3 dx = vec3(-3*sin(azim)*cos(zen)*PI*2., 2.*cos(azim)*cos(zen)*2.*PI, 0.);
    vec3 dy = vec3(-3.*cos(azim)*sin(zen)*PI, -2.*sin(azim)*sin(zen)*PI, cos(zen)*PI);

    return cross(dx,dy);
}


//Objekty

// Kartézská souřadnice 1
vec3 getPlot(vec2 vec) {
    return vec3(vec.x, vec.y, 0.5 * cos(sqrt(20 * vec.x * vec.x + 20 * vec.y * vec.y)));
}
vec3 getPlotNormal(vec2 vec) {
    vec3 u = getPlot(vec + vec2(0.001, 0)) - getPlot(vec - vec2(0.001, 0));
    vec3 v = getPlot(vec + vec2(0, 0.001)) - getPlot(vec - vec2(0, 0.001));
    return cross(u, v);
}


// Prstenc ; Kartézská souřadnice 2
vec3 getRing(vec2 vec) {
    float azim = vec.y * 2.0 * PI;
    float zen = vec.x * 2.0 * PI;

    float x = 3 * cos(azim) + cos(zen) * cos(azim);
    float y = 3 * sin(azim) + cos(zen) * sin(azim);
    float z = sin(zen);

    return vec3(x, y, z);
}
//Sloní hlava - ppt 1-10 -> 67 ; Sférická souřadnice 1
vec3 getElephantHand(vec2 vec){
    float azim = vec.x * 2 * PI;
    float zen = vec.y * PI;
    float r = 3 + cos(4 * azim);

    float x = r * sin(zen) * cos(azim) ;
    float y = r * sin(zen) * sin(azim);
    float z = r * cos(zen);

    return vec3(x, y, z);
}
//Sombrero - ppt 1-10 -> 71 ; Cylindr. souřadnice 1
vec3 getSombrero(vec3 vec){
    float azimut = vec.x * 2 * PI;
    float r = vec.y * 2 * PI;
    float v = 2 * sin(r);

    float x = r * cos(azimut);
    float y = r * sin(azimut);
    float z = v;//Snad funguje

    return vec3(x, y, z);
}



void main() {
    texCoords = inPosition;

    //???
    vec2 position = inPosition - 0.1;

    vec3 normal;
    vec3 finalPos;


    //Výber těles
    if (selectedModel == 0 ){
        //Default
        //vec2 pos = inPosition * 2 - 1; float z = 0.5 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2)));
        normal = getPlotNormal(position);
        finalPos = getPlot(position);
    }
    else if (selectedModel == 1){
        //objectPos = vec4(getRing(position),1.);
        finalPos = getRing(position);
    }
    else if (selectedModel == 2){
        //objectPos = vec4(getElephantHand(position),1.);
        finalPos = getElephantHand(position);
    }
    else if (selectedModel == 3){
        //objectPos = vec4(getSombrero(position),1.);
    }
    else if (selectedModel == 4){
        //objectPos = vec4(getXXX(position),1.);
    }
    else if (selectedModel == 5){
        //objectPos = vec4(getXXX(position),1.);
    }
    else if (selectedModel == 6){
        //objectPos = vec4(getXXX(position),1.);
    }


    vec4 objectPosition = vec4(inPosition, 0.f, 1.f);

    // Phong
    vec4 lightPosition = u_View * vec4(lightSoure, 1.);
    toLightVector = lightPosition.xyz - objectPosition.xyz;
    //normalVector = transpose(inverse(mat3(u_View))) * getNormal();

    //kamera -> NDC souřadnic
    //gl_Position = u_Proj * u_Model * u_View * objectPosition;
    gl_Position = u_Proj * u_View * objectPosition;
}


