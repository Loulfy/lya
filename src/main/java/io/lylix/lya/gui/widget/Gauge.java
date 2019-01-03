package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class Gauge extends Widget
{
    // gauge dimension
    private static final int W = 18;
    private static final int H = 60;

    private FluidTank tank;

    public Gauge(FluidTank tank, int x, int y)
    {
        super(x, y, W, H);
        this.tank = tank;
    }

    @Override
    public void render(GuiBase gui, int mouseX, int mouseY)
    {
        gui.bindTexture("mekanism", "gui/elements/guigaugestandard.png");
        gui.drawTexture(getX(), getY(), 0, 0, W, H);

        FluidStack stack = tank.getFluid();
        if(stack != null)
        {
            int per = (tank.getFluidAmount()*(H-2))/tank.getCapacity();
            gui.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite texture = gui.mc.getTextureMapBlocks().getTextureExtry(stack.getFluid().getStill().toString());
            gui.drawTexturedModalRect(getX()+1, getY()+(H-1)-per, texture, W-2, per);
        }

        gui.bindTexture("mekanism", "gui/elements/guigaugestandard.png");
        gui.drawTexture(getX(), getY(), W, 0, W, H);
    }

    @Override
    public String tooltip()
    {
        FluidStack stack = tank.getFluid();
        return stack == null ? "Empty" : stack.getLocalizedName()+": "+stack.amount+"mB";
    }
}
