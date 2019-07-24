package cn.wode490390.nukkit.vanillagenerator.biomegrid;

import cn.nukkit.level.biome.EnumBiome;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MapLayerBiomeEdgeThin extends MapLayer {

    private static final Set<Integer> OCEANS = Sets.newHashSet();
    private static final Map<Integer, Integer> MESA_EDGES = Maps.newHashMap();
    private static final Map<Integer, Integer> JUNGLE_EDGES = Maps.newHashMap();
    private static final Map<Map<Integer, Integer>, List<Integer>> EDGES = Maps.newHashMap();

    static {
        OCEANS.add(EnumBiome.OCEAN.id);
        OCEANS.add(EnumBiome.DEEP_OCEAN.id);

        MESA_EDGES.put(EnumBiome.MESA.id, EnumBiome.DESERT.id);
        MESA_EDGES.put(EnumBiome.MESA_BRYCE.id, EnumBiome.DESERT.id);
        MESA_EDGES.put(EnumBiome.MESA_PLATEAU_F.id, EnumBiome.DESERT.id);
        MESA_EDGES.put(EnumBiome.MESA_PLATEAU_F_M.id, EnumBiome.DESERT.id);
        MESA_EDGES.put(EnumBiome.MESA_PLATEAU.id, EnumBiome.DESERT.id);
        MESA_EDGES.put(EnumBiome.MESA_PLATEAU_M.id, EnumBiome.DESERT.id);

        JUNGLE_EDGES.put(EnumBiome.JUNGLE.id, EnumBiome.JUNGLE_EDGE.id);
        JUNGLE_EDGES.put(EnumBiome.JUNGLE_HILLS.id, EnumBiome.JUNGLE_EDGE.id);
        JUNGLE_EDGES.put(EnumBiome.JUNGLE_M.id, EnumBiome.JUNGLE_EDGE.id);
        JUNGLE_EDGES.put(EnumBiome.JUNGLE_EDGE_M.id, EnumBiome.JUNGLE_EDGE.id);

        EDGES.put(MESA_EDGES, null);
        EDGES.put(JUNGLE_EDGES, Arrays.asList(EnumBiome.JUNGLE.id, EnumBiome.JUNGLE_HILLS.id, EnumBiome.JUNGLE_M.id, EnumBiome.JUNGLE_EDGE_M.id, EnumBiome.FOREST.id, EnumBiome.TAIGA.id));
    }

    private final MapLayer belowLayer;

    public MapLayerBiomeEdgeThin(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = this.belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                // This applies biome thin edges using Von Neumann neighborhood
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                int val = centerVal;
                for (Entry<Map<Integer, Integer>, List<Integer>> entry : EDGES.entrySet()) {
                    Map<Integer, Integer> map = entry.getKey();
                    if (map.containsKey(centerVal)) {
                        int upperVal = values[j + 1 + i * gridSizeX];
                        int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                        int leftVal = values[j + (i + 1) * gridSizeX];
                        int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                        List<Integer> entryValue = entry.getValue();
                        if (entryValue == null && (!OCEANS.contains(upperVal) && !map.containsKey(upperVal) || !OCEANS.contains(lowerVal) && !map.containsKey(lowerVal) || !OCEANS.contains(leftVal) && !map.containsKey(leftVal) || !OCEANS.contains(rightVal) && !map.containsKey(rightVal))) {
                            val = map.get(centerVal);
                            break;
                        } else if (entryValue != null && (!OCEANS.contains(upperVal) && !entryValue.contains(upperVal) || !OCEANS.contains(lowerVal) && !entryValue.contains(lowerVal) || !OCEANS.contains(leftVal) && !entryValue.contains(leftVal) || !OCEANS.contains(rightVal) && !entryValue.contains(rightVal))) {
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
