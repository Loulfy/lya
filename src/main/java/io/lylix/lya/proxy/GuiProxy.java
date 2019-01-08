package io.lylix.lya.proxy;

import io.lylix.lya.LYA;
import io.lylix.lya.container.ContainerHeater;
import io.lylix.lya.container.ContainerNode;
import io.lylix.lya.gui.GuiChunk;
import io.lylix.lya.gui.GuiHeater;
import io.lylix.lya.gui.GuiMultiblockHeater;
import io.lylix.lya.gui.GuiNode;
import io.lylix.lya.multiblock.heater.TileHeaterMain;
import io.lylix.lya.tile.TileChunk;
import io.lylix.lya.container.ContainerChunk;
import io.lylix.lya.tile.TileHeater;
import io.lylix.lya.tile.TileNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiProxy implements IGuiHandler
{

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if(te instanceof TileChunk)
        {
            TileChunk tile = TileChunk.class.cast(te);
            return new ContainerChunk(player.inventory, tile);
        }
        if(te instanceof TileHeater)
        {
            TileHeater tile = TileHeater.class.cast(te);
            return new ContainerHeater(player.inventory, tile);
        }
        if(te instanceof TileHeaterMain)
        {
            TileHeaterMain tile = TileHeaterMain.class.cast(te);
            return new ContainerHeater(player.inventory, tile);
        }
        if(te instanceof TileNode)
        {
            TileNode tile = TileNode.class.cast(te);
            return new ContainerNode(player.inventory, tile);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if(te instanceof TileChunk)
        {
            TileChunk tile = TileChunk.class.cast(te);
            LYA.logger.debug("OPEN GUI CHUNK");
            return new GuiChunk(new ContainerChunk(player.inventory, tile), tile);
        }
        if(te instanceof TileHeater)
        {
            TileHeater tile = TileHeater.class.cast(te);
            return new GuiHeater(new ContainerHeater(player.inventory, tile), tile);
        }
        if(te instanceof TileHeaterMain)
        {
            TileHeaterMain tile = TileHeaterMain.class.cast(te);
            return new GuiMultiblockHeater(new ContainerHeater(player.inventory, tile), tile);
        }
        if(te instanceof TileNode)
        {
            TileNode tile = TileNode.class.cast(te);
            return new GuiNode(new ContainerNode(player.inventory, tile), tile);
        }

        return null;
    }
}