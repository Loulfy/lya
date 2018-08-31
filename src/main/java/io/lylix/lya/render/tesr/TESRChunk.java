package io.lylix.lya.render.tesr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import io.lylix.lya.tile.TileChunk;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class TESRChunk extends TileEntitySpecialRenderer<TileChunk>
{
    @Override
    public void render(TileChunk te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 15 * 16, 15 * 16);
        GlStateManager.glLineWidth(3.0f);

        int layer = te.getPos().getY();
        for(ChunkPos chunk : te.getChunkSet())
        {
            /*GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            //GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(3.0F);
            //RenderHelper.disableStandardItemLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            //GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.color(1.0F,0,0);*/

            AxisAlignedBB aabb = test(chunk, layer, partialTicks);
            /*RenderGlobal.*/drawSelectionBoundingBox(aabb, 1.0F, 0.549F, 0.0F, 1.0F);

            /*RenderHelper.enableStandardItemLighting();
            GlStateManager.depthMask(true);
            //GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();*/
        }

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
    }

    private AxisAlignedBB createChunkAABB(ChunkPos pos)
    {
        return new AxisAlignedBB(pos.getXStart(), 0, pos.getZStart(), pos.getXEnd(), 250, pos.getZEnd());
    }

    private AxisAlignedBB test(ChunkPos pos, int layer, float partialTicks)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Vec3d d = player.getPositionEyes(partialTicks);
        double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        //layer = getMaxY(player);
        return new AxisAlignedBB(pos.getXStart(), layer, pos.getZStart(), pos.getXEnd(), layer+1, pos.getZEnd()).offset(-d.x,-d4,-d.z);//.grow(0.02D);
    }

    private AxisAlignedBB secondTest(ChunkPos pos, double dx, double dy, double dz)
    {
        return new AxisAlignedBB(pos.getXStart()-dx, 0-dy, pos.getZStart()-dz, pos.getXEnd()-dx+1, 7-dy, pos.getZEnd()-dz+1);
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha)
    {
        drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        buffer.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
    }

    private int getMaxY(EntityPlayer player)
    {
        return MathHelper.clamp(Math.min((int) player.posY + 1, 120), 1, 255);
    }
}
