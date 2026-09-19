package mDimension.world.blocks;


import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import mDimension.tool.MD_Edge;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class RadiusItemBridge extends ItemBridge {
    public int maxLinks = 3;

    private static BuildPlan otherReq;
    public TextureRegion top;
    public void init(){
        super.init();

        top = Core.atlas.find(name+"-top");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range,range);
        stats.add(Stat.powerConnections,maxLinks);
    }

    public RadiusItemBridge(String name){
        super(name);
        allowDiagonal = true;;
        //point2 config is relative
        configurations.clear();
        config(Point2.class, (RadiusItemBridgeBuild tile, Point2 i) -> {
            tile.link = Point2.pack(i.x + tile.tileX(), i.y + tile.tileY());
        });
        //integer is not
        config(Integer.class, (RadiusItemBridgeBuild tile, Integer i) -> tile.link = i);
    }

    @Override
    public boolean positionsValid(int x1, int y1, int x2, int y2){
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        // 使用欧几里得距离计算半径
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    @Override
    public boolean positionsValid(int x1, int y1, int x2, int y2,int range){
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        // 使用欧几里得距离计算半径
        return Math.sqrt(dx * dx + dy * dy) <= range;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        Tile link = findLink(x, y);

        // 绘制圆形范围而不是十字范围
        Draw.reset();
        Draw.color(Pal.placing);
        Lines.stroke(1f);
        Drawf.dashCircle(x * tilesize, y * tilesize, range * tilesize,Pal.placing);

        if(link != null){
            float distance = Mathf.dst(x, y, link.x, link.y);
            if(distance > 1.1f){
                int rot = link.absoluteRelativeTo(x, y);
                float w = (link.x == x ? tilesize : Math.abs(link.x - x) * tilesize - tilesize);
                float h = (link.y == y ? tilesize : Math.abs(link.y - y) * tilesize - tilesize);
                Draw.color(Pal.placing);
                Lines.stroke(1f);
                Lines.line(x*8,y*8,link.x*8,link.y*8);
                Draw.rect("bridge-arrow", (link.x+x)*4f,(link.y+y)*4f,Mathf.angle(link.x-x,link.y-y)+180);
           }
        }
        Draw.reset();
    }

    @Override
    public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list){
        super.drawPlanConfigTop(plan,list);
        //Draw.rect(top,plan.drawx(),plan.drawy());

    }

    public void drawBridge(BuildPlan req, float ox, float oy, float flip){
        if(Mathf.zero(Renderer.bridgeOpacity)) return;
        Draw.alpha(Renderer.bridgeOpacity);

        Lines.stroke(bridgeWidth);

        Tmp.v1.set(ox, oy).sub(req.drawx(), req.drawy()).setLength(tilesize*0.3f);

        Lines.line(
                bridgeRegion,
                req.drawx()+Tmp.v1.x,
                req.drawy()+Tmp.v1.y,
                ox-Tmp.v1.x,
                oy-Tmp.v1.y, false
        );

        Draw.rect(arrowRegion, (req.drawx() + ox) / 2f, (req.drawy() + oy) / 2f,
                Angles.angle(req.drawx(), req.drawy(), ox, oy) + flip);

        Draw.reset();
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        for(int i = 0; i < plans.size - 1; i++){
            var cur = plans.get(i);
            var next = plans.get(i + 1);
           if(positionsValid(cur.x, cur.y, next.x, next.y)){
                cur.config = new Point2(next.x - cur.x, next.y - cur.y);
            }
        }
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> {
            int dx = Math.abs(point.x - other.x);
            int dy = Math.abs(point.y - other.y);
            return Math.sqrt(dx * dx + dy * dy) <= range;
        });
    }

    public class RadiusItemBridgeBuild extends ItemBridgeBuild{
        public int links = 0;
        public int cacheLinks = 0;

        @Override
        public boolean acceptItem(Building source, Item item) {
            return hasItems && team == source.team && items.total() < itemCapacity && checkAccept(source, world.tile(link));
        }



        @Override
        protected boolean checkAccept(Building source, Tile link){
            if(source instanceof ItemBridgeBuild bridge){
                if(tile == world.tile(bridge.link))return true;
            }
            int[] rels = MD_Edge.isInDiagonal(this,link);
            if(rels[0]>=0){
                var facing = Edges.getFacingEdge(source, this);
                int rel = facing == null ? -1 : relativeTo(facing);
                return rel == rels[0] || rel == rels[1];
            }
            return super.checkAccept(source,link);
        }

        @Override
        public void updateTile() {
            super.updateTile();
        }

        @Override
        public void updateTransport(Building other) {
            transportCounter += edelta();
            while(transportCounter >= transportTime){
                Item item = items.take();
                if(item != null && other.acceptItem(this,item)){
                    other.handleItem(this, item);
                    moved = true;
                }else if(item != null){
                    items.add(item, 1);
                    items.undoFlow(item);
                }
                transportCounter -= transportTime;
            }
        }

        @Override
        public void doDump() {
            super.doDump();
        }

        @Override
        protected boolean checkDump(Building to){


            Tile other = world.tile(link);
            if(!linkValid(tile, other)){
                Tile edge = Edges.getFacingEdge(to.tile, tile);
                int i = relativeTo(edge.x, edge.y);

                for(int j = 0; j < incoming.size; j++){
                    int v = incoming.items[j];
                    Tile sourceBridge = world.tile(v);
                    int[] rels = MD_Edge.isInDiagonal(this,sourceBridge);
                    if(rels[0]>=0){
                        if(i != rels[0] && i != rels[1]){
                            return false;
                        }
                    }else if(relativeTo(Point2.x(v), Point2.y(v)) == i){
                        return false;
                    }
                }
                return true;
            }

            int rel = relativeTo(other.x, other.y);
            int rel2 = relativeTo(to.tileX(), to.tileY());

            return rel != rel2;
        }

        @Override
        public void draw(){
            Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            Draw.z(Layer.power+0.01f);
            Draw.rect(top,x,y);
            Draw.z(Layer.power);

            Tile other = world.tile(link);
            if(!linkValid(tile, other)) return;

            if(Mathf.zero(Renderer.bridgeOpacity)) return;

            int i = relativeTo(other.x, other.y);

            if(pulse){
                Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f));
            }

            float warmup = hasPower ? this.warmup : 1f;

            Draw.alpha((fadeIn ? Math.max(warmup, 0.25f) : 1f) * Renderer.bridgeOpacity);

            //Draw.rect(endRegion, x, y, i * 90 + 90);
            //Draw.rect(endRegion, other.drawx(), other.drawy(), i * 90 + 270);

            Lines.stroke(bridgeWidth);

            //Tmp.v1.set(x, y).sub(other.worldx(), other.worldy()).setLength(tilesize/2f).scl(-1f);

            Lines.line(bridgeRegion,
                    x,
                    y,
                    other.worldx(),
                    other.worldy(), false);

            int dist = Math.max(Math.abs(other.x - tile.x), Math.abs(other.y - tile.y)) - 1;

            Draw.color();

            int arrows = (int)(Mathf.len(other.worldx()-x,other.worldy()-y)/ arrowSpacing)-1;
            float arrowAngle = Mathf.angle(other.worldx()-x,other.worldy()-y);
            float dx = Mathf.cos(arrowAngle * Mathf.degreesToRadians), dy = Mathf.sin(arrowAngle * Mathf.degreesToRadians);

            for(int a = 0; a < arrows; a++){
                Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity);
                Draw.rect(arrowRegion,
                        x + dx * (tilesize / 2f + a * arrowSpacing + arrowOffset),
                        y + dy * (tilesize / 2f + a * arrowSpacing + arrowOffset),
                        arrowAngle);
            }
            Draw.reset();

        }

        @Override
        public void drawSelect(){
            if(linkValid(tile, world.tile(link))){
                drawInput(world.tile(link));
            }

            incoming.each(pos -> drawInput(world.tile(pos)));

            Draw.reset();
        }
        private void drawInput(Tile other){
            if(!linkValid(tile, other, false)) return;
            boolean linked = other.pos() == link;

            Tmp.v2.trns(tile.angleTo(other), 2f);
            float tx = tile.drawx(), ty = tile.drawy();
            float ox = other.drawx(), oy = other.drawy();
            float alpha = Math.abs((linked ? 100 : 0)-(Time.time * 2f) % 100f) / 100f;
            float x = Mathf.lerp(ox, tx, alpha);
            float y = Mathf.lerp(oy, ty, alpha);

            Tile otherLink = linked ? other : tile;
            int rel = (linked ? tile : other).absoluteRelativeTo(otherLink.x, otherLink.y);

            //draw "background"
            Draw.color(Pal.gray);
            Lines.stroke(2.5f);
            Lines.square(ox, oy, 2f, 45f);
            Lines.stroke(2.5f);
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            float color = (linked ? Pal.place : Pal.accent).toFloatBits();

            //draw foreground colors
            Draw.color(color);
            Lines.stroke(1f);
            Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);

            Lines.square(ox, oy, 2f, 45f);
            Draw.mixcol(color);
            Draw.color();
            Draw.rect("bridge-arrow", x, y, Mathf.angle(tx-x,ty-y) + (linked?180:0));
            Draw.mixcol();
        }

        @Override
        public void drawConfigure(){
            Drawf.select(x, y, tile.block().size * tilesize / 2f + 2f, Pal.accent);
            Drawf.dashCircle(x, y, range * tilesize,Pal.placing);
        }
    }
}