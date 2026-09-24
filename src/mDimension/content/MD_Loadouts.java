package mDimension.content;

import arc.struct.Seq;
import arc.struct.StringMap;
import mindustry.Vars;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;

public class MD_Loadouts {
    public static Schematic
            basicDepicilon;
    public static void load(){
        basicDepicilon = Schematics.readBase64("bXNjaAF4nBWKMQrDMAxFf0LI0ELH3iK3yNoTlAyKrEFgyybyUkLvHgUeb3kPE6bAqAheO7nyKk1ZczU8uFoX6x9qGM8/nkmcD21dIwJzpl2yY/xuA94laRHzSAvXQxbvQukX23ATugBPoR0y");
        loadLoadoutsOf(MD_blocks.coreEngineering);
    }

    public static Schematic generateCore(Block core){
        var s = new Schematic(
                Seq.with(new Schematic.Stile(core,1,1,null,(byte) 0)),
                new StringMap(),core.size,core.size
        );
        return s;
    }

    public static void loadLoadoutsOf(Block c){
        Vars.schematics.getLoadouts((CoreBlock)c).add(generateCore(c));
    }
}
