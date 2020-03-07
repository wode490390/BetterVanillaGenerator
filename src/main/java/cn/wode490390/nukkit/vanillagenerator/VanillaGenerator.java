package cn.wode490390.nukkit.vanillagenerator;

import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.Config;
import com.google.common.collect.Maps;
import java.util.Map;

public abstract class VanillaGenerator extends Generator {

    public static final int TYPE_LARGE_BIOMES = 5;
    public static final int TYPE_AMPLIFIED = 6;

    protected static int SEA_LEVEL;

    private static Config config;

    public static void setConfig(Config config) {
        VanillaGenerator.config = config;
        SEA_LEVEL = getConfig("general.sea_level", 64);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(String variable, T defaultValue) {
        Object value = config.get("generator." + variable);
        return value == null ? defaultValue : (T) value;
    }

    @Override
    public Map<String, Object> getSettings() {
        return Maps.newHashMap();
    }
}
