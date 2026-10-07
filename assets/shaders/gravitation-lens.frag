const int MAX = 32;
uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
varying vec2 v_texCoords;
uniform vec4 u_holeData[MAX];
uniform int u_count;
void main() {
    vec2 uv = v_texCoords*u_resolution + u_campos;
    vec2 totalOffset = vec2(0.0);
    for (int i = 0; i < MAX; i++) {
        if(i>=u_count)break;
        vec2 pos = u_holeData[i].xy;
        float rad = u_holeData[i].z;
        float str = u_holeData[i].w;

        vec2 dir = pos-uv;
        float dst = length(dir);
        if(dst>rad || dst < 0.01)continue;
        float inf = smoothstep(rad,0.0,dst) * str;
        totalOffset += normalize(dir) * inf * 0.1;
    }
    vec2 resUv = clamp((uv+totalOffset-u_campos)/u_resolution,0.0,1.0);
    gl_FragColor = texture2D(u_texture,resUv);
}