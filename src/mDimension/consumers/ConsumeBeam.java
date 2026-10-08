package mDimension.consumers;

import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Strings;
import mDimension.content.MD_beams;
import mDimension.meta.MD_StatValues;
import mDimension.world.data.BeamData;
import mDimension.world.data.Beam;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

public class ConsumeBeam extends Consume {
    public static Seq<ConsumeBeam> out = new Seq<>();

    public float maxWavelength = -1;
    public float minWavelength = -1;

    public float requiredPower = 10f;
    //由于aunke使用min，所以改这个diao用没有
    public float maxEfficiency = 1f;

    public Beam inputBeam = MD_beams.near_infrared_light;
    public int maxSize = 256;

    public static Seq<ConsumeBeam> allConsume = new Seq<>();

    public ObjectMap<Building,LaserModule> beamDataMap = new ObjectMap<>();
    public static class LaserModule {
        public float cachePower;
        public float power = 0;
        public LaserModule(){}

        @Override
        public String toString() {
            return ""+power;
        }
    }

    public ConsumeBeam(){allConsume.add(this);}
    public ConsumeBeam(float requiredPower){
        this.requiredPower = requiredPower;
        allConsume.add(this);
    }
    public ConsumeBeam(float requiredPower, float minWavelength, float maxWavelength){
        this.requiredPower = requiredPower;
        this.minWavelength = minWavelength;
        this.maxWavelength = maxWavelength;
        allConsume.add(this);
    }
    public ConsumeBeam(float requiredPower, Beam beam){
        this.requiredPower = requiredPower;
        this.inputBeam = beam;
        allConsume.add(this);
    }
    @Override
    public void apply(Block block) {
        //region Bar
        block.addBar(inputBeam == null?"beam-"+minWavelength+"-"+maxWavelength:"beam-"+inputBeam.name,e->
            new Bar(
                    ()-> inputBeam.localizedName+" "+Core.bundle.format("bar.laserpower", Strings.fixed(getLaserPower(e),1), Strings.fixed(requiredPower,1)),
                    ()->inputBeam.color,
                    ()->getLaserPower(e)/requiredPower
            )
        );
        //endregion
    }

    public static Seq<ConsumeBeam> getBeamConsume(Block block){
        out.clear();
        for(Consume c:block.consumers){
            if(c instanceof ConsumeBeam cl){
                out.add(cl);
            }
        }
        return out;
    }

    @Override
    public float efficiency(Building b) {
        Items.copper.description = "amount:"+ beamDataMap.size;
        if (b.dead) {
            beamDataMap.remove(b);
            return 0;
        }
        LaserModule module = beamDataMap.get(b);
        if (module == null) {
            beamDataMap.put(b, module = new LaserModule());
        }
//        float power = 0;
//        for(int i=0;i<laserDataMap.get(b).laserDatas.size;i++) {
//            BeamData laserData = laserDataMap.get(b).laserDatas.get(i);
//            if (
//                    canConsume(laserData)
//            ) {
//
//                power += laserData.power;
//            }
//        }
        module.power = module.cachePower;
        module.cachePower = 0;


        return Math.min(maxEfficiency, module.power/requiredPower);
    }

    public boolean canConsume(BeamData date){
        return (minWavelength < 0 || minWavelength <= date.wavelengthLevel) &&
                (maxWavelength < 0 || date.wavelengthLevel <= maxWavelength) &&
                (inputBeam == null || inputBeam.equals(date.beam));
    }

    public void accrue(Building b,BeamData data){
        if(!canConsume(data))return;
        if (b.dead) {
            beamDataMap.remove(b);
            return;
        }
        LaserModule module = beamDataMap.get(b);
        if (module == null) {
            beamDataMap.put(b, module = new LaserModule());
        }
        module.cachePower += data.power;
    }

    public float getLaserPower(Building b){
        if(b.dead){
            beamDataMap.remove(b);
            return 0;
        }
        if(beamDataMap.get(b)==null) {
            beamDataMap.put(b,new LaserModule());
            return 0;
        }
        return beamDataMap.get(b).power;

    }
    @Override
    public void display(Stats stats){
        stats.add(booster ? Stat.booster : Stat.input,t->{
            if(inputBeam == null){
                t.add(MD_StatValues.BeamStack(requiredPower,minWavelength,maxWavelength,true));
            }else{
                t.add(MD_StatValues.BeamStack(inputBeam,requiredPower,false));
            }
        });
    }

    public static void free(){
        for (int i=0;i<allConsume.size;i++) {
            ConsumeBeam c = allConsume.get(i);
            for (var e : c.beamDataMap.keys()) {
                if (e!=null&&(e.dead || !e.isValid())) {
                    c.beamDataMap.remove(e);
                }
            }
        }
    }

}
