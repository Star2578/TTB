package logic.effect;

public class EffectConfig {
    double offsetX;
    double offsetY;
    double distanceFromOrigin;
    double scale;

    public EffectConfig(double offsetX, double offSetY, double distanceFromOrigin, double scale) {
        this.offsetX = offsetX;
        this.offsetY = offSetY;
        this.distanceFromOrigin = distanceFromOrigin;
        this.scale = scale;
    }
}
