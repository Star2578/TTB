package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
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
        super("Blood Lust", Color.CRIMSON, 5, 5,
                "More damage if target not at full health, heal if killed", Config.Rarity.LEGENDARY, Config.sfx_attackSound);

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

                if (!monsterPiece.isAlive()){
                    heal();
                }

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
        return DAMAGE;
    }

    @Override
    public void heal() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentHealth = playerPiece.getCurrentHealth();

                playerPiece.setCurrentHealth(currentHealth + HEAL);

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_TARGET ,
                                GameManager.getInstance().player ,
                                GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(5) ,
                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getHeal() {
        return HEAL;
    }
}
