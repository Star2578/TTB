package skills.archer;

import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;

public class Targetlock extends BaseSkill implements Attackable {
    private BasePiece target;
    private final int DAMAGE = 7;
    public Targetlock() {
        super("TargetLock", Color.DARKRED,
                5, 2,
                "Concentrate... Steady aim... SHOOT!",
                Config.Rarity.COMMON, "res/SFX/skills/slash/PP_01.wav"
        );
        icon = new ImageView(Config.TargetLockPath);
        range = 7;
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {

                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<ATTACK EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.BULLET_TO_TARGET ,
                                GameManager.getInstance().player ,
                                monsterPiece.getRow(), monsterPiece.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(13) ,
                                new EffectConfig(-6 , 0 , -10 , 1.5) );
                //===========================================================================================

                // Create a PauseTransition with a duration of 0.45 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.45));

                // Set the action to perform after the pause
                pause.setOnFinished(event -> {
                    //=========<SKILL EFFECT>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    monsterPiece.getRow(), monsterPiece.getCol(),
                                    EffectManager.getInstance().createInPlaceEffects(19) ,
                                    new EffectConfig(1 , 0 , 0 , 1.5) );
                    //===========================================================================================

                    monsterPiece.takeDamage(DAMAGE);
                });
                // Start the pause
                pause.play();

            }
        }
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
        SoundManager.getInstance().playSoundEffect(sfxPath);
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
    public int getAttack() {
        return DAMAGE;
    }



}
