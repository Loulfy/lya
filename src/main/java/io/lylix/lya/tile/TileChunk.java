package io.lylix.lya.tile;

import io.lylix.lya.LYA;
import io.lylix.lya.LYAItems;
import io.lylix.lya.util.Energy;
import io.lylix.lya.item.ItemIdCard;
import io.lylix.lya.render.Renderer;
import io.lylix.lya.chunkloader.IChunkLoader;
import io.lylix.lya.chunkloader.ChunkLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TileChunk extends TileEntity implements ITickable, IChunkLoader
{
    public boolean enabled = false;
    public EnumFacing facing = EnumFacing.NORTH;

    public final static int SIZE = 8;

    private ItemStackHandler cards = new ItemStackHandler(SIZE)
    {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(stack.getItem() == LYAItems.ID_CARD) return super.insertItem(slot, stack, simulate);
            return stack;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if(!world.isRemote && chunkloader.refreshPresence() && !chunkloader.canOperate()) setEnabled(false);
            TileChunk.this.markDirty();
        }
    };

    private Energy energy = new Energy(LYA.instance.config.capacity, LYA.instance.config.transfer);

    private BitSet chunks;
    private ChunkLoader chunkloader;

    private Renderer renderer;

    public TileChunk()
    {
        chunkloader = new ChunkLoader(this);
        chunks = new BitSet();
        chunks.set(24);
    }

    public void setEnabled(boolean state)
    {
        enabled = state;
        chunkloader.update();
        this.markDirty();
        synchronise();
    }

    public void synchronise()
    {
        if(!world.isRemote)
        {
            IBlockState state = world.getBlockState(getPos());
            world.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public void update()
    {
        if(!world.isRemote)
        {
            chunkloader.tick();

            if(!enabled && chunkloader.canOperate() && energy.getEnergyStored() > energy.getMaxEnergyStored()/2) setEnabled(true);

            if(enabled && chunkloader.canOperate())
            {
                int needed = getEnergyUsage();
                if(energy.extractEnergy(needed, true) == needed) energy.extractEnergy(needed, false);
                else setEnabled(false);
            }
        }
    }

    public void action(int id)
    {
        if(chunks.get(id)) chunks.clear(id);
        else chunks.set(id);

        chunkloader.refreshChunkSet();

        synchronise();
        markDirty();
    }

    private ChunkPos translate(int id)
    {
        // TODO : orientate
        int cx = getPos().getX() >> 4;
        int cz = getPos().getZ() >> 4;
        int xx = id%7;
        int yy = id/7;
        cx-= xx-3;
        cz-= yy-3;
        return new ChunkPos(cx, cz);
    }

    public boolean check(int id)
    {
        return chunks.get(id);
    }

    public int getEnergyUsage() { return chunks.cardinality()*LYA.instance.config.consume; }

    public Energy getEnergy() { return energy; }

    @Override
    public Set<ChunkPos> getChunkSet()
    {
        return chunks.stream().mapToObj(this::translate).collect(Collectors.toSet());
    }

    @Override
    public ChunkLoader getChunkLoader()
    {
        return chunkloader;
    }

    @Override
    public Set<UUID> getPresences()
    {
        Set<UUID> owners = new HashSet<>();
        for (int i = 0; i < cards.getSlots(); ++i)
        {
            ItemStack stack = cards.getStackInSlot(i);

            if (!stack.isEmpty())
            {
                UUID uuid = ItemIdCard.getOwner(stack);

                if (uuid == null) continue;

                owners.add(uuid);
            }
        }
        return owners;
    }

    @Override
    public boolean getState()
    {
        return enabled;
    }

    private void write(NBTTagCompound nbtTags)
    {
        NBTTagList list = new NBTTagList();

        for(byte b : chunks.toByteArray())
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("byte", b);
            list.appendTag(compound);
        }

        nbtTags.setTag("bytes", list);
        nbtTags.setTag("items", cards.serializeNBT());
        nbtTags.setInteger("energy", energy.getEnergyStored());
        nbtTags.setBoolean("enabled", enabled);
        nbtTags.setInteger("facing", facing.getIndex());
    }

    private void read(NBTTagCompound nbtTags)
    {
        NBTTagList list = nbtTags.getTagList("bytes", Constants.NBT.TAG_COMPOUND);

        byte b[] = new byte[list.tagCount()];

        for(int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            b[i] = compound.getByte("byte");
        }

        chunks = BitSet.valueOf(b);
        chunks.set(24);

        if(nbtTags.hasKey("items")) cards.deserializeNBT(NBTTagCompound.class.cast(nbtTags.getTag("items")));
        if(nbtTags.hasKey("energy")) energy.setStored(nbtTags.getInteger("energy"));
        if(nbtTags.hasKey("enabled")) enabled = nbtTags.getBoolean("enabled");
        if(nbtTags.hasKey("facing")) facing = EnumFacing.getFront(nbtTags.getInteger("facing"));
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

    @Override
    public void onLoad()
    {
        if(world.isRemote)
        {
            renderer = LYA.proxy.getRenderer();
            renderer.show(this);
        }
    }

    @Override
    public void onChunkUnload()
    {
        if(world.isRemote) renderer.clear(this);
    }

    @Override
    public void invalidate()
    {
        if(world.isRemote) renderer.clear(this);
        else chunkloader.invalidate();
        super.invalidate();
    }

    public ItemStackHandler getCards()
    {
        return cards;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        if(capability == CapabilityEnergy.ENERGY) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(cards);
        if(capability == CapabilityEnergy.ENERGY) return CapabilityEnergy.ENERGY.cast(energy);
        return super.getCapability(capability, facing);
    }
}
