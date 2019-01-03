package io.lylix.lya.integration.computer;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

public class OCDriver extends DriverSidedTileEntity
{
    @Override
    public Class<?> getTileEntityClass()
    {
        return IComputer.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, BlockPos blockPos, EnumFacing enumFacing)
    {
        TileEntity tile = world.getTileEntity(blockPos);

        if(tile instanceof IComputer)
        {
            return new OCManagedEnvironment(IComputer.class.cast(tile));
        }

        return null;
    }

    public class OCManagedEnvironment extends AbstractManagedEnvironment implements NamedBlock, ManagedPeripheral
    {
        private IComputer computer;

        public OCManagedEnvironment(IComputer computer)
        {
            this.setNode(Network.newNode(this, Visibility.Network).withComponent(computer.getName(), Visibility.Network).create());
            this.computer = computer;
        }

        @Override
        public String preferredName()
        {
            return computer.getName();
        }

        @Override
        public int priority()
        {
            return 1;
        }

        @Override
        public String[] methods()
        {
            return computer.getMethods();
        }

        @Override
        public Object[] invoke(String method, Context context, Arguments args) throws Exception
        {
            return computer.invoke(Arrays.asList(methods()).indexOf(method), args.toArray());
        }
    }
}
