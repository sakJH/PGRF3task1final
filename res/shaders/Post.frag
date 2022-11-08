#version 330
in vec2 texCoord;

uniform sampler2D textureBase;

out vec4 outColor;
in vec2 texCoords;

void main() {

    outColor = texture(textureBase);
}