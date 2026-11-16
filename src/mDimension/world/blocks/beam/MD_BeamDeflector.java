package mDimension.world.blocks.beam;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Eachable;
import mDimension.entity.BeamEntity;
import mDimension.world.blocks.Slant;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class MD_BeamDeflector extends BeamBlock implements Slant {
    public boolean canDeflectorParticle = false;
    public Vec2 afterRotation = new Vec2(1,0);
    public static Vec2 v = new Vec2();
    public boolean diagonalFlip = false;
    public DrawBlock drawer = new DrawDefault();
    public boolean guidance = true;
    public boolean polarizing = false;

    public MD_BeamDeflector(String name){
        super(name);
        rotateDraw = false;
    }

    @Override
    public boolean isSlant() {
        return diagonalFlip;
    }

    @Override
    public void load() {
        super.load();
        rotate = guidance;
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
    public boolean handleBeam(BeamEntity e, Building b){
        if(!b.enabled)return false;
        if(guidance)if(e.step+1<=e.cycleLength){
            rotateVec(afterRotation,v,b.rotation);
            if(v.x != e.rotation.x || v.y != e.rotation.y){
                e.node(b.x,b.y);
                e.passBuild.add(b);
                e.cycleLength =(int) Math.max(
                        (e.cycleLength-e.step)*(e.rotation.len()/v.len())+e.step
                        ,0);
                e.rotation.set(v);
                if( e.cycleLength-e.step == 0){
                    e.cx+=v.x*5;
                    e.cy+=v.y*5;
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
        if(polarizing){
            e.node(e.cx,e.cy);
            e.collision = !e.collision;
            e.node(e.cx,e.cy);
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
