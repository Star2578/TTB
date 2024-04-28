package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class TripleStrike extends BaseSkill implements Attack {
    private BasePiece target;

    public TripleStrike() {
        super("Triple Strike", Color.DEEPPINK, 7, 3,
                "Attack 3 times, scale by your normal attack", Config.Rarity.LEGENDARY, Config.sfx_attackSound);

        icon = new ImageView(Config.TripleStrikePath);
        range = 1;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        for (int i = 0; i < 3; i++) {
            attack();
            SoundManager.getInstance().playSoundEffect(sfxPath);
        }
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
                monsterPiece.takeDamage(GameManager.getInstance().player.getAttackDamage());
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(1) ,
                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getAttack() {
        return GameManager.getInstance().player.getAttackDamage();
    }
}
