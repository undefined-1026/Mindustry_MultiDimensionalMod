package mDimension.util;

import arc.math.geom.Vec2;
import arc.struct.FloatSeq;

public class FloatPosSeq extends FloatSeq{
    public static final Vec2 res = new Vec2();
    @Override
    public void add(float x, float y) {
        super.add(x, y);
    }

    public Vec2 getPos(int index){
        if(index*2+1>=size)return res.set(0,0);
        return res.set(get(index*2),get(index*2+1));
    }
}
