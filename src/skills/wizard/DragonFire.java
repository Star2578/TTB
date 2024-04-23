package skills.wizard;

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

public class DragonFire extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 15;
    public DragonFire() {
        super("Dragon Fire", Color.ORANGE, 15, 2, "Ultimate Skill Summon a dragon fire to the enemies by Triangle AOE for 3 range ", Config.Rarity.RARE);

        icon = new ImageView(Config.DragonFirePath);
        range = 3;
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

        // Perform the attack
        for (int i = 0; i < range; i++) {
            int newRow = currentRow + directionRow * i;
            int newCol = currentCol + directionCol * i;
            System.out.println(newRow + " " + newCol);
            if(i == 0 && checkRange(newRow,newCol)) {
                BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];
                PieceAttack(piece);
            }
        }
    }

    private boolean checkRange (int row, int col) {
        return row >= 0 && row < Config.BOARD_SIZE && col >= 0 && col < Config.BOARD_SIZE;
    }

    private void PieceAttack (BasePiece piece) {
        if (piece instanceof BaseMonsterPiece monsterPiece) {
            monsterPiece.takeDamage(getAttack());
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
