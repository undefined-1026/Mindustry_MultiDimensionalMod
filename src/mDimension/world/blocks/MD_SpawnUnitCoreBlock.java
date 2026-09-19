package mDimension.world.blocks;

import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.content.MD_UnitTypes;
import mindustry.ai.types.AssemblerAI;
import mindustry.content.Fx;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.net;

public class MD_SpawnUnitCoreBlock extends CoreBlock {
    public int unitAmount = 1;
    public float spawnRotate =0,offset=0,unitBuildTime = 60f;
    public UnitType spawnUnitType = MD_UnitTypes.engineering_drone;
    public float launchSpeed = 0.8f;
    public int maxUnit = 12;

    public MD_SpawnUnitCoreBlock(String name) {
        super(name);
        squareSprite = false;
    }

    public class MD_SpawnUnitCoreBuild extends CoreBuild implements UnitTetherBlock{
        public int spawnCount = 0;

        protected IntSeq readUnits = new IntSeq();
        //holds drone IDs that have been sent, but not synced yet - add to list as soon as possible
        protected IntSeq whenSyncedUnits = new IntSeq();
        public Seq<Unit> units = new Seq<>();
        public float buildProgress;

        public void spawned(int id){
            Fx.spawn.at(x, y);
            buildProgress = 0f;
            if(net.client()){
                whenSyncedUnits.add(id);
            }
        }

        @Override
        public void updateTile() {
            if(!readUnits.isEmpty()){
                units.clear();
                readUnits.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if(unit != null){
                        units.add(unit);
                    }
                });
                readUnits.clear();
            }

            //read newly synced drones on client end
            if(units.size < unitAmount && whenSyncedUnits.size > 0){
                whenSyncedUnits.each(id -> {
                    var unit = Groups.unit.getByID(id);
                    if(unit != null){
                        units.addUnique(unit);
                    }
                });
            }
            units.removeAll(u -> !u.isAdded() || u.dead);
            //unsupported
            if(!allowUpdate()){
                buildProgress = 0f;
                units.each(Unit::kill);
                units.clear();
            }
            if(units.size < unitAmount && enabled && (buildProgress += delta() / unitBuildTime) >= 1f && (maxUnit<0 || team.data().countType(spawnUnitType) < maxUnit)){
                if(!net.client()){
                    var unit = spawnUnitType.create(team);
                    //If a unit isn't using AssemblerAI, it's bugged, likely because of an incorrect data patch or mod.
                    //In that case, just ignore it and don't spawn anything
                    if(unit instanceof BuildingTetherc bt){
                        bt.building(this);
                    }
                    unit.rotation = unitAmount == 1?90f:(spawnCount%unitAmount)*(360f/unitAmount) + spawnRotate;

                    if(unitAmount!=1){
                        unit.set(Tmp.v2.trns(unit.rotation,offset).add(x,y));
                        unit.vel.trns(unit.rotation,launchSpeed);
                    }else{
                        unit.set(x,y);
                    }
                    unit.add();
                    units.add(unit);
                    Call.unitTetherBlockSpawned(tile, unit.id);
                    spawnCount++;
                    buildProgress = 0f;
                }
            }
        }


        @Override
        public void write(Writes write){
            super.write(write);

            write.b(units.size);
            for(var unit : units){
                write.i(unit.id);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int count = read.b();
            readUnits.clear();
            for(int i = 0; i < count; i++){
                readUnits.add(read.i());
            }
            whenSyncedUnits.clear();
        }
    }
}
