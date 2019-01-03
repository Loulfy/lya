package io.lylix.lya.gui.widget;

import io.lylix.lya.LYA;
import io.lylix.lya.gui.GuiBase;

public class Diode extends Widget
{
    private static final int S = 18;

    private IMonitor monitor;

    public Diode(IMonitor monitor, int x, int y)
    {
        super(x, y, S, S);
        this.monitor = monitor;
        LYA.logger.info("isON = {}", monitor.action());
    }

    @Override
    public void render(GuiBase gui, int mouseX, int mouseY)
    {
        gui.bindTexture("mekanism", "gui/elements/guiheattab.png");
        gui.drawTexture(getX(), getY(), 26, monitor.action() ? 0 : 18, S, S);
    }

    @Override
    public String tooltip()
    {
        return monitor.action() ? "On" : "Off";
    }

    public interface IMonitor
    {
        boolean action();
    }
}
