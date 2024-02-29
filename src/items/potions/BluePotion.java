package items.potions;

import logic.GameManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import utils.Config;
import utils.RefillMana;

public class BluePotion extends BasePotion implements RefillMana {
    private final int MANA_REFILL = 4;
    private BasePiece target;

    public BluePotion() {
        super("Blue Potion", Config.BluePotionPath, Config.ITEM_TYPE.USABLE, "Help replenish mana");
    }

    @Override
    public void usePotion(BasePiece target) {
        this.target = target;
        refillMana();
    }

    @Override
    public void refillMana() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMana = playerPiece.getCurrentMana();

                playerPiece.setCurrentMana(currentMana + MANA_REFILL);

                // remove this item out of player's inventory
                GameManager.getInstance().inventory.remove(this);
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
}
