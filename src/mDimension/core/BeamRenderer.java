package mDimension.core;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.util.Tmp;
import mDimension.entity.BeamEntity;
import mDimension.util.MDModStatus;
import mDimension.world.type.Beam;

public class BeamRenderer {
    public static void add(BeamEntity b){
        b.beam.beamEntities.add(b);
    }
    public static void remove(BeamEntity b){
        b.beam.beamEntities.remove(b);
    }
    public static void renderer(){
        float baseZ = Draw.z();
        Draw.z(MDRenderer.extBloomLayer);
        for(var beam: Beam.all) {
            var beams = beam.beamEntities;
            if (beams.isEmpty()) continue;
            for (int j = 0; j < beam.drawer.drawLayers.length; j++) {
                var drawLayers = beam.drawer.drawLayers[j];
                Draw.color(drawLayers.color,drawLayers.alpha * MDModStatus.beamOpacity);
                for (int i = 0; i < beams.size; i++) {
                    var e = beams.get(i);
                    if(Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(e.x, e.y, e.clipSize))) {
                        Draw.mixcol(beam.toColor, (e.strokeScl - 1) * 2.5f);
                        beam.drawer.drawLayers[j].draw(e);
                    }
                }
            }
        }
        Draw.z(baseZ);
    }
}
