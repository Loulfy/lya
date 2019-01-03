package io.lylix.lya.multiblock.heater;

import io.lylix.lya.LYA;
import io.lylix.lya.util.LYAUtils;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.validation.ValidationError;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class HeaterBlockMain extends HeaterBlockBase
{
    private static final PropertyDirection HFACING = PropertyDirection.create("hfacing", EnumFacing.Plane.HORIZONTAL);
    private static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);

    public HeaterBlockMain()
    {
        super(HeaterBlockType.Main);
        setDefaultState(blockState.getBaseState().withProperty(HFACING, EnumFacing.NORTH).withProperty(STATE, State.None));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(HFACING).getIndex();
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, HFACING, STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if(EnumFacing.Axis.Y == enumfacing.getAxis()) enumfacing = EnumFacing.NORTH;

        return this.getDefaultState().withProperty(HFACING, enumfacing);
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

            HeaterController controller = getHeaterController(world, pos);
            boolean active = controller.isActive();

            state = state.withProperty(STATE, State.parse(assembled, active));

            if(assembled)
            {

                switch(wallTile.getPartPosition())
                {
                    case NorthFace:
                        state = state.withProperty(HFACING, EnumFacing.NORTH);
                        break;

                    case SouthFace:
                        state = state.withProperty(HFACING, EnumFacing.SOUTH);
                        break;

                    case WestFace:
                        state = state.withProperty(HFACING, EnumFacing.WEST);
                        break;

                    case EastFace:
                        state = state.withProperty(HFACING, EnumFacing.EAST);
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
        facing = (null != placer) ? placer.getHorizontalFacing().getOpposite() : EnumFacing.NORTH;
        return this.getDefaultState().withProperty(HFACING, facing);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote) return true;

        HeaterController controller = getHeaterController(world, pos);

        if(controller != null)
        {
            ValidationError status = controller.getLastError();

            if(status != null && !controller.isAssembled())
            {
                player.sendMessage(status.getChatMessage());
                return true;
            }
            else
            {
                player.openGui(LYA.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if(tile != null) LYAUtils.dropInventoryItems(world, pos, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
        super.breakBlock(world, pos, state);
    }

    @Override
    public void onBlockAdded(World world, BlockPos position, IBlockState state)
    {
        EnumFacing newFacing = this.suggestDefaultFacing(world, position, state.getValue(HFACING));
        world.setBlockState(position, state.withProperty(HFACING, newFacing), 2);
    }

    private EnumFacing suggestDefaultFacing(World world, BlockPos position, EnumFacing currentFacing)
    {

        EnumFacing oppositeFacing = currentFacing.getOpposite();
        IBlockState facingBlockState = world.getBlockState(position.offset(currentFacing));
        IBlockState oppositeBlockState = world.getBlockState(position.offset(oppositeFacing));
        return facingBlockState.isFullBlock() && !oppositeBlockState.isFullBlock() ? oppositeFacing : currentFacing;
    }

    enum State implements IStringSerializable
    {
        None,
        Assembled,
        Active;

        @Override
        public String getName()
        {
            return this.toString().toLowerCase();
        }

        public static State parse(boolean assembled, boolean active)
        {
            if(assembled && active) return State.Active;
            if(assembled) return State.Assembled;
            return State.None;
        }
    }
}
