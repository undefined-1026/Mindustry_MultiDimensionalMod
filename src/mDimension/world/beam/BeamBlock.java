package mDimension.world.beam;

import arc.struct.EnumSet;
import arc.util.Eachable;
import mDimension.entity.BeamEntity;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;


public class BeamBlock extends Block {
    public BeamBlock(String name){
        super(name);
        solid = false;
        update = false;
        destructible = true;
        sync = true;
        flags = EnumSet.of(BlockFlag.factory);
    }

    public boolean handleBeam(BeamEntity entity,Building b){return false;}
    public class BeamBlockBuild extends Building{
        public boolean handleBeam(BeamEntity entity){
            return BeamBlock.this.handleBeam(entity,this);
        }
    }

    @Override
    public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list) {
        super.drawPlanConfigTop(plan, list);
    }
}
