package mDimension.meta;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import mDimension.world.type.Beam;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.tilesize;

public class MD_StatValues extends StatValues {
    public static Stack BeamStack(Beam beam, float power){
        return BeamStack(beam.uiIcon,power,beam,true,true);
    }
    public static Stack BeamStack(Beam beam, float power,boolean displayWavelenght){
        return BeamStack(beam.uiIcon,power,beam,true,displayWavelenght);
    }

    public static Stack BeamStack(TextureRegion region, float power, @Nullable Beam content,boolean tooltip,boolean displayWavelenght){

        Stack stack = new Stack();

        stack.add(new Table(b -> {
            b.table(Styles.grayPanel, t -> {
                t.image(region).size(48f).scaling(Scaling.fit).pad(10f);
                t.table(info -> {
                    info.add(content.localizedName).left();
                });
                t.table(o->{
                    o.right();
                    o.top();
                    if(displayWavelenght) {
                        o.add(
                                Core.bundle.format("stat.laserpower", power)
                        ).pad(10f).row();
                        o.add(
                                Core.bundle.format("stat.wavelength", content.energyLevel)
                        ).pad(10f);
                    }else{
                        o.add(
                                Core.bundle.format("stat.laserpower", power)
                        ).pad(12f);
                    }
                });

            }).growX().pad(5f);
        }));

        withTooltip(stack, content, tooltip);

        return stack;
    }
    public static Stack BeamStack(float power,float minWave,float maxWave,boolean tooltip ){

        Stack stack = new Stack();

        stack.add(new Table(b -> {
            b.table(Styles.grayPanel, t -> {
                t.add(Core.bundle.get("stat.light")).pad(12f);
                t.table(o->{
                    o.right();
                    o.top();
                    o.add(
                            Core.bundle.format("stat.laserpower",power)
                    ).pad(8f).row();
                    o.add(
                        Core.bundle.format("stat.wavelengthinterval",minWave,maxWave)
                    ).pad(8f);
                });

            }).growX().pad(5f);
        }));
        return stack;
    }
    public static Table displayLiquid(Liquid liquid, float amount, boolean perSecond,boolean showName){
        Table t = new Table();

        t.add(new Stack(){{
            add(new Image(liquid.uiIcon).setScaling(Scaling.fit));

            if(amount != 0){
                Table t = new Table().left().bottom();
                t.add(Strings.autoFixed(amount, 3)).style(Styles.outlineLabel);
                add(t);
            }
        }}).size(iconMed).padRight(3  + (amount != 0 ? (Strings.autoFixed(amount, 3).length() - 1) * 10 : 0)).with(s -> withTooltip(s, liquid, false));

        if(perSecond && amount != 0){
            t.add(StatUnit.perSecond.localized()).padLeft(1.5f).padRight(1.5f).color(Color.lightGray).style(Styles.outlineLabel);
        }

        if(showName)t.add(liquid.localizedName);

        return t;
    }
    //for AmmoListValue
    /** Adds an info table with an icon and description key from the bundle */
    private static Cell<?> tableInfo(Table table, String key){
        if(!Core.bundle.has(key)) return null;
        return table.table(t -> {
            if(!Vars.headless) t.image(Icon.info.getRegion()).size(20).color(Color.lightGray).scaling(Scaling.fit).padRight(8).padLeft(12);
            t.add("[lightgray]" + Core.bundle.get(key)).padBottom(10f);
        });
    }

    /** Adds a note under a value */
    private static Cell<?> note(Table table, String text){
        table.row();
        return table.table(t -> {
            if(!Vars.headless) t.image(Icon.arrowNoteSmall.getRegion()).size(15).color(Pal.stat).scaling(Scaling.fit).padRight(6).padLeft(12);
            t.add(text);
        });
    }

    //for AmmoListValue
    private static Cell<?> sep(Table table, String text){
        table.row();
        return table.add(text);
    }

    //for AmmoListValue
    private static String ammoStat(float val){
        return (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }

    private static String multStat(float val){
        return (val >= 1 ? "[stat]" : "[negstat]") + Strings.autoFixed(val, 2);
    }

    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }
    public static <T extends UnlockableContent> StatValue beamAmmo(ObjectMap<T, BulletType> map, boolean nested, boolean showUnit, @Nullable String blockName){
        return table -> {

            table.row();

            var orderedKeys = map.keys().toSeq();
            orderedKeys.sort();

            for(T t : orderedKeys){
                boolean compact = t instanceof UnitType && !showUnit || nested;

                BulletType type = map.get(t);

                if(type.spawnUnit != null && type.spawnUnit.weapons.size > 0){
                    ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), nested, false, blockName).display(table);
                    continue;
                }

                table.table(Styles.grayPanel, bt -> {
                    bt.left().top().defaults().padRight(3).left();
                    //no point in displaying unit icon twice
                    if(!compact && !(t instanceof Turret)){
                        bt.table(title -> {
                            title.image(icon(t)).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top().with(i -> withTooltip(i, t, false));

                            title.add(t.localizedName).padRight(10).left().top();

                            if(type.displayAmmoMultiplier && type.statLiquidConsumed > 0f){
                                title.add("[stat]" + fixValue(type.statLiquidConsumed / type.ammoMultiplier));
                            }
                        });
                        bt.row();
                    }

                    if(blockName != null && t != null){
                        tableInfo(bt, "block." + blockName + "." + t.name + ".info");
                        bt.row();
                    }

                    if(type.damage > 0 && (type.collides || type.splashDamage <= 0)){
                        bt.add(Core.bundle.format("bullet.damage", type.damage) + (type.continuousDamage() > 0 ?
                                "[lightgray] ~ [stat]" + Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized() : ""));
                    }

                    if(type.buildingDamageMultiplier != 1){
                        sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat((int)(type.buildingDamageMultiplier * 100 - 100))));
                    }

                    if(type.rangeChange != 0 && !compact){
                        sep(bt, Core.bundle.format("bullet.range", ammoStat(type.rangeChange / tilesize)));
                    }

                    if(type.shieldDamageMultiplier != 1){
                        sep(bt, Core.bundle.format("bullet.shielddamage", ammoStat((int)(type.shieldDamageMultiplier * 100 - 100))));
                    }

                    if(type.splashDamage > 0){
                        sep(bt, Core.bundle.format("bullet.splashdamage", (int)type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                    }

                    if(type.statLiquidConsumed <= 0f && !compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret turret) || turret.displayAmmoMultiplier)){
                        sep(bt, Core.bundle.format("bullet.multiplier", (int)type.ammoMultiplier));
                    }

                    if(!compact && !Mathf.equal(type.reloadMultiplier, 1f)){
                        int val = (int)(type.reloadMultiplier * 100 - 100);
                        sep(bt, Core.bundle.format("bullet.reload", ammoStat(val)));
                    }

                    if(type.knockback > 0){
                        sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                    }

                    if(type.healPercent > 0f){
                        sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                    }

                    if(type.healAmount > 0f){
                        sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                    }

                    if(type.pierce || type.pierceCap != -1){
                        sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                    }

                    if(type.incendAmount > 0){
                        sep(bt, "@bullet.incendiary");
                    }

                    if(type.homingPower > 0.01f){
                        sep(bt, "@bullet.homing");
                    }

                    // Showing the correct value for lightning damage is annoyinh
                    if(type.lightning > 0){
                        sep(bt, Core.bundle.format(
                                "bullet.lightning",
                                type.lightning,
                                type.lightningDamage < 0 ? type.damage : type.lightningDamage
                        ));
                    }

                    if(type instanceof LaserBulletType b && b.lightningSpacing > 0){
                        int count = (int)(b.length / b.lightningSpacing) * 2 + 2;
                        float damage = b.lightningDamage < 0 ? b.damage : b.lightningDamage;
                        sep(bt, Core.bundle.format("bullet.lightning", count, damage));
                        String length = b.lightningLengthRand > 0 ?
                                Strings.format("@[]-[stat]@", Strings.autoFixed(b.lightningLength, 2), Strings.autoFixed(b.lightningLength + b.lightningLengthRand, 2))
                                : Strings.autoFixed(b.lightningLength, 2);
                        note(bt, Core.bundle.format("bullet.lightninginterval", Strings.autoFixed(b.lightningSpacing / tilesize, 2), length)).left();
                    }

                    if(type instanceof EmpBulletType b && b.radius > 0f){
                        sep(bt, Core.bundle.format("bullet.empradius", Strings.fixed(b.radius / tilesize, 1)));
                        if(b.timeDuration > 0f && b.timeIncrease > 1f){
                            sep(bt, Core.bundle.format("bullet.empboost", Strings.autoFixed(b.timeIncrease * 100f, 2),
                                    Strings.autoFixed(b.timeDuration / 60f, 1)) + " " + StatUnit.seconds.localized());
                        }
                        if(b.timeDuration > 0f && b.powerSclDecrease < 1f){
                            sep(bt, Core.bundle.format("bullet.empslowdown",
                                    (b.powerSclDecrease < 1f ? "[negstat]" : "") + Strings.autoFixed((b.powerSclDecrease - 1f) * 100f, 2),
                                    Strings.autoFixed(b.timeDuration / 60f, 1)) + " " + StatUnit.seconds.localized());
                        }
                        if(!Mathf.equal(b.powerDamageScl, 1f)){
                            sep(bt, Core.bundle.format("bullet.empdamage", Strings.autoFixed(b.powerDamageScl * 100f, 2)));
                        }
                        if(b.hitUnits){
                            sep(bt, Core.bundle.format("bullet.empunitdamage",
                                    (b.unitDamageScl < 1f ? "[negstat]" : "") + Strings.autoFixed(b.unitDamageScl * 100f, 2)));
                        }
                    }

                    if(type.pierceArmor){
                        sep(bt, "@bullet.armorpierce");
                    }

                    if(!type.pierceArmor){
                        if(type.armorMultiplier != 1f){
                            if(type.armorMultiplier > 1f){
                                sep(bt, Core.bundle.format("bullet.armorweakness", (type.armorMultiplier)));
                            }else if(Mathf.sign(type.armorMultiplier) == 1){
                                sep(bt, Core.bundle.format("bullet.partialarmorpierce", (int)((1 - type.armorMultiplier) * 100)));
                            }else{
                                sep(bt, Core.bundle.format("bullet.antiarmor", (-type.armorMultiplier)));
                            }
                        }

                        if(type.blockArmorMultiplier != 1f){
                            if(type.blockArmorMultiplier > 1f){
                                sep(bt, Core.bundle.format("bullet.blockarmorweakness", (type.blockArmorMultiplier)));
                            }else if(Mathf.sign(type.blockArmorMultiplier) == 1){
                                sep(bt, Core.bundle.format("bullet.blockpartialarmorpierce", (int)((1 - type.blockArmorMultiplier) * 100)));
                            }else{
                                sep(bt, Core.bundle.format("bullet.blockantiarmor", (-type.blockArmorMultiplier)));
                            }
                        }
                    }

                    if(type.maxDamageFraction > 0){
                        sep(bt, Core.bundle.format("bullet.maxdamagefraction", (int)(type.maxDamageFraction * 100)));
                    }

                    if(type.suppressionRange > 0){
                        sep(bt, Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / tilesize, 1)));
                    }

                    // It is redundant to show the status effect if it can't be applied
                    if(type.status != StatusEffects.none && type.statusChance > 0f){
                        sep(bt, statusText(type.status, type.statusDuration, type.statusChance))
                                .with(c -> withTooltip(c, type.status));
                    }

                    if(!type.targetMissiles){
                        sep(bt, "@bullet.notargetsmissiles");
                    }

                    if(!type.targetBlocks){
                        sep(bt, "@bullet.notargetsbuildings");
                    }

                    if(type.intervalBullet != null){
                        bt.row();

                        Table ic = new Table();
                        ammo(ObjectMap.of(t, type.intervalBullet), true, false).display(ic);
                        Collapser coll = new Collapser(ic, true);
                        coll.setDuration(0.1f);

                        bt.table(it -> {
                            it.left().defaults().left();

                            it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                            it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                    if(type.fragBullet != null){
                        bt.row();

                        Table fc = new Table();
                        ammo(ObjectMap.of(t, type.fragBullet), true, false).display(fc);
                        Collapser coll = new Collapser(fc, true);
                        coll.setDuration(0.1f);

                        bt.table(ft -> {
                            ft.left().defaults().left();

                            ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                            ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                    if(type.spawnBullets != null && type.spawnBullets.size > 0){
                        bt.row();

                        Table sc = new Table();
                        for(BulletType spawn : type.spawnBullets){
                            if(spawn.showStats) ammo(ObjectMap.of(t, spawn), true, false).display(sc);
                        }
                        Collapser coll = new Collapser(sc, true);
                        coll.setDuration(0.1f);

                        bt.table(st -> {
                            st.left().defaults().left();

                            st.add(Core.bundle.format("bullet.spawnBullets", type.spawnBullets.size));
                            if(sc.getChildren().size > 0) st.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                        });
                        bt.row();
                        bt.add(coll);
                    }

                }).padLeft(5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
                table.row();
            }
        };
    }
}
