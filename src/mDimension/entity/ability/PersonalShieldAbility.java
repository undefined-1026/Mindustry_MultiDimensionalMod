package mDimension.entity.ability;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;

import static mindustry.Vars.tilesize;

public class PersonalShieldAbility extends Ability {
    public float max = 300;
    public float regen = 50/60f;
    public float explosionDamage = -1,explosionRange;
    public Color explosionEffectColor = null;
    public Effect explosionEffect = Fx.sparkExplosion;
    public Sound explosionSound =Sounds.explosionAfflict;

    public float explosionInterval = 5*60f;
    public float timer = 0;
    public boolean lastShield = false;

    public PersonalShieldAbility(float max, float regen) {
        this.max = max;
        this.regen = regen;
    }

    public PersonalShieldAbility(float max, float regen, float explosionDamage, float explosionRange, Color explosionEffectColor, float explosionInterval) {
        this.max = max;
        this.regen = regen;
        this.explosionDamage = explosionDamage;
        this.explosionRange = explosionRange;
        this.explosionEffectColor = explosionEffectColor;
        this.explosionInterval = explosionInterval;
    }

    public PersonalShieldAbility(float max, float regen, float explosionDamage, float explosionRange, Color explosionEffectColor, Effect explosionEffect, Sound explosionSound, float explosionInterval) {
        this.max = max;
        this.regen = regen;
        this.explosionDamage = explosionDamage;
        this.explosionRange = explosionRange;
        this.explosionEffectColor = explosionEffectColor;
        this.explosionEffect = explosionEffect;
        this.explosionSound = explosionSound;
        this.explosionInterval = explosionInterval;
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.add(abilityStat("shield", Strings.autoFixed(max, 1)));
        t.row();
        t.add(abilityStat("regensecond", Strings.autoFixed(regen*60f, 2)));
        if(explosionDamage>0){
            t.row();
            t.add(abilityStat("shieldexplosion", Strings.autoFixed(explosionDamage, 2)));
            t.row();
            t.add(Core.bundle.format("bullet.damage", Strings.autoFixed(explosionDamage, 2)));
            t.row();
            t.add(abilityStat("cooldown", Strings.autoFixed(explosionInterval/60f, 2)));
        }

    }

    @Override
    public void update(Unit unit) {
        if(unit.shield<max){
            unit.shield+=regen * Time.delta;
            if(unit.shield>max){
                unit.shield=max;
            }
        }
        if(explosionDamage>0 && timer<=0&& unit.shield<=0 && lastShield){
            boom(unit);
        } else if (explosionDamage>0 && timer>0) {
            timer-=Time.delta;
        }
        lastShield = unit.shield>0;

    }

    void boom(Unit unit){
        Damage.damage(unit.team,unit.x,unit.y,explosionRange,explosionDamage);
        explosionEffect.at(unit.x,unit.y,unit.rotation,explosionEffectColor == null?unit.team.color:explosionEffectColor);
        explosionSound.at(unit.x,unit.y, Mathf.range(0.8f,1.1f));
        timer = explosionInterval;
    }

    @Override
    public void death(Unit unit) {
        if(lastShield && explosionDamage>0){
            boom(unit);
        }
    }
}
