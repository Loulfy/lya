package io.lylix.lya.chunkloader;

import io.lylix.lya.LYA;
import io.lylix.lya.util.LYAUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import com.mojang.authlib.GameProfile;

import java.util.*;
import java.util.stream.Collectors;

public class ChunkLoader
{
    private TileEntity tileEntity;

    private Ticket chunkTicket = null;

    private Set<ChunkPos> chunkSet = new HashSet<>();

    private Set<UUID> owner = new HashSet<>();
    private Set<UUID> online = new HashSet<>();

    private boolean state = false;

    public ChunkLoader(TileEntity tile)
    {
        tileEntity = tile;
    }

    public void setTicket(Ticket t)
    {
        LYA.logger.debug("set ticket");
        if(chunkTicket != t && chunkTicket != null && chunkTicket.world == tileEntity.getWorld())
        {
            for(ChunkPos chunk : chunkTicket.getChunkList())
            {
                if(ForgeChunkManager.getPersistentChunksFor(tileEntity.getWorld()).keys().contains(chunk))
                {
                    LYA.logger.debug("unforce chunk {}", chunk);
                    ForgeChunkManager.unforceChunk(chunkTicket, chunk);
                }
            }

            LYA.logger.debug("release ticket");
            ForgeChunkManager.releaseTicket(chunkTicket);
        }

        if(chunkTicket == null) LYA.logger.debug("the current ticket is null");
        chunkTicket = t;
    }

    public void release()
    {
        setTicket(null);
    }

    public void sortChunks()
    {
        if(chunkTicket != null && canOperate())
        {
            for(ChunkPos chunk : chunkTicket.getChunkList())
            {
                if(!chunkSet.contains(chunk))
                {
                    if(ForgeChunkManager.getPersistentChunksFor(tileEntity.getWorld()).keys().contains(chunk))
                    {
                        ForgeChunkManager.unforceChunk(chunkTicket, chunk);
                        LYA.logger.debug("update chunk : unforce {}", chunk);
                    }
                }
            }

            for(ChunkPos chunk : chunkSet)
            {
                if(!chunkTicket.getChunkList().contains(chunk))
                {
                    ForgeChunkManager.forceChunk(chunkTicket, chunk);
                    LYA.logger.debug("update chunk : force {}", chunk);
                }
            }
        }
    }

    public void refreshChunkSet()
    {
        IChunkLoader loader = IChunkLoader.class.cast(tileEntity);

        if(!chunkSet.equals(loader.getChunkSet()))
        {
            chunkSet = loader.getChunkSet();
            LYA.logger.debug("Refresh chunkset (get {} chunk)", chunkSet.size());
            sortChunks();
        }
    }

    public void forceChunks(Ticket ticket)
    {
        setTicket(ticket);

        LYA.logger.debug("force chunks ({})", chunkSet.size());
        for(ChunkPos chunk : chunkSet)
        {
            ForgeChunkManager.forceChunk(chunkTicket, chunk);
        }
    }

    public void forceChunks()
    {
        LYA.logger.debug("TRY FORCE {}", this);
        sortChunks();
    }

    public void unforceChunks()
    {
        LYA.logger.debug("TRY UNFORCE {}", this);
        for(ChunkPos chunk : chunkTicket.getChunkList())
        {
            ForgeChunkManager.unforceChunk(chunkTicket, chunk);
            LYA.logger.debug("update chunk : unforce {}", chunk);
        }
    }

    public boolean canOperate()
    {
        return state && online.size() > 0;
    }

    public void tick()
    {
        if(!tileEntity.getWorld().isRemote)
        {
            if(chunkTicket != null)
            {
                IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
                if(state == tile.getState()) return;

                state = tile.getState();
                update();
            }
        }
    }

    public void create()
    {
        if(chunkTicket == null && !tileEntity.getWorld().isRemote)
        {
            refreshChunkSet();
            refreshPresence();

            LYA.logger.debug("create ticket");
            Ticket ticket = ForgeChunkManager.requestTicket(LYA.instance, tileEntity.getWorld(), Type.NORMAL);

            if(ticket != null)
            {
                ticket.getModData().setInteger("x", tileEntity.getPos().getX());
                ticket.getModData().setInteger("y", tileEntity.getPos().getY());
                ticket.getModData().setInteger("z", tileEntity.getPos().getZ());
                ticket.getModData().setInteger("d", tileEntity.getWorld().provider.getDimension());

                setTicket(ticket);
            }
        }
    }

    public void invalidate()
    {
        LYA.logger.debug("INVALIDATE");
        if(!tileEntity.getWorld().isRemote)
        {
            release();
        }
    }

    public void update()
    {
        if(canOperate()) this.forceChunks();
        else this.unforceChunks();
    }

    public boolean refreshPresence()
    {
        IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
        if(!owner.equals(tile.getPresences()) && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            owner = tile.getPresences();
            online = LYAUtils.getOnlinePlayerUUID();
            online.add(ChunkManager.FAKE_PLAYER);
            online = online.stream().filter(owner::contains).collect(Collectors.toSet());
            LYA.logger.debug("Refresh presence (get {} owner)", owner.size());
            return true;
        }
        return false;
    }

    public void login(EntityPlayer player)
    {
        online.add(player.getGameProfile().getId());
        update();
    }

    public void logout(EntityPlayer player)
    {
        online.remove(player.getGameProfile().getId());
        update();
    }

    public boolean contains(GameProfile gp)
    {
        return owner.contains(gp.getId());
    }

    public boolean contains(int dimension) { return tileEntity.getWorld().provider.getDimension() == dimension; }

    public boolean contains(ChunkPos pos, int dimension)
    {
        return contains(dimension) && chunkSet.contains(pos);
    }

    public String stringifyPos()
    {
        BlockPos p = tileEntity.getPos();
        return "{" + p.getX() + ", " + p.getY() + ", " + p.getZ() + "}";
    }

    @Override
    public String toString()
    {
        boolean active = canOperate() && IChunkLoader.class.cast(tileEntity).getState();

        return "Loader : " + stringifyPos() + "; Chunks{" + chunkSet.size() + "}; Owners{" + owner.size() + "}; Active{" + active + "}";
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 17 + tileEntity.getPos().hashCode();
        hash = hash * 31 + tileEntity.getWorld().provider.getDimension();
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ChunkLoader)
        {
            ChunkLoader c = ChunkLoader.class.cast(o);
            return c.tileEntity.getPos().equals(tileEntity.getPos()) && c.tileEntity.getWorld().provider.getDimension() == tileEntity.getWorld().provider.getDimension();
        }
        return false;
    }

    public Ticket getChunkTicket()
    {
        return chunkTicket;
    }

    public TileEntity getTileEntity()
    {
        return tileEntity;
    }
}
