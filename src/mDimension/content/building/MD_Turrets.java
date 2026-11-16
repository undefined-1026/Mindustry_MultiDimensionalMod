package mDimension.content.building;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mDimension.consumers.ConsumeBeam;
import mDimension.content.*;
import mDimension.entity.EntityShield;
import mDimension.entity.MultiSound;
import mDimension.entity.bullet.*;
import mDimension.entity.pattern.ShootBarrelRandom;
import mDimension.entity.pattern.ShootSwing;
import mDimension.world.blocks.beam.MD_BeamTurret;
import mDimension.world.blocks.MD_ConstraintItemTurret;
import mDimension.world.blocks.MD_PayloadTurret;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
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
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootHelix;
import mindustry.entities.pattern.ShootMulti;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.BuildVisibility;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mDimension.content.building.MD_blocks.*;
import static mindustry.Vars.world;
import static mindustry.type.ItemStack.with;

public class MD_Turrets {
    public static void load(){
        //region turret
        ionize = new ContinuousTurret("ionize") {{
            requirements(Category.turret, ItemStack.with(MD_Items.germanium, 50, Items.silicon, 20, MD_Items.aluminium, 20));
            //outlineColor = Pal.darkOutline;
            researchCost = with(MD_Items.germanium, 50, Items.silicon, 20, MD_Items.aluminium, 20);
            shootType = new PointLaserBulletType() {{
                beamEffect = MD_Fx.polyFacula(4, 3.2f, 0, 60f, c("d0d0ff"), 0.87f);
                beamEffectInterval = 6f;
                hitSound = Sounds.shootAtrax;
                shootSound = Sounds.shootMerui;
                hitEffect = MD_Fx.Mulitpleslash(20f, 1, c("d0d0ff"), 24f, 3f, 8f);
                damageInterval = 12f;
                setDefaults = false;
                despawnHit = false;
                fragOnDespawn = false;
                sprite = modname + "ionize-point-laser";
                targetAir = true;
                targetGround = false;
                damage = 40f / (60f / damageInterval);

                pierceArmor = true;
                hitColor = c("d0d0ff");
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
                    despawnEffect = hitEffect = MD_Fx.polyWave(16, 3, 0, 2.4f, 16f, c("f5f5ff"), 0.7f);
                    trailColor = backColor = hitColor = c("d0d0ff");
                    frontColor = c("f5f5ff");
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

                        backColor = trailColor = c("FFB89E");
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

                        backColor = trailColor = c("E6FDFF");

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
                    MD_Items.germanium,new ArtilleryBulletType(2.9f, 30) {{
                        hitEffect = new MultiEffect(Fx.flakExplosion, Fx.shockwaveSmaller);
                        knockback = 1.2f;
                        lifetime = 66f;
                        width = 9f;
                        height = 12f;
                        collidesTiles = false;
                        splashDamageRadius = 2.5f*8;
                        splashDamage = 40;

                        backColor = hitColor = trailColor = c("C569D3");
                        frontColor = c("EAA8E2");
                        despawnEffect = Fx.hitBulletColor;
                        lifeScaleRandMax = 1.08f;
                        lifeScaleRandMin = 0.95f;
                        fragBullets = 4;
                        fragBullet = new BasicBulletType(2.5f, 25, "bullet"){{
                            width = 7;
                            height = 10;
                            shrinkY = 1f;
                            lifetime = 10;
                            backColor = c("C569D3");
                            frontColor = c("EAA8E2");
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
                        backColor = hitColor = trailColor = c("F7DE80");
                        frontColor = c("FFF1D4");
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
                            hitColor = backColor = c("F7DE80");
                            frontColor = c("FFF1D4");
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
                                backColor = c("F7DE80");
                                frontColor = c("FFF1D4");
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
            researchCost = with(MD_Items.al_alloy,400,Items.silicon,500,Items.graphite,500);
            drawer = new DrawTurret("brown-");

        }};
        rays = new PowerTurret("rays"){{
            requirements(Category.turret,with(Items.silicon,60,MD_Items.germanium,100,MD_Items.al_alloy,40));
            shootType = new MD_ShrapnelBulletType(){{
                setDefault(this);
                shootEffect = Fx.none;
                despawnEffect = Fx.none;
                damage = 45f;
                serrationRotation = 60;
                firstSerrationScl = 1.7f;
                armorMultiplier = 0.5f;
                length = 8*17f;
                width = 6f;
                serrations = 6;
                serrationFadeOffset = 0.2f;
                serrationSpaceOffset = 22f;
                serrationLenScl = 3.2f;
                serrationSpacing = 8*7f/6f;
                toColor = c("D8FFA9");
            }};
            targetAir = false;
            scaledHealth = 250f;
            size = 2;
            shootSound = Sounds.shootLancer;
            soundPitchMax = 0.95f;
            soundPitchMin = 0.75f;
            shootSoundVolume = 0.4f;
            range = 8*17f;
            inaccuracy = 2f;
            shoot.shots = 3;
            shoot.shotDelay = 4f;
            reload = 60f;
            shootY = 5f;
            researchCost = with(Items.silicon,400,MD_Items.germanium,500,MD_Items.al_alloy,300);

            consumePower(400f/60f);
            coolant = new ConsumeCoolant(12f/60f);
            coolantMultiplier = 2f;
            drawer = new DrawTurret("steady-state-"){{
                parts.add(
                        new RegionPart("-barr"){{
                            under = true;
                            moveY = -3f/4f;
                            progress = PartProgress.recoil;
                        }}
                );
            }};
        }};
        fracture = new ItemTurret("fracture") {{
            requirements(Category.turret, ItemStack.with(MD_Items.aluminium, 120, Items.silicon, 80,MD_Items.al_alloy, 80));
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

                        splashDamage = 10f;
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

                        splashDamage = 20f;
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
                            toColor = c("CFE9FF");
                            length = 30f;
                            serrationSpaceOffset = 70;
                            serrations = 4;
                            serrationLenScl = 10f;
                            width = 12f;
                            damage = 15;
                            pierceArmor = true;
                            lifetime = 40f;
                        }};

                    }}
            );
            reload = 25f;
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
            heatColor = c("d8d8ff");

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
                    heatColor = c("d8d8ff");
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
                        frontColor = c("f8f8ff");
                        backColor = trailColor = hitColor = c("d8e0ff");
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
                                shield.create(b.x, b.y, b.rotation()+45f, b.team, c("e0e8ff"));
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
                                    backColor = hitColor = c("d8e0ff");
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
                        trailColor = backColor = hitColor = c("FFE894");
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
                        hitColor =  c("c0d8ff");
                        ammoMultiplier = 1f;
                        spawnUnit = new MissileUnitType("polarization-missile"){{
                            softShadowScl = 0.6f;
                            speed = 6f;
                            maxRange = 6f;
                            lifetime = 60f*2.25f;
                            hitSize = 10f;
                            outlineColor = Pal.darkOutline;
                            engineColor = trailColor = c("c0d8ff");
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
                                    hitColor = c("c0d8ff");
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
                                        backColor = trailColor = hitColor = c("c0d8ff");

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
            };
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
            requirements(Category.turret, BuildVisibility.sandboxOnly,with());
            drawDisabled = true;
            size = 4;
            shootType = new MultiPointLaserBullet(){{
                damage = 2500f/12f;
                buildingDamageMultiplier = 0.3f;
                hitColor = c("fda981");
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
        grudge = new MD_ConstraintItemTurret("grudge"){{
            scaledHealth = 250f;
            squareSprite = false;
            outlineColor = Pal.darkOutline;
            unitSort = UnitSorts.strongest;
            loadingAmmoTime = 110;
            shutDownLoadingSpeedBoost = 2.2f;
            ammoAmountReloadSpeedBoost = 6f/25f;
            maxAmmoAmountReloadSpeed = 6f;
            maxAmmo = 160;
            reload = 40f;
            inaccuracy=3f;
            ammoPerShot = 3;
            size = 3;
            range = 8*30f;
            shootY = 4.5f;
            minWarmup = 0.7f;
            warmupMaintainTime = 30f;
            shootWarmupSpeed = 0.07f;
            shootSound = Sounds.shootCyclone;
            shootSoundVolume = 2f;
            soundPitchMax = 1.1f;
            soundPitchMin = 0.75f;
            shoot = new ShootBarrel(){{
                barrels = new float[]{
                        -2,0,0,
                        0,0,0,
                        2,0,0
                };
                shots = 3;
                shotDelay = 4f;
            }};
            requirements(Category.turret,with(
                    MD_Items.polymer,220,
                    MD_Items.al_alloy,150,
                    MD_Items.germanium,150,
                    Items.silicon,120
            ));
            coolantMultiplier = 0.5f;
            coolant = new ConsumeCoolant(36f/60f);

            drawer = new DrawTurret("steady-state-"){{
                parts.addAll(
                        new RegionPart("-barrel"){{
                            mirror = true;
                            moves.add(new PartMove(PartProgress.warmup,2f,1.5f,5));
                        }},
                        new RegionPart("-blade"){{
                            mirror = true;
                            moves.add(new PartMove(PartProgress.warmup,3,2.5f,-30));
                        }}
                );
            }};
            ammo(
                    MD_Items.al_alloy,new BasicBulletType(14,40){{
                        setDefault(this);
                        ammoMultiplier = 5;
                        trailColor = backColor = hitColor = MD_Items.al_alloy.color;
                        splashDamage = 25;
                        splashDamageRadius = 10f;
                        pierceCap = 2;
                    }},MD_Items.ti_alloy,new BasicBulletType(14,80){{
                        setDefault(this);
                        ammoMultiplier = 10;
                        knockback = 0.2f;
                        rangeChange = 8*7f;
                        trailColor = backColor = hitColor = MD_Items.ti_alloy.color.cpy().lerp(Color.white,0.35f);
                        splashDamage = 40;
                        splashDamageRadius = 7f;
                        pierceCap = 3;
                    }},MD_Items.light_ceramic,new BasicBulletType(14,30){{
                        setDefault(this);
                        ammoMultiplier = 7;
                        rangeChange = 8*3f;
                        reloadMultiplier = 0.8f;
                        lightningColor = trailColor = backColor = hitColor = MD_Items.light_ceramic.color;
                        splashDamage = 20;
                        splashDamageRadius = 15;
                        lightning = 2;
                        lightningCone = 20f;
                        lightningLength = 5;
                        lightningLengthRand = 4;
                        lightningDamage = 12;
                    }}
            );
            setMapVal(this.ammoTypes,(k,bb)->{
                var b = (BasicBulletType)bb;
                b.trailLength = 8;
                b.trailWidth = 2.4f;
                b.width = 10;
                b.height = 10;
                b.knockback+=0.2f;
                if(k!=MD_Items.light_ceramic)b.pierce = true;
            });
            limitRange();
        }};
        refraction = new MD_BeamTurret("refraction"){{
            requirements(Category.turret,with(MD_Items.light_ceramic,220,MD_Items.germanium,350,MD_Items.ti_alloy,200,Items.silicon,300));
            scaledHealth = 250;
            enableDrawStatus = true;
            size = 4;
            requiredPower = 120;
            range = 50*8f;
            shootY = -2f;
            reload = 90f;
            moveWhileCharging = true;
            rotateSpeed = 2f;
            unitSort = UnitSorts.strongest;
            shoot.firstShotDelay = 30f;
            coolant = new ConsumeCoolant(15/60f);
            ammo(
                    MD_beams.near_infrared_light,new MD_RailBulletType(){{
                        damage = 580;
                        length = 8*50;
                        chargeEffect = MD_Fx.charge(30f,3f,15, MD_beams.near_infrared_light.color);
                        ammoMultiplier = 1f;
                        pointEffectSpace = 12;
                        pointEffectSpaceRand = 9;
                        status = StatusEffects.melting;
                        statusDuration = 8*60f;
                        float stroke = 8f;
                        lineEffect = new Effect(20f,8*70, e->{
                            if(e.data instanceof Vec2 v){
                                for(int i=0;i<3;i++){
                                    float scl = (1f-0.22f*i) * e.fout();
                                    if(i==0) {
                                        Draw.color(e.color, 0.5f);
                                    }else if(i==1) {
                                        Draw.color(e.color);
                                    }else{
                                        Draw.color(e.color, Color.white,0.9f);
                                    }
                                    Fill.circle(e.x,e.y,scl * 11f);
                                    Lines.stroke(scl * stroke);
                                    Lines.line(e.x,e.y,v.x,v.y);

                                }
                            }
                        }).layer(109f);
                        endEffect = new Effect(20f,e->{
                            for(int i=0;i<3;i++){
                                float scl = (1f-0.22f*i);
                                if(i==0) {
                                    Draw.color(e.color, 0.5f);
                                }else if(i==1) {
                                    Draw.color(e.color);
                                }else{
                                    Draw.color(e.color, Color.white,0.9f);
                                }
                                Drawf.tri(e.x,e.y,scl * stroke * e.fout(),20f*scl,e.rotation);
                            }
                        }).layer(109f);
                        pointEffect = Fx.smeltsmoke;
                        splashDamage = 300;
                        splashDamageRadius = 8*5;
                    }},
                    MD_beams.bright_light,new MD_RailBulletType(){{
                        damage = 850;
                        length = 8*60;
                        rangeChange = 8*10;
                        chargeEffect = MD_Fx.charge(30,4f,22, MD_beams.bright_light.color);
                        float stroke = 8f;
                        lineEffect = new Effect(20f,8*70,e->{
                            if(e.data instanceof Vec2 v){
                                float space = 15;
                                Vec2 t = MD_Fx.v2.set(v).sub(e.x,e.y);
                                float len = t.len();
                                var nor = t.nor();
                                for(int i=0;i<3;i++){
                                    float scl = (1f-0.22f*i) * e.fout();
                                    if(i==0) {
                                        Draw.color(e.color, 0.5f);
                                    }else if(i==1) {
                                        Draw.color(e.color);
                                    }else{
                                        Draw.color(e.color, Color.white,0.9f);
                                    }
                                    Fill.circle(e.x,e.y,scl * 11f);
                                    Lines.stroke(scl * stroke);
                                    Lines.line(e.x,e.y,v.x,v.y);

                                    for(float j=space;j<len/2;j+=space){
                                        float x = Math.min(1f,j/(len/2));
                                        float w = (x-1)*(x-1)*55;
                                        for(int s:Mathf.signs){
                                            Drawf.tri(e.x+j*nor.x,e.y+j*nor.y,11*scl,scl* w,e.rotation + 90*s);
                                        }
                                    }
                                }
                            }
                        }).layer(109f);
                        endEffect = new Effect(20f,e->{
                            for(int i=0;i<3;i++){
                                float scl = (1f-0.22f*i);
                                if(i==0) {
                                    Draw.color(e.color, 0.5f);
                                }else if(i==1) {
                                    Draw.color(e.color);
                                }else{
                                    Draw.color(e.color, Color.white,0.9f);
                                }
                                Drawf.tri(e.x,e.y,scl * stroke * e.fout(),20f*scl,e.rotation);
                            }
                        }).layer(109f);

                        splashDamage = 200;
                        splashDamageRadius = 8*4;
                        reloadMultiplier = 0.65f;
                        ammoMultiplier = 3;
                        fragBullets = 1;
                        pierceFragCap = 1;
                        fragBullet = new RefractedLaserBulletType(){{
                            pierceCap = 6;
                            laserStrokeMulti = 1.5f;
                            refractedRadius = 8*10;
                            laserColor = hitColor = trailColor = MD_beams.bright_light.color;
                            splashDamage = 80;
                            damage = 80;
                            splashDamageRadius = 8*2;
                            setDefault(this);
                        }};
                    }}
            );
            setMapVal(this.ammoTypes,(k, v)->{
                var b = (MD_RailBulletType)v ;
                b.pinpoint = true;
                b.shootSound = Sounds.shootArtillerySapBig;
                b.hitEffect = MD_Fx.hitBulletColor(0,25,50);
                b.endEffect2 = new Effect(15f, 100f, e -> {
                    color(e.color);
                    stroke(e.fout() * 4f);
                    Lines.circle(e.x, e.y, 4f + e.finpow() * 20f);
                    for(int i = 0; i < 3; i++){
                        Drawf.tri(e.x, e.y, 6f, 65 * e.fout(), i*120 + 30);
                    }
                    color();
                    for(int i = 0; i < 3; i++){
                        Drawf.tri(e.x, e.y, 3f, 25 * e.fout(), i*120 + 30);
                    }
                    Drawf.light(e.x, e.y, 150f, Pal.bulletYellowBack, 0.9f * e.fout());
                });
                b.despawnHit = false;
                b.setDefaults = false;
                b.fragOnDespawn = false;
                b.pierceCap = 3;
                b.pierceDamageFactor = 0.3f;
                b.hitColor = b.trailColor = k.color.cpy().lerp(Color.white,0.4f);
                shootEffect = MD_Fx.railShootColor;
            });

            drawer = new DrawTurret("steady-state-"){{
                parts.addAll(
                        new RegionPart("-blade"){{
                            mirror = true;
                            under = true;

                            moveY = 4f;
                            moveX = 4f;
                            moveRot = -30f;
                            progress = PartProgress.warmup;
                        }},
                        new RegionPart("-barrel"){{
                            mirror = true;

                            moveY = 0;
                            moveX = 1f;
                            moves.add(new PartMove(PartProgress.recoil,1,-2,-5));
                            progress = PartProgress.warmup;
                        }}
                );
            }};
        }};
        crest = new MD_PayloadTurret("crest"){{
            scaledHealth = 440;
            squareSprite = false;
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
                        frontColor = backColor = trailColor = hitColor = c("E3E8FF");
                        trailLength = 15;
                        trailWidth = 2.5f;
                        trailSinMag = 0.3f;
                        trailSinScl = 0.5f;
                        trailEffect = MD_Fx.Mulitpleslash(30,4,hitColor,40,5,30);
                        trailInterval = 1.2f;
                        width = 25f;
                        height = 43f;
                        lifetime = 16f;
                        hitSize = 22f;
                        knockback = 35f;
                        impact = true;
                        despawnEffect = new MultiEffect(MD_Fx.distortPow(25,20*8,5*8,25),MD_Fx.starExplosionBig, MD_Fx.spikeExplosion);
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
                            frontColor = backColor = trailColor = hitColor = c("E3E8FF");
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
            reload = 220;
            rotateSpeed = 45f/60f;
            coolant = consumeCoolant(40f/60f);
            coolantMultiplier = 0.4f;
            size = 5;
            shootWarmupSpeed = 0.03f;
            minWarmup = 0.98f;
            warmupMaintainTime = 300f;
            shootSound = Sounds.explosionReactor2;
            shootSoundVolume = 0.8f;
            soundPitchMin = 1.3f;
            soundPitchMax = 1.65f;
            shootEffect = new MultiEffect(MD_Fx.crestShoot, MD_Fx.crestShootFlame);
            ammoUseEffect = MD_Fx.casingCrest;
            drawer = new DrawTurret("steady-state-"){{
                float haloY = -15f;
                var haloProgress = DrawPart.PartProgress.warmup.delay(0.3f);
                Color haloColor = c("F2F8FF");
                int dises = 2;
                float width = 11;
                for(int i=dises-1;i>=0;i--){
                    int j = i;
                    parts.add(new RegionPart("-ammo"){{
                        layerOffset = -0.1f;
                        x = 0.5f*width - width/(dises)* j;
                        moves.addAll(new PartMove(
                                p->Mathf.clamp(p.reload*2-1),width/(dises),0,0
                        ));
                    }});
                }
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
                        new RegionPart("-mid-u"){{
                            under = false;
                            moves.add(new PartMove(PartProgress.warmup,0,-10f,0));
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
    }
}