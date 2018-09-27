package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiBase;

public interface IWidget
{
    void init(int guiLeft, int guiTop);
    void render(GuiBase gui, int mouseX, int mouseY);
}
