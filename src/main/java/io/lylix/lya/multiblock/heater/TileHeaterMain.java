package io.lylix.lya.multiblock.heater;

import io.lylix.lya.LYA;
import io.lylix.lya.util.FluidHandlerItem;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileHeaterMain extends TileHeater
{
    private DataHeater content = new DataHeater();

    private ItemStackHandler filler = new ItemStackHandler(2)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(slot == 1) return stack;
            FluidStack f = FluidUtil.getFluidContained(stack);
            if(f != null && LYA.instance.config.getLiquidFuelPower(f.getFluid().getName()) > 0) return super.insertItem(slot, stack, simulate);
            return stack;
        }
    };

    public void handleInventory()
    {
        if(!filler.getStackInSlot(0).isEmpty() && FluidHandlerItem.isFluidContainer(filler.getStackInSlot(0)))
        {
            FluidHandlerItem.fill(this, filler, 0, 1);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) return true;
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == null && isAssembled()) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(filler);
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == null) return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(controller().getTankHandler());
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isGoodForSides(IMultiblockValidator validator)
    {
        return true;
    }

    public double getHeat()
    {
        return isMachineAssembled() ? controller().getHeatHandler().getTemp() : 0;
    }

    public boolean toggle()
    {
        return controller().toggle();
    }

    private void write(NBTTagCompound data)
    {
        data.setTag("items", filler.serializeNBT());
        if(isMachineAssembled()) controller().content().serializeNBT(data);
    }

    private void read(NBTTagCompound data)
    {
        if(data.hasKey("items")) this.filler.deserializeNBT(data.getCompoundTag("items"));
        if(data.hasKey("heater")) this.content.deserializeNBT(data);
    }

    @Override
    protected void syncDataTo(NBTTagCompound data, SyncReason syncReason)
    {
        write(data);
        super.syncDataTo(data, syncReason);
    }

    @Override
    protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason)
    {
        read(data);
        super.syncDataFrom(data, syncReason);
    }

    public void saveInternalData()
    {
        content.assimilate(controller().content());
    }

    public DataHeater getInternalData()
    {
        return content;
    }
}
