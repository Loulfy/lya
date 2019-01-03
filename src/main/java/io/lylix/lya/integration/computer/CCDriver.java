package io.lylix.lya.integration.computer;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import io.lylix.lya.integration.LYAHook;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = LYAHook.CC)
public class CCDriver implements IPeripheralProvider
{
    @Nullable
    @Override
    public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing enumFacing)
    {
        TileEntity tile = world.getTileEntity(pos);

        if(tile instanceof IComputer)
        {
            return new CCPeripheral(IComputer.class.cast(tile));
        }

        return null;
    }

    public class CCPeripheral implements IPeripheral
    {
        private IComputer computer;

        public CCPeripheral(IComputer computer)
        {
            this.computer = computer;
        }

        @Nonnull
        @Override
        public String getType()
        {
            return computer.getName();
        }

        @Nonnull
        @Override
        public String[] getMethodNames()
        {
            return computer.getMethods();
        }

        @Nullable
        @Override
        public Object[] callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull Object[] args)
        {
            try
            {
                return this.computer.invoke(method, args);
            }
            catch(NoSuchMethodException e)
            {
                return new Object[] {"Unknown command."};
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return new Object[] {"Error."};
            }
        }

        @Override
        public boolean equals(@Nullable IPeripheral iPeripheral)
        {
            return this == iPeripheral;
        }
    }
}
