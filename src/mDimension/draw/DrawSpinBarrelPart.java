package mDimension.draw;

import mindustry.entities.part.DrawPart;
import mindustry.world.draw.DrawRegion;

public class DrawSpinBarrelPart extends DrawPart {
    public int barrels = 4;
    public float width = 8f;
    public float x,y;
    public DrawRegion[] drawRegions;
    public DrawSpinBarrelPart(int barrels) {
        super();
        this.barrels = barrels;
        drawRegions = new DrawRegion[barrels];
        for(int i=0;i<barrels;i++){

        }
    }

    @Override
    public void draw(PartParams params) {

    }
}
