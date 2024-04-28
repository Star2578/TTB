package skills.wizard;

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

public class Fireball extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 12;
    public Fireball() {
        super("Fireball", Color.RED,
                5, 2,
                "Shoot a fireball to the enemies",
                Config.Rarity.UNCOMMON, "res/SFX/skills/slash/PP_01.wav"
        );

        //TODO======================
        icon = new ImageView(Config.FireballPath);
        range = 5;
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
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(24) ,
                                new EffectConfig(0 , -3 , 34 , 1.2) );
                //===========================================================================================
                //=========<SKILL EFFECT TAKE DAMAGE>====================================================================
                EffectManager.getInstance()
                        .renderEffect(EffectManager.TYPE.ON_SELF,
                                GameManager.getInstance().player,
                                monsterPiece.getRow(), monsterPiece.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(23),
                                new EffectConfig(-16, -19, 0, 1));
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
