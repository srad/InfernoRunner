#version 450

#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform float dimming_ratio;
uniform float passed_time;

uniform vec2 u_resolution;
uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform mat4 u_projTrans;

float random(vec2 co) {
    const highp float a = 12.9898;
    const highp float b = 78.233;
    const highp float c = 43758.5453;
    const highp float dt= dot(co.xy ,vec2(a,b));
    const highp float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}

void main() {
    vec3 color = texture2D(u_texture, v_texCoords).rgb;
    // Move down from top.
    //const vec2 v_texel_offset = vec2(v_texel.x, max(v_texel.y + 6 - passed_time * 2, v_texel.y));
    //const vec3 color_offset = texture2D(u_texture, v_texel_offset).rgb;
    const vec3 shading_map = texture2D(u_texture2, v_texCoords).rgb;
    const vec2 pos = vec2(v_texCoords);

    // Shade only yellow areas from shading map.
    if (shading_map.r == 1 && shading_map.g == 1 && shading_map.b == 0) {
        color.rgb += passed_time / 10;
        color.rgb *= random(pos) * 2;
    }

    gl_FragColor = vec4(color, 1.0);
}