package mDimension.world.type;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import mindustry.type.Item;
import mindustry.world.Block;

public class SeedItem extends Item {
    public static ObjectMap<Item,Block> map = new ObjectMap<>();
    public SeedItem(String name, Color color) {
        super(name, color);
    }
}
