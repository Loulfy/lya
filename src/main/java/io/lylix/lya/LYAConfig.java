package io.lylix.lya;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class LYAConfig
{
    public int consume = 1000;
    public int capacity = 10000000;
    public int transfer = 50000;

    public int fuelPerTick = 10;
    public int heatPerFuel = 100;

    public String[] blacklist = {"net.minecraft.block.BlockGrass", "net.minecraft.block.BlockFarmland"};

    public double JOULES = 2.5;

    private final static String CHUNKLOADER = "chunkloader";
    private final static String HEATER = "heater";
    private final static String LAGGOGGLES = "laggoggles";

    private Configuration cfg;
    private File dir;

    public LYAConfig(FMLPreInitializationEvent e)
    {
        dir = e.getModConfigurationDirectory();
        cfg = new Configuration(e.getSuggestedConfigurationFile());

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

        if(cfg.hasChanged()) cfg.save();
    }

    private Configuration getModConfig(String modid)
    {
        Configuration cfg = new Configuration(new File(dir, modid+".cfg"));
        cfg.load();
        return cfg;
    }
}
