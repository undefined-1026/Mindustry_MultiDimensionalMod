//precision mediump float;

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform highp float u_time;
varying vec2 v_texCoords;

const float timeScl = 40.0;
const float lengthMax = 15.0;
const float stroke = 1.0;
const float threshold = 0.05;
const float noiseScl = 100.0;  // ← 100 → 100.0

vec2 worldToUV(vec2 world) {
    return (world - u_campos) / u_resolution;
}

float tra(float x) {
    return 3.0 * x * x - 2.0 * x * x * x;  // ← 3 和 2 也要加 .0
}

vec4 nearsMax4(float stroke) {
    vec2 v = vec2(1.0 / u_resolution.x, 1.0 / u_resolution.y);
    return max(max(max(
                       texture2D(u_texture, v_texCoords + v * vec2(0.0, stroke)),
                       texture2D(u_texture, v_texCoords + v * vec2(0.0, -stroke))
                   ), texture2D(
                       u_texture, v_texCoords + v * vec2(stroke, 0.0)
                   )), texture2D(
                   u_texture, v_texCoords + v * vec2(-stroke, 0.0)
               ));
}

vec2 center(vec2 p, float w, float dx) {
    vec2 q = vec2(p.x + p.y + dx, p.x - p.y + dx);
    float cx = floor(q.x / w) * w + 0.5 * w;
    float cy = floor(q.y / w) * w + 0.5 * w;
    return vec2((cx + cy) * 0.5, (cx - cy) * 0.5);
}

void main() {
    float btime = u_time / 1000;
    vec2 coords = v_texCoords * u_resolution + u_campos;
    float a = texture2D(u_noise,coords/500 + vec2(btime) * vec2(-0.9, 0.8)).r + texture2D(u_noise,coords/200 + vec2(btime) * vec2(0.7,-0.9)).r;
    a/=2.0;
    vec4 c = texture2D(u_texture,v_texCoords);
    c.rgb+=vec3(a*0.4);

    gl_FragColor = c;
//    vec4 color = texture2D(u_texture, v_texCoords);
//    vec2 c = v_texCoords * u_resolution + u_campos;
//    vec2 o = center(c, 5.0, u_time / 60.0);
//    float m = abs(((sin(2.0 * cos(1.8 * o.x) + o.y + u_time / 80.0) + 1.0) + cos(u_time / 40.0 + sin(o.y * 0.7))) / 4.0);
//
//    vec4 maxed = nearsMax4(stroke * (1.0 + m * 4.0));
//    vec2 v = vec2(1.0 / u_resolution.x, 1.0 / u_resolution.y);
//    vec2 T = v_texCoords.xy;
//
//    if (maxed.a > 0.9 && color.a < 0.9) {
//        gl_FragColor = vec4(maxed.rgb, maxed.a * 0.5);
//    } else {
//        color.a *= (0.3 * (m + 0.3));
//        color.rgb *= (m / 4.0 + 1.0);
//        gl_FragColor = color;
//    }
}