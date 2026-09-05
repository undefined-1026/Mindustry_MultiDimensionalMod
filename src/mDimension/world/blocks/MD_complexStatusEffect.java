package mDimension.world.blocks;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mDimension.meta.MD_Stat;
import mDimension.meta.MD_StatUnit;
import mDimension.tool.ReflectUtils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;

public class MD_complexStatusEffect extends StatusEffect {
    public float percentageDamage = 0;
    public float percentageShieldDamage = 0;
    public float armorAdditional = 0;
    public float armorMultiplier = 1;
    public float stackingTime = 0;
    public float maxStacking = 30*60f;
    public Acts act =e -> {};
    public Draws draw =e -> {};


    public MD_complexStatusEffect(String name){
        super(name);
    }
    public interface Acts{
        void acting(Unit u);
    }
    public interface Draws{
        void drawing(Unit u);
    }


    @Override
    public void setStats(){

        super.setStats();
        if(!reactive) {
            if(armorMultiplier !=1) {
                stats.addMultModifier(MD_Stat.armorMultiplier, armorMultiplier);
            }
            if(armorAdditional !=0) {
                stats.add(MD_Stat.armorAdditional, armorAdditional);
            }

            if (percentageDamage > 0) {
                stats.add(MD_Stat.percentageDamage, percentageDamage * 6000f, MD_StatUnit.percentsecond);
            } else if (percentageDamage < 0) {
                stats.add(MD_Stat.percentageReply, -1 * percentageDamage * 6000f, MD_StatUnit.percentsecond);
            }
            if(percentageShieldDamage !=0) {
                stats.add(MD_Stat.percentageShieldDamage, percentageShieldDamage * 6000f, MD_StatUnit.percentsecond);
            }

        }else {
            if (percentageDamage > 0) {
                stats.add(MD_Stat.percentageDamage, percentageDamage*100f,StatUnit.percent);
            } else if (percentageDamage < 0) {
                stats.add(MD_Stat.percentageReply, -1 * percentageDamage*100f,StatUnit.percent);
            }
            if(percentageShieldDamage !=0) {
                stats.add(MD_Stat.percentageShieldDamage, percentageShieldDamage*100f, StatUnit.percent);
            }
        }

    }
    @Override
    public void update(Unit unit, StatusEntry entry){



        if(unit.dead) return;
        if(damage > 0){
            unit.damageContinuousPierce(damage);
        }else if(damage < 0){ //heal unit
            unit.heal(-1f * damage * Time.delta);
        }

        if(intervalDamageTime > 0){
            entry.damageTime += Time.delta;
            if(entry.damageTime >= intervalDamageTime){
                entry.damageTime %= intervalDamageTime;
                if(intervalDamagePierce){
                    unit.damagePierce(intervalDamage);
                }else{
                    unit.damage(intervalDamage);
                }
            }
        }
        /// percentageDamage
        if(percentageDamage > 0){
            unit.damageContinuousPierce(unit.maxHealth*percentageDamage);
        }else if(percentageDamage < 0){ //heal unit
            unit.heal(-1f * percentageDamage *unit.maxHealth* Time.delta);
        }
        if(percentageShieldDamage > 0){
            if(unit.shield > 1) {
                unit.damageContinuousPierce(unit.shield * percentageShieldDamage);
            }else unit.shield = 0;
        }
        /// armor


        if(armorAdditional!=0||armorMultiplier!=1) {
            if(unit.armorOverride == -1)unit.armorOverride =unit.type.armor;
            //unit.armorOverride += (unit.type.armor*(armorMultiplier-1))+armorAdditional;
            unit.armorOverride+=unit.type.armor*(armorMultiplier-1)+armorAdditional;
            //if(unit.armorOverride<=0)unit.armor = 0;
            if(unit.armorOverride <=0) unit.armorOverride = 0.0001f;


        }

        act.acting(unit);
        if(!Vars.headless && effect != Fx.none && Mathf.chanceDelta(effectChance) && !unit.inFogTo(Vars.player.team())){
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
            effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
        }

    }

    @Override
    public void applied(Unit unit, float time, boolean extend) {
        super.applied(unit, time, extend);
        if(unit != null && stackingTime>0 && time <maxStacking){
            Seq<?> entrys;
            entrys = (Seq<?>) ReflectUtils.getValue(unit,"statuses");
            if(entrys == null)return;
            for(int i=0;i<entrys.size;i++){
                StatusEntry entry = (StatusEntry) entrys.get(i);
                if(entry.effect == this){
                    entry.time = Math.min(entry.time+stackingTime,maxStacking);
                    return;
                }
            }
        }
    }

    @Override
    public void onRemoved(Unit unit){
        unit.armor = unit.type.armor;
    }

    @Override
    public void draw(Unit u){
        draw.drawing(u);
    }
}
