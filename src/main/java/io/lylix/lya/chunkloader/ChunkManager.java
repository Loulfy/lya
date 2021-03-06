package io.lylix.lya.chunkloader;

import io.lylix.lya.LYA;
import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;
import java.util.stream.Collectors;

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
            LYA.logger.info("PLACED CHUNKY");

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

    public void remap(TileEntity newTe)
    {
        if(newTe instanceof IChunkLoader)
        {
            for(IChunkLoader tile : tiles)
            {
                TileEntity oldTe = tile.getChunkLoader().getTileEntity();

                if(oldTe.getWorld().provider.getDimension() == newTe.getWorld().provider.getDimension() && oldTe.getPos() == newTe.getPos())
                {
                    if(oldTe == newTe) LYA.logger.debug("No issue with ChunkLoader " + tile.getChunkLoader().printPos());
                    else
                    {
                        LYA.logger.warn("Patch ChunkLoader " + tile.getChunkLoader().printPos());
                        tiles.remove(tile);

                        IChunkLoader cl = IChunkLoader.class.cast(newTe);
                        cl.getChunkLoader().refreshChunkSet();
                        cl.getChunkLoader().refreshPresence();
                        cl.getChunkLoader().setTicket(tile.getChunkLoader().getChunkTicket());

                        tiles.add(cl);
                    }
                    break;
                }
            }
        }
    }

    public void remap(ICommandSender sender)
    {
        for(IChunkLoader tile : tiles)
        {
            ChunkLoader cl = tile.getChunkLoader();

            TileEntity ote = cl.getTileEntity();
            World world = ote.getWorld();

            if(world == null) sender.sendMessage(new TextComponentString(cl.stringifyPos()+" : Error no world in this tile"));
            else
            {
                TileEntity nte = world.getTileEntity(ote.getPos());
                if(nte == ote) sender.sendMessage(new TextComponentString(cl.stringifyPos()+" : Same tile, no issue"));
                else
                {
                    sender.sendMessage(new TextComponentString(cl.stringifyPos()+" : REMAP"));

                    tiles.remove(tile);

                    if(nte instanceof IChunkLoader)
                    {
                        IChunkLoader ncl = IChunkLoader.class.cast(nte);

                        ncl.getChunkLoader().refreshChunkSet();
                        ncl.getChunkLoader().refreshPresence();
                        ncl.getChunkLoader().setTicket(cl.getChunkTicket());

                        tiles.add(ncl);
                    }
                }
            }
        }
    }
}
