#version 330

in vec2 texCoords;

uniform sampler2D textureBase;

out vec4 outColor;

void main() {

    vec4 baseColor = texture(textureBase, texCoords);

    if (gl_FragCoord.y < 100 || gl_FragCoord.y > 250) {

        outColor = baseColor.rbga;
    } else {
        outColor = baseColor;
    }
}
