package io.lylix.lya.item;

import io.lylix.lya.LYA;
import io.lylix.lya.render.IModelRegister;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item
{
    public ItemBase()
    {
        setCreativeTab(LYA.instance.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(IModelRegister register)
    {
    }
}
