package io.lylix.lya.util;

import io.lylix.lya.LYA;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class TeleportToSpawn implements ITeleporter
{
    @Override
    public void placeEntity(World world, Entity entity, float yaw)
    {
        BlockPos p = LYA.instance.config.getSpawn(world);
        entity.setLocationAndAngles(p.getX(), p.getY(), p.getZ(), entity.rotationYaw, 0.0F);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.sendMessage(new TextComponentString("Redirected to spawn..."));
    }

    @Override
    public boolean isVanilla()
    {
        return false;
    }
}
