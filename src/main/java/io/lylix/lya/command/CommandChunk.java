package io.lylix.lya.command;

import io.lylix.lya.LYA;
import io.lylix.lya.chunkloader.ChunkLoader;
import io.lylix.lya.chunkloader.IChunkLoader;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CommandChunk extends CommandBase
{
    private final List<String> aliases;
    private static final String tag = TextFormatting.GOLD + "[" + LYA.ID + "] ";

    private Set<IChunkLoader> loaders;
    private ICommandSender sender;

    public CommandChunk()
    {
        aliases = Arrays.asList(LYA.ID, "cl");
        loaders = LYA.proxy.getChunkManager().getLoaders();
    }

    @Override
    public String getName()
    {
        return "chunkloader";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "chunkloader [world, name] [dim, player]";
    }

    @Override
    public List<String> getAliases()
    {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(!sender.canUseCommand(4, "lya.chunkloader.admin")) return;
        this.sender = sender;

        if(args.length == 0) sender.sendMessage(new TextComponentString(tag + TextFormatting.WHITE + "Hello!"));
        else
        {
            switch(args[0])
            {
                case "name":
                    if(checkSize(args, "name")) byPlayer(args[1], checkPrint(args));
                    break;
                case "world":
                    if(checkSize(args, "world")) byWorld(args[1], checkPrint(args));
                    break;
                /*case "result":
                    if(checkSize(args, "result")) byResult(args[1], checkPrint(args));
                    break;*/
                default:
                    sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error unknown command!"));
                    break;
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if(args.length == 1) return Arrays.asList("name", "world"/*, "result"*/);
        switch(args[0])
        {
            case "name":
                return Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getUsernames()).collect(Collectors.toList());
            case "world":
                return Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().worlds).map(w -> String.valueOf(w.provider.getDimension())).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

    private boolean checkSize(String[] args, String param)
    {
        if(args.length > 1) return true;
        sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error forgot " + param + "!"));
        return false;
    }

    private boolean checkPrint(String[] args)
    {
        return args.length == 3 && args[2].equals("print");
    }

    private void print(ChunkLoader loader)
    {
        sender.sendMessage(new TextComponentString(loader.toString()));
    }

    private void byWorld(String s, boolean print)
    {
        int dim;
        try
        {
            dim = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error parsing dimension!"));
            return;
        }

        WorldServer world = DimensionManager.getWorld(dim);
        if(world == null)
        {
            sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error unknown world!"));
            return;
        }

        List<ChunkLoader> cache =  loaders.stream().map(IChunkLoader::getChunkLoader).filter(te -> te.contains(dim)).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder(tag);
        builder.append(TextFormatting.GREEN);
        builder.append(world.getWorldInfo().getWorldName());
        builder.append(TextFormatting.WHITE);
        builder.append(" contains ");
        builder.append(TextFormatting.GREEN);
        builder.append(cache.size());
        builder.append(TextFormatting.WHITE);
        builder.append(" chunk ");
        builder.append(cache.size() > 1 ? "loaders." : "loader.");
        sender.sendMessage(new TextComponentString(builder.toString()));
        if(print) cache.forEach(this::print);
    }

    private void byPlayer(String name, boolean print)
    {
        GameProfile gp = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(name);
        List<ChunkLoader> cache = loaders.stream().map(IChunkLoader::getChunkLoader).filter(te -> te.contains(gp)).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder(tag);
        builder.append(TextFormatting.GREEN);
        builder.append(name);
        builder.append(TextFormatting.WHITE);
        builder.append(" has ");
        builder.append(TextFormatting.GREEN);
        builder.append(cache.size());
        builder.append(TextFormatting.WHITE);
        builder.append(" chunk ");
        builder.append(cache.size() > 1 ? "loaders" : "loader");
        builder.append(" bound to him.");
        sender.sendMessage(new TextComponentString(builder.toString()));
        if(print) cache.forEach(this::print);
    }

    /*private void byResult(String s, boolean print)
    {
        int limit;
        try
        {
            limit = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error parsing limit!"));
            return;
        }

        StringBuilder builder = new StringBuilder();

        if(Loader.isModLoaded("laggoggles"))
        {
            List<ProfileResultHandler.ChunkTick> chunkTickList = ProfileResultHandler.pick(limit);
            if(chunkTickList.isEmpty()) sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error no results!"));
            for(ProfileResultHandler.ChunkTick ct : chunkTickList)
            {
                builder.append(tag).append(TextFormatting.GREEN);
                builder.append("Chunkloader ").append(ct.chunk.stringifyPos());
                builder.append(TextFormatting.WHITE).append(" = ").append(TextFormatting.RED);
                builder.append(Calculations.muPerTickString(ct.nanos, ProfileResultHandler.getResult(ct.chunk)));
                sender.sendMessage(new TextComponentString(builder.toString()));
                builder.setLength(0);
            }
        }
        else sender.sendMessage(new TextComponentString(tag + TextFormatting.RED + "Error mod LagGoggles is required!"));
    }*/
}
