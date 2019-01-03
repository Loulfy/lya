package io.lylix.lya.proxy;

import io.lylix.lya.LYA;
import io.lylix.lya.LYAItems;
import io.lylix.lya.LYABlocks;
import io.lylix.lya.integration.LYAHook;
import io.lylix.lya.multiblock.heater.HeaterBlockBase;
import io.lylix.lya.network.*;
import io.lylix.lya.tile.TileChunk;
import io.lylix.lya.block.BlockBase;
import io.lylix.lya.render.Renderer;
import io.lylix.lya.chunkloader.ChunkManager;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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

    private LYAHook hook = new LYAHook();
    private ChunkManager manager;

    public void preInit(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);

        if(LYA.instance.config.isEnableChunky())
        {
            LYA.logger.info("Enable: chunkloader");
            LYA.instance.network.registerMessage(MessageChunkSync.class, MessageChunkSync.class, 0, Side.SERVER);
            registerBlock(LYABlocks.CHUNK, "chunkloader");
            registerItem(LYAItems.ID_CARD, "id_card");
            registerItem(LYAItems.OP_CARD, "op_card");
        }

        if(LYA.instance.config.isEnableHeater())
        {
            LYA.logger.info("Enable: heater");
            registerMultiblock(LYABlocks.HEATER_WALL);
            registerMultiblock(LYABlocks.HEATER_MAIN);
            registerMultiblock(LYABlocks.HEATER_CORE);
            registerMultiblock(LYABlocks.HEATER_HEAT);
            registerMultiblock(LYABlocks.HEATER_FUEL);
            registerMultiblock(LYABlocks.HEATER_INFO);
        }
    }

    public void init(FMLInitializationEvent e)
    {
        manager = new ChunkManager();
        ForgeChunkManager.setForcedChunkLoadingCallback(LYA.instance, manager);
        ForgeChunkManager.getConfig().getInt("maximumChunksPerTicket", LYA.ID, 50, 50, 50, "");

        NetworkRegistry.INSTANCE.registerGuiHandler(LYA.instance, new GuiProxy());

        if(LYA.instance.config.isEnableChunky()) GameRegistry.registerTileEntity(TileChunk.class, "Chunkloader");

        hook.init();
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

    private void registerMultiblock(HeaterBlockBase block)
    {
        registerBlock(block, block.getId());
        GameRegistry.registerTileEntity(block.getTileClass(), block.getId());
    }

    private void registerItem(Item item, String id)
    {
        //item.setUnlocalizedName(id).setRegistryName(LYA.ID+":"+id);
        item.setUnlocalizedName(id).setRegistryName(id);
        items.add(item);
    }

    public void clean()
    {
        manager.clean();
    }
}
