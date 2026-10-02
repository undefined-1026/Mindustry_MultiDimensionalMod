package mDimension.world.blocks.payload;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ai.types.AssemblerAI;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import mindustry.world.modules.ItemModule;

import static mDimension.content.MD_blocks.modname;
import static mindustry.Vars.*;

public class MD_UnitAssembler extends UnitAssembler {
    public MD_UnitAssembler(String name) {
        super(name);
        configurable = true;
        config(UnitType.class,(MD_UnitAssemblerBuild b,UnitType c)->{
            b.unitType = c;
        });
        configClear((MD_UnitAssemblerBuild b)->{
            b.unitType = null;
        });
    }
    public Seq<Block> fitModules = new Seq<>();
    public ObjectIntMap<UnitType> planTiers = new ObjectIntMap<>();
    public AssemblerUnitPlan setTier(int tier,AssemblerUnitPlan plan){
        planTiers.put(plan.unit,tier);
        return plan;
    }
    public AssemblerUnitPlan[] setTier(int tier,AssemblerUnitPlan... plans){
        for(var plan:plans)planTiers.put(plan.unit,tier);
        return plans;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.output);
        stats.add(Stat.output, table -> {
            table.row();

            int tier = 0;
            for(var plan : plans){
                table.table(Styles.grayPanel, t -> {

                    if(plan.unit.isBanned()){
                        t.image(Icon.cancel).color(Pal.remove).size(40).pad(10);
                        return;
                    }

                    if(plan.unit.unlockedNow()){
                        t.image(plan.unit.uiIcon).scaling(Scaling.fit).size(40).pad(10f).left().with(i -> StatValues.withTooltip(i, plan.unit));
                        t.table(info -> {
                            info.defaults().left();
                            info.add(plan.unit.localizedName);
                            info.row();
                            info.add(Strings.autoFixed(plan.time / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                            int ttier = planTiers.get(plan.unit);
                            if(ttier > 0){
                                info.row();
                                info.add(Stat.moduleTier.localized() + ": " + ttier).color(Color.lightGray);
                            }
                        }).left();

                        t.table(req -> {
                            req.add().grow(); //it refuses to go to the right unless I do this. please help.

                            req.table(solid -> {
                                int length = 0;
                                if(plan.itemReq != null){
                                    for(int i = 0; i < plan.itemReq.length; i++){
                                        if(length % 6 == 0){
                                            solid.row();
                                        }
                                        solid.add(StatValues.stack(plan.itemReq[i])).pad(5);
                                        length++;
                                    }
                                }

                                for(int i = 0; i < plan.requirements.size; i++){
                                    if(length % 6 == 0){
                                        solid.row();
                                    }
                                    solid.add(StatValues.stack(plan.requirements.get(i))).pad(5);
                                    length++;
                                }
                            }).right();

                            LiquidStack[] stacks = plan.liquidReq;
                            if(stacks != null){
                                for(int i = 0; i < plan.liquidReq.length; i++){
                                    req.row();

                                    req.add().grow(); //another one.

                                    req.add(StatValues.displayLiquid(stacks[i].liquid, stacks[i].amount * 60f, true)).right();
                                }
                            }
                        }).grow().pad(10f);
                    }else{
                        t.image(Icon.lock).color(Pal.darkerGray).size(40).pad(10);
                    }
                }).growX().pad(5);
                table.row();
                tier++;
            }
        });
        stats.add(Stat.booster,table->{
            for(var mod:fitModules) {
                table.row();
                table.table(Styles.grayPanel, t -> {
                    if(mod.isBanned()){
                        t.image(Icon.cancel).color(Pal.remove).size(40).pad(10);
                    }else if(!mod.unlockedNow()){
                        t.image(Icon.lock).color(Pal.darkerGray).size(40).pad(10);
                    }else {
                        t.image(mod.uiIcon).scaling(Scaling.fit).size(40).pad(10f).left().with(i -> StatValues.withTooltip(i, mod));
                        t.table(info -> {
                            info.defaults().left();
                            info.add(mod.localizedName);
                            info.row();
                            info.add(Core.bundle.get("stat.moduletier") + ":" + (mod instanceof UnitAssemblerModule module ? module.tier : 0)).color(Color.lightGray);
                        }).left();
                    }
                }).growX().pad(5);
            }
        });
    }

    public class MD_UnitAssemblerBuild extends UnitAssemblerBuild{
        public UnitType unitType;
        public float invalidWarmup2 = 0;

        @Override
        public AssemblerUnitPlan plan() {
            if(unitType == null)return plans.get(0);
            var res = plans.find(p->p.unit == unitType);
            return res == null?plans.get(0):res;
        }


        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(MD_UnitAssembler.this,table,
                    Vars.content.units().select(u->plans.contains(p->p.unit == u)).sort(u->u.id+planTiers.get(u)*1000),
                    ()->this.unitType,
                    this::configure,
                    selectionRows, selectionColumns
                    );
        }

        @Override
        public void configure(Object value) {
            this.progress = this.warmup = 0f;
            super.configure(value);
        }

        @Override
        public float efficiencyScale() {
            return unitType == null?0f:1f;
        }

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && unitType != null;
        }

        @Override
        public boolean moduleFits(Block other, float ox, float oy, int rotation) {
            return (fitModules.isEmpty() || fitModules.contains(other)) && super.moduleFits(other, ox, oy, rotation);
        }

        public boolean checkCanBuild() {
            return unitType != null && planTiers.get(plan().unit,0)<=this.currentTier;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            invalidWarmup2 = Mathf.lerpDelta(invalidWarmup2, checkCanBuild() ? 0f : 1f, 0.1f);

        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input conveyors
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());

            Draw.z(Layer.blockOver);

            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);

            Draw.rect(topRegion, x, y);

            if(isPayload()) return;

            //draw drone construction
            if(droneWarmup > 0.001f){
                Draw.draw(Layer.blockOver + 0.2f, () -> {
                    Drawf.construct(this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f);
                });
            }

            Vec2 spawn = getUnitSpawn();
            float sx = spawn.x, sy = spawn.y;

            var plan = plan();

            //draw the unit construction as outline
            if(unitType != null)Draw.draw(Layer.blockBuilding, () -> {
                Draw.color(Pal.accent, warmup);

                Shaders.blockbuild.region = plan.unit.fullIcon;
                Shaders.blockbuild.time = Time.time;
                Shaders.blockbuild.alpha = warmup;
                //margin due to units not taking up whole region
                Shaders.blockbuild.progress = Mathf.clamp(progress + 0.05f);

                Draw.rect(plan.unit.fullIcon, sx, sy, rotdeg() - 90f);
                Draw.flush();
                Draw.color();
                Shaders.blockbuild.alpha = 1f;
            });

            Draw.reset();

            Draw.z(Layer.buildBeam);

            //draw unit silhouette
            Draw.mixcol(Tmp.c1.set(Pal.accent).lerp(Pal.remove, Math.min(1f,invalidWarmup + invalidWarmup2)), 1f);
            Draw.alpha(Math.min(powerWarmup, sameTypeWarmup));
            if(unitType != null)Draw.rect(plan.unit.fullIcon, spawn.x, spawn.y, rotdeg() - 90f);

            //build beams do not draw when invalid
            Draw.alpha(Math.min(1f - invalidWarmup, warmup));

            //draw build beams
            for(var unit : units){
                if(!((AssemblerAI)unit.controller()).inPosition()) continue;

                float
                        px = unit.x + Angles.trnsx(unit.rotation, unit.type.buildBeamOffset),
                        py = unit.y + Angles.trnsy(unit.rotation, unit.type.buildBeamOffset);

                Drawf.buildBeam(px, py, spawn.x, spawn.y, plan.unit.hitSize/2f);
            }

            //fill square in middle
            Fill.square(spawn.x, spawn.y, plan.unit.hitSize/2f);

            Draw.reset();

            Draw.z(Layer.buildBeam);

            float fulls = areaSize * tilesize/2f;

            //draw full area
            Lines.stroke(2f, Tmp.c4.set(Pal.accent).lerp(Pal.remove, invalidWarmup2).a(1f));
            Draw.alpha(powerWarmup);
            Drawf.dashRectBasic(spawn.x - fulls, spawn.y - fulls, fulls*2f, fulls*2f);

            Draw.reset();

            float outSize = plan.unit.hitSize + 9f;

            if(invalidWarmup > 0){
                //draw small square for area
                Lines.stroke(2f, Tmp.c3.set(Pal.accent).lerp(Pal.remove, invalidWarmup).a(invalidWarmup));
                Drawf.dashSquareBasic(spawn.x, spawn.y, outSize);
            }

            Draw.reset();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return super.acceptPayload(source, payload) && unitType != null;
        }

        @Override
        public void display(Table table){
            table.table((t) -> {
                t.left();
                t.add(new Image(this.block.getDisplayIcon(this.tile))).scaling(Scaling.fit).size(32.0F);
                t.labelWrap(this.block.getDisplayName(this.tile)).left().width(190.0F).padLeft(5.0F);
            }).growX().left();
            table.row();
            if (this.team == Vars.player.team()) {
                table.table((bars) -> {
                    bars.defaults().growX().height(18.0F).pad(4.0F);
                    this.displayBars(bars);
                }).growX();
                table.row();
                table.table(this::displayConsumption).growX();
                boolean displayFlow = (this.block.category == Category.distribution || this.block.category == Category.liquid) && this.block.displayFlow;
                if (displayFlow) {
                    String ps = " " + StatUnit.perSecond.localized();
                    ItemModule flowItems = this.flowItems();
                    if (flowItems != null) {
                        table.row();
                        table.left();
                        table.table((l) -> {
                            Bits current = new Bits();
                            Runnable rebuild = () -> {
                                l.clearChildren();
                                l.left();

                                for(Item item : Vars.content.items()) {
                                    if (flowItems.hasFlowItem(item)) {
                                        l.image(item.uiIcon).scaling(Scaling.fit).padRight(3.0F);
                                        l.label(() -> flowItems.getFlowRate(item) < 0.0F ? "..." : Strings.fixed(flowItems.getFlowRate(item), 1) + ps).color(Color.lightGray);
                                        l.row();
                                    }
                                }

                            };
                            rebuild.run();
                            l.update(() -> {
                                for(Item item : Vars.content.items()) {
                                    if (flowItems.hasFlowItem(item) && !current.get(item.id)) {
                                        current.set(item.id);
                                        rebuild.run();
                                    }
                                }

                            });
                        }).left();
                    }

                    if (this.liquids != null) {
                        table.row();
                        table.left();
                        table.table((l) -> {
                            Bits current = new Bits();
                            Runnable rebuild = () -> {
                                l.clearChildren();
                                l.left();

                                for(Liquid liquid : Vars.content.liquids()) {
                                    if (this.liquids.hasFlowLiquid(liquid)) {
                                        l.image(liquid.uiIcon).scaling(Scaling.fit).size(32.0F).padRight(3.0F);
                                        l.label(() -> this.liquids.getFlowRate(liquid) < 0.0F ? "..." : Strings.fixed(this.liquids.getFlowRate(liquid), 1) + ps).color(Color.lightGray);
                                        l.row();
                                    }
                                }

                            };
                            rebuild.run();
                            l.update(() -> {
                                for(Liquid liquid : Vars.content.liquids()) {
                                    if (this.liquids.hasFlowLiquid(liquid) && !current.get(liquid.id)) {
                                        current.set(liquid.id);
                                        rebuild.run();
                                    }
                                }

                            });
                        }).left();
                    }
                }

                if (Vars.net.active() && this.lastAccessed != null) {
                    table.row();
                    table.add(Core.bundle.format("lastaccessed", new Object[]{this.lastAccessed})).growX().wrap().left();
                }

                table.marginBottom(-5.0F);
            }

            if(team != player.team()) return;

            table.row();
            table.table(t -> {
                t.left().defaults().left();


                t.label(() -> "[accent] -> []" + (unitType == null?'\ue815':unit().emoji() + " " + unit().localizedName));
            }).pad(4).padLeft(0f).fillX().left();
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.i(unitType == null?-1: unitType.id);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            int id = r.i();
            unitType = id<0?null:content.unit(id);
        }
    }
}
