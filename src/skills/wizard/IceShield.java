package skills.wizard;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.Effect;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import pieces.Buffable;
import utils.Config;

public class IceShield extends BaseSkill implements Buffable {
    private BasePiece target;
    private final int BUFF_DURATION = 3;
    public IceShield() {
        super("Ice Shield", Color.DARKCYAN,
                5, 2,
                "Summon Ice Shield that Decrease Damage by 30% for 2 turns",
                Config.Rarity.RARE , "SFX/skills/slash/PP_01.wav"
        );
        icon = new ImageView(Config.IceShieldPath);
        range = 0;
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
            EffectMaker.getInstance()
                    .renderEffect( EffectMaker.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                            EffectMaker.getInstance().createInPlaceEffects(27) ,
                            new EffectConfig(0 , -16 , 0 , 1.1) );
            //===========================================================================================
            //=========<SKILL BUFF EFFECT>====================================================================
            Effect shield = EffectMaker.getInstance().createInPlaceEffects(28);
            EffectMaker.getInstance()
                    .renderEffect( EffectMaker.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                            shield ,
                            new EffectConfig(3 , -8 , 0 , 1.4) );
            shield.bindToOwnerMovement(GameManager.getInstance().player);
            shield.setTurnRemain(BUFF_DURATION+1);
            //===========================================================================================

        }
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
    public boolean validRange(int row, int col) {
        // Only valid at player's square
        BasePlayerPiece player = GameManager.getInstance().player;

        return player.getRow() == row && player.getCol() == col;
    }

    @Override
    public int getDuration() {
        return BUFF_DURATION;
    }
}
