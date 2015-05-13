#version 130

in vec3 vPos;
uniform mat4 tr;
uniform mat4 proj;

void main() {
    gl_Position = proj * tr * vec4(vPos, 1.0);
}