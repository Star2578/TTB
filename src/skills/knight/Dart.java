package skills.knight;

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

public class Dart extends BaseSkill implements Attackable {
    private BasePiece target;
    private final int DAMAGE = 8;
    private final int KNOCKBACK = 2;
    public Dart() {
        super("Dart", Color.DARKRED, 4, 0, "Knock target 2 blocks away from you", Config.Rarity.UNCOMMON, "SFX/skills/slash/PP_01.wav");
        icon = new ImageView(Config.DartPath);
        range = 1;
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(DAMAGE);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);

                //=========<SKILL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(4) ,
                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================

                int currentRow = GameManager.getInstance().player.getRow();
                int currentCol = GameManager.getInstance().player.getCol();
                int directionRow = target.getRow() - currentRow;
                int directionCol = target.getCol() - currentCol;
                // Normalize the direction
                if (directionRow != 0) directionRow /= Math.abs(directionRow);
                if (directionCol != 0) directionCol /= Math.abs(directionCol);

                int tempRow = target.getRow();
                int tempCol = target.getCol();

                for (int i = 1; i <= KNOCKBACK; i++) {
                    int newRow = target.getRow() + directionRow * i;
                    int newCol = target.getCol() + directionCol * i;
                    if (!GameManager.getInstance().isEmptySquare(newRow, newCol)) {
                        break;
                    }
                    BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
                    pieces[tempRow][tempCol] = null;
                    pieces[newRow][newCol] = target;
                    tempRow = newRow;
                    tempCol = newCol;

                    target.moveWithTransition(newRow, newCol);
                }


                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

            }
        }
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

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}