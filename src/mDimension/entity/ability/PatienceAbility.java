package mDimension.entity.ability;

import arc.Core;
import arc.func.Cons2;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import mDimension.meta.MD_Stat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import arc.graphics.Color;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;


public class PatienceAbility extends Ability {
    public float maxEffectThreshold = 0.4f;
    public String suffix = "-patience";
    public float health = 1f;
    public float damage = 1f;
    public float speed = 1f;
    public float armor = 0;
    public TextureRegion heatRegion;
    public Cons2<Unit,Float> drawer = (u,scl)->{};
    public Effect effect = Fx.none;
    public Color effectColor = Color.white;
    public boolean parentizeEffect = false;
    public boolean drawHeat = false;
    public Color heatColor = Pal.turretHeat;
    public float effectChance = 0.15f;

    @Override
    public void init(UnitType type) {
        if(drawHeat)heatRegion = Core.atlas.find(type.name+suffix);
    }

    public PatienceAbility(){
    }

    public PatienceAbility(float maxEffectThreshold){
        this.maxEffectThreshold = maxEffectThreshold;
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.row();
        t.add("[stat]"+(int)(maxEffectThreshold*100)+ MD_Stat.maxEffectThreshold.localized());
        if(health>1){t.row();
        t.add("[white]"+Stat.healthMultiplier.localized()+":[stat]"+(int)(health*100)+" "+ StatUnit.percent.localized());}
        if(damage>1){t.row();
        t.add("[white]"+Stat.damageMultiplier.localized()+":[stat]"+(int)(damage*100)+" "+ StatUnit.percent.localized());}
        if(speed>1){t.row();
        t.add("[white]"+Stat.speedMultiplier.localized()+":[stat]"+(int)(speed*100)+" "+ StatUnit.percent.localized());}
        if(armor>0){t.row();
        t.add("[white]"+Stat.armor.localized()+":[stat]"+(armor));}
        if(health==1&&speed==1&&damage==1&&armor==0){
            t.row();
            t.add("[stat]滚木");
        }


    }

    @Override
    public void update(Unit unit) {
        if(unit.damaged()){
            float scl =Math.min(1f,(1- unit.health/unit.maxHealth)/(1-maxEffectThreshold));
            if(health>1f)unit.healthMultiplier += (health-1)*scl;
            if(damage>1f)unit.damageMultiplier += (damage-1)*scl;
            if(speed >1f)unit.speedMultiplier += (speed-1)*scl;
            if(armor>0){
                if(unit.armorOverride == -1)unit.armorOverride =unit.type.armor;
                unit.armorOverride+=armor*scl;
                if(unit.armorOverride <=0) unit.armorOverride = 0.0001f;
            }

            if(!Vars.headless && effect != Fx.none && Mathf.chanceDelta(effectChance*scl*scl) && !unit.inFogTo(Vars.player.team())){
                Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
                effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, effectColor, parentizeEffect ? unit : null);
            }
        }
//        else{
//            unit.healthMultiplier += 1;
//            unit.damageMultiplier += 1;
//            unit.speedMultiplier += 1;
//            unit.armorOverride(unit.armor);
//        }
    }

    @Override
    public void draw(Unit unit) {
        float scl =Math.min(1f,(1- unit.health/unit.maxHealth)/(1-maxEffectThreshold));
        if(drawHeat){
            float z = Draw.z();
            //Draw.color(heatColor);
            //Draw.alpha(scl);
            float heatLayer = unit.type.flying?unit.type.flyingLayer+0.01f: Layer.groundUnit+0.01f;
            //Draw.rect(heatRegion,unit.x,unit.y,unit.rotation()-90);
            Drawf.additive(heatRegion, heatColor.write(Tmp.c1).a(scl), unit.x, unit.y, unit.rotation()-90, heatLayer);

            Draw.reset();
            Draw.z(z);
        }
        drawer.get(unit,scl);
        Draw.reset();
    }
}
