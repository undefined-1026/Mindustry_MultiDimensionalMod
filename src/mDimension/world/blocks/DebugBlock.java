package mDimension.world.blocks;

import mDimension.content.MD_Planets;
import mDimension.content.MD_beams;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;

//赛一些引用
public class DebugBlock extends Block {
    public DebugBlock(String name) {
        super(name);
        destructible = true;
    }

    public class DebugBuild extends Building {
        public static Object
                Vars = Vars.class,
                plants = MD_Planets.class,
                beams = MD_beams.class
        ;
    }
}
