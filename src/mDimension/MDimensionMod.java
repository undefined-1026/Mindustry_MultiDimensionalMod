package mDimension;
import arc.Core;
import arc.Events;
import arc.util.Time;
import mDimension.core.MDRenderer;
import mDimension.core.MDShaders;
import mDimension.meta.MD_Stat;
import mDimension.meta.MD_StatUnit;
import mDimension.world.MDEvents;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

//物品导入
import mDimension.content.*;

public class MDimensionMod extends Mod {
    public static final String MODNAME = "mdimension",
    BEAM_OPACITY = MODNAME+"-beamopacity"
    ;
    public MDimensionMod() {

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Time.runTask(20, () -> {
                BaseDialog welcome = new BaseDialog("Welcome to play Multidimensional");
                welcome.cont.add("A new journey Let's begin").colspan(2).row();
                welcome.cont.image(Core.atlas.find("mdimension-evil")).pad(10f);
                welcome.cont.image(Core.atlas.find("mdimension-neuro")).pad(20f);
                Time.runTask(1, welcome::addCloseButton);
                welcome.show();
            });
        });


    }

    @Override
    public void init() {
        MDShaders.init();
        MDRenderer.init();
        MDEvents.init();

        Events.on(EventType.ClientLoadEvent.class,e->{
            Core.app.post(()->{
                Vars.ui.settings.graphics.sliderPref(BEAM_OPACITY,100,0,100,1,s->s+"%");
            });
        });
    }

    public void replaceRegion(String from,String to,boolean abbModName){
        Core.atlas.addRegion(from,Core.atlas.find(abbModName?"mdimension-"+to:to));
    }
    public void replaceRegion(String from,String to){
        replaceRegion(from,to,true);
    }

    @Override
    public void loadContent() {

        MD_beams.load();
        MD_StatUnit.load();
        MD_Stat.load();
        MD_StatusEffects.load();
        MD_Items.load();
        MD_Liquids.load();
        MD_environment.load();
        MD_UnitTypes.load();
        MD_crops.load();
        MD_blocks.load();MD_TestBlock.load();
        original_reset.load();

        MD_Loadouts.load();
        MD_Planets.load();

        MD_SectorPresets.load();
        MD_TechTree.load();
    }

}