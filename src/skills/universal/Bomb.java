package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import pieces.BasePiece;
import skills.BaseSkill;
import utils.Config;

public class Bomb extends BaseSkill {
    private BasePiece target;
    private final int DAMAGE = 10;
    private GameManager gameManager = GameManager.getInstance();

    public Bomb() {
        super("Bomb", Color.DARKGRAY, 3, 4, "Hot Bomb will explode in 2 turns!", Config.Rarity.COMMON, "");
        icon = new ImageView(Config.PlaceholderPath);
    }
    @Override
    public void perform(BasePiece target) {
        // TODO : Summon a bomb into the board at selected position
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
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
