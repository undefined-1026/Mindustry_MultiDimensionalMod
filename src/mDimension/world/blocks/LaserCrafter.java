package mDimension.world.blocks;

import arc.math.geom.Vec2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.content.MD_beams;
import mDimension.entity.BeamEntity;
import mDimension.meta.MD_StatValues;
import mDimension.tool.MD_Edge;
import mDimension.world.data.Beam;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;

import java.util.Arrays;

import static mindustry.world.meta.StatValues.stack;


public class LaserCrafter extends GenericCrafter implements Slant{
    public float beamPower = 10f;
    public boolean diagonalFilp = false;
    // one is index;two is laser rotate
    //3x3:
    //[4][3][2]
    //[5][ ][1]  -->
    //[6][7][0]

    public Vec2[] craftPos;
    public Vec2[] craftRotation;
    public Beam beam = MD_beams.near_infrared_light;
    private int beamAmount;
    private Vec2[] beamPos;
    public LaserCrafter(String name){
        super(name);
        rotate = true;
        rotateDraw = false;
        drawArrow = true;
    }
    @Override
    public void flipRotation(BuildPlan plan, boolean x) {
        if(!diagonalFilp) {
            super.flipRotation(plan,x);
        }else slantFlipRotation(plan,x);
    }

    @Override
    public boolean isSlant() {
        return diagonalFilp;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output,t->{
            t.add(MD_StatValues.BeamStack(beam,beamPower));
        });
    }

    @Override
    public void init() {
        super.init();
        if(craftPos == null){
            craftPos = MD_Edge.getFacingNearby(this);
        }else{
            for(Vec2 v:craftPos){
                MD_Edge.LimitInSquare(v,(this.size-1)*8f);
            }
        }
        if(craftRotation == null){
            craftRotation = new Vec2[craftPos.length];
            Arrays.fill(craftRotation,new Vec2(1,0));
        }
        beamPos = craftPos;

        beamAmount = beamPos.length;
    }

    public class TestCrafterBuild extends GenericCrafterBuild{

        public BeamEntity[] crafterLasers = new BeamEntity[beamAmount];

        public int lastRotation;

        {
            Arrays.fill(crafterLasers,null);
            lastRotation = rotation;
        }

        @Override
        public void update(){
            super.update();
            if(lastRotation!=rotation){
                Arrays.fill(crafterLasers,null);
                lastRotation = rotation;
            }
            for(int i = 0;i<beamAmount;i++) {
                if (crafterLasers[i] == null) {
                    Vec2 p = MD_Edge.transpose(craftPos[i].cpy(),rotation).add(x,y);
                    BeamEntity laserEntity = new BeamEntity(beam,this);
                    laserEntity.create(p.x, p.y, MD_Edge.transpose(craftRotation[i].cpy(),rotation), i);
                    if(this.team == Vars.player.team())laserEntity.beamData.beam.unlock();
                } else {
                    crafterLasers[i].setPower(efficiency*warmup*beamPower/beamAmount);
                }
            }

        }

        @Override
        public void read(Reads read) {
            super.read(read);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
        }
    }
}
