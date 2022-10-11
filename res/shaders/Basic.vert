#version 330
in vec2 inPosition;

uniform mat4 u_View;
uniform mat4 u_Proj;

void main() {
    //vstup <0,1>
    //vytup <-1,1>
    vec2 pos = inPosition * 2 - 1;

    //z
    float z = (1/2) * cos(sqrt(20 * pow(pos.x,2) + 20 * pow(pos.y,2))) ;

    vec4 posMVP = u_View * u_Proj * vec4(pos, z, 1.f);

    gl_Position = posMVP;
}
