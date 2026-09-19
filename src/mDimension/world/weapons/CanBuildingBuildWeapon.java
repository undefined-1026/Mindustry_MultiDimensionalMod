package mDimension.world.weapons;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Queue;
import arc.util.Time;
import arc.util.Tmp;
import mDimension.tool.Debug;
import mDimension.tool.ReflectUtils;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CommandAI;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.BuildPlan;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class CanBuildingBuildWeapon extends Weapon {


    public CanBuildingBuildWeapon(){
        super();
        mountType = BuildWeaponMount::new;
    }

    public CanBuildingBuildWeapon(String name){
        super(name);
        mountType = BuildWeaponMount::new;
    }

    {
        rotate = true;
        noAttack = true;
        predictTarget = false;
        display = false;
        bullet = new BulletType();
        useAttackRange = false;
    }
    public float range = Vars.buildingRange + 16f;
    public float speedMulti = 1f;
    protected float dst = -1;
    @Override
    public void update(Unit unit, WeaponMount mount) {
        var m = (BuildWeaponMount)mount;
        Unit targetUnit = null;
        if(unit.controller() instanceof CommandAI commandAI){
            if((commandAI.command == UnitCommand.assistCommand ||
                    commandAI.command == UnitCommand.rebuildCommand) &&
                            ReflectUtils.getValue(commandAI,"commandController") instanceof BuilderAI builderAI){
                targetUnit = builderAI.following;
            }

        }else if(unit.controller() instanceof BuilderAI builderAI){
            targetUnit = builderAI.following;
        }else{
            targetUnit = unit;
        }

        if(targetUnit != null) {
            if (targetUnit.activelyBuilding()) {
                findTarget(targetUnit,unit, m);


                var pp = targetUnit.plans.first();
                //Debug.point(pp.x * 8, pp.y * 8);

            } else {
                m.target = null;
                if(m.plan!=null)m.plan.initialized = false;
                m.plan = null;
            }

            check(unit, m);

            if (m.plan != null) {
                if (!(world.build(m.plan.x, m.plan.y) instanceof ConstructBlock.ConstructBuild) || m.plan.progress >= 1) {
                    m.target = null;
                    if(m.plan!=null)m.plan.initialized = false;
                    m.plan = null;
                    findTarget(targetUnit,unit, m);
                }
            }

            if (m.target != null && m.plan != null && targetUnit.activelyBuilding()) {
                float bs = 1f / m.target.buildCost * unit.type.buildSpeed * unit.buildSpeedMultiplier * state.rules.buildSpeed(unit.team) * speedMulti * Time.delta;
                if (m.plan.breaking) {
                    m.target.deconstruct(unit, unit.team.core(), bs);
                    m.plan.progress = m.target.progress;
                } else {
                    m.target.construct(unit, unit.team.core(), bs, m.plan.config);
                    m.plan.progress = m.target.progress;
                }
            }
        }

        if (m.plan != null) {
            mount.aimX = m.plan.drawx();
            mount.aimY = m.plan.drawy();
        }else{
            Tmp.v2.trns(unit.rotation(),8*100);
            mount.aimX = Tmp.v2.x + unit.x;
            mount.aimY = Tmp.v2.y + unit.y;
        }


        mount.shoot = false;
        mount.rotate = true;

        super.update(unit, mount);
    }

    BuildPlan findPlan(Unit unit,Unit weaponUnit,BuildWeaponMount tm){
        Queue<BuildPlan> plans = unit.plans();
        return plans.find(p->{
            if(unit.plans.first() == p || !weaponUnit.within(p.x*8,p.y*8,range))return false;
            for(var m:unit.mounts()){
                if(m instanceof BuildWeaponMount bm && bm.plan == p && bm!=tm)return false;
            }
            return !p.initialized;
            //return (p.breaking ?p.progress>0.999f:p.progress < 0.001f);//&&!p.initialized;
        });
    }

    void check(Unit unit,BuildWeaponMount m){
        if(m.plan == null)return;
        if(Mathf.dst(unit.x,unit.y,m.plan.x*8,m.plan.y*8) > (Vars.state.rules.infiniteResources ? Float.MAX_VALUE : range)){
            m.target = null;
            if(m.plan!=null)m.plan.initialized = false;
            m.plan = null;
        };
    }

    void findTarget(Unit unit,Unit weaponUnit,BuildWeaponMount m){
        if(m.plan == null || m.target == null || isRob(unit,m)){
            m.plan = null;
            m.target = null;
            var plan = findPlan(unit,weaponUnit,m);
            if(plan == null)return;
            if (!plan.breaking) {
                if(Build.validPlace(plan.block,unit.team,plan.x,plan.y,plan.rotation)) {
                    var build = world.build(plan.x, plan.y);
                    if (build instanceof ConstructBlock.ConstructBuild cb) {
                        m.target = cb;
                        m.plan = plan;
                        plan.initialized = true;
                        return;
                    }
                    Build.beginPlace(unit, plan.block, unit.team, plan.x, plan.y, plan.rotation, plan.config);
                    build = world.build(plan.x, plan.y);
                    if (build instanceof ConstructBlock.ConstructBuild cb) {
                        m.target = cb;
                        m.plan = plan;
                        plan.initialized = true;
                    }
                }
            }else{
                if(Build.validBreak(unit.team,plan.x,plan.y)){
                    var build = world.build(plan.x, plan.y);
                    if (build instanceof ConstructBlock.ConstructBuild cb) {
                        m.target = cb;
                        m.plan = plan;
                        plan.initialized = true;
                        return;
                    }
                    Build.beginBreak(unit,unit.team,plan.x,plan.y);
                    build = world.build(plan.x, plan.y);
                    if (build instanceof ConstructBlock.ConstructBuild cb) {
                        m.target = cb;
                        m.plan = plan;
                        plan.initialized = true;
                    }
                }

            }
        }
    }

    boolean isRob(Unit unit,BuildWeaponMount m){
        if(unit.plans.first() == m.plan)return true;
        for(var mount:unit.mounts){
            if(mount instanceof BuildWeaponMount bm){
                if(bm.target == m.target && bm!=m)return true;
            }
        }
        return false;
    }

    public static class BuildWeaponMount extends WeaponMount{
        public ConstructBlock.ConstructBuild target;
        public BuildPlan plan;
        public BuildWeaponMount(Weapon w){
            super(w);
        }
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);
        var m = (BuildWeaponMount)mount;
        if(m.plan!=null && m.target!=null){
            float
                    z = Draw.z(),
                    rotation = unit.rotation - 90,
                    weaponRotation  = rotation + (rotate ? mount.rotation : 0),
                    wx = unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -mount.recoil),
                    wy = unit.y + Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0, -mount.recoil),
                    px = wx + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                    py = wy + Angles.trnsy(weaponRotation, this.shootX, this.shootY);

            drawBuildingBeam(px, py,m,unit);
            Draw.z(z);
        }
    }
//Debug
    public void drawBuildingBeam(float px, float py,BuildWeaponMount mount,Unit unit) {

        Draw.z(114.0F);
        BuildPlan plan = mount.plan;
        if(plan == null)return;
        Tile tile = Vars.world.tile(plan.x, plan.y);
        if (tile != null && Mathf.dst(plan.drawx(),plan.drawy(),px,py)<=(Vars.state.rules.infiniteResources ? Float.MAX_VALUE : range)) {
            int size = plan.breaking ? tile.block().size : plan.block.size;
            float tx = plan.drawx();
            float ty = plan.drawy();
            Lines.stroke(1.0F, plan.breaking ? Pal.remove : Pal.accent);
            Draw.z(122.0F);
            Draw.alpha(unit.buildAlpha());
//            if (!active && !(tile.build instanceof ConstructBlock.ConstructBuild)) {
//                Fill.square(plan.drawx(), plan.drawy(), (float)(size * 8) / 2.0F);
//            }

            Drawf.buildBeam(px, py, tx, ty, (float)(8 * size) / 2.0F);
            Fill.square(px, py, 1.5F + Mathf.absin(Time.time, 2.2F, 0.7f), mount.rotation + 45.0F);
            Draw.reset();
            Draw.z(114.0F);
        }

    }
}
