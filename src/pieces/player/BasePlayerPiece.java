package pieces.player;

import javafx.scene.image.ImageView;
import pieces.BasePiece;
import pieces.BaseStatus;
import utils.Config;

public abstract class BasePlayerPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;
    private int currentActionPoint;
    private int maxActionPoint;
    private int currentMana;
    private int maxMana;
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

    public void decreaseActionPoint(int decrease) {
        this.currentActionPoint = Math.max(0, this.currentActionPoint - decrease);

        if (currentActionPoint == 0) setCanAct(false);
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

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(currentMana, 0);
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(1, maxMana);
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
