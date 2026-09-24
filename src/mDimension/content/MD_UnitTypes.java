package mDimension.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mDimension.ai.SeedingAI;
import mDimension.ai.TransportAI;
import mDimension.entity.ability.AccelerateAbility;
import mDimension.entity.ability.PatienceAbility;
import mDimension.entity.bullet.BallLightningBulletType;
import mDimension.entity.bullet.RefractedLaserBulletType;
import mDimension.tool.Drawff;
import mDimension.world.blocks.DepicilonUnitType;
import mDimension.world.weapons.BoostWeapon;
import mDimension.world.weapons.CanBuildingBuildWeapon;
import mDimension.world.weapons.DestoryWeapon;
import mDimension.world.weapons.OverdriveWeapon;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CargoAI;
import mindustry.ai.types.FlyingFollowAI;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.HoverPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootHelix;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mDimension.content.MD_blocks.setDefault;
import static mindustry.Vars.tilesize;

import static mDimension.content.MD_blocks.modname;
import mDimension.world.weapons.*;
import mindustry.type.unit.ErekirUnitType;

public class MD_UnitTypes {

    public static UnitType captive , zircon;
    public static UnitType mouse,coyote;
    public static UnitType shimmer , firefly,pyrolume,burst;
    public static UnitType lumen,floating;
    //coreUnit
    public static UnitType primitive;

    //?
    public static UnitType laborers,farmer,engineering_drone;


    public static void load(){
        captive = new DepicilonUnitType("captive"){{
            constructor = MechUnit::create;

            canBoost = true;
            boostMultiplier = 1.2f;
            riseSpeed = descentSpeed = 0.02f;

            researchCostMultiplier = 0.5f;
            legForwardScl = 0.8f;
            legLengthScl = 0.8f;
            legMaxLength = 1.2f;
            legMoveSpace = 0.8f;
            legBaseUnder = true;
            legLength = 2f;
            mechLegColor = Color.valueOf("4C4E5E");
            speed = 0.6f;
            hitSize = 10f;
            armor = 3f;
            health = 350;
            stepSoundVolume = 0.4f;
            abilities.add(
                    new PatienceAbility(0.3f){{
                        armor = 4;
                        damage = 2.5f;
                        drawHeat = true;
                        heatColor = Color.valueOf("E8FFFE");
                        suffix = "-p";
                    }}
            );

            weapons.add(new Weapon(modname+"captive-weapon"){{
                reload = 30;
                x = 5.25f;
                y = 0.75f;
                recoil = 1.0f;
                top = false;
                ejectEffect = Fx.casing1;
                shoot = new ShootBarrel(){{
                    shots = 2;
                    shotDelay = 4f;
                }};
                bullet = new BasicBulletType(2.5f, 9){{
                    recoil = 0.18f;
                    spin = 5f;
                    hitColor=trailColor = backColor = Color.valueOf("B2E9FF");
                    trailLength = 8;
                    trailWidth = 1.5f;
                    trailSinMag = 0.15f;
                    trailSinScl = 5f;
                    width = 6.5f;
                    height = 10f;
                    lifetime = 50f;
                    despawnEffect = MD_Fx.hitBulletColor(4f,3,12);
                    hitEffect = MD_Fx.hitBulletColor(6f,5,18);

                }};
            }});
            weapons.add(new Weapon(modname+"captive-weapon1"){{
                reload = 70;
                x = 13/4f;
                y = -0.5f;
                recoil = 0.7f;
                top = false;
                rotate = true;
                rotateSpeed = 120/60f;
                ejectEffect = Fx.none;
                shootSound = Sounds.shootScepterSecondary;
                shootSoundVolume = 1.2f;
                soundPitchMin = 0.6f;
                soundPitchMax = 0.8f;
                bullet = new ShrapnelBulletType(){{
                    pierceBuilding = false;
                    pierce = true;
                    pierceCap = 3;
                    serrationSpaceOffset = 50f;
                    damage = 30f;
                    recoil = 0;
                    length = 15*8f;
                    hitColor=trailColor = toColor = Color.valueOf("B2E9FF");
                    width = 4.3f;
                    hitEffect = MD_Fx.hitBulletColor(6f,5,18);

                }};
            }});

        }};
        zircon = new DepicilonUnitType("zircon"){{
            constructor = MechUnit::create;

            canBoost = true;
            boostMultiplier = 1.2f;
            riseSpeed = descentSpeed = 0.02f;

            engineOffset = 6.5f;

            researchCostMultiplier = 0.5f;
            legForwardScl = 0.8f;
            legLengthScl = 0.8f;
            legMaxLength = 1.2f;
            legMoveSpace = 0.8f;
            legBaseUnder = true;
            mechLegColor = Color.valueOf("4C4E5E");
            legLength = 2f;
            speed = 0.6f;
            hitSize = 12;
            armor = 4f;
            health = 1500;
            stepSoundVolume = 0.5f;

            abilities.add(
                    new StatusFieldAbility(
                            MD_StatusEffects.coordination,
                            4*60f,3.8f*60f,3*8f
                    ){{
                        activeEffect = MD_Fx.polyWave(4,20f,0,1f,40f,Color.valueOf("D9FFFC"),0.95f);
                    }}
            );

            weapons.add(
                    new Weapon(modname+"zircon-weapon"){{
                        reload = 40f;
                        mirror = true;
                        x = 27/4f;
                        y = 0;
                        recoil = 0.8f;
                        top  =false;
                        shootY = 2f;
                        rotate = false;
                        rotationLimit = 14;
                        rotateSpeed = 60/60f;
                        shootCone = 45f;

                        shootSound = Sounds.shootDisperse;
                        soundPitchMin = 0.85f;
                        soundPitchMax = 1.1f;

                        minWarmup = 0.55f;
                        shootWarmupSpeed = 0.2f;
//                        parts.addAll(
//                                new RegionPart("-blade"){{
//                                    moves.add(new PartMove(PartProgress.warmup,1.2f,0,-7));
//                                }}
//                        );

                        bullet = new BasicBulletType(4, 15){{
                            recoil = 0;
                            spin = 7f;
                            hitColor=trailColor = backColor = Color.valueOf("B2E9FF");
                            hitSound = Sounds.explosionCleroi;
                            hitSoundVolume = 0.4f;
                            trailLength = 8;
                            trailWidth = 3;
                            trailSinMag = 0.2f;
                            trailSinScl = 5f;
                            width = 12;
                            height = 16;
                            lifetime = 50f;
                            despawnEffect = MD_Fx.hitBulletColor(7f,4,30);
                            hitEffect = MD_Fx.hitBulletColor(13f,8,40);
                            despawnHit = true;
                            fragBullets = 1;
                            fragOffsetMin = 0;
                            fragOffsetMax = 0;
                            fragBullet = new BasicBulletType(0,15,"circle-bullet"){{
                                hitColor=trailColor = backColor = Color.valueOf("B2E9FF");
                                lifetime = 8*60f;
                                collidesTiles = true;
                                despawnHit = true;
                                hitSound = Sounds.explosionCrawler;

                                trailEffect = MD_Fx.Mulitpleslash(50,1,Color.valueOf("EBF8FF"),12,5,6);
                                trailInterval = 18f;
                                hitSoundVolume = 0.4f;
                                fragBullets = 3;
                                fragAngle = 60f;
                                fragRandomSpread = 0f;
                                fragSpread = 120f;
                                width = height = 7f;
                                shrinkX = shrinkY = 0.3f;
                                hitSize = 6f;
                                hitEffect = MD_Fx.hitBulletColor(20f,10,50);
                                fragBullet = new ShrapnelBulletType(){{
                                    lifetime = 18f;
                                    serrationSpaceOffset = 60;
                                    segmentScl = 12f;
                                    length = 18f;
                                    damage = 20;
                                    width = 8f;
                                    hittable = false;
                                    pierceArmor = true;
                                    serrations = 2;

                                }};
                            }};
                        }};
                    }}
            );
        }};

        mouse = new DepicilonUnitType.DepicilonTankUnitType("mouse"){{
            //UnitTypes.stell;
            hitSize = 11f;
            treadPullOffset = 5;
            softShadowRegion = Core.atlas.find("circle-shadow");
            health = 350;
            armor = 4f;
            itemCapacity = 0;
            floorMultiplier = 0.8f;
            rotateSpeed = 4.5f;
            rotateMoveFirst = false;
            accel = 0.2f;
            drag = 0.15f;
            speed = 8f/7.5f;

            tankMoveVolume *= 0.2f;
            tankMoveSound = Sounds.tankMoveSmall;

            treadRects = new Rect[]{
                    new Rect(12 - 32f, 29 - 32f, 11, 29),
                    new Rect(26 - 32f, 5 - 32f, 6, 21)
            };
            abilities.add(new AccelerateAbility());
            weapons.add(
                    new Weapon(modname + "mouse-weapon"){{
                        predictTarget = false;
                        reload = 120f;
                        shoot.shots = 6;
                        shoot.shotDelay = 3f;

                        x=0;y=-7f/4;
                        mirror = false;
                        rotate = true;
                        rotateSpeed =5f;
                        shootCone = 2f;
                        heatColor = Color.valueOf("B2E9FF");
                        cooldownTime = 20;
                        shootSound = Sounds.shootScepterSecondary;
                        bullet = new RailBulletType(){{
                            length = 12*8;
                            damage = 35f;
                            pierceCap = 2;
                            pierce = true;
                            armorMultiplier = 0.5f;
                            hitColor = Color.valueOf("B2E9FF");
                            hitEffect = Fx.hitBulletColor;
                            pierceDamageFactor = 0.5f;
                            inaccuracy = 1.5f;


                            smokeEffect = Fx.colorSpark;

                            endEffect = new Effect(14f, e -> {
                                color(e.color);
                                Drawf.tri(e.x, e.y, e.fout() * 2f, 5f, e.rotation);
                            });

                            shootEffect = new Effect(10, e -> {
                                color(e.color);
                                float w = 1.2f + 2.5f * e.fout();

                                Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                                color(e.color);

                                for(int i : Mathf.signs){
                                    Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 45f);
                                }

                                Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                            });

                            lineEffect = new Effect(20f, e -> {
                                if(!(e.data instanceof Vec2 v)) return;

                                color(e.color);
                                stroke(e.fout() * 1.4f + 0.6f);

                                Fx.rand.setSeed(e.id);
                                for(int i = 0; i < 7; i++){
                                    Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                                    Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                                }

                                e.scaled(14f, b -> {
                                    stroke(b.fout() * 1.5f);
                                    color(e.color);
                                    Lines.line(e.x, e.y, v.x, v.y);
                                });
                            });
                        }};
                    }}
            );

        }};
        coyote = new DepicilonUnitType.DepicilonTankUnitType("coyote"){{
            hitSize = 16;
            health = 1200;
            treadPullOffset = 11;
            armor = 6f;
            speed = 10/7.5f;
            rotateSpeed = 4f;
            accel = 0.2f;
            drag = 0.15f;
            rotateMoveFirst = false;
            itemCapacity = 0;
            floorMultiplier = 0.7f;
            treadRects = new Rect[]{
                    new Rect(24 - 48f, 11 - 48f, 13, 34),
                    new Rect(18 - 48f, 50 - 48f, 13, 34)
            };
            crushFragile = false;
            tankMoveVolume *= 0.55f;
            tankMoveSound = Sounds.tankMove;
            abilities.add(new AccelerateAbility(){{
                level = 0.8f;
                dSpeed = 3.5f/60;
                Aspeed = 0.08f;
                speed = 0.45f/60;
            }});

            weapons.addAll(
                    new Weapon(modname+"coyote-weapon1"){{
                        x = 0;
                        y = -7/4f;
                        mirror = false;
                        rotate = true;
                        rotateSpeed = 4.5f;
                        reload = 60;
                        shoot.shots = 4;
                        shoot.shotDelay = 5f;
                        inaccuracy = 5f;
                        shootSound = Sounds.shootFuse;
                        shootSoundVolume = 0.28f;
                        shootY = 8f;
                        bullet = new ShrapnelBulletType(){{
                            length = 7*8f;
                            width = 12f;
                            shootEffect = smokeEffect = Fx.thoriumShoot;
                            serrationSpacing = 6f;
                            serrationFadeOffset = 0.3f;
                            serrationSpaceOffset =30f;
                            serrationLenScl = 6f;
                            lifetime = 15f;
                            damage = 35f;
                            toColor = Color.valueOf("B2E9FF");
                        }};
                    }},
                    new Weapon(modname+"coyote-weapon2"){{
                        x = -25/4f;
                        y = -17/4f;
                        mirror = true;
                        rotate = true;
                        rotateSpeed = 5f;
                        reload = 40f;
                        shoot.shots = 3;
                        shoot.shotDelay = 5f;
                        shootSound = Sounds.shootArc;
                        shootSoundVolume = 0.55f;
                        bullet = new LightningBulletType(){{
                            damage = 18;
                            lightningColor = Color.valueOf("B2E9FF");
                            lightningLength = 12;
                        }};
                    }}
            );

        }};
        shimmer = new DepicilonUnitType("shimmer"){{

            hovering = true;
            canDrown = false;
            flying = true;
            lowAltitude = true;
            shadowElevation = 0.1f;
            softShadowScl = 0.7f;

            drag = 0.08f;
            speed = 2.2f;
            rotateSpeed = 8f;

            accel = 0.07f;

            health = 300f;
            armor = 4f;
            hitSize = 8f;

            engineSize = 2f;
            engineOffset = 19/4f;
            itemCapacity = 15;
            setEnginesMirror(
                    new UnitEngine(18/4f,-8/4f,2f,-45f)
            );

            useEngineElevation = false;
            researchCostMultiplier = 0f;
            moveSound = Sounds.loopExtract;
            moveSoundVolume = 0.25f;
            moveSoundPitchMin = 0.7f;
            moveSoundPitchMax = 1.5f;

            weapons.add(new Weapon(modname+"shimmer-weapon"){{
                top = false;
                range = 25f;
                alwaysContinuous = true;
                mirror = false;
                shootY = 0f;
                x = 0;
                y = 2f;
                recoil = 0;
                parts.add(new RegionPart("-blade"){{
                    heatProgress = PartProgress.warmup;
                    progress = PartProgress.warmup;
                    heatColor = Color.valueOf("FFAA50");
                    x = 0f;
                    y = 0f;
                    moveRot = -18f;
                    moveY = 0.8f;
                    moveX = 0f;
                    mirror = true;
                }});
                shootSound = Sounds.none;
                activeSound = Sounds.shootSublimate;
                activeSoundVolume = 1.5f;
                bullet = new ContinuousFlameBulletType(){{
                    damage = 30f;
                    length = 19;
                    width = 3f;
                    ammoMultiplier = 1.2f;
                    knockback = 1f;
                    pierceCap = 2;
                    buildingDamageMultiplier = 0.3f;

                    hitColor = flareColor = Color.valueOf("B2E9FF");
                    flareLength = 9f;
                    flareWidth = 2f;
                    colors = new Color[]{
                            Color.valueOf("4263DB").a(0.45f),
                            Color.valueOf("58A2E8").a(0.65f),
                            Color.valueOf("78E2FF").a(0.85f),
                            Color.white};
                }};
            }});
            weapons.add(new DestoryWeapon(){{
                shootOnDeath = true;
                shootOnDeathEffect = Fx.none;
                inaccuracy = 360f;

                targetUnderBlocks = false;
                reload = 24f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                shootSound = Sounds.explosionCrawler;
                shootSoundVolume = 0.4f;
                x = shootY = 0f;
                mirror = false;
                display = false;
                bullet = new BulletType(){{
                    collidesTiles = false;
                    collides = false;
                    despawnHit = true;
                    hitColor = Color.valueOf("B2E9FF");
                    despawnEffect = new MultiEffect(MD_Fx.spark(25f,15,7*8,16f),
                            MD_Fx.spikeWaveColor(22f,5*8f,2f,10,0.4f,2.5f),
                            MD_Fx.polyStarExplosion(20,3,9*8,5f,0,true)
                    );

                    hitEffect = Fx.pulverize;
                    speed = 0f;
                    splashDamageRadius = 6.8f*8f;
                    instantDisappear = true;
                    splashDamage = 120;
                    buildingDamageMultiplier =1f;
                    killShooter = true;
                    hittable = false;
                }};
            }});
        }};
        firefly = new DepicilonUnitType("firefly"){{
            hovering = true;
            canDrown = false;
            flying = true;
            lowAltitude = false;
            shadowElevation = 0.1f;
            softShadowScl = 0.7f;

            drag = 0.08f;
            speed = 2.2f;
            rotateSpeed = 8f;

            accel = 0.07f;

            health = 1000f;
            armor = 4f;
            hitSize = 13f;

            engineSize = 3.5f;
            engineOffset = 8.5f;
            itemCapacity = 30;
            setEnginesMirror(
                    new UnitEngine(4.5f,-8f,2.5f,-45f)
            );
            weapons.add(new BoostWeapon(modname + "firefly-weapon"){{
                warmupSpeed = 0.12f/60;
                dissipateSpeed = 0.6f/60;
                maxReloadSpeed = 10;
                layerOffset = -0.01f;
                baseRotation = -30f;
                parts.addAll(
                        new RegionPart("-barrel"){{
                            heatProgress = PartProgress.recoil.mul(PartProgress.warmup);
                            heatColor = Color.valueOf("FFF5BA");
                            moves.add(new PartMove(PartProgress.warmup.inv()
                                    ,-9f,-2f,30f));
                        }}
                );
                mirror = true;
                x = 9f;
                y = -2f;
                shootY = 4.5f;
                shootX = -0.5f;
                range = 18*8f;
                shootCone = 50f;
                reload = 40f;
                inaccuracy = 1.2f;
                minWarmup = 0.7f;
                shootSound = Sounds.shootDisperse;
                shootSoundVolume = 1.3f;
                bullet = new BasicBulletType(35f*8f/60f,10){{
                    setDefault(this);
                    homingPower = 0.055f;
                    homingRange = 6*8f;
                    lifetime = 20/35f * 60f;
                    width = 10f;
                    height = 16f;
                    trailColor = hitColor = backColor = Color.valueOf("B2E9FF");
                    fragBullets = 1;
                    trailLength = 8;
                    trailWidth  = 2f;
                    fragOffsetMax = fragOffsetMin = 0;
                    fragSpread = 0;
                    fragRandomSpread = 0;
                    fragAngle = 0;
                    armorMultiplier = 0.4f;
                    fragBullet = new ShrapnelBulletType(){{
                        toColor = Color.valueOf("B2E9FF");
                        lifetime = 18f;
                        serrationSpaceOffset = 40;
                        segmentScl = 12f;
                        length = 18f;
                        damage = 8;
                        width = 8f;
                        hittable = false;
                        serrations = 2;
                    }};
                }};
            }});
            researchCostMultiplier = 0f;
            moveSound = Sounds.loopExtract;
            moveSoundVolume = 0.25f;
            moveSoundPitchMin = 0.7f;
            moveSoundPitchMax = 1.5f;
        }};
        pyrolume = new DepicilonUnitType("pyrolume"){{
            //UnitTypes.obviate;
            flying = true;
            drag = 0.065f;
            speed = 1.9f;
            rotateSpeed = 2.4f;
            accel = 0.04f;
            health = 2100;
            armor = 6f;
            hitSize = 20f;
            engineSize = 0;
            engineOffset = 0;
            itemCapacity = 60;
            lowAltitude = false;
            setEnginesMirror(
                    new UnitEngine(3, -8, 2.5f, -45f),
                    new UnitEngine(11, -8, 2f, -45f)
            );
            range = 20*8f;
            weapons.addAll(
                    new Weapon(this.name +"-weapon"){{
                        x = 44/4f;
                        y = -8/4f;
                        layerOffset = -0.01f;
                        reload = 80;
                        mirror = true;
                        shootCone = 5f;
                        shootY = 3f;
                        rotate = true;
                        rotateSpeed = 1.5f;
                        rotationLimit = 140f;
                        shootSound = Sounds.shootOmura;
                        shootSoundVolume = 0.8f;
                        soundPitchMax = 1.3f;
                        soundPitchMin = 1f;
                        bullet = new RailBulletType(){{
                            length = 20*8;
                            damage = 450;
                            trailColor = hitColor = Color.valueOf("D9FCFF");
                            hitEffect = MD_Fx.spikeHitRotationSmall;
                            pierceEffect = Fx.none;
                            pierceDamageFactor = 0.2f;
                            pointEffectSpace = 15f;
                            smokeEffect = Fx.colorSpark;
                            endEffect = new Effect(14f, e -> {
                                color(e.color);
                                Drawf.tri(e.x, e.y, e.fout() * 2.5f, 9f, e.rotation);
                            });

                            pointEffect = new Effect(15f,e->{
                                color(e.color);
                                for(int i : Mathf.signs){
                                    Drawf.tri(e.x, e.y, 12+e.fin()*8f, 2.6f * e.fout(), e.rotation + i * 90);
                                }
                            });

                            shootEffect = new Effect(10, e -> {
                                color(e.color);
                                float w = 1.2f + 7 * e.fout();

                                Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                                color(e.color);

                                for(int i : Mathf.signs){
                                    Drawf.tri(e.x, e.y, w * 0.9f, 30 * e.fout(), e.rotation + i * 60);
                                }

                                Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                            });

                            lineEffect = new Effect(20f,8*20, e -> {
                                if(!(e.data instanceof Vec2 v)) return;

                                color(e.color);
                                stroke(e.fout() * 0.9f + 0.6f);

                                Fx.rand.setSeed(e.id);
                                for(int i = 0; i < 7; i++){
                                    Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                                    Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                                }

                                e.scaled(14f, b -> {
                                    stroke(b.fout() * 2.5f);
                                    color(e.color);
                                    Lines.line(e.x, e.y, v.x, v.y);
                                });
                            });
                        }};
                    }},
                    new Weapon(){{
                        x=0;
                        y=6f;
                        shootY = 0;
                        mirror = false;
                        minWarmup = 0.8f;
                        shootCone = 361f;
                        reload = 150;
                        shootSound = Sounds.shootMissilePlasma;
                        shootSoundVolume = 1;
                        parts.add(new HaloPart(){{
                            sides = 24;
                            radiusTo = 4f;
                            haloRadius = 0;
                            shapes = 1;
                            radius = 0;
                            layer = Layer.effect;
                            color = Color.valueOf("D9FCFF");
                            x=y=0;
                            progress = PartProgress.warmup.mul(PartProgress.reload.inv()).delay(0.1f);
                        }});
                        bullet = new RefractedLaserBulletType(){{
                            laserColor = Color.valueOf("85E0FF");
                            setDefault(this);
                            shootEffect = smokeEffect = Fx.none;
                            emit = true;
                            damage = 150;
                            length = 20*8f;
                            laserStrokeMulti = 0.7f;
                            pierceCap = 5;
                        }};
                    }}
            );
        }};
        burst = new DepicilonUnitType("burst"){{

            softShadowScl = 0.75f;
            constructor = PayloadUnit::create;
            envDisabled = 0;
            payloadCapacity = 3 * 3 * tilesize * tilesize;
            pickupUnits = true;
            vulnerableWithPayloads = true;
            itemOffsetY = 4f;

            range = 160f;
            faceTarget = false;
            targetPriority = 0;
            lowAltitude = false;
            mineWalls = false;
            mineFloor = false;
            mineHardnessScaling = false;
            flying = true;
            drag = 0.05f;
            speed = 1.3f;
            rotateSpeed = 2.8f;
            accel = 0.08f;
            hitSize = 40f;

            autoDropBombs = true;
            //because main weapon reload is long
            circleTarget = false;

            trailLength = 15;
            trailScl = 25/32f;
            waveTrailY = 0;
            waveTrailX = 0;

            engineOffset = 55/4f;
            engineSize = 30/4f;


            loopSoundVolume = 0.85f;
            loopSound = Sounds.loopHover;

            health = 8000;
            armor = 6;
            abilities.addAll(
                    new PatienceAbility(0.6f){{
                        heatColor = Color.valueOf("DEB660");
                        speed = 1.8f;
                        health = 2;
                        armor = 20f;
                        damage = 1.4f;
                        drawHeat = true;
                        effectColor = Color.valueOf("FFE096");
                        effect = MD_Fx.triangle.layer(Layer.flyingUnit+1f);
                        effectChance = 0.25f;
                    }},
                    new ShieldRegenFieldAbility(15,1200,75,40)
            );

            weapons.add(
                    new Weapon(){{
                        shootSound = Sounds.shootMissilePlasma;
                        soundPitchMax = 1.2f;
                        soundPitchMin = 0.8f;
                        shootY = 0;
                        x = 0;
                        y = 15/4f;
                        mirror = false;
                        shootCone = 360f;
                        reload = 180f;
                        range = 7.5f*8;
                        bullet = new BallLightningBulletType(){{
                            shootEffect = MD_Fx.polyStarExplosion(45,4,50,6,45,false);
                            overflow = false;
                            shockAmount = 3;
                            shockLimit = 1;
                            shockCooldown = 12f;
                            shockDamage = 150f;
                            shockRange = 120f;

                            shockEffect = MD_Fx.chainLightningBig;
                            sprite = "large-orb";
                            width = height = 25f;

                            maxRange = 8*8f;
                            ignoreRotation = true;

                            backColor =hitColor= Color.valueOf("B2E9FF");
                            frontColor = Color.white;
                            mixColorTo = Color.white;

                            hitSound = Sounds.explosionQuad;
                            hitSoundVolume = 0.9f;

                            shootCone = 180f;
                            ejectEffect = Fx.none;
                            hitShake = 4f;

                            collidesAir = false;

                            lifetime = 70f;
                            despawnHit = true;
                            hitSound = Sounds.explosionReactor;
                            hitSoundVolume = 0.65f;

                            despawnEffect = new MultiEffect(
                                    MD_Fx.Mulitpleslash(40f,10,hitColor,64f,5f,70f),
                                    MD_Fx.brokenWaveColor(32f,56,16f,3f,18,0.7f,3f),
                                    MD_Fx.brokenWaveColor(38f,45,13f,3f,12,0.45f,4f),
                                    MD_Fx.polyStarExplosion(30f,3,120,8,0,true),
                                    MD_Fx.polyStarExplosion(30f,3,75,6,60,true)
                            );
                            hitEffect = Fx.none;
                            keepVelocity = false;
                            spin = 2f;

                            width = height =

                            speed = 0f;
                            collides = false;

                            splashDamage = 600;
                            splashDamageRadius = 100f;
                            damage = splashDamage * 0.7f;
                            fragBullets = 10;
                            fragLifeMax = 1.2f;fragLifeMin = 0.8f;
                            fragVelocityMin = 1f;fragVelocityMax = 1.1f;
                            fragBullet = new BasicBulletType(36*8/60f,70){{
                                sprite = "missile-large";
                                backSprite = "missile-large";
                                width = 10f;
                                height = 14f;
                                trailLength = 5;
                                trailWidth = 3f;
                                lifetime = 27f;
                                homingPower = 0.12f;
                                homingRange = 30*8f;
                                frontColor = trailColor = backColor = hitColor = Color.valueOf("B2E9FF");
                                despawnEffect = hitEffect = MD_Fx.Mulitpleslash(20,2,hitColor,16,3f,4f);
                            }};
                        }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);

                                float fin = (float) ((Math.sqrt(b.fin())-b.fin())*4f);
                                Draw.color(c);
                                if(fin>0.05f){
                                    Drawff.prominence(b.id,b.x,b.y,20,12,fin*45,fin*12f,fin*10,6);
                                }
                                Fill.circle(b.x,b.y,fin*10);
                                for(int o: Mathf.zeroOne) {
                                    Draw.color(c,Color.white,o*0.95f);
                                    float scl = 1-o*0.4f;
                                    for (int i = 0; i < 3; i++) {
                                        Drawf.tri(b.x, b.y, scl*8 *fin, scl*80 *fin, i * 120 + Time.time*1.2f);
                                    }
                                }
                                Fill.circle(b.x,b.y,fin*6);
                                Draw.reset();
                            }
                        };
                    }
                        Color c = Color.valueOf("B2E9FF");
                        @Override
                        public void draw(Unit unit, WeaponMount mount) {
                            super.draw(unit, mount);
                            if(!unit.isValid())return;
                            float fin = (reload-mount.reload)/reload;
                            Draw.z(Layer.bullet+1f);
                            Tmp.v2.set(x,y).rotate(unit.rotation()-90);
                            Draw.color(c);
                            Fill.circle(unit.x +Tmp.v2.x,unit.y+Tmp.v2.y,fin*4.5f);
                            if(fin>0.1f)Drawff.prominence(
                                    unit.id,
                                    unit.x +Tmp.v2.x,unit.y+Tmp.v2.y,
                                    15,10,fin*5f,
                                    fin*5.5f,fin*4.5f,6);
                            Draw.color(Color.white);
                            Fill.circle(unit.x +Tmp.v2.x,unit.y+Tmp.v2.y,fin*2.3f);

                            Draw.reset();
                        }
                    },
                    new Weapon(modname+"burst-weapon"){{
                        shootSound = Sounds.shootReign;
                        soundPitchMin = 0.7f;
                        soundPitchMax = 1.3f;
                        controllable = false;
                        autoTarget = true;
                        x = 16;
                        y = 4.8f;
                        mirror = true;
                        layerOffset = -20;
                        shoot = new ShootBarrel(){{
                            barrels = new float[]{
                                    2,0,10,
                                    1,0,0,
                                    -1,0,0,
                                    2,0,-10
                            };
                            shotDelay = 2f;
                            shots = 8;
                        }};
                        parts.add(new RegionPart("-blade"){{
                            heatProgress = PartProgress.warmup;
                            progress = PartProgress.warmup.blend(PartProgress.recoil, 0.15f);
                            heatColor = Color.valueOf("FFAA50");
                            x = 0 / 4f;
                            y = 0f;
                            moveRot = -25f;
                            moveY = -1f;
                            moveX = -1f;
                            under = true;
                            mirror = true;
                        }});
                        reload = 40f;
                        shootCone = 30f;
                        shootY = 4f;
                        rotateSpeed = 240/60f;
                        rotate = true;
                        rotationLimit = 361;
                        bullet = new BasicBulletType(22*8/60f,35,modname+"acicular-bullet"){{
                            homingPower = 0.02f;
                            lightningColor = backColor = trailColor = hitColor = Color.valueOf("B2E9FF");
                            lightningDamage = 20f;
                            lightning = 1;
                            lightningLength = 2;
                            lightningCone = 15f;
                            lifetime = 50;
                            maxRange = 16*8f;

                            width = 11;
                            height = 13f;
                            pierce = true;
                            pierceCap = 2;

                            trailWidth = 1.5f;
                            trailLength = 8;
                            weaveScale = 5f;
                            weaveMag = 1.8f;
                        }};
                    }}
            );
            setEnginesMirror(
                    new UnitEngine(93/4f,-16/4f,25/4f,-60f),
                    new UnitEngine(105/4f,-55/4f,16/4f,-45f),
                    new UnitEngine(76/4f,-65/4f,16/4f,-45f)
            );
        }
            @Override
            public void drawItems(Unit unit) {
                float z = Draw.z();
                Draw.z(Layer.flyingUnit+1);
                super.drawItems(unit);
                Draw.z(z);
            }
        };
        lumen = new DepicilonUnitType("lumen"){{
            defaultCommand = UnitCommand.repairCommand;
            aiController = FlyingFollowAI::new;
            canHeal = true;
            hovering = true;
            canDrown = false;
            flying = true;
            lowAltitude = true;
            shadowElevation = 0.1f;
            softShadowScl = 0.7f;

            drag = 0.08f;
            speed = 1.7f;
            rotateSpeed = 10f;

            accel = 0.13f;

            health = 200f;
            armor = 2f;
            hitSize = 8.5f;

            engineSize = 1.8f;
            engineOffset = 5.2f;
            itemCapacity = 20;
            weapons.add(
                    new MD_RepairBeamWeapon(){{
                        widthSinMag = 0.11f;
                        reload = 20f;
                        x = y = 0;
                        rotate = false;
                        shootY = 5f;
                        beamWidth = 0.7f;
                        aimDst = 0f;
                        shootCone = 360;
                        mirror = false;

                        buildFraction = 0.25f;
                        buildSpeed = 220/60f;
                        unitFraction = 0.01f;
                        unitSpeed = 120/60f;

                        targetUnits = true;
                        targetBuildings = true;
                        canTargetSelf = true;
                        autoTarget = true;
                        controllable = false;
                        laserColor = Pal.heal;
                        healColor = Pal.heal;

                        bullet = new BulletType() {{
                            maxRange = 12*8f;
                        }};
                    }}
            );
        }};

        floating = new DepicilonUnitType("floating"){{
            defaultCommand = UnitCommand.assistCommand;
            buildSpeed = 0.5f;
            hovering = true;
            canDrown = false;
            flying = true;
            lowAltitude = true;
            shadowElevation = 0.1f;
            softShadowScl = 0.7f;

            drag = 0.08f;
            speed = 17f/7.5f;
            rotateSpeed = 7f;

            accel = 0.13f;

            health = 2500;
            armor=6;
            hitSize = 24;

            engineSize = 2.7f;
            engineOffset = 11.5f;
            buildBeamOffset = 6f;
            setEnginesMirror(
                    new UnitEngine(11f,-6f,3,-45f),
                    new UnitEngine(6.5f,-10.5f,3f,-45f)
            );
            itemCapacity = 100;
            weapons.addAll(
                    new CanBuildingBuildWeapon(this.name+"-build-weapon"){{
                        speedMulti = 1.5f;
                        mirror = true;
                        shootY = 2f;
                        x = 11.25f;
                        y = 0.25f;
                    }},
                    new CanBuildingBuildWeapon(this.name+"-build-weapon"){{
                        speedMulti = 1.5f;
                        mirror = true;
                        shootY = 2f;
                        x = 4f;
                        y = -5.5f;
                    }},
                    new Weapon(this.name+"-weapon"){{
                        mirror = false;
                        x = 0;
                        y = 2f;
                        reload = 2*60f;
                        shootY = 3.5f;
                        shoot = new ShootHelix() {{
                            scl = 3.5f;
                            mag = 0.8f;
                        }};
                        shootSound = Sounds.shootToxopidShotgun;
                        shootSoundVolume = 0.75f;
                        rotate = true;
                        rotateSpeed = 6;
                        shootCone = 12;

                        bullet = new BasicBulletType(20*8/60f,60){{
                            smokeEffect = new Effect(25f,Fx.shootSmokeSmite.renderer);
                            scaleLife = true;
                            lifetime = 1.25f*60f;
                            homingPower = 0.01f;
                            despawnHit = false;
                            fragOnHit = false;
                            splashDamage = 30f;
                            splashDamageRadius = 7*8f;

                            fragOnDespawn = true;
                            fragBullets = 1;
                            trailLength = 20;
                            trailWidth = 2.5f;
                            width = 14f;
                            height = 20f;
                            trailColor = backColor = hitColor = Pal.heal;
                            fragOffsetMax = fragOffsetMin = 0;
                            hitSound = despawnSound = Sounds.explosionAfflict;
                            hitSoundVolume = 0.75f;
                            hitEffect = despawnEffect = MD_Fx.hitBulletColor(7*8f,9,7.5f*8);
                            fragBullet = new BallLightningBulletType(0,0){{
                                hitColor = Pal.heal;
                                instantDisappear = true;
                                shockAmount = 8;
                                shockRange = 10*8f;
                                minRemainingRange = 0.3f;
                                maxRemainingRange = 0.8f;
                                maxRemaining = 3;
                                shockDamage = 30f;
                                shockLimit = 2;
                                shockEffect = new MultiEffect(MD_Fx.chainLightningPro,
                                        MD_Fx.hitBulletColor(8f,7,16f));
                            }};
                        }};
                    }}
            );
        }};


        primitive = new DepicilonUnitType("primitive"){{
            constructor = PayloadUnit::create;
            coreUnitDock = true;
            controller = u -> new BuilderAI(true, 400f);
            isEnemy = false;
            envDisabled = 0;

            range = 80f;
            faceTarget = true;
            targetPriority = -2;
            lowAltitude = true;
            mineWalls = true;
            mineFloor = true;
            mineHardnessScaling = true;
            flying = true;
            mineSpeed = 4f;
            mineTier = 3;
            buildSpeed = 1f;
            drag = 0.08f;
            speed = 4.8f;
            rotateSpeed = 6f;
            accel = 0.08f;
            itemCapacity = 40;
            health = 200;
            armor = 2f;
            hitSize = 10f;

            engineSize = 10/4f;
            engineOffset = 22/4f;
            setEnginesMirror(
                    new UnitEngine(18 / 4f, 4 / 4f, 10/4f, -45)
            );

            payloadCapacity = 2 * 2 * tilesize * tilesize;
            pickupUnits = true;
            vulnerableWithPayloads = true;

            fogRadius = 0f;
            targetable = false;
            hittable = false;

            weapons.addAll(
                    new OverdriveWeapon(modname+"primitive-weapon1"){{
                        lockedRange = 30*8f;
                        lockedTime = 50f;
                        reload = 30f;
                        extraDuration = 300f;
                        aimChangeSpeed = 4.5f;

                        y = -4/4f;
                        x = 0;
                        shootY = 7/4f;
                        mirror = false;
                        top = false;
                        layerOffset = -0.01f;
                    }}
//                    new CanBuildingBuildWeapon(){{
//                        mirror = true;
//                        y=0;
//                        x = 10;
//                    }}
            );
            clipSize = 32*8f;
        }};


        laborers = new DepicilonUnitType("laborers"){{
            controller = u -> new TransportAI();
            constructor = BuildingTetherPayloadUnit::create;
            isEnemy = false;
            allowedInPayloads = false;
            logicControllable = false;
            playerControllable = false;
            envDisabled = 0;
            payloadCapacity = 0f;

            lowAltitude = false;
            flying = true;
            drag = 0.06f;
            speed = 3;
            rotateSpeed = 9f;
            accel = 0.1f;
            itemCapacity = 50;
            health = 120;
            hitSize = 9;
            engineSize = 2f;
            engineOffset = 6;
            hidden = true;
            setEnginesMirror(new UnitEngine(3, -4.5f, 7/4f, -45));
        }};

        farmer = new DepicilonUnitType("farmer"){{
            controller = u -> new SeedingAI();
            constructor = BuildingTetherPayloadUnit::create;
            isEnemy = false;
            allowedInPayloads = false;
            logicControllable = false;
            playerControllable = false;
            envDisabled = 0;
            payloadCapacity = 0f;

            lowAltitude = false;
            flying = true;
            drag = 0.05f;
            speed = 2;
            rotateSpeed = 9f;
            accel = 0.08f;
            itemCapacity = 0;
            health = 120;
            hitSize = 9;
            engineSize = 1.8f;
            engineOffset = 4.5f;
            hidden = true;
        }};

        engineering_drone = new DepicilonUnitType("engineering-drone"){{
            constructor = BuildingTetherPayloadUnit::create;
            isEnemy = false;
            allowedInPayloads = false;
            defaultCommand = UnitCommand.rebuildCommand;
            envDisabled = 0;
            payloadCapacity = 0f;

            lowAltitude = false;
            flying = true;
            drag = 0.06f;
            speed = 3;
            rotateSpeed = 9f;
            accel = 0.1f;
            itemCapacity = 50;
            health = 180;
            hitSize = 9;
            engineSize = 0;
            engineOffset = 6;
            hidden = true;
            buildSpeed = 0.3f;
            setEnginesMirror(new UnitEngine(4, -3.5f, 7/4f, -45));
            weapons.add(new CanBuildingBuildWeapon(this.name+"-weapon"){{
                mirror = true;
                x = 4;
                y = -1;
                shootY = 3f;
                speedMulti = 1f;
                layerOffset = -0.01f;
            }});
        }};
    }



}
