package io.lylix.lya;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LYAConfig
{
    public int consume = 1000;
    public int capacity = 10000000;
    public int transfer = 50000;

    public int fuelPerTick = 10;
    public int heatPerFuel = 100;

    public String[] blacklist = {"net.minecraft.block.BlockGrass", "net.minecraft.block.BlockFarmland"};

    public double JOULES = 2.5;

    private Map<Integer, BlockPos> warp = new HashMap<>();

    private boolean enableChunky = false;
    private boolean enableHeater = false;
    private boolean enableBorder = false;

    private final static String CHUNKLOADER = "chunkloader";
    private final static String HEATER = "heater";
    private final static String LAGGOGGLES = "laggoggles";
    private final static String WBREDIRECT = "wb-redirect";

    private Configuration cfg;
    private File dir;

    public LYAConfig(FMLPreInitializationEvent e)
    {
        dir = e.getModConfigurationDirectory();
        cfg = new Configuration(e.getSuggestedConfigurationFile());

        loadSpawn();
        loadConfig();

        // Hook Mekanism Config : "JoulesToForge"
        JOULES = getModConfig("mekanism").get("general", "JoulesToForge", JOULES).getDouble();
    }

    public void loadConfig()
    {
        cfg.load();

        // chunkloader
        consume = cfg.getInt("consume", CHUNKLOADER, consume, 100, 10000, "Energy multiplier consumed per tick");
        capacity = cfg.getInt("capacity", CHUNKLOADER, capacity, 100000, 10000000, "Internal capacity of the chunkloader");
        transfer = cfg.getInt("transfer", CHUNKLOADER, transfer, 100, 100000, "Max transferred energy per tick");

        // heater
        fuelPerTick = cfg.getInt("fuelPerTick", HEATER, fuelPerTick, 1, 100, "Fuel burned per tick");
        heatPerFuel = cfg.getInt("heatPerFuel", HEATER, heatPerFuel, 1, 100, "Heat generated per fuel");

        // laggoggles
        blacklist = cfg.getStringList("blacklist", LAGGOGGLES, blacklist, "Classes blacklisted in the result scan of a chunk loader");

        // enable
        enableChunky = cfg.getBoolean("enable", CHUNKLOADER, enableChunky, "Enable this addon");
        enableHeater = cfg.getBoolean("enable", HEATER, enableHeater, "Enable this addon");
        enableBorder = cfg.getBoolean("enable", WBREDIRECT, enableHeater, "Enable this addon");

        if(cfg.hasChanged()) cfg.save();
    }

    private Configuration getModConfig(String modid)
    {
        Configuration cfg = new Configuration(new File(dir, modid+".cfg"));
        cfg.load();
        return cfg;
    }

    private void loadSpawn()
    {
        File file = new File(dir, "lya-spawn.txt");
        Properties props = new Properties();

        try
        {
            if(file.createNewFile()) LYA.logger.warn("create spawn file");
            props.load(new FileInputStream(file));
        }
        catch (IOException e)
        {
            LYA.logger.warn("can not parse spawn file");
        }

        for(String key : props.stringPropertyNames())
        {
            try
            {
                final String[] coords = props.getProperty(key).split(",");
                if(coords.length < 3) continue;


                int d = Integer.valueOf(key);
                int x = Integer.valueOf(coords[0]);
                int y = Integer.valueOf(coords[1]);
                int z = Integer.valueOf(coords[2]);

                BlockPos p = new BlockPos(x,y,z);
                LYA.logger.info("register spawn : {} to {}", d, p);
                warp.put(d, p);
            }
            catch(NumberFormatException e)
            {
                LYA.logger.warn("can not completely parse the spawn file : {}", props.getProperty(key));
            }
        }
    }

    public BlockPos getSpawn(World world)
    {
        return warp.getOrDefault(world.provider.getDimension(), world.getSpawnPoint());
    }

    public boolean isEnableChunky()
    {
        return enableChunky;
    }

    public boolean isEnableHeater()
    {
        return enableHeater && Loader.isModLoaded("mekanism");
    }

    public boolean isEnableBorder()
    {
        return enableBorder;
    }
}
