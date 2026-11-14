package mDimension.content.building;

import mDimension.content.MD_Fx;
import mDimension.content.MD_Items;
import mDimension.content.MD_UnitTypes;
import mDimension.world.blocks.DumpStorageBlock;
import mDimension.world.blocks.MD_ElectricFieldCoreBlock;
import mDimension.world.blocks.MD_SpawnUnitCoreBlock;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.effect.MultiEffect;
import mindustry.type.Category;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;
import static mDimension.content.building.MD_blocks.*;
public class MD_Storages {
    public static void load(){
        //region storage
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
        }};

        coreEngineering = new MD_SpawnUnitCoreBlock("core-engineering"){{
            requirements(Category.effect, BuildVisibility.coreZoneOnly, with(Items.silicon, 1200, MD_Items.aluminium,1500,MD_Items.al_alloy,500));
            conductivePower = true;
            isFirstTier = true;
            researchCostMultiplier = 0.5f;
            unitType =  MD_UnitTypes.primitive;
            spawnUnitType = MD_UnitTypes.engineering_drone;
            unitAmount = 2;
            spawnRotate = 45f;
            offset = 12f;
            armor = 2;
            health = 3000;
            itemCapacity = 8000;
            buildCostMultiplier = 1.2f;
            thrusterLength = 34f/4f;
            unitCapModifier = 12;
            size = 4;
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
    }
}
