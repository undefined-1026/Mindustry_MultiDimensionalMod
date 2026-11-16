package mDimension.world.blocks;

import arc.graphics.Color;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.consumers.ConsumeBeam;
import mDimension.content.MD_beams;
import mDimension.entity.BeamEntity;
import mDimension.meta.MD_StatValues;
import mDimension.tool.MD_Edge;
import mDimension.world.blocks.beam.BeamBlock;
import mDimension.world.type.Beam;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;

import java.util.Arrays;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;
import static mindustry.world.meta.StatValues.stack;


public class MD_BeamLaunch extends GenericCrafter implements Slant{
    public float beamPower = 10f;
    public boolean diagonal = false;
    protected int beamAmount=0;
    // Beam,LaunchX,LaunchY,RotationX,RotationY,....
    public Object[] beams = new Object[]{
            MD_beams.near_infrared_light,4,-4,1,0,
            MD_beams.near_infrared_light,4,4,1,0,
    };
    public MD_BeamLaunch(String name){
        super(name);
        rotate = true;
        rotateDraw = false;
        drawArrow = true;
    }
    @Override
    public void flipRotation(BuildPlan plan, boolean x) {
        if(!diagonal) {
            super.flipRotation(plan,x);
        }else slantFlipRotation(plan,x);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Color c = valid? Pal.placing:Pal.remove;
        float wx = x * tilesize + offset;
        float wy = y * tilesize + offset;
        for (int i = 0; i < beams.length; i+=5) {
            var p = MD_Edge.transpose(Tmp.v3.set(f(beams[i + 1]), f(beams[i + 2])), rotation);
            var r = MD_Edge.transpose(Tmp.v2.set(f(beams[i + 3]), f(beams[i + 4])), rotation);
            var beam = (Beam) beams[i];
            float len = beam.length * 8;

            for (int j = 1; j <= beam.length; j++) {

                float cx = wx + p.x + j * r.x * tilesize,
                        cy = wy + p.y + j * r.y * tilesize;
                var t = world.tileWorld(cx, cy);
                if (t == null || t.solid()){
                    len = j*8;
                    break;
                }
                var b = t.build;
                if (b != null) {
                    if (b instanceof BeamBlock.BeamBlockBuild || b instanceof BeamConsumer || b.block.findConsumer(co -> co instanceof ConsumeBeam)!=null) {
                        len = j*8;
                        break;
                    }
                }
            }
            Drawf.dashLine(c,
                    wx + p.x + r.x * 3, wy + p.y + r.y * 3,
                    wx + p.x + r.x*len, wy + p.y + r.y*len
            );
        }
    }

    @Override
    public boolean isSlant() {
        return diagonal;
    }
    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output,t->{
            for (int i = 0; i < beams.length; i+=5) {
                t.add(MD_StatValues.BeamStack((Beam) beams[i],beamPower)).row();
            }
        });
    }

    public void beamReasted(Building self, BeamEntity e){
    }

    @Override
    public void init() {
        super.init();
        beamAmount = beams.length/5;
    }

    public class MD_BeamLaunchBuild extends GenericCrafterBuild{

        public BeamEntity[] crafterBeams = new BeamEntity[beamAmount];

        public int lastRotation;

        {
            Arrays.fill(crafterBeams,null);
            lastRotation = rotation;
        }

        @Override
        public void update(){
            super.update();
            if(lastRotation!=rotation){
                Arrays.fill(crafterBeams,null);
                lastRotation = rotation;
            }
            for (int i = 0; i < beams.length; i+=5) {
                if (crafterBeams[i/5] == null) {
                    var p = MD_Edge.transpose(Tmp.v3.set(f(beams[i+1]),f(beams[i+2])),rotation);
                    var r = MD_Edge.transpose(Tmp.v2.set(f(beams[i+3]),f(beams[i+4])),rotation);
                    var e = new BeamEntity((Beam) beams[i],this);
                    e.create(p.x+x,p.y+y,r,i/5);
                    crafterBeams[i/5] = e;
                } else {
                    crafterBeams[i/5].setPower(efficiency*warmup*beamPower/beamAmount);
                    if(efficiency>0.01f && !net.client() && state.isCampaign() && team == state.rules.defaultTeam)
                        crafterBeams[i/5].beamData.beam.unlock();
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

    float f(Object o){
        if(!(o instanceof Number))return 0f;
        return ((Number) o).floatValue();
    }
}
