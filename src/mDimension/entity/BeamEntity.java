package mDimension.entity;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.MDimensionMod;
import mDimension.consumers.ConsumeBeam;
import mDimension.content.MD_beams;
import mDimension.world.beam.BeamBlock;
import mDimension.world.data.BeamData;
import mDimension.world.data.Beam;
import mDimension.world.blocks.LaserCrafter;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import java.util.Arrays;

public class BeamEntity implements Entityc, Drawc {
    protected transient int index__all;
    protected transient int index__draw;
    public boolean added = false;
    public boolean isBlocked = false;
    public int id = EntityGroup.nextId();
    public float warmup=0;
    public float scl = 1;
    public Building owner = null;

    public int createId;

    public Vec2 rotation = new Vec2(1,0);
    public Vec2 launchRotation;

    public FloatSeq points = new FloatSeq(6);

    public float x;
    public float y;

    public Seq<Building> passBuild = new Seq<Building>(6);

    public Beam laser = MD_beams.near_infrared_light;
    public BeamData beamData;
    public int step = 0;
    public int cycleLength = 0;
    public float cx,cy;




    public BeamEntity(){

    }
    public BeamEntity(Beam laser, Building owner){
        this.laser = laser;
        this.owner = owner;
    }

    public void setPower(float power){this.beamData.setPower(power);}

    public void start(float x,float y,Vec2 r){
        points.add(x +r.x*4,y +r.y*4);
    }
    public void node(float x, float y){
        points.add(x,y);
    }
    public void end(float x,float y,Vec2 r){
        points.add(x -r.x*4,y -r.y*4);
    }
    public void updateEntity(){
        warmup = Mathf.approachDelta(warmup,beamData.power > 0.01f ?1f:0f,1.5f/60f);
    }
    public float scl(){
        return  (Mathf.absin(Time.time + id*2,3f,0.15f)+1)*warmup;
    }
    @Override
    public void update() {
        this.scl = scl();

        if (!(owner instanceof LaserCrafter.TestCrafterBuild tcb) || !owner.isValid() || !Arrays.asList(tcb.crafterLasers).contains(this)) {
            remove();
            return;
        }

        points.clear();
        passBuild.clear();
        start(x,y,launchRotation);
        isBlocked = false;
        cx=x;cy=y;
        rotation.set(launchRotation);
        updateEntity();
        cycleLength = beamData.length;
        for (int i = 1; i <= cycleLength; i++) {
            step = i;
            cx += rotation.x*8;
            cy += rotation.y*8;
            Building onBuild = buildOn(cx, cy);
            if (onBuild == null) {
                if (onSolid(cx, cy)) {
                    end(cx,cy,rotation);
                    isBlocked = true;
                    break;
                }
                continue;
            }
            if(passBuild.contains(onBuild)){
                end(cx,cy,rotation);
                isBlocked = true;
                break;
            }
            if(onBuild instanceof BeamBlock.BeamBlockBuild beamBuild){
                if(beamBuild.handleBeam(this)){
                    break;
                };
                continue;
            }
            if (ConsumeBeam.getLaserConsume(onBuild.block).size != 0) {
                end(cx,cy,rotation);
                isBlocked = true;
                for (ConsumeBeam c : ConsumeBeam.getLaserConsume(onBuild.block)) {
                    if (c.laserDataMap.get(onBuild) == null) continue;
                    c.accrue(onBuild, beamData);
                }
                break;
            }

            if (onSolid(cx, cy)) {
                end(cx,cy,rotation);
                isBlocked = true;
                break;
            }
        }
        if(!isBlocked){
            end(cx,cy,rotation);
            isBlocked = false;
        }
    }


    @Override
    public void draw() {
        float a = (Core.settings.getInt(MDimensionMod.BEAM_OPACITY,80)/100f);
        Draw.mixcol(laser.toColor,(scl-1) * 2.5f);
        if(warmup>0.01f)laser.beamDrawer.get(this,a);
        Draw.reset();
    }


    public void create(float x, float y,int rotation, int id){
        float rx = 1,ry = 0;
        switch(rotation){
            case(1)->{rx = 0;ry = 1;}
            case(2)->{rx = -1;ry = 0;}
            case(3)->{rx = 0;ry = -1;}
        }
        create(x,y,new Vec2(rx,ry),id);
    }
    public void create(float x, float y,Vec2 rotation,int id){
        this.x = x;
        this.y = y;
        if(buildOn()!=null && buildOn() instanceof LaserCrafter.TestCrafterBuild tcb && id >=0){
            tcb.crafterLasers[id] = this;
        }
        this.launchRotation = rotation;
        this.beamData = new BeamData(laser,0);
        add();
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void add() {
        if(!added) {
            this.index__draw = Groups.draw.addIndex(this);
            this.index__all = Groups.all.addIndex(this);
            added = true;
        }

    }
    @Override
    public void remove() {
        Groups.all.removeIndex(this, this.index__all);
        this.index__all = -1;
        Groups.draw.removeIndex(this, this.index__draw);
        this.index__draw = -1;
        Groups.all.remove(this);
        Groups.draw.remove(this);

        if (Vars.net.client()) {
            Vars.netClient.addRemovedEntity(this.id());
        }
    }

    @Override
    public void id(int i) {
        this.id = i;
    }
    @Override
    public void read(Reads read) {
       // this.x = read.f();
       // this.y = read.f();
    }
    @Override
    public void write(Writes write) {
       // write.f(x);
       // write.f(y);
    }

    @Override
    public float clipSize() {
        return 300f;
    }
    public float x() {return this.x;}
    public float y() {return this.y;}
    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }

    @Override
    public void x(float x) {
        this.x = x;
    }
    @Override
    public void y(float y) {
        this.y = y;
    }

    public int tileX() {return World.toTile(this.x);}
    public int tileY() {return World.toTile(this.y);}
    public Tile tileOn() {return Vars.world.tileWorld(this.x, this.y);}
    public Building buildOn() {return Vars.world.buildWorld(this.x, this.y);}

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public int tileX(float x) {return World.toTile(x);}
    public int tileY(float y) {return World.toTile(y);}
    public Tile tileOn(float x,float y) {return Vars.world.tileWorld(x,y);}
    public Building buildOn(float x,float y) {return Vars.world.buildWorld(x,y);}

    public boolean onSolid(float x,float y) {
        Tile tile = this.tileOn(x,y);
        return tile == null || tile.solid();
    }

    public Block blockOn(float x,float y) {
        Tile tile = this.tileOn(x,y);
        return tile == null ? Blocks.air : tile.block();
    }

    public Floor floorOn(float x,float y) {
        Tile tile = this.tileOn(x,y);
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    @Override
    public void set(Position position) {

    }

    @Override
    public void set(float v, float v1) {

    }

    @Override
    public void trns(Position position) {

    }

    @Override
    public void trns(float v, float v1) {

    }

    @Override
    public <T extends Entityc> T self() {
        return (T)this;
    }

    @Override
    public <T> T as() {
        return (T)this;
    }

    @Override
    public boolean isAdded() {
        return added;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean serialize() {
        return false;
    }

    @Override
    public int classId() {
        return 101;
    }


    @Override
    public void afterRead() {

    }

    @Override
    public void afterReadAll() {

    }

    @Override
    public void beforeWrite() {

    }
}
