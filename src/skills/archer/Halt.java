package skills.archer;

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

public class Halt extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 6;
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
                monsterPiece.takeDamage(DAMAGE);
                // Stun monster 1 turn
                monsterPiece.setStun(monsterPiece.getStun() + 1);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<ATTACK EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                monsterPiece.getRow(), monsterPiece.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(13) ,
                                new EffectConfig(-6 , 0 , 18 , 1.5) );
                //===========================================================================================
                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF ,
                                GameManager.getInstance().player ,
                                monsterPiece.getRow(), monsterPiece.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(20) ,
                                new EffectConfig(1 , 0 , 0 , 1.2) );
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
