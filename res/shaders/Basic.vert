#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

void main() {
    //vstup <0,1>
    //vytup <-1,1>
    vec2 pos = inPosition * 2 - 1;

    vec4 posMVP = u_View * u_Proj * vec4(pos, 0.f, 1.f);

    gl_Position = posMVP;
}
