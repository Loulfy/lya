package io.lylix.lya.multiblock.heater;

import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.BlockFacings;
import it.zerono.mods.zerocore.lib.PropertyBlockFacings;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class HeaterBlockWall extends HeaterBlockBase
{
    private final static PropertyEnum FACES;

    static
    {
        List<PropertyBlockFacings> values = new ArrayList<>();

        values.addAll(PropertyBlockFacings.ALL_AND_NONE);
        values.addAll(PropertyBlockFacings.FACES);
        values.addAll(PropertyBlockFacings.ANGLES);
        values.addAll(PropertyBlockFacings.CORNERS);
        values.add(PropertyBlockFacings.Opposite_EW);

        FACES = PropertyEnum.create("faces", PropertyBlockFacings.class, values);
    }

    public HeaterBlockWall()
    {
        super(HeaterBlockType.Wall);
        setDefaultState(blockState.getBaseState().withProperty(FACES, PropertyBlockFacings.All));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACES);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IMultiblockPart part = this.getMultiblockPartAt(world, pos);

        if(part instanceof TileEntity)
        {

            TileHeater wallTile = TileHeater.class.cast(part);
            boolean assembled = wallTile.isConnected() && wallTile.getMultiblockController().isAssembled();
            BlockFacings facings = assembled ? wallTile.getOutwardsDir() : BlockFacings.ALL;

            state = state.withProperty(FACES, facings.toProperty());
        }

        return state;
    }
}
