package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class BloodPact extends BaseSkill implements Attack {
    private BasePiece target;

    private final int HEALTH_COST = 5;
    private final int DAMAGE = 20;
    public BloodPact() {
        super("Blood Pact", Color.CRIMSON, 2, 0,
                "Just 5 of your HP for immense power", Config.Rarity.EPIC, Config.sfx_darkMagicSound);

        icon = new ImageView(Config.BloodPactPath);
        range = 3;
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

        return Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
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
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.takeDamage(HEALTH_COST);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
//                EffectManager.getInstance()
//                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
//                                GameManager.getInstance().player ,
//                                target.getRow(), target.getCol(),
//                                EffectManager.getInstance().createInPlaceEffects(1) ,
//                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
