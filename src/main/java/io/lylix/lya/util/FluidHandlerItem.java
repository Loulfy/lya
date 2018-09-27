package io.lylix.lya.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
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
                clear(stack, handler);
                inputCopy = handler.getContainer();
            }

            // Can stack ?
            if(!inventory.getStackInSlot(outSlot).isEmpty() && (!ItemHandlerHelper.canItemStacksStack(inventory.getStackInSlot(outSlot), inputCopy) || inventory.getStackInSlot(outSlot).getCount() == inventory.getStackInSlot(outSlot).getMaxStackSize()))
            {
                return;
            }

            // Can fill ?
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
