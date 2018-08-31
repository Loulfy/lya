package io.lylix.lya.block;

import io.lylix.lya.LYA;
import io.lylix.lya.render.IModelRegister;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends Block
{
    public BlockBase()
    {
        super(Material.IRON);
        setHardness(2F);
        setCreativeTab(LYA.instance.tab);
    }

    public Item createItem()
    {
        return new ItemBlock(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(IModelRegister register)
    {

    }
}
