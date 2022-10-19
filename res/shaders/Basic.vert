#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;
out vec3 toLightVector;
out vec3 normalVector;

vec3 lightSoure = vec3(0.5, 0.5, 0.1);

vec3 getNormal() {
    // TODO: korektně implementovat
    return vec3(0., 0., 1.);
}

void main() {
    texCoords = inPosition;

    vec4 objectPosition = u_View * vec4(inPosition, 0.f, 1.f);

    // Phong
    vec4 lightPosition = u_View * vec4(lightSoure, 1.);
    toLightVector = lightPosition.xyz - objectPosition.xyz;
    normalVector = transpose(inverse(mat3(u_View))) * getNormal();


    gl_Position = u_Proj * objectPosition;
}


