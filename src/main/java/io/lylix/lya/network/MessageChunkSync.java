package io.lylix.lya.network;

import io.lylix.lya.LYA;
import io.lylix.lya.multiblock.heater.TileHeaterMain;
import io.lylix.lya.tile.TileChunk;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChunkSync implements IMessage, IMessageHandler<MessageChunkSync, IMessage>
{
    private int id;
    private BlockPos pos;

    public MessageChunkSync() {}

    public MessageChunkSync(BlockPos pos, int id)
    {
        this.id = id;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeLong(pos.toLong());
    }

    @Override
    public IMessage onMessage(MessageChunkSync message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    private void handle(MessageChunkSync msg, MessageContext ctx)
    {
        LYA.logger.debug("Server received msg!");
        World world = ctx.getServerHandler().player.getEntityWorld();
        if(world.isBlockLoaded(msg.pos))
        {
            TileEntity te = world.getTileEntity(msg.pos);
            if(te instanceof TileChunk) TileChunk.class.cast(te).action(msg.id);
            else if(te instanceof TileHeaterMain) TileHeaterMain.class.cast(te).toggle();
        }
    }
}
