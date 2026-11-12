package mDimension.world.blocks;

import arc.Core;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.util.Nullable;
import arc.util.Strings;
import mDimension.world.type.Beam;
import mindustry.entities.bullet.BulletType;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.Turret;

public class MD_BeamTurret extends Turret {
    /**用 消耗子弹/装填倍数决定需要的激光功率*/
    public ObjectMap<Beam, BulletType> ammoTypes = new OrderedMap<>();
    public float minPowerOfBeam = 1f;
    public MD_BeamTurret(String name) {
        super(name);
    }

    public class BeamAmmoEntry extends AmmoEntry{
        public Beam beam;

        public BeamAmmoEntry(Beam beam) {
            this.beam = beam;
        }

        public BulletType type(){
            return ammoTypes.get(beam);
        };
    }

    @Override
    public void setBars() {
        super.setBars();
        for(var en:ammoTypes){
            var beam = en.key;

            addBar("beam-"+en.key.name,(MD_BeamTurretBuild e)-> {
                        float power = e.ammoTypePower.get(beam, 0f);
                        float requiredPower = getRequiredPower(ammoTypes.get(beam));
                        return new Bar(
                                () -> beam.localizedName + " " + Core.bundle.format("bar.laserpower", Strings.fixed(power, 1), Strings.fixed(requiredPower, 1)),
                                () -> beam.color,
                                () -> power / requiredPower
                        );
                    }
            );
        }
    }

    /** Initializes accepted ammo map. Format: [beam1, bullet1, beam2, bullet2...] */
    public void ammo(Object... objects){
        ammoTypes = OrderedMap.of(objects);
    }
    public class MD_BeamTurretBuild extends TurretBuild implements BeamConsumer{
        public ObjectFloatMap<Beam> ammoTypePowerTmp = new ObjectFloatMap<>();
        public ObjectFloatMap<Beam> ammoTypePower = new ObjectFloatMap<>();
        public float beamPower = 0;
        public BulletType ammoType;
        @Override
        public void handleBeam(Beam beam, float power) {
            ammoTypePowerTmp.put(beam,ammoTypePowerTmp.get(beam,0f) + power);
        }

        @Override
        public boolean acceptBeam(Beam beam, float power) {
            return ammoTypes.containsKey(beam) && power>=minPowerOfBeam;
        }

        @Override
        public void update(){
            ammoType = null;
            beamPower = 0;
            for(var e:ammoTypes.entries()){
                float tmpp = ammoTypePowerTmp.get(e.key,0);
                ammoTypePower.put(e.key,tmpp);
                if(tmpp>beamPower && tmpp>0.001f){
                    beamPower = tmpp;
                    ammoType = ammoTypes.get(e.key);
                }
                ammoTypePowerTmp.put(e.key,0);
            }

            super.update();
        }

        /** Consume ammo and return a type. */
        public BulletType useAmmo(){
            return ammoType;
        }

        /** @return the ammo type that will be returned if useAmmo is called. */
        public @Nullable BulletType peekAmmo(){
            return ammoType;
        }

        /** @return whether the turret has ammo. */
        public boolean hasAmmo(){
            //skip first entry if it has less than the required amount of ammo
            return ammoType!=null;
        }

        @Override
        public float efficiencyScale() {
            return Math.min(1f,beamPower/getRequiredPower(ammoType));
        }
    }
    float getRequiredPower(BulletType b){
        return (float)this.ammoPerShot/b.ammoMultiplier;
    }
}
