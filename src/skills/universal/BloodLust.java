package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;
import pieces.Healable;

public class BloodLust extends BaseSkill implements Attackable, Healable {
    private BasePiece target;

    private final int DAMAGE = 9;
    private final int HEAL = 5;
    public BloodLust() {
        super("Blood Lust", Color.CRIMSON, 0, 0,
                "More damage if target not at full health, heal if killed", Config.Rarity.LEGENDARY, Config.sfx_attackSound);
//mana 5 ap 5
        icon = new ImageView(Config.BloodLustPath);
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

                if (monsterPiece.getCurrentHealth() != monsterPiece.getMaxHealth()) {
                    monsterPiece.takeDamage(DAMAGE + 5);
                } else {
                    monsterPiece.takeDamage(DAMAGE);
                }

                //=========<SKILL BLOOD EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.ON_TARGET ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(40) ,
                                new EffectConfig(-24 , -40 , 0 , 0.8) );
                //===========================================================================================

                if (!monsterPiece.isAlive()) {
                    heal();
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

    @Override
    public void heal() {
        if (target != null) {
            int currentHealth = GameManager.getInstance().player.getCurrentHealth();
            GameManager.getInstance().player.setCurrentHealth(currentHealth + HEAL);

            //=========<SKILL HEAL EFFECT>====================================================================
            EffectMaker.getInstance()
                    .renderEffect( EffectMaker.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                            EffectMaker.getInstance().createInPlaceEffects(41) ,
                            new EffectConfig(0 , -8 , 0 , 1.5) );
            //===========================================================================================
        }
    }

    @Override
    public int getHeal() {
        return HEAL;
    }
}
