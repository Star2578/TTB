package skills.knight;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import utils.Attack;
import skills.BaseSkill;
import utils.Config;

public class Slash extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 10;
    public Slash() {
        super("Slash", Color.DARKRED, 2, 2, "A true knight slash doesn't need a sword", Config.Rarity.COMMON);
        icon = new ImageView(Config.SlashPath);
        range = 1;
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target ,
                                EffectManager.getInstance().createInPlaceEffects(1) ,
                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================
            }
        }
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return true;
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
