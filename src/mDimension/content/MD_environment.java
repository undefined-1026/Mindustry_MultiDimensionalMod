package mDimension.content;

import arc.graphics.Color;
import mDimension.world.blocks.environment.CrustFloor;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Shaders;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

import mDimension.meta.*;

public class MD_environment {
    public static Block
            crystallization_oil_deep,crystallization_oil,magnetic_shale_wall,magnetic_shale_stone,brownSandFloor,brownSandWall,
    light_shale_wall,pure_light_shale_wall,darkCrystallineStoneWall,darkRedStoneWall,germaniumStone,germaniumStoneWall,
            inlayMetalTiles4,darkCrystalFloor,darkCrystallineStone,darkDenseRedStone,darkRedStone,
            light_shale_floor,pure_light_shale_floor,yellow_ice,yellow_ice_wall,chlorite,chlorite_wall,yellow_ice_snow,depthTile1,depthTile2,depthTile3,depthTile4,depthTile5,
    //ore
    oreAluminium,WallOreTitanium,oreGraphite,oreGermanium,WallOreAluminium,oreChromium,WallOreCopper,
    test100,

    oreAmmonia,
    //props
    darkCrystallineBoulders,
    //vent
    dark_crystalline_stoneVent,light_stone_vent,yellow_ice_vent,dark_red_stone_vent,magnetic_shale_stone_vent;
    public static CacheLayer
            crystallization_deep,crystallization
    ;
    public static void load(){
        loadCacheLayer();
        crystallization_oil_deep = new Floor("crystallization-oil-deep"){{
            liquidDrop = MD_Liquids.crystallization_oil;
            cacheLayer = crystallization_deep;
            supportsOverlay = true;
            overlayAlpha = 0.6f;
            drownTime = 80f;
            status = StatusEffects.tarred;
            statusDuration = 150f;
            speedMultiplier = 0.9f;
            variants = 0;
            liquidMultiplier = 1.5f;
            isLiquid = true;
        }};

        crystallization_oil = new Floor("crystallization-oil-floor"){{
            liquidMultiplier = 0.5f;
            variants = 3;
            liquidDrop = MD_Liquids.crystallization_oil;
            cacheLayer = crystallization_deep;
            supportsOverlay = true;
            overlayAlpha = 0.6f;
            drownTime = 0;
            status = StatusEffects.tarred;
            statusDuration = 150f;
            speedMultiplier = 0.9f;
            liquidMultiplier = 1f;
            isLiquid = true;

        }};

        brownSandFloor = new Floor("brown-sand-floor"){{
            itemDrop = Items.sand;
            attributes.set(Attribute.oil,1.2f);
        }};

        brownSandWall = new StaticWall("brown-sand-wall"){{
            brownSandFloor.asFloor().wall = this;
            attributes.set(Attribute.sand, 2f);
        }};

        magnetic_shale_stone = new Floor("magnetic-shale-stone"){{
            attributes.set(Attribute.water, -0.2f);
        }};

        magnetic_shale_wall = new StaticWall("magnetic-shale-wall"){{
            attributes.set(Attribute.sand, 0.7f);
            magnetic_shale_stone.asFloor().wall = this;
        }};


        oreAluminium = new OreBlock("ore-aluminium", MD_Items.aluminium){{
            oreDefault = true;
            oreThreshold = 0.846f;
            oreScale = 24.428572f;
        }};

        WallOreAluminium = new OreBlock("ore-wall-aluminium", MD_Items.aluminium){{
            wallOre = true;
            oreThreshold = 0.846f;
            oreScale = 24.428572f;
        }};

        WallOreTitanium = new OreBlock("ore-wall-titanium",Items.titanium){{
            wallOre = true;
        }};

        oreGraphite = new OreBlock("ore-graphite",Items.graphite){{
            oreDefault = true;
            oreThreshold = 0.846f;
            oreScale = 24.428572f;
        }};

        WallOreCopper = new OreBlock("ore-wall-copper",Items.copper){{
            wallOre = true;
        }};

        oreGermanium = new OreBlock("ore-germanium", MD_Items.germanium){{
            oreThreshold = 0.846f;
            oreScale = 24.428572f;
        }};

        oreChromium = new OreBlock("ore-chromium", MD_Items.chromium){{
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 25.380953f;
        }};



        light_shale_floor = new Floor("light-stone-floor"){{
            emitLight = true;
            lightRadius = 12f;
            lightColor = Color.valueOf("AFB88B").a(0.2f);
        }};
        pure_light_shale_floor = new Floor("pure-light-stone-floor"){{
            emitLight = true;
            lightRadius = 30f;
            lightColor = Color.valueOf("E0E8C0").a(0.6f);
        }};

        light_shale_wall = new StaticWall("light-stone-wall"){{
            light_shale_floor.asFloor().wall = this;
        }};
        pure_light_shale_wall = new StaticWall("pure-light-stone-wall"){{
            pure_light_shale_floor.asFloor().wall = this;
        }};

        inlayMetalTiles4 = new Floor("inlay-metal-tiles-1"){{
            autotile = true;
            drawEdgeOut = false;
            drawEdgeIn = false;
            mapColor = Color.valueOf("FFDB82");
            this.asFloor().wall = Blocks.metalWall3;
        }};

        depthTile1 = new Floor("depth-tile-1"){{
            autotile = true;
            drawEdgeOut = false;
            drawEdgeIn = false;
            this.asFloor().wall = Blocks.metalWall3;
        }};

        depthTile2 = new Floor("depth-tile-2"){{
            autotile = true;
            drawEdgeOut = false;
            drawEdgeIn = false;
            this.asFloor().wall = Blocks.metalWall3;
        }};

        depthTile3 = new Floor("depth-tile-3"){{
            autotile = true;
            drawEdgeOut = false;
            drawEdgeIn = false;
            this.asFloor().wall = Blocks.metalWall3;
        }};

        depthTile4 = new Floor("depth-tile-4"){{
            autotile = true;
            drawEdgeOut = false;
            drawEdgeIn = false;
            this.asFloor().wall = Blocks.metalWall3;
        }};

        depthTile5 = new Floor("depth-tile-5"){{
            autotile = true;
            drawEdgeOut = false;
            drawEdgeIn = false;
            this.asFloor().wall = Blocks.metalWall3;
        }};


        darkCrystalFloor = new Floor("dark-crystal-floor"){{
            variants = 4;
            mapColor = Color.valueOf("292328");
        }};

        darkCrystallineStone = new Floor("dark-crystalline-stone"){{
            variants = 5;
            mapColor = Color.valueOf("2E1A20");
        }};

        darkCrystallineStoneWall = new StaticWall("dark-crystalline-stone-wall"){{
            variants = 4;
            darkCrystallineStone.asFloor().wall = darkCrystalFloor.asFloor().wall = this;
            mapColor = Color.valueOf("402C32");
        }};

        darkRedStone = new Floor("dark-red-stone"){{
            mapColor = Color.valueOf("857240");
            variants=4;
        }};
        darkDenseRedStone = new Floor("dark-dense-red-stone"){{
            mapColor = Color.valueOf("8A753E");
            variants=4;
        }};
        darkRedStoneWall = new StaticWall("dark-red-stone-wall"){{
            darkDenseRedStone.asFloor().wall = darkRedStone.asFloor().wall = this;
        }};
        germaniumStone = new Floor("germanium-stone"){{

        }};
        germaniumStoneWall = new StaticWall("germanium-stone-wall"){{
            germaniumStone.asFloor().wall= this;
        }};
        yellow_ice = new Floor("yellow-ice"){{
            variants = 3;
            attributes.set(Attribute.water, 1.5f);
        }};

        yellow_ice_wall = new StaticWall("yellow-ice-wall"){{
            yellow_ice.asFloor().wall = darkRedStone.asFloor().wall = this;
        }};

        chlorite = new Floor("chlorite"){{
            variants = 3;
        }};

        chlorite_wall = new StaticWall("chlorite-wall"){{
            chlorite.asFloor().wall = darkRedStone.asFloor().wall = this;
        }};

        yellow_ice_snow = new Floor("yellow-ice-snow"){{
            variants = 3;
            attributes.set(Attribute.water, 1.5f);
        }};


        darkCrystallineBoulders = new Prop("dark-crystalline-boulders"){{
            variants = 3;
            darkCrystalFloor.asFloor().decoration = this;
        }};

        dark_crystalline_stoneVent = new SteamVent("dark-crystalline-stone-vent"){{
            parent = blendGroup = darkCrystalFloor;
            effectColor = Color.valueOf("FFE8A1").a(0.7f);
            attributes.set(MD_Attribute.ammonia, 1f);
        }};

        light_stone_vent = new SteamVent("light-stone-vent"){{
            parent = blendGroup = light_shale_floor;
            effectColor = Color.valueOf("FFE8A1").a(0.7f);
            attributes.set(MD_Attribute.ammonia, 1f);
        }};

        yellow_ice_vent = new SteamVent("yellow-ice-vent"){{
            parent = blendGroup = yellow_ice;
            effectColor = Color.valueOf("FFE8A1").a(0.7f);
            attributes.set(MD_Attribute.ammonia, 1f);
        }};

        dark_red_stone_vent = new SteamVent("dark-red-stone-vent"){{
            parent = blendGroup = darkRedStone;
            effectColor = Color.valueOf("FFE8A1").a(0.7f);
            attributes.set(MD_Attribute.ammonia, 1f);
        }};

        magnetic_shale_stone_vent = new SteamVent("magnetic-shale-stone-vent"){{
            parent = blendGroup = magnetic_shale_stone;
            effectColor = Color.valueOf("FFE8A1").a(0.7f);
            attributes.set(MD_Attribute.ammonia, 1f);
        }};

        test100 = new CrustFloor("test100"){{
            strata = with(
                    new Stratum(1,2,Items.sand,3),
                    new Stratum(3,4, MD_Liquids.helium,0.05f),
                    new Stratum(5,6, MD_Liquids.dimension_fluid,0.2f)
            );
        }};

    }

    public static void loadCacheLayer(){
        crystallization_deep = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader("crystallization-oil-deep"));
        CacheLayer.add(crystallization_deep);
//        crystallization = new CacheLayer.ShaderLayer(new Shaders.SurfaceShader("crystallization-oil"));
//        CacheLayer.add(crystallization);
    }
}