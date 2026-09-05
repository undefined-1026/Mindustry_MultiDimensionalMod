package mDimension.world.data;

import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Tmp;
import mDimension.core.MDRenderer;
import mDimension.draw.MDLines;
import mDimension.entity.BeamEntity;
import mDimension.meta.MD_Stat;
import mDimension.tool.Drawff;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.logic.LAccess;
import mindustry.logic.Senseable;

public class Beam extends UnlockableContent implements Senseable {

    public int energyLevel = 3;
    public Seq<Beam> all = new Seq<>();

    public int length = 15;

    public int beamID = -1;
    //no achieve
    public boolean hasDamage = false;
    public float layer = MDRenderer.extBloomLayer;

    public boolean targetAir = false;

    public boolean targetGround = true;

    public Color color = Color.white;
    public Color toColor = Color.white;
    //如果是的，会在子分支里显示，并且无法被激光使用的棱镜转向
    public boolean isParticle  = false;

    public Beam(String name){
        super(name);
        this.databaseCategory = "beam";
        this.color = Color.white;
        if(isParticle){
            this.databaseTag = "particle";
        }else {
            this.databaseTag = "laser";
        }
    }
    public Beam(String name,Color color){
        super(name);
        this.databaseCategory = "beam";
        this.color = color;
        if(isParticle){
            this.databaseTag = "particle";
        }else {
            this.databaseTag = "laser";
        }
    }
    {
        this.beamID = all.size;
        all.add(this);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.error;
    }

    @Override
    public double sense(LAccess sensor) {
        return 0;
    }

    @Override
    public void setStats() {
        stats.add(MD_Stat.energyLevel,energyLevel);
    }
    public Cons<BeamEntity> beamDrawer= l->{
        basicDraw(l,(last,now)->{
            float scl = l.scl*1.15f;
            float z = Draw.z();
            Draw.color(color,0.2f);
            Lines.stroke(5*scl);
            Lines.line(last.x,last.y,now.x,now.y,false);
            Draw.z(z+0.001f);
            Draw.color(color,Color.white,0.2f);
            Lines.stroke(3*scl);
            Lines.line(last.x,last.y,now.x,now.y,false);
            Draw.z(z+0.002f);
            Draw.color(Color.white);
            Lines.stroke(1f*scl);
            Lines.line(last.x,last.y,now.x,now.y,false);
            Draw.z(z);
        },v->{
            float scl = l.scl*0.5f*1.15f;

            Draw.color(color,0.2f);
            Fill.circle(v.x,v.y,5*scl);

            Draw.color(color,Color.white,0.2f);
            Fill.circle(v.x,v.y,3*scl);

            Draw.color(Color.white);
            Fill.circle(v.x,v.y,scl);
        },v->{
            float scl = l.scl*0.5f;
            float z = Draw.z();
            Draw.color(color,0.2f);
            Fill.circle(v.x,v.y,7f*scl);
            Draw.z(z+0.001f);
            Draw.color(color,Color.white,0.2f);
            Fill.circle(v.x,v.y,5f*scl);
            Draw.z(z+0.002f);
            Draw.color(Color.white);
            Fill.circle(v.x,v.y,2f*scl);
            Draw.z(z);
        },(v,rot)->{
            float scl = l.scl*1.15f;
            float dst = 6f;
            Draw.color(color,0.2f);
            Lines.stroke(5*scl);
            MDLines.line2(v.x,v.y, v.x+rot.x*dst,v.y+rot.y*dst);

            Draw.color(color,Color.white,0.2f);
            Lines.stroke(3*scl);
            MDLines.line2(v.x,v.y, v.x+rot.x*dst,v.y+rot.y*dst);

            Draw.color(Color.white);
            Lines.stroke(1f*scl);
            MDLines.line2(v.x,v.y, v.x+rot.x*dst,v.y+rot.y*dst);
        });
    };


    public void basicDraw(BeamEntity l,Cons2<Vec2,Vec2> cons,Cons<Vec2> node,Cons<Vec2> cap,Cons2<Vec2,Vec2> end){
        for(int i=1;i<l.points.size/2-1;i++){
            Draw.z(layer);
            node.get(Tmp.v2.set(l.points.get(i*2),l.points.get(i*2+1)));
        }
        for(int i=1;i<l.points.size/2;i++){
            Draw.z(layer+0.1f);
            cons.get(Tmp.v1.set(l.points.get(i*2-2),l.points.get(i*2-1))
                    ,Tmp.v2.set(l.points.get(i*2),l.points.get(i*2+1))
            );
        }
        int size = l.points.size;
        float tx = l.points.get(size-2);
        float ty = l.points.get(size-1);
        Draw.z(layer+0.01f);
        cap.get(Tmp.v1.set(l.points.get(0),l.points.get(1)));
        if(l.isBlocked){
            cap.get(Tmp.v1.set(tx,ty));
        }else {
            end.get(Tmp.v1.set(tx,ty) , l.rotation);
        }
        Draw.reset();
    }
    public static void DrawCap(BeamEntity l,Cons<Vec2> cap){
        cap.get( Tmp.v1.set(l.points.get(0) , l.points.get(1)));
        if(l.isBlocked){
            int size = l.points.size;
            cap.get( Tmp.v1.set(l.points.get(size-2) , l.points.get(size-1)));
        }
    };
    public static void DrawEnd(BeamEntity l){};
    public static void particleFlowDraw(BeamEntity l, Color color, float length, float spread, float amountMulti, float alpha, float Layer){
        color = color.a(Math.min(alpha*l.beamData.power/10,1));
        Draw.color(color);
        Draw.z(Layer);
        Lines.stroke(0.5f);
        for(int i=1;i<l.points.size/2;i++){
            Vec2 lp = Tmp.v1.set(l.points.get(i*2-2),l.points.get(i*2-1));
            Vec2 np = Tmp.v2.set(l.points.get(i*2),l.points.get(i*2+1));
            float len = Tmp.v1.set(np).sub(lp).len();
            Drawff.particleFlow(l.id,4f,lp.x,lp.y,np.x,np.y,(int)(len*amountMulti), length,spread,3);
        }
        Draw.reset();
    }



}