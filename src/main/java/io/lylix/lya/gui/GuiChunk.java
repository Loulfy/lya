package io.lylix.lya.gui;

import io.lylix.lya.LYA;
import io.lylix.lya.gui.widget.Button;
import io.lylix.lya.gui.widget.Check;
import io.lylix.lya.network.MessageChunkSync;
import io.lylix.lya.tile.TileChunk;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import io.lylix.lya.container.ContainerChunk;

import java.io.IOException;

public class GuiChunk extends GuiContainer
{
    public static final int WIDTH = 210;//180
    public static final int HEIGHT = 194;//152

    private static final ResourceLocation background = new ResourceLocation(LYA.ID, "textures/gui/guichunk.png");

    private TileChunk tile;

    public GuiChunk(ContainerChunk container, TileChunk tile)
    {
        super(container);

        xSize = WIDTH;
        ySize = HEIGHT;

        this.tile = tile;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        for(int y=0; y < 7; y++)
        {
            for(int x=0; x < 7; x++)
            {
                if(x+y*7 == 24) continue;
                Button btn = new Check(this);
                btn.id = x+y*7;
                //btn.x = 150+20*x; btn.y = 50+20*y;

                int w = (175/2)-(82/2);
                btn.x = guiLeft+w+12*x; btn.y = guiTop+15+12*y;
                this.buttonList.add(btn);
            }
        }

        this.buttonList.add(new GuiButton(50, guiLeft+130, guiTop+40, 40, 20, "Show"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.disableLighting();
        drawString(fontRenderer, "Chunk Loader", (175/2)-(fontRenderer.getStringWidth("Chunk Loader")/2), 4, 0xffffff);
        //drawString(fontRenderer, ""+tile.getEnergyUsage()*100, xSize-100, 10, 0x404040);
        drawString(fontRenderer, "Inventory", 7, 100, 0xffffff);
        int per = (tile.getEnergy().getEnergyStored()*100)/tile.getEnergy().getMaxEnergyStored();
        drawString(fontRenderer, per+"%", 140, 20, 0xffffff);
        GlStateManager.enableLighting();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        LYA.logger.info("Button {} clicked!", button.id);

        if(button.id == 50) LYA.proxy.getRenderer().toggle();

        if(button instanceof Button)
        {
            LYA.instance.network.sendToServer(new MessageChunkSync(tile.getPos(), button.id));
            Button btn = (Button) button;
            btn.actionPerformed();
        }
    }

    public TileChunk getTile()
    {
        return tile;
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

        /*if (!TEXTURE_CACHE.containsKey(id)) {
            TEXTURE_CACHE.put(id, new ResourceLocation(base, "textures/" + file));
        }*/

        ResourceLocation rc = new ResourceLocation(base, "textures/gui/"+file);

        mc.getTextureManager().bindTexture(rc);
    }

    public void drawTexture(int x, int y, int textureX, int textureY, int width, int height)
    {
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }
}
