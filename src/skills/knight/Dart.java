package skills.knight;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class Dart extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 5;
    private final int KNOCKBACK = 2;
    public Dart() {
        super("Dart", Color.DARKRED, 4, 0, "With a deft flick of their wrist, they send forth a piercing strike that packs a wallop, knocking back adversaries with formidable force.", Config.Rarity.UNCOMMON, "res/SFX/skills/slash/PP_01.wav");
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
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(4) ,
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