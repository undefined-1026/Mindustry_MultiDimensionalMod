package mDimension.tool;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.*;

public class MD_Edge {
    public static Vec2[] getFacingNearby(Building b){
        if(b.block.size<=1)return new Vec2[]{new Vec2(b.x,b.y)};
        int size = b.block.size;
        Vec2[] points = new Vec2[size];
        for(int i = 0;i<size;i++){
            points[i] = transpose(new Vec2(((size-1)/2f)*8f,((size-1)/2f)*8f-i*8),b.rotation);
        }
        for(int i = 0;i<size;i++){
            points[i].add(b.x,b.y);
        }
        return points;
    }
    public static Vec2[] getFacingNearby(Block block){
        if(block.size<=1)return new Vec2[]{new Vec2(0,0)};
        int size = block.size;
        Vec2[] points = new Vec2[size];
        for(int i = 0;i<size;i++){
            points[i] = new Vec2(((size-1)/2f)*8f,((size-1)/2f)*8f-i*8);
        }
        return points;
    }
    public static Vec2[] getFacingNearby(Building b,int r){
        if(b.block.size<=1)return new Vec2[]{new Vec2(b.x,b.y)};
        int size = b.block.size;
        Vec2[] points = new Vec2[size];
        for(int i = 0;i<size;i++){
            points[i] = transpose(new Vec2(((size-1)/2f)*8f,((size-1)/2f)*8f-i*8),r);
        }
        for(int i = 0;i<size;i++){
            points[i].add(b.x,b.y);
        }
        return points;
    }

    public static Vec2 transpose(Vec2 v, int r){
        if(r == 0)return v;
        switch (r){
            case(1)-> v.set(-1*v.y,v.x);
            case(2)-> v.set(-1*v.x,-1*v.y);
            case(3)-> v.set(v.y,-1*v.x);
        }
        return v;
    }

    public static Vec2 direction (int r){
        switch (r){
            case(1)-> {
                return new Vec2(0,1);
            }
            case(2)-> {
                return new Vec2(-1,0);
            }
            case(3)-> {
                return new Vec2(0,-1);
            }
        }
        return new Vec2(1,0);
    }

    public static Building alignedNearby(Building b,int r){
        boolean isFind = false;
        Building cache = null;
        for(var v:getFacingNearby(b,r)){
            Vec2 rotat = direction(b.rotation);
            v.add(rotat.x*8,rotat.y*8);
            Building onBuild = world.buildWorld(v.x,v.y);
            if(onBuild == null)return null;
            if(!isFind){cache = onBuild;isFind = true;continue;}
            if(cache != onBuild)return null;
            if(onBuild.block.size < b.block.size)return null;
        }
        return cache;
    }
    public static Vec2 LimitInSquare(Vec2 v,float sideLen){
        v.x = Mathf.clamp(v.x,-sideLen/2,sideLen/2);
        v.y = Mathf.clamp(v.y,-sideLen/2,sideLen/2);
        return v;
    }

    public static int[] isInDiagonal(Building b, Tile t){
        if(t == null || b == null)return new int[]{-1};
        if(
                Math.abs(
                        Math.abs(b.x-t.worldx())-Math.abs(b.y-t.worldy())
                )<0.01f
        ){
            if(t.worldx()>b.x && t.worldy()>b.y)return new int[]{2,3};
            if(t.worldx()<b.x && t.worldy()>b.y)return new int[]{0,3};
            if(t.worldx()>b.x && t.worldy()<b.y)return new int[]{1,2};
            if(t.worldx()<b.x && t.worldy()<b.y)return new int[]{0,1};
        }

        return new int[]{-1};
    }
    public static Tile[] getFacingTile(Building b){
        return getFacingTile(b,-1,-1);
    }

    public static Tile[] getFacingTile(Building b,int start,int end){
        int size=b.block.size;
        int r = (b.rotation+1)%4;
        int dx = Geometry.d4x(r);
        int dy = Geometry.d4y(r);
        int l = size/2+1;

        int ox = f4[b.rotation].x*l;
        int oy = f4[b.rotation].y*l;
        if(b.block.size %2 == 0) {
            ox+=f4i[b.rotation].x;
            oy+=f4i[b.rotation].y;
        }
        int len = start == -1||end == -1?size:end-start+1;
        var res = new Tile[len];
        int s = start!=-1?start:0;
        for(int i=0;i<len;i++){
            int wx = b.tile.x + ox + dx*(i + s);
            int wy = b.tile.y + oy + dy*(i + s);
            res[i] = world.tile(wx,wy);
        }

        return res;
    }
    public static Building getAllFacingBuild(Building b){
        Building res = null;
        for(var t:getFacingTile(b)){
            if(t == null)return null;
            Building other = t.build;
            if(other ==null)return null;
            if(res == null){
                res = other;
            }else if(res != other){
                return null;
            }
        }
        return res;
    }
    //OOOOOO
    //OOOOOO
    //OOOOOO ->OO
    //OOOOOO ->OO
    //OOOOOO
    //OOOOOO

    public static Tile getFacingVoidTile(Building b){
        if (b.block.size %2 ==0) {
            int start = b.block.size/2-1;
            int end = b.block.size/2;
            //int var1=0;
            for(var t:getFacingTile(b,start,end)){
                if(!t.solid())return t;
            }
            return null;
        }else{
            int trns = b.block.size / 2 + 1;
            Tile next = b.tile.nearby(Geometry.d4(b.rotation).x * trns, Geometry.d4(b.rotation).y * trns);
            return next.solid() ? null : next;
        }
    }
    public static Vec2 bias(Vec2 scr,Building b){
        var tile = getFacingVoidTile(b);
        if(tile == null)return scr.set(0,0);
        scr.set(tile.worldx()-b.x,tile.worldy()-b.y);
        if(Math.abs(scr.x)>Math.abs(scr.y)){
            scr.x = 0;
            scr.y = Math.signum(scr.y) * 0.01f;
        }else{
            scr.y = 0;
            scr.x = Math.signum(scr.x) * 0.01f;
        }
        return scr;
    }
    public static Building getFacingBuild(Building b){
        if (b.block.size %2 ==0) {
            Building res = null;
            int start = b.block.size/2-1;
            int end = b.block.size/2;
            //int var1=0;
            for(var t:getFacingTile(b,start,end)){
                if(t == null)return null;
                //Debug.point(t.worldx(),t.worldy());
                //Debug.string(var1 + "("+t.x+","+t.y+")",60f,150*8 + var1*10,160*8);
                Building other = t.build;
                //var1++;
                if(other ==null)return null;
                if(res == null){
                    res = other;
                }else if(res != other){
                    return null;
                }
            }
            return res;
        }else{
            int trns = b.block.size / 2 + 1;
            Tile next = b.tile.nearby(Geometry.d4(b.rotation).x * trns, Geometry.d4(b.rotation).y * trns);
            if (next != null && next.build != null) {
                return next.build;
            } else {
                return null;
            }
        }
    }

    public static final Point2[] f4=new Point2[]{
            new Point2(1,-1),
            new Point2(1,1),
            new Point2(-1,1),
            new Point2(-1,-1)
    };

    ///c x | x x
    ///c x | x x
    ///----o----
    ///c x | x x
    ///c c | c c

    public static final Point2[] f4i=new Point2[]{
            new Point2(0,2),
            new Point2(-1,0),
            new Point2(1,-1),
            new Point2(2,1)
    };
}
