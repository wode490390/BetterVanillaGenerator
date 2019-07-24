package cn.wode490390.nukkit.vanillagenerator.biomegrid;

import cn.nukkit.level.biome.EnumBiome;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapLayerBiomeEdge extends MapLayer {

    private static final Map<Integer, Integer> MESA_EDGES = Maps.newHashMap();
    private static final Map<Integer, Integer> MEGA_TAIGA_EDGES = Maps.newHashMap();
    private static final Map<Integer, Integer> DESERT_EDGES = Maps.newHashMap();
    private static final Map<Integer, Integer> SWAMP1_EDGES = Maps.newHashMap();
    private static final Map<Integer, Integer> SWAMP2_EDGES = Maps.newHashMap();
    private static final Map<Map<Integer, Integer>, List<Integer>> EDGES = Maps.newHashMap();

    static {
        MESA_EDGES.put(EnumBiome.MESA_PLATEAU_F.id, EnumBiome.MESA.id);
        MESA_EDGES.put(EnumBiome.MESA_PLATEAU.id, EnumBiome.MESA.id);

        MEGA_TAIGA_EDGES.put(EnumBiome.MEGA_TAIGA.id, EnumBiome.TAIGA.id);

        DESERT_EDGES.put(EnumBiome.DESERT.id, EnumBiome.EXTREME_HILLS_PLUS.id);

        SWAMP1_EDGES.put(EnumBiome.SWAMP.id, EnumBiome.PLAINS.id);
        SWAMP2_EDGES.put(EnumBiome.SWAMP.id, EnumBiome.JUNGLE_EDGE.id);

        EDGES.put(MESA_EDGES, null);
        EDGES.put(MEGA_TAIGA_EDGES, null);
        EDGES.put(DESERT_EDGES, Arrays.asList(EnumBiome.ICE_PLAINS.id));
        EDGES.put(SWAMP1_EDGES, Arrays.asList(EnumBiome.DESERT.id, EnumBiome.COLD_TAIGA.id, EnumBiome.ICE_PLAINS.id));
        EDGES.put(SWAMP2_EDGES, Arrays.asList(EnumBiome.JUNGLE.id));
    }

    private final MapLayer belowLayer;

    public MapLayerBiomeEdge(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                // This applies biome large edges using Von Neumann neighborhood
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                int val = centerVal;
                for (Entry<Map<Integer, Integer>, List<Integer>> entry : EDGES.entrySet()) {
                    Map<Integer, Integer> map = entry.getKey();
                    if (map.containsKey(centerVal)) {
                        int upperVal = values[j + 1 + i * gridSizeX];
                        int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                        int leftVal = values[j + (i + 1) * gridSizeX];
                        int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                        if (entry.getValue() == null && (!map.containsKey(upperVal) || !map.containsKey(lowerVal) || !map.containsKey(leftVal) || !map.containsKey(rightVal))) {
                            val = map.get(centerVal);
                            break;
                        } else if (entry.getValue() != null && (entry.getValue().contains(upperVal) || entry.getValue().contains(lowerVal) || entry.getValue().contains(leftVal) || entry.getValue().contains(rightVal))) {
                            val = map.get(centerVal);
                            break;
                        }
                    }
                }

                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }
}
