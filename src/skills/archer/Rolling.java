package skills.archer;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
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
    }

    public void roll() {
        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);

        // Teleport the player to the target position
        BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
        pieces[GameManager.getInstance().player.getRow()][GameManager.getInstance().player.getCol()] = null;
        pieces[target.getRow()][target.getCol()] = GameManager.getInstance().player;

        GameManager.getInstance().player.moveWithTransition(target.getRow(), target.getCol());
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
