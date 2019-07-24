package cn.wode490390.nukkit.vanillagenerator;

import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class BetterGenerator extends PluginBase {

    private static final String CONFIG_OVERWORLD = "replace.overworld";
    private static final String CONFIG_NETHER = "replace.nether";

    protected static Config config;

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this);
        } catch (Exception ignore) {

        }
        this.saveDefaultConfig();
        config = this.getConfig();
        String node = CONFIG_OVERWORLD;
        boolean overworld;
        try {
            overworld = config.getBoolean(node, true);
        } catch (Exception e) {
            overworld = true;
            this.logLoadException(node);
        }
        if (overworld) {
            Generator.addGenerator(NormalGenerator.class, "default", VanillaGenerator.TYPE_INFINITE);
            Generator.addGenerator(NormalGenerator.class, "normal", VanillaGenerator.TYPE_INFINITE);
        }
        node = CONFIG_NETHER;
        boolean nether;
        try {
            nether = config.getBoolean(node, true);
        } catch (Exception e) {
            nether = true;
            this.logLoadException(node);
        }
        if (nether) {
            Generator.addGenerator(NetherGenerator.class, "nether", VanillaGenerator.TYPE_NETHER);
        }
    }

    private void logLoadException(String node) {
        this.getLogger().alert("An error occurred while reading the configuration '" + node + "'. Use the default value.");
    }
}
