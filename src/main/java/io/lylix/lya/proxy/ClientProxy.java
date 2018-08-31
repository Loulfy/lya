package io.lylix.lya.proxy;

import io.lylix.lya.item.ItemBase;
import io.lylix.lya.block.BlockBase;
import io.lylix.lya.render.IModelRegister;
import io.lylix.lya.render.Renderer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy implements IModelRegister
{
    private Renderer renderer = new Renderer();

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        for (BlockBase block : blocks)
        {
            block.initModel(this);
        }

        for (Item item : items)
        {
            if (item instanceof ItemBase) ItemBase.class.cast(item).initModel(this);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        renderer.render(event.getPartialTicks());
    }


    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if(event.getWorld().isRemote) renderer.clean();
    }

    @Override
    public Renderer getRenderer()
    {
        return renderer;
    }

    @Override
    public void setModel(Block block, int meta, ModelResourceLocation resource)
    {
        setModel(Item.getItemFromBlock(block), meta, resource);
    }

    @Override
    public void setModel(Item item, int meta, ModelResourceLocation resource)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, resource);
    }
}
