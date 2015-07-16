#version 130

in vec3 vPos;
in vec3 vColor;

out vec3 color;

uniform mat4 tr;

void main() {
    color = vColor;
    gl_Position = tr * vec4(vPos, 1.0);
}