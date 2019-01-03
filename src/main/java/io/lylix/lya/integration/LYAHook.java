package io.lylix.lya.integration;

import li.cil.oc.api.Driver;
import dan200.computercraft.api.ComputerCraftAPI;
import io.lylix.lya.integration.computer.CCDriver;
import io.lylix.lya.integration.computer.OCDriver;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

public class LYAHook
{
    public final static String CC = "computercraft";
    public final static String OC = "opencomputers";

    public void init()
    {
        if(Loader.isModLoaded(CC)) hookCC();
        if(Loader.isModLoaded(OC)) hookOC();
    }

    @Optional.Method(modid = CC)
    private void hookCC()
    {
        ComputerCraftAPI.registerPeripheralProvider(new CCDriver());
    }

    @Optional.Method(modid = OC)
    private void hookOC()
    {
        Driver.add(new OCDriver());
    }
}
