package io.lylix.lya.block;

import io.lylix.lya.LYA;
import io.lylix.lya.render.IModelRegister;
import io.lylix.lya.tile.TileNode;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockNode extends BlockBase implements ITileEntityProvider
{
    @Override
    public void initModel(IModelRegister register)
    {
        register.setModel(this, 0, new ModelResourceLocation("lya:node", "inventory"));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileNode();
    }

    @Override
    @SuppressWarnings("deprecated")
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z)
    {
        if(world.isRemote) return true;
        player.openGui(LYA.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    @SuppressWarnings("deprecated")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileNode)
        {
            TileNode node = TileNode.class.cast(te);
            node.findChildren();
            node.onNeighborBlockChange();
        }
    }
}
