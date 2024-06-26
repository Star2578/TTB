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

public class HolyLight extends BaseSkill implements Healable, Attackable {
    private BasePiece target;

    private final int DAMAGE = 10;
    private final int HEAL = 4;

    public HolyLight() {
        super("Holy Light", Color.GOLD, 4, 4,
                "The light will burn the enemy and heal thou wounds", Config.Rarity.RARE, Config.sfx_holyMagicSound);

        icon = new ImageView(Config.HolyLightPath);
        range = 2;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
        heal();
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
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(30) ,
                                new EffectConfig(-6 , -18 , 0 , 1.1) );
                //===========================================================================================
                //=========<HEAL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.ON_SELF,
                                GameManager.getInstance().player ,
                                GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(5) ,
                                new EffectConfig(-48 , -52 , 0 , 1.2) );
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
        int currentHealth = GameManager.getInstance().player.getCurrentHealth();
        GameManager.getInstance().player.setCurrentHealth(currentHealth + HEAL);

        //=========<SKILL EFFECT>====================================================================
        EffectMaker.getInstance()
                .renderEffect( EffectMaker.TYPE.ON_TARGET ,
                        GameManager.getInstance().player ,
                        GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                        EffectMaker.getInstance().createInPlaceEffects(5) ,
                        new EffectConfig(0 , -16 , 24 , 1.1) );
        //===========================================================================================
    }

    @Override
    public int getHeal() {
        return HEAL;
    }
}
