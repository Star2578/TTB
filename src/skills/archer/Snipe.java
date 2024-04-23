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
    private final int DAMAGE = 18;
    public Snipe() {
        super("Snipe", Color.DARKRED, 1, 2, "", Config.Rarity.COMMON);
        icon = new ImageView(Config.SlashPath);
        range = 7;
    }

    @Override
    public void attack() {
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();
        int dRow = target.getRow() - currentRow;
        int dCol = target.getCol() - currentCol;
        int directionRow = dRow;
        int directionCol = dCol;

        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
        GameManager.getInstance().player.decreaseMana(manaCost);

        // Normalize the direction
        if (dRow != 0) directionRow /= Math.abs(dRow);
        if (dCol != 0) directionCol /= Math.abs(dCol);

        for (int i = 1; i <= range; i++) {
            int newRow, newCol;
            int dAbsCol = Math.abs(dCol);
            int dAbsRow = Math.abs(dRow);

            if (dAbsCol >= dAbsRow) {
                int y = (int) Math.round(i * Math.tan(Math.atan2(dAbsRow, dAbsCol)));
                newRow = currentRow + directionRow * y;
                newCol = currentCol + directionCol * i;
            } else {
                int x = (int) Math.round(i * Math.tan(Math.atan2(dAbsCol, dAbsRow)));
                newRow = currentRow + directionRow * i;
                newCol = currentCol + directionCol * x;
            }

            if (newRow < 0 || newRow >= Config.BOARD_SIZE || newCol < 0 || newCol >= Config.BOARD_SIZE) {
                break;
            }

            BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];

            if (piece instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(getAttack());
            }

            System.out.println("Use " + name + " on " + newRow + " " + newCol);
        }
        // Perform the attack
//        for (int i = 1; i <= range; i++) {
//            int newRow = currentRow + directionRow * i;
//            int newCol = currentCol + directionCol * i;
//
//            if (newRow < 0 || newRow >= Config.BOARD_SIZE || newCol < 0 || newCol >= Config.BOARD_SIZE) {
//                break;
//            }
//
//            BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];
//            if (piece instanceof BaseMonsterPiece monsterPiece) {
//                monsterPiece.takeDamage(getAttack());
//            }
//
//            System.out.println("Use " + name + " on " + " " + newRow + " " + newCol);
//        }
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
