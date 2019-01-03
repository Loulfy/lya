package io.lylix.lya.multiblock.heater;

import io.lylix.lya.LYA;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.rectangular.RectangularMultiblockControllerBase;
import it.zerono.mods.zerocore.api.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.block.ModTileEntity;
import mekanism.api.IHeatTransfer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.HashSet;
import java.util.Set;

public class HeaterController extends RectangularMultiblockControllerBase
{
    private boolean active;

    private DataHeater content;
    private TileHeaterMain main;

    private Set<TileHeater> portSet;
    private int core;

    public HeaterController(World world)
    {
        super(world);
        core = 0;
        active = false;
        portSet = new HashSet<>();

        content = new DataHeater()
        {
            @Override
            public void onContentsChanged()
            {
                HeaterController.this.synchronize();
            }

            @Override
            public boolean isReady()
            {
                return isAssembled();
            }
        };
    }

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data)
    {

    }

    @Override
    protected void onBlockAdded(IMultiblockPart part)
    {

    }

    @Override
    protected void onBlockRemoved(IMultiblockPart part)
    {

    }

    @Override
    protected void onMachineAssembled()
    {
        if(WORLD.isRemote) this.markMultiblockForRenderUpdate();
        this.buildMachine();
    }

    @Override
    protected void onMachineRestored()
    {
        if(WORLD.isRemote) this.markMultiblockForRenderUpdate();
        this.buildMachine();
    }

    @Override
    protected void onMachinePaused()
    {
        if(WORLD.isRemote) this.markMultiblockForRenderUpdate();
        else this.markReferenceCoordDirty();
    }

    @Override
    protected void onMachineDisassembled()
    {
        if(WORLD.isRemote) this.markMultiblockForRenderUpdate();
        else main.saveInternalData();
    }

    @Override
    protected int getMinimumNumberOfBlocksForAssembledMachine() {
        return 27;
    }

    @Override
    protected int getMaximumXSize() {
        return 5;
    }

    @Override
    protected int getMaximumZSize() {
        return 5;
    }

    @Override
    protected int getMaximumYSize() {
        return 5;
    }

    @Override
    protected void onAssimilate(MultiblockControllerBase multiblockControllerBase)
    {

    }

    @Override
    protected void onAssimilated(MultiblockControllerBase multiblockControllerBase)
    {

    }

    @Override
    protected boolean updateServer()
    {
        int fuel = getConsumption();
        FluidStack s = this.getTankHandler().drain(fuel, false);
        if(this.isOn() && s != null && s.amount == fuel)
        {
            this.getTankHandler().drain(fuel, true);
            setActive(true);

            this.getHeatHandler().transferHeatTo(Math.pow(fuel,LYA.instance.config.powHeatCoef)*LYA.instance.config.getLiquidFuelPower(s.getFluid().getName()));
        }
        else
        {
            setActive(false);
        }

        this.portSet.forEach(port -> content.heat.simulateHeat(port, port.getPartPosition().getFacing()));

        if(content.heat.applyTemperatureChange() > 0) synchronize();

        main.handleInventory();

        return false;
    }

    @Override
    protected void updateClient()
    {

    }

    @Override
    protected boolean isBlockGoodForFrame(World world, int x, int y, int z, IMultiblockValidator validator)
    {
        validator.setLastError("lya:api.multiblock.validation.invalid_block", x, y, z);
        return false;
    }

    @Override
    protected boolean isBlockGoodForTop(World world, int x, int y, int z, IMultiblockValidator validator)
    {
        validator.setLastError("lya:api.multiblock.validation.invalid_block", x, y, z);
        return false;
    }

    @Override
    protected boolean isBlockGoodForBottom(World world, int x, int y, int z, IMultiblockValidator validator)
    {
        validator.setLastError("lya:api.multiblock.validation.invalid_block", x, y, z);
        return false;
    }

    @Override
    protected boolean isBlockGoodForSides(World world, int x, int y, int z, IMultiblockValidator validator)
    {
        validator.setLastError("lya:api.multiblock.validation.invalid_block", x, y, z);
        return false;
    }

    @Override
    protected boolean isBlockGoodForInterior(World world, int x, int y, int z, IMultiblockValidator validator)
    {
        validator.setLastError("lya:api.multiblock.validation.invalid_block", x, y, z);
        return false;
    }

    @Override
    protected void syncDataFrom(NBTTagCompound data, ModTileEntity.SyncReason syncReason)
    {
        if(data.hasKey("active")) this.setActive(data.getBoolean("active"));
        this.content.deserializeNBT(data);
    }

    @Override
    protected void syncDataTo(NBTTagCompound data, ModTileEntity.SyncReason syncReason)
    {
        data.setBoolean("active", this.isActive());
        this.content.serializeNBT(data);
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {

        if(this.active == active) return;

        // the state was changed, set it
        this.active = active;

        if(WORLD.isRemote)
        {
            // on the client, request a render update
            this.markMultiblockForRenderUpdate();
        }
        else
        {
            // on the server side, request an update to be sent to the client and mark the save delegate as dirty
            this.markReferenceCoordForUpdate();
            this.markReferenceCoordDirty();
        }
    }

    public void synchronize()
    {
        //IBlockState state = WORLD.getBlockState(main.getPos());
        //WORLD.notifyBlockUpdate(main.getPos(), state, state, 3);
        if(isAssembled()) this.markReferenceCoordForUpdate();
    }

    public boolean toggle()
    {
        if(isAssembled()) setOn(!isOn());
        return content.gear;
    }

    public void setOn(boolean state)
    {
        if(content.gear == state) return;
        content.gear = state;
        synchronize();
    }

    public boolean isOn()
    {
        return content.gear;
    }

    public String fuel()
    {
        return getTankHandler().getFluid() == null ? "empty" : getTankHandler().getFluid().getFluid().getName();
    }

    public int core()
    {
        return this.core;
    }

    @Override
    protected boolean isMachineWhole(IMultiblockValidator validator)
    {
        TileHeaterMain main = null;

        if(!super.isMachineWhole(validator)) return false;

        for(IMultiblockPart part : this.connectedParts)
        {
            if(part instanceof TileHeaterMain)
            {
                if(main != null)
                {

                    validator.setLastError("lya:api.multiblock.validation.main_already_present");
                    return false;
                }

                main = TileHeaterMain.class.cast(part);
            }

        }

        if(main == null)
        {

            validator.setLastError("lya:api.multiblock.validation.main_missing");
            return false;
        }

        return true;
    }

    private void buildMachine()
    {
        this.core = 0;
        this.portSet.clear();

        for(IMultiblockPart part : this.connectedParts)
        {
            if(part instanceof TileHeaterHeat)
            {
                this.portSet.add(TileHeater.class.cast(part));
            }
            else if(part instanceof TileHeaterMain)
            {
                this.main = TileHeaterMain.class.cast(part);
            }
            else if(part instanceof TileHeaterCore)
            {
                this.core+=1;
            }
        }

        if(main.getInternalData().isDirty()) this.content.assimilate(main.getInternalData());
        this.synchronize();
    }

    public FluidTank getTankHandler()
    {
        return this.content.tank;
    }

    public IHeatTransfer getHeatHandler()
    {
        return this.content.heat;
    }

    public int getConsumption()
    {
        return core * LYA.instance.config.fuelPerTick;
    }

    public DataHeater content()
    {
        return this.content;
    }
}
