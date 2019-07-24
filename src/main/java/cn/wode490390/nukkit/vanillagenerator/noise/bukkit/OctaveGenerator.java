package cn.wode490390.nukkit.vanillagenerator.noise.bukkit;

public abstract class OctaveGenerator {

    protected final NoiseGenerator[] octaves;
    protected double xScale = 1;
    protected double yScale = 1;
    protected double zScale = 1;

    protected OctaveGenerator(NoiseGenerator[] octaves) {
        this.octaves = octaves;
    }

    public void setScale(double scale) {
        this.setXScale(scale);
        this.setYScale(scale);
        this.setZScale(scale);
    }

    public double getXScale() {
        return this.xScale;
    }

    public void setXScale(double scale) {
        this.xScale = scale;
    }

    public double getYScale() {
        return this.yScale;
    }

    public void setYScale(double scale) {
        this.yScale = scale;
    }

    public double getZScale() {
        return this.zScale;
    }

    public void setZScale(double scale) {
        this.zScale = scale;
    }

    public NoiseGenerator[] getOctaves() {
        return this.octaves.clone();
    }

    public double noise(double x, double frequency, double amplitude) {
        return this.noise(x, 0, 0, frequency, amplitude);
    }

    public double noise(double x, double frequency, double amplitude, boolean normalized) {
        return this.noise(x, 0, 0, frequency, amplitude, normalized);
    }

    public double noise(double x, double y, double frequency, double amplitude) {
        return this.noise(x, y, 0, frequency, amplitude);
    }

    public double noise(double x, double y, double frequency, double amplitude, boolean normalized) {
        return this.noise(x, y, 0, frequency, amplitude, normalized);
    }

    public double noise(double x, double y, double z, double frequency, double amplitude) {
        return this.noise(x, y, z, frequency, amplitude, false);
    }

    public double noise(double x, double y, double z, double frequency, double amplitude, boolean normalized) {
        double result = 0;
        double amp = 1;
        double freq = 1;
        double max = 0;

        x *= this.xScale;
        y *= this.yScale;
        z *= this.zScale;
        for (NoiseGenerator octave : this.octaves) {
            result += octave.noise(x * freq, y * freq, z * freq) * amp;
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
