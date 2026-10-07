uniform sampler2D u_texture;
uniform sampler2D u_teakSample;
uniform vec2 u_campos;
uniform vec2 u_resolution;
varying vec2 v_texCoords;

void main() {
    float p = texture2D(u_texture,v_texCoords).r;
    vec4 c = texture2D(u_teakSample,v_texCoords);
    vec4 inv = vec4(1.0,1.0,1.0,1.0)-vec4(c.r,c.g,c.b,0);
    gl_FragColor = mix(c,inv,p);

}