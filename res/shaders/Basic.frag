#version 330
in vec2 texCoords;

uniform float u_ColorR;

uniform sampler2D textureBricks;

out vec4 outColor;

void main() {
    outColor = texture(textureBricks, texCoords);
    // outColor = vec4(u_ColorR, 0.1f, 0.1f, 1.f);
}
