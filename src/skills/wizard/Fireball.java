package skills.wizard;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;

public class Fireball extends BaseSkill implements Attackable {
    private BasePiece target;
    private final int DAMAGE = 8;
    public Fireball() {
        super("Fireball", Color.RED,
                6, 2,
                "Shoot a fireball to the enemies",
                Config.Rarity.UNCOMMON, "SFX/skills/slash/PP_01.wav"
        );

        //TODO======================
        icon = new ImageView(Config.FireballPath);
        range = 3;
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {

                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.BULLET_TO_TARGET ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(24) ,
                                new EffectConfig(0 , -3 , 0 , 1.2) );
                //===========================================================================================

                //=========<SKILL EFFECT TAKE DAMAGE>====================================================================
                new Thread(()->{ //delay for a while
                    try { Thread.sleep(400); }
                    catch (InterruptedException e) { throw new RuntimeException(e); }
                    Platform.runLater(()->{
                        EffectMaker.getInstance()
                                .renderEffect(EffectMaker.TYPE.ON_SELF,
                                        GameManager.getInstance().player,
                                        monsterPiece.getRow(), monsterPiece.getCol(),
                                        EffectMaker.getInstance().createInPlaceEffects(23),
                                        new EffectConfig(-16, -19, 0, 1));

                        monsterPiece.takeDamage(DAMAGE); //call it here, so that dead effect will be delayed

                    });

                }).start();

                //===========================================================================================
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
