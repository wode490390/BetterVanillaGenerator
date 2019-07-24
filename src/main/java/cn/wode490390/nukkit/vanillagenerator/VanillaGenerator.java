package cn.wode490390.nukkit.vanillagenerator;

import cn.nukkit.level.generator.Generator;
import com.google.common.collect.Maps;
import java.util.Map;

public abstract class VanillaGenerator extends Generator {

    public static final int TYPE_LARGE_BIOMES = 5;
    public static final int TYPE_AMPLIFIED = 6;

    protected static final int SEA_LEVEL = getConfig("general.sea_level", 64);

    public static <T> T getConfig(String variable) {
        return getConfig(variable, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(String variable, T defaultValue) {
        Object value = BetterGenerator.config.get("generator." + variable);
        return value == null ? defaultValue : (T) value;
    }

    @Override
    public Map<String, Object> getSettings() {
        return Maps.newHashMap();
    }
}
