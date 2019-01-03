package io.lylix.lya.multiblock.heater;

import io.lylix.lya.integration.capability.Capabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileHeaterHeat extends TileHeaterPort
{
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == Capabilities.HEAT && isAssembled()) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == Capabilities.HEAT && isAssembled()) return Capabilities.HEAT.cast(controller().getHeatHandler());
        return super.getCapability(capability, facing);
    }
}
