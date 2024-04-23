package items.potions;

import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import utils.Config;
import utils.RefillMana;

public class BluePotion extends BasePotion implements RefillMana {
    private final int MANA_REFILL = 4;
    private BasePiece target;

    public BluePotion() {
        super("Blue Potion", Color.AQUAMARINE, Config.BluePotionPath,
                "Help replenish mana", Config.Rarity.COMMON, "res/SFX/powerup/8bit-powerup2.wav");
    }

    @Override
    public void usePotion(BasePiece target) {
        this.target = target;
        refillMana();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public void refillMana() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMana = playerPiece.getCurrentMana();

                playerPiece.setCurrentMana(currentMana + MANA_REFILL);
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
    public int getRefill() {
        return MANA_REFILL;
    }
}
