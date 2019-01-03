package io.lylix.lya.multiblock.heater;

import io.lylix.lya.block.BlockBase;
import io.lylix.lya.render.IModelRegister;
import it.zerono.mods.zerocore.api.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.api.multiblock.MultiblockControllerBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class HeaterBlockBase extends BlockBase implements ITileEntityProvider
{
    private HeaterBlockType type;

    public HeaterBlockBase(HeaterBlockType type)
    {
        this.type = type;
    }

    public String getId()
    {
        return "heater_" + type.getName().toLowerCase();
    }

    public Class<? extends TileHeater> getTileClass()
    {
        switch(type)
        {
            case Wall:
                return TileHeaterWall.class;
            case Main:
                return TileHeaterMain.class;
            case Core:
                return TileHeaterCore.class;
            case Heat:
                return TileHeaterHeat.class;
            case Fuel:
                return TileHeaterFuel.class;
            case Info:
                return TileHeaterInfo.class;
            default:
                return TileHeater.class;
        }
    }

    @Override
    public void initModel(IModelRegister register)
    {
        register.setModel(this, 0, new ModelResourceLocation("lya:"+getId(), "inventory"));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        try
        {
            return getTileClass().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected IMultiblockPart getMultiblockPartAt(IBlockAccess world, BlockPos position)
    {
        TileEntity te = world.getTileEntity(position);
        return te instanceof IMultiblockPart ? (IMultiblockPart) te : null;
    }

    protected HeaterController getHeaterController(IBlockAccess world, BlockPos position)
    {
        IMultiblockPart part = this.getMultiblockPartAt(world, position);
        if(part != null)
        {
            MultiblockControllerBase controller = part.getMultiblockController();
            return controller instanceof HeaterController ? (HeaterController) controller : null;
        }
        return null;
    }
}
