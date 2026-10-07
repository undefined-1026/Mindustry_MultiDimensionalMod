package mDimension.core;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.*;
import arc.struct.Seq;
import arc.util.pooling.Pool;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.Layer;

import static arc.Core.settings;
//*参考了eu**/
public class MDRenderer {
    public static MDRenderer renderer;

    private FrameBuffer invBuffer;
    private FrameBuffer holeBuffer;
    private Texture sample;
    private Seq<Hole> holes = new Seq<>();
    private Pool<Hole> holePool = new Pool<Hole>() {
        @Override
        protected Hole newObject() {
            return new Hole();
        }

        @Override
        protected void reset(Hole o) {
            o.radius = 10;
            o.x = o.y = o.strength = 0;
        }
    };
    public static final float extBloomLayer = 42f;

    public static void addHole(float x,float y,float str,float rad){
        renderer.holes.add(renderer.holePool.obtain().init(x,y,str,rad));
    }

    public static Well well;
    protected MDRenderer(){
        if(!Vars.headless) {
            invBuffer = new FrameBuffer();
            holeBuffer = new FrameBuffer();
            well = new Well();
            well.init();
            Events.run(EventType.Trigger.draw, this::advancedDraw);
        }
    }
    public static void init(){
        if(renderer == null) renderer = new MDRenderer();
    }

    public void advancedDraw(){
        if(settings.getBool("pixelate") || !settings.getBool("bloom")){
            return;
        }

        addHole(8*160,8*160,50,10*8f);

        Draw.draw(extBloomLayer-0.01f, () -> {
            Vars.renderer.bloom.capture();
        });

        Draw.draw(extBloomLayer+1.01f, () -> {
            Vars.renderer.bloom.render();
        });

        Draw.drawRange(212f,well::capture,well::render);


        Draw.draw(-12 ,()->{
            invBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            invBuffer.begin();
            holeBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            holeBuffer.begin();
        });

        Draw.drawRange(211,0.2f,()->{
            invBuffer.end();
            Vars.renderer.effectBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            Vars.renderer.effectBuffer.begin();
        },()->{
            MDShaders.invert.teakSample = invBuffer.getTexture();
            Vars.renderer.effectBuffer.end();
            Vars.renderer.effectBuffer.blit(MDShaders.invert);
        });
        Draw.draw(140.1f,()->{
            holeBuffer.end();
            MDShaders.hole.setOfSeq(holes);
            holeBuffer.blit(MDShaders.hole);
            renderer.holePool.freeAll(holes);
        });

        holes.clear();
    }

    public static class Well{

        private Shader shader;
        private FrameBuffer buffer;

        private boolean capturing = false;

        public void init(){
            buffer = Vars.renderer.effectBuffer;
            shader = MDShaders.well;
            //shader.apply();
        }
        public void capture(){
            if(!capturing){
                capturing = true;
                buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                buffer.begin(Color.clear);
            }
        }

        public void render(){
            if(capturing){
                capturing = false;
                buffer.end();
            }
            buffer.blit(shader);

            buffer.begin();
            Draw.rect();
            buffer.end();
        }
    }
    public static class Hole{
        public float x,y,strength,radius;
        public Hole() {
        }
        public Hole(float x, float y, float strength, float radius) {
            this.x = x;
            this.y = y;
            this.strength = strength;
            this.radius = radius;
        }

        public Hole init(float x, float y, float strength, float radius){
            this.x = x;
            this.y = y;
            this.strength = strength;
            this.radius = radius;
            return this;
        }
    }

}



//public class MainRenderer{
//    private final Seq<BlackHole> holes = new Seq<>();
//    private static MainRenderer renderer;
//
//    private FrameBuffer buffer;
//
//    private static final float[][] initFloat = new float[512][];
//    private static final Pool<BlackHole> holePool = Pools.get(BlackHole.class, BlackHole::new);
//
//    protected MainRenderer(){
//        if(!Vars.headless) {
//            MainShader.createShader();
//
//            buffer = new FrameBuffer();
//            Events.run(Trigger.draw, this::advancedDraw);
//        }
//    }
//
//    public static void init(){
//        if(renderer == null) renderer = new MainRenderer();
//        for(int i = 0; i < 512; i++){
//            initFloat[i] = new float[i * 4];
//        }
//    }
//
//    public static void addBlackHole(float x, float y, float inRadius, float outRadius, float alpha){
//        if(!Vars.headless) renderer.addHole(x, y, inRadius, outRadius, alpha);
//    }
//    public static void addBlackHole(float x, float y, float inRadius, float outRadius){
//        if(!Vars.headless) renderer.addHole(x, y, inRadius, outRadius, 1);
//    }
//
//    private void advancedDraw(){
//        if(settings.getBool("pixelate") || holes.size >= 512 || hasOtherContentMod) {
//            holes.clear();
//            return;
//        }
//
//        Draw.draw(Layer.background - 1, () -> {
//            buffer.resize(graphics.getWidth(), graphics.getHeight());
//            buffer.begin();
//        });
//
//        Draw.draw(Layer.max - 1, () -> {
//            buffer.end();
//
//            if(holes.size >= 512) {
//                for(int i = 0; i <= holes.size - 512; i++){
//                    holes.remove(i);
//                }
//            }
//            if(holes.size >= MainShader.MaxCont) MainShader.createShader();
//
//            float[] blackholes = initFloat[holes.size];
//
//            for(int i = 0; i < holes.size; i++){
//                var hole = holes.get(i);
//                blackholes[i * 4] = hole.x;
//                blackholes[i * 4 + 1] = hole.y;
//                blackholes[i * 4 + 2] = hole.inRadius;
//                blackholes[i * 4 + 3] = hole.outRadius;
//
//                Draw.color(Tmp.c2.set(Color.black).a(hole.alpha));
//                Fill.circle(hole.x, hole.y, hole.inRadius * 1.5f);
//                Draw.color();
//                //之前忘了
//                holePool.free(hole);
//            }
//            MainShader.holeShader.blackHoles = blackholes;
//            buffer.blit(MainShader.holeShader);
//
//            buffer.begin();
//            Draw.rect();
//            buffer.end();
//            holes.clear();
//        });
//    }
//
//    private void addHole(float x, float y, float inRadius, float outRadius, float alpha){
//        if(inRadius > outRadius || outRadius <= 0) return;
//
//        holes.add(holePool.obtain().set(x, y, inRadius, outRadius, alpha));
//    }
//
//    private static class BlackHole{
//        float x, y, inRadius, outRadius, alpha;
//
//        public BlackHole set(float x, float y, float inRadius, float outRadius, float alpha){
//            this.x = x;
//            this.y = y;
//            this.inRadius = inRadius;
//            this.outRadius = outRadius;
//            this.alpha = alpha;
//            return this;
//        }
//
//        public BlackHole(){
//
//        }
//    }
//}
