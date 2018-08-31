package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiChunk;

public class Check extends Button
{
    private boolean active;

    public Check(GuiChunk gui)
    {
        super(gui);
        active = false;
    }

    @Override
    protected void drawButtonIcon(int x, int y)
    {
        gui.drawTexture(x, y, 10, gui.getTile().check(id) ? 0 : 10,10,10);
    }

    @Override
    public String getTooltip()
    {
        return "Potatoes";
    }

    @Override
    public void actionPerformed()
    {
        active = !active;
    }
}
