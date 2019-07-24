package cn.wode490390.nukkit.vanillagenerator.noise.bukkit;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;

public class PerlinOctaveGenerator extends OctaveGenerator {

    public PerlinOctaveGenerator(Level world, int octaves) {
        this(new NukkitRandom(world.getSeed()), octaves);
    }

    public PerlinOctaveGenerator(ChunkManager level, int octaves) {
        this(new NukkitRandom(level.getSeed()), octaves);
    }

    public PerlinOctaveGenerator(long seed, int octaves) {
        this(new NukkitRandom(seed), octaves);
    }

    public PerlinOctaveGenerator(NukkitRandom rand, int octaves) {
        super(createOctaves(rand, octaves));
    }

    private static NoiseGenerator[] createOctaves(NukkitRandom rand, int octaves) {
        NoiseGenerator[] result = new NoiseGenerator[octaves];
        for (int i = 0; i < octaves; ++i) {
            result[i] = new PerlinNoiseGenerator(rand);
        }
        return result;
    }
}
