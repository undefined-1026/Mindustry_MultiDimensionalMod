package mDimension.entity.ability;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

public class RepairBuildFieldAbility extends Ability {
    public float delay = 90f,range = 8*12;
    public float amount = 150;
    public static final Seq<Building> builds = new Seq<>();
    public float timer=0;
    public Color effectColor = Pal.heal;
    RepairBuildFieldAbility(){}
    public RepairBuildFieldAbility(float amount, float range, float delay) {
        this.amount = amount;
        this.range = range;
        this.delay = delay;
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / tilesize, 2)));
        t.row();
        t.add(abilityStat("repairspeed", Strings.autoFixed(amount/delay*60f, 2)));

    }

    @Override
    public void update(Unit u) {
        if((timer += Time.delta) >= delay) {
            timer=0;
            float x = u.x, y = u.y;
            builds.clear();
            indexer.eachBlock(null, x, y, range, build -> build.team == u.team, build -> {
                if (build.damaged()) {
                    build.heal(amount);
                    //add prev check so ability spam doesn't lead to particle spam (essentially, recently suppressed blocks don't get new particles)
                    if (!headless) {
                        builds.add(build);
                    }
                }
            });

            //to prevent particle spam, the amount of particles is to remain constant (scales with number of buildings)
            float scaledChance = 3f / builds.size;
            for (var build : builds) {
                if (Mathf.chance(scaledChance)) {
                    Time.run(Mathf.random(delay), () -> {
                        Fx.regenSuppressSeek.at(build.x + Mathf.range(build.block.size * tilesize / 2f), build.y + Mathf.range(build.block.size * tilesize / 2f), 0f, effectColor, u);
                    });
                }
            }
        }
    }
}
