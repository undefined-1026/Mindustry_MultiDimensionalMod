package mDimension.world.blocks;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mDimension.meta.MD_Stat;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class MD_ConstraintItemTurret extends ItemTurret {
    /**if -1 close*/
    public float
            ammoAmountReloadSpeedBoost,overheatSpeed,loadingAmmoTime,shutDownLoadingSpeedBoost=-1;
    public float maxAmmoAmountReloadSpeed = 1f;
    public float coolingTime=5*60f;
    public Effect overheat = Fx.fuelburn;

    public MD_ConstraintItemTurret(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        if (ammoAmountReloadSpeedBoost>0) {
            stats.remove(Stat.reload);
            float s = 60f / (reload + (!reloadWhileCharging ? shoot.firstShotDelay : 0f)) * shoot.shots;
            stats.add(Stat.reload,table -> {
                var unit = StatUnit.perSecond;
                table.add(Strings.autoFixed(s,1)+"~"+Strings.autoFixed(s*maxAmmoAmountReloadSpeed,1)).left();
                table.add((unit.space ? " " : "") + unit.localized()).left();
            });
        }

        if(loadingAmmoTime>0)stats.add(MD_Stat.loadingammospeed,60/loadingAmmoTime,StatUnit.perSecond);
        if(overheatSpeed>0)stats.add(MD_Stat.overheatspeed,overheatSpeed,StatUnit.perShot);
    }

    @Override
    public void setBars() {
        super.setBars();
        if(loadingAmmoTime>0)addBar("loading-ammo",(constraintItemTurretBuild b)-> new Bar(
                ()-> Core.bundle.get("bar.loadprogress") + "："+(int)(Math.min(100.01f,b.loadProgress / loadingAmmoTime)*100f+0.01f)+"%",
                ()-> Pal.slagOrange,
                ()-> b.loadProgress / loadingAmmoTime
        ));

        if(overheatSpeed>0)addBar("overheat",(constraintItemTurretBuild b)-> new Bar(
                ()-> Core.bundle.format(b.isOverheat?"bar.cooling":"bar.overheat",(int)Math.min(100.01f,(b.heat*100f+0.01f))),
                ()-> Pal.redDust,
                ()-> b.heat
        ));
    }

    public class constraintItemTurretBuild extends ItemTurretBuild{
        public float loadProgress = 0f;
        public float heat = 0;
        public boolean isOverheat = false;

        @Override
        public double sense(LAccess sensor) {
            return switch (sensor){
                case heat -> this.heat;
                default->super.sense(sensor);
            };
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if(super.acceptItem(source, item) && loadProgress>=loadingAmmoTime){
                loadProgress%= loadingAmmoTime;
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void updateTile(){
            if(loadProgress<loadingAmmoTime && loadingAmmoTime>0)loadProgress+=delta()*(!isShooting&& shutDownLoadingSpeedBoost>0?shutDownLoadingSpeedBoost+1f:1f);
            super.updateTile();
        }

        @Override
        public void update(){

            if (overheatSpeed>0) {
                if(heat>=1f && !isOverheat){
                    isOverheat = true;
                    heat = 1f;
                }
                if(isOverheat || (!isShooting && shootWarmup < 0.05f && heat>0.0001f)){
                    heat-=delta()/coolingTime;
                    if(Mathf.chanceDelta(size*(0.08f))){
                        overheat.at(x+Mathf.range (size*tilesize/2f-(size>1?2.5f:0)),y+Mathf.range(size*tilesize/2f-(size>1?2.5f:0)));
                    }
                }
                if(heat<=0.01f){
                    heat = 0;
                    isOverheat = false;
                }
            }
            super.update();
        }

        @Override
        protected void shoot(BulletType type) {
            super.shoot(type);
            if(overheatSpeed>0)heat+=overheatSpeed;
        }

        @Override
        protected void updateReload() {
            if(isOverheat){reloadCounter=0;return;}
            float d = delta() * ammoReloadMultiplier() * baseReloadSpeed();
            if(ammoAmountReloadSpeedBoost>0){
                d *=1+ Mathf.clamp(totalAmmo*ammoAmountReloadSpeedBoost,0,maxAmmoAmountReloadSpeed);
            }
            reloadCounter += d;
        }
    }
}

