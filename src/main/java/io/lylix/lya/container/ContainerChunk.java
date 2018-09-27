package io.lylix.lya.container;

import io.lylix.lya.tile.TileChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerChunk extends ContainerBase
{
    public ContainerChunk(IInventory playerInventory, TileChunk tile)
    {
        super(playerInventory, tile);
    }

    @Override
    public void addOwnSlots(IItemHandler handler)
    {
        int x = 187;
        int y = 6;

        int slotIndex = 0;
        for (int i = 0; i < handler.getSlots(); i++)
        {
            addSlotToContainer(new SlotItemHandler(handler, slotIndex, x, y));
            slotIndex++;
            y += 18;
        }
    }
}
