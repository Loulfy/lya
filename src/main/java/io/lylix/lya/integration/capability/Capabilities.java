package io.lylix.lya.integration.capability;

import mekanism.api.IHeatTransfer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class Capabilities
{
    @CapabilityInject(IHeatTransfer.class)
    public static Capability<IHeatTransfer> HEAT = null;

    public static boolean hasCapability(ICapabilityProvider provider, Capability<?> cap, EnumFacing side)
    {
        return provider != null && cap != null && provider.hasCapability(cap, side);
    }

    public static <T> T getCapability(ICapabilityProvider provider, Capability<T> cap, EnumFacing side)
    {
        if (provider == null || cap == null) return null;
        return provider.getCapability(cap, side);
    }
}
