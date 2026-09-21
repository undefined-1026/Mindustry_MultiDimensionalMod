package mDimension.world.blocks;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Eachable;
import mDimension.entity.BeamEntity;
import mDimension.world.beam.BeamBlock;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class MD_BeamDeflector extends BeamBlock implements Slant{
    public boolean canDeflectorParticle = false;
    public Vec2 afterRotation = new Vec2(1,0);
    public static Vec2 v = new Vec2();
    public boolean diagonalFlip = false;
    public DrawBlock drawer = new DrawDefault();

    public MD_BeamDeflector(String name){
        super(name);
        rotate = true;
        rotateDraw = false;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }


    @Override
    public void flipRotation(BuildPlan plan, boolean x) {
        if(!diagonalFlip) {
            super.flipRotation(plan,x);
        }else slantFlipRotation(plan,x);
    }
    @Override
    public boolean handleBeam(BeamEntity beam, Building b){

        if(beam.step+1<=beam.cycleLength){
            rotateVec(afterRotation,v,b.rotation);
            if(v.x != beam.rotation.x || v.y != beam.rotation.y){
                beam.node(b.x,b.y);
                beam.passBuild.add(b);
                beam.cycleLength =(int) Math.max(
                        (beam.cycleLength-beam.step)*(beam.rotation.len()/v.len())+beam.step
                        ,0);
                beam.rotation.set(v);
                if( beam.cycleLength-beam.step == 0){
                    beam.cx+=v.x*5;
                    beam.cy+=v.y*5;
                    return true;
                }
            }
//            if(cacheRotat.x != rotation.x || cacheRotat.y != rotation.y) {
//                node(cx, cy);
//                passBuild.add(onBuild);
//                length =(int) Math.max(
//                        (length-i)*(rotation.len()/cacheRotat.len())+i
//                        ,0);
//                rotation.set(cacheRotat);
//                Items.lead.description=""+(length-i);
//                if(length-i == 0){
//                    cx+=rotation.x*5;
//                    cy+=rotation.y*5;
//                    break;
//                }
//            }
        }
        return false;
    }

    Vec2 rotateVec(Vec2 scr,Vec2 v,int rotate){
        rotate = (rotate + 4)%4;
        return switch (rotate){
            case 1 -> v.set(-scr.y,scr.x);
            case 2 -> v.set(-scr.x,-scr.y);
            case 3 -> v.set(scr.y,-scr.x);
            default -> v.set(scr);
        };
    }

    public class MD_BeamDeflectorBuild extends BeamBlockBuild{
        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
    }
}
