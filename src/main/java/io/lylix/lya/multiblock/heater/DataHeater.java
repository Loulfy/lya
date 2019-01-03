package io.lylix.lya.multiblock.heater;

import io.lylix.lya.LYA;
import io.lylix.lya.integration.capability.Heater;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class DataHeater
{
    private final static String GEAR = "gear";

    public Heater heat;
    public boolean gear;
    public FluidTank tank;

    public DataHeater()
    {
        gear = true;
        heat = new Heater();
        tank = new FluidTank(10000)
        {
            @Override
            public boolean canFillFluidType(FluidStack fluid)
            {
                return isReady() && LYA.instance.config.getLiquidFuelPower(fluid.getFluid().getName()) > 0;
            }

            @Override
            protected void onContentsChanged()
            {
                DataHeater.this.onContentsChanged();
            }
        };
    }

    public void onContentsChanged()
    {

    }

    public boolean isReady()
    {
        return true;
    }

    public boolean isDirty()
    {
        return tank.getFluidAmount() > 0 || heat.getTemp() > 0 || !gear;
    }

    public void assimilate(DataHeater data)
    {
        tank.setFluid(data.tank.getFluid() == null ? null : data.tank.getFluid().copy());
        heat.setTemp(data.heat.getTemp());
        gear = data.gear;
    }

    public void serializeNBT(NBTTagCompound data)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(GEAR, gear);
        tank.writeToNBT(tag);
        heat.writeToNBT(tag);
        data.setTag("heater", tag);
    }

    public void deserializeNBT(NBTTagCompound data)
    {
        if(data.hasKey("heater"))
        {
            NBTTagCompound tag = data.getCompoundTag("heater");
            if(tag.hasKey(GEAR)) gear = tag.getBoolean(GEAR);
            tank.readFromNBT(tag);
            heat.readFromNBT(tag);
        }
    }
}
