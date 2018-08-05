#version 450

#ifdef GL_ES
    precision mediump float;
#endif

#define draw_edge true

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 mouse;
varying vec2 window;

uniform float dimming_ratio;
uniform int invert;

uniform vec2 u_resolution;
uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform mat4 u_projTrans;

// http://byteblacksmith.com/improvements-to-the-canonical-one-liner-glsl-rand-for-opengl-es-2-0/
float random(vec2 co) {
    const highp float a = 12.9898;
    const highp float b = 78.233;
    const highp float c = 43758.5453;
    const highp float dt= dot(co.xy ,vec2(a,b));
    const highp float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}

bool isInLightTrace(vec2 pos, vec2 v_mouse, float distanceFromDiagonale) {
    const float lightOffset = v_mouse.x  - 200;
    const float tan = (v_mouse.x - lightOffset) / v_mouse.y;
    const float fn = ceil(pos.y * tan);
    const float coneShaper = pos.y / v_mouse.y;
    const float fnOffsetLeft = fn + distanceFromDiagonale * coneShaper;
    const float fnOffsetRight = fn - distanceFromDiagonale * coneShaper;
    const float offsetedCenterX = ceil(pos.x - lightOffset);
    // Check range |>=-----O-----=<|
    return (offsetedCenterX <= fnOffsetLeft) && (offsetedCenterX >= fnOffsetRight);
}

void main() {
    vec3 color = texture2D(u_texture, v_texCoords).rgb;
    const vec3 color_original = texture2D(u_texture, v_texCoords).rgb;
    vec3 c_bump = texture2D(u_texture2, v_texCoords).rgb;

    //vec2 size = textureSize2D(u_texture, 0);
    const vec2 pos = vec2(gl_FragCoord.xy);
    const vec2 center = vec2(window) * 0.5;
    // fix mouse y pos relative to middle (0,0)
    const vec2 v_mouse = vec2(mouse.x, (mouse.y - (center.y * 2)) * -1);
    const float maxDistance = distance(v_mouse, center);

    const float ratioToCenter = 1.0 - (distance(pos, center) / maxDistance);
    const float pixelToMouseDist = distance(gl_FragCoord.xy, v_mouse);
    float scale = dimming_ratio;

    const float angle = dot(v_mouse, pos) / (length(v_mouse) * length(pos));
    const float lighting = dot(normalize(v_mouse), normalize(pos)) * angle;

    // Stipe mod
    const float m = mod(floor(gl_FragCoord.y), 5.0);
    const int lightradius = 90;
    const int lightEdgeThickness = 10;

    // Mouse circle
    // main inner circle area
    if (pixelToMouseDist <= lightradius && pixelToMouseDist > 0) {
        if (random(pos) < 0.2) {
            // Simulate griddyness / dirt / scatches on surface.
            scale = 0.1f;
        } else {
            // decrease light until circle edge.
            // Exp gives the center a lamp-like look.
            scale = abs(exp(-pixelToMouseDist / 40));
        }

        // apply bump inside light
        if (c_bump.r > 0) {
            color.r *= c_bump.r * 5 * lighting;
            color.g = color.r;
            color.b = color.r;
            color.rgb *= 0.20;
        }
    }
    // edge of light
    else if (draw_edge && (pixelToMouseDist > lightradius && pixelToMouseDist < (lightradius + lightEdgeThickness))) {
        color.rgb += c_bump.r;
        scale = log((pixelToMouseDist * 0.98) / lightradius) * 1.9;
    }
    // any other area outside of the light
    else {
        // Lamp light trace.
        const bool inLightTrace = (pos.y < v_mouse.y) && isInLightTrace(pos, v_mouse, lightradius + lightEdgeThickness);
        if (inLightTrace) {
            float offsetRatio;
            const float fadeOut = pos.y / v_mouse.y;
            color.rgb = (color_original.rgb + c_bump.r * fadeOut * 0.3) * fadeOut * 0.2;
            scale = 0.7f;
        } else {
            // stripes
            if (m <= 2) {
                scale = 0.1;
            }
            // change the brightness of some lines
            else if (m == 3) {
                scale = 1.5;
            }

            // Add noise to lines
            if (m > 2) {
                const float r = random(pos);
                if (r < 0.45) {
                    scale = 0;
                }
            }
            if (!inLightTrace && invert == 1 && c_bump.r == 0) {
                color.rgb = (1 - color.rgb) * 0.6;
            }

            // Outside of light area still.
            // 1. If we have bump data then use it.
            // 2. Also scale depending on the mouse distance and dim a little.
            if ((c_bump.r > 0) && (ratioToCenter < 0.1)) {
                const float bumpMouseScale = c_bump.r * ((1 - ratioToCenter) * 0.01);
                color.rgb *= bumpMouseScale;
                scale = 1;
                if (!inLightTrace && invert == 1) {
                    color.r = 0.1;
                    color.g = 0.1;
                    color.b = 0;
                }
            } else {
                // Use if to flicker whole screen during "thunder", instead of lightened radius.
                //if (dimming_ratio == 1) {
                    const float s = smoothstep(0, .8, ratioToCenter);
                    color.rgb = color.rgb * s * 1.4f;
                //}
            }
        }
    }

    color.rgb *= scale;
    gl_FragColor = vec4(color, 1.0);
}