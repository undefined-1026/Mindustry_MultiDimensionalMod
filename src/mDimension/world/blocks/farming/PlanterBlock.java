package mDimension.world.blocks.farming;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.content.MD_Fx;
import mDimension.content.MD_UnitTypes;
import mDimension.world.type.SeedItem;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;


import static arc.math.geom.Geometry.d4;
import static mindustry.Vars.*;

public class PlanterBlock extends Block {
    public int regionSize = 7;
    public Effect takeEffect = Fx.itemTransfer,planterEffect = MD_Fx.planter;
    public float seedingInterval = 30f;
    public UnitType unitType = MD_UnitTypes.farmer;
    public float unitBuildTime = 5*60f;
    public DrawBlock drawer = new DrawDefault();
    public Sound planterSound = Sounds.plantBreak;
    public float planterSoundPit = 1.25f;
    public PlanterBlock(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        rotate = true;
        rotateDraw = false;
        flags = EnumSet.of(BlockFlag.unitAssembler);
    }
    @Override
    public void load() {
        super.load();
        drawer.load(this);
        if(itemFilter.length == 0){
            itemFilter = new boolean[content.items().size];
        }
        Vars.content.items().each(i->i instanceof SeedItem,t->itemFilter[t.id]=true);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public void drawPlace(int tx, int ty, int rotation, boolean valid) {
        super.drawPlace(tx, ty, rotation, valid);
        int d =  regionSize/2 + size/2+1;
        int x = tx + d4[rotation].x *d;
        int y = ty + d4[rotation].y *d;
        Drawf.dashSquare(valid ? Pal.accent : Pal.remove,x*8,y*8,regionSize*8);

    }
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        //overlapping construction areas not allowed unless it s being replaced; grow by a tiny amount so edges can't overlap either.
        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, rotation).grow(0.1f);
        return
                !indexer.getFlagged(team, BlockFlag.unitAssembler).contains(b -> b != tile.build && b.block instanceof PlanterBlock planterBlock && planterBlock.getRect(Tmp.r2, b.x, b.y, b.rotation).overlaps(rect)) &&
                        !team.data().getBuildings(ConstructBlock.get(size)).contains(b -> b != tile.build && ((ConstructBlock.ConstructBuild)b).current instanceof PlanterBlock planterBlock && planterBlock.getRect(Tmp.r2, b.x, b.y, b.rotation).overlaps(rect));
    }

    public Rect getRect(Rect rect, float x, float y, int rotation){
        rect.setCentered(x, y, regionSize * tilesize);
        float len = tilesize * (regionSize + size)/2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }
    public class PlanterBlockBuild extends Building implements UnitTetherBlock {
        public @Nullable Unit unit;
        public int readUnitId = -1;
        public float seedingProgress = 0f;
        public float buildProgress=0;
        public float totalProgress;
        public float warmup, readyness;
        public Crop crop;
        public Tile target;
        public Item consumeItem;
        public float lastTime = 0;
        public boolean isFull;
        public void spawned(int id){
            Fx.spawn.at(x, y);
            buildProgress = 0f;
            if(net.client()){
                readUnitId = id;
            }
        }

        @Override
        public void updateTile() {
            if(unit!=null && (seedingProgress += edelta())>=seedingInterval){
                findCrop();
                if(crop!=null && items.has(crop.seed,crop.seedingRequirements)){
                    updateSeed();
                }
            }
            //unit was lost/destroyed
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !net.client()){
                    readUnitId = -1;
                }
            }

            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);

            if(unit == null && Units.canCreate(team, unitType)){
                buildProgress += delta() / unitBuildTime;
                totalProgress += delta();

                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = unitType.create(team);
                        if(unit instanceof BuildingTetherc bt){
                            bt.building(this);
                        }
                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();
                        Call.unitTetherBlockSpawned(tile, unit.id);
                        consume();
                    }
                }
            }
        }
        public void updateSeed(){
            if (!canSeeding(target) && lastTime+10< Time.time) {
                lastTime = Time.time;
                int d =  regionSize/2 + size/2+1;
                int x = this.tile.x + d4[this.rotation].x *d;
                int y = this.tile.y + d4[this.rotation].y *d;
                int mx =x+regionSize/2,my =y+regionSize/2,ox =x-regionSize/2,oy =y-regionSize/2;
                target = null;
                float dst = -1;
                for(int dy=oy;dy<=my;dy++) {
                    for (int dx = ox; dx <= mx; dx++) {
                        Tile other = world.tile(dx, dy);
                        //Debug.pointTile(dx,dy);
                        if (other == null) continue;
                        float od = Mathf.dst2(other.worldx(), other.worldy(), unit.x, unit.y);
                        if (canSeeding(other) && (od < dst || dst < 0)) {
                            target = other;
                            dst = od;
                        }
                    }
                }
            }
            if(target!=null){
                isFull = false;
                float tx = target.worldx(),ty = target.worldy();
                if(unit.within(tx,ty,5f)){
                    planterSound.at(target.worldx(),target.worldy(),planterSoundPit + Mathf.random(-0.1f,0.1f),0.2f);
                    planterEffect.at(target.worldx(),target.worldy(),target.floor().mapColor);
                    Call.setTile(target,crop, Team.derelict,0);
                    takeEffect.at(x,y,0, Color.white,unit);
                    items.remove(crop.seed,crop.seedingRequirements);
                    seedingProgress=0;
                }
            }else{
                isFull = true;
            }


        }
        boolean findCrop(){
            crop = null;
            consumeItem = null;
            for(var e:SeedItem.map.entries()){
                if(items.has(e.key)){
                    crop = (Crop)e.value;
                    consumeItem = e.key;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldConsume() {
            return enabled && !isFull && unit != null && findCrop() && crop!=null && items.has(crop.seed,crop.seedingRequirements);
        }

        boolean canSeeding(Tile tile){

            return tile!=null && !tile.solid() && !tile.floor().isDeep() && tile.build == null && crop.canPlaceOn(tile,Team.derelict, 0);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(unit == null ? -1 : unit.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            readUnitId = read.i();
        }

        @Override
        public void draw() {
            super.draw();
            drawer.draw(this);
            int d =  regionSize/2 + size/2+1;
            int x = this.tile.x + d4[this.rotation].x *d;
            int y = this.tile.y + d4[this.rotation].y *d;
            Draw.z(Layer.buildBeam);
            Lines.stroke(2f);
            Draw.color(Pal.accent);
            Drawf.dashRectBasic((x-regionSize*0.5f)*8f,(y-regionSize*0.5f)*8f,regionSize*8,regionSize*8);
        }
        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }
    }


}
