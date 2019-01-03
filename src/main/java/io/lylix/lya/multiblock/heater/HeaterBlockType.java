package io.lylix.lya.multiblock.heater;

import net.minecraft.util.IStringSerializable;

public enum HeaterBlockType implements IStringSerializable
{
    Wall,
    Main,
    Core,
    Heat,
    Fuel,
    Info;

    @Override
    public String getName()
    {
        return this.toString();
    }
}
