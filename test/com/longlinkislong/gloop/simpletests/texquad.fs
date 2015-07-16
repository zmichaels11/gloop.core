#version 130

in vec2 uv;

uniform sampler2D tex;

out vec4 fColor;

void main() {
    fColor = texture(tex, uv);    
}