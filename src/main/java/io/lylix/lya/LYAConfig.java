package io.lylix.lya;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LYAConfig
{
    public int consume = 1000;
    public int capacity = 10000000;
    public int transfer = 40000;

    private final static String CHUNKLOADER = "chunkloader";

    private Configuration cfg;

    public LYAConfig(File file)
    {
        cfg = new Configuration(file);

        loadConfig();
    }

    public void loadConfig()
    {
        cfg.load();

        consume = cfg.getInt("consume", CHUNKLOADER, consume, 100, 10000, "Energy multiplier consumed per tick");
        capacity = cfg.getInt("capacity", CHUNKLOADER, capacity, 100000, 10000000, "Internal capacity of the chunkloader");
        transfer = cfg.getInt("transfer", CHUNKLOADER, transfer, 100, 40000, "Max transferred energy per tick");


        if(cfg.hasChanged()) cfg.save();
    }
}
