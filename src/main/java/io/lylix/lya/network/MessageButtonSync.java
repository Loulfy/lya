package io.lylix.lya.network;

import io.lylix.lya.tile.TileNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageButtonSync implements IMessage, IMessageHandler<MessageButtonSync, IMessage>
{
    private int id;
    private int btn;
    private BlockPos pos;

    public MessageButtonSync() {}

    public MessageButtonSync(BlockPos pos, int button, int id)
    {
        this.pos = pos;
        this.btn = button;
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        id = buf.readInt();
        btn = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeInt(btn);
        buf.writeLong(pos.toLong());
    }

    @Override
    public IMessage onMessage(MessageButtonSync message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    private void handle(MessageButtonSync msg, MessageContext ctx)
    {
        World world = ctx.getServerHandler().player.getEntityWorld();
        if(world.isBlockLoaded(msg.pos))
        {
            TileEntity te = world.getTileEntity(msg.pos);
            if(te instanceof TileNode)
            {
                TileNode node = TileNode.class.cast(te);
                switch(msg.btn)
                {
                    case 0:
                        node.toggleSignalDirection(msg.id);
                        break;
                    case 1:
                        node.toggleSignalConnected(msg.id);
                        break;
                }
            }
        }
    }
}
