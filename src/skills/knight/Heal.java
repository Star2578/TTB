package skills.knight;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import utils.Healing;
import utils.Config;

public class Heal extends BaseSkill implements Healing {
    private BasePiece target;
    private final int HEAL = 5;
    public Heal() {
        super("Heal", Color.DARKGREEN, 1, 1, "Rest for one turn to heal", Config.Rarity.COMMON, "res/SFX/skills/heal/8bit-powerup1.wav");
        icon = new ImageView(Config.HealPath);
        range = 0;
    }
    @Override
    public void perform(BasePiece target) {
        this.target = target;
        heal();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public boolean validRange(int row, int col) {
        // Only valid at player's square
        BasePlayerPiece player = GameManager.getInstance().player;

        return player.getRow() == row && player.getCol() == col;
    }

    @Override
    public boolean castOnSelf() {
        return true;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }

    @Override
    public void heal() {
        if (target != null && target instanceof BasePlayerPiece player) {
            // Perform healing
            int currentHp = player.getCurrentHealth();
            player.setCurrentHealth(currentHp + HEAL);
            player.decreaseMana(manaCost);
            player.decreaseActionPoint(actionPointCost);
            //=========<SKILL EFFECT>====================================================================
            EffectManager.getInstance()
                    .renderEffect( EffectManager.TYPE.ON_SELF,
                            GameManager.getInstance().player ,
                            GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                            EffectManager.getInstance().createInPlaceEffects(5) ,
                            new EffectConfig(0 , -12 , 0 , 1.7) );
            //===========================================================================================
        }
    }

    @Override
    public int getHeal() {
        return HEAL;
    }
}
