# Better Vanilla Generator
[![Build](https://img.shields.io/circleci/build/github/wode490390/BetterVanillaGenerator/master)](https://circleci.com/gh/wode490390/BetterVanillaGenerator/tree/master)
[![Release](https://img.shields.io/github/v/release/wode490390/BetterVanillaGenerator)](https://github.com/wode490390/BetterVanillaGenerator/releases)
[![Release date](https://img.shields.io/github/release-date/wode490390/BetterVanillaGenerator)](https://github.com/wode490390/BetterVanillaGenerator/releases)
<!--[![Servers](https://img.shields.io/bstats/servers/5198)](https://bstats.org/plugin/bukkit/BetterVanillaGenerator/5198)
[![Players](https://img.shields.io/bstats/players/5198)](https://bstats.org/plugin/bukkit/BetterVanillaGenerator/5198)-->

This plugin not only provides better terrain generators for [Nukkit](https://github.com/NukkitX/Nukkit), you can also customize your world by modifying the configuration.

In addition, this plugin can offload overworld surface noise operations onto the GPU. (Experimental)

[![](https://i.loli.net/2019/06/12/5d00613070e3947388.png)](https://www.mcbbs.net/thread-872584-1-1.html "可自定义的更好的原生地形生成器")

If you found any bugs or have any suggestions, please open an issue on [GitHub Issues](https://github.com/wode490390/BetterVanillaGenerator/issues).

If you love this plugin, please star it on [GitHub](https://github.com/wode490390/BetterVanillaGenerator).

*Note: Please back up old worlds before using this plugin.*

## Download
- [Releases](https://github.com/wode490390/BetterVanillaGenerator/releases)
- [Snapshots](https://circleci.com/gh/wode490390/BetterVanillaGenerator)

## Configurations

### config.yml
```yaml
replace:
  # Whether to replace the overworld generator
  overworld: true
  # Whether to replace the nether generator
  nether: true

# Advanced configuration for generator
generator:
  general:
    sea_level: 64
  overworld:
    coordinate-scale: 684.412
    height:
      scale: 684.412
      noise-scale:
        x: 200.0
        z: 200.0
    detail:
      noise-scale:
        x: 80.0
        y: 160.0
        z: 80.0
    surface-scale: 0.0625
    base-size: 8.5
    stretch-y: 12.0
    biome:
      height-offset: 0.0
      height-weight: 1.0
      scale-offset: 0.0
      scale-weight: 1.0
      height:
        default: 0.1
        flat-shore: 0.0
        high-plateau: 1.5
        flatlands: 0.125
        swampland: -0.2
        mid-plains: 0.2
        flatlands-hills: 0.275
        swampland-hills: -0.1
        low-hills: 0.2
        hills: 0.45
        mid-hills2: 0.1
        default-hills: 0.2
        mid-hills: 0.3
        big-hills: 0.525
        big-hills2: 0.55
        extreme-hills: 1.0
        rocky-shore: 0.1
        low-spikes: 0.4125
        high-spikes: 1.1
        river: -0.5
        ocean: -1.0
        deep-ocean: -1.8
      scale:
        default: 0.2
        flat-shore: 0.025
        high-plateau: 0.025
        flatlands: 0.05
        swampland: 0.1
        mid-plains: 0.2
        flatlands-hills: 0.25
        swampland-hills: 0.3
        low-hills: 0.3
        hills: 0.3
        mid-hills2: 0.4
        default-hills: 0.4
        mid-hills: 0.4
        big-hills: 0.55
        big-hills2: 0.5
        extreme-hills: 0.5
        rocky-shore: 0.8
        low-spikes: 1.325
        high-spikes: 1.3125
        river: 0.0
        ocean: 0.1
        deep-ocean: 0.1
    density:
      fill:
        mode: 0
        sea-mode: 0
        offset: 0.0
  nether:
    coordinate-scale: 684.412
    height:
      scale: 2053.236
      noise-scale:
        x: 100.0
        z: 100.0
    detail:
      noise-scale:
        x: 80.0
        y: 60.0
        z: 80.0
    surface-scale: 0.0625

# Whether to use graphics compute functionality (Experimental)
gpu:
  enable: false
  use-any-device: false
```

## Compiling
1. Install [Maven](https://maven.apache.org/).
2. Run `mvn clean package`. The compiled JAR can be found in the `target/` directory.

## Metrics Collection

This plugin uses [bStats](https://github.com/wode490390/bStats-Nukkit) - you can opt out using the global bStats config, see the [official website](https://bstats.org/getting-started) for more details.

[![Metrics](https://bstats.org/signatures/bukkit/BetterVanillaGenerator.svg)](https://bstats.org/plugin/bukkit/BetterVanillaGenerator/5198)

###### If I have any grammar and terms error, please correct my wrong :)
