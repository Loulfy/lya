package io.lylix.lya;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LYAConfig
{
    public int consume = 1000;
    public int capacity = 10000000;
    public int transfer = 50000;

    public int fuelPerTick = 10;
    public float powHeatCoef = 1.4f;

    private Map<String,Float> fuelHeatPowerMap = new HashMap<>();
    private String[] fuelMap = {"lava:1","diesel:4","biodiesel:2"};

    public double JOULES = 2.5;

    private boolean enableChunky = true;
    private boolean enableHeater = true;

    private final static String CHUNKLOADER = "chunkloader";
    private final static String HEATER = "heater";

    private final static String MK = "mekanism";
    private final static String ZC = "zerocore";
    private final static String PR = "projectred-transmission";
    private final static String EI = "enderioconduits";

    private Configuration cfg;
    private File dir;

    public LYAConfig(FMLPreInitializationEvent e)
    {
        dir = e.getModConfigurationDirectory();
        cfg = new Configuration(e.getSuggestedConfigurationFile());

        loadConfig();

        // Hook Mekanism Config : "JoulesToForge"
        if(Loader.isModLoaded(MK)) JOULES = getModConfig(MK).get("general", "JoulesToForge", JOULES).getDouble();
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
        powHeatCoef = cfg.getFloat("powHeatCoef", HEATER, powHeatCoef, 1, 2, "Power heat coefficient");

        fuelMap = cfg.getStringList("fuelHeatMap", HEATER, fuelMap, "Liquid fuel heat power map");
        parseLiquidFuelPower();


        // enable
        enableChunky = cfg.getBoolean("enable", CHUNKLOADER, enableChunky, "Enable this addon");
        enableHeater = cfg.getBoolean("enable", HEATER, enableHeater, "Enable this addon");

        if(cfg.hasChanged()) cfg.save();
    }

    private Configuration getModConfig(String modid)
    {
        Configuration cfg = new Configuration(new File(dir, modid+".cfg"));
        cfg.load();
        return cfg;
    }

    private void parseLiquidFuelPower()
    {
        for(String s : fuelMap)
        {
            String[] m = s.split(":");
            if(m.length < 2) continue;

            try
            {
                fuelHeatPowerMap.put(m[0], Float.valueOf(m[1]));
            }
            catch(NumberFormatException ignored)
            {
                LYA.logger.warn("Config Error: Number Format Exception from {}", s);
            }
        }
    }

    public float getLiquidFuelPower(String name)
    {
        return fuelHeatPowerMap.getOrDefault(name, 0.f);
    }

    public boolean isEnableChunky()
    {
        return enableChunky;
    }

    public boolean isEnableHeater()
    {
        return enableHeater && Loader.isModLoaded(MK) && Loader.isModLoaded(ZC);
    }

    public boolean isEnableBundle()
    {
        return Loader.isModLoaded(PR) && Loader.isModLoaded(EI);
    }

    public static boolean isEnable(String mod)
    {
        if(mod.equals("bundled")) return LYA.instance.config.isEnableBundle();
        return Loader.isModLoaded(mod) || mod.equals("lya");
    }
}
