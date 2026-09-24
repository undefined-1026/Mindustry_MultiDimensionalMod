package mDimension.input;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import mDimension.world.blocks.Slant;
import mindustry.graphics.Pal;
import mindustry.input.MobileInput;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class MD_MobileInput extends MobileInput {
    @Override
    public void drawArrow(Block block, int x, int y, int rotation, boolean valid) {
        if(block instanceof Slant s&& s.isSlant()) {
            float trns = (block.size / 2) * tilesize;
            int dx = Geometry.d8edge(rotation).x, dy = Geometry.d8edge(rotation).y;
            float offsetx = x * tilesize + block.offset + dx*trns;
            float offsety = y * tilesize + block.offset + dy*trns;

            Draw.color(!valid ? Pal.removeBack : Pal.accentBack);
            TextureRegion regionArrow = Core.atlas.find("place-arrow");

            Draw.rect(regionArrow,
                    offsetx,
                    offsety - 1,
                    regionArrow.width * regionArrow.scl(),
                    regionArrow.height * regionArrow.scl(),
                    rotation * 90 - 45);

            Draw.color(!valid ? Pal.remove : Pal.accent);
            Draw.rect(regionArrow,
                    offsetx,
                    offsety,
                    regionArrow.width * regionArrow.scl(),
                    regionArrow.height * regionArrow.scl(),
                    rotation * 90 - 45);
        }else{
            super.drawArrow(block, x, y, rotation, valid);
        }
    }
}
