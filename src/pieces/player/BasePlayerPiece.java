package pieces.player;

import javafx.scene.image.ImageView;
import pieces.BasePiece;
import utils.BaseStatus;

public class BasePlayerPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;

    public BasePlayerPiece(int row, int col) {
        super("Player", new ImageView("sprites/player/Knight.png"), row, col);
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
}
