package skills.archer;

import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.Effect;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class Halt extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 6;
    private final int STUN_DURATION = 1;
    public Halt() {
        super("Halt", Color.DARKRED,
                5, 2,
                "Get STUN on!! \n\n  - Archer probably"
                , Config.Rarity.RARE, "res/SFX/skills/slash/PP_01.wav");
        icon = new ImageView(Config.HaltPath);
        range = 5;
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
                                    EffectManager.getInstance().createInPlaceEffects(20) ,
                                    new EffectConfig(1 , 0 , 0 , 1.2) );
                    //===========================================================================================

                    monsterPiece.takeDamage(DAMAGE);

                    // Stun monster 1 turn
                    if (monsterPiece.isAlive()) {
                        monsterPiece.addBuff(STUN_DURATION, "Stun");
                        //=========<STUN EFFECT>====================================================================
                        Effect Stun = EffectManager.getInstance().createInPlaceEffects(8);
                        EffectManager.getInstance()
                                .renderEffect(EffectManager.TYPE.ON_SELF,
                                        GameManager.getInstance().player,
                                        target.getRow(), target.getCol(),
                                        Stun,
                                        new EffectConfig(12, -6, 0, 1.6));
                        Stun.bindToOwnerMovement(target);
                        Stun.setTurnRemain(STUN_DURATION + 1);
                        //===========================================================================================
                    }
                });

                // Start the pause
                pause.play();

                if (!monsterPiece.isAlive()) {
                    GameManager.getInstance().gameScene.removePiece(monsterPiece);
                    EffectManager.getInstance().clearDeadEffect();
                }

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
