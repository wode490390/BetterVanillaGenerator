package cn.wode490390.nukkit.vanillagenerator;

import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.wode490390.nukkit.vanillagenerator.util.MetricsLite;
import cn.wode490390.nukkit.vanillagenerator.util.OpenCL;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLPlatform;

public class BetterGenerator extends PluginBase {

    private static BetterGenerator INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this, 5198);
        } catch (Throwable ignore) {

        }

        this.saveDefaultConfig();
        Config config = this.getConfig();

        VanillaGenerator.setConfig(config);

        String node = "replace.overworld";
        boolean overworld = true;
        try {
            overworld = config.getBoolean(node, overworld);
        } catch (Exception e) {
            this.logConfigException(node, e);
        }

        if (overworld) {
            Generator.addGenerator(NormalGenerator.class, "default", VanillaGenerator.TYPE_INFINITE);
            Generator.addGenerator(NormalGenerator.class, "normal", VanillaGenerator.TYPE_INFINITE);

            node = "gpu.enable";
            boolean useGPU = false;
            try {
                useGPU = config.getBoolean(node, useGPU);
            } catch (Exception e) {
                this.logConfigException(node, e);
            }

            if (useGPU) {
                node = "use-any-device";
                boolean useAnyDevice = false;
                try {
                    useAnyDevice = config.getBoolean(node, useAnyDevice);
                } catch (Exception e) {
                    this.logConfigException(node, e);
                }

                int openClMajor = 1;
                int openClMinor = 2;

                int maxGpuFlops = 0;
                int maxIntelFlops = 0;
                int maxCpuFlops = 0;
                CLPlatform bestPlatform = null;
                CLPlatform bestIntelPlatform = null;
                CLPlatform bestCpuPlatform = null;
                // gets the max flops device across platforms on the computer
                for (CLPlatform platform : CLPlatform.listCLPlatforms()) {
                    if (platform.isAtLeast(openClMajor, openClMinor) && platform.isExtensionAvailable("cl_khr_fp64")) { // NON-NLS
                        for (CLDevice device : platform.listCLDevices()) {
                            if (device.getType() == CLDevice.Type.GPU) {
                                int flops = device.getMaxComputeUnits() * device.getMaxClockFrequency();
                                this.getLogger().info("Found " + device + " with " + flops + " flops");
                                if (device.getVendor().contains("Intel")) { // NON-NLS
                                    if (flops > maxIntelFlops) {
                                        maxIntelFlops = flops;
                                        this.getLogger().info("Device is best platform so far, on " + platform);
                                        bestIntelPlatform = platform;
                                    } else if (flops == maxIntelFlops) {
                                        if (bestIntelPlatform != null && bestIntelPlatform.getVersion().compareTo(platform.getVersion()) < 0) {
                                            maxIntelFlops = flops;
                                            this.getLogger().info("Device tied for flops, but had higher version on " + platform);
                                            bestIntelPlatform = platform;
                                        }
                                    }
                                } else {
                                    if (flops > maxGpuFlops) {
                                        maxGpuFlops = flops;
                                        this.getLogger().info("Device is best platform so far, on " + platform);
                                        bestPlatform = platform;
                                    } else if (flops == maxGpuFlops) {
                                        if (bestPlatform != null && bestPlatform.getVersion().compareTo(platform.getVersion()) < 0) {
                                            maxGpuFlops = flops;
                                            this.getLogger().info("Device tied for flops, but had higher version on " + platform);
                                            bestPlatform = platform;
                                        }
                                    }
                                }
                            } else {
                                int flops = device.getMaxComputeUnits() * device.getMaxClockFrequency();
                                this.getLogger().info("Found " + device + " with " + flops + " flops");
                                if (flops > maxCpuFlops) {
                                    maxCpuFlops = flops;
                                    this.getLogger().info("Device is best platform so far, on " + platform);
                                    bestCpuPlatform = platform;
                                } else if (flops == maxCpuFlops) {
                                    if (bestCpuPlatform != null && bestCpuPlatform.getVersion().compareTo(platform.getVersion()) < 0) {
                                        maxCpuFlops = flops;
                                        this.getLogger().info("Device tied for flops, but had higher version on " + platform);
                                        bestCpuPlatform = platform;
                                    }
                                }
                            }
                        }
                    }
                }

                if (useAnyDevice) {
                    if (maxGpuFlops - maxIntelFlops < 0 && maxCpuFlops - maxIntelFlops <= 0) {
                        bestPlatform = bestIntelPlatform;
                    } else if (maxGpuFlops - maxCpuFlops < 0 && maxIntelFlops - maxCpuFlops < 0) {
                        bestPlatform = bestCpuPlatform;
                    }
                } else {
                    if (maxGpuFlops == 0) {
                        if (maxIntelFlops == 0) {
                            this.getLogger().info("No Intel graphics found, best platform is the best CPU platform we could find...");
                            bestPlatform = bestCpuPlatform;
                        } else {
                            this.getLogger().info("No dGPU found, best platform is the best Intel graphics we could find...");
                            bestPlatform = bestIntelPlatform;
                        }
                    }
                }

                if (bestPlatform == null) {
                    this.getLogger().info("Your system does not meet the OpenCL requirements for overworld generator. See if driver updates are available.");
                    this.getLogger().info("Required version: " + openClMajor + "." + openClMinor);
                    this.getLogger().info("Required extensions: [ cl_khr_fp64 ]");
                } else {
                    OpenCL.initContext(bestPlatform);
                    NormalGenerator.setUseGraphicsCompute(true);
                }
            }
        }

        node = "replace.nether";
        boolean nether = true;
        try {
            nether = config.getBoolean(node, nether);
        } catch (Exception e) {
            this.logConfigException(node, e);
        }

        if (nether) {
            Generator.addGenerator(NetherGenerator.class, "nether", VanillaGenerator.TYPE_NETHER);
        }
    }

    private void logConfigException(String node, Throwable t) {
        this.getLogger().alert("An error occurred while reading the configuration '" + node + "'. Use the default value.", t);
    }

    public static BetterGenerator getInstance() {
        return INSTANCE;
    }
}
