#version 450

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform vec4 dimming_ration;
uniform float dimming;
uniform mat4 u_projTrans;
uniform float passed_time;

varying vec4 v_color;
varying vec2 v_texCoords;
varying float color_scale;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;

    gl_Position = u_projTrans * a_position;
}