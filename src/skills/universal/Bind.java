package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
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

public class Bind extends BaseSkill implements Attack {
    private BasePiece target;

    private final int DAMAGE = 1;
    public Bind() {
        super("Bind", Color.PALEGOLDENROD, 6, 1,
                "crouch and tied target legs up, stun it for 3 turns", Config.Rarity.EPIC, Config.sfx_darkMagicSound);

        icon = new ImageView(Config.BindPath);
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
                monsterPiece.takeDamage(DAMAGE);
                // Stun monster 3 turn
                monsterPiece.addBuff(3,"Stun");
                //=========<STUN EFFECT>====================================================================
                Effect Stun = EffectManager.getInstance().createInPlaceEffects(8);
                Stun.setOwner(target);
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                Stun ,
                                new EffectConfig(12 , -6 , 0 , 1.6) );
                Stun.setTurnRemain(4);
                //===========================================================================================
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_TARGET ,
                                GameManager.getInstance().player,
                                target.getRow(), target.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(0) ,
                                new EffectConfig(0 , 8 , 0 , 1.25) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
