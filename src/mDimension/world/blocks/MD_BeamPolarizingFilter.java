package mDimension.world.blocks;

import mDimension.entity.BeamEntity;
import mDimension.world.beam.BeamBlock;
import mindustry.gen.Building;

public class MD_BeamPolarizingFilter extends BeamBlock {
    public MD_BeamPolarizingFilter(String name) {
        super(name);
    }

    @Override
    public boolean handleBeam(BeamEntity e, Building b) {
        if(b.enabled){
            e.node(e.cx,e.cy);
            e.collision = !e.collision;
            e.node(e.cx,e.cy);
        }
        return false;
    }
}
