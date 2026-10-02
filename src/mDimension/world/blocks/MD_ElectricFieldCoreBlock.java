package mDimension.world.blocks;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.tool.Drawff;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.tilesize;

public class MD_ElectricFieldCoreBlock extends MD_CoreBlock {
    public float powerProduction = 5f;
    public float lightningTime = 30f;
    public int lightnings = 10;
    public Effect lightningEffect = Fx.chainLightning;
    public Sound lightningSound = Sounds.shootEnergyField;
    public float lightningSoundPitMin = 0.9f;
    public float lightningSoundPitMax = 1.1f;
    public float lightningRadius = 30*8f;
    public float ballRadius = 4.5f;
    public float lightningDamage = 80f;
    public Color lightningColor = null;
    public float statusDuration = 60f * 2f;
    public StatusEffect lightningStatu = StatusEffects.none;

    public MD_ElectricFieldCoreBlock(String name){
        super(name);
        outputsPower = true;
        consumesPower = false;

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize,y* tilesize,lightningRadius, Pal.placing);
    }

    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower){
            addBar("power", (md_ElectricFieoldCoreBuild entity) -> new Bar(() ->
                    Core.bundle.format("bar.poweroutput",
                            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
                    () -> Pal.powerBar,
                    ()->entity.warmup));
        }
    }
    public class md_ElectricFieoldCoreBuild extends  CoreBuild {
        public float warmup = 0f;
        public float lightningProgress = 0f;

        @Override
        public float getPowerProduction(){
            return powerProduction * warmup;
        }
        @Override
        public void updateTile(){
            if(warmup<1f){
                warmup+= Time.delta/600f;
                warmup = warmup>1f?1:warmup;
            }
            lightningProgress +=Time.delta;
            if(lightningProgress>=lightningTime){
                lightningProgress = 0;
                lightningpublic();
            }
        }

        public void lightningpublic(){
            Seq<Unit> enemies = new Seq<>();
            Units.nearbyEnemies(team, x, y, lightningRadius, enemy -> {
                if (!enemy.dead() && enemy.isValid() && enemy.targetable(team)) {
                    enemies.add(enemy);
                }
            });
            int remainingStrikes = lightnings;
            if (enemies.size > 0) {
                // 随机生成初始索引
                int currentIndex = Mathf.random(enemies.size - 1);

                while (remainingStrikes > 0 && enemies.size > 0 && lightnings-remainingStrikes < enemies.size) {

                    currentIndex %= enemies.size;
                    if (currentIndex < 0) currentIndex = 0;

                    Unit target = enemies.get(currentIndex);

                    if (target.dead() || !target.isValid() || !target.targetable(team)) {
                        enemies.remove(currentIndex);
                        if (enemies.size == 0) break;
                        continue;
                    }
                    Vec2 endPoint = new Vec2(x,y);
                    lightningEffect.at(target.getX(), target.getY(), 0f, lightningColor == null?team.color:lightningColor, endPoint);
                    target.damage(lightningDamage * warmup);

                    lightningSound.at(x,y,Mathf.randomSeed((long) (Time.time+currentIndex),lightningSoundPitMin,lightningSoundPitMax));
                    if (lightningStatu != null && lightningStatu != StatusEffects.none) {
                        target.apply(lightningStatu, statusDuration);
                    }
                    remainingStrikes--;
                    currentIndex++;
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
            float rad = warmup<0.99f?(float) (ballRadius*Math.pow(Math.sin(warmup*Math.PI/2),0.8f)):ballRadius;
            rad += Mathf.sin(Time.time/60,1f,0.25f);
            Draw.z(Layer.effect-1f);
            Draw.color(this.team.color,Color.white,0.1f);
            Draw.alpha(1F);
            Fill.circle(x,y,rad);
            if(rad>0.1f) Drawff.prominence(
                    id,
                    x,y,
                    12,12,rad*1.4f,
                    rad*1.2f,rad,6);
            Lines.stroke(warmup*4.5f/ballRadius);
            for(int i = 0;i<5;i++){
                Lines.arc(x,y,rad*1.22f,1/6.5f,(Time.time)/3 + i*(360f/5f));
            }
//            if(Time.progress%15<=Time.delta&&!Vars.state.isPaused()){
//                md_Fx.leakage.at(x,y,0,team.color.cpy().a(0.9f),new float[]{rad,rad,1});
//            }
            Draw.color(Color.white.cpy().lerp(this.team.color, 0.4F));
            Fill.circle(x,y,rad*0.6f);


        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x,y,lightningRadius,Pal.placing);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(warmup);
            write.f(lightningProgress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            warmup = read.f();
            lightningProgress = read.f();

        }
    }
}
