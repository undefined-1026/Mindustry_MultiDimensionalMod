package mDimension.entity;

import arc.math.Angles;
import arc.math.geom.Position;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Posc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
//only get x and y
public class TmpPos implements Posc {
    public float x,y;
    public TmpPos() {
    }
    public TmpPos(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public TmpPos(Posc posc) {
        this(posc.x(),posc.y());
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    @Override
    public Building buildOn() {
        return tileOn().build;
    }

    @Override
    public boolean onSolid() {
        return tileOn().solid();
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }


    @Nullable
    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    @Override
    public void set(Position position) {
        x = position.getX();
        y = position.getY();
    }
    public void set(Posc position) {
        x = position.x();
        y = position.y();
    }
    @Override
    public void set(float v, float v1) {
        x = v;
        y = v1;
    }
    @Override
    public void trns(Position position) {
        x = position.getX();
        y = position.getY();
    }

    @Override
    public void trns(float v, float v1) {
        x = Angles.trnsx(v,v1);
        y = Angles.trnsy(v,v1);
    }

    @Override
    public void x(float v) {
        x=v;
    }

    @Override
    public void y(float v) {
        y=v;
    }

    @Override
    public <T extends Entityc> T self() {
        return null;
    }

    @Override
    public <T> T as() {
        return null;
    }

    @Override
    public boolean isAdded() {
        return false;
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
        return 0;
    }

    @Override
    public int id() {
        return 0;
    }

    @Override
    public void add() {

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

    @Override
    public void id(int i) {

    }

    @Override
    public void read(Reads reads) {

    }

    @Override
    public void remove() {

    }

    @Override
    public void update() {

    }

    @Override
    public void write(Writes writes) {

    }
}
