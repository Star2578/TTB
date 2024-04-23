package skills.archer;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import pieces.BasePiece;
import skills.BaseSkill;
import utils.Config;

public class Teleport extends BaseSkill {
    private BasePiece target;
    public Teleport() {
        super("Teleport", Color.DARKRED, 7, 2, "Teleport to a chosen location", Config.Rarity.COMMON);
        icon = new ImageView(Config.HealPath);
        range = 5; // Set the range to 5
    }

    public void teleport() {

        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
        GameManager.getInstance().player.decreaseMana(manaCost);

        // Teleport the player to the target position
        BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
        pieces[GameManager.getInstance().player.getRow()][GameManager.getInstance().player.getCol()] = null;
        pieces[target.getRow()][target.getCol()] = GameManager.getInstance().player;

//        GameManager.getInstance().player.setRow(target.getRow());
//        GameManager.getInstance().player.setCol(target.getCol());
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        teleport();
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        // Check if the target is within range
        return Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
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