package io.lylix.lya.chunkloader;

import net.minecraft.util.math.ChunkPos;

import java.util.Set;
import java.util.UUID;

public interface IChunkLoader
{
    Set<ChunkPos> getChunkSet();

    ChunkLoader getChunkLoader();

    Set<UUID> getPresences();

    boolean getState();
}
