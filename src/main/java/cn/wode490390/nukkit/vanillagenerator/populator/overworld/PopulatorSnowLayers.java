package cn.wode490390.nukkit.vanillagenerator.populator.overworld;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class PopulatorSnowLayers extends Populator {

    protected static final boolean[] coverableBiome = new boolean[256];
    protected static final boolean[] uncoverableBlock = new boolean[256];

    static {
        coverableBiome[EnumBiome.ICE_PLAINS.id] = true;
        coverableBiome[EnumBiome.ICE_PLAINS_SPIKES.id] = true;
        coverableBiome[EnumBiome.COLD_BEACH.id] = true;
        coverableBiome[EnumBiome.COLD_TAIGA.id] = true;
        coverableBiome[EnumBiome.COLD_TAIGA_HILLS.id] = true;
        coverableBiome[EnumBiome.COLD_TAIGA_M.id] = true;

        uncoverableBlock[WATER] = true;
        uncoverableBlock[STILL_WATER] = true;
        uncoverableBlock[LAVA] = true;
        uncoverableBlock[STILL_LAVA] = true;
        uncoverableBlock[TALL_GRASS] = true;
        uncoverableBlock[DEAD_BUSH] = true;
        uncoverableBlock[DANDELION] = true;
        uncoverableBlock[RED_FLOWER] = true;
        uncoverableBlock[BROWN_MUSHROOM] = true;
        uncoverableBlock[RED_MUSHROOM] = true;
        uncoverableBlock[SNOW_LAYER] = true; //existed
        uncoverableBlock[ICE] = true;
        uncoverableBlock[CACTUS] = true;
        uncoverableBlock[REEDS] = true;
        uncoverableBlock[VINE] = true;
        uncoverableBlock[WATER_LILY] = true;
        uncoverableBlock[COCOA] = true;
        uncoverableBlock[DOUBLE_PLANT] = true;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (coverableBiome[chunk.getBiomeId(x, z)]) {
                    int y = chunk.getHighestBlockAt(x, z);
                    if (y > 0 && y < 255 && !uncoverableBlock[chunk.getBlockId(x, y, z)]) {
                        chunk.setBlock(x, y + 1, z, SNOW_LAYER, random.nextBoundedInt(3));
                    }
                }
            }
        }
    }
}
