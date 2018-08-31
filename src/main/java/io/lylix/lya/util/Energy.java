package io.lylix.lya.util;

import net.minecraftforge.energy.EnergyStorage;

public class Energy extends EnergyStorage
{
    public Energy(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public void setStored(int amount)
    {
        this.energy = Math.min(amount, capacity);
    }
}
