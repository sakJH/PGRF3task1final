#version 330
in vec4 o_color;

uniform float u_ColorR;

out vec4 outColor;

void main() {
    outColor = vec4(u_ColorR, 0.1f, 0.1f, 1.f);
}
