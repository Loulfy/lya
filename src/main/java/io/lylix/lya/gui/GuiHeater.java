package io.lylix.lya.gui;

import io.lylix.lya.container.ContainerHeater;
import io.lylix.lya.gui.widget.Gauge;
import io.lylix.lya.tile.TileHeater;
import io.lylix.lya.util.UnitDisplayUtils;

public class GuiHeater extends GuiBase
{
    private static final int WIDTH = 176;
    private static final int HEIGHT = 194;

    private TileHeater tile;

    public GuiHeater(ContainerHeater container, TileHeater tile)
    {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
        this.tile = tile;
        addWidget(new Gauge(tile.getTank(), 140, 25));
    }

    @Override
    public void drawBackground()
    {
        bindTexture("textures/gui/guichunk.png");
        drawTexture(guiLeft, guiTop, 0, 0, xSize, ySize);

        bindTexture("mekanism", "gui/elements/guislot.png");
        drawTexture(guiLeft+20, guiTop+25, 0, 0, 18, 18);
        drawTexture(guiLeft+20, guiTop+25, 54, 18, 18, 18);
        drawTexture(guiLeft+20, guiTop+67, 0, 0, 18, 18);
        drawTexture(guiLeft+20, guiTop+67, 72, 18, 18, 18);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString("Liquid Fueled Heater", (175/2)-(fontRenderer.getStringWidth("Liquid Fueled Heater")/2), 6, 0x404040);
        fontRenderer.drawString("Inventory", 7, 100, 0x404040);
        fontRenderer.drawString("Temperature", 50, 40, 0x404040);
        fontRenderer.drawString(UnitDisplayUtils.getTemperatureDisplay(tile.getTemp(), UnitDisplayUtils.TemperatureUnit.AMBIENT), 60, 50, 0x404040);
    }
}
