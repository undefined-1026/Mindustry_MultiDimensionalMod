package mDimension.content;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mDimension.consumers.ConsumeBeamBoost;
import mDimension.consumers.ConsumeFlux;
import mDimension.consumers.ConsumeBeam;
import mDimension.consumers.MultiRecipeConsume;
import mDimension.draw.DrawAutotile;
import mDimension.draw.DrawJetFlame;
import mDimension.draw.DrawPiston;
import mDimension.draw.DrawRotation;
import mDimension.entity.EntityShield;
import mDimension.entity.MultiSound;
import mDimension.entity.bullet.*;
import mDimension.entity.pattern.ShootBarrelRandom;
import mDimension.entity.pattern.ShootSwing;
import mDimension.world.blocks.*;
import mDimension.world.blocks.drill.MD_BurstDrill;
import mDimension.world.blocks.drill.CrustDrill;
import mDimension.world.blocks.drill.CrustDrillBooster;
import mDimension.world.blocks.drill.DirectionalPump;
import mDimension.world.blocks.farming.HarvestingBlock;
import mDimension.world.blocks.farming.PlanterBlock;
import mDimension.world.blocks.flux.FluxCrafter;
import mDimension.world.blocks.flux.FluxNode;
import mDimension.world.blocks.payload.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.*;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.OverflowGate;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.world;
import static mindustry.type.ItemStack.with;
import mDimension.meta.*;

public class MD_blocks {
    public static final String modname = "mdimension-";
    //region defined
    public static Block
            small_silicon_arc_furnace, aluminium_electrolysis_cell, al_alloy_smelting, infrared_laser, ultraviolet_laser, nihility_exciter, ngm_launch_pad,
            ti_alloy_smelting, helium_factory, test2, diagonal_beam_merging_prism,
            water_pyrolyzer, carbon_fibre_binder, heavy_pulverizer, polymer_compressor, phase_adder, ammonia_chamber,
    //distribution
    beam_merging_prism, light_junction,
            multiway_unloader, light_duct_bridge, shunt_router,
            light_sorter, light_invertedSorter, light_overflowGate, light_underflowGate, light_duct, armored_light_duct, stack_rail_conveyor,

            moving_node,
    //liquid
    siphon_pump,  fluid_unloader,fluid_conduit_bridge,directional_fluid_router,fluid_junction,fluid_conduit,fluid_container,
    deep_water_extractor,
    //produce
            beam_bore,small_impact_drill,ammonia_collector,crustal_drill,  drilling_casing_module,

            planter,harvestingArm,
    //ammo
    heavy_ammo,
    //turret
    ionize,crack,ejection, fracture,break_water,dawn,fluffrain,polarization,crest,test4,test5,
    //wall
    aluminium_wall,aluminium_wall_large,al_alloy_wall,al_alloy_wall_large,
    //core
    coreSteady,proof_container,stack,
    //power
    internal_energy_pile,magnetic_node,graphite_combustion_chamber,composite_combustion,
    //payload

    small_payload_conveyor,
            small_payload_router,payload_processing_platform,
            test3,ammo_constructor,
    //unit
    infantry_factory,airborne_vessels_factory,

    shaping_assembler,
    eigen_unit_assembler,
            forging_assembler,
            tempering_assembler,
            polarization_assembler,

    anchor_radar,
            army_anchor_point_reconstructor,
            airforce_anchor_point_reconstructor
                    ;
    //endregion
    public static void load() {
        loadAmmo();
        //region craft
        small_silicon_arc_furnace = new GenericCrafter("small-silicon-arc-furnace") {{
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.aluminium, 30,
                    Items.graphite, 20
            ));
            researchCostMultiplier = 0.05f;
            itemCapacity = 20;

            consumeItem(Items.sand, 3);
            outputItem = new ItemStack(Items.silicon, 2);
            craftTime = 60f;
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.12f;
            consumePower(2.5f);
            size = 2;
            hasPower = true;
            hasLiquids = false;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawArcSmelt() {{
                        flameRad *= 0.7f;
                        circleSpace *= 0.7f;
                        flameRadiusScl *= 0.7f;
                        flameRadiusMag *= 0.7f;
                        circleStroke *= 0.7f;

                    }},
                    new DrawRegion()
            );
        }};
        //region al_alloy_smelting 铝合金
        al_alloy_smelting = new GenericCrafter("al-alloy-smelting") {{
            squareSprite = false;
            health = 500;
            armor = 3;
            size = 3;
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.aluminium, 80,
                    Items.copper, 80,
                    Items.silicon, 60
            ));
            craftEffect = new MultiEffect(
                    MD_Fx.craftEffectLight(40, 2.5f, 4f, Color.valueOf("D1F8FF"), 6, 1f),
                    MD_Fx.craftEffectLight(25, 1f, 3f, Color.valueOf("D1F8FF"), 6, 5f)
            );
            outputItem = new ItemStack(MD_Items.al_alloy, 4);
            consumeItems(ItemStack.with(
                    MD_Items.aluminium, 6,
                    Items.copper, 3
            ));
            consumePower(4f);
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("D1E4FF")) {{
                flameRadiusScl = 8f;
                flameRadiusInMag = 0.7f;
            }},
                    new DrawGlowRegion() {{
                        color = MD_Items.al_alloy.color;
                    }});
            craftTime = 90f;
            hasItems = true;
            hasPower = true;


        }};
        //endregion
        //region ti_alloy_smelting 钛合金
        ti_alloy_smelting = new GenericCrafter("ti-alloy-smelting") {{
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.al_alloy, 100,
                    Items.phaseFabric, 45,
                    MD_Items.aluminium, 150,
                    Items.silicon, 120
            ));
            health = 500;
            armor = 3;
            size = 4;
            buildTime = 6f;
            consume(new ConsumeBeam(6, MD_beams.ultraviolet_light));
            consume(new ConsumeBeam(30, MD_beams.near_infrared_light));
            craftTime = 80f;
            itemCapacity = 30;
            consumeItems(ItemStack.with(Items.silicon, 3, Items.titanium, 12));
            consumeLiquid(MD_Liquids.helium, 1.45f / 60f);
            outputItem = new ItemStack(MD_Items.ti_alloy, 4);
            hasItems = true;
            hasPower = true;
            craftEffect = Fx.pulverizeMedium;
            consumePower(4f);
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(MD_Liquids.helium),
                    new DrawCrucibleFlame() {{
                        flameRad = 5f;
                        particleLife = 110f;
                        particleRad = 12f;
                        particleSize = 5;
                    }},
                    new DrawRegion(),
                    new DrawRegion("-top"),
                    new DrawGlowRegion() {{
                        color = Color.valueOf("FFF4DB");
                        alpha = 0.7f;
                    }}
            );

        }};
        //endregion
        //region helium_factory 氦气
        helium_factory = new GenericCrafter("helium-factory") {{
            squareSprite = false;
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.aluminium, 60,
                    Items.silicon, 40,
                    Items.lead, 70,
                    Items.copper, 40
            ));
            health = 500;
            armor = 3;
            buildTime = 3f;
            consumeLiquid(Liquids.hydrogen, 6 / 60f);
            consumeItem(Items.phaseFabric, 1);
            craftTime = 240f;
            size = 2;

            outputLiquid = new LiquidStack(MD_Liquids.helium, 1.5f / 60f);
            hasItems = true;
            hasPower = true;
            craftEffect = MD_Fx.polyWave(
                    4, 10.31f, 0, 5, 100f, new Color(0xffc0ffff), 0.7f
            );

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.hydrogen, 2f),
                    new DrawLiquidTile(MD_Liquids.helium, 2f),
                    new DrawRegion(),
                    new DrawRegion("-top")

            );

            consumePower(1f);
        }};
        //endregion
        //region aluminium_electrolysis_cell 铝
        aluminium_electrolysis_cell = new GenericCrafter("aluminium-electrolysis-cell") {{
            squareSprite = false;
            requirements(Category.crafting, ItemStack.with(
                    Items.copper, 60,
                    Items.lead, 50,
                    Items.silicon, 30
            ));
            health = 500;
            armor = 3;
            buildTime = 1f;
            size = 2;
            itemCapacity = 20;
            consumeItem(MD_Items.bauxite, 4);
            outputItem = new ItemStack(MD_Items.aluminium, 3);
            consumePower(2f);
            craftEffect = MD_Fx.craftEffectLight(40, 2.5f, 3, Color.valueOf("FFAC99"), 5, 1f);
            craftTime = 120f;

            drawer = new DrawMulti(

                    new DrawRegion("-bottom"),
                    new DrawRegion(),
                    new DrawFlame(Color.valueOf("FFD3BD")) {{
                        flameRadius = 2.5f;
                        flameRadiusIn = 1.2f;
                        flameRadiusMag = 0.5f;
                        flameRadiusInMag = 0.33f;
                    }}
            );
        }};
        //endregion
        //region test2 测试-激光消费者
        test2 = new GenericCrafter("test2") {{
            requirements(Category.crafting,BuildVisibility.sandboxOnly, with());
            size = 3;
            consumePower(1f);
            consume(new ConsumeBeam(20f, MD_beams.ultraviolet_light));
            outputItem = new ItemStack(Items.silicon, 1);
            craftTime = 15f;
            hasPower = true;
            hasItems = true;
            alwaysUnlocked = true;
            drawDisabled = true;
        }};
        //endregion
        //region beam_merging_prism 激光棱镜
        beam_merging_prism = new MD_BeamDeflector("beam-merging-prism") {{
            requirements(Category.crafting, with());
            size = 1;
            drawArrow = true;
            drawer = new DrawMulti(
                    new DrawRegion(),
                    new DrawRotation("-arrow", 4)
            );
        }};

        diagonal_beam_merging_prism = new MD_BeamDeflector("diagonal-beam-merging-prism") {{
            requirements(Category.crafting, with());
            afterRotation.set(1, 1);
            size = 1;
            diagonalFlip = true;
            drawArrow = true;
            drawer = new DrawMulti(
                    new DrawRegion(),
                    new DrawRotation("-arrow", 4)
            );
        }};
        //endregion
        ammonia_chamber = new GenericCrafter("ammonia-chamber") {{
            requirements(Category.crafting, with(Items.silicon, 30, MD_Items.aluminium, 50, Items.copper, 50));
            consumeLiquid(MD_Liquids.ammonia, 24 / 60f);
            outputLiquids = LiquidStack.with(Liquids.hydrogen, 24 / 60f, Liquids.nitrogen, 12 / 60f);
            liquidOutputDirections = new int[]{1, 3};
            size = 3;
            rotate = true;
            rotateDraw = false;

            invertFlip = true;
            group = BlockGroup.liquids;

            consumePower(0.5f);
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(MD_Liquids.ammonia, 3f),
                    new DrawCrucibleFlame(){{

                    }},
                    new DrawRegion(),
                    new DrawLiquidOutputs(),
                    new DrawGlowRegion() {{
                        alpha = 0.7f;
                        color = Color.valueOf("EFBDF3");
                        glowIntensity = 0.3f;
                        glowScale = 6f;
                    }}
            );
        }};
        //region polymer_compressor 聚合物压缩机
        polymer_compressor = new GenericCrafter("polymer-compressor") {{
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.aluminium, 70,
                    MD_Items.al_alloy, 40,
                    Items.silicon,50
            ));
            squareSprite = false;
            size = 3;
            consumeLiquids(LiquidStack.with(MD_Liquids.crystallization_oil, 15 / 60f, Liquids.hydrogen, 6f / 60f));
            consumePower(3f);
            outputItem = new ItemStack(MD_Items.polymer, 1);
            craftTime = 60f;
            craftEffect = MD_Fx.craftEffect(60f, 4f, MD_Items.polymer.color, 6, new float[]{4, 4, -4, 4, -4, -4, 4, -4});
            fullOverride = this.name + "-full";
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.hydrogen, 4f),
                    new DrawLiquidTile(Liquids.oil, 6.25f),
                    new DrawRegion(),
                    new DrawPiston("-blade") {{
                        strokeFunction = (b, i) -> {
                            return (float) ((Math.cos((b.totalProgress() % 150) / 150f * 2 * Math.PI) - 1) / 2);
                        };
                    }},
                    new DrawRegion("-top")
            );
        }};
        //endregion
        //region ngm_launch_pad 开采平台
        ngm_launch_pad = new MD_LaunchPadCrafter("ngm-launch-pad") {{
            requirements(Category.crafting, with(MD_Items.ti_alloy, 350, MD_Items.al_alloy, 500, Items.silicon, 600, Items.graphite, 700));
            size = 10;
            hasItems = true;
            hasLiquids = true;
            itemCapacity = 500;
            liquidCapacity = 9000;
            health = 8000;
            squareSprite = false;
            craftTime = 25 * 60f;
            baseHoverTime = 55 * 60f;
            launchTime = 6.6f * 60f;
            landTime = 3.4f * 60f;
            launchEffect = MD_Fx.loadLaunch(launchTime, this.name + "-pod", 17f * 8f, 0f, 1f, 1f);
            landEffect = MD_Fx.loadLand(landTime, this.name + "-returnpod", 17 * 8f, 0, 1, 1.5f, loadStayTime);
            consumePower(5f);

            consumeItems(ItemStack.with(
                    MD_Items.ti_alloy, 150,
                    MD_Items.al_alloy, 250,
                    MD_Items.polymer, 200
            ));
            consumeLiquids(LiquidStack.with(Liquids.hydrogen, 100f / 60f, Liquids.ozone, 1f));
            outputLiquid = new LiquidStack(MD_Liquids.dimension_fluid, 4500);
            drawer = new DrawMulti(new DrawLiquidTile(MD_Liquids.dimension_fluid, 5f), new DrawRegion());
        }};
        //endregion
        //region infrared_laser 激光发生器
        infrared_laser = new LaserCrafter("infrared-laser") {{
            requirements(Category.crafting, with());
            craftPos = new Vec2[]{
                    new Vec2(4, -4),
                    new Vec2(-4, 4)
            };
            craftRotation = new Vec2[]{
                    new Vec2(1, 0),
                    new Vec2(0, 1)
            };
            diagonalFilp = true;
            consumePower(2f);
            beam = MD_beams.near_infrared_light;
            size = 2;
            beamPower = 10f;
            rotateDraw = false;
            drawArrow = true;
            drawer = new DrawMulti(new DrawRegion(), new DrawRotation("-top", 4));
        }};
        ultraviolet_laser = new LaserCrafter("ultraviolet-laser") {{
            requirements(Category.crafting, with());
            craftPos = new Vec2[]{
                    new Vec2(4, -4),
                    new Vec2(-4, 4)
            };
            craftRotation = new Vec2[]{
                    new Vec2(1, 1),
                    new Vec2(1, 1)
            };
            consumePower(3f);
            diagonalFilp = true;
            beam = MD_beams.ultraviolet_light;
            size = 2;
            beamPower = 6f;
            rotateDraw = false;
            drawArrow = true;
            drawer = new DrawMulti(new DrawRegion(), new DrawRotation("-top", 4));
        }};
        nihility_exciter = new LaserCrafter("nihility-exciter") {{
            requirements(Category.crafting, with());
            craftPos = new Vec2[]{
                    new Vec2(-4, 4),
                    new Vec2(4, 4),
                    new Vec2(-4, -4),
                    new Vec2(4, -4)
            };
            craftRotation = new Vec2[]{
                    new Vec2(0, 1),
                    new Vec2(0, 1),
                    new Vec2(0, -1),
                    new Vec2(0, -1)
            };
            beam = MD_beams.nihility_light;
            beamPower = 40f;
            size = 2;
            rotateDraw = false;
            drawArrow = true;
            consumePower(5f);
            consumeLiquid(MD_Liquids.dimension_fluid, 6 / 60f);

        }};
        //endregion
        carbon_fibre_binder = new GenericCrafter("carbon-fibre-binder") {{
            requirements(Category.crafting, with());
            size = 3;
            consume(new ConsumeBeam(40, MD_beams.near_infrared_light));
            consumePower(5f);
            consumeItems(
                    with(MD_Items.polymer, 5)
            );
            consumeLiquid(MD_Liquids.helium, 1.5f / 60f);
            craftTime = 120f;
            outputItem = new ItemStack(MD_Items.carbon_fibre, 2);
            craftEffect = new MultiEffect(
                    MD_Fx.brokenWave(27f, Color.valueOf("E0EAFF").a(0.85f), 5f, 4f, 1.6f, 6, 0.3f, 1.3f),
                    MD_Fx.brokenWave(27f, Color.valueOf("C9DBFF").a(0.85f), 7.5f, 6f, 1.2f, 8, 0.5f, 1.4f)

            );


            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawMultiWeave() {{
                        rotateSpeed = 1.6f;
                        rotateSpeed2 = -1.3f;
                        glowColor = new Color(0.5f, 0.7f, 0.8f, 0.6f);
                    }},
                    new DrawRegion(),
                    new DrawRegion("-top2", 1.8f, true) {{
                        rotation = 45f;
                    }},
                    new DrawRegion("-top1", -1.6f, true),
                    new DrawRegion("-top")
            );
        }};
        water_pyrolyzer = new GenericCrafter("water-pyrolyzer") {{
            requirements(Category.crafting, with(Items.silicon, 70, MD_Items.al_alloy, 40, MD_Items.polymer, 80, MD_Items.aluminium, 120));
            size = 2;
            craftTime = 10f;
            rotate = true;
            invertFlip = true;
            group = BlockGroup.liquids;
            itemCapacity = 0;

            liquidCapacity = 100;

            consumeLiquid(Liquids.water, 20 / 60f);
            consumePower(1f);
            consume(new ConsumeBeam(10, MD_beams.near_infrared_light));
            warmupSpeed = 0.003f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.water, 2f),
                    new DrawBubbles(Color.valueOf("7693e3")) {{
                        sides = 10;
                        recurrence = 3f;
                        spread = 6;
                        radius = 1.5f;
                        amount = 20;
                    }},
                    new DrawRegion(),
                    new DrawLiquidOutputs(),
                    new DrawGlowRegion() {{
                        alpha = 0.7f;
                        color = Color.valueOf("c4bdf3");
                        glowIntensity = 0.3f;
                        glowScale = 6f;
                    }}
            );

            ambientSound = Sounds.loopElectricHum;
            ambientSoundVolume = 0.08f;

            regionRotated1 = 3;
            outputLiquids = LiquidStack.with(Liquids.ozone, 8f / 60, Liquids.hydrogen, 12f / 60);
            liquidOutputDirections = new int[]{1, 3};
        }};
        test5 = new MultiRecipeCrafter("test5") {{
            drawDisabled = true;
            requirements(Category.crafting,BuildVisibility.sandboxOnly, with());
            consumeRecipes(new MultiRecipeConsume(
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.sand, 2, Items.lead, 2);
                        consumeLiquids = LiquidStack.with(Liquids.water, 12 / 60f);
                        outputItems = with(Items.metaglass, 3);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.sand, 2);
                        outputItems = with(Items.silicon, 1);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeLiquids = LiquidStack.with(MD_Liquids.crystallization_oil, 12 / 60f);
                        outputItems = with(MD_Items.polymer, 1);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(MD_Items.polymorphic_crystal, 2);
                        outputLiquids = LiquidStack.with(MD_Liquids.dimension_fluid, 12 / 60f);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeLiquids = LiquidStack.with(Liquids.arkycite, 12 / 60f);
                        outputLiquids = LiquidStack.with(MD_Liquids.helium, 12 / 60f);
                    }}

            ));

            autoResetEnabled = true;
            craftTime = 20f;
            size = 3;
            consumePower(2f);

        }};
        heavy_pulverizer = new MultiRecipeCrafter("heavy-pulverizer") {{
            requirements(Category.crafting, with(MD_Items.aluminium, 40, Items.graphite, 50));
            craftTime = 60f;
            autoResetEnabled = true;
            itemCapacity = 30;
            consumePower(3f);
            size = 2;
            researchCost = with(MD_Items.aluminium, 30, Items.graphite, 30);
            craftEffect = MD_Fx.craftEffect(60f, 3f, MD_Items.polymer.color, 8, new float[]{3, 3, -3, 3, -3, -3, 3, -3});
            consumeRecipes(new MultiRecipeConsume(
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.scrap, 6);
                        outputItems = with(Items.sand, 15);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.thorium, 5);
                        outputItems = with(Items.sand, 9);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.tungsten, 5);
                        outputItems = with(Items.sand, 9);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.titanium, 6);
                        outputItems = with(Items.sand, 9);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.beryllium, 6);
                        outputItems = with(Items.sand, 9);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(MD_Items.aluminium, 6);
                        outputItems = with(Items.sand, 9);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.copper, 8);
                        outputItems = with(Items.sand, 9);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.lead, 8);
                        outputItems = with(Items.sand, 9);
                    }}
            ));

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion("-rotator", 200 / 60f, true),
                    new DrawRegion(),
                    new DrawGlowRegion("-glow") {{
                        color = Color.valueOf("FFEF96");
                        alpha = 0.6f;
                    }}
            );
        }};
        phase_adder = new GenericCrafter("phase-adder") {{
            requirements(Category.crafting, with(Items.silicon, 80, MD_Items.polymer, 50, MD_Items.al_alloy, 70, MD_Items.ti_alloy, 40));
            craftTime = 180f;
            size = 2;
            itemCapacity = 20;
            consumeItems(with(MD_Items.light_ceramic, 4));
            consumeLiquid(MD_Liquids.dimension_fluid, 10 / 60f);
            consumePower(300 / 60f);
            outputItem = new ItemStack(MD_Items.polymorphic_crystal, 2);

            craftEffect = new MultiEffect(
                    MD_Fx.craftEffectLight(70f, 4.5f, 5f, Color.valueOf("FFEBA3"), 7, 1f),
                    MD_Fx.craftEffectLight(55f, 2f, 7f, Color.valueOf("FFEBA3"), 4, 6f)
            );

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(MD_Liquids.dimension_fluid, 5 / 4f),
                    new DrawRegion(),
                    new DrawRegion("-mid"),
                    new DrawCrucibleFlame() {
                        {
                            flameColor = Color.valueOf("F5D37C");
                            midColor = Color.valueOf("F2AD85");
                            alpha = 0.38f;
                            particles = 20;
                            particleSize = 2.8f;
                        }

                        @Override
                        public void draw(Building build) {

                            if (build.warmup() > 0f && flameColor.a > 0.001f) {
                                Lines.stroke(circleStroke * build.warmup());

                                float si = Mathf.absin(flameRadiusScl, flameRadiusMag);
                                float a = alpha * build.warmup();
                                Draw.blend(Blending.additive);

                                Draw.color(midColor, a);
                                Fill.poly(build.x + x, build.y + y, 4, flameRad + si);

                                Draw.color(flameColor, a);
                                Lines.poly(build.x + x, build.y + y, 4, (flameRad + circleSpace + si) * build.warmup());

                                float base = (Time.time / particleLife);
                                rand.setSeed(build.id);
                                for (int i = 0; i < particles; i++) {
                                    float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
                                    float angle = rand.random(360f) + (Time.time / rotateScl) % 360f;
                                    float len = particleRad * particleInterp.apply(fout);
                                    Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)));
                                    Fill.poly(
                                            build.x + Angles.trnsx(angle, len) + x,
                                            build.y + Angles.trnsy(angle, len) + y,
                                            4,
                                            particleSize * fin * build.warmup()
                                    );
                                }

                                Draw.blend();
                                Draw.reset();
                            }
                        }
                    },
                    new DrawRegion("-top")
            );
        }};
        //endregion
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
                heatColor = Color.valueOf("E6C845");
                boostHeatColor = Color.valueOf("F26B4D");

                consume(new ConsumeBeamBoost(5, MD_beams.near_infrared_light,optionalBoostIntensity).boost());
            }
        };
        small_impact_drill = new MD_BurstDrill("small-impact-drill") {{
            requirements(Category.production, with(Items.graphite, 18, Items.copper, 18));
            drillTime = 60f * 12f;
            dominantItemsMulti = 2f;
            drillMultipliers.put(MD_Items.aluminium, 1.2f);
            drillMultipliers.put(Items.beryllium, 1.5f);
            drillMultipliers.put(Items.graphite, 1.5f);


            size = 2;
            hasPower = true;
            tier = 3;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, MD_Fx.mineImpactWave.wrap(Color.valueOf("F0FFFF"), 15f));
            shake = 1f;
            itemCapacity = 30;

            arrows = 1;
            arrowOffset = 0;
            arrowSpacing = 3f;

            researchCost = with();
            liquidBoostIntensity = 2;

            fogRadius = 4;
            consumeLiquid(Liquids.nitrogen, 1f / 60f).boost();
        }};
        ammonia_collector = new AttributeCrafter("ammonia-collector") {{
            requirements(Category.production, with(Items.silicon, 30, MD_Items.aluminium, 40));
            attribute = md_Attribute.ammonia;
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
            requirements(Category.production,with(Items.silicon,120,Items.copper,200,Items.graphite,150, MD_Items.al_alloy,150));
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
        //region distribution
            light_duct = new Duct("light-duct") {{
                requirements(Category.distribution, with(MD_Items.aluminium, 1));
                speed = 4f;
                health = 180;
                armor = 1;
                buildTime = 0.2f/60;
                bridgeReplacement = light_duct_bridge;
                alwaysUnlocked = true;
                researchCost = ItemStack.with(MD_Items.aluminium, 5);
                fullOverride = this.name + "-private";
            }
                @Override
                public void init() {
                    super.init();
                    bridgeReplacement = light_duct_bridge;
                }
            };

            armored_light_duct = new Duct("armored-light-duct") {{
                requirements(Category.distribution, with(MD_Items.polymer, 1, MD_Items.aluminium, 1, MD_Items.al_alloy,1));
                speed = 4f;
                health = 300;
                armor = 3;
                armored = true;
                researchCost = with(MD_Items.polymer, 100, MD_Items.aluminium, 100, MD_Items.al_alloy,100);
                bridgeReplacement = light_duct_bridge;
                buildCostMultiplier = 0.6f;
                fullOverride = this.name + "-private";
            }

                @Override
                public void init() {
                    super.init();
                    bridgeReplacement = light_duct_bridge;
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
                requirements(Category.distribution, with(Items.silicon, 10, Items.copper, 10));
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
                        Items.titanium, 30
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
                haloColor = Color.valueOf("E3E9FF");
            }};
        //endregion
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
            requirements(Category.liquid, with(Items.silicon,10, Items.copper, 10));
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
            requirements(Category.liquid, with(Items.copper, 10, MD_Items.aluminium,8,Items.silicon,5));
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
            requirements(Category.liquid, with(Items.copper, 1, MD_Items.aluminium,1));
            liquidCapacity = 60f;
            health = 100;
            botColor = Color.valueOf("45413B");
            explosivenessScale = flammabilityScale = 12f/60f;
            junctionReplacement = fluid_junction;
            bridgeReplacement = fluid_conduit_bridge;
        }};

        fluid_container = new LiquidRouter("fluid-container"){{
            requirements(Category.liquid, with(Items.titanium, 10, Items.metaglass, 15));
            liquidCapacity = 700f;
            size = 2;
            solid = true;

        }};
        //endregion
        //region turret
            ionize = new ContinuousTurret("ionize") {{
                requirements(Category.turret, ItemStack.with(Items.copper, 50, Items.silicon, 20, MD_Items.aluminium, 20));
                //outlineColor = Pal.darkOutline;
                researchCost = with(Items.copper, 50, Items.silicon, 20, MD_Items.aluminium, 20);
                shootType = new PointLaserBulletType() {{
                    beamEffect = MD_Fx.polyFacula(4, 3.2f, 0, 60f, Color.valueOf("d0d0ff"), 0.87f);
                    beamEffectInterval = 6f;
                    hitSound = Sounds.shootAtrax;
                    shootSound = Sounds.shootMerui;
                    hitEffect = MD_Fx.Mulitpleslash(20f, 1, Color.valueOf("d0d0ff"), 24f, 3f, 8f);
                    damageInterval = 12f;
                    setDefaults = false;
                    despawnHit = false;
                    fragOnDespawn = false;
                    sprite = modname + "ionize-point-laser";
                    targetAir = true;
                    targetGround = false;
                    damage = 40f / (60f / damageInterval);

                    pierceArmor = true;
                    hitColor = Color.valueOf("d0d0ff");
                    fragAngle = 90;
                    fragVelocityMin = 1;
                    fragVelocityMax = 1.2f;
                    fragLifeMax = 1f;
                    fragLifeMin = 1.5f;
                    fragOffsetMax = 1f;
                    fragOffsetMin = 1f;
                    fragBullets = 1;
                    fragBullet = new BasicBulletType(2.5f, 10f) {{

                        lifetime = 20;
                        pierce = true;
                        pierceCap = 2;
                        targetAir = true;
                        targetGround = false;
                        width = 6f;
                        height = 8f;
                        homingPower = 0.5f;
                        trailLength = 6;
                        trailWidth = 1.2f;
                        despawnEffect = hitEffect = MD_Fx.polyWave(16, 3, 0, 2.4f, 16f, Color.valueOf("f5f5ff"), 0.7f);
                        trailColor = backColor = hitColor = Color.valueOf("d0d0ff");
                        frontColor = Color.valueOf("f5f5ff");
                    }};

                }};
                scaledHealth = 250;
                armor = 2;
                scaleDamageEfficiency = true;
                recoil = 0f;
                range = 8 * 30f;
                aimChangeSpeed = 14f;
                rotateSpeed = 14f;
                drawer = new DrawTurret("steady-state-") {{
                    Color heatc = Pal.turretHeat;
                    heatColor = heatc;
                    parts.addAll(
                            new RegionPart("-blade") {{
                                progress = PartProgress.warmup;
                                heatProgress = PartProgress.warmup;
                                heat = Core.atlas.find(modname + "ionize" + suffix + "-heat");
                                mirror = true;
                                moveX = 3f / 4f;
                                moveY = 3f / 4f;
                                under = true;
                                heatColor = heatc;
                            }}
                    );
                }};
                size = 1;
                consumePower(4f);
            }};
            crack = new ItemTurret("crack"){{
                requirements(Category.turret, ItemStack.with(MD_Items.aluminium, 80, Items.silicon, 50));
                researchCost = with(MD_Items.aluminium, 20, Items.silicon, 10);
                ammo(
                        MD_Items.aluminium,
                        new CatapultBulletType(20*8/60f*1.5f,30){{
                        trailLength = 10;
                        trailWidth = 2f;
                        lifetime = 40;
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 3;
                        reloadMultiplier = 1.3f;

                        catapultProlongLifeTime = 2f;
                        catapultSpeedUp = 0.4f;

                        backColor = trailColor = Color.valueOf("FFB89E");
                        width = 8;
                        height = 10;
                        }},
                        MD_Items.light_ceramic,
                        new CatapultBulletType(25*8/60f*1.5f,60){{
                            homingPower = 0.01f;
                            homingRange = 100f;
                            rangeChange = 8*8f;
                            trailLength = 10;
                            trailWidth = 2f;
                            lifetime = 40;
                            pierce = true;
                            pierceBuilding = true;
                            pierceCap = 6;
                            catapultRange = 100f;
                            catapultProlongLifeTime = 3f;
                            catapultSpeedUp = 0.8f;

                            width = 8;
                            height = 13;

                            backColor = trailColor = MD_Items.light_ceramic.color;

                            lightning = 2;
                            lightningColor = MD_Items.light_ceramic.color;
                            lightningDamage = 20;
                            lightningLength = 2;
                            lightningLengthRand = 2;
                        }},
                        MD_Items.al_alloy,
                        new CatapultBulletType(27*8/60f*1.5f,25){{
                            rangeChange = 5*8f;
                            trailLength = 12;
                            trailWidth = 2f;
                            lifetime = 40;
                            pierce = true;
                            pierceBuilding = true;
                            pierceCap = 4;
                            catapultRange = 60;
                            catapultProlongLifeTime = 7f;
                            catapultSpeedUp = 0.4f;

                            backColor = trailColor = Color.valueOf("E6FDFF");

                            width = 8;
                            height = 13;

                            fragBullets = 2;
                            fragOffsetMax = fragOffsetMin = 0;
                            fragAngle = 0;
                            fragSpread = 180f;
                            fragRandomSpread = 0;
                            fragBullet = new ShrapnelBulletType(){{
                                damage = 20;
                                length = 2*8f;
                                width = 6f;
                                serrationWidth = 4f;
                                serrations = 3;
                                serrationSpacing = 6f;
                                serrationSpaceOffset = 20;
                                serrationLenScl = 3;
                                toColor = MD_Items.al_alloy.color;
                            }};
                        }}

                );
                size = 2;

                range = 20*8;
                reload = 25;
                inaccuracy = 3f;
                scaledHealth = 250;
                drawer = new DrawTurret("brown-"){{
                    parts.addAll(
                            new RegionPart("-blade"){{
                                mirror = true;
                                moveX = 1.9f;
                                moveY = 1.5f;
                                moveRot = -35f;
                                progress = PartProgress.warmup;
                                moves.add(new PartMove(PartProgress.recoil,0.6f,0,-13f));

                            }},new RegionPart("-hand"){{
                                mirror = true;
                                moveX = 0.4f;
                                moveY = -0.4f;
                                progress = PartProgress.warmup;
                                moves.add(new PartMove(PartProgress.recoil,0.3f,-0.4f,-10f));

                            }},
                            new RegionPart("-mid"),
                            new RegionPart("-barr"){{
                                moveY = -1f;
                                progress = PartProgress.warmup;
                                moves.add(new PartMove(PartProgress.recoil,0,-0.6f,0));
                            }}

                    );
                }};

            }};
            ejection = new ItemTurret("ejection"){{
                requirements(Category.turret, ItemStack.with(MD_Items.al_alloy,50,Items.silicon,50,Items.graphite,50));
                researchCostMultiplier = 0.5f;
                consumeLiquid(Liquids.nitrogen,6/60f);
                liquidCapacity = 40;
                //region ammo
                ammo(
                        Items.copper,new ArtilleryBulletType(2.9f, 30) {{
                            hitEffect = new MultiEffect(Fx.flakExplosion, Fx.shockwaveSmaller);
                            knockback = 1.2f;
                            lifetime = 66f;
                            width = 9f;
                            height = 12f;
                            collidesTiles = false;
                            splashDamageRadius = 2.5f*8;
                            splashDamage = 40;

                            backColor = hitColor = trailColor = Pal.copperAmmoBack;
                            frontColor = Pal.copperAmmoFront;
                            despawnEffect = Fx.hitBulletColor;
                            lifeScaleRandMax = 1.08f;
                            lifeScaleRandMin = 0.95f;
                            fragBullets = 4;
                            fragBullet = new BasicBulletType(2.5f, 25, "bullet"){{
                                width = 7;
                                height = 10;
                                shrinkY = 1f;
                                lifetime = 10;
                                backColor = Pal.copperAmmoBack;
                                frontColor = Pal.copperAmmoFront;
                                despawnEffect = Fx.none;
                                collidesAir = false;
                            }};
                        }},
                        Items.silicon,new ArtilleryBulletType(2.9f, 20) {{
                            hitEffect = new MultiEffect(Fx.flakExplosion, Fx.shockwaveSmaller);
                            knockback = 0.3f;
                            lifetime = 66f;
                            width = 9f;
                            height = 12f;
                            collidesTiles = false;
                            splashDamageRadius = 2f*8;
                            splashDamage = 50;

                            backColor = hitColor = trailColor = Pal.siliconAmmoBack;
                            frontColor = Pal.siliconAmmoFront;
                            despawnEffect = Fx.hitBulletColor;
                            lifeScaleRandMax = 1.08f;
                            lifeScaleRandMin = 0.95f;
                            fragBullets = 6;
                            fragBullet = new BasicBulletType(2, 15, "bullet"){{
                                width = 7;
                                height = 10;
                                shrinkY = 1f;
                                lifetime = 13;
                                homingPower = 0.42f;
                                backColor = Pal.siliconAmmoBack;
                                frontColor = Pal.siliconAmmoFront;
                                despawnEffect = Fx.none;
                                collidesAir = false;
                            }};
                        }},
                        MD_Items.polymer,new ArtilleryBulletType(2.9f, 40) {{
                            hitEffect = new MultiEffect(Fx.flakExplosion, Fx.shockwaveSmaller);
                            knockback = 0.3f;
                            lifetime = 66f;
                            width = 11f;
                            height = 13f;
                            collidesTiles = false;
                            splashDamageRadius = 3.2f*8;
                            splashDamage = 60;
                            status = MD_StatusEffects.adhesion;
                            statusDuration = 180;
                            backColor = hitColor = trailColor = Color.valueOf("F7DE80");
                            frontColor = Color.valueOf("FFF1D4");
                            despawnEffect = Fx.hitBulletColor;
                            lifeScaleRandMax = 1.08f;
                            lifeScaleRandMin = 0.95f;
                            fragBullets = 3;
                            fragOffsetMax = fragOffsetMin = fragRandomSpread = 0;
                            fragVelocityMax = fragVelocityMin = 1;
                            fragAngle = 60f;
                            fragSpread = 120f;
                            fragBullet = new BasicBulletType(1.8f, 10, "bullet"){{
                                width = 16;
                                height = 12;
                                shrinkY = 0.6f;
                                lifetime = 8;
                                hitColor = backColor = Color.valueOf("F7DE80");
                                frontColor = Color.valueOf("FFF1D4");
                                hitEffect = MD_Fx.waveHitColor(12,10f,13,1f,0.8f);
                                despawnEffect = Fx.none;
                                collidesAir = false;
                                splashDamageRadius = 1.5f*8;
                                splashDamage = 20;
                                fragBullets = 2;
                                fragOffsetMax = fragOffsetMin = fragRandomSpread = 0;
                                fragVelocityMax = fragVelocityMin = 1;
                                fragAngle = 0;
                                fragSpread = 180;
                                fragBullet = new BasicBulletType(1.8f,15){{
                                    collidesAir = false;
                                    lifetime = 8;
                                    width = 11;
                                    height = 8;
                                    shrinkY = 1f;
                                    backColor = Color.valueOf("F7DE80");
                                    frontColor = Color.valueOf("FFF1D4");
                                    despawnEffect = Fx.none;
                                }};
                            }};
                        }}
                );
                //endregion
                shoot.shots = 3;
                shoot.shotDelay = 0;
                inaccuracy = 8;
                shootSound = Sounds.shootDiffuse;
                shootSoundVolume = 0.6f;
                soundPitchMin = 0.6f;
                soundPitchMax = 0.8f;
                reload = 90f;
                range = 30f*8;
                size = 2;
                targetAir = false;
                drawer = new DrawTurret("brown-");

            }};
            fracture = new ItemTurret("fracture") {{
                requirements(Category.turret, ItemStack.with(MD_Items.aluminium, 120, Items.silicon, 80, Items.titanium, 80));
                ammo(
                        MD_Items.aluminium, new BasicBulletType(7f, 10) {{
                            hitColor = backColor = frontColor = MD_Items.aluminium.color;
                            trailColor = new Color(0xd0c0c0f0);
                            sprite = "circle";
                            width = 6;
                            height = 6;
                            shrinkX = 0;
                            shrinkY = 0;
                            lifetime = 32f;
                            trailWidth = 2.5f;
                            trailLength = 8;
                            trailSinScl = 8f;

                            reloadMultiplier = 1.2f;
                            status = MD_StatusEffects.embrittlement;
                            statusDuration = 0.5f * 60f;

                            ammoMultiplier = 4;

                            splashDamage = 20f;
                            splashDamageRadius = 35;
                            hitEffect = despawnEffect = new MultiEffect(
                                    MD_Fx.polyWave(48, 35, 0, 4f, 25f, hitColor, 0.85f),
                                    MD_Fx.spatter,
                                    MD_Fx.polygonalStar(40f, 4, hitColor.a(0.85f), 45f, 7f, 45f)
                            );
                        }},
                        MD_Items.al_alloy, new BasicBulletType(8f, 15f) {{
                            hitColor = backColor = frontColor = MD_Items.al_alloy.color;
                            trailColor = new Color(0xd0d0d8f8);
                            sprite = "circle";
                            width = 7;
                            height = 7;
                            shrinkX = 0;
                            shrinkY = 0;
                            lifetime = 32f;
                            trailWidth = 2.7f;
                            trailLength = 8;
                            trailSinScl = 8f;
                            rangeChange = 4 * 8f;

                            status = MD_StatusEffects.embrittlement;
                            statusDuration = 1.5f * 60f;
                            ammoMultiplier = 4;

                            splashDamage = 30f;
                            splashDamageRadius = 37;
                            hitEffect = despawnEffect = new MultiEffect(
                                    MD_Fx.polyWave(48, splashDamageRadius, 0, 4f, 25f, hitColor, 0.85f),
                                    MD_Fx.spatter,
                                    MD_Fx.polygonalStar(40f, 4, hitColor.a(0.85f), splashDamageRadius + 10f, 8f, 45f)
                            );
                            despawnHit = true;
                            fragBullets = 3;
                            fragOffsetMax =fragOffsetMin = 0;
                            fragBullet = new ShrapnelBulletType() {{
                                toColor = Color.valueOf("CFE9FF");
                                length = 30f;
                                serrationSpaceOffset = 70;
                                serrations = 4;
                                serrationLenScl = 10f;
                                width = 12f;
                                damage = 20;
                                pierceArmor = true;
                                lifetime = 40f;
                            }};

                        }}
                );
                reload = 10f;
                range = 28 * 8f;
                shootEffect = Fx.shootSmokeSquareBig;

                shoot = new ShootMulti(
                        new ShootBarrel() {{
                            barrels = new float[]{
                                    -12f, -4f, -1f,
                                    6f, -1f, 0.8f,
                                    -6f, -1f, -0.8f,
                                    12f, -4f, 1f
                            };
                        }},
                        new ShootHelix() {{
                            scl = 2f;
                            mag = 2f;
                        }}
                );
                recoils = 4;
                warmupMaintainTime = 30f;
                shootSound = Sounds.shootMissilePlasmaShort;
                soundPitchMin = 1.2f;
                soundPitchMax = 1.4f;
                minWarmup = 0.90f;
                heatColor = Color.valueOf("d8d8ff");

                drawer = new DrawTurret("steady-state-") {{
                    for (int i : new int[]{1, 4, 2, 3}) {
                        parts.add(new RegionPart("-barrel-" + i) {{
                            progress = PartProgress.recoil;
                            recoilIndex = i - 1;
                            under = true;
                            moveY = -3f;

                            if (i == 1) {
                                moves.add(new PartMove(PartProgress.warmup, -15 / 4f, 9 / 4f, 0f));
                            } else if (i == 2) {
                                moves.add(new PartMove(PartProgress.warmup, 9f / 4f, 12f / 4f, 0f));
                            } else if (i == 3) {
                                moves.add(new PartMove(PartProgress.warmup, -9f / 4f, 12f / 4f, 0f));
                            } else {
                                moves.add(new PartMove(PartProgress.warmup, 15f / 4f, 9f / 4f, 0f));
                            }

                        }});
                    }
                    parts.add(new RegionPart("-side") {{
                        heatColor = Color.valueOf("d8d8ff");
                        under = true;
                        mirror = true;
                        progress = PartProgress.warmup;
                        heatProgress = PartProgress.warmup;
                        moveX = 1f;
                        moveY = -1f;
                        x = -0.1f;
                        y = 0.1f;

                    }});
                }};
                hasLiquids = true;

                size = 3;
                scaledHealth = 250;
                armor = 3;
                outlineColor = Pal.darkOutline;
            }};
            break_water = new ItemTurret("break-water") {{
                hideDatabase = true;
                scaledHealth = 250;

                requirements(Category.turret, with(MD_Items.al_alloy, 120, MD_Items.polymorphic_crystal, 50, Items.phaseFabric, 80, Items.silicon, 150));
                ammo(
                        Items.phaseFabric, new BallLightningBulletType(7f, 20f,"mine-bullet") {{
                            shrinkX= shrinkY = 0;
                            lifetime = 30f;
                            shockRange = 80f;
                            shockDamage = 30;
                            lightning = 10;
                            shockCooldown = 10f;
                            shockLimit = 1;
                            frontColor = Color.valueOf("f8f8ff");
                            backColor = trailColor = hitColor = Color.valueOf("d8e0ff");
                            hitEffect = new MultiEffect(
                                    MD_Fx.polyWave(4,25,45,5f,35f,hitColor,0.95f),
                                    MD_Fx.spatter
                            );
                            fragBullets = 1;
                            width = 32f;
                            height = 32f;
                            trailLength = 12;
                            spin = 10f;
                            trailWidth = 4f;
                            fragOffsetMax = 0;
                            fragOffsetMin = 0;
                            fragSpread = fragRandomSpread = intervalRandomSpread = 0f;
                            fragBullet = new EntityCrafterBulletType() {{
                                        hitEffect = Fx.none;
                                        craft = b -> {
                                            EntityShield shield = new EntityShield() {{
                                                sides = 4;
                                                radius = 40f;
                                                shieldRotation = 0f;
                                                shieldHealth = 500;
                                                lifeTime = 300;
                                            }};
                                            shield.create(b.x, b.y, b.rotation()+45f, b.team, Color.valueOf("e0e8ff"));
                                        fragBullets = 4;
                                        fragLifeMax = 1.2f;
                                        fragLifeMin = 0.6f;
                                        fragVelocityMax = fragVelocityMin = 1;
                                        fragBullet = new BasicBulletType(2f,20,"mine-bullet"){{
                                            backRegion = Core.atlas.find("mine-bullet-back");
                                            frontRegion = Core.atlas.find("mine-bullet");
                                            width = 16;
                                            height = 16;
                                            shrinkX= shrinkY = 0.5f;
                                            splashDamageRadius = 28f;
                                            splashDamage = 40f;
                                            lifetime = 16f;
                                            backColor = hitColor = Color.valueOf("d8e0ff");
                                            despawnEffect = hitEffect = new MultiEffect(
                                                    MD_Fx.polyWave,
                                                    MD_Fx.polygonalStar(30f,4,hitColor,15f,4f,0)
                                                    );
                                            despawnHit = true;
                                        }};
                                        };
                                    }};

                        }}
                );
                range = 8f * 45;
                size = 4;
                reload = 120f;
                shootY = 0;
                shoot = new ShootSwing() {{
                    startRotation = -12;
                    endRotation = 12;
                    shots = 3;
                    shotDelay = 5f;
                    drawer = new DrawTurret("steady-state-");
                }};
            }};
            dawn = new ItemTurret("dawn"){{
                requirements(Category.turret,with(MD_Items.polymorphic_crystal,50, MD_Items.polymer,200, MD_Items.light_ceramic,150,Items.silicon,220));
                scaledHealth = 250;
                predictTarget = false;
                shootSound = Sounds.shootMissileLong;
                moveWhileCharging = false;
                soundPitchMax = 0.8f;
                soundPitchMin = 0.65f;
                shoot.firstShotDelay = 50f;
                rotateSpeed = 1.5f;
                ammo(
                        MD_Items.polymorphic_crystal,new BallLightningBulletType(110f/60f,120,"large-orb"){{
                            drag = 0.005f;
                            lifetime = 60f*6;
                            despawnHit = true;
                            splashDamage = 200f;
                            splashDamageRadius = 72f;
                            chargeEffect = MD_Fx.dawnCharge;
                            shootEffect = new MultiEffect(MD_Fx.starExplosionSmall, MD_Fx.spatterBig);

                            hitSound = Sounds.explosionReactor2;
                            hitSoundVolume = 0.45f;
                            hitEffect = new MultiEffect(MD_Fx.gradientWave(40f,50f), MD_Fx.starExplosionBig);

                            trailColor = hitColor = backColor = MD_Items.polymorphic_crystal.color;

                            trailLength = 8;
                            trailWidth = 3f;

                            width = height = 18f;
                            shrinkX = shrinkY = 0;

                            bulletDrawer = b->{
                                Draw.color(hitColor);
                                for(int i: Mathf.zeroOne){
                                Drawf.tri(b.x,b.y,10,30, Time.time%360+i*180f);

                                Drawf.tri(b.x,b.y,10,20, (Time.time*-2f)%360+i*180f);
                                }
                            };

                            shockEffect = new MultiEffect(MD_Fx.chainLightningPro, MD_Fx.waveHitColor(12,9f,13f,1.5f,0.85f));
                            shockDamage = 80f;
                            shockRange = 160;
                            shockAmount = 5;
                            shockLimit = 3;

                            shockCooldown = 4.8f;
                            shockStatus = MD_StatusEffects.dimension_slip;
                            statusDuration = 2*60f;

                            collidesAir = false;
                            collidesGround = false;

                            intervalBullets = 2;
                            bulletInterval = 6f;
                        }}
                );
                range = 8f*38;
                ammoPerShot = 5;
                size = 4;
                reload = 120f;
                warmupMaintainTime = 40f;
                shootY = 15.5f;
                minWarmup = 0.99f;
                shootWarmupSpeed = 0.06f;

                drawer = new DrawTurret("brown-"){{
                    parts.addAll(
                            new RegionPart("-mid-under-blade"){{
                                mirror = true;
                                x = -1.5f;
                                progress = PartProgress.warmup;
                                moveY = 7.8f;
                                moveX = 6.5f;
                                moves.add(new PartMove(PartProgress.recoil,1,-2,0));
                                under = true;
                            }},
                            new RegionPart("-mid-under-l"){{
                                progress = PartProgress.warmup;
                                moveY = 7.8f;
                                moveX = -5f;
                                moves.add(new PartMove(PartProgress.recoil,-1,-2,0));
                                under = true;
                            }},
                            new RegionPart("-mid-under-r"){{
                                progress = PartProgress.warmup;
                                moveY = 7.8f;
                                moveX = 5f;
                                moves.add(new PartMove(PartProgress.recoil,1,-2,0));
                                under = true;
                            }},

                            new RegionPart("-blade-der-l"){{
                                x = 3f;
                                moves.add(new PartMove(PartProgress.warmup,-10f,4f,30));
                                moves.add(new PartMove(PartProgress.recoil,0,0,5));
                                under = true;
                            }},
                            new RegionPart("-blade-l"){{
                                moves.add(new PartMove(PartProgress.warmup,-8f,4f,30));
                                moves.add(new PartMove(PartProgress.recoil,0,0,5));
                                under = true;
                            }},

                            new RegionPart("-blade-der-r"){{
                                x = -3f;
                                moves.add(new PartMove(PartProgress.warmup,10f,4f,-30));
                                moves.add(new PartMove(PartProgress.recoil,0,0,-5));
                                under = true;
                            }},
                            new RegionPart("-blade-r"){{
                                moves.add(new PartMove(PartProgress.warmup,8f,4f,-30));
                                moves.add(new PartMove(PartProgress.recoil,0,0,-5));
                                under = true;
                            }},
                            new RegionPart("-mid-middle"){{
                                under = true;
                            }},
                            new RegionPart("-mid"){{
                                moveY = 2f;
                                moves.add(new PartMove(PartProgress.recoil,0,-2,0));
                                under = false;
                            }}


                    );
                }};

                outlineColor = Pal.darkOutline;
            }};
            fluffrain = new LiquidTurret("fluffrain"){{
                requirements(Category.turret,with(MD_Items.polymer,150,Items.silicon,200, MD_Items.al_alloy,200, MD_Items.ti_alloy,180));
                scaledHealth = 250;
                predictTarget = false;
                ammo(
                        MD_Liquids.dimension_fluid,new BasicBulletType(15.2f*8/60f,50){{
                            homingPower = 0.02f;
                            lifetime = 55f;
                            width = 7f;
                            height = 20;
                            trailWidth = 1.2f;
                            trailLength = 18;
                            trailColor = backColor = hitColor = Color.valueOf("FFE894");
                            drag = -0.03f;
                            shootSound = Sounds.shootMissilePlasma;
                            soundPitchMax = 0.9f;
                            soundPitchMin = 0.6f;
                            shootSoundVolume = 0.68f;
                            ammoMultiplier = 1/7.2f;

                            despawnEffect=hitEffect = MD_Fx.fluffrainHit;
                            trailEffect = Fx.colorSpark;
                            trailInterval = 15;
                            trailRotation = true;

                            splashDamage = 40f;
                            splashDamageRadius =8;
                            pierce = true;
                            pierceCap = 2;
                            despawnHit = false;
                            fragOnDespawn = false;
                            setDefaults = false;


                            fragBullets = 1;
                            fragOffsetMax = fragOffsetMin = 0;
                            fragBullet = new RefractedLaserBulletType(){{
                                damage = 250;
                                pierceCap = 6;
                                status = MD_StatusEffects.dimension_slip;
                                statusDuration = 60f;
                            }
                                @Override
                                public void hit(Bullet b, float hitx, float hity, boolean createFrags) {
                                    super.hit(b, hitx, hity, createFrags);
                                    Puddles.deposit(world.tileWorld(hitx, hity), MD_Liquids.dimension_fluid, 15f);
                                }
                            };
                        }
                            @Override
                            public void createFrags(Bullet b, float x, float y) {
                                if(Mathf.random()>0.9f){
                                    Sounds.explosionCleroi.at(x,y,0.85f + Mathf.random(0.3f),0.4f);
                                    super.createFrags(b, x, y);
                                }
                            }
                            @Override
                            public void updateTrailEffects(Bullet b){
                                boolean canSpawn = trailMinVelocity <= 0f || b.vel.len2() >= trailMinVelocity * trailMinVelocity;
                                if(b.time>15 && trailInterval > 0f && canSpawn){
                                    if(b.timer(0, trailInterval / (b.fin()*2.2f+0.8f) + b.id%4*2)){
                                        trailEffect.at(b.x, b.y,b.rotation()+180, trailColor);
                                    }
                                }
                            }
                        }
                );
                liquidCapacity = 120f;
                shootEffect = MD_Fx.fluffrainShoot;
                loopSound = Sounds.none;
                size = 4;
                warmupMaintainTime = 40f;
                shoot = new ShootBarrelRandom(){{
                    float s = 2f;
                    barrels = new float[]{
                            -5*s,2,0,
                            -3*s,2,0,
                            -s,-4,0,
                            0,-4,0,
                            s,-4,0,
                            3*s,2,0,
                            5*s,2,0,
                    };
                    shots = 5;
                    shotDelay = 4f;
                }};
                shootY = 13f;
                reload = 18f;
                minWarmup = 0.7f;
                range = 35*8f;
                drawer = new DrawTurret("brown-"){{
                    parts.addAll(
                            new RegionPart("-barrel"){{
                                under = true;
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,7/4f,-12/4f,0));
                            }},
                            new RegionPart("-blade"){{
                                under = true;
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,2/4f,-3/4f,0));
                            }}

                    );
                }};
            }};
            polarization = new ItemTurret("polarization"){{
                requirements(Category.turret,with(MD_Items.plasma,80, MD_Items.al_alloy,200, MD_Items.polymer,150,Items.silicon,200));
                scaledHealth = 250;

                ammo(
                        MD_Items.plasma,new BulletType(0,0){{
                            shootEffect = Fx.none;
                            smokeEffect = MD_Fx.shootSmokeMissileSmallColor;
                            hitColor =  Color.valueOf("c0d8ff");
                            ammoMultiplier = 1f;
                            spawnUnit = new MissileUnitType("polarization-missile"){{
                                softShadowScl = 0.6f;
                                speed = 6f;
                                maxRange = 6f;
                                lifetime = 60f*2.25f;
                                hitSize = 10f;
                                outlineColor = Pal.darkOutline;
                                engineColor = trailColor = Color.valueOf("c0d8ff");
                                engineLayer = Layer.effect;
                                engineSize = 2.2f;
                                engineOffset = 8f;
                                rotateSpeed = 1f;
                                trailLength = 18;
                                missileAccelTime = 40f;
                                lowAltitude = true;
                                loopSound = Sounds.loopMissileTrail;
                                loopSoundVolume = 0.6f;
                                deathSound = Sounds.explosionMissile;
                                targetAir = true;
                                targetUnderBlocks = false;

                                fogRadius = 4f;

                                health = 200;

                                weapons.add(new Weapon() {{
                                    shootCone = 360f;
                                    mirror = false;
                                    reload = 1f;
                                    deathExplosionEffect = Fx.massiveExplosion;
                                    shootOnDeath = true;
                                    shake = 10f;
                                    bullet = new ExplosionBulletType(500, 45) {{
                                        hitColor = Color.valueOf("c0d8ff");
                                        shootEffect = new MultiEffect(MD_Fx.starExplosionBig, MD_Fx.spatterBig,new WaveEffect() {{
                                            lifetime = 20f;
                                            strokeFrom = 4f;
                                            sizeTo = 60f;
                                        }});

                                        collidesAir = true;
                                        buildingDamageMultiplier = 0.1f;

                                        ammoMultiplier = 1;
                                        fragLifeMax = 1.2f;
                                        fragLifeMin = 0.8f;
                                        fragBullets = 1;
                                        fragBullet = new BallLightningBulletType(0f, 70,"large-orb") {{
                                            shrinkX = 0.3f;
                                            shrinkY = 0.3f;

                                            shockCooldown = 12f;
                                            shockStatus = StatusEffects.shocked;
                                            shockEffect = new MultiEffect(MD_Fx.starExplosionSmall, MD_Fx.chainLightningPro(30f,2.8f,15f,8f));
                                            lightning = 5;
                                            shockRange = 80f;
                                            shockDamage = damage;
                                            backColor = trailColor = hitColor = Color.valueOf("c0d8ff");

                                            collidesAir = false;
                                            collidesGround = false;
                                            collidesTiles = false;
                                            buildingDamageMultiplier = 0.1f;
                                            drag = 0.02f;
                                            hitEffect = MD_Fx.starExplosion;
                                            despawnHit = true;
                                            despawnSound = Sounds.shootEnergyField;
                                            knockback = 0.8f;
                                            lifetime = 70f;
                                            width = height = 34f;

                                            splashDamageRadius = 60f;
                                            splashDamage = 120;

                                            frontColor = Color.white;
                                        }};
                                    }};
                                }});
                            }};
                        }}
                );
                shootY = 0;
                float[] barr = new float[]{
                        -12f,1.5f,0,
                        -6.5f,3f,0,
                        6.5f,3f,0f,
                        12f,1.5f,0f
                };;
                shoot = new ShootBarrel(){{
                    barrels = barr;
                    shots = 4;
                    shotDelay = 12f;

                }};

                shootSound = Sounds.shootMissileLarge;
                soundPitchMax = 0.57f;
                soundPitchMin = 0.45f;
                shootSoundVolume = 0.65f;

                reload = 12*60f;
                warmupMaintainTime = 200f;
                shootWarmupSpeed = 0.045f;
                minWarmup = 0.98f;
                coolant = consumeCoolant(20f/60f);
                coolantMultiplier = 2f;
                range = 8f*80;


                drawer = new DrawTurret("steady-state-"){{
                    parts.addAll(
                            new RegionPart("-mid"){{
                                under = false;
                            }},
                            new RegionPart("-outside-l"){{
                                moves.add(new PartMove(PartProgress.warmup,-20f/4f,0,0));
                                under = true;
                            }},
                            new RegionPart("-outside-r"){{
                                moves.add(new PartMove(PartProgress.warmup,20f/4f,0,0));
                                under = true;
                            }},
                            new RegionPart("-inside-l"){{
                                moves.add(new PartMove(PartProgress.warmup,-10f/4f,0,0));
                                under = true;
                            }},
                            new RegionPart("-inside-r"){{
                                moves.add(new PartMove(PartProgress.warmup,10f/4f,0,0));
                                under = true;
                            }}
                    );
                    for(int i=0;i<4;i++){
                        int f = i;
                        parts.add(new RegionPart("-missile"){{
                            x = barr[f*3]*0.5f;
                            y = barr[f*3+1]*0.5f;
                            moves.add(new PartMove(PartProgress.warmup,barr[f*3]*0.5f,barr[f*3+1]*0.5f,0));
                            progress = PartProgress.reload.curve(Interp.pow2In);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            color = Color.white;
                            mixColorTo = Pal.accent;
                            mixColor = new Color(1f, 1f, 1f, 0f);
                            outline = false;
                            under = true;
                            layerOffset = -0.01f;
                        }});
                    }
                }};
                size = 4;
            }};
            test4 = new ContinuousTurret("test4"){{
                requirements(Category.turret,BuildVisibility.sandboxOnly,with());
                drawDisabled = true;
                size = 4;
                shootType = new MultiPointLaserBullet(){{
                    damage = 2500f/12f;
                    buildingDamageMultiplier = 0.3f;
                    hitColor = Color.valueOf("fda981");
                    amount = 3;
                    trailLength = 15;
                    trailWidth = 5f;
                    angleRange(10);
                    baseTraceRad = 5f;
                    fractMulti = 0.7f;
                    beamEffect = Fx.none;

                    beginPos = new float[]{
                            -8,-3,
                            0,0,
                            8,-3
                    };
                }
                    @Override
                    public void idleAct(Vec2 v, int i, Bullet b) {
                        Vec2 bv = beginVec2[i].cpy().rotate(b.rotation()-90).scl(0.7f);
                        v.set(
                                Mathf.approachDelta(
                                        v.x,
                                        bv.x + Mathf.cos(Time.time/18 + Mathf.PI * 2 * i / amount, 1, 1.2f),
                                        2f
                                ),
                                Mathf.approachDelta(
                                        v.y,
                                        bv.y + Mathf.sin(Time.time/18 + Mathf.PI * 2 * i / amount, 1, 1.2f),
                                        2f
                                )
                        );
                    }
                };
                warmupMaintainTime = 50f;
                aimChangeSpeed =2;
                range = 320f;
                rotateSpeed = 60/60f;
                shootCone = 360f;
                unitSort = UnitSorts.strongest;
                consume(new ConsumeBeam(80, MD_beams.near_infrared_light));

            }};
            crest = new MD_PayloadTurret("crest"){{
                scaledHealth = 440;
                squareSprite = false;
                outlineColor = Pal.darkOutline;
                unitSort = UnitSorts.strongest;
                requirements(Category.turret,with(
                        MD_Items.ti_alloy,800,
                        MD_Items.polymer,1500,
                        MD_Items.al_alloy,1200,
                        MD_Items.plasma,200,
                        Items.silicon,1500
                ));
                maxAmmo = 6;
                ammoPerShot = 2;
                ammo(
                        MD_blocks.heavy_ammo,new BasicBulletType(40,3840,modname+"heavyammo"){{
                            displayAmmoMultiplier = true;
                            ammoMultiplier = 1;
                            frontColor = backColor = trailColor = hitColor = Color.valueOf("E3E8FF");
                            trailLength = 15;
                            trailWidth = 2.5f;
                            trailSinMag = 0.3f;
                            trailSinScl = 0.5f;
                            trailEffect = MD_Fx.Mulitpleslash(30,4,hitColor,40,5,30);
                            trailInterval = 1.2f;
                            width = 25f;
                            height = 43f;
                            lifetime = 16f;
                            hitSize = 15f;
                            despawnEffect = new MultiEffect(MD_Fx.starExplosionBig, MD_Fx.spikeExplosion);
                            hitEffect = new MultiEffect(MD_Fx.spikeHit, MD_Fx.spikeHitRotation);

                            setDefaults = false;
                            despawnHit = false;
                            fragOnDespawn = true;
                            despawnSound = new MultiSound(Sounds.explosionReactor,Sounds.explosionReactor2);
                            hitSound = Sounds.explosionDull;

                            pierce = true;
                            pierceDamageFactor = 0.5f;
                            pierceCap = 4;

                            splashDamage = 1500;
                            splashDamageRadius = 60f;

                            fragBullets = 5;
                            fragBullet = new BasicBulletType(11,300){{
                                lifetime = 6;
                                splashDamage = 500;
                                splashDamageRadius = 60f;
                                despawnHit = true;
                                frontColor = backColor = trailColor = hitColor = Color.valueOf("E3E8FF");
                                trailLength = 5;
                                trailWidth = 1.5f;
                                width = 12;
                                height = 13;
                                hitSound = Sounds.explosionPlasmaSmall;
                                hitEffect = new MultiEffect(
                                        MD_Fx.waveColor(13,45,2.3f,Interp.linear),
                                        MD_Fx.waveColor(13,39,1.7f,Interp.linear),
                                        MD_Fx.waveColor(13,22.5f,1.7f,Interp.linear)
                                        );
                            }};

                        }}
                );
                shootCone = 3f;
                range = 8*79.4f;
                reload = 180;
                rotateSpeed = 2;
                coolant = consumeCoolant(40f/60f);
                coolantMultiplier = 0.6f;
                size = 5;
                shootWarmupSpeed = 0.03f;
                minWarmup = 0.98f;
                warmupMaintainTime = 300f;
                shootSound = Sounds.explosionReactor2;
                shootSoundVolume = 0.8f;
                soundPitchMin = 1.3f;
                soundPitchMax = 1.65f;
                shootEffect = new MultiEffect(MD_Fx.crestShoot, MD_Fx.crestShootFlame);
                drawer = new DrawTurret("steady-state-"){{

                    float haloY = -15f;
                    var haloProgress = DrawPart.PartProgress.warmup.delay(0.3f);
                    float haloRotSpeed = 0.7f;
                    Color haloColor = Color.valueOf("F2F8FF");
                    parts.addAll(
                            new RegionPart("-end"){{
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,8f/4,-8f/4,0));
                                moves.add(new PartMove(PartProgress.recoil,5f/4,-5f/4,0));
                                under = false;
                            }},
                            new RegionPart("-side"){{
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,20f/4,-3f/4,3));
                                moves.add(new PartMove(PartProgress.recoil,5f/4,-3/4f,-7));
                            }},
                            new RegionPart("-blade"){{
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,15f/4,-9f/4,0));
                                moves.add(new PartMove(PartProgress.recoil,3f/4,0,4));
                            }},
                            new RegionPart("-mid"){{
                                under = false;
                            }},
                            new RegionPart("-barrel-under"){{
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,12f/4,23f/4,3));
                                moves.add(new PartMove(PartProgress.recoil,6f/4,-4f/4,0));
                            }},
                            new RegionPart("-barrel"){{
                                mirror = true;
                                moves.add(new PartMove(PartProgress.warmup,13.2f/4,4f/4,3));
                                moves.add(new PartMove(PartProgress.recoil,6f/4,-4f/4,0));
                            }},
                            new ShapePart(){{
                                progress = PartProgress.warmup.delay(0.2f);
                                color = haloColor;
                                sides = 4;
                                hollow = false;
                                stroke = 0f;
                                radius = 0;
                                radiusTo = 4f;
                                layer = Layer.effect;
                                y = haloY;
                            }},
                            new ShapePart(){{
                                progress = PartProgress.warmup.delay(0.2f);
                                color = haloColor;
                                sides = 4;
                                hollow = true;
                                stroke = 0;
                                strokeTo = 2f;
                                radius = 9f;
                                layer = Layer.effect;
                                y = haloY;
                            }},
                            new HaloPart(){{
                                progress = haloProgress;
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 2;
                                triLength = 0f;
                                triLengthTo = 35f;
                                haloRotation = 90;
                                haloRadius = 7;
                                tri = true;
                                radius = 4;
                            }},
                            new HaloPart(){{
                                progress = haloProgress;
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 2;
                                triLength = 0f;
                                triLengthTo = 12;
                                haloRotation = 0;
                                haloRadius = 7;
                                tri = true;
                                radius = 4;
                            }},
                            new HaloPart(){{
                                progress = haloProgress;
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 4;
                                triLength = 0f;
                                triLengthTo = 3;
                                haloRotation = 45;
                                haloRadius = 12;
                                tri = true;
                                radius = 6;
                            }},
                            new HaloPart(){{
                                progress = haloProgress;
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 4;
                                triLength = 0f;
                                triLengthTo = 3;
                                haloRotation = 45;
                                shapeRotation = 180f;
                                haloRadius = 12;
                                tri = true;
                                radius = 6;
                            }},
                            new HaloPart(){{
                                progress = PartProgress.warmup.delay(0.2f).mul(p->(1-p.reload));
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 2;
                                triLength = 0f;
                                triLengthTo = 16;
                                haloRotation = 22.5f+45;
                                shapeRotation = -22.5f;
                                haloRadius = 16;
                                tri = true;
                                radius = 4;
                            }},
                            new HaloPart(){{
                                progress = PartProgress.warmup.delay(0.2f).mul(p->(1-p.reload));
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 2;
                                triLength = 0f;
                                triLengthTo = 4;
                                haloRotation = 22.5f+45;
                                shapeRotation = -22.5f + 180;
                                haloRadius = 16;
                                tri = true;
                                radius = 4;
                            }},
                            new HaloPart(){{
                                progress = PartProgress.warmup.delay(0.2f).mul(p->(1-p.reload));
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 2;
                                triLength = 0f;
                                triLengthTo = 16;
                                haloRotation = -22.5f-45;
                                shapeRotation = 22.5f;
                                haloRadius = 16;
                                tri = true;
                                radius = 4;
                            }},
                            new HaloPart(){{
                                progress = PartProgress.warmup.delay(0.2f).mul(p->(1-p.reload));
                                color = haloColor;
                                layer = Layer.effect;
                                y = haloY;
                                haloRotateSpeed = 0;

                                shapes = 2;
                                triLength = 0f;
                                triLengthTo = 4;
                                haloRotation = -22.5f-45f;
                                shapeRotation = 22.5f + 180;
                                haloRadius = 16;
                                tri = true;
                                radius = 4;
                            }}
                    );
                }};
                loopSound = Sounds.loopGlow;
                loopSoundVolume = 0.8f;
            }};
        //endregion
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
        //region core
            coreSteady = new MD_ElectricFieldCoreBlock("core-steady"){{
                requirements(Category.effect, BuildVisibility.coreZoneOnly, with(Items.silicon, 1200, MD_Items.aluminium,1500,Items.graphite,1000));
                alwaysUnlocked = true;
                hasPower = true;
                conductivePower = true;

                powerProduction = 5f;

                isFirstTier = true;
                unitType =  MD_UnitTypes.primitive;
                armor = 4;
                health = 3500;
                itemCapacity = 6000;
                buildCostMultiplier = 1f;
                thrusterLength = 38f/4f;

                lightningEffect = new MultiEffect(Fx.chainLightning, MD_Fx.waveColor(20f,12f,4f));
                lightnings = 7;
                lightningDamage = 50f;
                unitCapModifier = 15;
                size = 4;
                fullOverride = this.name + "-private";
            }};

            proof_container = new StorageBlock("proof-container"){{
                requirements(Category.effect, with(MD_Items.aluminium,80, MD_Items.polymer,60));
                size = 2;
                itemCapacity = 220;
                scaledHealth = 100;
                baseExplosiveness = -100f;
                explosivenessScale = 0.015f;
                flammabilityScale = 0.1f;
                fullOverride = this.name + "-full";
            }};
            
            stack = new DumpStorageBlock("stack"){{
                requirements(Category.effect, with(MD_Items.aluminium,20, MD_Items.polymer,15,Items.silicon,20));
                squareSprite = false;
                buildCostMultiplier = 2;
                size = 1;
                health = 100;
                solid = false;
                fullOverride = this.name + "-full";
                itemCapacity = 40;
            }};
        //endregion
        //region power
        internal_energy_pile = new Battery("internal-energy-pile"){{
            requirements(Category.power,with(Items.silicon,20, MD_Items.al_alloy,15,Items.graphite,15));
            health = 300;
            armor = 2;
            solid = false;
            underBullets = false;
            enableDrawStatus = false;
            researchCost = with(Items.silicon,100, MD_Items.al_alloy,80,Items.graphite,50);
            consumePowerBuffered(6000f);
            size = 1;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawPower(),
                    new DrawAutotile()
                    );
        }};
        magnetic_node = new PowerNode("magnetic-node"){{
            requirements(Category.power, with(MD_Items.aluminium,7));
            maxNodes = 8;
            laserRange = 7;
            underBullets = true;
            crushFragile = true;
            researchCost = with( MD_Items.aluminium, 10);
            consume(new ConsumePower(1f/60f,300,true));
            enableDrawStatus = false;
        }};
        graphite_combustion_chamber = new ConsumeGenerator("graphite-combustion-chamber"){{
            requirements(Category.power, with(MD_Items.aluminium, 35, Items.graphite,35));
            powerProduction = 200/60f;
            itemDuration = 60f;
            size = 2;
            generateEffect = Fx.none;
            researchCostMultiplier = 0.05f;
            ambientSoundVolume = 0.06f;

            consumeItems(with(Items.graphite,1));
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion(),
                    new DrawJetFlame(),
                    new DrawRegion("-top")
            );
        }};

        composite_combustion = new ConsumeGenerator("composite-combustion"){{
            requirements(Category.power, with(MD_Items.al_alloy, 120, Items.graphite,200,Items.silicon,150, MD_Items.polymer,120));
            powerProduction = 1800/60f;
            itemDuration = 300f;
            size = 4;
            liquidCapacity = 200;
            itemCapacity = 20;
            generateEffect = Fx.none;
            researchCostMultiplier = 0.05f;
            ambientSoundVolume = 0.06f;
            consumeLiquids(LiquidStack.with(Liquids.hydrogen,10f/60f, MD_Liquids.crystallization_oil,36f/60f));
            consumeItems(with(MD_Items.polymer,4));
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(MD_Liquids.crystallization_oil,3f),
                    new DrawRegion(),
                    new DrawJetFlame(){{
                        amount = 8;
                        rotate = 0;
                        beginLen = 6.5f;
                        len = 17f;
                        stroke = 7f;
                        lenMul = 3f;
                        colorOut = Color.valueOf("CDE060");
                    }},
                    new DrawRegion("-mid"),
                    new DrawLiquidTile(Liquids.hydrogen,11f),
                    new DrawArcSmelt(),
                    new DrawRegion("-top")
            );
        }};
        //endregion
        //region payload
        small_payload_conveyor = new MD_PayloadConveyor("small-payload-conveyor",2){{
            requirements(Category.units,with());
            canOverdrive = false;
            payloadLimit = 2f;
            moveTime = 25f;
            moveForce = 200;
            health = 400;
        }};
        small_payload_router = new MD_PayloadRouter("small-payload-router",2){{
            requirements(Category.units,with());
            canOverdrive = false;
            payloadLimit = 2f;
            moveTime = 25;
            moveForce = 200;
            health = 400;
        }};
        payload_processing_platform = new PayloadPlatform("payload-processing-platform"){{
            requirements(Category.units,with(Items.graphite,50, MD_Items.aluminium,80));
            size = 4;
            regionSuffix = "-plat";
        }};
        //endregion
        //region ammo
        ammo_constructor = new Constructor("ammo-constructor"){{
            requirements(Category.turret, with(Items.silicon, 50, Items.graphite, 75, MD_Items.al_alloy, 40));
            regionSuffix = "-ammo";
            hasPower = true;
            buildSpeed = 0.6f;
            consumePower(2.5f);
            size = 3;
            filter = Seq.with(MD_blocks.heavy_ammo);

        }};
        infantry_factory = new UnitFactory("infantry-factory"){{
            requirements(Category.units, with(Items.silicon, 150, MD_Items.aluminium, 180, Items.graphite, 120));
            plans = Seq.with(
                    new UnitPlan(MD_UnitTypes.captive, 60f * 30, with(Items.silicon, 50, MD_Items.aluminium,55)),
                    new UnitPlan(MD_UnitTypes.mouse, 60f * 38, with(Items.silicon, 70, Items.copper,40, MD_Items.al_alloy,30))
            );
            size = 3;
            regionSuffix = "-ammo";
            consumePower(1.2f);
            researchCost = with(Items.silicon, 100, MD_Items.aluminium, 120, Items.graphite, 80);
        }};

        airborne_vessels_factory = new UnitFactory("airborne-vessels-factory"){{
            requirements(Category.units, with(Items.silicon, 170, MD_Items.polymer, 150, Items.copper, 120));
            plans = Seq.with(
                    new UnitPlan(MD_UnitTypes.shimmer, 60f * 28, with(Items.silicon, 45, MD_Items.polymer,70)),
                    new UnitPlan(MD_UnitTypes.lumen, 60f * 25, with(Items.silicon, 60,Items.copper,70))
            );
            size = 3;
            regionSuffix = "-ammo";
            consumePower(1.2f);
            researchCostMultiplier = 0.2f;
        }};
        anchor_radar = new AnchorRadar("anchor-radar"){{
            requirements(Category.units,BuildVisibility.sandboxOnly,with(Items.silicon,15,Items.graphite,10, MD_Items.al_alloy,5,Items.copper,10));
            radius = 8f*25;
            callUnitInterval = 150f;
            callEffect = new Effect(120f, e->{
                int amount = 4;
                for(int i=0;i<amount;i++){
                    float fin = e.fin()*amount;
                    fin -=i * 0.5f;
                    if(fin>1 || fin < 0)continue;
                    float sin = Mathf.sin(fin * Mathf.PI);
                    Draw.color(Color.white,sin * 0.8f);
                    Lines.stroke(0.9f * sin);
                    Lines.circle(e.x,e.y,fin * 14f);
                }
            }){{followParent=false;}};
        }};
        army_anchor_point_reconstructor = new RegionReconstructor("army-anchor-point-reconstructor"){{
            requirements(Category.units,BuildVisibility.sandboxOnly, with(Items.silicon, 150, MD_Items.al_alloy, 120, Items.graphite, 150));
            size = 3;
            craftTime = 60f*20;
            researchCostMultiplier = 0.2f;
            consumeItems(with(
                    Items.silicon,70, MD_Items.al_alloy,40
            ));
            consumePower(220f/60f);
            upgrades.addAll(
                    new UnitType[]{MD_UnitTypes.captive, MD_UnitTypes.zircon}
            );
        }};

        airforce_anchor_point_reconstructor = new RegionReconstructor("airforce-anchor-point-reconstructor"){{
            requirements(Category.units,BuildVisibility.sandboxOnly, with(Items.silicon, 150, MD_Items.al_alloy, 120, MD_Items.polymer, 100));
            size = 3;
            craftTime = 60f*30;
            researchCostMultiplier = 0.2f;
            consumeItems(with(
                    Items.silicon,50, MD_Items.polymer,40,Items.graphite,20
            ));
            consumePower(250f/60f);
            upgrades.addAll(
                    new UnitType[]{MD_UnitTypes.shimmer, MD_UnitTypes.firefly}
            );
        }};

        eigen_unit_assembler = new MD_UnitAssembler("eigen-unit-assembler"){{
            requirements(Category.units, with(Items.silicon, 150));
            regionSuffix = "-assembler";
            size = 3;
            plans.add(
                    new AssemblerUnitPlan(MD_UnitTypes.zircon, 60f * 35, PayloadStack.list(MD_UnitTypes.captive,1)){{
                        itemReq = with(Items.silicon,70,MD_Items.al_alloy,40);
                    }},
                    new AssemblerUnitPlan(MD_UnitTypes.coyote, 60f * 40, PayloadStack.list(MD_UnitTypes.mouse,1)){{
                        itemReq = with(Items.silicon,80,MD_Items.al_alloy,60);
                    }},
                    new AssemblerUnitPlan(MD_UnitTypes.firefly, 60f * 35, PayloadStack.list(MD_UnitTypes.shimmer,1)){{
                        itemReq = with(Items.silicon,70,MD_Items.polymer,40);
                    }}

            );
            areaSize = 5;
            researchCostMultiplier = 0.4f;

            consumePower(2.0f);
        }};

        shaping_assembler = new PayloadPlatformConstructor("shaping-assembler"){{
            requirements(Category.units, with(Items.silicon,120, MD_Items.al_alloy,80));
            size = 2;
            consumeItems(with(Items.silicon,2, MD_Items.al_alloy,1));
            consumePower(1.5f);
            addUpgrade(MD_UnitTypes.captive, MD_UnitTypes.zircon,30*60f);
            addUpgrade(MD_UnitTypes.shimmer, MD_UnitTypes.firefly,35*60f);
            addUpgrade(MD_UnitTypes.mouse, MD_UnitTypes.coyote,40*60f);
        }};
        //forging_assembler;
        //tempering_assembler;
        //polarization_assembler;
        //phase_assembler;

        //endregion

        Block test = new FluxCrafter("testBlock"){{
            requirements(Category.crafting,BuildVisibility.sandboxOnly,with());
            consume(new ConsumeFlux(){{
                produceAmount = 3/60f;
                capacity = 30f;
            }});
            craftTime = 30f;
            drawDisabled = true;
        }

        };
        Block test1 = new FluxNode("node"){{
            requirements(Category.power,BuildVisibility.sandboxOnly,with());
            size = 1;
            enableDrawStatus = false;
            drawDisabled = true;
        }};

        Block test11 = new PayloadPlatform("test11"){{
            requirements(Category.units,BuildVisibility.sandboxOnly,with());
            size = 5;
            regionSuffix = "-dark";
        }};

        Block test12 = new PayloadPlatformConstructor("test12"){{
            requirements(Category.units,BuildVisibility.sandboxOnly,with());
            addUpgrade(MD_UnitTypes.mouse, MD_UnitTypes.coyote,35*60);
            size = 2;
        }};

        Block test13 = new PayloadPlatformConstructor("test13"){{
            requirements(Category.units,BuildVisibility.sandboxOnly,with());
            addUpgrade(MD_UnitTypes.coyote, MD_UnitTypes.zircon,40*60);
            size = 2;
        }};

    }
    public static void loadAmmo(){
        heavy_ammo = new AmmoBlock("heavy-ammo"){{
            requirements = ItemStack.with(MD_Items.al_alloy,20, MD_Items.polymer,15);
            // Explosion properties
            baseExplosiveness = 20; // Base explosion power

            size = 1;
            buildCostMultiplier = 30f;
        }};
    }
}
