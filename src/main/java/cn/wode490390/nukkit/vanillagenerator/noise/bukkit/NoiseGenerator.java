package cn.wode490390.nukkit.vanillagenerator.noise.bukkit;

public abstract class NoiseGenerator {

    protected final int[] perm = new int[512];
    protected double offsetX;
    protected double offsetY;
    protected double offsetZ;

    public static int floor(double x) {
        return x >= 0 ? (int) x : (int) x - 1;
    }

    protected static double fade(double x) {
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    protected static double lerp(double x, double y, double z) {
        return y + x * (z - y);
    }

    protected static double grad(int hash, double x, double y, double z) {
        hash &= 0xf;
        double u = hash < 8 ? x : y;
        double v = (hash == 12) || (hash == 14) ? x : hash < 4 ? y : z;
        return ((hash & 0x1) == 0 ? u : -u) + ((hash & 0x2) == 0 ? v : -v);
    }

    public double noise(double x) {
        return this.noise(x, 0, 0);
    }

    public double noise(double x, double y) {
        return this.noise(x, y, 0);
    }

    public abstract double noise(double p0, double p1, double p2);

    public double noise(double x, int octaves, double frequency, double amplitude) {
        return this.noise(x, 0, 0, octaves, frequency, amplitude);
    }

    public double noise(double x, int octaves, double frequency, double amplitude, boolean normalized) {
        return this.noise(x, 0, 0, octaves, frequency, amplitude, normalized);
    }

    public double noise(double x, double y, int octaves, double frequency, double amplitude) {
        return this.noise(x, y, 0, octaves, frequency, amplitude);
    }

    public double noise(double x, double y, int octaves, double frequency, double amplitude, boolean normalized) {
        return this.noise(x, y, 0, octaves, frequency, amplitude, normalized);
    }

    public double noise(double x, double y, double z, int octaves, double frequency, double amplitude) {
        return this.noise(x, y, z, octaves, frequency, amplitude, false);
    }

    public double noise(double x, double y, double z, int octaves, double frequency, double amplitude, boolean normalized) {
        double result = 0;
        double amp = 1;
        double freq = 1;
        double max = 0;
        for (int i = 0; i < octaves; i++) {
            result += this.noise(x * freq, y * freq, z * freq) * amp;
            max += amp;
            freq *= frequency;
            amp *= amplitude;
        }
        if (normalized) {
            result /= max;
        }
        return result;
    }
}
