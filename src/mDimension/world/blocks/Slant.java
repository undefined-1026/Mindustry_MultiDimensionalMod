package mDimension.world.blocks;

import mindustry.entities.units.BuildPlan;
import mindustry.world.Block;

public interface Slant {
    boolean isSlant();
    default void slantFlipRotation(BuildPlan plan, boolean x) {
        if(this instanceof Block b) {
            if (!x) {
                switch (plan.rotation) {
                    case (0) -> plan.rotation = b.planRotation(3);
                    case (3) -> plan.rotation = b.planRotation(1);
                    case (1) -> plan.rotation = b.planRotation(2);
                    case (2) -> plan.rotation = b.planRotation(1);
                }
            } else {
                switch (plan.rotation) {
                    case (0) -> plan.rotation = b.planRotation(1);
                    case (1) -> plan.rotation = b.planRotation(0);
                    case (2) -> plan.rotation = b.planRotation(3);
                    case (3) -> plan.rotation = b.planRotation(2);
                }
            }
        }
    }
}
