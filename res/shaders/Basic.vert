#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;
uniform mat4 u_Model;

//TODO
uniform float type;
out vec3 objectPos;
out vec3 normalDir;

uniform vec3 eyePos;


out vec3 eyeVec;
out vec2 texCoords;
out vec3 toLightVec;
out vec3 normalVec;

vec3 lightSoure = vec3(0.5, 0.5, 0.1);

const float PI = 3.1415926;

uniform int selectedModel;
out vec4 objectPosition;

//TODO secondOBJ
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


//OBJEKTY
/*Kartézské souřadnice
-

Sférické souřadnice
- Kulová soustava souřadnic
- Parametry azimut, zenit, poloměr (R)

Cylindrické souřadnice
- Válcová soustava souřadnic
- Parametry azimut, poloměr, výška
*/


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
vec3 getSombrero(vec2 vec){
    float azim = vec.x * 2 * PI;
    float zen = vec.y * 2 * PI;
    float v = 2 * sin(zen);

    float x = zen * cos(azim);
    float y = zen * sin(azim);
    float z = v;

    return vec3(x, y, z); //TODO - nefunguje
}
// Kartez 3 - https://christopherchudzicki.github.io/MathBox-Demos/parametric_surfaces_3D.html
vec3 getCylinder(vec2 vec){
    float azim = vec.x * PI * 2.;
    float zen = vec.y * PI - PI / 2.;

    float x = 3 * cos(azim);//u
    float y = 3 * sin(azim);//u
    float z = zen; //v

    return vec3(x,y,z);
}
// Sférické souř. 2 - https://www.wolframalprha.com/input/?i=3D+spherical+plot+%283+++cos%5B8%%CF%95%8E%F9%2C+%7B%CE%B8%2C+0%2C+Pi%7D%2C+%7B%CF%95%2C+0%2C+2Pi%7D
vec3 getSphere(vec2 vec) {
    float azim = vec.x * PI;
    float zen = vec.y * PI ;
    float r = 1.;

    float x = r * cos(azim) * cos(zen);
    float y =  r * sin(azim) * cos(zen);
    float z =  r * sin(zen);

    return vec3(x, y, z);
}

//Cosi jaksi https://reference.wolfram.com/language/ref/SphericalPlot3D.html  Sphere
//Cylinder
vec3 getUnknown(vec2 vec) {
    float azim = 1 + 2 * cos(2 * vec.y);
    float zen = vec.x + vec.y * PI;
    float r = PI + vec.x;

    float x = zen * cos(azim);
    float y = zen * sin(azim);
    float z = r * sin(zen);

    return vec3(x, y, z);
}



/*
Ripple
sin(10(x^2+y^2))/10

Pyramid
1-abs(x+y)-abs(y-x)
*/

// Přepočty na souřadicové systémy
vec3 transKartezToSferic(vec3 vec){

    float r = sqrt( (vec.x * vec.x) + (vec.y * vec.y) + (vec.z * vec.z));
    float azim = atan(vec.y,vec.z);
    float zen = acos(vec.z/r);

    return vec3(r,azim,zen);
}

vec3 transKartezToCylinder(vec3 vec){

    float r = sqrt( (vec.x * vec.x) + (vec.y * vec.y));
    float azim = atan(vec.y,vec.z);
    float zen = vec.z;

    return vec3(r,azim,zen);
}

vec3 getSecondSphere(vec2 vec){
    float azim = vec.x * PI;
    float zem = vec.y * PI / 2.0;
    float r = 0.1;

    float x = r * cos(azim) * cos(zem);
    float y =  r * sin(azim) * cos(zem);
    float z =  r * sin(zem);
    return vec3(x, y, z);
}


//CV7
vec3 getTangent() {
    // TODO: implementovat
    return vec3(0);
}



void main() {
    texCoords = inPosition;

    vec2 position = inPosition - 0.1;

    vec3 normal;
    vec3 finalPos;

    //Výber těles
    if (selectedModel == 0 ){
        //normal = getNormal(position);
        finalPos = getPlot(position);
    }
    else if (selectedModel == 1){
        normal = getNormal(position);
        finalPos = getRing(position);
    }
    else if (selectedModel == 2){
        normal = getNormal(position);
        finalPos = getElephantHand(position);
    }
    else if (selectedModel == 3){
        normal = getNormal(position);
        finalPos = getSombrero(position);
    }
    else if (selectedModel == 4){
        normal = getNormal(position);
        finalPos = getCylinder(position);
    }
    else if (selectedModel == 5){
        normal = getNormal(position);
        finalPos = getSphere(position);
    }
    else if (selectedModel == 6){
        normal = getNormal(position);
        finalPos = getUnknown(position);
    }
    else if (selectedModel == 7){
        //Second OBj
        normal = getNormal(position);
        finalPos = getSecondSphere(position);
    }


    //CV7 ---
    vec3 tangent = mat3(u_View) * getTangent();
    vec3 bitangent = cross(normalize(normalVec), normalize(tangent));

    // TODO: Vytvořit TBN matici
    mat3 tbn = mat3(1);

    // TODO: Aplikovat TBN na vektory, které se používají pro výpočet osvětlení
    //--- CV7


    // Phong
    vec4 lightPosition = u_View * vec4(lightSoure, 1.);
    toLightVec = lightPosition.xyz - objectPosition.xyz;
    //normalVector = transpose(inverse(mat3(u_View))) * getNormal();

    vec2 a = vec2(float (2));

    objectPos = finalPos;
    normalDir = inverse(transpose(mat3(u_Model))) * normal;

    vec4 finalPos4 = u_Model * vec4(finalPos, 1.0);

    //secondObj
    secondObjDir = normalize(u_secondObj - finalPos4.xyz);
    eyeVec = normalize(eyePos - finalPos4.xyz);
    secondObjDis = length(u_secondObj - finalPos4.xyz);

    vec4 pos4 = vec4(finalPos, 1.0);
    //kamera -> NDC souřadnic
    gl_Position = u_Proj * u_View * u_Model * pos4;
}


