package mDimension.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.content.MD_Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.MultiReqImage;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class MD_PayloadTurret extends Turret {
    public ObjectMap<UnlockableContent, BulletType> ammoTypes = new OrderedMap<>();
    public int maxAmmo = 5;

    public MD_PayloadTurret(String name) {
        super(name);
        acceptsPayload = true;
    }

    public void ammo(Object... objects) {
        ammoTypes = OrderedMap.of(objects);
    }

    /**
     * Limits bullet range to this turret's range value.
     */
    public void limitRange() {
        limitRange(9f);
    }

    /**
     * Limits bullet range to this turret's range value.
     */
    public void limitRange(float margin) {
        for (var entry : ammoTypes.entries()) {
            limitRange(entry.value, margin);
        }
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.add(Stat.ammo, StatValues.ammo(ammoTypes, true));
        stats.add(Stat.ammoCapacity, maxAmmo / ammoPerShot, StatUnit.shots);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        int count=0;
        for(var e:ammoTypes){
            float range = this.range;
            if(e.value.rangeChange!=0){
                range+=e.value.rangeChange;
                Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
            }
            for (int j = 0; j < 4; j++) {
                float ang = (-Time.time*0.3f + count * 90f / ammoTypes.size + j*90f + 360f)%360f;
                var v = Tmp.v2.trns(ang,range).add(x * tilesize + offset, y * tilesize + offset);
                Draw.rect(e.key.fullIcon,v.x,v.y,16,16,ang-90f);
            }
            count++;
        }
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("ammo", (TPayloadTurretBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> (float) entity.totalAmmo / maxAmmo
                )
        );
    }

    @Override
    public void init() {
        consume(new Consume() {
            @Override
            public void build(Building build, Table table) {
                MultiReqImage image = new MultiReqImage();
                // 这里简化处理，实际可能需要更复杂的逻辑来显示payload图标
                table.add(image).size(8 * 4);
            }

            @Override
            public float efficiency(Building build) {
                //valid when it can shoot
                return build instanceof TPayloadTurretBuild it && it.ammo.size > 0 && (it.ammo.peek().amount >= ammoPerShot || it.cheating()) ? 1f : 0f;
            }

            @Override
            public void display(mindustry.world.meta.Stats stats) {
                //don't display
            }
        });

        if (targetGround) {
            ammoTypes.each((payload, type) -> placeOverlapRange = Math.max(placeOverlapRange, range + type.rangeChange + placeOverlapMargin));
        }

        super.init();
    }

    public class TPayloadTurretBuild extends TurretBuild {

        @Override
        public void onProximityAdded() {
            super.onProximityAdded();

            //add first ammo item to cheaty blocks so they can shoot properly
            if (!hasAmmo() && cheating() && ammoTypes.size > 0) {
                // 这里简化处理，实际可能需要更复杂的逻辑来获取第一个payload
            }
        }

        @Override
        public void updateTile() {
            unit.ammo((float) unit.type().ammoCapacity * totalAmmo / maxAmmo);

            super.updateTile();
        }

        @Override
        public void handlePayload(Building source, Payload payload) {

            if (payload instanceof BuildPayload bp) {
                BulletType type = ammoTypes.get(bp.block());
                Object[] eff = new Object[]{
                        bp.block(),
                        new Vec2(bp.x() - x, bp.y() - y)
                };
                MD_Fx.payloadInput.at(x, y, 0, Color.white, eff);
                if (type == null) return;
                totalAmmo += (int) type.ammoMultiplier;

                //find ammo entry by type
                for (int i = 0; i < ammo.size; i++) {
                    PayloadEntry entry = (PayloadEntry) ammo.get(i);
                    //if found, put it to the right
                    if (entry.content == bp.block()) {
                        entry.amount += (int) type.ammoMultiplier;
                        ammo.swap(i, ammo.size - 1);
                        return;
                    }
                }

                ammo.add(new PayloadEntry(bp.block(), (int) type.ammoMultiplier));
            } else if (payload instanceof UnitPayload up) {
                BulletType type = ammoTypes.get(up.unit.type);
                Object[] eff = new Object[]{
                        up.unit.type,
                        new Vec2(up.x() - x, up.y() - y)
                };
                MD_Fx.payloadInput.at(x, y, 0, Color.white, eff);
                if (type == null) return;
                totalAmmo += (int) type.ammoMultiplier;

                //find ammo entry by type
                for (int i = 0; i < ammo.size; i++) {
                    PayloadEntry entry = (PayloadEntry) ammo.get(i);
                    //if found, put it to the right
                    if (entry.content == up.unit.type) {
                        entry.amount += (int) type.ammoMultiplier;
                        ammo.swap(i, ammo.size - 1);
                        return;
                    }
                }

                ammo.add(new PayloadEntry(up.unit.type, (int) type.ammoMultiplier));
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            if (payload instanceof BuildPayload bp) {
                BulletType type = ammoTypes.get(bp.block());
                return type != null && totalAmmo + type.ammoMultiplier <= maxAmmo;
            } else if (payload instanceof UnitPayload up) {
                BulletType type = ammoTypes.get(up.unit.type);
                return type != null && totalAmmo + type.ammoMultiplier <= maxAmmo;
            }
            return false;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
        }

        @Override
        public void read(Reads read) {
            super.read(read);
        }
    }

    public class PayloadEntry extends AmmoEntry {
        public UnlockableContent content;

        PayloadEntry(UnlockableContent content, int amount) {
            this.content = content;
            this.amount = amount;
        }

        @Override
        public BulletType type() {
            return ammoTypes.get(content);
        }

        public String name() {
            return content.name;
        }

        @Override
        public String toString() {
            return "PayloadEntry{" +
                    "payload=" + name() +
                    ", amount=" + amount +
                    '}';
        }
    }
}
