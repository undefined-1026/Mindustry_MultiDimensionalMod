package mDimension.content;

import arc.graphics.Color;
import mDimension.world.type.Beam;

public class MD_beams {
    public static Beam
            near_infrared_light, ultraviolet_light,bright_light,nihility_light;
    public static void load() {
        near_infrared_light = new Beam("near-infrared-light", Color.valueOf("FF6E61")) {{
            energyLevel = 3;
            length = 18;

        }};
        ultraviolet_light = new Beam("ultraviolet-light", Color.valueOf("E363FF")) {{
            energyLevel = 5;
            length = 12;

        }};
        bright_light = new Beam("bright-light",Color.valueOf("C9EB6E")){{
            energyLevel = 4;
            length = 12;

        }};
        nihility_light = new Beam("nihility_light",Color.valueOf("fff080")){{
            energyLevel = 9;
            length = 6;

        }};
    }

}
