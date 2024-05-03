package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;

public class Punch extends BaseSkill implements Attackable {
    private BasePiece target;

    private final int DAMAGE = 3;
    private final int KNOCKBACK = 2;
    public Punch() {
        super("Punch", Color.ORANGE, 1, 2,
                "50% chances to knock enemy back", Config.Rarity.UNCOMMON, Config.sfx_attackSound);

        icon = new ImageView(Config.PunchPath);
        range = 1;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
        SoundManager.getInstance().playSoundEffect(sfxPath);
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

    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);

                // Check for knockback
                double knockChance = Math.random() * 100; // Generate a random number between 0 and 100
                if (knockChance <= 50) { // 50% chance for knockback
                    int currentRow = target.getRow();
                    int currentCol = target.getCol();
                    int directionRow = GameManager.getInstance().player.getRow() - currentRow;
                    int directionCol = GameManager.getInstance().player.getCol() - currentCol;
                    // Normalize the direction
                    if (directionRow != 0) directionRow /= Math.abs(directionRow);
                    if (directionCol != 0) directionCol /= Math.abs(directionCol);

                    int newRow = 0;
                    int newCol = 0;
                    for (int i = 1; i <= KNOCKBACK; i++) {
                        newRow = GameManager.getInstance().player.getRow() + directionRow * i;
                        newCol = GameManager.getInstance().player.getCol() + directionCol * i;
                        if (!GameManager.getInstance().isEmptySquare(newRow, newCol)) {
                            break;
                        }
                    }

                    GameManager.getInstance().piecesPosition[GameManager.getInstance().player.getRow()][GameManager.getInstance().player.getCol()] = null;
                    GameManager.getInstance().player.moveWithTransition(newRow, newCol);
                    GameManager.getInstance().piecesPosition[newRow][newCol] = GameManager.getInstance().player;
                }
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(33) ,
                                new EffectConfig(0 , -16 , 0 , 1.1) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
