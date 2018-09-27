package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class Gauge implements IWidget
{
    // gauge dimension
    private static final int W = 18;
    private static final int H = 60;

    private FluidTank tank;

    private int x;
    private int y;

    private int mx;
    private int my;

    public Gauge(FluidTank tank, int x, int y)
    {
        this.tank = tank;
        this.x = x;
        this.y = y;
    }

    @Override
    public void init(int guiLeft, int guiTop)
    {
        mx = x+guiLeft;
        my = y+guiTop;
    }

    @Override
    public void render(GuiBase gui, int mouseX, int mouseY)
    {
        gui.bindTexture("mekanism", "gui/elements/guigaugestandard.png");
        gui.drawTexture(mx, my, 0, 0, W, H);

        FluidStack stack = tank.getFluid();
        if(stack != null)
        {
            int per = (tank.getFluidAmount()*(H-2))/tank.getCapacity();
            gui.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite texture = gui.mc.getTextureMapBlocks().getTextureExtry(stack.getFluid().getStill().toString());
            gui.drawTexturedModalRect(mx+1, my+(H-1)-per, texture, W-2, per);
        }

        gui.bindTexture("mekanism", "gui/elements/guigaugestandard.png");
        gui.drawTexture(mx, my, W, 0, W, H);

        if(gui.inBounds(mx, my, W, H, mouseX, mouseY)) gui.drawHoveringText(getContentDisplay(), mouseX, mouseY);
    }

    private String getContentDisplay()
    {
        FluidStack stack = tank.getFluid();
        return stack == null ? "Empty" : stack.getLocalizedName()+": "+stack.amount+"mB";
    }
}
