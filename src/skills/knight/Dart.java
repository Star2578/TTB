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
    private final int DAMAGE = 3;
    private final int KNOCKBACK = 2;
    public Dart() {
        super("Dart", Color.DARKRED, 2, 2, "With a deft flick of their wrist, they send forth a piercing strike that packs a wallop, knocking back adversaries with formidable force.", Config.Rarity.COMMON, "res/SFX/skills/slash/PP_01.wav");
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

                int currentRow = GameManager.getInstance().player.getRow();
                int currentCol = GameManager.getInstance().player.getCol();
                int dRow = target.getRow() - currentRow;
                int dCol = target.getCol() - currentCol;
                int directionRow = dRow;
                int directionCol = dCol;
                // Normalize the direction
                if (directionRow != 0) directionRow /= Math.abs(directionRow);
                if (directionCol != 0) directionCol /= Math.abs(directionCol);

                for (int i = 1;i <= KNOCKBACK; i++){
                    int newRow = target.getRow() + directionRow * i;
                    int newCol = target.getCol() + directionCol * i;
                    if (!GameManager.getInstance().isEmptySquare(newRow, newCol)) {
                        break;
                    }
                    GameManager.getInstance().piecesPosition[target.getRow()][target.getCol()] = null;
                    target.setRow(newRow);
                    target.setCol(newCol);
                    GameManager.getInstance().piecesPosition[newRow][newCol] = target;
                }

                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target ,
                                EffectManager.getInstance().createInPlaceEffects(1) ,
                                new EffectConfig(0 , -16 , 24 , 1.1) );
                //===========================================================================================
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