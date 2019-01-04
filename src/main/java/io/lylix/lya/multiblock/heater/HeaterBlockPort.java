package io.lylix.lya.multiblock.heater;

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HeaterBlockPort extends HeaterBlockBase
{
    private static final PropertyBool ASSEMBLED = PropertyBool.create("assembled");
    private static final PropertyDirection FACING = PropertyDirection.create("facing");

    public HeaterBlockPort(HeaterBlockType type)
    {
        super(type);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ASSEMBLED, false));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, ASSEMBLED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IMultiblockPart part = this.getMultiblockPartAt(world, pos);

        if(part instanceof TileHeater)
        {

            TileHeater wallTile = TileHeater.class.cast(part);
            boolean assembled = wallTile.isConnected() && wallTile.getMultiblockController().isAssembled();

            state = state.withProperty(ASSEMBLED, assembled);

            if(assembled)
            {

                switch(wallTile.getPartPosition())
                {
                    case NorthFace:
                        state = state.withProperty(FACING, EnumFacing.NORTH);
                        break;

                    case SouthFace:
                        state = state.withProperty(FACING, EnumFacing.SOUTH);
                        break;

                    case WestFace:
                        state = state.withProperty(FACING, EnumFacing.WEST);
                        break;

                    case EastFace:
                        state = state.withProperty(FACING, EnumFacing.EAST);
                        break;

                    case BottomFace:
                        state = state.withProperty(FACING, EnumFacing.DOWN);
                        break;

                    case TopFace:
                        state = state.withProperty(FACING, EnumFacing.UP);
                        break;
                }
            }
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if(!world.isRemote && getTileClass() == TileHeaterInfo.class)
        {
            HeaterController controller = getHeaterController(world, pos);
            if(controller != null && controller.isAssembled()) controller.setOn(world.isBlockIndirectlyGettingPowered(pos) > 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state)
    {
        return getTileClass() == TileHeaterInfo.class;
    }
}
