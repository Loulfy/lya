package io.lylix.lya.chunkloader;

import io.lylix.lya.LYA;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    private static Set<IChunkLoader> tiles = new HashSet<>();

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world)
    {
        LYA.logger.info(world.getWorldInfo().getWorldName());
        for(Ticket ticket : tickets)
        {
            ticket.setChunkListDepth(49);
            LYA.logger.info(ticket.getMaxChunkListDepth());
            LYA.logger.info("TICKET LOADED!");

            int x = ticket.getModData().getInteger("x");
            int y = ticket.getModData().getInteger("y");
            int z = ticket.getModData().getInteger("z");

            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

            LYA.logger.info(new BlockPos(x, y, z));

            if(tileEntity instanceof IChunkLoader)
            {
                IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
                tile.getChunkLoader().refreshChunkSet();
                tile.getChunkLoader().refreshPresence();
                tile.getChunkLoader().setTicket(ticket);

                tiles.add(tile);
            }
        }
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.PlaceEvent event)
    {
        TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
        if(tileEntity instanceof IChunkLoader)
        {
            LYA.logger.info("PLACED CHUNKY");

            IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
            tiles.add(tile);
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event)
    {
        TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
        if(tileEntity instanceof IChunkLoader)
        {
            LYA.logger.info("BROKE CHUNKY");

            IChunkLoader tile = IChunkLoader.class.cast(tileEntity);
            tiles.remove(tile);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        LYA.logger.info("PLAYER LOGIN");
        tiles.stream().filter(te -> te.getChunkLoader().contains(e.player.getGameProfile())).forEach(te -> te.getChunkLoader().login(e.player));
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedOutEvent e)
    {
        LYA.logger.info("PLAYER LOGOUT");
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
