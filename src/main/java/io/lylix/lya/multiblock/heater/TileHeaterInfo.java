package io.lylix.lya.multiblock.heater;

import io.lylix.lya.integration.computer.IComputer;

public class TileHeaterInfo extends TileHeaterPort implements IComputer
{
    private final static String[] METHODS = {"amount", "toggle", "temp", "fuel", "core", "isOn", "isAssembled", "on", "off", "consumption"};

    @Override
    public String getName()
    {
        return "heater";
    }

    @Override
    public String[] getMethods()
    {
        return METHODS;
    }

    @Override
    public Object[] invoke(int method, Object[] args) throws Exception
    {
        switch (method)
        {
            case 0:
                return new Object[]{controller().getTankHandler().getFluidAmount()};
            case 1:
                return new Object[]{controller().toggle()};
            case 2:
                return new Object[]{controller().getHeatHandler().getTemp()};
            case 3:
                return new Object[]{controller().fuel()};
            case 4:
                return new Object[]{controller().core()};
            case 5:
                return new Object[]{controller().isOn()};
            case 6:
                return new Object[]{isMachineAssembled()};
            case 7:
                controller().setOn(true);
                return new Object[]{};
            case 8:
                controller().setOn(false);
                return new Object[]{};
            case 9:
                return new Object[]{controller().getConsumption()};
            default:
                throw new NoSuchMethodException();
        }
    }
}
