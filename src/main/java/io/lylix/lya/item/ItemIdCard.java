package io.lylix.lya.item;

import io.lylix.lya.LYAItems;
import io.lylix.lya.chunkloader.ChunkManager;
import io.lylix.lya.render.IModelRegister;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemIdCard extends ItemBase
{
    public static final String NBT_OWNER = "Owner";
    private static final String NBT_OWNER_NAME = "OwnerName";

    public ItemIdCard()
    {
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("active"), (stack, world, entity) -> (entity != null && isValid(stack)) ? 1.0f : 0.0f);
    }

    @Override
    public void initModel(IModelRegister register)
    {
        register.setModel(this, 0, new ModelResourceLocation("lya:id_card", "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        if(!world.isRemote)
        {
            if(player.isSneaking())
            {
                if(stack.hasTagCompound())
                {
                    getTagCompoundSafe(stack).removeTag(NBT_OWNER);
                    getTagCompoundSafe(stack).removeTag(NBT_OWNER_NAME);
                }
            }
            else
            {
                getTagCompoundSafe(stack).setString(NBT_OWNER, player.getGameProfile().getId().toString());
                getTagCompoundSafe(stack).setString(NBT_OWNER_NAME, player.getGameProfile().getName());
            }
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);

        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_OWNER_NAME))
        {
            tooltip.add("Bound to: "+stack.getTagCompound().getString(NBT_OWNER_NAME));
        }
    }

    private NBTTagCompound getTagCompoundSafe(ItemStack stack)
    {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if(!stack.hasTagCompound())
        {
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        return tagCompound;
    }

    @Nullable
    public static UUID getOwner(ItemStack stack)
    {
        if(stack.getItem() == LYAItems.OP_CARD) return ChunkManager.FAKE_PLAYER;

        if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_OWNER))
        {
            return UUID.fromString(stack.getTagCompound().getString(NBT_OWNER));
        }

        return null;
    }

    private static boolean isValid(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_OWNER);
    }
}
