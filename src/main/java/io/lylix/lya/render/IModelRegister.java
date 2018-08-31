package io.lylix.lya.render;

import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IModelRegister
{
    void setModel(Block block, int meta, ModelResourceLocation resource);

    void setModel(Item item, int meta, ModelResourceLocation resource);
}
