package skills.knight;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import pieces.Healable;
import utils.Config;

public class Heal extends BaseSkill implements Healable {
    private BasePiece target;
    private final int HEAL = 5;
    public Heal() {
        super("Heal", Color.DARKGREEN, 5, 10, "Rest for one turn to heal", Config.Rarity.COMMON, "res/SFX/skills/heal/8bit-powerup1.wav");
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
            EffectMaker.getInstance()
                    .renderEffect( EffectMaker.TYPE.ON_SELF,
                            GameManager.getInstance().player ,
                            GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                            EffectMaker.getInstance().createInPlaceEffects(5) ,
                            new EffectConfig(-48 , -52 , 0 , 1.2) );
            //===========================================================================================
        }
    }

    @Override
    public int getHeal() {
        return HEAL;
    }
}
