attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec4 v_mouse;
attribute vec4 v_window;

uniform vec4 dimming_ration;
uniform float dimming;
uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 mouse;
varying vec2 window;
varying float color_scale;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;

    mouse = vec2(v_mouse[0], v_mouse[1]);
    window = vec2(v_window[0], v_window[1]);

    gl_Position = u_projTrans * a_position;
}