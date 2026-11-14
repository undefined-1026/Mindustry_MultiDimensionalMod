package mDimension.content.building;
import mDimension.content.MD_Items;
import mindustry.type.Category;
import mindustry.world.blocks.defense.Wall;

import static mDimension.content.building.MD_blocks.*;
import static mindustry.type.ItemStack.with;

public class MD_Walls {
    public static void load(){
        //region wall
        aluminium_wall = new Wall("aluminium-wall"){{
            requirements(Category.defense,with(MD_Items.aluminium,6));
            scaledHealth = 550f;
            armor = 1;
            researchCost = with( MD_Items.aluminium, 20);

        }};
        aluminium_wall_large = new Wall("aluminium-wall-large"){{
            requirements(Category.defense,with(MD_Items.aluminium,24));
            scaledHealth = 550f;
            armor = 1;
            size = 2;
            researchCost = with( MD_Items.aluminium, 80);

        }};

        al_alloy_wall = new Wall("al-alloy-wall"){{
            requirements(Category.defense,with(MD_Items.al_alloy,6));
            scaledHealth = 750f;
            armor = 8;
            researchCost = with( MD_Items.al_alloy, 200);

        }};
        al_alloy_wall_large = new Wall("al-alloy-wall-large"){{
            requirements(Category.defense,with(MD_Items.al_alloy,24));
            scaledHealth = 750f;
            armor = 8;
            size = 2;
            researchCost = with( MD_Items.al_alloy, 1000);

        }};
        //endregion
    }
}
