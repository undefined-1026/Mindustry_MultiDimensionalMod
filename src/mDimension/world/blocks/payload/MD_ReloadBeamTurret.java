package mDimension.world.blocks.payload;

import arc.math.Mathf;
import mDimension.world.blocks.beam.MD_BeamTurret;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class MD_ReloadBeamTurret extends MD_BeamTurret {
    public float reload = 10f;
    public MD_ReloadBeamTurret(String name) {
        super(name);
    }
    @Override
    public void setStats(){
        super.setStats();

        if(coolant != null){
            stats.replace(Stat.booster, StatValues.boosters(reload, coolant.amount, coolantMultiplier, true, coolant::consumes));
        }
    }

    public class MD_ReloadBeamTurretBuild extends MD_BeamTurretBuild {
        public float reloadCounter;

        protected void updateCooling(){
            if(coolant != null && coolant.efficiency(this) > 0 && efficiency > 0){
                float capacity = coolant instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : (coolant.consumes(liquids.current()) ? liquids.current().heatCapacity : 0.4f);
                float amount = coolant.amount * coolant.efficiency(this);
                coolant.update(this);
                reloadCounter += amount * edelta() * capacity * coolantMultiplier * ammoReloadMultiplier();

                if(Mathf.chance(0.06 * amount)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }
            }
        }

        protected float ammoReloadMultiplier(){
            return 1f;
        }

        protected float baseReloadSpeed(){
            return efficiency;
        }
    }
}
