package io.lylix.lya.multiblock.heater;

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;

public class TileHeaterCore extends TileHeater
{
    @Override
    public boolean isGoodForInterior(IMultiblockValidator validator)
    {
        return true;
    }
}
