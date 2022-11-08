#version 330
in vec2 inPosition;
out vec2 texCoord;

void main() {

    texCoord = inPosition;

    vec2 position = inPosition * 2 - 1;
    vec2 finalPos = position;

    gl_Position = vec4(finalPos, 0, 1.);
}