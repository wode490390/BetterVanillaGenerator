package cn.wode490390.nukkit.vanillagenerator.noise.bukkit;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;

/**
 * Creates perlin noise through unbiased octaves
 */
public class PerlinOctaveGenerator extends OctaveGenerator {

    /**
     * Creates a perlin octave generator for the given world
     *
     * @param world World to construct this generator for
     * @param octaves Amount of octaves to create
     */
    public PerlinOctaveGenerator(Level world, int octaves) {
        this(new NukkitRandom(world.getSeed()), octaves);
    }

    /**
     * Creates a perlin octave generator for the given level
     *
     * @param level Level to construct this generator for
     * @param octaves Amount of octaves to create
     */
    public PerlinOctaveGenerator(ChunkManager level, int octaves) {
        this(new NukkitRandom(level.getSeed()), octaves);
    }

    /**
     * Creates a perlin octave generator for the given world
     *
     * @param seed Seed to construct this generator for
     * @param octaves Amount of octaves to create
     */
    public PerlinOctaveGenerator(long seed, int octaves) {
        this(new NukkitRandom(seed), octaves);
    }

    /**
     * Creates a perlin octave generator for the given {@link NukkitRandom}
     *
     * @param rand NukkitRandom object to construct this generator for
     * @param octaves Amount of octaves to create
     */
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
