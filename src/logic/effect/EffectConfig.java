package logic.effect;

public class EffectConfig {
    public double offsetX;
    public double offsetY;
    public double distanceFromOrigin;
    public double scale;

    public EffectConfig(double offsetX, double offSetY, double distanceFromOrigin, double scale) {
        this.offsetX = offsetX;
        this.offsetY = offSetY;
        this.distanceFromOrigin = distanceFromOrigin;
        this.scale = scale;
    }
}
