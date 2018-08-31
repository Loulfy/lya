package io.lylix.lya.gui.widget;

import io.lylix.lya.gui.GuiChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public abstract class Button extends GuiButton
{
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    protected GuiChunk gui;

    public Button(GuiChunk gui)
    {
        super(-1, -1, -1, WIDTH, HEIGHT, "");

        this.gui = gui;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();

        hovered = gui.inBounds(x, y, width, height, mouseX, mouseY);

        gui.bindTexture("buttons.png");
        gui.drawTexture(x, y, 0, hovered ? 10 : 0, WIDTH, HEIGHT);

        drawButtonIcon(x, y);

        if (hovered)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
            gui.drawTexture(x, y, 0, 20, WIDTH, HEIGHT);
            GlStateManager.disableBlend();
        }
    }

    protected abstract void drawButtonIcon(int x, int y);

    // TODO : remove ?
    public abstract String getTooltip();

    public abstract void actionPerformed();
}
