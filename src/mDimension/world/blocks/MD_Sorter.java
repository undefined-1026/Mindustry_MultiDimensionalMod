package mDimension.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mDimension.content.MD_blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.distribution.Sorter;

import static mindustry.Vars.tilesize;

public class MD_Sorter extends Sorter {
    public MD_Sorter(String name){
        super(name);
    }

    public TextureRegion center;

    @Override
    public void load() {
        super.load();
        center = Core.atlas.find(name+"-center", MD_blocks.modname+"md-sorter-center");
        cross = Core.atlas.find(name+"-cross", MD_blocks.modname+"md-sorter-cross");
    }
    protected TextureRegion[] icons(){
        return new TextureRegion[]{cross, region};
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(plan.config == null){
            Draw.rect(cross, plan.drawx(), plan.drawy());
        }else{
            if(plan.config instanceof Item i){
                Draw.color(i.color,1);
                Draw.rect(center, plan.drawx(), plan.drawy());
                Draw.color();
                Draw.rect(i.fullIcon,plan.drawx(),plan.drawy(),4f,4f);
            }
        }
        Draw.rect(region,plan.drawx(),plan.drawy());
    }

    public class MD_SorterBuild extends SorterBuild {
        @Override
        public void draw(){

            if(sortItem == null){
                Draw.rect(cross, x, y);
            }else{
                Draw.color(sortItem.color);
                Draw.rect(center,x,y);
                Draw.color();
                Draw.rect(sortItem.fullIcon,x,y,4f,4f);
            }

            if (this.block.variants != 0 && this.block.variantRegions != null) {
                Draw.rect(this.block.variantRegions[Mathf.randomSeed((long)this.tile.pos(), 0, Math.max(0, this.block.variantRegions.length - 1))], this.x, this.y, this.drawrot());
            } else {
                Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            }

            this.drawTeamTop();
        }

    }
}
