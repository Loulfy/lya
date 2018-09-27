package io.lylix.lya.integration.capability;

import mekanism.api.Coord4D;
import mekanism.api.IHeatTransfer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface="mekanism.api.IHeatTransfer", modid="mekanism")
public class Heater implements IHeatTransfer
{
    private TileEntity tile;
    private double temperature;
    private double heatToAbsorb;

    public Heater(TileEntity te)
    {
        tile = te;
        temperature = 0;
        heatToAbsorb = 0;
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
        TileEntity adj = Coord4D.get(tile).offset(side).getTileEntity(tile.getWorld());

        if(Capabilities.hasCapability(adj, Capabilities.HEAT, side.getOpposite())) return Capabilities.getCapability(adj, Capabilities.HEAT, side.getOpposite());

        return null;
    }

    public static double[] simulate(IHeatTransfer source)
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

                /*if(!(sink instanceof ICapabilityProvider && CapabilityUtils.hasCapability((ICapabilityProvider)sink, Capabilities.GRID_TRANSMITTER_CAPABILITY, side.getOpposite())))
                {
                    heatTransferred[0] += heatToTransfer;
                }*/

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
}
