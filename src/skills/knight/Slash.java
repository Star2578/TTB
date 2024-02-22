package skills.knight;

import logic.GameManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.Attack;
import skills.BaseSkill;
import skills.SingleTargetSkill;

public class Slash<Type extends BasePiece> extends BaseSkill implements Attack, SingleTargetSkill<Type> {
    private Type target;
    private final int DAMAGE = 10;
    public Slash() {
        super("Slash", 3, 2, "A true knight slash doesn't need a sword");
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
}
