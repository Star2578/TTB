package skills.archer;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class Snipe extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 7;
    public Snipe() {
        super("Snipe", Color.DARKRED, 5, 2, "", Config.Rarity.COMMON);
        icon = new ImageView(Config.SlashPath);
        range = 7;
    }

    @Override
    public void attack() {
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();
        int directionRow = target.getRow() - currentRow;
        int directionCol = target.getCol() - currentCol;

        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
        GameManager.getInstance().player.decreaseMana(manaCost);

        // Normalize the direction
        if (directionRow != 0) directionRow /= Math.abs(directionRow);
        if (directionCol != 0) directionCol /= Math.abs(directionCol);

        // Perform the attack
        for (int i = 0; i < range; i++) {
            int newRow = currentRow + directionRow * i;
            int newCol = currentCol + directionCol * i;

            BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];
            if (piece instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(getAttack());
            }
        }
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        return Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return true;
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }



}
