package cn.wode490390.nukkit.vanillagenerator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.wode490390.nukkit.vanillagenerator.noise.PerlinOctaveGenerator;
import cn.wode490390.nukkit.vanillagenerator.noise.bukkit.OctaveGenerator;
import cn.wode490390.nukkit.vanillagenerator.populator.nether.PopulatorFire;
import cn.wode490390.nukkit.vanillagenerator.populator.nether.PopulatorGlowstone;
import cn.wode490390.nukkit.vanillagenerator.populator.nether.PopulatorLava;
import cn.wode490390.nukkit.vanillagenerator.populator.nether.PopulatorMushroom;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class NetherGenerator extends VanillaGenerator {

    protected static double coordinateScale = getConfig("nether.coordinate-scale", 684.412d);
    protected static double heightScale = getConfig("nether.height.scale", 2053.236d);
    protected static double heightNoiseScaleX = getConfig("nether.height.noise-scale.x", 100d); // depthNoiseScaleX
    protected static double heightNoiseScaleZ = getConfig("nether.height.noise-scale.x", 100d); // depthNoiseScaleZ
    protected static double detailNoiseScaleX = getConfig("nether.detail.noise-scale.x", 80d);  // mainNoiseScaleX
    protected static double detailNoiseScaleY = getConfig("nether.detail.noise-scale.y", 60d);  // mainNoiseScaleY
    protected static double detailNoiseScaleZ = getConfig("nether.detail.noise-scale.z", 80d);  // mainNoiseScaleZ
    protected static double surfaceScale = getConfig("nether.surface-scale", 0.0625d);

    protected final Map<String, Map<String, OctaveGenerator>> octaveCache = Maps.newHashMap();
    protected final double[][][] density = new double[5][5][17];

    protected ChunkManager level;
    protected NukkitRandom nukkitRandom;
    protected final List<Populator> populators = Lists.newArrayList();
    protected List<Populator> generationPopulators = Lists.newArrayList();

    protected long localSeed1;
    protected long localSeed2;

    public NetherGenerator() {

    }

    public NetherGenerator(Map<String, Object> options) {

    }

    @Override
    public int getId() {
        return Generator.TYPE_NETHER;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_NETHER;
    }

    @Override
    public String getName() {
        return "nether";
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.level;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = ThreadLocalRandom.current().nextLong();
        this.localSeed2 = ThreadLocalRandom.current().nextLong();

        PopulatorLava lava = new PopulatorLava();
        lava.setAmount(16);
        this.populators.add(lava);

        PopulatorLava flowingLava = new PopulatorLava(true);
        flowingLava.setAmount(8);
        this.populators.add(flowingLava);

        PopulatorGlowstone glowstone1 = new PopulatorGlowstone();
        glowstone1.setAmount(1);
        this.populators.add(glowstone1);

        PopulatorGlowstone glowstone2 = new PopulatorGlowstone(true);
        glowstone2.setAmount(1);
        this.populators.add(glowstone2);

        PopulatorMushroom brownMushroom = new PopulatorMushroom(BROWN_MUSHROOM);
        brownMushroom.setAmount(1);
        this.populators.add(brownMushroom);

        PopulatorMushroom redMushroom = new PopulatorMushroom(RED_MUSHROOM);
        redMushroom.setAmount(1);
        this.populators.add(redMushroom);

        PopulatorFire fire = new PopulatorFire();
        fire.setAmount(1);
        this.populators.add(fire);

        PopulatorOre ores = new PopulatorOre(Block.NETHERRACK, new OreType[]{
                new OreType(Block.get(LAVA), 32, 1, 0, 32, NETHERRACK),
                new OreType(Block.get(QUARTZ_ORE), 13, 16, 10, 118, NETHERRACK),
                new OreType(Block.get(MAGMA), 32, 16, 26, 37, NETHERRACK),
        });
        this.populators.add(ores);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        int cx = chunkX << 4;
        int cz = chunkZ << 4;
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());

        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        int densityX = chunkX << 2;
        int densityZ = chunkZ << 2;

        Map<String, OctaveGenerator> octaves = this.getWorldOctaves();
        //double[] heightNoise = ((PerlinOctaveGenerator) octaves.get("height")).getFractalBrownianMotion(densityX, densityZ, 0.5d, 2d);
        double[] roughnessNoise = ((PerlinOctaveGenerator) octaves.get("roughness")).getFractalBrownianMotion(densityX, 0, densityZ, 0.5d, 2d);
        double[] roughnessNoise2 = ((PerlinOctaveGenerator) octaves.get("roughness2")).getFractalBrownianMotion(densityX, 0, densityZ, 0.5d, 2d);
        double[] detailNoise = ((PerlinOctaveGenerator) octaves.get("detail")).getFractalBrownianMotion(densityX, 0, densityZ, 0.5d, 2d);

        double[] nv = new double[17];
        for (int i = 0; i < 17; i++) {
            nv[i] = Math.cos(i * Math.PI * 6d / 17d) * 2d;
            double nh = i > 17 / 2 ? 17 - 1 - i : i;
            if (nh < 4d) {
                nh = 4d - nh;
                nv[i] -= nh * nh * nh * 10d;
            }
        }

        int index = 0;
        //int indexHeight = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                /*double noiseH = heightNoise[indexHeight++] / 8000d;
                if (noiseH < 0) {
                    noiseH = Math.abs(noiseH);
                }
                noiseH = noiseH * 3d - 3d;
                if (noiseH < 0) {
                    noiseH = Math.max(noiseH * 0.5d, -1) / 1.4d * 0.5d;
                } else {
                    noiseH = Math.min(noiseH, 1) / 6d;
                }
                noiseH = noiseH * 17 / 16d;*/
                for (int k = 0; k < 17; k++) {
                    double noiseR = roughnessNoise[index] / 512d;
                    double noiseR2 = roughnessNoise2[index] / 512d;
                    double noiseD = (detailNoise[index] / 10d + 1d) / 2d;
                    double nh = nv[k];
                    // linear interpolation
                    double dens = noiseD < 0 ? noiseR : noiseD > 1 ? noiseR2 : noiseR + (noiseR2 - noiseR) * noiseD;
                    dens -= nh;
                    index++;
                    if (k > 13) {
                        double lowering = (k - 13) / 3d;
                        dens = dens * (1d - lowering) + lowering * -10d;
                    }
                    this.density[i][j][k] = dens;
                }
            }
        }

        for (int i = 0; i < 5 - 1; i++) {
            for (int j = 0; j < 5 - 1; j++) {
                for (int k = 0; k < 17 - 1; k++) {
                    double d1 = this.density[i][j][k];
                    double d2 = this.density[i + 1][j][k];
                    double d3 = this.density[i][j + 1][k];
                    double d4 = this.density[i + 1][j + 1][k];
                    double d5 = (this.density[i][j][k + 1] - d1) / 8;
                    double d6 = (this.density[i + 1][j][k + 1] - d2) / 8;
                    double d7 = (this.density[i][j + 1][k + 1] - d3) / 8;
                    double d8 = (this.density[i + 1][j + 1][k + 1] - d4) / 8;

                    for (int l = 0; l < 8; l++) {
                        double d9 = d1;
                        double d10 = d3;
                        for (int m = 0; m < 4; m++) {
                            double dens = d9;
                            for (int n = 0; n < 4; n++) {
                                // any density higher than 0 is ground, any density lower or equal to 0 is air (or lava if under the lava level).
                                if (dens > 0) {
                                    chunk.setBlock(m + (i << 2), l + (k << 3), n + (j << 2), NETHERRACK);
                                } else if (l + (k << 3) < 32) {
                                    chunk.setBlock(m + (i << 2), l + (k << 3), n + (j << 2), STILL_LAVA);
                                    chunk.setBlockLight(m + (i << 2), l + (k << 3) + 1, n + (j << 2), Block.light[STILL_LAVA]);
                                }
                                // interpolation along z
                                dens += (d10 - d9) / 4;
                            }
                            // interpolation along x
                            d9 += (d2 - d1) / 4;
                            // interpolate along z
                            d10 += (d4 - d3) / 4;
                        }
                        // interpolation along y
                        d1 += d5;
                        d3 += d7;
                        d2 += d6;
                        d4 += d8;
                    }
                }
            }
        }

        double[] surfaceNoise = ((PerlinOctaveGenerator) getWorldOctaves().get("surface")).getFractalBrownianMotion(cx, cz, 0, 0.5d, 2d);
        double[] soulsandNoise = ((PerlinOctaveGenerator) getWorldOctaves().get("soulsand")).getFractalBrownianMotion(cx, cz, 0, 0.5d, 2d);
        double[] gravelNoise = ((PerlinOctaveGenerator) getWorldOctaves().get("gravel")).getFractalBrownianMotion(cx, 0, cz, 0.5d, 2d);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBiomeId(x, z, EnumBiome.HELL.biome.getId());

                int columnX = (cx + x) & 0xf;
                int columnZ = (cz + z) & 0xf;

                int topMat = NETHERRACK;
                int groundMat = NETHERRACK;

                boolean soulSand = soulsandNoise[x | z << 4] + this.nukkitRandom.nextDouble() * 0.2d > 0;
                boolean gravel = gravelNoise[x | z << 4] + this.nukkitRandom.nextDouble() * 0.2d > 0;

                int surfaceHeight = (int) (surfaceNoise[x | z << 4] / 3d + 3d + this.nukkitRandom.nextDouble() * 0.25d);
                int deep = -1;
                for (int y = 127; y >= 0; y--) {
                    if (y <= this.nukkitRandom.nextBoundedInt(5) || y >= 127 - this.nukkitRandom.nextBoundedInt(5)) {
                        chunk.setBlock(columnX, y, columnZ, BEDROCK);
                        continue;
                    }
                    int mat = chunk.getBlockId(columnX, y, columnZ);
                    if (mat == AIR) {
                        deep = -1;
                    } else if (mat == NETHERRACK) {
                        if (deep == -1) {
                            if (surfaceHeight <= 0) {
                                topMat = AIR;
                                groundMat = NETHERRACK;
                            } else if (y >= 60 && y <= 65) {
                                topMat = NETHERRACK;
                                groundMat = NETHERRACK;
                                if (gravel) {
                                    topMat = GRAVEL;
                                    groundMat = NETHERRACK;
                                }
                                if (soulSand) {
                                    topMat = SOUL_SAND;
                                    groundMat = SOUL_SAND;
                                }
                            }

                            deep = surfaceHeight;
                            if (y >= 63) {
                                chunk.setBlock(columnX, y, columnZ, topMat);
                            } else {
                                chunk.setBlock(columnX, y, columnZ, groundMat);
                            }
                        } else if (deep > 0) {
                            deep--;
                            chunk.setBlock(columnX, y, columnZ, groundMat);
                        }
                    }
                }
            }
        }

        this.generationPopulators.forEach((populator) -> {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        });
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        this.nukkitRandom.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        this.populators.forEach((populator) -> {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        });
        Biome.getBiome(chunk.getBiomeId(7, 7)).populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 64, 0.5);
    }

    /**
     * Returns the {@link OctaveGenerator} instances for the world, which are
     * either newly created or retrieved from the cache.
     *
     * @return A map of {@link OctaveGenerator}s
     */
    protected Map<String, OctaveGenerator> getWorldOctaves() {
        Map<String, OctaveGenerator> octaves = this.octaveCache.get(this.getName());
        if (octaves == null) {
            octaves = Maps.newHashMap();
            NukkitRandom seed = new NukkitRandom(this.level.getSeed());

            /*OctaveGenerator gen = new PerlinOctaveGenerator(seed, 16, 5, 5);
            gen.setXScale(heightNoiseScaleX);
            gen.setZScale(heightNoiseScaleZ);
            octaves.put("height", gen);*/

            OctaveGenerator gen = new PerlinOctaveGenerator(seed, 16, 5, 17, 5);
            gen.setXScale(coordinateScale);
            gen.setYScale(heightScale);
            gen.setZScale(coordinateScale);
            octaves.put("roughness", gen);

            gen = new PerlinOctaveGenerator(seed, 16, 5, 17, 5);
            gen.setXScale(coordinateScale);
            gen.setYScale(heightScale);
            gen.setZScale(coordinateScale);
            octaves.put("roughness2", gen);

            gen = new PerlinOctaveGenerator(seed, 8, 5, 17, 5);
            gen.setXScale(coordinateScale / detailNoiseScaleX);
            gen.setYScale(heightScale / detailNoiseScaleY);
            gen.setZScale(coordinateScale / detailNoiseScaleZ);
            octaves.put("detail", gen);

            gen = new PerlinOctaveGenerator(seed, 4, 16, 16, 1);
            gen.setScale(surfaceScale);
            octaves.put("surface", gen);

            gen = new PerlinOctaveGenerator(seed, 4, 16, 16, 1);
            gen.setXScale(surfaceScale / 2d);
            gen.setYScale(surfaceScale / 2d);
            octaves.put("soulsand", gen);

            gen = new PerlinOctaveGenerator(seed, 4, 16, 1, 16);
            gen.setXScale(surfaceScale / 2d);
            gen.setZScale(surfaceScale / 2d);
            octaves.put("gravel", gen);

            this.octaveCache.put(this.getName(), octaves);
        }
        return octaves;
    }
}
