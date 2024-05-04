package skills.universal;

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

public class Ambush extends BaseSkill implements Attackable {
    private BasePiece target;

    private final int DAMAGE = 8;
    public Ambush() {
        super("Ambush", Color.DARKGRAY, 4, 2,
                "Increase damage when use from behind the target", Config.Rarity.EPIC, Config.sfx_attackSound);

        icon = new ImageView(Config.AmbushPath);
        range = 1;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public boolean validRange(int row, int col) {
        // Get the current row and column
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();


        // Check if the row or column is the same as the current position
        return (row == currentRow) && Math.abs(col - currentCol) <= range;
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

                // if use from behind, dmg x 3
                if (monsterPiece.getCurrentDirection() == GameManager.getInstance().player.getCurrentDirection()) {
                    //=========<SKILL CRIT EFFECT>====================================================================
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.ON_TARGET ,
                                    GameManager.getInstance().player ,
                                    target.getRow(), target.getCol(),
                                    EffectMaker.getInstance().createInPlaceEffects(39) ,
                                    new EffectConfig(-12 , -52 , 0 , 0.8) );
                    //===========================================================================================
                    new Thread(()->{
                        for(int i = 0 ; i < 3 ; i++){
                            try {
                                Platform.runLater(()->monsterPiece.takeDamage(DAMAGE));
                                Thread.sleep(150);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                } else {
                    //=========<SKILL EFFECT>====================================================================
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.ON_TARGET ,
                                    GameManager.getInstance().player ,
                                    target.getRow(), target.getCol(),
                                    EffectMaker.getInstance().createInPlaceEffects(38) ,
                                    new EffectConfig(-12 , -56 , 0 , 1) );
                    //===========================================================================================
                    monsterPiece.takeDamage(DAMAGE);
                }

                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());


            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
