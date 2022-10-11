#version 330
in vec2 inPosition;
in vec3 inColor;

out vec4 o_color;

void main() {
    vec2 newPosition = inPosition;
    newPosition.x += 0.5f;

    gl_Position = vec4(newPosition, 0.f, 1.f);
    o_color = vec4(inColor, 1.0f);
}
