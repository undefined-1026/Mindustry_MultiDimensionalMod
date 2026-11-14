package mDimension.content.building;

import mDimension.content.MD_Items;
import mDimension.draw.DrawRotation;
import mDimension.world.blocks.BlendLiquidJunction;
import mDimension.world.blocks.DirectionalLiquidRouter;
import mDimension.world.blocks.LiquidUnloader;
import mDimension.world.blocks.RadiusLiquidBridge;
import mDimension.world.blocks.drill.DirectionalPump;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawPumpLiquid;
import mindustry.world.draw.DrawRegion;

import static mindustry.type.ItemStack.with;
import static mDimension.content.building.MD_blocks.*;
public class MD_LiquidBuilds {
    public static void load(){
        //region liquid
        siphon_pump = new DirectionalPump("siphon-pump"){{
            requirements(Category.liquid,with(Items.silicon,30, MD_Items.aluminium,50));
            pumpAmount = 15/60f;
            liquidCapacity = 120f;
            size = 2;
            consumePower(80/60f);
            researchCost = with(Items.silicon,300, MD_Items.aluminium,500);
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawPumpLiquid(),
                    new DrawRegion(),
                    new DrawRegion("-rotate",1.5f),
                    new DrawRegion("-top"),
                    new DrawRotation("-rotation",true)
            );
        }};
        fluid_unloader = new LiquidUnloader("fluid-unloader"){{
            requirements(Category.liquid,with(Items.silicon,10, MD_Items.polymer, 5, MD_Items.al_alloy,5));
            connectedPower = true;
            rotate = true;
            rotateDraw = false;
            health = 150;
            speed = 2;
            regionRotated1 = 1;
            size = 1;
            conductivePower = true;
            fullOverride = this.name + "-private";
        }};
        fluid_conduit_bridge = new RadiusLiquidBridge("fluid-conduit-bridge"){{
            requirements(Category.liquid, with(Items.silicon,10, MD_Items.germanium, 10));
            arrowSpacing = 6f;
            bridgeWidth = 8;
            arrowTimeScl = 15;
            health = 200;
            armor = 2;
            range = 6;
            pulse = true;
            hasPower = false;
            liquidCapacity = 200f;
            buildCostMultiplier = 3f;
            researchCostMultiplier = 0.3f;
            squareSprite = false;
        }};
        directional_fluid_router = new DirectionalLiquidRouter("directional-fluid-router"){{
            requirements(Category.liquid, with(MD_Items.germanium, 10, MD_Items.aluminium,8,Items.silicon,5));
            health = 250;
            liquidCapacity = 150f;
            squareSprite = false;
        }};
        fluid_junction = new BlendLiquidJunction("fluid-junction"){{
            requirements(Category.liquid, with(MD_Items.aluminium,5,Items.silicon,3));
            health = 200;
            solid = false;
        }};

        fluid_conduit = new Conduit("fluid-conduit"){{
            requirements(Category.liquid, with(MD_Items.germanium, 1, MD_Items.aluminium,1));
            liquidCapacity = 60f;
            health = 100;
            botColor = c("45413B");
            explosivenessScale = flammabilityScale = 12f/60f;
            junctionReplacement = fluid_junction;
            bridgeReplacement = fluid_conduit_bridge;
        }};

        fluid_container = new LiquidRouter("fluid-container"){{
            requirements(Category.liquid, with(Items.graphite,20,MD_Items.germanium,20));
            liquidCapacity = 700f;
            size = 2;
            solid = true;

        }};
        //endregion
    }

}
