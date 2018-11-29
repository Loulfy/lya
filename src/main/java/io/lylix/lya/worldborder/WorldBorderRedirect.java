package io.lylix.lya.worldborder;

import io.lylix.lya.LYA;
import io.lylix.lya.util.TeleportToSpawn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class WorldBorderRedirect
{
    @SubscribeEvent
    public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        World world = DimensionManager.getWorld(event.toDim);
        WorldBorder border = world.getWorldBorder();
        if(border.getClosestDistance(event.player) < 0)
        {
            LYA.logger.debug("WorldBorderRedirect : {} from {} to {}", event.player.getName(), event.fromDim, event.toDim);
            MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
            s.getPlayerList().transferPlayerToDimension((EntityPlayerMP) event.player, event.toDim, new TeleportToSpawn());
        }
    }
}
