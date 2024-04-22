package skills.wizard;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Buff;
import utils.Config;

public class IceShield extends BaseSkill implements Buff {
    private BasePiece target;
    private final int BUFF_DURATION = 2;
    public IceShield() {
        super("Ice Shield", Color.DARKCYAN, 5, 2, "", Config.Rarity.COMMON);
        //TODO===
        icon = new ImageView(Config.SlashPath);
        range = 0;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        applyBuff();
    }

    @Override
    public void applyBuff() {
        if (target != null && target instanceof BasePlayerPiece player) {
            // Perform Buff
            GameManager.getInstance().player.addBuff(BUFF_DURATION, name);
            GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
            GameManager.getInstance().player.decreaseMana(manaCost);
            System.out.println("Use " + name + " on " + player.getClass().getSimpleName());
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
