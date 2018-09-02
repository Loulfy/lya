package io.lylix.lya.item.itemblock;

import io.lylix.lya.LYA;
import io.lylix.lya.integration.energy.ItemBlockEnergy;
import io.lylix.lya.tile.TileChunk;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockChunk extends ItemBlockEnergy
{
    private static final String NBT_INVENTORY = "inventory";

    public ItemBlockChunk(Block block)
    {
        super(block, LYA.instance.config.capacity, LYA.instance.config.transfer);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state)
    {
        boolean place = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state);

        if(place)
        {
            TileChunk tile = TileChunk.class.cast(world.getTileEntity(pos));
            if(tile != null)
            {
                tile.getEnergy().setStored(get(stack));
                if(hasInventory(stack))
                {
                    tile.getInventory().deserializeNBT(getInventory(stack));
                    tile.getChunkLoader().refreshPresence();
                }
            }
        }

        return place;
    }

    public static void createStack(ItemStack stack, TileChunk tile)
    {
        set(stack, tile.getEnergy().getEnergyStored());
        setInventory(stack, tile.getInventory().serializeNBT());
    }

    public static void setInventory(ItemStack stack, NBTTagCompound tag)
    {
        getTagCompoundSafe(stack).setTag(NBT_INVENTORY, tag);
    }

    public static NBTTagCompound getInventory(ItemStack stack)
    {
        NBTTagCompound tag = getTagCompoundSafe(stack);
        return tag.hasKey(NBT_INVENTORY) ? NBTTagCompound.class.cast(tag.getTag(NBT_INVENTORY)) : new NBTTagCompound();
    }

    public static boolean hasInventory(ItemStack stack)
    {
        return getTagCompoundSafe(stack).hasKey(NBT_INVENTORY);
    }
}
