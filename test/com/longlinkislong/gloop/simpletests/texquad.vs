#version 130
in vec3 vPos;
in vec2 vUV;

out vec2 uv;

uniform mat4 tr;

void main() {
    uv = vUV;
    gl_Position = tr * vec4(vPos, 1.0);
}