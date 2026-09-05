package mDimension.meta;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import mDimension.world.data.Beam;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.iconMed;

public class MD_StatValues extends StatValues {
    public static Stack BeamStack(Beam beam, float power){
        return BeamStack(beam.uiIcon,power,beam,true,true);
    }
    public static Stack BeamStack(Beam beam, float power,boolean displayWavelenght){
        return BeamStack(beam.uiIcon,power,beam,true,displayWavelenght);
    }

    public static Stack BeamStack(TextureRegion region, float power, @Nullable Beam content,boolean tooltip,boolean displayWavelenght){

        Stack stack = new Stack();

        stack.add(new Table(b -> {
            b.table(Styles.grayPanel, t -> {
                t.image(region).size(48f).scaling(Scaling.fit).pad(10f);
                t.table(info -> {
                    info.add(content.localizedName).left();
                });
                t.table(o->{
                    o.right();
                    o.top();
                    if(displayWavelenght) {
                        o.add(
                                Core.bundle.format("stat.laserpower", power)
                        ).pad(10f).row();
                        o.add(
                                Core.bundle.format("stat.wavelength", content.energyLevel)
                        ).pad(10f);
                    }else{
                        o.add(
                                Core.bundle.format("stat.laserpower", power)
                        ).pad(12f);
                    }
                });

            }).growX().pad(5f);
        }));

        withTooltip(stack, content, tooltip);

        return stack;
    }
    public static Stack BeamStack(float power,float minWave,float maxWave,boolean tooltip ){

        Stack stack = new Stack();

        stack.add(new Table(b -> {
            b.table(Styles.grayPanel, t -> {
                t.add(Core.bundle.get("stat.light")).pad(12f);
                t.table(o->{
                    o.right();
                    o.top();
                    o.add(
                            Core.bundle.format("stat.laserpower",power)
                    ).pad(8f).row();
                    o.add(
                        Core.bundle.format("stat.wavelengthinterval",minWave,maxWave)
                    ).pad(8f);
                });

            }).growX().pad(5f);
        }));
        return stack;
    }
    public static Table displayLiquid(Liquid liquid, float amount, boolean perSecond,boolean showName){
        Table t = new Table();

        t.add(new Stack(){{
            add(new Image(liquid.uiIcon).setScaling(Scaling.fit));

            if(amount != 0){
                Table t = new Table().left().bottom();
                t.add(Strings.autoFixed(amount, 3)).style(Styles.outlineLabel);
                add(t);
            }
        }}).size(iconMed).padRight(3  + (amount != 0 ? (Strings.autoFixed(amount, 3).length() - 1) * 10 : 0)).with(s -> withTooltip(s, liquid, false));

        if(perSecond && amount != 0){
            t.add(StatUnit.perSecond.localized()).padLeft(1.5f).padRight(1.5f).color(Color.lightGray).style(Styles.outlineLabel);
        }

        if(showName)t.add(liquid.localizedName);

        return t;
    }

}
