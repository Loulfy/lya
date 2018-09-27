package io.lylix.lya.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.stream.Collectors;

public class LYAUtils
{
    public static Set<UUID> getOnlinePlayerUUID()
    {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            return Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerProfiles()).map(GameProfile::getId).collect(Collectors.toSet());
        }
        else return new HashSet<>();
    }

    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory)
    {
        dropInventoryItems(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
    }

    private static void dropInventoryItems(World worldIn, double x, double y, double z, IItemHandler inventory)
    {
        for (int i = 0; i < inventory.getSlots(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                spawnItemStack(worldIn, x, y, z, itemstack);
            }
        }
    }

    private static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        float f = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (!stack.isEmpty())
        {
            EntityItem entityitem = new EntityItem(worldIn, x + (double)f, y + (double)f1, z + (double)f2, stack.splitStack(RANDOM.nextInt(21) + 10));
            entityitem.motionX = RANDOM.nextGaussian() * 0.05000000074505806D;
            entityitem.motionY = RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
            entityitem.motionZ = RANDOM.nextGaussian() * 0.05000000074505806D;
            worldIn.spawnEntity(entityitem);
        }
    }
}
