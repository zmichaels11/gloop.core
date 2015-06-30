#version 150

in vec2 fTex;
out vec4 color;

uniform sampler2D texture;

void main() {
    color = texture2D(texture, fTex);
}