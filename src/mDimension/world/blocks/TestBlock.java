package mDimension.world.blocks;

import arc.func.Cons;
import mDimension.core.MDRenderer;
import mindustry.gen.Building;
import mindustry.world.blocks.production.GenericCrafter;

public class TestBlock extends GenericCrafter {
    public Cons<Building> draw = (b)->{};
    public Cons<Building> update = (b)->{};
    public Runnable init = ()->{};


    public TestBlock(String name){
        super(name);
    }

    @Override
    public void init() {
        super.init();
        init.run();
    }

    public class TestBlockBuild extends GenericCrafterBuild{
        public MDRenderer renderer = MDRenderer.renderer;
        @Override
        public void draw() {
            super.draw();
            draw.get(this);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            update.get(this);
        }
    }
}
