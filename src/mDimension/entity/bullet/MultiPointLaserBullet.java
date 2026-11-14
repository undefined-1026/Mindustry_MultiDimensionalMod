package mDimension.entity.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Trail;

import java.util.Arrays;

import static mDimension.content.building.MD_blocks.modname;
import static mindustry.Vars.headless;

/**
 * 最终合并版多点激光子弹类
 * 保留所有自定义修改 + 修复核心问题：
 * 1. 初始单目标时激光轮询分配，新增目标自动切换；
 * 2. 每束激光优先攻击不同目标，锁定至失效；
 * 3. 保留动态检测半径、自定义激光起点、巡逻轨迹、自定义绘制等所有修改。
 */
public class MultiPointLaserBullet extends BulletType {

    public String sprite = "point-laser";
    public String spriteTop = "";
    public TextureRegion laser, laserEnd, laserTop, point;

    public Vec2[] beginVec2;
    public float[] beginPos;

    public void idleAct(Vec2 v, int i, Bullet b) {
        v.set(
                Mathf.approachDelta(
                        v.x,
                        Mathf.cos(Time.time / 60 + Mathf.PI * 2 * i / amount, 1, traceRad(b) * 0.5f),
                        2f
                ),
                Mathf.approachDelta(
                        v.y,
                        Mathf.sin(Time.time / 60 + Mathf.PI * 2 * i / amount, 1, traceRad(b) * 0.5f),
                        2f
                )
        );
    }

    // 激光颜色（保留原配置）
    public Color color = Color.white;

    // 光束特效相关（保留原配置）
    public Effect beamEffect = Fx.colorTrail;
    public float beamEffectInterval = 3f, beamEffectSize = 3.5f;

    // 激光震荡相关（保留原配置）
    public float oscScl = 2f, oscMag = 0.3f;

    // 伤害间隔（保留原配置）
    public float damageInterval = 5f;

    // 屏幕震动（保留原配置）
    public float shake = 0f;

    // 激光数量（保留原配置，默认3束）
    public int amount = 3;

    // 目标检测半径（你的自定义动态半径）
    public float baseTraceRad = 40f;
    public float traceRadMulti = 0;
    public float fractMulti = 1f;

    // 新增：角度范围转半径系数（你的自定义方法）
    public void angleRange(float range) {
        this.traceRadMulti = (float) Math.tan((range / 360) * Math.PI);
    }

    // 构造方法：初始化子弹基础属性（保留你的配置）
    public MultiPointLaserBullet() {
        removeAfterPierce = false;
        speed = 0f;
        despawnEffect = Fx.none;
        lifetime = 20f;
        impact = true;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        optimalLifeFract = 0.5f;
        shootEffect = smokeEffect = Fx.none;
        drawSize = 1000f;
    }

    // 初始化子弹：绑定模块（保留修复逻辑）
    @Override
    public void init(Bullet b) {
        super.init(b);
        if (!(b.data instanceof MultiPointLaserModule)) {
            b.data = new MultiPointLaserModule(this);
        }
    }

    // 加载纹理：保留你的自定义纹理和beginVec2初始化
    @Override
    public void load() {
        super.load();

        laser = Core.atlas.find(sprite);
        laserEnd = Core.atlas.find(sprite + "-end");
        point = Core.atlas.find(modname + "shoot-point");

        beginVec2 = new Vec2[amount];
        for (int i = 0; i < amount; i++) {
            beginVec2[i] = new Vec2(0, 0);
        }
        if (beginPos != null) {
            for (int i = 0; i < beginVec2.length; i++) {
                float vx = 0;
                float vy = 0;
                if (i * 2 + 1 < beginPos.length) {
                    vx = beginPos[i * 2];
                    vy = beginPos[i * 2 + 1];
                }
                beginVec2[i].set(vx, vy);
            }
        }
    }

    @Override
    public float continuousDamage() {
        return damage / damageInterval * 60f;
    }

    @Override
    public float estimateDPS() {
        return damage * 100f / damageInterval * 3f;
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        if (b.data instanceof MultiPointLaserModule module) {
            for (int i = 0; i < amount; i++) {
                Vec2 aim = module.aims[i];
                float drawX = aim.x + b.aimX;
                float drawY = aim.y + b.aimY;
                Draw.color(color);
                Vec2 begin = beginVec2[i].cpy().rotate(b.rotation() - 90);
                Drawf.laser(laser, laserEnd, b.x + begin.x, b.y + begin.y, drawX, drawY, getLaserWidth(b));
                Draw.reset();
            }
            Draw.color(b.team.color);
            Draw.alpha(b.fslope() * 0.7f);
            Draw.z(122);
            float scl = traceRad(b) / point.width * 2;
            Draw.rect(point, b.aimX, b.aimY, scl * point.width, scl * point.height, b.rotation() - 90);
            Draw.reset();
        }
    }

    // 更新子弹逻辑：核心修复+保留你的所有修改

    @Override
    public void update(Bullet b) {
        if (b.data instanceof MultiPointLaserModule module) {
            boolean isDamageTime = b.timer.get(0, damageInterval);
            boolean isBeamEffectTime = b.timer.get(1, beamEffectInterval);

            updateTargetList(b, module);

            assignDifferentTargets(module,b);


            for (int i = 0; i < amount; i++) {
                module.index = Mathf.clamp(i, 0, amount - 1);
                updateAim(b, module, module.index);
                updateTrail(b, module);
                updateTrailEffects(b, module);
                updateBulletInterval(b, module);

                if (isDamageTime) {
                    Vec2 aim = module.aims[i];
                    Damage.collidePoint(b, b.team, hitEffect, aim.x + b.aimX, aim.y + b.aimY);
                }
                if (isBeamEffectTime) {
                    Vec2 aim = module.aims[i];
                    beamEffect.at(aim.x + b.aimX, aim.y + b.aimY, beamEffectSize * b.fslope(), hitColor);
                }
                if (shake > 0) {
                    Effect.shake(shake, shake, b);
                }
            }
        }
    }

    @Override
    public float damageMultiplier(Bullet b) {
        return super.damageMultiplier(b) / amount;
    }


    public float traceRad(Bullet b) {
        return Mathf.len(b.aimX - b.x, b.aimY - b.y) * traceRadMulti + baseTraceRad;
    }

    public float traceRad2(Bullet b) {
        float len = Mathf.len(b.aimX - b.x, b.aimY - b.y) * traceRadMulti + baseTraceRad;
        return len*len;
    }

    private boolean updateTargetList(Bullet b, MultiPointLaserModule module) {
        Seq<Unit> oldTargets = new Seq<>(module.targets);
        module.targets.clear();

        Units.nearbyEnemies(b.team, b.aimX, b.aimY, traceRad(b), enemy -> {
            if (!enemy.dead() && enemy.isValid() && enemy.targetable(b.team)) {
                module.targets.add(enemy);
            }
        });

        module.targets.sort((u1, u2) -> Float.compare(
                u1.dst2(b.aimX, b.aimY) - u1.maxHealth,
                u2.dst2(b.aimX, b.aimY) - u2.maxHealth
        ));

        if (oldTargets.size != module.targets.size) return true;
        for (int i = 0; i < oldTargets.size; i++) {
            if (oldTargets.get(i) != module.targets.get(i)) return true;
        }
        return false;
    }

    public boolean has(Object[] list,Object object){
        for(Object k:list){
            if(k == object)return true;
        }
        return false;
    }
    public boolean AllNull(Object[] list){
        for(Object k:list){
            if(k != null)return false;
        }
        return true;
    }


    private void assignDifferentTargets(MultiPointLaserModule module,Bullet b) {
        Seq<Unit> targets = module.targets;
        Unit[] locks = module.lockedTargets;
        //Seq<Unit> validLocked = new Seq<>();
        if (targets.isEmpty()) {
            Arrays.fill(module.lockedTargets, null);
            return;
        }
        int count = 0;
        for(int i=0;i<locks.length;i++){
            Unit u =locks[i];
            boolean targetValid = u != null
                    && !u.dead()
                    && u.dst2(b.aimX, b.aimY) <= traceRad2(b)
                    && u.isValid()
                    && u.targetable(b.team)
                    && u.team != b.team;
            if(!targetValid){
                locks[i] = null;
            }else{
                count++;
            }
        }

        if(count < amount && count < targets.size){
            for(int i=0;i<targets.size;i++){
                Unit u = targets.get(i);
                if(u == null)continue;
                if(u.isValid() && !u.dead()){
                    for(int j = 0;j<locks.length;j++){
                        if(locks[j] == null && !has(locks,u)){
                            locks[j] = u;
                            break;
                        }

                    }
                }
            }
        }








//        for (Unit locked : module.lockedTargets) {
//            if (locked != null && !locked.dead() && locked.isValid() && targets.contains(locked)) {
//                validLocked.add(locked);
//            }
//        }
//
//        int targetIdx = 0;
//        for (int i = 0; i < module.lockedTargets.length; i++) {
//            Unit current = module.lockedTargets[i];
//            if (current != null && !current.dead() && current.isValid() && targets.contains(current)) {
//                continue;
//            }
//
//            Unit newTarget = null;
//            int loopCount = 0;
//            // 增加循环次数限制，避免无限循环
//            if(targets.size>module.lockedTargets.length) {
//                while (newTarget == null && loopCount < targets.size) {
//                    // 确保 targetIdx 始终为非负数
//                    int safeIdx = Mathf.mod(targetIdx, targets.size);
//                    Unit candidate = targets.get(safeIdx);
//                    if (!validLocked.contains(candidate)) {
//                        newTarget = candidate;
//                        validLocked.add(candidate);
//                    }
//                    targetIdx++;
//                    loopCount++;
//                }
//                // 兜底：如果所有目标都被锁定，按索引轮询
//                if (newTarget == null) {
//                    newTarget = targets.get(Mathf.mod(i, targets.size));
//                }
//            }else{
//                newTarget = targets.get(Mathf.mod(i, targets.size));
//            }
//            module.lockedTargets[i] = newTarget;
//        }
    }

    private void updateAim(Bullet b, MultiPointLaserModule module, int index) {
        // 核心防御：index 必须在 [0, amount-1] 范围内
        if (index < 0 || index >= module.aims.length) {
            return;
        }

        Vec2 aim = module.aims[index];
        Unit locked = module.lockedTargets[index];
        int fi = 0;
        if(locked == null){
            for(int i =1;i<module.lockedTargets.length;i++){
                if(module.lockedTargets[(i+index)%amount] != null){
                    fi = i;
                    locked = module.lockedTargets[(i+index)%amount];
                    break;
                }
            }
        }
        boolean targetValid = locked != null
                && !locked.dead()
                && locked.isValid()
                && locked.targetable(b.team)
                && locked.dst2(b.aimX, b.aimY) <= traceRad2(b);

        if (locked!=null) {
            aim.set(
                    Mathf.approachDelta(aim.x, locked.x - b.aimX, 2f),
                    Mathf.approachDelta(aim.y, locked.y - b.aimY, 2f)
            );
            return;
        }

        module.lockedTargets[index + fi] = null;
        idleAct(aim, index, b);
    }

    private void updateTrail(Bullet b, MultiPointLaserModule module) {
        if (headless || trailLength <= 0) return;
        if (module.index < 0 || module.index >= module.trails.length || module.trails == null) {
            return;
        }
        Trail trail = module.trails[module.index];
        Vec2 aim = module.aims[module.index];
        trail.length = trailLength;
        trail.update(aim.x + b.aimX, aim.y + b.aimY, getLaserWidth(b));
    }

    private void updateTrailEffects(Bullet b, MultiPointLaserModule module) {
        Vec2 aim = module.aims[module.index];
        float aimX = aim.x + b.aimX, aimY = aim.y + b.aimY;

        if (trailChance > 0 && Mathf.chanceDelta(trailChance)) {
            float rotation = trailRotation ? b.angleTo(aimX, aimY) : (trailParam * b.fslope());
            trailEffect.at(aimX, aimY, rotation, trailColor);
        }
        if (trailInterval > 0f && b.timer.get(3, trailInterval)) {
            float rotation = trailRotation ? b.angleTo(aimX, aimY) : (trailParam * b.fslope());
            trailEffect.at(aimX, aimY, rotation, trailColor);
        }
    }

    private void updateBulletInterval(Bullet b, MultiPointLaserModule module) {
        if (intervalBullet == null || b.time < intervalDelay) return;
        if (b.timer.get(2, bulletInterval)) {
            Vec2 aim = module.aims[module.index];
            float aimX = aim.x + b.aimX, aimY = aim.y + b.aimY;
            float ang = b.rotation();

            for (int i = 0; i < intervalBullets; i++) {
                float finalAng = ang + Mathf.range(intervalRandomSpread) + intervalAngle +
                        ((i - (intervalBullets - 1f) / 2f) * intervalSpread);
                intervalBullet.create(b, aimX, aimY, finalAng);
            }
        }
    }

    private float getLaserWidth(Bullet b) {
        float lifeFract = b.fslope();
        return lifeFract * (1f - oscMag + Mathf.absin(Time.time, oscScl, oscMag)) * fractMulti;
    }

    @Override
    public void drawTrail(Bullet b) {
        if (trailLength > 0 && b.data instanceof MultiPointLaserModule module) {
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            for (int i = 0; i < amount; i++) {
                Trail trail = module.trails[i];
                trail.draw(trailColor, trailWidth);
            }
            Draw.z(z);
        }
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);
        if (b.data instanceof MultiPointLaserModule module) {
            if (module.trails != null) Arrays.fill(module.trails, null);
            if (module.targets != null) {
                module.targets.clear();
                module.targets = null;
            }
            if (module.lockedTargets != null) Arrays.fill(module.lockedTargets, null);
            module.aims = null;
        }
    }

    @Override
    public void updateTrail(Bullet b) {}
    @Override
    public void updateTrailEffects(Bullet b) {}
    @Override
    public void updateBulletInterval(Bullet b) {}


    public static class MultiPointLaserModule {
        public MultiPointLaserBullet type;
        public Vec2[] aims;
        public Trail[] trails;
        public Seq<Unit> targets;
        public int index;
        public Unit[] lockedTargets;

        public MultiPointLaserModule(MultiPointLaserBullet type) {
            this.type = type;
            this.aims = new Vec2[type.amount];
            this.trails = new Trail[type.amount];
            this.lockedTargets = new Unit[type.amount];
            this.targets = new Seq<>();
            for (int i = 0; i < type.amount; i++) {
                this.aims[i] = new Vec2();
                this.trails[i] = new Trail(type.trailLength);
            }
        }
    }
}