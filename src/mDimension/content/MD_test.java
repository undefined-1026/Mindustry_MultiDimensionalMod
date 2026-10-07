package mDimension.content;

import mDimension.core.MDRenderer;
import mDimension.world.blocks.TestBlock;

public class MD_test {
    public static void load(){
        var test1 = new TestBlock("debug"){
            float str = 50;
            float rad = 10f;
            {
                draw = b-> MDRenderer.addHole(b.x,b.y,str,rad);
            }
        };
    }
}
