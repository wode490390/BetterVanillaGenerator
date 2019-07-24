package cn.wode490390.nukkit.vanillagenerator.noise.bukkit;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;

public class SimplexNoiseGenerator extends PerlinNoiseGenerator {

    protected static final double SQRT_3 = Math.sqrt(3);
    protected static final double SQRT_5 = Math.sqrt(5);
    protected static final double F2 = 0.5 * (SQRT_3 - 1);
    protected static final double G2 = (3 - SQRT_3) / 6;
    protected static final double G22 = G2 * 2 - 1;
    protected static final double F3 = 0.3333333333333333d;
    protected static final double G3 = 0.16666666666666666d;
    protected static final double F4 = (SQRT_5 - 1) / 4;
    protected static final double G4 = (5 - SQRT_5) / 20;
    protected static final double G42 = G4 * 2;
    protected static final double G43 = G4 * 3;
    protected static final double G44 = G4 * 4 - 1;
    protected static final int[][] grad4 = new int[][]{{0, 1, 1, 1}, {0, 1, 1, -1}, {0, 1, -1, 1}, {0, 1, -1, -1}, {0, -1, 1, 1}, {0, -1, 1, -1}, {0, -1, -1, 1}, {0, -1, -1, -1}, {1, 0, 1, 1}, {1, 0, 1, -1}, {1, 0, -1, 1}, {1, 0, -1, -1}, {-1, 0, 1, 1}, {-1, 0, 1, -1}, {-1, 0, -1, 1}, {-1, 0, -1, -1}, {1, 1, 0, 1}, {1, 1, 0, -1}, {1, -1, 0, 1}, {1, -1, 0, -1}, {-1, 1, 0, 1}, {-1, 1, 0, -1}, {-1, -1, 0, 1}, {-1, -1, 0, -1}, {1, 1, 1, 0}, {1, 1, -1, 0}, {1, -1, 1, 0}, {1, -1, -1, 0}, {-1, 1, 1, 0}, {-1, 1, -1, 0}, {-1, -1, 1, 0}, {-1, -1, -1, 0}};
    protected static final int[][] simplex = new int[][]{{0, 1, 2, 3}, {0, 1, 3, 2}, {0, 0, 0, 0}, {0, 2, 3, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 2, 3, 0}, {0, 2, 1, 3}, {0, 0, 0, 0}, {0, 3, 1, 2}, {0, 3, 2, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 3, 2, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {1, 2, 0, 3}, {0, 0, 0, 0}, {1, 3, 0, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 3, 0, 1}, {2, 3, 1, 0}, {1, 0, 2, 3}, {1, 0, 3, 2}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 0, 3, 1}, {0, 0, 0, 0}, {2, 1, 3, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {2, 0, 1, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0, 1, 2}, {3, 0, 2, 1}, {0, 0, 0, 0}, {3, 1, 2, 0}, {2, 1, 0, 3}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 1, 0, 2}, {0, 0, 0, 0}, {3, 2, 0, 1}, {3, 2, 1, 0}};
    protected double offsetW;
    private static final SimplexNoiseGenerator instance = new SimplexNoiseGenerator();

    protected SimplexNoiseGenerator() {

    }

    public SimplexNoiseGenerator(Level world) {
        this(new NukkitRandom(world.getSeed()));
    }

    public SimplexNoiseGenerator(ChunkManager level) {
        this(new NukkitRandom(level.getSeed()));
    }

    public SimplexNoiseGenerator(long seed) {
        this(new NukkitRandom(seed));
    }

    public SimplexNoiseGenerator(NukkitRandom rand) {
        super(rand);
        this.offsetW = rand.nextDouble() * 256;
    }

    protected static double dot(int[] g, double x, double y) {
        return g[0] * x + g[1] * y;
    }

    protected static double dot(int[] g, double x, double y, double z) {
        return g[0] * x + g[1] * y + g[2] * z;
    }

    protected static double dot(int[] g, double x, double y, double z, double w) {
        return g[0] * x + g[1] * y + g[2] * z + g[3] * w;
    }

    public static double getNoise(double xin) {
        return instance.noise(xin);
    }

    public static double getNoise(double xin, double yin) {
        return instance.noise(xin, yin);
    }

    public static double getNoise(double xin, double yin, double zin) {
        return instance.noise(xin, yin, zin);
    }

    public static double getNoise(double x, double y, double z, double w) {
        return instance.noise(x, y, z, w);
    }

    @Override
    public double noise(double xin, double yin, double zin) {
        xin += this.offsetX;
        yin += this.offsetY;
        zin += this.offsetZ;

        double s = (xin + yin + zin) * 0.3333333333333333d;
        int i = NoiseGenerator.floor(xin + s);
        int j = NoiseGenerator.floor(yin + s);
        int k = NoiseGenerator.floor(zin + s);
        double t = (i + j + k) * 0.16666666666666666d;
        double X0 = i - t;
        double Y0 = j - t;
        double Z0 = k - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        double z0 = zin - Z0;
        int i2;
        int j2;
        int k2;
        int i3;
        int j3;
        int k3;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i2 = 1;
                j2 = 0;
                k2 = 0;
                i3 = 1;
                j3 = 1;
                k3 = 0;
            } else if (x0 >= z0) {
                i2 = 1;
                j2 = 0;
                k2 = 0;
                i3 = 1;
                j3 = 0;
                k3 = 1;
            } else {
                i2 = 0;
                j2 = 0;
                k2 = 1;
                i3 = 1;
                j3 = 0;
                k3 = 1;
            }
        } else if (y0 < z0) {
            i2 = 0;
            j2 = 0;
            k2 = 1;
            i3 = 0;
            j3 = 1;
            k3 = 1;
        } else if (x0 < z0) {
            i2 = 0;
            j2 = 1;
            k2 = 0;
            i3 = 0;
            j3 = 1;
            k3 = 1;
        } else {
            i2 = 0;
            j2 = 1;
            k2 = 0;
            i3 = 1;
            j3 = 1;
            k3 = 0;
        }
        double x2 = x0 - i2 + 0.16666666666666666d;
        double y2 = y0 - j2 + 0.16666666666666666d;
        double z2 = z0 - k2 + 0.16666666666666666d;
        double x3 = x0 - i3 + 0.3333333333333333d;
        double y3 = y0 - j3 + 0.3333333333333333d;
        double z3 = z0 - k3 + 0.3333333333333333d;
        double x4 = x0 - 1 + 0.5d;
        double y4 = y0 - 1 + 0.5d;
        double z4 = z0 - 1 + 0.5d;

        int ii = i & 0xff;
        int jj = j & 0xff;
        int kk = k & 0xff;
        int gi0 = this.perm[ii + this.perm[jj + this.perm[kk]]] % 12;
        int gi2 = this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2]]] % 12;
        int gi3 = this.perm[ii + i3 + this.perm[jj + j3 + this.perm[kk + k3]]] % 12;
        int gi4 = this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1]]] % 12;

        double t2 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        double n0;
        if (t2 < 0) {
            n0 = 0;
        } else {
            t2 *= t2;
            n0 = t2 * t2 * dot(grad3[gi0], x0, y0, z0);
        }
        double t3 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        double n2;
        if (t3 < 0) {
            n2 = 0;
        } else {
            t3 *= t3;
            n2 = t3 * t3 * dot(grad3[gi2], x2, y2, z2);
        }
        double t4 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        double n3;
        if (t4 < 0) {
            n3 = 0;
        } else {
            t4 *= t4;
            n3 = t4 * t4 * dot(grad3[gi3], x3, y3, z3);
        }
        double t5 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4;
        double n4;
        if (t5 < 0) {
            n4 = 0;
        } else {
            t5 *= t5;
            n4 = t5 * t5 * dot(grad3[gi4], x4, y4, z4);
        }
        return 32 * (n0 + n2 + n3 + n4);
    }

    @Override
    public double noise(double xin, double yin) {
        xin += this.offsetX;
        yin += this.offsetY;

        double s = (xin + yin) * F2;
        int i = NoiseGenerator.floor(xin + s);
        int j = NoiseGenerator.floor(yin + s);
        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        int i2;
        int j2;
        if (x0 > y0) {
            i2 = 1;
            j2 = 0;
        } else {
            i2 = 0;
            j2 = 1;
        }
        double x2 = x0 - i2 + G2;
        double y2 = y0 - j2 + G2;
        double x3 = x0 + G22;
        double y3 = y0 + G22;

        int ii = i & 0xff;
        int jj = j & 0xff;
        int gi0 = this.perm[ii + this.perm[jj]] % 12;
        int gi2 = this.perm[ii + i2 + this.perm[jj + j2]] % 12;
        int gi3 = this.perm[ii + 1 + this.perm[jj + 1]] % 12;

        double t2 = 0.5d - x0 * x0 - y0 * y0;
        double n0;
        if (t2 < 0) {
            n0 = 0;
        } else {
            t2 *= t2;
            n0 = t2 * t2 * dot(grad3[gi0], x0, y0);
        }
        double t3 = 0.5d - x2 * x2 - y2 * y2;
        double n2;
        if (t3 < 0) {
            n2 = 0;
        } else {
            t3 *= t3;
            n2 = t3 * t3 * dot(grad3[gi2], x2, y2);
        }
        double t4 = 0.5d - x3 * x3 - y3 * y3;
        double n3;
        if (t4 < 0) {
            n3 = 0;
        } else {
            t4 *= t4;
            n3 = t4 * t4 * dot(grad3[gi3], x3, y3);
        }
        return 70 * (n0 + n2 + n3);
    }

    public double noise(double x, double y, double z, double w) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;
        w += this.offsetW;

        double s = (x + y + z + w) * F4;
        int i = NoiseGenerator.floor(x + s);
        int j = NoiseGenerator.floor(y + s);
        int k = NoiseGenerator.floor(z + s);
        int l = NoiseGenerator.floor(w + s);

        double t = (i + j + k + l) * G4;
        double X0 = i - t;
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;
        double x2 = x - X0;
        double y2 = y - Y0;
        double z2 = z - Z0;
        double w2 = w - W0;

        int c1 = (x2 > y2) ? 32 : 0;
        int c2 = (x2 > z2) ? 16 : 0;
        int c3 = (y2 > z2) ? 8 : 0;
        int c4 = (x2 > w2) ? 4 : 0;
        int c5 = (y2 > w2) ? 2 : 0;
        int c6 = (z2 > w2) ? 1 : 0;
        int c7 = c1 + c2 + c3 + c4 + c5 + c6;

        int i2 = (simplex[c7][0] >= 3) ? 1 : 0;
        int j2 = (simplex[c7][1] >= 3) ? 1 : 0;
        int k2 = (simplex[c7][2] >= 3) ? 1 : 0;
        int l2 = (simplex[c7][3] >= 3) ? 1 : 0;

        int i3 = (simplex[c7][0] >= 2) ? 1 : 0;
        int j3 = (simplex[c7][1] >= 2) ? 1 : 0;
        int k3 = (simplex[c7][2] >= 2) ? 1 : 0;
        int l3 = (simplex[c7][3] >= 2) ? 1 : 0;

        int i4 = (simplex[c7][0] >= 1) ? 1 : 0;
        int j4 = (simplex[c7][1] >= 1) ? 1 : 0;
        int k4 = (simplex[c7][2] >= 1) ? 1 : 0;
        int l4 = (simplex[c7][3] >= 1) ? 1 : 0;

        double x3 = x2 - i2 + G4;
        double y3 = y2 - j2 + G4;
        double z3 = z2 - k2 + G4;
        double w3 = w2 - l2 + G4;

        double x4 = x2 - i3 + G42;
        double y4 = y2 - j3 + G42;
        double z4 = z2 - k3 + G42;
        double w4 = w2 - l3 + G42;

        double x5 = x2 - i4 + G43;
        double y5 = y2 - j4 + G43;
        double z5 = z2 - k4 + G43;
        double w5 = w2 - l4 + G43;

        double x6 = x2 + G44;
        double y6 = y2 + G44;
        double z6 = z2 + G44;
        double w6 = w2 + G44;

        int ii = i & 0xff;
        int jj = j & 0xff;
        int kk = k & 0xff;
        int ll = l & 0xff;

        int gi0 = this.perm[ii + this.perm[jj + this.perm[kk + this.perm[ll]]]] % 32;
        int gi2 = this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2 + this.perm[ll + l2]]]] % 32;
        int gi3 = this.perm[ii + i3 + this.perm[jj + j3 + this.perm[kk + k3 + this.perm[ll + l3]]]] % 32;
        int gi4 = this.perm[ii + i4 + this.perm[jj + j4 + this.perm[kk + k4 + this.perm[ll + l4]]]] % 32;
        int gi5 = this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1 + this.perm[ll + 1]]]] % 32;

        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        double n0;
        if (t2 < 0) {
            n0 = 0;
        } else {
            t2 *= t2;
            n0 = t2 * t2 * dot(grad4[gi0], x2, y2, z2, w2);
        }
        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        double n2;
        if (t3 < 0) {
            n2 = 0;
        } else {
            t3 *= t3;
            n2 = t3 * t3 * dot(grad4[gi2], x3, y3, z3, w3);
        }
        double t4 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        double n3;
        if (t4 < 0) {
            n3 = 0;
        } else {
            t4 *= t4;
            n3 = t4 * t4 * dot(grad4[gi3], x4, y4, z4, w4);
        }
        double t5 = 0.6 - x5 * x5 - y5 * y5 - z5 * z5 - w5 * w5;
        double n4;
        if (t5 < 0) {
            n4 = 0;
        } else {
            t5 *= t5;
            n4 = t5 * t5 * dot(grad4[gi4], x5, y5, z5, w5);
        }
        double t6 = 0.6 - x6 * x6 - y6 * y6 - z6 * z6 - w6 * w6;
        double n5;
        if (t6 < 0) {
            n5 = 0;
        } else {
            t6 *= t6;
            n5 = t6 * t6 * dot(grad4[gi5], x6, y6, z6, w6);
        }
        return 27 * (n0 + n2 + n3 + n4 + n5);
    }

    public static SimplexNoiseGenerator getInstance() {
        return instance;
    }
}
