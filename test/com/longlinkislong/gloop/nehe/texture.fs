#version 130

in vec2 fTex;
out vec4 color;

uniform sampler2D texture;

void main() {
    color = vec4(texture2D(texture, fTex).rgb, 1.0);
}