package mDimension.core;

import arc.struct.Seq;
import mindustry.graphics.Shaders;

public class HoleShader extends Shaders.SurfaceShader {
    public HoleShader(String frag) {
        super(frag);
    }
    public static final int MAX = 32;
    Seq<MDRenderer.Hole> seq;
    public void setOfSeq(Seq<MDRenderer.Hole> seq){
        this.seq = seq;
    }
    @Override
    public void apply() {

        int amount = Math.min(MAX,seq.size);
        float[] data = new float[MAX*4];
        for (int i = 0; i < amount; i++) {
            var o = seq.get(i);
            int base = i*4;
            data[base] = o.x;
            data[base+1] = o.y;
            data[base+2] = o.radius;
            data[base+3] = o.strength;
        }
        setUniform4fv("u_holeData[0]",data,0,amount*4);
        setUniformi("u_count",amount);
        super.apply();
    }
}
