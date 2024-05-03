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

public class Kick extends BaseSkill implements Attackable {
    private BasePiece target;

    private final int DAMAGE = 1;
    private final int KNOCKBACK = 3;
    public Kick() {
        super("Kick", Color.DARKCYAN, 4, 1,
                "Kick *yourself* away from danger", Config.Rarity.UNCOMMON, Config.sfx_attackSound);

        icon = new ImageView(Config.KickPath);
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
        return true;
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(1) ,
                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================


                int currentRow = target.getRow();
                int currentCol = target.getCol();
                int directionRow = GameManager.getInstance().player.getRow() - currentRow;
                int directionCol = GameManager.getInstance().player.getCol() - currentCol;
                // Normalize the direction
                if (directionRow != 0) directionRow /= Math.abs(directionRow);
                if (directionCol != 0) directionCol /= Math.abs(directionCol);

                int newRow=GameManager.getInstance().player.getRow();
                int newCol = GameManager.getInstance().player.getCol();
                for (int i = 1; i <= KNOCKBACK; i++) {
                    if (!GameManager.getInstance().isEmptySquare(
                                    GameManager.getInstance().player.getRow() + directionRow * i,
                                    GameManager.getInstance().player.getRow() + directionRow * i)) {
                        break;
                    }
                    newRow = GameManager.getInstance().player.getRow() + directionRow * i;
                    newCol = GameManager.getInstance().player.getCol() + directionCol * i;
                }
                BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
                pieces[GameManager.getInstance().player.getRow()][GameManager.getInstance().player.getCol()] = null;
                pieces[newRow][newCol] = GameManager.getInstance().player;
                GameManager.getInstance().player.moveWithTransition(newRow, newCol);
                
            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
