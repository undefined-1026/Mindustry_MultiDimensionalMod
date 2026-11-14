package mDimension.content.building;

import mDimension.content.MD_Items;
import mDimension.content.MD_Liquids;
import mDimension.draw.DrawAutotile;
import mDimension.draw.DrawJetFlame;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.draw.*;

import static mindustry.type.ItemStack.with;
import static mDimension.content.building.MD_blocks.*;
public class MD_Powers {
    public static void load(){
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
            solid = false;
            researchCost = with( MD_Items.aluminium, 10);
            enableDrawStatus = false;
        }};
        large_magnetic_node = new PowerNode("large-magnetic-node"){{
            requirements(Category.power, with(MD_Items.aluminium,15,MD_Items.polymer,10,Items.silicon,10));
            maxNodes = 12;
            laserRange = 18;
            underBullets = true;
            crushFragile = true;
            solid = false;
            enableDrawStatus = false;
            buildCostMultiplier = 0.5f;
            size=2;
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
                        colorOut = c("CDE060");
                    }},
                    new DrawRegion("-mid"),
                    new DrawLiquidTile(Liquids.hydrogen,11f),
                    new DrawArcSmelt(),
                    new DrawRegion("-top")
            );
        }};
        //endregion
    }
}
