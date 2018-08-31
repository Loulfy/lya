package io.lylix.lya.util;

import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class LYAUtils
{
    public static Set<UUID> getOnlinePlayerUUID()
    {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            return Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerProfiles()).map(GameProfile::getId).collect(Collectors.toSet());
        }
        else return new HashSet<>();
    }
}
