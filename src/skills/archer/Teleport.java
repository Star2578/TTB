package skills.archer;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import pieces.BasePiece;
import skills.BaseSkill;
import utils.Config;

public class Teleport extends BaseSkill {
    public Teleport() {
        super("Teleport", Color.DARKRED, 0, 0, "Teleport to a chosen location", Config.Rarity.COMMON);
        icon = new ImageView();
        range = 5; // Set the range to 5
    }

    @Override
    public void perform(BasePiece target) {
        // Teleport the player to the target position
        GameManager.getInstance().player.setRow(target.getRow());
        GameManager.getInstance().player.setCol(target.getCol());
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