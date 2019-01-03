package io.lylix.lya.tile;

import io.lylix.lya.LYA;
import io.lylix.lya.integration.capability.Capabilities;
import io.lylix.lya.integration.capability.Heater;
import io.lylix.lya.util.FluidHandlerItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileHeater extends TileEntity implements ITickable
{
    private static final int BURN = LYA.instance.config.fuelPerTick;

    private boolean enabled = true;
    private boolean burning = false;
    public EnumFacing facing = EnumFacing.NORTH;

    private ItemStackHandler buck = new ItemStackHandler(2)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(slot == 1) return stack;
            FluidStack f = FluidUtil.getFluidContained(stack);
            if(f != null && (f.getFluid().getName().equals("water"))||f.getFluid().getName().equals("biodiesel")) return super.insertItem(slot, stack, simulate);
            return stack;
        }
    };

    private void handleInventory()
    {
        if(!buck.getStackInSlot(0).isEmpty() && FluidHandlerItem.isFluidContainer(buck.getStackInSlot(0)))
        {
            FluidHandlerItem.fill(this, buck, 0, 1);
        }
    }

    private FluidTank tank = new FluidTank(10000)
    {
        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            return fluid.getFluid().getName().equals("biodiesel") || FluidRegistry.getFluid("water") == fluid.getFluid();
        }
    };

    private Heater heater = new Heater(this);

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == Capabilities.HEAT) return true;
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == Capabilities.HEAT) return Capabilities.HEAT.cast(heater);
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(buck);
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        return super.getCapability(capability, facing);
    }

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            handleInventory();

            FluidStack s = tank.drain(BURN, false);
            if (enabled && s != null && s.amount == BURN)
            {
                tank.drain(BURN, true);
                setBurning(true);

                heater.transferHeatTo(100);
            }
            else setBurning(false);

            heater.simulateHeat();
            heater.applyTemperatureChange();
            synchronise();
        }
    }

    public void synchronise()
    {
        if(!world.isRemote)
        {
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public double getTemp()
    {
        return heater.getTemp();
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setBurning(boolean burning)
    {
        if(this.burning == burning) return;

        this.burning = burning;
        synchronise();
    }

    public boolean isBurning()
    {
        return burning;
    }

    private void write(NBTTagCompound nbtTags)
    {
        nbtTags.setBoolean("enabled", enabled);
        nbtTags.setBoolean("burning", burning);
        nbtTags.setInteger("facing", facing.getIndex());
        nbtTags.setDouble("temperature", heater.getTemp());
        nbtTags.setTag("items", buck.serializeNBT());
        tank.writeToNBT(nbtTags);
    }

    private void read(NBTTagCompound nbtTags)
    {
        if(nbtTags.hasKey("enabled")) enabled = nbtTags.getBoolean("enabled");
        if(nbtTags.hasKey("burning")) burning = nbtTags.getBoolean("burning");
        if(nbtTags.hasKey("facing")) facing = EnumFacing.getFront(nbtTags.getInteger("facing"));
        if(nbtTags.hasKey("temperature")) heater.setTemp(nbtTags.getDouble("temperature"));
        if(nbtTags.hasKey("items")) buck.deserializeNBT(NBTTagCompound.class.cast(nbtTags.getTag("items")));
        tank.readFromNBT(nbtTags);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        read(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        write(compound);
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.write(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.read(pkt.getNbtCompound());
        if(world.isRemote) world.markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    public FluidTank getTank()
    {
        return tank;
    }
}
