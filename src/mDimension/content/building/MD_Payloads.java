package mDimension.content.building;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import mDimension.content.MD_Items;
import mDimension.content.MD_UnitTypes;
import mDimension.world.blocks.payload.*;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.type.Category;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;
import static mDimension.content.building.MD_blocks.*;
public class MD_Payloads {
    public static void load(){
        //region payload
        ammo_constructor = new Constructor("ammo-constructor"){{
            requirements(Category.turret, with(Items.silicon, 50, Items.graphite, 75, MD_Items.al_alloy, 40));
            regionSuffix = "-ammo";
            hasPower = true;
            buildSpeed = 0.6f;
            consumePower(2.5f);
            size = 3;
            filter = Seq.with(MD_blocks.heavy_ammo);

        }};


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
        eigen_factory = new UnitFactory("infantry-factory"){{
            requirements(Category.units, with(Items.silicon, 150, MD_Items.aluminium, 180, Items.graphite, 120));
            plans = Seq.with(
                    new UnitPlan(MD_UnitTypes.captive, 60f * 30, with(Items.silicon, 50, MD_Items.aluminium,55)),
                    new UnitPlan(MD_UnitTypes.mouse, 60f * 38, with(Items.silicon, 70, MD_Items.germanium,40, MD_Items.al_alloy,30)),
                    new UnitPlan(MD_UnitTypes.shimmer, 60f * 28, with(Items.silicon, 45, MD_Items.polymer,70))
            );
            size = 3;
            regionSuffix = "-ammo";
            consumePower(1.2f);
            researchCost = with(Items.silicon, 100, MD_Items.aluminium, 120, Items.graphite, 80);
        }};

        phase_factory = new UnitFactory("airborne-vessels-factory"){{
            requirements(Category.units, with(Items.silicon, 170, MD_Items.polymer, 150, MD_Items.germanium, 120));
            plans = Seq.with(
                    new UnitPlan(MD_UnitTypes.lumen, 60f * 25, with(Items.silicon, 60,MD_Items.germanium,70))
            );
            size = 3;
            regionSuffix = "-ammo";
            consumePower(1.2f);
            researchCostMultiplier = 0.2f;
        }};
        anchor_radar = new AnchorRadar("anchor-radar"){{
            requirements(Category.units, BuildVisibility.sandboxOnly,with(Items.silicon,15,Items.graphite,10, MD_Items.al_alloy,5,MD_Items.germanium,10));
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
            requirements(Category.units, with(Items.silicon, 150,MD_Items.polymer,80,MD_Items.germanium,120));
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
            plans.add(
                    setTier(1
                            ,new AssemblerUnitPlan(MD_UnitTypes.sin, 60f * 48, PayloadStack.list(MD_UnitTypes.captive,4)){{
                                itemReq = with(MD_Items.light_ceramic,30,MD_Items.chromium,100);
                            }},
                            new AssemblerUnitPlan(MD_UnitTypes.adjudge, 60f * 40, PayloadStack.list(MD_UnitTypes.mouse,3)){{
                                itemReq = with(MD_Items.light_ceramic,60,MD_Items.chromium,120);
                            }},
                            new AssemblerUnitPlan(MD_UnitTypes.pyrolume, 60f * 45, PayloadStack.list(MD_UnitTypes.shimmer,3)){{
                                itemReq = with(MD_Items.light_ceramic,40,MD_Items.chromium,80);
                            }})

            );
            areaSize = 7;
            researchCost = with(Items.silicon, 150,MD_Items.polymer,80,MD_Items.germanium,120);

            consumePower(2.0f);
        }};
        airborne_unit_assembler = new MD_UnitAssembler("airborne-unit-assembler"){{
            requirements(Category.units, with(Items.silicon, 180,MD_Items.light_ceramic,50,MD_Items.germanium,100));
            regionSuffix = "-assembler";
            size = 3;
            plans.add(
                    new AssemblerUnitPlan(MD_UnitTypes.mist, 60f * 28, PayloadStack.list(MD_UnitTypes.lumen,2)){{
                        itemReq = with(MD_Items.light_ceramic,20);
                    }},
                    setTier(1,new AssemblerUnitPlan(MD_UnitTypes.floating, 60f * 42, PayloadStack.list(MD_UnitTypes.lumen,5)){{
                        itemReq = with(MD_Items.polymer,80,MD_Items.chromium,80);
                    }})

            );
            areaSize = 7;
            researchCost = with(Items.silicon, 150,MD_Items.polymer,80,MD_Items.germanium,120);

            consumePower(2.0f);
        }};
        sharpen_assembler_module = new MD_UnitAssemblerModule("sharpen-assembler-module"){{
            requirements(Category.units, with(Items.silicon, 200, MD_Items.light_ceramic, 80, MD_Items.chromium, 180, MD_Items.al_alloy, 150));
            fitAssembler.addAll(
                    eigen_unit_assembler,airborne_unit_assembler
            );
            consumePower(2.5f);
            size = 3;
            tier = 1;
            regionSuffix = "-assembler";
        }};

        shaping_assembler = new PayloadPlatformConstructor("shaping-assembler"){{
            requirements(Category.units,BuildVisibility.sandboxOnly, with(Items.silicon,120, MD_Items.al_alloy,80));
            size = 2;
            consumeItems(with(Items.silicon,2, MD_Items.al_alloy,1));
            consumePower(1.5f);
            addUpgrade(MD_UnitTypes.captive, MD_UnitTypes.zircon,30*60f);
            addUpgrade(MD_UnitTypes.shimmer, MD_UnitTypes.firefly,35*60f);
            addUpgrade(MD_UnitTypes.mouse, MD_UnitTypes.coyote,40*60f);
        }};
        //endregion
    }
}
