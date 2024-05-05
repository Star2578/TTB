package items.potions;

import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import utils.Config;
import pieces.Healable;

public class RedPotion extends BasePotion implements Healable {
    private final int HEALTH_REGEN = 5;
    private BasePiece target;

    public RedPotion() {
        super("Red Potion", Color.CRIMSON, Config.RedPotionPath,
                "The classic red potion, it's very red.", Config.Rarity.UNCOMMON, "SFX/powerup/8bit-powerup2.wav", Color.DARKRED);
    }

    @Override
    public void usePotion(BasePiece target) {
        this.target = target;
        heal();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public void heal() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentHealth = playerPiece.getCurrentHealth();

                playerPiece.setCurrentHealth(currentHealth + HEALTH_REGEN);
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
    public int getHeal() {
        return HEALTH_REGEN;
    }
}
