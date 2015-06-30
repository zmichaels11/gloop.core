#version 330

in vec3 vPos;
in vec2 vTex;

uniform mat4 tr;
uniform mat4 proj;

out vec2 fTex;

void main() {
    gl_Position = proj * tr * vec4(vPos, 1.0);
    fTex = vTex;
}