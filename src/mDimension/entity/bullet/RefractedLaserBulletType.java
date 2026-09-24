package mDimension.entity.bullet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Tmp;
import mDimension.content.MD_Fx;
import mDimension.entity.TmpPos;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Posc;

public class RefractedLaserBulletType extends BulletType {
    public RefractedLaserBulletType() {
        super();
        instantDisappear = true;
        collides = false;
        keepVelocity = false;
        pierceCap = 3;
        pierce = true;
        hittable = false;
        absorbable = false;
        fragOnDespawn = false;
        despawnHit = false;
        setDefaults = false;
        speed = 0;
        shootEffect = smokeEffect = Fx.none;
    }

    public float refractedRadius = 8 * 8f, length = 80f;
    public boolean emit = false;
    public Effect LaserEffect = MD_Fx.RefractedLaser;
    public float laserStrokeMulti = 1f;
    public Color laserColor = Color.valueOf("FFE791");

    @Override
    protected float calculateRange() {
        return length;
    }
    Healthc target = null;
    float dst = -1;
    Posc node = null;
    @Override
    public void init(Bullet b) {
        super.init(b);

        // ===== 全部局部化，避免 BulletType 单例共享 =====
        Seq<Posc> totalTarget = new Seq<>(Posc.class);

        node = b;
        float sx = b.x,sy = b.y;
        if (emit) {
            float len = Mathf.len(b.aimX - b.x, b.aimY - b.y);
            if (len > length) {
                Tmp.v1.set(b.aimX, b.aimY).sub(b.x, b.y).scl(length / len).add(b.x, b.y);
                b.aimX = Tmp.v1.x;
                b.aimY = Tmp.v1.y;
            }
            b.set(b.aimX, b.aimY);
            totalTarget.add(new TmpPos(b));  // 拷贝坐标，防止 Bullet 池化后 x/y 归零
        }
        Vec2 nodePos = new Vec2(b.x, b.y);
        for (int i = 0; i < pierceCap; i++) {
            target = null;
            dst = -1;

            Units.nearbyEnemies(b.team, nodePos.x, nodePos.y, refractedRadius, u -> {
                float udst = u.dst2(nodePos) * (b.collided.contains(u.id) ? 2 : 1);
                if (!u.dead && (udst < dst || dst < 0) && u != node) {
                    dst = udst;
                    target = u;
                }
            });

            if (target == null) {
                dst = -1;
                Units.nearbyBuildings(nodePos.x, nodePos.y, refractedRadius, u -> {
                    float udst = u.dst2(nodePos) * (b.collided.contains(u.id) ? 2 : 1);
                    if (b.team != u.team && !u.dead && (udst < dst || dst < 0) && u != node) {
                        dst = udst;
                        target = u;
                    }
                });
                if (target == null) {
                    if (emit) break;
                    return;
                }
            }

            totalTarget.add(target);
            node = target;
            nodePos.set(target.x(), target.y());
            Damage.collidePoint(b, b.team, hitEffect, target.x(), target.y());
        }

        LaserEffect.at(sx,sy, laserStrokeMulti, laserColor, totalTarget.toArray());
    }
}