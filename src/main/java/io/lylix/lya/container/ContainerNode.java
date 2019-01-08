package io.lylix.lya.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class ContainerNode extends ContainerBase
{
    public ContainerNode(IInventory playerInventory, TileEntity tile)
    {
        super(playerInventory, tile);
    }

    @Override
    public void addPlayerSlots(IInventory playerInventory)
    {

    }
}
