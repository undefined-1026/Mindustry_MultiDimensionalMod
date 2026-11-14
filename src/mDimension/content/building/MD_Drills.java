package mDimension.content.building;
import mDimension.consumers.ConsumeBeamBoost;
import mDimension.content.MD_Fx;
import mDimension.content.MD_Items;
import mDimension.content.MD_Liquids;
import mDimension.content.MD_beams;
import mDimension.draw.DrawRotation;
import mDimension.meta.MD_Attribute;
import mDimension.world.blocks.drill.CrustDrill;
import mDimension.world.blocks.drill.CrustDrillBooster;
import mDimension.world.blocks.drill.MD_BurstDrill;
import mDimension.world.blocks.farming.HarvestingBlock;
import mDimension.world.blocks.farming.PlanterBlock;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mDimension.content.building.MD_blocks.*;
import static mindustry.type.ItemStack.with;

public class MD_Drills {
    public static void load(){
        // region drill
        deep_water_extractor = new SolidPump("deep-water-extractor") {{
            requirements(Category.production, with(Items.silicon, 40, MD_Items.aluminium, 30, MD_Items.polymer, 30));
            result = Liquids.water;
            pumpAmount = 10.2f / 60f;
            size = 2;
            liquidCapacity = 150;
            rotateSpeed = 1.1f;
            attribute = Attribute.water;
            envRequired |= Env.groundWater;
//            drawer = new DrawMulti(
//                    new DrawRegion(),
//                    new DrawRegion("-rotator",1.1f,true),
//                    new DrawRegion("-top")
//            );


            consumePower(1.5f);
        }};
        beam_bore = new BeamDrill("beam-bore") {
            {
                requirements(Category.production, with(MD_Items.aluminium, 30, Items.silicon, 20));
                consumeLiquid(MD_Liquids.ammonia,0.5f/60f);
                drillTime = 150;
                tier = 3;
                size = 2;
                optionalBoostIntensity = 2.2f;
                range = 7;
                fogRadius = 3;
                researchCost = with(MD_Items.aluminium, 10, Items.silicon, 10);
                heatColor = c("E6C845");
                boostHeatColor = c("F26B4D");

                consume(new ConsumeBeamBoost(5, MD_beams.near_infrared_light,optionalBoostIntensity).boost());
            }
        };
        small_cliff_crusher = new WallCrafter("small-cliff-crusher"){{
            requirements(Category.production, with(Items.graphite, 15, MD_Items.aluminium,10));
            consumePower(8 / 60f);

            drillTime = 120f;
            size = 1;
            attribute = Attribute.sand;
            output = Items.sand;
            fogRadius = 2;
            researchCost = with(MD_Items.aluminium, 35, Items.graphite, 20);
            ambientSound = Sounds.loopDrill;
            ambientSoundVolume = 0.015f;
        }};
        small_impact_drill = new MD_BurstDrill("small-impact-drill") {{
            requirements(Category.production, with(Items.graphite, 18, MD_Items.germanium, 18));
            drillTime = 60f * 12f;
            dominantItemsMulti = 2f;
            drillMultipliers.put(MD_Items.aluminium, 1.2f);
            drillMultipliers.put(Items.beryllium, 1.5f);
            drillMultipliers.put(Items.graphite, 1.5f);
            drillMultipliers.put(MD_Items.germanium, 1.2f);
            size = 2;
            tier = 3;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, MD_Fx.mineImpactWave.wrap(c("FFEADE"), 15f));
            shake = 1f;
            itemCapacity = 30;

            arrows = 1;
            arrowOffset = 0;
            arrowSpacing = 3f;

            researchCost = with();
            liquidBoostIntensity = 2;

            fogRadius = 4;
            consumeLiquid(Liquids.nitrogen, 1.5f / 60f).boost();
        }};
        electric_impact_drill = new MD_BurstDrill("electric-impact-drill"){{
            requirements(Category.production, with(Items.silicon,50,MD_Items.germanium,80,MD_Items.al_alloy,30));
            drillTime = 60f * 4f;
            dominantItemsMulti = 1f;
            drillMultipliers.put(MD_Items.aluminium, 1.2f);
            drillMultipliers.put(Items.beryllium, 1.5f);
            drillMultipliers.put(Items.graphite, 1.5f);
            drillMultipliers.put(MD_Items.germanium, 1.2f);
            size = 3;
            hasPower = true;
            tier = 5;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, MD_Fx.mineImpactWave.wrap(c("D9DCFF"), 22f));
            shake = 1.2f;
            itemCapacity = 30;

            arrows = 1;
            arrowOffset = 0;
            arrowSpacing = 3f;

            liquidBoostIntensity = 2;

            fogRadius = 4;
            consumePower(1.5f);
            consumeLiquid(Liquids.nitrogen, 1f / 60f).boost();
        }};
        ammonia_collector = new AttributeCrafter("ammonia-collector") {{
            requirements(Category.production, with(Items.silicon, 30, MD_Items.aluminium, 40));
            attribute = MD_Attribute.ammonia;
            group = BlockGroup.liquids;
            minEfficiency = 9f - 0.0001f;
            baseEfficiency = 0f;
            displayEfficiency = false;

            craftEffect = Fx.turbinegenerate;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawBlurSpin("-rotator", 6f), new DrawRegion("-mid"), new DrawLiquidTile(MD_Liquids.ammonia, 36f / 4f), new DrawDefault());
            craftTime = 120f;
            size = 3;
            ambientSound = Sounds.loopHum;
            ambientSoundVolume = 0.06f;
            hasLiquids = true;
            hasItems = false;
            boostScale = 1f / 9f;
            itemCapacity = 0;
            outputLiquid = new LiquidStack(MD_Liquids.ammonia, 48f / 60f);
            consumePower(0.5f);
            liquidCapacity = 180;
        }};
        crustal_drill = new CrustDrill("crustal-drill"){{
            requirements(Category.production,with(Items.silicon,120,MD_Items.germanium,200,Items.graphite,150, MD_Items.al_alloy,150));
            drawer = new DrawMulti(
                    new DrawRegion("-rotate",120/60f,true),
                    new DrawRegion(),
                    new DrawRegion("-bit",120/60f,true),
                    new DrawRegion("-top"),
                    new DrawRegion("-cap",120/60f,true),
                    new DrawGlowRegion(){{alpha = 0.6f;}}
            );
            consumePower(300/60f);
        }};
        drilling_casing_module = new CrustDrillBooster("drilling-casing-module"){{
            requirements(Category.production,with());
            consumeLiquid(Liquids.nitrogen,3/60f);
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.nitrogen,4f),
                    new DrawRegion(),
                    new DrawRegion("-top")
            );
        }};

        planter = new PlanterBlock("planter"){{
            requirements(Category.production,with(Items.silicon,120,Items.graphite,150,MD_Items.polymer,100,MD_Items.aluminium,150));
            size = 3;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion(),
                    new DrawRegion("-top"),
                    new DrawRotation("-rotate",true)
            );
            consumeLiquid(Liquids.water,15/60f);
        }};
        harvestingArm = new HarvestingBlock("harvesting-arm"){{
            requirements(Category.production,with(Items.silicon,40,MD_Items.al_alloy,20,MD_Items.polymer,80));
            size = 1;

        }};
        //endregion
    }
}
