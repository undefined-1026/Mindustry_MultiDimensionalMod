package mDimension.world.type;

import arc.func.Boolf;
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
    public static final Seq<Beam> all = new Seq<>();
    public Seq<BeamEntity> beamEntities = new Seq<>(false,128);
    public float noCollisionScl = 0.5f;

    public int length = 15;
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
        drawer = new BeamDrawers(
                new BeamDrawLayer(0.2f, color, 5f, 6f),
                new BeamDrawLayer(1f, color, 3f, 3.5f),
                new BeamDrawLayer(1f, Color.white, 1f, 1.15f) {{
                    canDrawOfPolarizing = false;
                }}
        );
    }
    {
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
    public BeamDrawers drawer;
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
    public static class BeamDrawers{
        public BeamDrawLayer[] drawLayers;
        public BeamDrawers(BeamDrawLayer... drawLayers) {
            this.drawLayers = drawLayers;
        }
    }
    public static class BeamDrawLayer {
        public boolean canDrawOfPolarizing = true;
        public float stroke;
        public Color color = Color.white;
        public float capSize;
        public float alpha = 1f;
        public BeamDrawLayer() {
        }
        public BeamDrawLayer(float alpha, Color color, float stroke, float capSize) {
            this.alpha = alpha;
            this.color = color;
            this.stroke = stroke;
            this.capSize = capSize;
        }


        public void draw(BeamEntity e){
            if (!e.vertexes.isEmpty()) {
                drawLine(e.vertexes,e.strokeScl);
                drawVertexes(e.vertexes,e.strokeScl);
                var vertexes = e.vertexes;
                float sx = vertexes.get(0),sy = vertexes.get(1),ss = vertexes.get(2);
                float ex = vertexes.get(vertexes.size-3),ey = vertexes.get(vertexes.size-2),es = vertexes.get(vertexes.size-1);
                ;
                drawStartCap(sx,sy,ss,e.strokeScl,e.launchRotation);
                if(e.isBlocked){
                    drawBlockedEndCap(ex,ey,es,e.strokeScl,e.rotation);
                }else drawEndCap(ex,ey,es,e.strokeScl,e.rotation);
            }
        }
        public void drawLine(FloatSeq vertexes,float strokeScl) {
            float str = stroke*strokeScl;
            for (int i = 0; i+4< vertexes.size; i+=3) {
                float x = vertexes.get(i),y = vertexes.get(i+1),s = vertexes.get(i+2);
                if(!canDrawOfPolarizing && s<0.99f)continue;
                float nx = vertexes.get(i+3),ny = vertexes.get(i+4);
                Lines.stroke(s*str);
                Lines.line(x,y,nx,ny,false);
            }
        }
        public void drawVertexes(FloatSeq vertexes,float strokeScl){
            float str = stroke*strokeScl/2f;
            for (int i = 3; i+5< vertexes.size; i+=3) {
                float x = vertexes.get(i),y = vertexes.get(i+1),s = vertexes.get(i+2);
                if(!canDrawOfPolarizing && s<0.99f)continue;
                Fill.circle(x,y,s*str);
            }
        }
        public void drawStartCap(float x,float y,float s,float strokeScl,Vec2 startVel) {
            if(!canDrawOfPolarizing && s<0.99f)return;
            float str = capSize*strokeScl/2f;
            Fill.circle(x,y,s*str);
        }
        public void drawEndCap(float x,float y,float s,float strokeScl,Vec2 endVel) {
            if(!canDrawOfPolarizing && s<0.99f)return;
            float str = stroke*strokeScl;
            Lines.stroke(str*s);
            MDLines.line2(x,y,x + endVel.x*6f,y + endVel.y*6f);
        }
        public void drawBlockedEndCap(float x,float y,float s,float strokeScl,Vec2 endVel) {
            drawStartCap(x,y,s,strokeScl,endVel);
        }
    }


    public void basicDraw(BeamEntity l,
                          Cons3<Vec2,Vec2,Float> line,
                          Cons2<Vec2,Float> node,
                          Cons2<Vec2,Float> cap,
                          Cons3<Vec2,Vec2,Float> end){
        float scl = l.strokeScl;
        FloatSeq v = l.vertexes;
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
}