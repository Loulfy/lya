package io.lylix.lya.proxy;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.lylix.lya.LYA;
import io.lylix.lya.item.ItemBase;
import io.lylix.lya.block.BlockBase;
import io.lylix.lya.render.IModelRegister;
import io.lylix.lya.render.Renderer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Map;

public class ClientProxy extends CommonProxy implements IModelRegister
{
    //public static final String[] PLAYER_INFO = new String[]{"d", "field_175157_a", "playerInfo"};

    //public static final String[] PLAYER_TEXTURES = new String[]{"a", "field_187107_a", "playerTextures"};
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

    /*@SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Post event)
    {
        AbstractClientPlayer p = (AbstractClientPlayer) event.getEntityPlayer();
        if(p.hasPlayerInfo())
        {
            NetworkPlayerInfo info = ReflectionHelper.getPrivateValue(AbstractClientPlayer.class, p, PLAYER_INFO);
            Map<MinecraftProfileTexture.Type, ResourceLocation> textures = ReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, info, PLAYER_TEXTURES);
            ResourceLocation loc = new ResourceLocation("lya", "textures/masterpiece.png");
            textures.put(MinecraftProfileTexture.Type.CAPE, loc);
        }
    }*/

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
