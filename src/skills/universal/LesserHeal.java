package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import utils.Config;
import pieces.Healable;

public class LesserHeal extends BaseSkill implements Healable {
    private BasePiece target;

    private final int HEAL = 3;
    public LesserHeal() {
        super("Lesser Heal", Color.LIMEGREEN, 1, 3,
                "Good enough for now", Config.Rarity.UNCOMMON, Config.sfx_powerupSound);

        icon = new ImageView(Config.LesserHealPath);
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
        BasePlayerPiece player = GameManager.getInstance().player;

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
                        new EffectConfig(-48 , -52 , 0 , 1.2) );
        //===========================================================================================
    }

    @Override
    public int getHeal() {
        return HEAL;
    }
}
