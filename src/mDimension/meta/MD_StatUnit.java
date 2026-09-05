package mDimension.meta;

import mindustry.world.meta.StatUnit;

public class MD_StatUnit {
    public static StatUnit
            percentsecond,laserPower;
    public static void load(){
        percentsecond = new StatUnit("percentsecond");
        laserPower = new StatUnit("laserpower");
    }
}
