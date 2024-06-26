package skills.wizard;

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

public class RainOfFire extends BaseSkill implements Attackable {
    private BasePiece target;
    private final int DAMAGE = 6;
    public RainOfFire() {
        super("Rain of Fire", Color.DARKORANGE,
                7, 2,
                "Summon a rain of fire above the enemies for  2 x 2 range around the enemy",
                Config.Rarity.LEGENDARY, "SFX/skills/slash/PP_01.wav");

        icon = new ImageView(Config.RainOfFirePath);
        range = 3;
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
                System.out.println("Damage: " + DAMAGE);
                int newRow = monsterPiece.getRow();
                int newCol = monsterPiece.getCol();
                BasePiece piece2 = GameManager.getInstance().piecesPosition[newRow+1][newCol];
                BasePiece piece3 = GameManager.getInstance().piecesPosition[newRow][newCol+1];
                BasePiece piece4 = GameManager.getInstance().piecesPosition[newRow][newCol-1];
                BasePiece piece5 = GameManager.getInstance().piecesPosition[newRow-1][newCol];

                PieceAttack(piece2);
                PieceAttack(piece3);
                PieceAttack(piece4);
                PieceAttack(piece5);

                System.out.println("Attack at " + newRow + " " + newCol);
                System.out.println("Attack at " + (newRow+1) + " " + newCol);
                System.out.println("Attack at " + newRow + " " + (newCol+1));
                System.out.println("Attack at " + newRow + " " + (newCol-1));
                System.out.println("Attack at " + (newRow-1) + " " + newCol);

                renderEffects(newRow, newCol);
                renderEffects(newRow+1, newCol);
                renderEffects(newRow, newCol+1);
                renderEffects(newRow-1, newCol);
                renderEffects(newRow, newCol-1);

            }
        }
    }

    private void renderEffects(int newRow, int newCol) {
        //=========<SKILL EFFECT>====================================================================
        EffectMaker.getInstance()
                .renderEffect( EffectMaker.TYPE.ON_SELF ,
                        GameManager.getInstance().player ,
                        newRow, newCol,
                        EffectMaker.getInstance().createInPlaceEffects(25) ,
                        new EffectConfig(0 , -16 , 34 , 1.2) );
        //===========================================================================================
        //=========<SKILL EFFECT TAKE DAMAGE>====================================================================
        if (GameManager.getInstance().validMovesCache[newRow][newCol])
            EffectMaker.getInstance()
                    .renderEffect(EffectMaker.TYPE.ON_SELF,
                            GameManager.getInstance().player,
                            newRow, newCol,
                            EffectMaker.getInstance().createInPlaceEffects(23),
                            new EffectConfig(-16, -19, 0, 1));
        //===========================================================================================
    }

    private boolean checkRange (int row, int col) {
        return row >= 0 && row < Config.BOARD_SIZE && col >= 0 && col < Config.BOARD_SIZE;
    }

    private void PieceAttack (BasePiece piece) {
        if (piece instanceof BaseMonsterPiece monsterPiece) {
            monsterPiece.takeDamage(getAttack());
            System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());
            System.out.println("Damage: " + DAMAGE);
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
