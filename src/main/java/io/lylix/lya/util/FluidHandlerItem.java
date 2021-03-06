package io.lylix.lya.util;

import io.lylix.lya.LYA;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class FluidHandlerItem
{
    public static boolean isFluidContainer(ItemStack stack)
    {
        return !stack.isEmpty() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
    }

    public static void fill(TileEntity tileEntity, ItemStackHandler inventory, int inSlot, int outSlot)
    {
        IFluidHandler tank = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if(tank == null) return;

        FluidStack stack = FluidUtil.getFluidContained(inventory.getStackInSlot(inSlot));
        if(stack != null)
        {
            ItemStack inputCopy = size(inventory.getStackInSlot(inSlot).copy(), 1);

            // Get empty item's container
            IFluidHandlerItem handler = FluidUtil.getFluidHandler(inputCopy);
            if (handler != null)
            {
                // Can fill ?
                stack = extract(handler, tank);
                if(stack == null) return;
                inputCopy = handler.getContainer();
            }

            // Can stack ?
            if(!inventory.getStackInSlot(outSlot).isEmpty() && (!ItemHandlerHelper.canItemStacksStack(inventory.getStackInSlot(outSlot), inputCopy) || inventory.getStackInSlot(outSlot).getCount() == inventory.getStackInSlot(outSlot).getMaxStackSize()))
            {
                return;
            }

            // Fill
            if(tank.fill(stack, false) == stack.amount) tank.fill(stack, true);
            else return;

            // Consume item
            if(inventory.getStackInSlot(outSlot).isEmpty())
            {
                inventory.setStackInSlot(outSlot, inputCopy);
            }
            else if(ItemHandlerHelper.canItemStacksStack(inventory.getStackInSlot(outSlot), inputCopy))
            {
                inventory.getStackInSlot(outSlot).grow(1);
            }

            inventory.getStackInSlot(inSlot).shrink(1);

            tileEntity.markDirty();
        }
    }

    public static void clear(FluidStack fluid, IFluidHandler handler)
    {
        if(fluid != null && handler != null) handler.drain(fluid, true);
    }

    private static FluidStack extract(IFluidHandler src, IFluidHandler dst)
    {
        IFluidTankProperties[] isrc = src.getTankProperties();
        IFluidTankProperties[] idst = dst.getTankProperties();
        if(isrc.length == 1 && idst.length == 1)
        {
            FluidStack item = isrc[0].getContents();
            FluidStack tank = idst[0].getContents();

            int amount = idst[0].getCapacity();
            if(tank != null) amount -= tank.amount;
            if(item != null) return src.drain(amount, true);
        }
        return null;
    }

    public static ItemStack size(ItemStack stack, int size)
    {
        if(size <= 0 || stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        ItemStack ret = stack.copy();
        ret.setCount(size);

        return ret;
    }
}
