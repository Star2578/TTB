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
import pieces.Buffable;
import utils.Config;

public class Rho_Aias extends BaseSkill implements Buffable {
    private BasePiece target;

    private final int BUFF_DURATION = 1;
    public Rho_Aias() {
        super("Rho Aias", Color.PINK, 10, 2,
                "Reduce incoming damage by 80% for 1 turn", Config.Rarity.LEGENDARY, "");

        icon = new ImageView(Config.RhoAiasPath);
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        applyBuff();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public void applyBuff() {
        if (target != null && target instanceof BasePlayerPiece player) {
            // Perform Buff
            GameManager.getInstance().player.addBuff(BUFF_DURATION, name);
            GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
            GameManager.getInstance().player.decreaseMana(manaCost);
            System.out.println("Use " + name + " on " + player.getClass().getSimpleName());

            //=========<SKILL EFFECT>====================================================================
            EffectManager.getInstance()
                    .renderEffect( EffectManager.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                            EffectManager.getInstance().createInPlaceEffects(31) ,
                            new EffectConfig(0 , -16 , 0 , 1.1) );
            //===========================================================================================
        }
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
    public int getDuration() {
        return BUFF_DURATION;
    }
}
