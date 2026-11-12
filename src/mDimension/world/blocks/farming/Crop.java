package mDimension.world.blocks.farming;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Interval;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mDimension.world.type.SeedItem;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.BlockRenderer;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.LiquidModule;
import mindustry.world.modules.PowerModule;

import static mindustry.Vars.world;

public class Crop extends Block {
    public int stageAmount = 3;
    public static final Rand rand = new Rand();
    public float growthTime = 30*60f;
    public float minSize = 0.5f,maxSize = 1.2f;
    public TextureRegion[] stage;
    public TextureRegion botRegion,centerRegion,fruitRegion;
    public ItemStack[] outputItems;
    public LiquidStack[] outputLiquids;
    public Color fromColor = Color.valueOf("CAFFA8"),toColor = Color.white;
    public int seedingRequirements = 1;
    public Item seed;

    public int adultLobesMin = 6, adultLobesMax = 7;
    public int larvaLobesMin = 2, larvaLobesMax = 3;

    public float minBotAngle = 60,maxBotAngle = 80, origin = 0.1f;
    public float sclMin = 38f, sclMax = 55f, magMin = 3.5f, magMax = 8f, timeRange = 40f, spread = 0f;
    public float fMagMin = 0.1f,fMagMax = 0.3f,fruitThreshold = 0.60f;

    public Crop(String name, Item seed) {
        super(name);
        placeablePlayer = false;
        update = true;
        destructible = false;
        breakable = true;
        alwaysReplace = true;
        buildTime = 0;
        unitMoveBreakable = true;
        destroyEffect = breakEffect = Fx.breakProp;
        destroySound = breakSound = Sounds.plantBreak;
        hasColor = true;
        noUpdateDisabled = false;
        canOverdrive = false;
        rebuildable = false;
        logicConfigurable = false;
        drawDisabled = false;
        customShadow = true;
        allowDerelictRepair = false;
        drawTeamOverlay = false;
        SeedItem.map.put(this.seed = seed,this);
        requirements(Category.production, BuildVisibility.editorOnly,ItemStack.with());
    }


    @Override
    public int minimapColor(Tile tile) {
        return this.mapColor.rgba();
    }

    @Override
    public void load() {
        super.load();
        stage = new TextureRegion[stageAmount];
        for(int i=0;i<stageAmount;i++){
            Core.atlas.find(name+i);
        }
        fruitRegion = Core.atlas.find(name+"-fruit");
        botRegion = Core.atlas.find(name + "-bot");
        centerRegion = Core.atlas.find(name + "-center");
        customShadowRegion = Core.atlas.find("circle-shadow");
    }


    public class CorpBuild extends Building{
        public float life = 0;
        public int tryHarvestingBuildPos = -1;
        public float growthProgress(){
            return Mathf.clamp(life/growthTime);
        }

        public boolean adult(){return life>growthTime;}

        @Override
        public void unitRemoved(Unit unit) {
            super.unitRemoved(unit);
        }

        public Building create(Block block, Team team) {
            this.block = block;
            this.team = Team.derelict;
            this.health = (float)block.health;
            this.maxHealth((float)block.health);
            this.timer(new Interval(block.timers));
            if (block.hasItems) {
                this.items = new ItemModule();
            }

            if (block.hasLiquids) {
                this.liquids = new LiquidModule();
            }

            if (block.hasPower) {
                this.power = new PowerModule();
                this.power.graph.add(this);
            }

            this.initialized = true;
            return this;
        }
        @Override
        public void update() {
            if ((this.timeScaleDuration -= Time.delta) <= 0.0F) {
                this.timeScale = 1.0F;
            }
            if(tryHarvestingBuildPos!=-1){
                var b = world.build(tryHarvestingBuildPos);
                if(!(b instanceof HarvestingBlock.HarvestingBlockBuild h) || h.target!=this.tile)tryHarvestingBuildPos = -1;
            }

            this.updateConsumption();
            if (this.enabled || !this.block.noUpdateDisabled) {
                this.updateTile();
            }
            if(!isPayload() && life<growthTime+1)life+=delta();
        }

        @Override
        public boolean displayable() {
            return false;
        }

        public void harvest(@Nullable Building other){
            if (other!=null) {
                if(outputLiquids!=null && other.block.hasLiquids){
                    for(var liquids:outputLiquids){
                        other.liquids.add(liquids.liquid,liquids.amount);
                    }
                }
                if(outputItems!=null && other.block.hasItems){
                    for(var items:outputItems){
                        other.items.add(items.item,items.amount);
                    }
                }
            }
            block.breakSound.at(tile, block.breakPitchChange ? Mathf.random(0.7f, 1.3f) : 0.4f);
            block.breakEffect.at(tile.drawx(), tile.drawy(), block.size, block.mapColor);
            Call.setTile(this.tile,Blocks.air,Team.derelict,0);
        }

        @Override
        public void draw(){
            rand.setSeed(tile.pos());
            float offset = rand.random(180f);
            int mlobes = rand.random(adultLobesMin, adultLobesMax);
            float lobes = Mathf.lerp(rand.random(larvaLobesMin, larvaLobesMax),mlobes,growthProgress());
            float scl = Mathf.lerp(minSize,maxSize,growthProgress());
            Draw.z(Layer.blockProp+0.1f);
            drawFruit();
            Draw.reset();
            Draw.z(Layer.blockProp);
            for(int i = 0; i < lobes; i++) {
                float ba = i / lobes * 360f + offset + rand.range(spread),
                        angle = ba + Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax), rand.random(magMin, magMax));
                float w = region.width * region.scl()*scl, h = region.height * region.scl()*scl;
                var region = Angles.angleDist(angle, 225f) <= minBotAngle ? botRegion : Crop.this.region;

                Draw.rect(region,
                        tile.worldx() - Angles.trnsx(angle, origin) + w * 0.5f, tile.worldy() - Angles.trnsy(angle, origin),
                        w, h,
                        origin * 4f, h / 2f,
                        angle
                );

                if(region == Crop.this.region) {
                    float botLerp = 1f-Mathf.clamp((Angles.angleDist(angle, 225f) - minBotAngle) / (maxBotAngle - minBotAngle));
                    Draw.alpha(botLerp);
                    Draw.rect(botRegion,
                            tile.worldx() - Angles.trnsx(angle, origin) + w * 0.5f, tile.worldy() - Angles.trnsy(angle, origin),
                            w, h,
                            origin * 4f, h / 2f,
                            angle
                    );
                    Draw.alpha(1f);
                }
            }
            Draw.reset();
            if(centerRegion.found()){
                Draw.rect(centerRegion, tile.worldx(), tile.worldy());
            }


        }

        public void drawFruit() {
            if(growthProgress() < fruitThreshold)return;
            float scl =  ((growthProgress() - fruitThreshold)/(1f-fruitThreshold)) * 0.25f * rand.random(1f,1.12f);
            Draw.color(fromColor,toColor,(growthProgress() - fruitThreshold)/(1f-fruitThreshold));
            float mx = rand.random(fMagMin,fMagMax);
            float my = rand.random(fMagMin,fMagMax);
            float dx = Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax),mx);
            float dy = Mathf.sin(Time.time + rand.random(0, timeRange), rand.random(sclMin, sclMax),my);
            Draw.rect(fruitRegion,x+dx,y+dy,fruitRegion.width*scl,fruitRegion.height*scl);
        }
        @Override
        public void drawSelect() {
            super.drawSelect();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(life);
            write.i(tryHarvestingBuildPos);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            life = read.f();
            tryHarvestingBuildPos = read.i();
        }
    }

    @Override
    public void drawShadow(Tile tile) {
        if(tile.build instanceof CorpBuild corpBuild){
            float w = Mathf.lerp(minSize,maxSize,corpBuild.growthProgress())*14f;
            Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a * (corpBuild.growthProgress()*0.5f+0.5f));

            Draw.rect(
                    customShadowRegion,
                    tile.drawx(), tile.drawy(),w,w,0);
            Draw.color();
        }
    }
}
