package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiBase;

public abstract class Widget
{
    private int x;
    private int y;

    private int w;
    private int h;

    private int mx;
    private int my;

    public Widget(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void init(int guiLeft, int guiTop)
    {
        mx = x+guiLeft;
        my = y+guiTop;
    }

    public boolean inBounds(int ox, int oy)
    {
        return ox >= mx && ox <= mx + w && oy >= my && oy <= my + h;
    }

    public String tooltip()
    {
        return "";
    }

    public int getX()
    {
        return mx;
    }

    public int getY()
    {
        return my;
    }

    public abstract void render(GuiBase gui, int mouseX, int mouseY);
}
