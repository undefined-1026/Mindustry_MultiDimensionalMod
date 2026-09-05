package mDimension.consumers;

import arc.Core;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import mDimension.world.data.Beam;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.Stats;

import static mindustry.world.meta.StatValues.withTooltip;

public class ConsumeBeamBoost extends ConsumeBeam{
    float effect = 0;
    public ConsumeBeamBoost(float requiredPower, Beam beam,float effect){
        super(requiredPower,beam);
        this.effect = effect;
    }

    {
        boost();
    }

    @Override
    public void display(Stats stats) {

        if(inputBeam != null){
            Stack stack = new Stack();

            stack.add(new Table(b -> {
                b.table(Styles.grayPanel, t -> {
                    t.image(inputBeam.uiIcon).size(48f).scaling(Scaling.fit).pad(10f);
                    t.table(info -> {
                        info.add(inputBeam.localizedName).left();
                    });
                    t.table(o->{
                        o.right();
                        o.top();
                        o.add(
                                Core.bundle.format("stat.laserpower", requiredPower)
                        ).pad(12f);
                        o.add("[stat]"+Strings.autoFixed(effect *100,1) + StatUnit.percent.localized()).pad(12f);

                    });

                }).growX().pad(5f);
            }));

            withTooltip(stack, inputBeam, true);
            stats.add(booster ? Stat.booster : Stat.input, t->{
                t.add(stack);
            });
        }
    }
}
