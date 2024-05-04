package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
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

                // have a chance to knockback enemy
                double knockChance = Math.random() * 100; // Generate a random number between 0 and 100
                if (knockChance <= 50) { // 50% chance for knockback
                    int currentRow = GameManager.getInstance().player.getRow();
                    int currentCol = GameManager.getInstance().player.getCol();
                    int directionRow = target.getRow() - currentRow;
                    int directionCol = target.getCol() - currentCol;
                    // Normalize the direction
                    if (directionRow != 0) directionRow /= Math.abs(directionRow);
                    if (directionCol != 0) directionCol /= Math.abs(directionCol);

                    int newRow=target.getRow(),newCol = target.getCol();

                    for (int i = 1; i <= KNOCKBACK; i++) {
                        if (!GameManager.getInstance().isEmptySquare(target.getRow() + directionRow * i, target.getCol() + directionCol * i)) {
                            break;
                        }
                        newRow = target.getRow() + directionRow * i;
                        newCol = target.getCol() + directionCol * i;
                    }

                    BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
                    pieces[target.getRow()][target.getCol()] = null;
                    pieces[newRow][newCol] = target;

                    //=========<SKILL EFFECT>====================================================================
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.AROUND_SELF ,
                                    GameManager.getInstance().player ,
                                    target.getRow(), target.getCol(),
                                    EffectMaker.getInstance().createInPlaceEffects(36) ,
                                    new EffectConfig(0, -18 , 28, 1.3) );
                    //===========================================================================================

                    target.moveWithTransition(newRow, newCol);
                }

                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
