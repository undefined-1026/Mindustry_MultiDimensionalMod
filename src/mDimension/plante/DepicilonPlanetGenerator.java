package mDimension.plante;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.*;
import arc.util.Structs;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mDimension.content.*;
import mindustry.ai.Astar;
import mindustry.ai.BaseRegistry;
import mindustry.content.*;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.game.Waves;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;

import static mindustry.Vars.*;
import static mindustry.Vars.bases;
import static mindustry.Vars.spawner;

public class DepicilonPlanetGenerator extends PlanetGenerator {
    public Color EmissiveColor = Color.valueOf("fffaa0");

    {
        seed = 1145;
        baseSeed = 114;
        defaultLoadout = MD_Loadouts.basicDepicilon;
    }

    //alternate, less direct generation
    public static boolean indirectPaths = false;
    //random water patches
    public static boolean genLakes = false;

    BaseGenerator basegen = new BaseGenerator();
    float heightYOffset = 42.7f;
    float scl = 5f;
    float waterOffset = 0.04f;
    float heightScl = 1.01f;



    Block[][] arr =
            {
                    {MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.crystallization_oil, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone},
                    {MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.crystallization_oil, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone},
                    {MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.darkRedStone, MD_environment.pure_light_shale_floor, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.crystallization_oil, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone},
                    {MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkRedStone, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.darkRedStone, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone, MD_environment.magnetic_shale_stone, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice_snow, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil_deep, MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkRedStone, MD_environment.pure_light_shale_floor, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkCrystallineStone, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil_deep, MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.chlorite, MD_environment.yellow_ice_snow, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil_deep, MD_environment.crystallization_oil, MD_environment.darkRedStone, MD_environment.darkRedStone, MD_environment.chlorite, MD_environment.chlorite, MD_environment.pure_light_shale_floor, MD_environment.darkCrystallineStone, MD_environment.darkCrystallineStone, MD_environment.darkCrystallineStone, MD_environment.yellow_ice, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil_deep, MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.darkCrystallineStone, MD_environment.chlorite, MD_environment.darkCrystallineStone, MD_environment.light_shale_floor, MD_environment.darkCrystallineStone, MD_environment.yellow_ice, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.chlorite, MD_environment.germaniumStone, MD_environment.pure_light_shale_floor, MD_environment.darkCrystallineStone, MD_environment.darkCrystallineStone, MD_environment.yellow_ice, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.darkDenseRedStone, MD_environment.germaniumStone, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.pure_light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil_deep, MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.germaniumStone, MD_environment.germaniumStone, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.pure_light_shale_floor, MD_environment.light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil, MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.germaniumStone, MD_environment.chlorite, MD_environment.germaniumStone, MD_environment.yellow_ice_snow, MD_environment.light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice},
                    {MD_environment.crystallization_oil, MD_environment.darkDenseRedStone, MD_environment.light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice_snow, MD_environment.light_shale_floor, MD_environment.light_shale_floor, MD_environment.light_shale_floor, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice, MD_environment.yellow_ice}
            };

    ObjectMap<Block, Block> dec = ObjectMap.of(
            Blocks.sporeMoss, Blocks.sporeCluster,
            Blocks.moss, Blocks.sporeCluster,
            Blocks.taintedWater, MD_environment.crystallization_oil_deep,
            MD_environment.crystallization_oil, Blocks.darksandWater
    );

    ObjectMap<Block, Block> tars = ObjectMap.of(
            Blocks.sporeMoss, Blocks.shale,
            Blocks.moss, Blocks.shale
    );

    float water = 2f / arr[0].length;
    Vec3 basePos = new Vec3(0.9341721, 0.0, 0.3568221);





    public boolean isEmissive(){
        return true;
    }

    @Override
    public float getHeight(Vec3 position){
        float height = rawHeight(position);
        if (height>0.72f){
            height += 0.3f;
        }else if (height > 0.68f) {
            height += 0.15f;
        }else if (height > 0.58f) {
            height -= 0.1f;
        }else if (height <0.42f){
            height -=0.2f;
        }
        return height * 0.5f;
    }

    @Override
    public void getColor(Vec3 position, Color out){
        float height = rawHeight(position);
        float latit = getAbsLatitude(position);
        latit = (float)Math.pow(latit,0.7f);
        Color toC = Color.valueOf("FFFFE8");
        Color c = Color.valueOf("524727");
        if(height>0.72f){
            c.lerp(toC,0.8f*latit);
        }else if(height>0.68f){
            c.lerp(toC,0.55f*latit);
        }else if(height>0.58f){
            c.lerp(toC,0.35f*latit);
        }else if(height<0.42f){
            c.lerp(toC,0.2f*latit);
        }
        out.set(c);
    }

    @Override
    public void getEmissiveColor(Vec3 position, Color out) {
        float height = rawHeight(position);
        if(height<0.4f){
            out.set(EmissiveColor);
            out.lerp(Color.clear,Math.max(0,(height-0.25f)*2.66F));
        }
    }

    int count =0;
    float amount;

    float getLatitude(Vec3 v){
        return (float) (Math.asin(v.y)/(Math.PI/2));
    }
    float getAbsLatitude(Vec3 v){
        return (float) (Math.abs(Math.asin(v.y)/(Math.PI/2)));
    }

    float rawHeight(Vec3 position) {
        float height = Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x*scl, position.y*scl + heightYOffset, position.z*scl) * heightScl;
        count++;
        amount+=height;
        Items.lead.description = ""+(amount)/count;
        return height;
    }


    @Override
    public float getSizeScl(){
        return 2000 * 1.07f * 6f / 5f;
    }

//    float rawTemp(Vec3 position){
//        return position.dst(0,0,1)*2.2f - Simplex.noise3d(seed, 8, 0.54f, 1.4f, 10+position.x, 10+position.y, 10+position.z)*2.9f;
//    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        if(tile.floor == Blocks.darkPanel6) tile.floor = Blocks.darkPanel3;


        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
            tile.block = Blocks.air;
        }else{
            tile.block = tile.floor.asFloor().wall;
        }
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);
        float px = position.x * scl, py = position.y * scl, pz = position.z * scl;

        float rad = scl;
        float temp = Mathf.clamp(Math.abs(py * 2f) / (rad));
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, px, py + 999f - 0.1f, pz);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        float tar = Simplex.noise3d(seed, 4, 0.55f, 1f/2f, px, py + 999f, pz) * 0.3f + position.dst(0, 0, 1f) * 0.2f;

        Block res = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if(tar > 0.5f){
            return tars.get(res, res);
        }else{
            if(position.within(basePos, 0.65f)){

                float dst = 999f;

                Object[] sectors = Planets.serpulo.sectors.items;
                int size = Planets.serpulo.sectors.size;

                for(int i = 0; i < size; i ++){
                    var sector = (Sector)sectors[i];

                    if(sector.hasEnemyBase()){
                        dst = Math.min(dst, position.dst(sector.tile.v));
                    }
                }

                float freq = 0.05f, freq2 = 0.15f;

                if(dst*0.85f + Simplex.noise3d(seed, 3, 0.4, 5.5f, position.x, position.y + 200f, position.z)*0.015f + ((basePos.dst(position) + 0.00f) % freq < freq/2f ? 1f : 0f) * 0.07f < 0.15f){
                    return ((basePos.dst(position) + 0.01f) % freq2 < freq2*0.65f) ? Blocks.metalFloor : Blocks.darkPanel6;
                }
            }
            return res;
        }
    }

    static double metalDstScl = 0.25;

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    //while

    @Override
    protected void generate(){

        class Room{
            int x, y, radius;
            ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius){
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2){
                float nscl = rand.random(100f, 140f) * 6f;
                int stroke = rand.random(3, 9);
                brush(pathfind(x1, y1, x2, y2, tile -> (tile.solid() ? 50f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan), stroke);
            }

            void connect(Room to){
                if(!connected.add(to) || to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                if(indirectPaths){
                    midpoint.add(Tmp.v2.set(1, 0f).setAngle(Angles.angle(to.x, to.y, x, y) + 90f * (rand.chance(0.5) ? 1f : -1f)).scl(Tmp.v1.dst(x, y) * 2f));
                }else{
                    //add randomized offset to avoid straight lines
                    midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                }

                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);

                int mx = (int)midpoint.x, my = (int)midpoint.y;

                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }

            void joinLiquid(int x1, int y1, int x2, int y2){
                float nscl = rand.random(100f, 140f) * 6f;
                int rad = rand.random(7, 11);
                int avoid = 2 + rad;
                var path = pathfind(x1, y1, x2, y2, tile -> (tile.solid() || !tile.floor().isLiquid ? 70f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan);
                path.each(t -> {
                    //don't place liquid paths near the core
                    if(Mathf.dst2(t.x, t.y, x2, y2) <= avoid * avoid){
                        return;
                    }

                    for(int x = -rad; x <= rad; x++){
                        for(int y = -rad; y <= rad; y++){
                            int wx = t.x + x, wy = t.y + y;
                            if(Structs.inBounds(wx, wy, width, height) && Mathf.within(x, y, rad)){
                                Tile other = tiles.getn(wx, wy);
                                other.setBlock(Blocks.air);
                                if(Mathf.within(x, y, rad - 1) && !other.floor().isLiquid){
                                    Floor floor = other.floor();
                                    //TODO does not respect tainted floors
                                    other.setFloor((Floor)(floor == Blocks.sand || floor == Blocks.salt ? MD_environment.crystallization_oil : MD_environment.crystallization_oil));
                                }
                            }
                        }
                    }
                });
            }

            void connectLiquid(Room to){
                if(to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                //add randomized offset to avoid straight lines
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);

                int mx = (int)midpoint.x, my = (int)midpoint.y;

                joinLiquid(x, y, mx, my);
                joinLiquid(mx, my, to.x, to.y);
            }
        }


        cells(4);
        distort(10f, 12f);

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
        Seq<Room> roomseq = new Seq<>();

        for(int i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint));
            float rx = (width/2f + Tmp.v1.x);
            float ry = (height/2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max((int)(sector.threat * 4), 1));
        int offset = rand.nextInt(360);
        float length = width/2.55f - rand.random(13, 23);
        int angleStep = 5;
        int waterCheckRad = 5;
        for(int i = 0; i < 360; i+= angleStep){
            int angle = offset + i;
            int cx = (int)(width/2 + Angles.trnsx(angle, length));
            int cy = (int)(height/2 + Angles.trnsy(angle, length));

            int waterTiles = 0;

            //check for water presence
            for(int rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(int ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    Tile tile = tiles.get(cx + rx, cy + ry);
                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles ++;
                    }
                }
            }

            if(waterTiles <= 4 || (i + angleStep >= 360)){
                roomseq.add(spawn = new Room(cx, cy, rand.random(8, 15)));

                for(int j = 0; j < enemySpawns; j++){
                    float enemyOffset = rand.range(60f);
                    Tmp.v1.set(cx - width/2, cy - height/2).rotate(180f + enemyOffset).add(width/2, height/2);
                    Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(8, 16));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        //clear radius around each room
        for(Room room : roomseq){
            erase(room.x, room.y, room.radius);
        }

        //randomly connect rooms together
        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(int i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for(Room room : roomseq){
            spawn.connect(room);
        }

        Room fspawn = spawn;

        cells(1);

        int tlen = tiles.width * tiles.height;
        int total = 0, waters = 0;

        for(int i = 0; i < tlen; i++){
            Tile tile = tiles.geti(i);
            if(tile.block() == Blocks.air){
                total ++;
                if(tile.floor().liquidDrop == Liquids.water){
                    waters ++;
                }
            }
        }

        boolean naval = (float)waters / total >= 0.19f;

        //create water pathway if the map is flooded
        if(naval){
            for(Room room : enemies){
                room.connectLiquid(spawn);
            }
        }

        distort(10f, 6f);

        //rivers
        pass((x, y) -> {
            if(block.solid) return;

            Vec3 v = sector.rect.project(x, y);

            float rr = Simplex.noise2d(sector.id, (float)2, 0.6f, 1f / 7f, x, y) * 0.1f;
            float value = Ridged.noise3d(2, v.x, v.y, v.z, 1, 1f / 55f) + rr - rawHeight(v) * 0f;
            float rrscl = rr * 44 - 2;

            if(value > 0.17f && !Mathf.within(x, y, fspawn.x, fspawn.y, 12 + rrscl)){
                boolean deep = value > 0.17f + 0.1f && !Mathf.within(x, y, fspawn.x, fspawn.y, 15 + rrscl);
                boolean spore = floor != Blocks.sand && floor != Blocks.salt;
                //do not place rivers on ice, they're frozen
                //ignore pre-existing liquids
                if(!(floor == MD_environment.yellow_ice || floor == MD_environment.yellow_ice_snow || floor == Blocks.snow || floor.asFloor().isLiquid)){
                    floor = spore ?
                            (deep ? MD_environment.crystallization_oil_deep : MD_environment.crystallization_oil) :
                            (deep ? MD_environment.crystallization_oil_deep :
                                    (floor == Blocks.sand || floor == Blocks.salt ? MD_environment.crystallization_oil : MD_environment.crystallization_oil));
                }
            }
        });

        //shoreline setup
        pass((x, y) -> {
            int deepRadius = 3;

            if(floor.asFloor().isLiquid && floor.asFloor().shallow){

                for(int cx = -deepRadius; cx <= deepRadius; cx++){
                    for(int cy = -deepRadius; cy <= deepRadius; cy++){
                        if((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius){
                            int wx = cx + x, wy = cy + y;

                            Tile tile = tiles.get(wx, wy);
                            if(tile != null && (!tile.floor().isLiquid || tile.block() != Blocks.air)){
                                //found something solid, skip replacing anything
                                return;
                            }
                        }
                    }
                }

                floor = floor == MD_environment.crystallization_oil ? MD_environment.crystallization_oil: MD_environment.crystallization_oil_deep;
            }
        });

        if(naval){
            int deepRadius = 2;

            //TODO code is very similar, but annoying to extract into a separate function
            pass((x, y) -> {
                if(floor.asFloor().isLiquid && !floor.asFloor().isDeep() && !floor.asFloor().shallow){

                    for(int cx = -deepRadius; cx <= deepRadius; cx++){
                        for(int cy = -deepRadius; cy <= deepRadius; cy++){
                            if((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius){
                                int wx = cx + x, wy = cy + y;

                                Tile tile = tiles.get(wx, wy);
                                if(tile != null && (tile.floor().shallow || !tile.floor().isLiquid)){
                                    //found something shallow, skip replacing anything
                                    return;
                                }
                            }
                        }
                    }

                    floor = floor == MD_environment.crystallization_oil_deep ? Blocks.deepwater : Blocks.taintedWater;
                }
            });
        }


        trimDark();

        median(2);



        //wang ge
        tech(37, MD_environment.inlayMetalTiles4,Blocks.metalTiles8, MD_environment.inlayMetalTiles4.asFloor().wall);

        pass((x, y) -> {
            //random moss
            if(floor == Blocks.sporeMoss){
                if(Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 65)) > 0.02){
                    floor = Blocks.moss;
                }
            }

            //tar
            if(floor == Blocks.darksand){
                if(Math.abs(0.5f - noise(x - 40, y, 2, 0.7, 80)) > 0.25f &&
                        Math.abs(0.5f - noise(x, y + sector.id*10, 1, 1, 60)) > 0.41f && !(roomseq.contains(r -> Mathf.within(x, y, r.x, r.y, 30)))){
                    floor = Blocks.tar;
                }
            }

            //hotrock tweaks
            if(floor == Blocks.hotrock){
                if(Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 80)) > 0.035){
                    floor = MD_environment.darkCrystalFloor;
                }else{
                    ore = Blocks.air;
                    boolean all = true;
                    for(Point2 p : Geometry.d4){
                        Tile other = tiles.get(x + p.x, y + p.y);
                        if(other == null || (other.floor() != Blocks.hotrock && other.floor() != Blocks.magmarock)){
                            all = false;
                        }
                    }
                    if(all){
                        floor = Blocks.magmarock;
                    }
                }
            }else if(genLakes && floor != MD_environment.darkCrystalFloor && floor != MD_environment.yellow_ice && floor.asFloor().hasSurface()){
                float noise = noise(x + 782, y, 5, 0.75f, 260f, 1f);
                if(noise > 0.67f && !roomseq.contains(e -> Mathf.within(x, y, e.x, e.y, 14))){
                    if(noise > 0.72f){
                        floor = noise > 0.78f ? Blocks.taintedWater : (floor == Blocks.sand ? MD_environment.crystallization_oil : MD_environment.crystallization_oil);
                    }else{
                        floor = (floor == Blocks.sand ? floor : Blocks.darksand);
                    }
                }
            }
//tree
//            if(rand.chance(0.0075)){
//                //random spore trees
//                boolean any = false;
//                boolean all = true;
//                for(Point2 p : Geometry.d4){
//                    Tile other = tiles.get(x + p.x, y + p.y);
//                    if(other != null && other.block() == Blocks.air){
//                        any = true;
//                    }else{
//                        all = false;
//                    }
//                }
//                if(any && ((block == Blocks.snowWall || block == md_environment.yellow_iceWall) || (all && block == Blocks.air && floor == Blocks.snow && rand.chance(0.03)))){
//                    block = rand.chance(0.5) ? Blocks.whiteTree : Blocks.whiteTreeDead;
//                }
//            }

        });

        float difficulty = sector.threat;
        int ruinCount = rand.random(-2, 4);

        if(ruinCount > 0){
            IntSeq ints = new IntSeq(width * height / 4);

            int padding = 25;

            //create list of potential positions
            for(int x = padding; x < width - padding; x++){
                for(int y = padding; y < height - padding; y++){
                    Tile tile = tiles.getn(x, y);
                    if(!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)){
                        ints.add(tile.pos());
                    }
                }
            }

            ints.shuffle(rand);

            int placed = 0;
            float diffRange = 0.4f;
            //try each position
            for(int i = 0; i < ints.size && placed < ruinCount; i++){
                int val = ints.items[i];
                int x = Point2.x(val), y = Point2.y(val);

                //do not overwrite player spawn
                if(Mathf.within(x, y, spawn.x, spawn.y, 18f)){
                    continue;
                }

                float range = difficulty + rand.random(diffRange);

                Tile tile = tiles.getn(x, y);
                BaseRegistry.BasePart part = null;
                if(tile.overlay().itemDrop != null){
                    part = bases.forResource(tile.drop()).getFrac(range);
                }else if(tile.floor().liquidDrop != null && rand.chance(0.05)){
                    part = bases.forResource(tile.floor().liquidDrop).getFrac(range);
                }else if(rand.chance(0.05)){ //ore-less parts are less likely to occur.
                    part = bases.parts.getFrac(range);
                }

                //actually place the part
                if(part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, rand, (cx, cy) -> {
                    Tile other = tiles.getn(cx, cy);
                    if(other.floor().hasSurface()){
                        other.setOverlay(Blocks.oreScrap);
                        for(int j = 1; j <= 2; j++){
                            for(Point2 p : Geometry.d8){
                                Tile t = tiles.get(cx + p.x*j, cy + p.y*j);
                                if(t != null && t.floor().hasSurface() && rand.chance(j == 1 ? 0.4 : 0.2)){
                                    t.setOverlay(Blocks.oreScrap);
                                }
                            }
                        }
                    }
                })){
                    placed ++;

                    int debrisRadius = Math.max(part.schematic.width, part.schematic.height)/2 + 3;
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius, (cx, cy) -> {
                        float dst = Mathf.dst(cx, cy, x, y);
                        float removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius);

                        Tile other = tiles.getn(cx, cy);
                        if(other.build != null && other.isCenter()){
                            if(other.team() == Team.derelict && rand.chance(removeChance)){
                                other.remove();
                            }else if(rand.chance(0.5)){
                                other.build.health = other.build.health - rand.random(other.build.health * 0.9f);
                            }
                        }
                    });
                }
            }
        }
        inverseFloodFill(tiles.getn(spawn.x, spawn.y));

        Seq<OreBlock> ores = Seq.with((OreBlock) MD_environment.oreAluminium,(OreBlock)MD_environment.oreGraphite,(OreBlock) MD_environment.oreGermanium);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1f;
        float addscl = 1.3f;

        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.25f*addscl){
            ores.add((OreBlock)MD_environment.WallOreTitanium);
        }

        //218 doesn't have thorium generation due to proximity (TODO remove the special case and replace with hidden preset)
        if(Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 2, sector.tile.v.y, sector.tile.v.z)*nmag + poles > 0.7f*addscl && sector.id != 218){
            ores.add((OreBlock)MD_environment.oreChromium);
        }

//        if(rand.chance(0.25)){
//            ores.add((OreBlock)Blocks.oreScrap);
//        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.03f);
        }

        pass((x, y) -> {
            if(!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for(int i = ores.size - 1; i >= 0; i--){
                OreBlock entry = ores.get(i);
                float freq = frequencies.get(i);
                if(!entry.wallOre) {
                    if (Math.abs(0.5f - noise(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22f + i * 0.01 &&
                            Math.abs(0.5f - noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37f + freq) {
                        ore = entry;
                        break;
                    }
                }else{
                    if(block == Blocks.air || (ore!=Blocks.air&&ore!=null))continue;
                    boolean empty = false;
                    for(Point2 p : Geometry.d4){
                        Tile other = tiles.get(x + p.x, y + p.y);
                        if(other != null && other.block() == Blocks.air){
                            empty = true;
                            break;
                        }
                    }
                    if (empty && noise(x + 78+i*700, y, 4, 0.7f, 33f, 1f) > 0.57f) {
                        ore = entry;
                        break;
                    }
                }
            }

            if(ore == Blocks.oreScrap && rand.chance(0.33)){
                floor = Blocks.metalFloorDamaged;
            }
        });

        decoration(0.017f);


        //remove invalid ores
        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(Blocks.air);
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        for(Room espawn : enemies){
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = sector.info.attack = true;
        }else{
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(difficulty * 10, 1);
        }

        float waveTimeDec = 0.4f;

        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 1f, Math.max(difficulty - waveTimeDec, 0f));
        state.rules.waves = true;
        state.rules.env = sector.planet.defaultEnv;
        state.rules.enemyCoreBuildRadius = 600f;

        //spawn air only when spawn is blocked
        state.rules.spawns = Waves.generate(difficulty, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0, naval);
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();

            //spawn air enemies
            if(spawner.countGroundSpawns() == 0){
                state.rules.spawns = Waves.generate(sector.threat, new Rand(sector.id), state.rules.attackMode, true, false);
            }
        }
    }

    //Override basic;
    public void tech(int secSize,Block floor1, Block floor2, Block wall){

        pass((x, y) -> {
            if(!floor.asFloor().hasSurface()) return;

            int mx = x % secSize, my = y % secSize;
            int sclx = x / secSize, scly = y / secSize;
            if(noise(sclx, scly, 0.2f, 1f) > 0.63f && noise(sclx, scly + 999, 200f, 1f) > 0.6f && (mx == 0 || my == 0 || mx == secSize - 1 || my == secSize - 1)){
                if(rand.chance(noise(x + 0x231523, y, 40f, 1f))){
                    floor = floor1;
                    if(Mathf.dst(mx, my, secSize/2, secSize/2) > secSize/2f + 2){
                        floor = floor2;
                    }
                }

                if(block.solid && rand.chance(0.7)){
                    block = wall;
                    ore = Blocks.air;
                }
            }
        });
    }
}