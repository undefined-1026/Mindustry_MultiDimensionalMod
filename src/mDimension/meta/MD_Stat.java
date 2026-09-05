package mDimension.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class MD_Stat {
    public static Stat
            percentageDamage,percentageReply,armorAdditional,armorMultiplier,percentageShieldDamage,overdrive,maxEffectThreshold,recipes
            ,energyLevel,mintomaxreload,loadingammospeed,overheatspeed;
    public static void load(){
        percentageDamage = new Stat("percentageDamage");
        percentageReply = new Stat("percentageReply");
        armorAdditional = new Stat("armorAdditional");
        armorMultiplier = new Stat("armorMultiplier");
        percentageShieldDamage = new Stat("percentageShieldDamage");
        overdrive = new Stat("overdrive", StatCat.function);
        maxEffectThreshold = new Stat("maxEffectThreshold",StatCat.function);
        recipes = new Stat("recipes",StatCat.crafting);
        energyLevel = new Stat("energylevel");
        mintomaxreload = new Stat("mintomaxreload",StatCat.function);
        loadingammospeed = new Stat("loadingammospeed",StatCat.function);
        overheatspeed = new Stat("overheatspeed",StatCat.function);

    }
}
