package mDimension.world.blocks;

import arc.graphics.Color;
import mindustry.gen.TankUnit;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

import mindustry.world.meta.Env;

public class DepicilonUnitType extends UnitType {
    public DepicilonUnitType(String name){
        super(name);
        constructor = UnitEntity::create;
        outlineColor = Color.valueOf("2D2D2D");
        envDisabled = Env.space;
        researchCostMultiplier = 7.5f;
    }

    public static class DepicilonTankUnitType extends DepicilonUnitType{
        public DepicilonTankUnitType(String name){
            super(name);
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 1.3f;
            envDisabled = Env.none;
            speed = 0.8f;
            constructor = TankUnit::create;
        }
    }
}
