package io.lylix.lya.tile;

import com.enderio.core.common.util.DyeColor;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduits.conduit.redstone.RedstoneConduitNetwork;
import io.lylix.lya.integration.computer.IComputer;
import mekanism.api.Coord4D;
import mrtjp.projectred.api.IBundledTile;

import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Optional.Interface(iface="mrtjp.projectred.api.IBundledTile", modid="projectred-transmission")
public class TileNode extends TileEntity implements IBundledTile, IComputer
{
    private final static HashMap<DyeColor,DyeColor> TABLE = new HashMap<DyeColor, DyeColor>()
    {{
        put(DyeColor.WHITE, DyeColor.BLACK);
        put(DyeColor.ORANGE, DyeColor.RED);
        put(DyeColor.MAGENTA, DyeColor.GREEN);
        put(DyeColor.LIGHT_BLUE, DyeColor.BROWN);
        put(DyeColor.YELLOW, DyeColor.BLUE);
        put(DyeColor.LIME, DyeColor.PURPLE);
        put(DyeColor.PINK, DyeColor.CYAN);
        put(DyeColor.GRAY, DyeColor.SILVER);
        put(DyeColor.SILVER, DyeColor.GRAY);
        put(DyeColor.CYAN, DyeColor.PINK);
        put(DyeColor.PURPLE, DyeColor.LIME);
        put(DyeColor.BLUE, DyeColor.YELLOW);
        put(DyeColor.BROWN, DyeColor.LIGHT_BLUE);
        put(DyeColor.GREEN, DyeColor.MAGENTA);
        put(DyeColor.RED, DyeColor.ORANGE);
        put(DyeColor.BLACK, DyeColor.WHITE);
    }};

    public final static int DIRECTION_BIT = 1;
    public final static int CONNECTED_BIT = 2;

    public final static int BUNDLED_TO_CONDUIT = 0;
    public final static int CONDUIT_TO_BUNDLED = 1;

    public int[] signalConfig = new int[16];

    private byte[] bundledOutput = new byte[16];

    private Set<RedstoneConduitNetwork> conduitNetworks = new HashSet<>();

    private BitSet maskBundled;
    private BitSet maskConduit;

    private BitSet power;

    public TileNode()
    {
        for(int i = 0; i < 16; i++)
        {
            signalConfig[i] = BUNDLED_TO_CONDUIT;
        }

        maskBundled = new BitSet(16);
        maskConduit = new BitSet(16);
        power = new BitSet(16);

        genMask();
    }

    private void genMask()
    {
        maskBundled.clear();
        maskConduit.clear();

        for(int i = 0; i < 16; i++)
        {
            switch (signalConfig[i])
            {
                case CONNECTED_BIT | BUNDLED_TO_CONDUIT:
                    maskBundled.set(i);
                    break;
                case CONNECTED_BIT | CONDUIT_TO_BUNDLED:
                    maskConduit.set(i);
                    break;
            }
        }
    }

    // CONFIGURATION
    public void toggleSignalDirection(int i)
    {
        int s = signalConfig[i];
        if((s & CONNECTED_BIT) == 0) setSignalConfig(i, s | CONNECTED_BIT);
        else setSignalConfig(i, s ^ DIRECTION_BIT);
    }

    public void toggleSignalConnected(int i)
    {
        setSignalConfig(i, signalConfig[i] ^ CONNECTED_BIT);
    }

    public void setSignalConfig(int i, int state)
    {
        switch(signalConfig[i])
        {
            case CONNECTED_BIT | BUNDLED_TO_CONDUIT:
                resetAllConduitStrength(DyeColor.fromIndex(i));
                break;
            case CONNECTED_BIT | CONDUIT_TO_BUNDLED:
                resetAllBundledStrength(i);
                break;
        }
        signalConfig[i] = state;
        genMask();
        synchronize();
    }

    // SYNCHRONIZATION
    private void synchronize()
    {
        IBlockState state = world.getBlockState(getPos());
        world.notifyBlockUpdate(getPos(), state, state, 3);
        markDirty();

        world.notifyNeighborsOfStateChange(getPos(), getBlockType(), false);
    }

    private void write(NBTTagCompound data)
    {
        data.setIntArray("config", signalConfig);
    }

    private void read(NBTTagCompound data)
    {
        if(data.hasKey("config")) signalConfig = data.getIntArray("config");
        genMask();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        read(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        write(compound);
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.write(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.read(pkt.getNbtCompound());
    }

    //SEARCH
    public void findChildren()
    {
        conduitNetworks.clear();
        for(EnumFacing side : EnumFacing.values())
        {
            TileEntity te = Coord4D.get(this).offset(side).getTileEntity(getWorld());
            if(te instanceof IConduitBundle)
            {
                IConduitBundle bundle = IConduitBundle.class.cast(te);
                IRedstoneConduit conduit = bundle.getConduit(IRedstoneConduit.class);
                if(conduit == null || conduit.getNetwork() == null) continue;
                conduitNetworks.add(conduit.getNetwork());
            }
        }
    }

    // OUTPUT
    @Override
    public boolean canConnectBundled(int side)
    {
        return true;
    }

    @Override
    public byte[] getBundledSignal(int dir)
    {
        readConduit();
        return bundledOutput;
    }

    // MAIN LOOP
    public void onNeighborBlockChange()
    {
        readBundled();
    }

    // INPUT
    private void readBundled()
    {
        power.clear();

        List<DyeColor> mask = maskBundled.stream().mapToObj(DyeColor::fromIndex).collect(Collectors.toList());

        for(EnumFacing side : EnumFacing.values())
        {
            byte[] signal = ProjectRedAPI.transmissionAPI.getBundledInput(world, pos, side);
            if(signal == null) continue;
            for(DyeColor wire : mask)
            {
                if(signal[wire.ordinal()] != 0) power.set(wire.ordinal());
            }
        }

        transferConduit(power);
    }

    private void readConduit()
    {
        power.clear();

        List<DyeColor> mask = maskConduit.stream().mapToObj(DyeColor::fromIndex).collect(Collectors.toList());

        conduitNetworks.forEach(net -> mask.forEach(color -> { if(net.getSignalStrengthForColor(TABLE.get(color)) > 0) power.set(color.ordinal()); }));

        transferBundled(power);
    }

    // UTILITIES
    private void writeBundledStrength(DyeColor color, boolean power)
    {
        bundledOutput[color.ordinal()] = (byte) (power ? 255 : 0);
    }

    private void writeConduitStrength(RedstoneConduitNetwork network, DyeColor color, boolean power)
    {
        if(power) network.getBundledSignal().getSignal(TABLE.get(color)).addStrength(15);
        else network.getBundledSignal().getSignal(TABLE.get(color)).resetSignal();
    }

    private void writeAllBundledStrength(DyeColor color, BitSet power)
    {
        writeBundledStrength(color, power.get(color.ordinal()));
    }

    private void writeAllConduitStrength(DyeColor color, BitSet power)
    {
        conduitNetworks.forEach(net -> writeConduitStrength(net, color, power.get(color.ordinal())));
    }

    private void resetAllConduitStrength(DyeColor color)
    {
        conduitNetworks.forEach(net -> writeConduitStrength(net, color, false));
    }

    private void resetAllBundledStrength(int color)
    {
        bundledOutput[color] = 0;
    }

    private void transferBundled(BitSet wires)
    {
        maskConduit.stream().mapToObj(DyeColor::fromIndex).forEach(color -> writeAllBundledStrength(color, wires));
    }

    private void transferConduit(BitSet wires)
    {
        maskBundled.stream().mapToObj(DyeColor::fromIndex).forEach(color -> writeAllConduitStrength(color, wires));
    }

    // COMPUTER
    private final static String[] METHODS = {"connected", "direction", "isConnected", "isBundled", "isConduit", "reset"};

    @Override
    public String getName()
    {
        return "bundled";
    }

    @Override
    public String[] getMethods()
    {
        return METHODS;
    }

    @Override
    public Object[] invoke(int method, Object[] args) throws Exception
    {
        if(method == 5)
        {
            reset();
            return new Object[]{};
        }

        if(args[0] instanceof Double)
        {
            Double i = (Double)args[0];
            switch(method)
            {
                case 0:
                    toggleSignalConnected(i.intValue());
                    return new Object[]{};
                case 1:
                    toggleSignalDirection(i.intValue());
                    return new Object[]{};
                case 2:
                    return new Object[]{isConnected(i.intValue())};
                case 3:
                    return new Object[]{maskBundled.get(i.intValue())};
                case 4:
                    return new Object[]{maskConduit.get(i.intValue())};
                default:
                    throw new NoSuchMethodException();
            }
        }
        return new Object[]{"Missing number"};
    }

    private boolean isConnected(int i)
    {
        BitSet set = new BitSet();
        set.or(maskBundled);
        set.or(maskConduit);
        return set.get(i);
    }

    private void reset()
    {
        for(int i = 0; i < 16; i++) setSignalConfig(i, BUNDLED_TO_CONDUIT | CONNECTED_BIT);
    }
}
