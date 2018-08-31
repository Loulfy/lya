package io.lylix.lya.block;

import io.lylix.lya.LYA;
import io.lylix.lya.tile.TileChunk;
import io.lylix.lya.render.IModelRegister;
import io.lylix.lya.item.itemblock.ItemBlockChunk;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockChunk extends BlockBase implements ITileEntityProvider
{
    private static final PropertyDirection FACING = PropertyDirection.create("facing");
    private static final PropertyBool ENABLED = PropertyBool.create("enabled");

    public BlockChunk()
    {
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ENABLED, false));
    }

    @SideOnly(Side.CLIENT)
    public void initModel(IModelRegister register)
    {
        register.setModel(this, 0, new ModelResourceLocation("lya:chunkloader", "inventory"));
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
        TileChunk tile = getTile(world, pos);
        assert tile != null;

        tile.facing = placer.getHorizontalFacing().getOpposite();

        if(!world.isRemote)
        {
            tile.getEnergy().setStored(ItemBlockChunk.getEnergyStored(stack));

            if(ItemBlockChunk.hasItems(stack))
            {
                tile.getCards().deserializeNBT(ItemBlockChunk.getItemsStored(stack));
                tile.getChunkLoader().refreshPresence();
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileChunk();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileChunk tile = getTile(world, pos);
        return tile == null ? state : state.withProperty(ENABLED, tile.enabled).withProperty(FACING, tile.facing);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z)
    {
        if(world.isRemote) return true;
        player.openGui(LYA.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    private TileChunk getTile(IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileChunk ? TileChunk.class.cast(tile) : null;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));

        TileChunk tile = getTile(world, pos);
        if(tile != null) ItemBlockChunk.createStack(stack, tile);

        drops.add(stack);
    }

    @Override
    public Item createItem()
    {
        return new ItemBlockChunk(this);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        // Delay deletion of the block until after getDrops
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack)
    {
        super.harvestBlock(world, player, pos, state, tile, stack);
        world.setBlockToAir(pos);
    }


}
