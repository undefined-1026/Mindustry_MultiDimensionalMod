package mDimension.content.building;
import mDimension.content.MD_Items;
import mDimension.world.blocks.*;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.OverflowGate;
import mindustry.world.meta.BuildVisibility;

import static mDimension.content.building.MD_blocks.*;
import static mindustry.type.ItemStack.with;

public class MD_Distributions {
    public static void load(){
        //region distribution
        light_duct = new Duct("light-duct") {{
            requirements(Category.distribution, with(MD_Items.aluminium, 1));
            speed = 4f;
            health = 180;
            armor = 1;
            buildTime = 0.2f/60;
            bridgeReplacement = light_duct_bridge;
            junctionReplacement = light_junction;
            alwaysUnlocked = true;
            researchCost = ItemStack.with(MD_Items.aluminium, 5);
            fullOverride = this.name + "-private";
        }
            @Override
            public void init() {
                super.init();
                bridgeReplacement = light_duct_bridge;
                junctionReplacement = light_junction;
            }
        };

        armored_light_duct = new Duct("armored-light-duct") {{
            requirements(Category.distribution, with(MD_Items.germanium, 1, MD_Items.aluminium, 1, MD_Items.al_alloy,1));
            speed = 4f;
            health = 300;
            armor = 3;
            armored = true;
            researchCost = with(MD_Items.germanium, 100, MD_Items.aluminium, 100, MD_Items.al_alloy,100);
            bridgeReplacement = light_duct_bridge;
            junctionReplacement = light_junction;
            buildCostMultiplier = 0.6f;
            fullOverride = this.name + "-private";
        }

            @Override
            public void init() {
                super.init();
                bridgeReplacement = light_duct_bridge;
                junctionReplacement = light_junction;
            }
        };
        light_junction = new Junction("light-junction"){{
            requirements(Category.distribution, with(MD_Items.aluminium, 3));
            researchCost = with();
            health = 150;
            speed = 22;
            displayedSpeed = 16f;
        }};
        light_duct_bridge = new RadiusItemBridge("al-alloy-duct-bridge") {{
            requirements(Category.distribution, with(Items.silicon, 10, MD_Items.germanium, 10));
            arrowSpacing = 6f;
            bridgeWidth = 8;
            arrowTimeScl = 15;
            buildTime = 0.2f*60f;
            health = 200;
            armor = 2;
            range = 6;
            hasPower = false;
            transportTime = 4f;
            pulse = true;
            buildCostMultiplier = 3f;
            researchCostMultiplier = 0.3f;
            squareSprite = false;
        }};
        shunt_router = new ShuntDuctRouter("shunt-router"){{
            requirements(Category.distribution, with(MD_Items.aluminium, 6));
            squareSprite = false;
            researchCost = with( MD_Items.aluminium, 10);
            health = 150;
            speed = 4f;
            regionRotated1 = 1;
            solid = false;
        }};
        light_sorter = new MD_Sorter("light-sorter"){{
            requirements(Category.distribution, with(MD_Items.aluminium, 4));
            squareSprite = false;
            researchCost = with();
            health = 120;
        }};

        light_invertedSorter = new MD_Sorter("light-inverted-sorter"){{
            requirements(Category.distribution, with(MD_Items.aluminium, 4));
            squareSprite = false;
            buildCostMultiplier = 3f;
            invert = true;
            researchCost = with( );
            health = 120;
        }};
        light_overflowGate = new OverflowGate("light-overflow-gate"){{
            requirements(Category.distribution, with(MD_Items.aluminium, 4));
            squareSprite = false;
            buildCostMultiplier = 3f;
            researchCost = with( );
            health = 120;
        }};

        light_underflowGate = new OverflowGate("light-underflow-gate"){{
            requirements(Category.distribution, with(MD_Items.aluminium, 4));
            squareSprite = false;
            buildCostMultiplier = 3f;
            invert = true;
            researchCost = with( );
            health = 120;
        }};

        multiway_unloader = new MD_MultiwayUnloader("multiway-unloader") {{
            requirements(Category.distribution, ItemStack.with(
                    Items.silicon, 20,
                    MD_Items.al_alloy, 20,
                    MD_Items.germanium, 30
            ));
            squareSprite = false;
            size = 1;
            health = 100;
            speed = 3f;
            solid = false;
            underBullets = true;
            regionRotated1 = 1;

        }};

        stack_rail_conveyor = new MulitStackConveyor("stack-rail-conveyor") {{
            requirements(Category.distribution, with(MD_Items.aluminium, 1, MD_Items.al_alloy, 1, Items.silicon, 1));
            health = 150;
            armor = 3;
            itemCapacity = 15;
            speed = 6 / 60f;
        }};

        moving_node = new ActiveTransferBlock("moving-node"){{
            requirements(Category.distribution,new BuildVisibility(()->
                    !Vars.state.rules.onlyDepositCore
            ), with(MD_Items.aluminium, 50, MD_Items.al_alloy, 30, Items.silicon, 50,MD_Items.polymer,50));
            squareSprite = false;
            health = 200;
            size = 2;
            itemCapacity = 50;
            consumePower(300f/60);
            haloColor = c("E3E9FF");
        }};
        //endregion
    }
}
