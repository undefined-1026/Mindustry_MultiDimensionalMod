package mDimension.world.weapons;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mDimension.meta.MD_Stat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class OverdriveWeapon extends Weapon {
    // 可配置参数
    public float speedBoost = 2f;          // 建筑加速倍率
    public float extraDuration = 120f;     // 加速额外持续时间
    public float beamSmoothness = 0.1f;    // 光束渐变平滑度
    public float lockedTime = 60;
    public float lockedRange = 25*8f;
    public Effect lockedEffect = Fx.healBlockFull;
    public Color color = Color.valueOf("FEA274");

    public OverdriveWeapon(String name){
        super(name);
    }

    public OverdriveWeapon(){
    }


    @Override
    public void addStats(UnitType u, Table w){
        w.row();
        w.add("[lightgray]" + MD_Stat.overdrive.localized() + ": " + (mirror ? "2x " : "") + "[white]" + (int)(speedBoost*100) + " " + StatUnit.percent.localized());
    }

    @Override
    public float dps(){
        return 0f;
    }

    {
        predictTarget = false;
        autoTarget = true;
        controllable = true;
        rotate = true;
        mountType = OverdriveWeaponMount::new;
        recoil = 0f;
        noAttack = true;
        useAttackRange = false;
        activeSound = Sounds.beamHeal;
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        return Units.findAllyTile(unit.team, x, y, range, b -> {
                    return b.block.canOverdrive;
                });
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range){
        return !(target.within(unit, range + unit.hitSize/2f) && target.team() == unit.team && target instanceof Building u && u.block.canOverdrive&& u.isValid());
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        //does nothing, shooting is handled in update()
    }

    @Override
    public void init(){
        super.init();
        bullet.rangeOverride = speedBoost;
    }

    @Override
    public void update(Unit unit, WeaponMount m) {
        super.update(unit, m);

        if (!(m instanceof OverdriveWeaponMount mount)) {
            return;
        }



        float weaponRotation = unit.rotation() - 90;
        float wx = unit.x + Angles.trnsx(weaponRotation, this.x, this.y);
        float wy = unit.y + Angles.trnsy(weaponRotation, this.x, this.y);

        boolean targetInvalid = mount.lockedTarget == null
                || mount.lockedTarget.dead()
                || !mount.lockedTarget.isValid()
                || Mathf.dst(mount.lockedTarget.x, mount.lockedTarget.y, wx, wy) > lockedRange;
        boolean canShoot = unit.isShooting() & unit.canShoot();
        boolean locking = false;

        if(canShoot){
            mount.lastEnd.set(mount.aimX, mount.aimY);
            mount.pointPos.set(mount.aimX, mount.aimY);
            if(!rotate && !Angles.within(Angles.angle(wx, wy, mount.aimX, mount.aimY), unit.rotation, shootCone)){
                canShoot = false;
            }
        }
        if (targetInvalid) {
            mount.lockedTarget = null;
        }
        if(canShoot){
            Building newTarget = null;

            var build = Vars.world.build(World.toTile(mount.lastEnd.x), World.toTile(mount.lastEnd.y));

            if(
                    build == null
                    ||Mathf.dst(build.x, build.y, wx, wy) > range()
                    ||!build.block.canOverdrive
            )build = null;

            if(
                    !(build == null && mount.lockedTarget == null)&& build != mount.lockedTarget
            ) {
                if (build != null && build.team == unit.team) {
                    newTarget = build;
                }
                boolean newTargetInvalid = newTarget == null
                        || newTarget.dead()
                        || !newTarget.isValid()
                        || Mathf.dst(newTarget.x, newTarget.y, wx, wy) > range();
                if (mount.lockedProcess > lockedTime) {

                    if (!newTargetInvalid) {
                        mount.lockedTarget = newTarget;
                        lockedEffect.at(newTarget.x, newTarget.y, 0, color, newTarget.block);
                    } else {
                        mount.lockedTarget = null;
                        mount.realSize =4f;
                    }
                    mount.lockedProcess = 0;
                } else {
                    if (!newTargetInvalid) {
                        if (mount.lockedTarget == null) {
                            mount.lastEnd.set(newTarget.x, newTarget.y);
                            mount.beamSize = newTarget.block.size * tilesize / 2f;
                        }
                        mount.pointPos.set(newTarget.x, newTarget.y);
                        mount.pointSize = newTarget.block.size * tilesize / 2f;
                        locking = true;
                    }else{
                        mount.pointSize = 4;
                    }
                    mount.lockedProcess += Time.delta;
                }
            }else{
                mount.lockedProcess = Mathf.lerp(mount.lockedProcess,0,Time.delta*beamSmoothness);
            }
        }else {
            mount.lockedProcess = Mathf.lerp(mount.lockedProcess,0,Time.delta*beamSmoothness);
        }


        if (mount.lockedTarget != null) {
            Building build = mount.lockedTarget;
            mount.beamSize = build.block.size*tilesize/2f;

            mount.lastEnd.set(build.x, build.y);

            if (build.block.canOverdrive
                    && (mount.process += Time.delta) >= reload) {

                build.applyBoost(speedBoost, reload + extraDuration);
                mount.process = 0;
            }

        }else if(!locking){
            mount.beamSize = 4f;
            mount.lastEnd
                    .sub(wx, wy)
                    .limit(range())
                    .add(wx, wy);
        }
        mount.pointPos
                .sub(wx, wy)
                .limit(range())
                .add(wx, wy);
        mount.lerp = Mathf.approachDelta(mount.lerp,mount.lockedTarget !=null?0:1,0.03f);
        if(mount.lerp <0.99f) {
            float pro =Time.delta * beamSmoothness+mount.lerp*(1-Time.delta * beamSmoothness);
            mount.realLastEnd.set(
                    Mathf.lerp(mount.realLastEnd.x, mount.lastEnd.x, pro),
                    Mathf.lerp(mount.realLastEnd.y, mount.lastEnd.y, pro)
            );
        }else {
            mount.realLastEnd.set(mount.lastEnd);
        }
//        if(canShoot) {
//            mount.rotation = Angles.angle(wx, wy, mount.realLastEnd.x, mount.realLastEnd.y) - unit.rotation;
//        }
        mount.beamWarmup = Mathf.lerp(mount.beamWarmup, mount.lockedTarget!=null || canShoot ? 1f : 0f, Time.delta * beamSmoothness);
    }

    @Override
    public void draw(Unit unit, WeaponMount m) {
        super.draw(unit, m);

        if (m instanceof OverdriveWeaponMount mount){
            mount.realSize = Mathf.lerp(mount.realSize,mount.beamSize,Time.delta * beamSmoothness);
            float z = Draw.z();
            Draw.z(Layer.buildBeam);

            float weaponRotation = unit.rotation() - 90;
            Vec2 shot = new Vec2(shootX,shootY).setAngle(mount.rotation+90);
            float wx = unit.x + Angles.trnsx(weaponRotation, this.x +shot.x, this.y + shot.y);
            float wy = unit.y + Angles.trnsy(weaponRotation, this.x +shot.x, this.y + shot.y);

            Draw.color(color);
            Draw.alpha(mount.beamWarmup);
            Drawf.buildBeam(
                    wx, wy,
                    mount.realLastEnd.x, mount.realLastEnd.y,
                    mount.realSize
            );
            Fill.square(mount.realLastEnd.x, mount.realLastEnd.y,mount.realSize+0.001f);
            Fill.poly(wx,wy,3,3.2f*(1+Mathf.sin(Time.time,10,0.15f)),Time.time%360);
            if(mount.lockedProcess >0.01f){
                float scl = mount.lockedProcess/lockedTime;
                Draw.z(mount.lockedTarget != null?122:118);
                Draw.alpha(mount.beamWarmup*over(scl)*(mount.lockedTarget != null?1f:0.3f));
                Drawf.buildBeam(
                        wx, wy,
                        mount.pointPos.x, mount.pointPos.y,
                        mount.pointSize*scl
                );
                Fill.square(mount.pointPos.x, mount.pointPos.y,mount.pointSize*scl);
            }

            Draw.reset();
            Draw.z(z);
        }
    }

    public float over(float x){
        return (float) (x*(Math.sqrt(1-x))/0.3849f);
    }


    public static class OverdriveWeaponMount extends WeaponMount {
        public Building lockedTarget = null;
        public float lockedProcess = 0;
        public float beamSize = 0;
        public float realSize = 0;
        public float lerp = 0f;
        public float process = 0;
        public Vec2 lastEnd = new Vec2(0, 0);
        public Vec2 pointPos = new Vec2(0, 0);
        public Vec2 realLastEnd = new Vec2(0, 0);
        public float pointSize = 0;
        public float beamWarmup = 0;

        public OverdriveWeaponMount(Weapon w) {
            super(w);
        }
    }
}