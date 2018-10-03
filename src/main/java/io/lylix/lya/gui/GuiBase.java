package io.lylix.lya.gui;

import io.lylix.lya.LYA;
import io.lylix.lya.gui.widget.IWidget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class GuiBase extends GuiContainer
{
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    private Set<IWidget> widgetSet;

    public GuiBase(Container container)
    {
        super(container);
        widgetSet = new HashSet<>();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        for(IWidget widget : widgetSet) widget.init(guiLeft, guiTop);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawBackground();
        for(IWidget widget : widgetSet) widget.render(this, mouseX, mouseY);
    }

    public void drawBackground()
    {

    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public boolean inBounds(int x, int y, int w, int h, int ox, int oy)
    {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }

    public void bindTexture(String file)
    {
        bindTexture(LYA.ID, file);
    }

    public void bindTexture(String base, String file)
    {
        String id = base + ":" + file;
        mc.getTextureManager().bindTexture(TEXTURE_CACHE.computeIfAbsent(id, k -> new ResourceLocation(base, file)));
    }

    public void drawTexture(int x, int y, int textureX, int textureY, int width, int height)
    {
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public void addWidget(IWidget widget)
    {
        widgetSet.add(widget);
    }
}
