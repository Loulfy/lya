package io.lylix.lya;

import io.lylix.lya.block.*;
import io.lylix.lya.multiblock.heater.*;

public class LYABlocks
{
    public static final BlockChunk CHUNK = new BlockChunk();

    public static final HeaterBlockWall HEATER_WALL = new HeaterBlockWall();
    public static final HeaterBlockMain HEATER_MAIN = new HeaterBlockMain();
    public static final HeaterBlockCore HEATER_CORE = new HeaterBlockCore();
    public static final HeaterBlockPort HEATER_HEAT = new HeaterBlockPort(HeaterBlockType.Heat);
    public static final HeaterBlockPort HEATER_FUEL = new HeaterBlockPort(HeaterBlockType.Fuel);
    public static final HeaterBlockPort HEATER_INFO = new HeaterBlockPort(HeaterBlockType.Info);
}
