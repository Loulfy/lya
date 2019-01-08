package io.lylix.lya.gui;

import io.lylix.lya.LYA;
import io.lylix.lya.gui.widget.Widget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class GuiBase extends GuiContainer
{
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    private double uscale, vscale;
    private Set<Widget> widgetSet;

    public GuiBase(Container container)
    {
        super(container);
        widgetSet = new HashSet<>();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        for(Widget widget : widgetSet) widget.init(guiLeft, guiTop);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
        renderWidgetToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawBackground();
        for(Widget widget : widgetSet) widget.render(this, mouseX, mouseY);
    }

    public void drawBackground()
    {

    }

    private void renderWidgetToolTip(int mouseX, int mouseY)
    {
        for(Widget widget : widgetSet)
        {
            if(!widget.tooltip().isEmpty() && widget.inBounds(mouseX, mouseY))
            {
                drawHoveringText(widget.tooltip(), mouseX, mouseY);
            }
        }
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

    public void addWidget(Widget widget)
    {
        widgetSet.add(widget);
    }

    public void bindTexture(String path, int usize, int vsize)
    {
        bindTexture(path);
        uscale = 1.0 / usize;
        vscale = 1.0 / vsize;
    }

    public void drawTextureRectUV(double x, double y, double w, double h, double u, double v, double us, double vs)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y+h, this.zLevel).tex(u, v+vs).endVertex();
        bufferbuilder.pos(x+w, y+h, this.zLevel).tex(u+us, v+vs).endVertex();
        bufferbuilder.pos(x+w, y, this.zLevel).tex(u+us, v).endVertex();
        bufferbuilder.pos(x, y, this.zLevel).tex(u, v).endVertex();
        tessellator.draw();
    }

    public void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs)
    {
        drawTextureRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale);
    }

    public void drawTexturedRect(double x, double y, double w, double h)
    {
        drawTextureRectUV(x, y, w, h, 0.0, 0.0, 1.0, 1.0);
    }

    public void drawTexturedRect(double x, double y, double w, double h, double u, double v)
    {
        drawTexturedRect(x, y, w, h, u, v, w, h);
    }
}
