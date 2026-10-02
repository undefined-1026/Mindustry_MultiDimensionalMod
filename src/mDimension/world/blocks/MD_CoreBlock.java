package mDimension.world.blocks;

import arc.graphics.g2d.TextureRegion;
import mindustry.world.blocks.storage.CoreBlock;

public class MD_CoreBlock extends CoreBlock {

    public MD_CoreBlock(String name) {
        super(name);
        squareSprite = false;
        fullOverride = this.name + "-private";
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{this.fullIcon};
    }
}
