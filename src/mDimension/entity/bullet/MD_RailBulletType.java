package mDimension.entity.bullet;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

public class MD_RailBulletType extends BulletType {
    public Effect pierceEffect = Fx.hitBulletSmall, pointEffect = Fx.none, lineEffect = Fx.none;
    public Effect endEffect = Fx.none;
    public Effect endEffect2 = Fx.none;

    public float length = 100f;

    public float pointEffectSpace = 15f,pointEffectSpaceRand = 0;
    public boolean pinpoint = false;

    public MD_RailBulletType(){
        speed = 0f;
        pierceBuilding = true;
        pierce = true;
        reflectable = false;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        collides = false;
        keepVelocity = false;
        lifetime = 1f;
        delayFrags = true;
    }

    @Override
    protected float calculateRange(){
        return length;
    }

    @Override
    public void handlePierce(Bullet b, float initialHealth, float x, float y){
        float sub = Math.max(initialHealth * pierceDamageFactor, 0);

        if(b.damage <= 0){
            b.fdata = Math.min(b.fdata, b.dst(x, y));
            return;
        }

        if(b.damage > 0){
            pierceEffect.at(x, y, b.rotation());
        }

        //subtract health from each consecutive pierce
        b.damage -= Math.min(b.damage, sub);

        //bullet was stopped, decrease furthest distance
        if(b.damage <= 0f){
            b.fdata = Math.min(b.fdata, b.dst(x, y));
        }
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        float length = pinpoint?Math.min(this.length,2f+Mathf.len(b.aimX-b.x,b.aimY-b.y)):this.length;

        b.fdata = length;
        Damage.collideLine(b, b.team, b.x, b.y, b.rotation(), length, false, false, pierceCap);
        float resultLen = b.fdata;

        Vec2 nor = Tmp.v1.trns(b.rotation(), 1f).nor();
        if(pointEffect != Fx.none){
            for(float i = 0; i <= resultLen; i += pointEffectSpace+(pointEffectSpaceRand>0?Mathf.random(pointEffectSpaceRand):0)){
                pointEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation(), trailColor,i);
            }
        }

        boolean any = b.collided.size > 0;

        if(!pinpoint&& b.collided.size < pierceCap && endEffect != Fx.none){
            endEffect.at(b.x + nor.x * resultLen, b.y + nor.y * resultLen, b.rotation(), hitColor);
        }else if((pinpoint || pierceCap>0) && endEffect2 != Fx.none){
            endEffect2.at(b.x + nor.x * resultLen, b.y + nor.y * resultLen, b.rotation(), hitColor);
        }


        if(lineEffect != Fx.none){
            lineEffect.at(b.x, b.y, b.rotation(), hitColor, new Vec2(b.x, b.y).mulAdd(nor, resultLen));
        }
    }

    @Override
    public boolean testCollision(Bullet bullet, Building tile){
        return bullet.team != tile.team;
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
        handlePierce(b, initialHealth, x, y);
    }
}
