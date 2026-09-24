package mDimension.content;

import arc.struct.Seq;

import static mDimension.content.MD_Items.*;
import static mDimension.content.MD_Liquids.*;
import static mDimension.content.MD_blocks.*;
import static mDimension.content.MD_UnitTypes.*;
import static mDimension.content.MD_SectorPresets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.game.Objectives.*;

import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;


public class MD_TechTree {
    private static TechTree.TechNode context = null;
    public static Seq<TechTree.TechNode> all = new Seq<>();
    public static Seq<TechTree.TechNode> roots = new Seq<>();



    public static void load(){
        MD_Planets.depicilon.techTree = nodeRoot("depicilon", MD_blocks.coreSteady,()->{
            node(MD_blocks.light_duct,()->{
                node(MD_blocks.armored_light_duct,()->{
                    node(MD_blocks.stack_rail_conveyor);
                });
                node(MD_blocks.multiway_unloader);
                node(MD_blocks.light_duct_bridge);
                node(shunt_router,()->{
                    node(light_sorter,()->{
                        node(light_invertedSorter);
                    });
                    node(light_overflowGate,()->{
                        node(light_underflowGate);
                    });
                    node(light_junction);
                });
                node(proof_container,()->{
                    node(stack);
                    node(moving_node,Seq.with(new Objectives.SectorComplete(marginal_outpost)));
                });
            });
            node(silicon_reaction_furnace,()->{
                node(MD_blocks.al_alloy_smelting,()->{
                    node(ammonia_chamber,Seq.with(new Objectives.OnSector(marginal_outpost)),()->{
                        node(polymer_compressor);
                    });
                });
                node(light_ceramic_wrapper,()->{
                    node(infrared_laser,()-> {
                        node(beam_merging_prism,()->{
                            node(diagonal_beam_merging_prism);
                        });
                    });
                });
            });
            node(fluid_conduit,Seq.with(new Research(ammonia)),()->{
                node(directional_fluid_router,()->{
                    node(fluid_junction);
                });
                node(fluid_conduit_bridge,()->{
                    node(fluid_unloader);
                });
                node(fluid_container);

                node(siphon_pump);
            });
            node(small_impact_drill,()->{
                node(small_cliff_crusher);
                node(heavy_pulverizer,()->{
                });
                node(electric_impact_drill);
                node(beam_bore,()->{
                    node(ammonia_collector,Seq.with(new OnSector(marginal_outpost)));
                });
            });
            node(crack,()->{
                node(fracture,()->{
                    node(grudge,Seq.with(new Objectives.OnSector(marginal_outpost)));
                });
                node(ionize);
                node(rays,()->node(ejection));
            });
            node(aluminium_wall,()->{
                node(aluminium_wall_large,()->{
                    node(al_alloy_wall,()->{
                        node(al_alloy_wall_large);
                    });
                });
            });
            node(magnetic_node,()->{
                node(graphite_combustion_chamber,()->{
                    node(composite_combustion);
                });
                node(internal_energy_pile);
            });



            nodeProduce(sand,()->{
                nodeProduce(aluminium,()->{
                    nodeProduce(silicon,()->{
                        nodeProduce(al_alloy,()->{
                            nodeProduce(polymer);
                        });
                    });
                });
                nodeProduce(titanium,()->{
                    nodeProduce(ti_alloy,()->{
                        nodeProduce(polymorphic_crystal);
                        nodeProduce(plasma);
                    });
                });
                nodeProduce(germanium);
                nodeProduce(graphite);

                nodeProduce(ammonia,()->{
                    nodeProduce(crystallization_oil);
                    nodeProduce(hydrogen);
                    nodeProduce(nitrogen);
                });
            });
            nodeProduce(MD_beams.near_infrared_light,()->{
                nodeProduce(MD_beams.bright_light);
            });
            node(eigen_factory,Seq.with(new SectorComplete(starting_point)),()->{
                node(phase_factory,()->{
                    node(lumen,ItemStack.with(germanium,150,silicon,200),()->{});
                });

                node(captive,ItemStack.with(),()->{
                    node(zircon,ItemStack.with(silicon,2000,al_alloy,2000),()->{});
                    node(mouse,ItemStack.with(silicon,2500,germanium,2500,graphite,2500),()->{
                        node(coyote);
                    });
                });
                node(shimmer,ItemStack.with(polymer,50,silicon,100),()->{
                    node(firefly,ItemStack.with(polymer,1200,silicon,1200,al_alloy,1200),()->{
                        node(pyrolume,
                                ItemStack.with(silicon,12000,titanium,8000,light_ceramic,4000,al_alloy,6000),
                                Seq.with(new Research(titanium)),
                                ()->{

                        });
                    });
                });

                node(eigen_unit_assembler);
            });

            node(starting_point,()->{
                node(marginal_outpost,Seq.with(
                        new SectorComplete(starting_point),
                        new Research(eigen_factory),
                        new Research(al_alloy)),()->{
                    node(halo_canyon,Seq.with(
                            new SectorComplete(marginal_outpost),
                            new Research(ammonia_collector),
                            new Research(grudge)
                    ));

                });

            });

            node(coreEngineering);
        });
    }

    public static void addToNext(UnlockableContent content,Runnable run){
        context = TechTree.all.find(t->t.content == content);
        run.run();
    }

    public static TechTree.TechNode nodeRoot(String name, UnlockableContent content, Runnable children){
        return nodeRoot(name, content, false, children);
    }

    public static TechTree.TechNode nodeRoot(String name, UnlockableContent content, boolean requireUnlock, Runnable children){
        var root = node(content, content.researchRequirements(), children);
        root.name = name;
        root.requiresUnlock = requireUnlock;
        roots.add(root);
        return root;
    }
    public static TechTree.TechNode node(UnlockableContent content,Seq<Objectives.Objective>objectives){
        return node(content, content.researchRequirements(),objectives,()->{});
    }
    public static TechTree.TechNode node(UnlockableContent content, Runnable children){
        return node(content, content.researchRequirements(), children);
    }

    public static TechTree.TechNode node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        return node(content, requirements, null, children);
    }

    public static TechTree.TechNode node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children){
        TechTree.TechNode node = new TechTree.TechNode(context, content, requirements);
        if(objectives != null){
            node.objectives.addAll(objectives);
        }

        //insert missing sector parent dependencies
        if(context != null && context.content instanceof SectorPreset preset && !node.objectives.contains(o -> o instanceof Objectives.SectorComplete sc && sc.preset == preset)){
            node.objectives.insert(0, new Objectives.SectorComplete(preset));
        }

        TechTree.TechNode prev = context;
        context = node;
        children.run();
        context = prev;

        return node;
    }

    public static TechTree.TechNode node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children){
        return node(content, content.researchRequirements(), objectives, children);
    }

    public static TechTree.TechNode node(UnlockableContent block){
        return node(block, () -> {});
    }

    public static TechTree.TechNode nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children){
        return node(content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), children);
    }

    public static TechTree.TechNode nodeProduce(UnlockableContent content, Runnable children){
        return nodeProduce(content, new Seq<>(), children);
    }

    public static TechTree.TechNode nodeProduce(UnlockableContent content){
        return nodeProduce(content, new Seq<>(),()->{});
    }
}
