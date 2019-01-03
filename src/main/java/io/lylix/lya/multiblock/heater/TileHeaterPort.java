package io.lylix.lya.multiblock.heater;

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;

public class TileHeaterPort extends TileHeater
{
    @Override
    public boolean isGoodForSides(IMultiblockValidator validator)
    {
        return true;
    }

    @Override
    public boolean isGoodForTop(IMultiblockValidator validator)
    {
        return true;
    }

    @Override
    public boolean isGoodForBottom(IMultiblockValidator validator)
    {
        return true;
    }
}
