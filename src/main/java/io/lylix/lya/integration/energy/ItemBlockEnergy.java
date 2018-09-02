package io.lylix.lya.integration.energy;

import mekanism.api.energy.IEnergizedItem;
import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(iface="mekanism.api.energy.IEnergizedItem", modid="mekanism")
@Optional.Interface(iface="cofh.redstoneflux.api.IEnergyContainerItem", modid ="redstoneflux")
public class ItemBlockEnergy extends ItemBlock implements IEnergizedItem, IEnergyContainerItem
{
    private static final String NBT_ENERGY = "energy";

    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public ItemBlockEnergy(Block block, int capacity, int maxTransfer) { this(block, capacity, maxTransfer, 0); }

    public ItemBlockEnergy(Block block, int capacity, int maxReceive, int maxExtract)
    {
        super(block);
        setNoRepair();
        setMaxStackSize(1);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public static NBTTagCompound getTagCompoundSafe(ItemStack stack)
    {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (!stack.hasTagCompound())
        {
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        return tagCompound;
    }

    public static int get(ItemStack stack)
    {
        NBTTagCompound tag = getTagCompoundSafe(stack);
        return tag.hasKey(NBT_ENERGY) ? tag.getInteger(NBT_ENERGY) : 0;
    }

    public static void set(ItemStack stack, int energy)
    {
        NBTTagCompound tag = getTagCompoundSafe(stack);
        tag.setInteger(NBT_ENERGY, energy);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(I18n.format("misc.lya:energy_stored", get(stack), capacity));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1D - (double) get(stack) / (double) capacity;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) get(stack) / (float) capacity) / 3.0F, 1.0F, 1.0F);
    }

    // Mekanism

    @Override
    public double getEnergy(ItemStack stack)
    {
        return get(stack);
    }

    @Override
    public void setEnergy(ItemStack stack, double energy)
    {
        set(stack, (int) energy);
    }

    @Override
    public double getMaxEnergy(ItemStack stack)
    {
        return capacity;
    }

    @Override
    public double getMaxTransfer(ItemStack stack)
    {
        return maxReceive;
    }

    @Override
    public boolean canReceive(ItemStack stack)
    {
        return maxReceive > 0;
    }

    @Override
    public boolean canSend(ItemStack stack)
    {
        return maxExtract > 0;
    }

    // Redstone Flux

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
    {
        if(maxReceive <= 0) return 0;

        int energy = get(container);

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if(!simulate) set(container, energy + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
    {
        if(maxExtract <= 0) return 0;

        int energy = get(container);

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if(!simulate) set(container, energy - energyExtracted);
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container)
    {
        return get(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container)
    {
        return capacity;
    }
}
