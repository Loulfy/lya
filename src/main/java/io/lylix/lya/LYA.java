package io.lylix.lya;

import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import io.lylix.lya.proxy.CommonProxy;
import io.lylix.lya.command.CommandChunk;

@Mod(modid = LYA.ID, name = LYA.NAME, version = "${version}", dependencies = LYA.DEPS)
public class LYA
{
    public static final String ID = "lya";
    public static final String NAME = "Lya";
    public static final String DEPS = "required-after:zerocore;after:mekanism;after:redstoneflux;after:opencomputers;";

    @SidedProxy(clientSide = "io.lylix.lya.proxy.ClientProxy", serverSide = "io.lylix.lya.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static LYA instance;

    public static Logger logger;

    public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

    public LYAConfig config;

    public final CreativeTabs tab = new CreativeTabs(ID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack( config.isEnableHeater() ? LYABlocks.HEATER_MAIN : LYABlocks.CHUNK);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        config = new LYAConfig(event);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("Hello LYA");
        proxy.init(event);
    }

    @EventHandler
    public static void onServerStopped(FMLServerStoppedEvent event)
    {
        LYA.logger.info("Cleaning up");
        proxy.clean();
    }

    @EventHandler
    public static void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandChunk());
    }
}
