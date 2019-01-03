package io.lylix.lya.multiblock.heater;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileHeaterFuel extends TileHeaterPort
{
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && isAssembled()) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && isAssembled()) return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(controller().getTankHandler());
        return super.getCapability(capability, facing);
    }
}
