#version 330

in vec3 vPos;
in vec3 vCol;

uniform mat4 tr;
uniform mat4 proj;

out vec3 vColor;

void main() {
    gl_Position = proj * tr * vec4(vPos, 1.0);
    vColor = vCol;
}