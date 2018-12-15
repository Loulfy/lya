package io.lylix.lya.chunkloader;

import io.lylix.lya.LYA;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.border.IBorderListener;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;

@Mod.EventBusSubscriber
public class ChunkManager implements LoadingCallback
{
    public static UUID FAKE_PLAYER = UUID.randomUUID();
    private static Set<IChunkLoader> tiles = new HashSet<>();

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world)
    {
        int dim = world.provider.getDimension();
        for(Ticket ticket : tickets)
        {
            ticket.setChunkListDepth(49);

            int x = ticket.getModData().getInteger("x");
            int y = ticket.getModData().getInteger("y");
            int z = ticket.getModData().getInteger("z");

            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

            if(tileEntity instanceof IChunkLoader)
            {
                LYA.logger.info("TICKET LOADED : World{{}}; {}", dim, tileEntity.getPos());
                IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
                tile.getChunkLoader().refreshChunkSet();
                tile.getChunkLoader().refreshPresence();
                tile.getChunkLoader().setTicket(ticket);

                tiles.add(tile);
            }
            else
            {
                LYA.logger.warn("ONE TICKET WAS PURGE!");
                ForgeChunkManager.releaseTicket(ticket);
            }
        }
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.PlaceEvent event)
    {
        TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
        if(tileEntity instanceof IChunkLoader)
        {
            LYA.logger.debug("PLACED CHUNKY");

            IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
            tile.getChunkLoader().create();
            tiles.add(tile);
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event)
    {
        TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
        if(tileEntity instanceof IChunkLoader)
        {
            LYA.logger.debug("BROKE CHUNKY");

            IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
            tiles.remove(tile);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        LYA.logger.debug("PLAYER LOGIN : {}", e.player.getName());
        tiles.stream().filter(te -> te.getChunkLoader().contains(e.player.getGameProfile())).forEach(te -> te.getChunkLoader().login(e.player));
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedOutEvent e)
    {
        LYA.logger.debug("PLAYER LOGOUT : {}", e.player.getName());
        tiles.stream().filter(te -> te.getChunkLoader().contains(e.player.getGameProfile())).forEach(te -> te.getChunkLoader().logout(e.player));
    }

    public void clean()
    {
        tiles.clear();
    }

    public Set<IChunkLoader> getLoaders()
    {
        return tiles;
    }
}
