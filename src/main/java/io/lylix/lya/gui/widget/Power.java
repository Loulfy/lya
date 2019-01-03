package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiBase;
import net.minecraftforge.energy.EnergyStorage;

public class Power extends Widget
{
    // gauge dimension
    private static final int W = 6;
    private static final int H = 54;

    private EnergyStorage bank;

    public Power(EnergyStorage bank, int x, int y)
    {
        super(x, y, W, H);
        this.bank = bank;
    }

    @Override
    public void render(GuiBase gui, int mouseX, int mouseY)
    {
        gui.bindTexture("lya", "textures/gui/guipowerbar.png");
        gui.drawTexture(getX(), getY(), 0, 1, W, H);

        int per = (bank.getEnergyStored()*(H-2))/bank.getMaxEnergyStored();

        boolean low = false;
        if(bank.getEnergyStored() < bank.getMaxEnergyStored()/2) low = true;
        gui.drawTexture(getX()+1, getY()+(H-1)-per, low ? 13 : 7, 2, W-2, per);
    }

    @Override
    public String tooltip()
    {
        return String.valueOf((bank.getEnergyStored()*100)/bank.getMaxEnergyStored()) + "%";
    }
}
