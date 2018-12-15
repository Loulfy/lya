package io.lylix.lya.render;

import io.lylix.lya.LYA;
import io.lylix.lya.tile.TileChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public class Renderer
{
    private Set<TileChunk> tiles = new HashSet<>();

    private boolean active = false;

    public void render(float partialTicks)
    {
        if(active)
        {
            Vec3d d = offsetPlayer(Minecraft.getMinecraft().player, partialTicks);

            for(TileChunk te : tiles)
            {
                int y = te.getPos().getY();
                for(ChunkPos chunk : te.getChunkSet())
                {
                    renderBoundingBox(createChunkAABB(chunk, y).offset(d));
                }
            }
        }
    }

    public void renderBoundingBox(AxisAlignedBB aabb)
    {
        GlStateManager.depthMask(false);
        //GlStateManager.disableLighting();
        GlStateManager.disableCull();
        //GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(2.0f);

        RenderGlobal.drawSelectionBoundingBox(aabb, 1.0f, 0.549f, 0.0f, 1.0f);

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        //GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
    }

    public Vec3d offsetPlayer(EntityPlayer player, float partialTicks)
    {
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        return new Vec3d(-x, -y, -z);
    }

    private AxisAlignedBB createChunkAABB(ChunkPos pos, int y)
    {
        return new AxisAlignedBB(pos.getXStart(), y, pos.getZStart(), pos.getXEnd(), y+1, pos.getZEnd());
    }

    public void toggle()
    {
        active = !active;
    }

    public void show(TileChunk te)
    {
        tiles.add(te);
    }

    public void clear(TileChunk te)
    {
        tiles.remove(te);
    }

    public void clean()
    {
        tiles.clear();
        LYA.logger.debug("PURGE 3D");
    }

    public Set<TileChunk> getTiles()
    {
        return tiles;
    }
}

