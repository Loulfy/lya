package io.lylix.lya.integration.capability;

import mekanism.api.Coord4D;
import mekanism.api.IHeatTransfer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface="mekanism.api.IHeatTransfer", modid="mekanism")
public class Heater implements IHeatTransfer
{
    private final static String NBT = "temp";

    private TileEntity tile;
    private double temperature;
    private double heatToAbsorb;

    public Heater(TileEntity te)
    {
        tile = te;
        temperature = 0;
        heatToAbsorb = 0;
    }

    public Heater()
    {
        this(null);
    }

    public void setTemp(double t)
    {
        temperature = t;
    }

    @Override
    public double getTemp()
    {
        return temperature;
    }

    @Override
    public double getInverseConductionCoefficient()
    {
        return 5;
    }

    @Override
    public double getInsulationCoefficient(EnumFacing enumFacing)
    {
        return 1000;
    }

    @Override
    public void transferHeatTo(double heat)
    {
        heatToAbsorb += heat;
    }

    @Override
    public double[] simulateHeat()
    {
        return simulate(this);
    }

    @Override
    public double applyTemperatureChange()
    {
        temperature += heatToAbsorb;
        heatToAbsorb = 0;

        return temperature;
    }

    @Override
    public boolean canConnectHeat(EnumFacing enumFacing)
    {
        return true;
    }

    @Override
    public IHeatTransfer getAdjacent(EnumFacing side)
    {
        return getAdjacent(tile, side);
    }

    private IHeatTransfer getAdjacent(TileEntity tile, EnumFacing side)
    {
        TileEntity adj = Coord4D.get(tile).offset(side).getTileEntity(tile.getWorld());

        if(Capabilities.hasCapability(adj, Capabilities.HEAT, side.getOpposite())) return Capabilities.getCapability(adj, Capabilities.HEAT, side.getOpposite());

        return null;
    }

    private static double[] simulate(IHeatTransfer source)
    {
        double heatTransferred[] = new double[] {0, 0};

        for(EnumFacing side : EnumFacing.VALUES)
        {
            IHeatTransfer sink = source.getAdjacent(side);

            if(sink != null)
            {
                double invConduction = sink.getInverseConductionCoefficient() + source.getInverseConductionCoefficient();
                double heatToTransfer = source.getTemp() / invConduction;
                source.transferHeatTo(-heatToTransfer);
                sink.transferHeatTo(heatToTransfer);
                continue;
            }

            //Transfer to air otherwise
            double invConduction = IHeatTransfer.AIR_INVERSE_COEFFICIENT + source.getInsulationCoefficient(side) + source.getInverseConductionCoefficient();
            double heatToTransfer = source.getTemp() / invConduction;
            source.transferHeatTo(-heatToTransfer);
            heatTransferred[1] += heatToTransfer;
        }

        return heatTransferred;
    }

    private double[] simulate(IHeatTransfer source, TileEntity tile, EnumFacing side)
    {
        double heatTransferred[] = new double[] {0, 0};

        // stop useless calculations
        if(source.getTemp() < 0.005) return heatTransferred;

        IHeatTransfer sink = getAdjacent(tile, side);

        if(sink != null)
        {
            double invConduction = sink.getInverseConductionCoefficient() + source.getInverseConductionCoefficient();
            double heatToTransfer = source.getTemp() / invConduction;
            source.transferHeatTo(-heatToTransfer);
            sink.transferHeatTo(heatToTransfer);
            heatTransferred[0] += heatToTransfer;
        }
        else
        {
            double invConduction = IHeatTransfer.AIR_INVERSE_COEFFICIENT + source.getInsulationCoefficient(side) + source.getInverseConductionCoefficient();
            double heatToTransfer = source.getTemp() / invConduction;
            source.transferHeatTo(-heatToTransfer);
            heatTransferred[1] += heatToTransfer;
        }

        return heatTransferred;

    }

    public double[] simulateHeat(TileEntity tile, EnumFacing side)
    {
        return simulate(this, tile, side);
    }

    public void readFromNBT(NBTTagCompound data)
    {
        if(data.hasKey(NBT)) setTemp(data.getDouble(NBT));
    }

    public void writeToNBT(NBTTagCompound data)
    {
        data.setDouble(NBT, getTemp());
    }
}
