package logic;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
    private final long TARGET_FRAME_TIME = 1_000_000_000 / 60; // 60 FPS
    private final Runnable updateLogic;
    private final Runnable renderLogic;
    private long lastUpdateTime = 0;

    public GameLoop(Runnable updateLogic, Runnable renderLogic) {
        this.updateLogic = updateLogic;
        this.renderLogic = renderLogic;
    }

    @Override
    public void handle(long now) {
        if (now - lastUpdateTime >= TARGET_FRAME_TIME) {
            updateLogic.run();
            renderLogic.run();
            lastUpdateTime = now;
        }
    }
}
