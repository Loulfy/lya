package io.lylix.lya.multiblock.heater;

import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;

public class TileHeaterWall extends TileHeaterPort
{
    @Override
    public boolean isGoodForFrame(IMultiblockValidator validator)
    {
        return true;
    }
}
