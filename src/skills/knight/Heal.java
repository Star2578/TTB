package skills.knight;

import javafx.scene.image.ImageView;
import logic.GameManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import utils.Healing;
import utils.Config;

public class Heal extends BaseSkill implements Healing {
    private BasePiece target;
    private final int HEAL = 5;
    public Heal() {
        super("Heal", 1, 10, "Rest for one turn to heal.");
        icon = new ImageView(Config.HealPath);
        range = 0;
    }
    @Override
    public void perform(BasePiece target) {
        this.target = target;
        heal();
    }

    @Override
    public boolean validRange(int row, int col) {
        // Only valid at player's square
        BasePlayerPiece player = GameManager.getInstance().player;

        return player.getRow() == row && player.getCol() == col;
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
    public void heal() {
        if (target != null && target instanceof BasePlayerPiece player) {
            // Perform healing
            int currentHp = player.getCurrentHealth();
            player.setCurrentHealth(currentHp + HEAL);
            player.decreaseMana(manaCost);
            player.decreaseActionPoint(actionPointCost);
        }
    }
}
