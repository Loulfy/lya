package io.lylix.lya.gui;

import io.lylix.lya.LYA;
import io.lylix.lya.container.ContainerHeater;
import io.lylix.lya.gui.widget.Diode;
import io.lylix.lya.gui.widget.Gauge;
import io.lylix.lya.multiblock.heater.TileHeaterMain;
import io.lylix.lya.network.MessageChunkSync;
import io.lylix.lya.util.UnitDisplayUtils;
import net.minecraft.client.gui.GuiButton;

public class GuiMultiblockHeater extends GuiBase
{
    private static final int WIDTH = 176;
    private static final int HEIGHT = 194;

    private TileHeaterMain tile;

    public GuiMultiblockHeater(ContainerHeater container, TileHeaterMain tile)
    {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
        this.tile = tile;
        addWidget(new Gauge(tile.controller().getTankHandler(), 140, 25));
        addWidget(new Diode(() -> tile.controller().isOn(), 105, 68)); // 140 88
    }

    @Override
    public void initGui()
    {
        super.initGui(); // 65 67
        this.buttonList.add(new GuiButton(0, guiLeft+55, guiTop+67, 40, 20, "Toggle"));
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
        fontRenderer.drawString(UnitDisplayUtils.getTemperatureDisplay(tile.getHeat(), UnitDisplayUtils.TemperatureUnit.AMBIENT), 60, 50, 0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if(button.id == 0) LYA.instance.network.sendToServer(new MessageChunkSync(tile.getPos(), button.id));
    }
}
