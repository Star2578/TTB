package pieces.player;

import javafx.scene.image.ImageView;
import pieces.BasePiece;
import utils.BaseStatus;
import utils.Config;

public abstract class BasePlayerPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;
    private int currentActionPoint;
    private int maxActionPoint;
    private boolean canAct;

    public BasePlayerPiece(int row, int col) {
        super("Player", new ImageView(Config.PlaceholderPath), row, col);
        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;
        canAct = false;
    }

    @Override
    public int getCurrentHealth() {
        return currentHp;
    }

    @Override
    public void setCurrentHealth(int health) {
        this.currentHp = Math.max(health, 0);
    }

    @Override
    public int getMaxHealth() {
        return maxHp;
    }

    public void setCurrentActionPoint(int currentActionPoint) {
        this.currentActionPoint = Math.max(currentActionPoint, 0);
    }

    public int getCurrentActionPoint() {
        return currentActionPoint;
    }

    public void setMaxActionPoint(int maxActionPoint) {
        this.maxActionPoint = Math.max(maxActionPoint, 1);
    }

    public int getMaxActionPoint() {
        return maxActionPoint;
    }

    public void setCanAct(boolean canAct) {
        this.canAct = canAct;
    }

    public boolean canAct() {
        return canAct;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        int maxHpBuffer = maxHp;
        this.maxHp = Math.max(maxHealth, 1);

        if (maxHp == maxHpBuffer) currentHp = maxHp;
        if (maxHp < currentHp) currentHp = maxHp;
    }

    @Override
    public boolean isAlive() {
        return currentHp > 0;
    }

    @Override
    public void onDeath() {
        // TODO: Call Game Over
    }

    public abstract void startTurn();

    public abstract void endTurn();

    public abstract boolean validMove(int row, int col); // To set valid move for each classes
}
