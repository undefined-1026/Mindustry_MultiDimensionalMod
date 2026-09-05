package mDimension.content;

import arc.graphics.Color;
import mDimension.world.data.SeedItem;
import mindustry.type.Item;

public class MD_Items {
    public static Item
            bauxite,aluminium,germanium,chromium,uranium,al_alloy,ti_alloy,polymer,carbon_fibre,polymorphic_crystal,plasma,light_ceramic,nihility_alloy

            //seed
            ,colloid_fruit
            ;

    public static void load(){
        bauxite = new Item("bauxite",Color.valueOf("ffebd0")){{
            hardness = 0;
            cost = 0.5f;
            alwaysUnlocked = true;
            hidden = true;
        }};
        aluminium = new Item("aluminium",Color.valueOf("FAA79C")){{
            healthScaling = 0.3f;
            hardness = 1;
            cost = 0.4f;
        }};
        germanium = new Item("germanium",Color.valueOf("F99BC4")){{
            healthScaling = 0.4f;
            hardness = 1;
            cost = 0.6f;
        }};
        chromium = new Item("chromium",Color.valueOf("C9B3D6")){{
            healthScaling = 0.8f;
            hardness = 1;
            cost = 2f;
        }};
        uranium = new Item("uranium",Color.valueOf("375229")){{
            healthScaling = 0.8f;
            hardness = 1;
            cost = 3f;
        }};
        al_alloy = new Item("al-alloy",Color.valueOf("DEE6FF")){{
            healthScaling = 0.5f;
            hardness = 2;
            cost = 0.7f;
        }};
        ti_alloy = new Item("ti-alloy",Color.valueOf("9373FF")){{
            healthScaling = 0.8f;
            hardness = 3;
            cost = 2f;
        }};
        polymer = new Item("polymer",Color.valueOf("FFE399")){{
            flammability = 0.6f;
            cost = 0.3f;
            hardness = 0;
        }};
        carbon_fibre = new Item("carbon-fibre",Color.valueOf("303030")){{
            flammability = 0.2f;
            cost = 1.5f;
            hardness = 1;
        }};
        polymorphic_crystal = new Item("polymorphic-crystal",Color.valueOf("F8D09E")){{
            frames = 10;
            frameTime = 20;
            transitionFrames = 15;
            charge = 1.5f;
            explosiveness =2.73f;
            radioactivity = 3.25f;
            cost = 3.5f;
            hardness = 2;

        }};
        plasma = new Item("plasma",Color.valueOf("ADB0FF")){{
            frames = 2;
            frameTime = 2.5f;
            transitionFrames = 18;
            charge = 2.5f;
            explosiveness = 2f;
            cost = 3f;
            hardness  = 0;
        }};
        light_ceramic = new Item("light-ceramic",Color.valueOf("EFFF87")){{
            frames = 2;
            frameTime = 3;
            transitionFrames = 25;
            charge = 0.8f;
            explosiveness = 0.8f;
            cost = 2.5f;
            hardness  = 1;
        }};
        nihility_alloy = new Item("nihility-alloy",Color.valueOf("FCE08C")){{
            healthScaling = 0.7f;
            charge = 1.5f;
            explosiveness = 0.5f;
            cost = 3f;
            hardness  = 3;
        }};

        colloid_fruit = new SeedItem("colloid-fruit",Color.valueOf("FFB082")){{
            flammability = 0.4f;
        }};
    }
}
