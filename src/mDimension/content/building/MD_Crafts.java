package mDimension.content.building;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mDimension.consumers.ConsumeBeam;
import mDimension.consumers.MultiRecipeConsume;
import mDimension.content.MD_Fx;
import mDimension.content.MD_Items;
import mDimension.content.MD_Liquids;
import mDimension.content.MD_beams;
import mDimension.draw.DrawPiston;
import mDimension.draw.DrawRotation;
import mDimension.world.blocks.beam.MD_BeamDeflector;
import mDimension.world.blocks.*;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;
import static mDimension.content.building.MD_blocks.*;
public class MD_Crafts {
    public static void load(){
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
        silicon_reaction_furnace = new MultiRecipeCrafter("silicon-reaction-furnace"){{
            requirements(Category.crafting, with(MD_Items.aluminium, 60, Items.graphite, 50 , MD_Items.germanium,50));
            craftTime = 120f;
            itemCapacity = 30;
            liquidCapacity = 100f;
            consumePower(3f);
            size = 3;
            researchCost = with(MD_Items.aluminium, 60, Items.graphite, 50 , MD_Items.germanium,50);
            craftEffect = Fx.none;
            consumeRecipes(new MultiRecipeConsume(
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.sand, 10);
                        consumeLiquids = LiquidStack.with(Liquids.hydrogen,12/60f);
                        outputItems = with(Items.silicon, 10);
                    }},
                    new MultiRecipeConsume.Recipe() {{
                        consumeItems = with(Items.sand, 9,MD_Items.aluminium,2);
                        outputItems = with(Items.silicon, 7);
                    }}
            ));

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.hydrogen,3f),
                    new DrawArcSmelt() {{
                        flameColor = c("CE85F2");
                        midColor = c("B086F5");
                    }},
                    new DrawRegion()
            );
        }};
        //region al_alloy_smelting 铝合金
        al_alloy_smelting = new GenericCrafter("al-alloy-smelting") {{
            squareSprite = false;
            itemCapacity = 30;
            size = 3;
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.aluminium, 80,
                    MD_Items.germanium, 40,
                    Items.silicon, 60
            ));
            craftEffect = new MultiEffect(
                    MD_Fx.craftEffectLight(40, 2.5f, 4f, c("D1F8FF"), 6, 1f),
                    MD_Fx.craftEffectLight(25, 1f, 3f, c("D1F8FF"), 6, 5f)
            );
            outputItem = new ItemStack(MD_Items.al_alloy, 4);
            consumeItems(ItemStack.with(
                    MD_Items.aluminium, 6,
                    MD_Items.germanium, 2
            ));
            consumePower(4f);
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(c("D1E4FF")) {{
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
        light_ceramic_wrapper = new GenericCrafter("light-ceramic-wrapper"){{
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.al_alloy,30,
                    Items.silicon,50,
                    MD_Items.polymer,30,
                    MD_Items.germanium,70
            ));
            squareSprite = false;
            itemCapacity = 20;
            consumeItem(Items.silicon, 1);
            consume(new ConsumeBeam(15, MD_beams.near_infrared_light));
            outputItem = new ItemStack(MD_Items.light_ceramic, 1);
            craftTime = 120f;
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.12f;
            consumePower(2.5f);
            size = 2;
            hasPower = true;
            hasLiquids = false;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawArcSmelt() {{
                        flameRad *= 0.57f;
                        circleSpace *= 0.57f;
                        flameRadiusScl *= 1.3f;
                        flameRadiusMag *= 0.5f;
                        circleStroke *= 0.7f;
                        particleRad = 5f;
                        flameColor = Color.valueOf("DBF549");
                        midColor = Color.valueOf("F2F285");
                    }},
                    new DrawRegion()
            );
        }};
        //region ti_alloy_smelting 钛合金
        ti_alloy_smelting = new GenericCrafter("ti-alloy-smelting") {{
            requirements(Category.crafting, ItemStack.with(
                    MD_Items.al_alloy, 120,
                    Items.titanium,150,
                    MD_Items.light_ceramic, 70,
                    Items.silicon, 120
            ));
            health = 500;
            armor = 3;
            size = 4;
            buildTime = 6f;
            consume(new ConsumeBeam(20, MD_beams.bright_light));
            consume(new ConsumeBeam(20, MD_beams.near_infrared_light));
            craftTime = 80f;
            itemCapacity = 30;
            liquidCapacity = 100f;
            consumeItems(ItemStack.with(Items.silicon, 3, Items.titanium, 12));
            consumeLiquid(MD_Liquids.helium, 3f / 60f);
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
                        color = c("FFF4DB");
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
                    MD_Items.germanium, 40
            ));
            health = 500;
            armor = 3;
            buildTime = 3f;
            liquidCapacity = 100f;
            consumeLiquid(Liquids.hydrogen, 6 / 60f);
            consume(new ConsumeBeam(5f,MD_beams.bright_light));
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
            craftEffect = MD_Fx.craftEffectLight(40, 2.5f, 3, c("FFAC99"), 5, 1f);
            craftTime = 120f;

            drawer = new DrawMulti(

                    new DrawRegion("-bottom"),
                    new DrawRegion(),
                    new DrawFlame(c("FFD3BD")) {{
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
            requirements(Category.crafting, BuildVisibility.sandboxOnly, with());
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
            requirements(Category.crafting, with(MD_Items.aluminium,10,MD_Items.germanium,15));
            size = 1;
            drawArrow = true;
            drawer = new DrawMulti(
                    new DrawRegion(),
                    new DrawRotation("-arrow", true)
            );
        }};

        diagonal_beam_merging_prism = new MD_BeamDeflector("diagonal-beam-merging-prism") {{
            requirements(Category.crafting, with(MD_Items.aluminium,10,MD_Items.germanium,15));
            afterRotation.set(1, 1);
            size = 1;
            diagonalFlip = true;
            drawArrow = true;
            drawer = new DrawMulti(
                    new DrawRegion(),
                    new DrawRotation("-arrow", 4)
            );
        }};

        beam_polarizing_filter = new MD_BeamDeflector("beam-polarizing-filter"){{
            requirements(Category.crafting, with(MD_Items.light_ceramic,10,MD_Items.germanium,15));
            guidance = false;
            polarizing = true;
            size = 1;
        }};

        beam_polarizing_prism = new MD_BeamDeflector("beam-polarizing-prism") {{
            requirements(Category.crafting, with(MD_Items.light_ceramic,15,MD_Items.germanium,20));
            size = 1;
            polarizing = true;
            drawArrow = true;
            drawer = new DrawMulti(
                    new DrawRegion(),
                    new DrawRotation("-arrow", true)
            );
        }};

        diagonal_beam_polarizing_prism = new MD_BeamDeflector("diagonal-beam-polarizing-prism") {{
            requirements(Category.crafting, with(MD_Items.light_ceramic,15,MD_Items.germanium,20));
            afterRotation.set(1, 1);
            size = 1;
            polarizing = true;
            diagonalFlip = true;
            drawArrow = true;
            drawer = new DrawMulti(
                    new DrawRegion(),
                    new DrawRotation("-arrow", 4)
            );
        }};
        //endregion
        ammonia_chamber = new GenericCrafter("ammonia-chamber") {{
            requirements(Category.crafting, with(Items.silicon, 30, MD_Items.aluminium, 50, MD_Items.germanium, 50));
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
                    new DrawCrucibleFlame(),
                    new DrawRegion(),
                    new DrawLiquidOutputs(),
                    new DrawGlowRegion() {{
                        alpha = 0.7f;
                        color = c("EFBDF3");
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
            researchCost = with(
                    MD_Items.aluminium, 200, MD_Items.al_alloy, 180, Items.silicon,150
            );
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
                        strokeFunction = (b, i) -> (float) ((Math.cos((b.totalProgress() % 150) / 150f * 2 * Math.PI) - 1) / 2);
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
        infrared_laser = new MD_BeamCrafter("infrared-laser") {{
            requirements(Category.crafting, with(Items.silicon,30,MD_Items.germanium,50));
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
            drawer = new DrawMulti(new DrawRegion(), new DrawRotation("-top", 4));
        }};
        ultraviolet_laser = new MD_BeamCrafter("ultraviolet-laser") {{
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
            drawer = new DrawMulti(new DrawRegion(), new DrawRotation("-top", 4));
        }};
        bright_light_laser = new MD_BeamCrafter("bright-light-laser") {{
            requirements(Category.crafting, with(Items.silicon,50,MD_Items.light_ceramic,15,MD_Items.germanium,80));
            craftPos = new Vec2[]{
                    new Vec2(4, -4),
                    new Vec2(-4, 4)
            };
            craftRotation = new Vec2[]{
                    new Vec2(1, 1),
                    new Vec2(1, 1)
            };
            consumePower(1f);
            craftTime = 330;
            consumeItem(MD_Items.light_ceramic);
            diagonalFilp = true;
            beam = MD_beams.bright_light;
            size = 2;
            beamPower = 10f;
            drawer = new DrawMulti(new DrawRegion(), new DrawRotation("-top", 4));
        }};

        nihility_exciter = new MD_BeamCrafter("nihility-exciter") {{
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
                    MD_Fx.brokenWave(27f, c("E0EAFF").a(0.85f), 5f, 4f, 1.6f, 6, 0.3f, 1.3f),
                    MD_Fx.brokenWave(27f, c("C9DBFF").a(0.85f), 7.5f, 6f, 1.2f, 8, 0.5f, 1.4f)

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
                    new DrawBubbles(c("7693e3")) {{
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
                        color = c("c4bdf3");
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
                        consumeItems = with(MD_Items.germanium, 8);
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
                        color = c("FFEF96");
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
                    MD_Fx.craftEffectLight(70f, 4.5f, 5f, c("FFEBA3"), 7, 1f),
                    MD_Fx.craftEffectLight(55f, 2f, 7f, c("FFEBA3"), 4, 6f)
            );

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(MD_Liquids.dimension_fluid, 5 / 4f),
                    new DrawRegion(),
                    new DrawRegion("-mid"),
                    new DrawCrucibleFlame() {
                        {
                            flameColor = c("F5D37C");
                            midColor = c("F2AD85");
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
    }
}
