package mDimension.world.blocks;

import mDimension.world.type.Beam;

public interface BeamConsumer{
    boolean acceptBeam(Beam beam,float power);
    default boolean blockBeam(Beam beam){return true;};
    void handleBeam(Beam beam,float power);
}
