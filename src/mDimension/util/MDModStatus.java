package mDimension.util;

import arc.Core;
import arc.Events;
import mDimension.MDimensionMod;
import mindustry.game.EventType;

public class MDModStatus {
    public static float beamOpacity=1f;
    public static void init(){
        Events.run(EventType.Trigger.update,()->{
            beamOpacity = (Core.settings.getInt(MDimensionMod.BEAM_OPACITY,80)/100f);
        });
    }
}
