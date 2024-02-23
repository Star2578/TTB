package skills.knight;

import javafx.scene.image.ImageView;
import logic.GameManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.Attack;
import skills.BaseSkill;
import skills.SingleTargetSkill;
import utils.Config;

public class Slash<Type extends BasePiece> extends BaseSkill implements Attack, SingleTargetSkill<Type> {
    private Type target;
    private final int DAMAGE = 10;
    public Slash() {
        super("Slash", 3, 2, "A true knight slash doesn't need a sword");
        icon = new ImageView(Config.SlashPath);
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void setTarget(Type target) {
        this.target = target;
    }

    @Override
    public void perform() {
        attack();
    }

    @Override
    public boolean validRange(int col, int row) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }
}
