package io.lylix.lya.block;

import io.lylix.lya.LYA;
import io.lylix.lya.render.IModelRegister;
import io.lylix.lya.tile.TileHeater;
import io.lylix.lya.util.LYAUtils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockHeater extends BlockBase implements ITileEntityProvider
{
    private static final PropertyDirection FACING = PropertyDirection.create("facing");
    private static final PropertyBool ENABLED = PropertyBool.create("active");

    public BlockHeater()
    {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ENABLED, false));
    }

    @SideOnly(Side.CLIENT)
    public void initModel(IModelRegister register)
    {
        register.setModel(this, 0, new ModelResourceLocation("lya:heater", "inventory"));
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, ENABLED);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileHeater tile = getTile(world, pos);
        if(tile != null) tile.facing = placer.getHorizontalFacing().getOpposite();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote) return true;
        player.openGui(LYA.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileHeater tile = getTile(world, pos);
        if(tile != null) LYAUtils.dropInventoryItems(world, pos, tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
        super.breakBlock(world, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileHeater();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileHeater tile = getTile(world, pos);
        if(tile == null) return state;
        return state.withProperty(ENABLED, tile.active).withProperty(FACING, tile.facing);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    private TileHeater getTile(IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileHeater ? TileHeater.class.cast(tile) : null;
    }
}
