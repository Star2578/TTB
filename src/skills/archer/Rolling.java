package skills.archer;

import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import skills.BaseSkill;
import utils.Config;

public class Rolling extends BaseSkill {
    private BasePiece target;

    public Rolling() {
        super("Rolling", Color.BROWN, 0, 2,
                "Oldest trick in the book to disengage, or the opposite", Config.Rarity.UNCOMMON, Config.sfx_moveSound);

        icon = new ImageView(Config.RollingPath);
        range = 3;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        roll();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    public void roll() {
        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);

        // change the player position to the target position
        boolean isRollLeft = (GameManager.getInstance().player.getCol() > target.getCol());//use in rotating later
        BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
        pieces[GameManager.getInstance().player.getRow()][GameManager.getInstance().player.getCol()] = null;
        pieces[target.getRow()][target.getCol()] = GameManager.getInstance().player;

        // make player move
        GameManager.getInstance().player.moveWithTransition(target.getRow(), target.getCol());
        // make player rotate
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(500),GameManager.getInstance().player.animationImage);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(isRollLeft? -360 : 360);
        rotateTransition.play();

    }

    @Override
    public boolean validRange(int row, int col) {
        // Get the current row and column
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        // Check if the row or column is the same as the current position
        return (row == currentRow || col == currentCol) && Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }
}
