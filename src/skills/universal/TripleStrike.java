package skills.universal;

import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;

public class TripleStrike extends BaseSkill implements Attackable {
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
            PauseTransition pause = new PauseTransition(Duration.seconds(i + 0.4));

            // Set the action to perform after the pause
            pause.setOnFinished(event -> {
                attack();
                SoundManager.getInstance().playSoundEffect(sfxPath);
            });

            // Start the pause
            pause.play();
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
                //attack effect
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.ON_TARGET ,
                                GameManager.getInstance().player,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(0) ,
                                new EffectConfig(0 , 8 , 0 , 1.25) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getAttack() {
        return GameManager.getInstance().player.getAttackDamage();
    }
}
