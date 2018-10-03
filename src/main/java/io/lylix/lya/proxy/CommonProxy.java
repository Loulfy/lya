package io.lylix.lya.proxy;

import io.lylix.lya.LYA;
import io.lylix.lya.LYAItems;
import io.lylix.lya.LYABlocks;
import io.lylix.lya.network.*;
import io.lylix.lya.tile.TileChunk;
import io.lylix.lya.block.BlockBase;
import io.lylix.lya.render.Renderer;
import io.lylix.lya.chunkloader.ChunkManager;
import io.lylix.lya.tile.TileHeater;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;
import java.util.LinkedList;

public class CommonProxy
{
    protected List<Item> items = new LinkedList<>();
    protected List<BlockBase> blocks = new LinkedList<>();

    private ChunkManager manager;

    public void preInit(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);

        LYA.instance.network.registerMessage(MessageChunkSync.class, MessageChunkSync.class, 0, Side.SERVER);

        registerBlock(LYABlocks.CHUNK, "chunkloader");
        registerItem(LYAItems.ID_CARD, "id_card");

        if(Loader.isModLoaded("mekanism")) registerBlock(LYABlocks.HEATER, "heater");
    }

    public void init(FMLInitializationEvent e)
    {
        manager = new ChunkManager();
        ForgeChunkManager.setForcedChunkLoadingCallback(LYA.instance, manager);
        ForgeChunkManager.getConfig().getInt("maximumChunksPerTicket", LYA.ID, 50, 50, 50, "");

        NetworkRegistry.INSTANCE.registerGuiHandler(LYA.instance, new GuiProxy());
        GameRegistry.registerTileEntity(TileChunk.class, "Chunkloader");

        if(Loader.isModLoaded("mekanism")) GameRegistry.registerTileEntity(TileHeater.class, "Heater");
    }

    public void postInit(FMLPostInitializationEvent e)
    {

    }

    public Renderer getRenderer()
    {
        // Client Proxy Only
        return null;
    }

    public ChunkManager getChunkManager()
    {
        return manager;
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e)
    {
        blocks.forEach(e.getRegistry()::register);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        items.forEach(event.getRegistry()::register);
    }

    private void registerBlock(BlockBase block, String id)
    {
        block.setUnlocalizedName(id).setRegistryName(LYA.ID+":"+id);
        blocks.add(block);
        registerItem(block.createItem(), id);
    }

    private void registerItem(Item item, String id)
    {
        item.setUnlocalizedName(id).setRegistryName(LYA.ID+":"+id);
        items.add(item);
    }

    public void clean()
    {
        manager.clean();
    }
}
