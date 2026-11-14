package mDimension.content.building;

import arc.func.Cons2;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import mDimension.consumers.ConsumeFlux;
import mDimension.content.*;
import mDimension.world.blocks.*;
import mDimension.world.blocks.flux.FluxCrafter;
import mDimension.world.blocks.flux.FluxNode;
import mDimension.world.blocks.payload.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.with;

public class MD_blocks {
    public static final String modname = "mdimension-";
    //region defined
    public static Block
            small_silicon_arc_furnace,silicon_reaction_furnace, aluminium_electrolysis_cell, al_alloy_smelting,

    infrared_laser, ultraviolet_laser,bright_light_laser, nihility_exciter,

    ngm_launch_pad,
            ti_alloy_smelting, helium_factory, test2,
    //beam
    beam_merging_prism,diagonal_beam_merging_prism,beam_polarizing_filter,
            beam_polarizing_prism,diagonal_beam_polarizing_prism,

            light_ceramic_wrapper,
            water_pyrolyzer, carbon_fibre_binder, heavy_pulverizer, polymer_compressor, phase_adder, ammonia_chamber,
    //distribution
    light_junction,
            multiway_unloader, light_duct_bridge, shunt_router,
            light_sorter, light_invertedSorter, light_overflowGate, light_underflowGate, light_duct, armored_light_duct, stack_rail_conveyor,

    moving_node,
    //liquid
    siphon_pump,  fluid_unloader,fluid_conduit_bridge,directional_fluid_router,fluid_junction,fluid_conduit,fluid_container,
            deep_water_extractor,
    //drill
    beam_bore,small_cliff_crusher,small_impact_drill,electric_impact_drill,ammonia_collector,crustal_drill,  drilling_casing_module,

    planter,harvestingArm,
    //ammo
    heavy_ammo,
    //turret
    ionize,crack,ejection,rays, fracture,break_water,dawn,fluffrain,polarization,
            grudge,refraction,crest,test4,test5,
    //wall
    aluminium_wall,aluminium_wall_large,al_alloy_wall,al_alloy_wall_large,
    //core
    coreSteady,coreEngineering,proof_container,stack,
    //power
    internal_energy_pile,magnetic_node,large_magnetic_node,graphite_combustion_chamber,composite_combustion,
    //payload

    small_payload_conveyor,
            small_payload_router,payload_processing_platform,
            test3,ammo_constructor,
    //unit
    eigen_factory, phase_factory,

    shaping_assembler,
            eigen_unit_assembler, airborne_unit_assembler,
            forging_assembler,
            tempering_assembler,
            polarization_assembler,

    sharpen_assembler_module,

    anchor_radar,
            army_anchor_point_reconstructor,
            airforce_anchor_point_reconstructor
                    ;
    //endregion
    public static void load() {
        loadAmmo();
        MD_Crafts.load();
        MD_Drills.load();
        MD_Distributions.load();
        MD_LiquidBuilds.load();
        MD_Turrets.load();
        MD_Walls.load();
        MD_Storages.load();
        MD_Powers.load();
        MD_Payloads.load();
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

    public static Color c(String hex){
        return Color.valueOf(hex);
    }
    public static void setDefault(BulletType b){
        b.hitEffect = MD_Fx.hitBulletColor(5f,8,10f);
        b.despawnEffect = MD_Fx.hitBulletColor(12,10,18);
        b.shootEffect = MD_Fx.diffusionShoot;
        b.smokeEffect = Fx.none;
    }
    public static <K,V> void setMapVal(ObjectMap<K,V> map, Cons2<K,V> cons){
        for(var e:map.entries()){
            cons.get(e.key,e.value);
        }
    }
}
