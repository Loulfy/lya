package io.lylix.lya.container;

import io.lylix.lya.LYA;
import io.lylix.lya.tile.TileChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class ContainerBase extends Container
{
    private IItemHandler handler;

    public ContainerBase(IInventory playerInventory, TileEntity tile)
    {
        if(tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
            handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        addOwnSlots(handler);
        addPlayerSlots(playerInventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(index < handler.getSlots())
            {
                if(!this.mergeItemStack(itemstack1, handler.getSlots(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(itemstack1, 0, handler.getSlots(), false))
            {
                return ItemStack.EMPTY;
            }

            if(itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    public void addOwnSlots(IItemHandler handler)
    {

    }

    public void addPlayerSlots(IInventory playerInventory)
    {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                int x = 8 + col * 18;
                int y = row * 18 + 112;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row)
        {
            int x = 8 + row * 18;
            int y = 170;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }
}
