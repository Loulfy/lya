package io.lylix.lya.integration;

import io.lylix.lya.LYA;
import io.lylix.lya.tile.TileChunk;
import io.lylix.lya.render.Renderer;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.ClientEvent.Type;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.util.math.ChunkPos;

import java.util.EnumSet;

@ClientPlugin
public class JourneyMapPlugin implements IClientPlugin
{
    private IClientAPI api = null;
    private Renderer renderer;

    @Override
    public void initialize(IClientAPI jmClientApi)
    {
        api = jmClientApi;
        renderer = LYA.proxy.getRenderer();
        this.api.subscribe(getModId(), EnumSet.of(Type.DISPLAY_UPDATE, Type.MAPPING_STARTED, Type.MAPPING_STOPPED));
    }

    @Override
    public String getModId()
    {
        return LYA.ID;
    }

    @Override
    public void onEvent(ClientEvent event)
    {
        try
        {
            switch (event.type)
            {
                case MAPPING_STOPPED:
                    this.api.removeAll(getModId());
                    break;

                case MAPPING_STARTED:
                    this.updatePolygons(event.dimension);
                case DISPLAY_UPDATE:
                    this.updatePolygons(event.dimension);
                    break;

                default:
            }
        }
        catch (Exception e)
        {
            LYA.logger.warn("Exception while updating map overlay");
        }
    }

    private PolygonOverlay createPolygon(int dimension, ChunkPos pos, ShapeProperties shape)
    {
        String displayId = "chunk_" + pos.toString();

        MapPolygon polygon = PolygonHelper.createChunkPolygon(pos.x, 256, pos.z);
        return new PolygonOverlay(getModId(), displayId, dimension, shape, polygon, null);

        /*String groupName = "Modified Chunks";
        String label = String.format("Modified Chunk [%s,%s]", pos.x, pos.z);
        TextProperties textProps = new TextProperties()
                .setBackgroundColor(0x000022).setBackgroundOpacity(.5f)
                .setColor(0x00ff00).setOpacity(1f)
                .setMinZoom(2).setFontShadow(true);
        overlay.setOverlayGroupName(groupName).setLabel(label).setTextProperties(textProps);*/
    }

    private void updatePolygons(int dim) throws Exception
    {
        this.api.removeAll(getModId());
        ShapeProperties shape = new ShapeProperties().setStrokeColor(0xff8c00).setStrokeOpacity(1f).setStrokeWidth(2);

        for(TileChunk te : renderer.getTiles())
        {
            for(ChunkPos chunk : te.getChunkSet())
            {
                this.api.show(this.createPolygon(dim, chunk, shape));
            }
        }
    }
}
