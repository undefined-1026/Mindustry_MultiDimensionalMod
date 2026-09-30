package mDimension.world.weapons;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ai.types.MissileAI;
import mindustry.audio.SoundLoop;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Entityc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;

import static mindustry.Vars.headless;
//barrel amount is recoils;
public class GatlingWeapon extends Weapon {

    public float recoilTravel = 2f;
    public float baseAngle = 0;
    public float shootAngle = 180f;
    public float inclineRotation = 0;
    public float bx,by,width;
    public float keepWarmup = 45f;
    public float overheatSpeed = 0.01f,overheatDurationTime = 3*60f;
    public boolean inversion = false;
    public Color overheatColor = Pal.turretHeat;
    public TextureRegion barrel,barrelHeat;

    @Override
    public void load() {
        super.load();
        barrel = Core.atlas.find(this.name+"-barrel");
        barrelHeat = Core.atlas.find(this.name+"-barrel-heat");
    }

    public GatlingWeapon(String name) {
        super(name);
        shootWarmupSpeed = 0.03f;
    }
    {
        mountType = GatlingWeaponMount::new;
    }

    @Override
    public void flip() {
        super.flip();
        inclineRotation*=-1;
        bx*=-1;
        shootAngle = Mathf.mod(180-shootAngle,360f);
        inversion = !inversion;
    }

    protected void bullet(Unit unit, WeaponMount m, float xOffset, float yOffset, float angleOffset, Mover mover){
        if(!unit.isAdded()) return;
        var mount = (GatlingWeaponMount)m;
        mount.charging = false;
        float
                xSpread = Mathf.range(xRand),
                ySpread = Mathf.range(yRand),
                weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
                mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
                bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX + xOffset + xSpread, this.shootY + yOffset + ySpread),
                bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX + xOffset + xSpread, this.shootY + yOffset + ySpread),
                shootAngle = bulletRotation(unit, mount, bulletX, bulletY) + angleOffset,
                baseLife = (1f - lifeRnd) + Mathf.random(lifeRnd) + extraLife,
                lifeScl = bullet.scaleLife ? baseLife * Mathf.clamp(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY) / bullet.range) : baseLife,
                angle = shootAngle + Mathf.range(inaccuracy + bullet.inaccuracy);

        Entityc shooter = unit.controller() instanceof MissileAI ai ? ai.shooter : unit; //Pass the missile's shooter down to its bullets
        mount.bullet = bullet.create(unit, shooter, unit.team, bulletX, bulletY, angle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd) + extraVelocity, lifeScl, null, mover, mount.aimX, mount.aimY, mount.target);
        handleBullet(unit, mount, mount.bullet);

        if(!continuous){
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax), shootSoundVolume);
        }else{
            initialShootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax), shootSoundVolume);
        }

        if(mount.allowShootEffects){
            ejectEffect.at(mountX, mountY, angle * Mathf.sign(this.x));
            bullet.shootEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);
            bullet.smokeEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);
        }

        unit.vel.add(Tmp.v1.trns(shootAngle + 180f, bullet.recoil));
        Effect.shake(shake, shake, bulletX, bulletY);
        mount.recoil = 1f;
        if(recoils > 0){
            float step = 360f/recoils;
            float delta = Mathf.mod(this.shootAngle-mount.angle,360f);
            int index = Mathf.round(delta/step)%recoils;
            mount.recoils[index] = 1f;
        }
        mount.heat = 1f;
        mount.overheat += overheatSpeed;
    }
    @Override
    public void update(Unit unit, WeaponMount m){
        var mount = (GatlingWeaponMount)m;
        if(mount.overheat>1f){
            mount.isOverheat = true;
        }
        if(mount.overheat>0){
            mount.overheat-=Time.delta*(1/overheatDurationTime) * (1-mount.warmup);
            if(mount.overheat<=0){
                mount.overheat = 0;
                mount.isOverheat = false;
            }
        }
        if(mount.isOverheat){
            mount.heat = Mathf.approachDelta(mount.heat,mount.overheat,1/60f);
        }
        boolean can = unit.canShoot() && !mount.isOverheat;

        float lastReload = mount.reload;
        mount.angle = (mount.angle + (inversion?-1:1) * (360f/reload) * Time.delta * unit.reloadMultiplier * mount.warmup/recoils * shoot.shots)%360f;
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier * mount.warmup, 0);
        mount.recoil = Mathf.approachDelta(mount.recoil, 0, unit.reloadMultiplier / recoilTime);
        if(recoils > 0){
            if(mount.recoils == null) mount.recoils = new float[recoils];
            for(int i = 0; i < recoils; i++){
                mount.recoils[i] = Mathf.approachDelta(mount.recoils[i], 0, unit.reloadMultiplier / recoilTime);
            }
        }
        mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed);
        mount.charge = mount.charging && shoot.firstShotDelay > 0 ? Mathf.approachDelta(mount.charge, 1, 1 / shoot.firstShotDelay) : 0;

        float warmupTarget = (can && mount.shoot && !mount.isOverheat) || (continuous && mount.bullet != null) || mount.charging ? 1f : 0f;
        mount.keepWarmup = Mathf.approachDelta(mount.keepWarmup, warmupTarget, 1/keepWarmup);
        warmupTarget = mount.keepWarmup>0.01f?1f:warmupTarget;
        if(linearWarmup){
            mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
        }else{
            mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
        }

        float
                mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

        //find a new target
        if(!controllable && autoTarget){
            if((mount.retarget -= Time.delta) <= 0f){
                mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
                mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
            }

            if(mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)){
                mount.target = null;
            }

            boolean shoot = false;

            if(mount.target != null){
                shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize()/2f : 0f)) && can;

                if(predictTarget){
                    Vec2 to = Predict.intercept(unit, mount.target, bullet);
                    mount.aimX = to.x;
                    mount.aimY = to.y;
                }else{
                    mount.aimX = mount.target.x();
                    mount.aimY = mount.target.y();
                }
            }

            mount.shoot = mount.rotate = shoot;

            //note that shooting state is not affected, as these cannot be controlled
            //logic will return shooting as false even if these return true, which is fine
        }

        //rotate if applicable
        if(rotate && (mount.rotate || mount.shoot) && can){
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90,  x, y),
                    axisY = unit.y + Angles.trnsy(unit.rotation - 90,  x, y);

            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
            if(rotationLimit < 360){
                float dst = Angles.angleDist(mount.rotation, baseRotation);
                if(dst > rotationLimit/2f){
                    mount.rotation = Angles.moveToward(mount.rotation, baseRotation, dst - rotationLimit/2f);
                }
            }
        }else if(!rotate){
            mount.rotation = baseRotation;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }

        float
                weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
                bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
                shootAngle = bulletRotation(unit, mount, bulletX, bulletY);

        if(alwaysShooting) mount.shoot = true;

        //update continuous state
        if(continuous && mount.bullet != null){
            if(!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet){
                mount.bullet = null;
            }else{
                mount.bullet.rotation(weaponRotation + 90);
                mount.bullet.set(bulletX, bulletY);
                mount.reload = reload;
                mount.recoil = 1f;
                unit.vel.add(Tmp.v1.trns(mount.bullet.rotation() + 180f, mount.bullet.type.recoil * Time.delta));
                if(shootSound != Sounds.none && !headless){
                    if(mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                    mount.sound.update(bulletX, bulletY, true);
                }

                //target length of laser
                float shootLength = Math.min(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY), range());
                //current length of laser
                float curLength = Mathf.dst(bulletX, bulletY, mount.bullet.aimX, mount.bullet.aimY);
                //resulting length of the bullet (smoothed)
                float resultLength = Mathf.approachDelta(curLength, shootLength, aimChangeSpeed);
                //actual aim end point based on length
                Tmp.v1.trns(shootAngle, mount.lastLength = resultLength).add(bulletX, bulletY);

                mount.bullet.aimX = Tmp.v1.x;
                mount.bullet.aimY = Tmp.v1.y;

                if(alwaysContinuous && mount.shoot){
                    mount.bullet.time = mount.bullet.lifetime * mount.bullet.type.optimalLifeFract * mount.warmup;
                    mount.bullet.keepAlive = true;

                    unit.apply(shootStatus, shootStatusDuration);
                }
            }
        }else{
            //heat decreases when not firing
            mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);

            if(mount.sound != null){
                mount.sound.update(bulletX, bulletY, false);
            }
        }

        //flip weapon shoot side for alternating weapons
        boolean wasFlipped = mount.side;
        if(otherSide >= 0 && alternate && mount.side == flipSprite && otherSide < unit.mounts.length && mount.reload <= reload / 2f && lastReload > reload / 2f){
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
            mount.side = !mount.side;
        }

        if(!headless && activeSound != Sounds.none && mount.shoot && can && mount.warmup >= minWarmup){
            Vars.control.sound.loop(activeSound, unit, activeSoundVolume);
        }

        float velLen = unit.isRemote() ? unit.vel.len() : unit.deltaLen() / Time.delta;

        //shoot if applicable
        if(mount.shoot && //must be shooting
                can && //must be able to shoot
                !(bullet.killShooter && mount.totalShots > 0) && //if the bullet kills the shooter, you should only ever be able to shoot once
                (!alternate || wasFlipped == flipSprite) &&
                mount.warmup >= minWarmup && //must be warmed up
                velLen >= minShootVelocity && (maxShootVelocity == -1 || velLen <= maxShootVelocity)  && //check velocity requirements
                (mount.reload <= 0.0001f || (alwaysContinuous && mount.bullet == null)) && //reload has to be 0, or it has to be an always-continuous weapon
                (alwaysShooting || Angles.within(rotate ? mount.rotation : unit.rotation + baseRotation, mount.targetRotation, shootCone)) //has to be within the cone
        ){
            shoot(unit, mount, bulletX, bulletY, shootAngle);

            mount.reload = reload;
        }
    }


    @Override
    public void draw(Unit unit, WeaponMount m) {
        var mount = (GatlingWeaponMount)m;
        if(recoils > 0){
            if(mount.recoils == null) mount.recoils = new float[recoils];
        }
        float
                rotation = unit.rotation - 90,
                realRecoil = Mathf.pow(mount.recoil, recoilPow) * recoil,
                weaponRotation  = rotation + (rotate ? mount.rotation : baseRotation),
                wx = unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -realRecoil),
                wy = unit.y + Angles.trnsy(rotation,x, y) + Angles.trnsy(weaponRotation, 0, -realRecoil),
                tx = wx + Angles.trnsx(weaponRotation, bx, by),
                ty = wy + Angles.trnsy(weaponRotation, bx, by);
        float z = Draw.z();
        float width = this.width/Mathf.cosDeg(inclineRotation);
        for (int i = 0; i < recoils; i++) {
            float angle = Mathf.mod(mount.angle + (360f/recoils)*i,360f);
            var v = Tmp.v2.trns(weaponRotation+inclineRotation,width/2)
                    .scl(width/2 * Mathf.cosDeg(angle))
                    .add(tx,ty).add(Tmp.v3.trns(weaponRotation -90,mount.recoils[i] * recoilTravel));
            float sin = Mathf.sinDeg(angle);;
            Draw.z(z + sin*0.04f+0.05f);
            Draw.color(Color.white,Color.black,0.10f-sin * 0.09f);
            Draw.rect(barrel,v.x,v.y,weaponRotation);
            if(mount.overheat>0.01f) {
                Draw.color(overheatColor,mount.overheat);
                Draw.blend(Blending.additive);
                Draw.rect(barrelHeat, v.x, v.y, weaponRotation);
                Draw.blend();
            }
        }
        Draw.z(z+0.1f);
        super.draw(unit, m);



    }

    public static class GatlingWeaponMount extends WeaponMount{
        public float angle;
        public float keepWarmup=0;
        public float overheat = 0f;
        public boolean isOverheat;
        public GatlingWeaponMount(Weapon weapon) {
            super(weapon);
            var w = (GatlingWeapon)weapon;
            angle = w.baseAngle;
        }
    }
}
