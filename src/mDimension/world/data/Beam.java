package mDimension.world.data;

import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Cons3;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Tmp;
import mDimension.core.MDRenderer;
import mDimension.draw.MDLines;
import mDimension.entity.BeamEntity;
import mDimension.meta.MD_Stat;
import mDimension.tool.Drawff;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;

public class Beam extends UnlockableContent {

    public int energyLevel = 3;
    public Seq<Beam> all = new Seq<>();
    public float noCollisionScl = 0.5f;

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
    public void setStats() {
        stats.add(MD_Stat.energyLevel,energyLevel);
    }
    public Cons2<BeamEntity,Float> beamDrawer= (l,a)->{
        basicDraw(l,(last,now,scl)->{
            scl*=1.15f;
            float z = Draw.z();
            Draw.color(color,0.2f*a);
            Lines.stroke(5*scl);
            Lines.line(last.x,last.y,now.x,now.y,false);
            Draw.z(z+0.1f);
            Draw.color(color,Color.white,0.2f);
            Draw.alpha(a);
            Lines.stroke(3*scl);
            Lines.line(last.x,last.y,now.x,now.y,false);
            if (scl>0.8f) {
                Draw.z(z+0.2f);
                Draw.color(Color.white,a);
                Lines.stroke(scl);
                Lines.line(last.x,last.y,now.x,now.y,false);
            }
            Draw.z(z);
        },(v,scl)->{
            scl*=0.5f*1.15f;

            Draw.color(color,0.2f*a);
            Fill.circle(v.x,v.y,5*scl);

            Draw.color(color,Color.white,0.2f);
            Draw.alpha(a);
            Fill.circle(v.x,v.y,3*scl);

            Draw.color(Color.white,a);
            Fill.circle(v.x,v.y,scl);
        },(v,scl)->{
            scl*=0.5f;
            float z = Draw.z();
            Draw.color(color,0.2f*a);
            Fill.circle(v.x,v.y,7f*scl);
            Draw.z(z+0.1f);
            Draw.color(color,Color.white,0.2f);
            Draw.alpha(a);
            Fill.circle(v.x,v.y,5f*scl);
            Draw.z(z+0.2f);
            Draw.color(Color.white,a);
            Fill.circle(v.x,v.y,2f*scl);
            Draw.z(z);
        },(v,rot,scl)->{
            scl*=1.15f;
            float dst = 6f;
            Draw.color(color,0.2f*a);
            Lines.stroke(5*scl);
            MDLines.line2(v.x,v.y, v.x+rot.x*dst,v.y+rot.y*dst);

            Draw.color(color,Color.white,0.2f);
            Draw.alpha(a);
            Lines.stroke(3*scl);
            MDLines.line2(v.x,v.y, v.x+rot.x*dst,v.y+rot.y*dst);

            Draw.color(Color.white,a);
            Lines.stroke(scl);
            MDLines.line2(v.x,v.y, v.x+rot.x*dst,v.y+rot.y*dst);
        });
    };


    public void basicDraw(BeamEntity l,
                          Cons3<Vec2,Vec2,Float> line,
                          Cons2<Vec2,Float> node,
                          Cons2<Vec2,Float> cap,
                          Cons3<Vec2,Vec2,Float> end){
        float scl = l.strokeScl;
        FloatSeq v = l.vertexs;
        for(int i = 1; i< v.size/3-1; i++){
            Draw.z(layer);
            node.get(Tmp.v2.set(v.get(i*3), v.get(i*3+1)) , scl* v.get(i*3+2));
        }
        for(int i = 1; i< v.size/3; i++){
            Draw.z(layer+0.01f);
            line.get(Tmp.v1.set(v.get(i*3-3), v.get(i*3-2))
                    ,Tmp.v2.set(v.get(i*3), v.get(i*3+1))
                    , scl* v.get(i*3-1)
            );
        }
        int size = v.size;
        float tx = v.get(size-3);
        float ty = v.get(size-2);
        float ts = v.get(size-1);
        Draw.z(layer+0.02f);
        cap.get(Tmp.v1.set(v.get(0), v.get(1)) , scl*v.get(2));
        if(l.isBlocked){
            cap.get(Tmp.v1.set(tx,ty) , scl*ts);
        }else {
            end.get(Tmp.v1.set(tx,ty) , l.rotation , scl*ts);
        }
        Draw.reset();
    }
    public static void DrawCap(BeamEntity l,Cons<Vec2> cap){
        cap.get( Tmp.v1.set(l.vertexs.get(0) , l.vertexs.get(1)));
        if(l.isBlocked){
            int size = l.vertexs.size;
            cap.get( Tmp.v1.set(l.vertexs.get(size-2) , l.vertexs.get(size-1)));
        }
    };
    public static void DrawEnd(BeamEntity l){};
    public static void particleFlowDraw(BeamEntity l, Color color, float length, float spread, float amountMulti, float alpha, float Layer){
        color = color.a(Math.min(alpha*l.beamData.power/10,1));
        Draw.color(color);
        Draw.z(Layer);
        Lines.stroke(0.5f);
        for(int i = 1; i<l.vertexs.size/3; i++){
            Vec2 lp = Tmp.v1.set(l.vertexs.get(i*2-2),l.vertexs.get(i*2-1));
            Vec2 np = Tmp.v2.set(l.vertexs.get(i*2),l.vertexs.get(i*2+1));
            float len = Tmp.v1.set(np).sub(lp).len();
            Drawff.particleFlow(l.id,4f,lp.x,lp.y,np.x,np.y,(int)(len*amountMulti), length,spread,3);
        }
        Draw.reset();
    }



}