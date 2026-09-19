package mDimension.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.content;

public class LiquidUnloader extends Block {
    public TextureRegion centerRegion, topRegion,sideRegion;

    public float speed = 1;
    public boolean allowCoreUnload = true;
    public LiquidUnloader(String name){
        super(name);
        update = true;
        solid = false;
        hasLiquids = true;
        liquidCapacity = 100;
        configurable = true;
        outputsLiquid = true;
        saveConfig = true;
        noUpdateDisabled = true;
        displayFlow = false;
        group = BlockGroup.liquids;
        clearOnDoubleTap = true;
        rotate = true;

        config(Liquid.class, (LiquidUnloader.LiquidUnloaderBuild tile, Liquid l) -> tile.sortLiquid = l);
        configClear((LiquidUnloader.LiquidUnloaderBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void init() {
        super.init();
        centerRegion = Core.atlas.find(name+"-center");
        topRegion = Core.atlas.find(name+"-top");
        sideRegion = Core.atlas.find(name+"-side");
        uiIcon = Core.atlas.find(name+"-ui");
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
        drawPlanConfigCenter(plan, plan.config, name+"-center", false);
        Draw.rect(topRegion,plan.drawx(),plan.drawy(),plan.rotation*90f);
    }

    @Override
    public TextureRegion getDisplayIcon(Tile tile) {
        return super.getDisplayIcon(tile);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        super.drawPlanRegion(plan,list);
        float z = Draw.z();
        Draw.rect(sideRegion,plan.drawx(),plan.drawy(),(plan.rotation%2)*90);
        Draw.z(z);

    }

    public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        return region;
    }


    public class LiquidUnloaderBuild extends Building{
        public Liquid sortLiquid;

        @Override
        public void updateTile() {

            float amount = amount();

            Building back = back();
            if(sortLiquid != null&&back != null
            &&back.block.hasLiquids && back.liquids.get(sortLiquid)>0.1f && back.canUnload()
            && this.liquids.get(sortLiquid)<block.liquidCapacity-0.1f){
                pullLiquid(back,Math.min(back.liquids.get(sortLiquid) /2,amount),sortLiquid);


//                float minAmount = Math.min(block.liquidCapacity-this.liquids.get(sortLiquid),amount);
//                minAmount = Math.min(minAmount,back.liquids.get(sortLiquid));
//                back.liquids.remove(sortLiquid,minAmount);
//                this.handleLiquid(back,sortLiquid,minAmount);
            }

            dumpLiquid(liquids.current(),2,0);
        }
        public float amount(){
            return Time.delta  *  this.timeScale*this.efficiency * speed;
        }

        public void unloaderLiquid(){
            if(this.front() == null || !this.front().block.hasLiquids || this.front().liquids == null)return;
            if(sortLiquid!=null){
                if(this.liquids.get(sortLiquid)>0.01f)transferLiquid(this.front(),Math.min(this.liquids.get(sortLiquid),amount()),sortLiquid);
            }else if(liquids.current() !=null){
                if(this.liquids.get(liquids.current())>0.01f)transferLiquid(this.front(),Math.min(this.liquids.get(liquids.current()),amount()),liquids.current());
            }
        }

        public void pullLiquid(Building source, float amount, Liquid liquid) {
            float flow = Math.min(this.block.liquidCapacity - this.liquids.get(liquid), amount);
            if (flow>0) {
                this.handleLiquid(this, liquid, flow);
                source.liquids.remove(liquid, flow);
            }

        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return false;
        }


        @Override
        public void draw() {
            super.draw();
            float z = Draw.z();
            Draw.rect(sideRegion,x,y,(rotation%2)*90);
            if(sortLiquid != null){
                Draw.color(sortLiquid.color);
                Draw.rect(centerRegion,x,y,rotdeg());
            }
            Draw.z(z+0.01f);
            Draw.color(Color.white);
            Draw.rect(topRegion,x,y,rotdeg());
            Draw.reset();
            Draw.z(z);
        }

        @Override
        public void drawSelect(){
            if(sortLiquid == null)return;
            Building f = front();
            if(f!=null&&f.block.hasLiquids)drawInput(f,sortLiquid.color,false);
            Building b = back();
            if(b!=null&&b.block.hasLiquids)drawInput(b,sortLiquid.color,true);

            Draw.reset();
        }

        public void drawInput(Building other,Color color,boolean toThis){
            float pro = Time.time%60/60f;
            if(toThis)pro = 1-pro;
            float ox  =other.x;
            float oy = other.y;
            float cx = Mathf.lerp(x,other.x,pro);
            float cy = Mathf.lerp(y,other.y,pro);
            float ang = this.angleTo(other);
            Tmp.v2.trns(ang, 2f);
            Draw.color(Pal.gray);
            Lines.stroke(2.5f);
            Lines.square(ox, oy, 2f, 45f);
            Lines.stroke(2.5f);
            Lines.line(x + Tmp.v2.x, y + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);
            Fill.circle(cx,cy,1.2f * 1.6f);
            Draw.color(color);
            Draw.alpha(1f);
            Lines.stroke(1f);
            Lines.line(x + Tmp.v2.x, y + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            Lines.square(ox, oy, 2f, 45f);

            Fill.circle(cx,cy,1.2f);
            Draw.reset();
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(LiquidUnloader.this, table, content.liquids(), () -> sortLiquid, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Liquid config(){
            return sortLiquid;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortLiquid == null?-1:sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = read.s();
            sortLiquid = id == -1?  null  :content.liquid(id);
        }
    }
}
