package io.lylix.lya.item.itemblock;

import io.lylix.lya.LYA;
import io.lylix.lya.tile.TileChunk;
import mekanism.api.energy.IEnergizedItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(iface="mekanism.api.energy.IEnergizedItem", modid="mekanism")
public class ItemBlockChunk extends ItemBlock implements IEnergizedItem
{
    private static final String NBT_ENERGY = "energy";
    private static final String NBT_ITEMS = "items";

    private static final int CAPACITY = LYA.instance.config.capacity;

    public ItemBlockChunk(Block block)
    {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        super.onCreated(stack, worldIn, playerIn);
        createStack(stack, new TileChunk());
    }

    public static ItemStack createStack(ItemStack stack, TileChunk te)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if(tag == null)
        {
            tag = new NBTTagCompound();
        }

        tag.setInteger(NBT_ENERGY, te.getEnergy().getEnergyStored());
        tag.setTag(NBT_ITEMS, te.getCards().serializeNBT());

        stack.setTagCompound(tag);

        return stack;
    }

    public static int getEnergyStored(ItemStack stack)
    {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ENERGY)) ? stack.getTagCompound().getInteger(NBT_ENERGY) : 0;
    }

    public static NBTTagCompound getItemsStored(ItemStack stack)
    {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ITEMS)) ? NBTTagCompound.class.cast(stack.getTagCompound().getTag(NBT_ITEMS)) : new NBTTagCompound();
    }

    public static boolean hasItems(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ITEMS);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1D - ((double) getEnergyStored(stack) / (double) CAPACITY);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) getEnergyStored(stack) / (float) CAPACITY) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(I18n.format("misc.lya:energy_stored", getEnergyStored(stack), CAPACITY));
    }

    @Override
    public double getEnergy(ItemStack stack) {
        return getEnergyStored(stack);
    }

    @Override
    public void setEnergy(ItemStack stack, double energy)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if(tag == null)
        {
            tag = new NBTTagCompound();
        }

        tag.setInteger(NBT_ENERGY, (int) energy);

        stack.setTagCompound(tag);
    }

    @Override
    public double getMaxEnergy(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public double getMaxTransfer(ItemStack stack) {
        return 40000;
    }

    @Override
    public boolean canReceive(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canSend(ItemStack stack) {
        return false;
    }
}
