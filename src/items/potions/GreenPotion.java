package items.potions;

import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import utils.BuffHealth;
import utils.Config;

public class GreenPotion extends BasePotion implements BuffHealth {
    private final int MAX_HEALTH = 5;
    private BasePiece target;

    public GreenPotion() {
        super("Green Potion", Color.OLIVE, Config.GreenPotionPath,
                "It slightly glowing in the darkness of the dungeon, you're not sure if you could drink this.", Config.Rarity.EPIC, "res/SFX/powerup/8bit-powerup2.wav", Color.DARKOLIVEGREEN);
    }

    @Override
    public void usePotion(BasePiece target) {
        this.target = target;
        buffHealth();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public void buffHealth() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMaxHealth = playerPiece.getMaxHealth();

                playerPiece.setMaxHealth(currentMaxHealth + MAX_HEALTH);
            }
        }
    }

    @Override
    public boolean castOnSelf() {
        return true;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public boolean validRange(int row, int col) {
        // Only valid at player's square
        BasePlayerPiece player = GameManager.getInstance().player;

        return player.getRow() == row && player.getCol() == col;
    }

    @Override
    public void useItem(BasePiece on) {
        usePotion(on);
    }

    @Override
    public int getBuffHealth() {
        return MAX_HEALTH;
    }
}
