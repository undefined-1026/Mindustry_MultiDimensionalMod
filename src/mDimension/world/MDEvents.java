package mDimension.world;

import arc.Core;
import arc.Events;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Interval;
import mDimension.consumers.ConsumeBeam;
import mDimension.consumers.modules.ExtraModule;
import mDimension.input.MD_DesktopInput;
import mDimension.input.MD_MobileInput;
import mDimension.ui.ObjectInspector;
import mDimension.ui.MDKeyBind;
import mDimension.world.blocks.flux.Flux;
import mDimension.world.blocks.flux.FluxGraph;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Groups;

public class MDEvents {
    public static Interval timer;
    public static Seq<Building> out = new Seq<>();
    public static Seq<Building> overload = new Seq<>();
    public static Seq<FluxGraph> graphs = new Seq<>();
    public static ObjectInspector inspector = new ObjectInspector();
    static Object hover = null;
    static float dst;
    static Rect range = new Rect(0,0,3,3);
    static boolean down = false;

    public static  void init(){
        timer = new Interval(3);
        Events.run(EventType.Trigger.update,()->{
            if(timer.get(0,5*60)){
                ConsumeBeam.free();
                for(ExtraModule<?> mods :ExtraModule.allModule){
                    mods.freeAllIf(b->b.dead);
                }
            }
            updateGraphs();

        });
        Events.on(EventType.ClientLoadEvent.class,e->{
            Vars.control.input.remove();
            Vars.control.input = Vars.mobile?new MD_MobileInput():new MD_DesktopInput();
            Vars.control.input.add();
        });

        inspector.setPosition(40, 40); // 左下角


        if(!Vars.mods.list().contains(m->
                m.meta.name.equals("fieldmodifier") && m.enabled())
        )Events.run(EventType.Trigger.update, () -> {
            if(Core.input.keyRelease(MDKeyBind.reflection_debugging) && Vars.state.rules.infiniteResources){ // F4 打开检视器
                hover = null;
                dst = -1;
                float x = Core.input.mouseWorldX(), y= Core.input.mouseWorldY();
                range.setCenter(x,y);
                Units.nearby(range,u->{
                    if(u.dst2(x,y) < dst || dst < 0){
                        hover = u;
                    }
                });
                if(hover == null){
                    hover = Vars.world.buildWorld(x,y);
                    if(hover!=null){
                        var build = (Building)hover;
                    }
                }
                if(hover == null){
                    dst = -1;
                    Groups.bullet.intersect(range.x,range.y,range.width,range.height,b->{
                        if(b.dst2(x,y) < dst || dst < 0){
                            hover = b;
                        }
                    });
                }
                if(hover != null){
                    inspector.inspect(hover);
                    if(!inspector.hasParent()){
                        Core.scene.add(inspector);
                    }
                }
            }
        });
        Events.on(EventType.SaveLoadEvent.class,e->{
            updateGraphs(true);
        });
    }


    public static void updateGraphs(){
        updateGraphs(false);
    }
    public static void updateGraphs(boolean loadSave){
        overload.clear();
        if(!Vars.state.isPaused()) {
            int ind = 0;
            Items.coal.description = "";
            for (FluxGraph graph : graphs) {
                if (graph.init) {
                    //Items.coal.description += "\n\n\nindex:" + ind + " allSize:" + graphs.size + "\n" + graph.getDebugLog();
                    if(loadSave){
                        graph.saveLoad();
                        graph.updateGraph();
                    }else{
                        graph.update();
                    }
                }
                ind++;
            }

            for(int i=0;i<overload.size;i++){
                Building b = overload.get(i);
                if(b instanceof Flux f){
                    var cons = f.consFlux();
                    f.cleanFlux();
                    if(cons.canOverload){
                        f.overload();
                        f.flux().overload = true;
                    }
                }
            }
        }
    }

}
