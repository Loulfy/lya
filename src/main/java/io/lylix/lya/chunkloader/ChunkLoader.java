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

    public ChunkLoader(TileEntity tile)
    {
        tileEntity = tile;
    }

    public void setTicket(Ticket t)
    {
        if(chunkTicket != t && chunkTicket != null && chunkTicket.world == tileEntity.getWorld())
        {
            for(ChunkPos chunk : chunkTicket.getChunkList())
            {
                if(ForgeChunkManager.getPersistentChunksFor(tileEntity.getWorld()).keys().contains(chunk))
                {
                    LYA.logger.info("unforce chunks");
                    ForgeChunkManager.unforceChunk(chunkTicket, chunk);
                }
            }

            LYA.logger.info("release ticket");
            ForgeChunkManager.releaseTicket(chunkTicket);
        }

        chunkTicket = t;
    }

    public void release()
    {
        setTicket(null);
    }

    public void sortChunks()
    {
        if(chunkTicket != null && canOperate() && IChunkLoader.class.cast(tileEntity).getState())
        {
            for(ChunkPos chunk : chunkTicket.getChunkList())
            {
                if(!chunkSet.contains(chunk))
                {
                    if(ForgeChunkManager.getPersistentChunksFor(tileEntity.getWorld()).keys().contains(chunk))
                    {
                        ForgeChunkManager.unforceChunk(chunkTicket, chunk);
                        LYA.logger.info("update chunk : unforce {}", chunk);
                    }
                }
            }

            for(ChunkPos chunk : chunkSet)
            {
                if(!chunkTicket.getChunkList().contains(chunk))
                {
                    ForgeChunkManager.forceChunk(chunkTicket, chunk);
                    LYA.logger.info("update chunk : force {}", chunk);
                }
            }
        }
    }

    public void refreshChunkSet()
    {
        IChunkLoader loader = IChunkLoader.class.cast(tileEntity);

        if(!chunkSet.equals(loader.getChunkSet()))
        {
            LYA.logger.info("Refresh chunk set");
            chunkSet = loader.getChunkSet();
            LYA.logger.info("Get {} Chunk", chunkSet.size());
            sortChunks();
        }
    }

    public void forceChunks(Ticket ticket)
    {
        setTicket(ticket);

        LYA.logger.info("force chunks ({})", chunkSet.size());
        for(ChunkPos chunk : chunkSet)
        {
            ForgeChunkManager.forceChunk(chunkTicket, chunk);
        }
    }

    public void forceChunks()
    {
        LYA.logger.info("force chunks ({})", chunkSet.size());
        for(ChunkPos chunk : chunkSet)
        {
            ForgeChunkManager.forceChunk(chunkTicket, chunk);
        }
    }

    public void unforceChunks()
    {
        LYA.logger.info("unforce chunks ({})", chunkTicket.getChunkList().size());
        for(ChunkPos chunk : chunkTicket.getChunkList())
        {
            ForgeChunkManager.unforceChunk(chunkTicket, chunk);
        }
    }

    public boolean canOperate()
    {
        return online.size() > 0;
    }

    public void tick()
    {
        if(!tileEntity.getWorld().isRemote)
        {
            refreshChunkSet();

            if(chunkTicket == null)
            {
                LYA.logger.info("create ticket");
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
    }

    public void invalidate()
    {
        if(!tileEntity.getWorld().isRemote)
        {
            release();
        }
    }

    public void update()
    {
        IChunkLoader tile = IChunkLoader.class.cast(tileEntity);

        LYA.logger.info("STATE:{}",tile.getState());
        LYA.logger.info("OPERA:{}",canOperate());

        if(tile.getState() && canOperate()) this.forceChunks();
        else this.unforceChunks();
    }

    public boolean refreshPresence()
    {
        IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
        if(!owner.equals(tile.getPresences()) && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            LYA.logger.info("refresh presence");
            owner = tile.getPresences();
            online = LYAUtils.getOnlinePlayerUUID().stream().filter(owner::contains).collect(Collectors.toSet());
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
}
