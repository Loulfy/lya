package io.lylix.lya.gui;

import io.lylix.lya.LYA;
import io.lylix.lya.gui.widget.Button;
import io.lylix.lya.gui.widget.Check;
import io.lylix.lya.gui.widget.Power;
import io.lylix.lya.network.MessageChunkSync;
import io.lylix.lya.container.ContainerChunk;
import io.lylix.lya.tile.TileChunk;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;


import java.io.IOException;

public class GuiChunk extends GuiBase
{
    private static final int WIDTH = 210;//180
    private static final int HEIGHT = 194;//152

    private TileChunk tile;

    public GuiChunk(ContainerChunk container, TileChunk tile)
    {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
        this.tile = tile;
        addWidget(new Power(tile.getEnergy(), 20, 27));
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
    public void drawBackground()
    {
        bindTexture("textures/gui/guichunk.png");
        drawTexture(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString("Chunk Loader", (175/2)-(fontRenderer.getStringWidth("Chunk Loader")/2), 4, 0x404040);
        fontRenderer.drawString("Inventory", 7, 100, 0x404040);
        //int per = (tile.getEnergy().getEnergyStored()*100)/tile.getEnergy().getMaxEnergyStored();
        //fontRenderer.drawString(per+"%", 140, 30, 0x404040);
        fontRenderer.drawString(getConsumptionDisplay(), 80, 100, 0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        LYA.logger.debug("Button {} clicked!", button.id);

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

    private String getConsumptionDisplay()
    {
        return (tile.enabled ? tile.getEnergyUsage() : 0)+" FE/t";
    }

}
