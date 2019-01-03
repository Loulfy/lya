package io.lylix.lya.multiblock.heater;

import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockTileEntityBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import net.minecraft.nbt.NBTTagCompound;

public class TileHeater extends RectangularMultiblockTileEntityBase
{
    private final static HeaterController FAKE = new HeaterController(null);
    private final static String ASSEMBLED = "assembled";

    private boolean assembled = false;

    @Override
    public boolean isGoodForFrame(IMultiblockValidator iMultiblockValidator)
    {
        return false;
    }

    @Override
    public boolean isGoodForSides(IMultiblockValidator iMultiblockValidator)
    {
        return false;
    }

    @Override
    public boolean isGoodForTop(IMultiblockValidator iMultiblockValidator)
    {
        return false;
    }

    @Override
    public boolean isGoodForBottom(IMultiblockValidator iMultiblockValidator)
    {
        return false;
    }

    @Override
    public boolean isGoodForInterior(IMultiblockValidator iMultiblockValidator)
    {
        return false;
    }

    @Override
    public void onMachineActivated()
    {
        world.notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
    }

    @Override
    public void onMachineDeactivated()
    {

    }

    @Override
    public MultiblockControllerBase createNewMultiblock()
    {
        return new HeaterController(this.world);
    }

    @Override
    public Class<? extends MultiblockControllerBase> getMultiblockControllerType()
    {
        return HeaterController.class;
    }

    @Override
    protected void syncDataFrom(NBTTagCompound data, SyncReason syncReason)
    {
        if(syncReason == SyncReason.FullSync && data.hasKey(ASSEMBLED)) assembled = data.getBoolean(ASSEMBLED);
        super.syncDataFrom(data, syncReason);
    }

    @Override
    protected void syncDataTo(NBTTagCompound data, SyncReason syncReason)
    {
        if(syncReason == SyncReason.FullSync) data.setBoolean(ASSEMBLED, isMachineAssembled());
        super.syncDataTo(data, syncReason);
    }

    public boolean isAssembled()
    {
        return isMachineAssembled() || assembled;
    }

    public HeaterController controller()
    {
        return isMachineAssembled() ? HeaterController.class.cast(getMultiblockController()) : FAKE;
    }
}
