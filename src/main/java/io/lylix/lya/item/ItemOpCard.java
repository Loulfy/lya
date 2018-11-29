package io.lylix.lya.item;

import io.lylix.lya.render.IModelRegister;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemOpCard extends ItemBase
{
    public ItemOpCard()
    {
        setMaxStackSize(1);
    }

    @Override
    public void initModel(IModelRegister register)
    {
        register.setModel(this, 0, new ModelResourceLocation("lya:id_card_active", "inventory"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add("One to rule them all");
    }
}
