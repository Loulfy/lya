package io.lylix.lya.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerHeater extends ContainerBase
{
    public ContainerHeater(IInventory playerInventory, TileEntity tile)
    {
        super(playerInventory, tile);
    }

    @Override
    public void addOwnSlots(IItemHandler handler)
    {
        addSlotToContainer(new SlotItemHandler(handler, 0, 21, 26));
        addSlotToContainer(new SlotItemHandler(handler, 1, 21, 68));
    }
}
