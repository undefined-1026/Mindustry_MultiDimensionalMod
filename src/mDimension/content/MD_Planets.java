package mDimension.content;

import arc.graphics.Color;
import mDimension.plante.DepicilonPlanetGenerator;
import mDimension.world.ExtendedPlanet;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;
import mindustry.content.Planets;
import mindustry.world.meta.Env;

public class MD_Planets {
    public static Planet depicilon;
    public static void load(){
        depicilon = new ExtendedPlanet("depicilon",Planets.sun,1f,3){{
            loadPlanetData = true;
            orbitRadius = 75;
            defaultEnv = Env.terrestrial;
            defaultCore = MD_blocks.coreSteady;
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.coreDestroyClear = true;

                r.env = defaultEnv;
                r.borderDarkness = false;
            };
            generator = new DepicilonPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 48, 2.2f, 0.10f, 5, new Color().set(Color.valueOf("a8a040")).mul(0.9f).a(0.9f), 4, 0.40f, 1.3f, 0.40f),
                    new HexSkyMesh(this, 15, -1.3f, 0.16f, 5, new Color().set(Color.valueOf("a8a040")).mul(0.9f).a(0.80f), 3, 0.46f, 0.9f, 0.35f),
                    new HexSkyMesh(this, 25, -5f, 0.22f, 5, Color.white.cpy().lerp(Color.valueOf("a8a040"), 0.55f).a(0.50f), 5, 0.41f, 1.2f, 0.22f)
            );
            launchCapacityMultiplier = 0.15f;
            sectorSeed = 1145;

            allowSectorInvasion = false;
            allowLaunchSchematics = true;
            allowLaunchLoadout = true;

            enemyCoreSpawnReplace = true;

            showRtsAIRule = true;
            prebuildBase = true;
            iconColor = Color.valueOf("FFF5B2");
            hasAtmosphere = true;
            atmosphereColor = Color.valueOf("FCCD70");
            atmosphereRadIn = 0.01f;
            atmosphereRadOut = 0.3f;

            updateLighting = true;
            //TODO 还没做完生成器
            allowLaunchToNumbered = false;
            lightSrcFrom = 0.4f;
            lightSrcTo = 0.85f;
            lightDstFrom = 0.3f;
            lightDstTo = 1f;
            lightColor = Color.valueOf("f8fa95");


            bloom = false;



            startSector = 0;
            alwaysUnlocked = true;
            allowSelfSectorLaunch = true;
            landCloudColor = Pal.spore.cpy().a(0.65f);
            maxZoom = 5f;
            minZoom = 0.35f;
            rings.addAll(
                    new PlanetRing(2.3f,2.5f,Color.valueOf("B59B61"),40f,30f){{alphaOut = 1f;alphaIn =0.5f;}},
                    new PlanetRing(2.5f,3.1f,Color.valueOf("F5DC90"),40f,30f),
                    new PlanetRing(3.2f,3.6f,Color.valueOf("C7C166"),40f,30f){{alphaOut = 0.75f;}},
                    new PlanetRing(3.68f,3.85f,Color.valueOf("87812B"),40f,30f),

                    new PlanetRing(3.1f,3.2f,Color.valueOf("C99B74"),40f,30f){{alphaOut = 0.28f;alphaIn =0.35f;}},
                    new PlanetRing(3.6f,3.68f,Color.valueOf("C99B74"),40f,30f){{alphaOut = 0.13f;alphaIn =0.2f;}},
                    new PlanetRing(3.85f,4.25f,Color.valueOf("C99B74"),40f,30f){{alphaOut = 0f;alphaIn =0.1f;}}
            );

            auroras.addAll(
                    new PlanetAurora(Color.valueOf("62DE78"),35f,25f,10)
            );

        }};
    }


}
