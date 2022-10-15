#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

out vec2 texCoords;

void main() {
    texCoords = inPosition;
    // vstup <0;1>
    // chci <-1;1>
    vec2 pos = inPosition * 2 - 1;
    float z = 0.5 * cos(sqrt(20 * pow(pos.x, 2) + 20 * pow(pos.y, 2)));
    vec4 posMVP = u_Proj * u_View * vec4(inPosition, z, 1.f);
    gl_Position = posMVP;
}
