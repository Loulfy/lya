package io.lylix.lya.gui;

import io.lylix.lya.LYA;
import io.lylix.lya.network.MessageButtonSync;
import io.lylix.lya.tile.TileNode;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;

import java.io.IOException;

import static io.lylix.lya.tile.TileNode.BUNDLED_TO_CONDUIT;
import static io.lylix.lya.tile.TileNode.CONNECTED_BIT;
import static io.lylix.lya.tile.TileNode.CONDUIT_TO_BUNDLED;

public class GuiNode extends GuiBase
{
    private static final int WIDTH = 216;
    private static final int HEIGHT = 84;

    private final static int wireLeft = 12, wireTop = 30, wireWidth = 10, wireHeight = 26, wireSpacing = 12;

    private TileNode tile;

    public GuiNode(Container container, TileNode tile)
    {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
        this.tile = tile;
    }

    @Override
    public void drawBackground()
    {
        bindTexture("textures/gui/gui_bundle_adaptor.png", 256, 256);
        drawTexture(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        for(EnumDyeColor color : EnumDyeColor.values())
        {
            float[] rgba = color.getColorComponentValues();
            GlStateManager.color(rgba[0], rgba[1], rgba[2]);

            int wireX = wireLeft + wireSpacing * color.ordinal() + guiLeft;
            int wireY = wireTop + guiTop;
            int wireU = 216, wireV = 0;
            int arrowU = 216, arrowV = 52;
            boolean showArrow = true;
            switch (tile.signalConfig[color.ordinal()])
            {
                case CONNECTED_BIT | BUNDLED_TO_CONDUIT:
                    break;
                case CONNECTED_BIT | CONDUIT_TO_BUNDLED:
                    arrowV += 8;
                    break;
                default:
                    wireU += 20;
                    showArrow = false;
                    break;
            }

            drawTexturedRect(wireX, wireY, wireWidth, wireHeight, wireU, wireV, 20, 52); // wire
            GlStateManager.color(1, 1, 1);

            if(showArrow) drawTexturedRect(wireX, wireY + 11, wireWidth, 4, arrowU, arrowV, 20, 8); // arrow
            else drawTexturedRect(wireX, wireY + 8, wireWidth, 2, 216, 68, 20, 4); // cut wire end
        }

        int W = guiLeft + WIDTH/2;
        int A = W - fontRenderer.getStringWidth("Bundled Cable")/2;
        int B = W - fontRenderer.getStringWidth("Redstone Conduit")/2;
        fontRenderer.drawString("Bundled Cable", A, guiTop+6, 0x404040);
        fontRenderer.drawString("Redstone Conduit", B, guiTop+71, 0x404040);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        if(x > wireLeft && y > wireTop && y < wireTop + wireHeight)
        {
            int i = (x - wireLeft) / wireSpacing;
            if(i < 16) LYA.instance.network.sendToServer(new MessageButtonSync(tile.getPos(), mouseButton, i));
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
