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

public class DragonFire extends BaseSkill implements Attackable {
    private BasePiece target;
    private final int DAMAGE = 24;
    public DragonFire() {
        super("Dragon Fire", Color.ORANGE,
                15, 2,
                "Ultimate Skill Summon a dragon fire to the enemies by Triangle AOE for 3 range ",
                Config.Rarity.RARE, "res/SFX/skills/slash/PP_01.wav"
        );

        icon = new ImageView(Config.DragonFirePath);
        range = 3;
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
    directionRow = normalizeDirection(directionRow);
    directionCol = normalizeDirection(directionCol);

    // Perform the attack
    for (int i = 1; i <= range; i++) {
        performAttackInDirection(currentRow, currentCol, directionRow, directionCol, i);
    }
}

    private int normalizeDirection(int direction) {
        return direction != 0 ? direction / Math.abs(direction) : 0;
    }

    private void performAttackInDirection(int currentRow, int currentCol, int directionRow, int directionCol, int i) {
        int newRow = currentRow + directionRow * i;
        int newCol = currentCol + directionCol * i;

        BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];

        if (piece instanceof BaseMonsterPiece monsterPiece) {
            monsterPiece.takeDamage(getAttack());
        }

        renderEffects(newRow, newCol);

        if (i == 2) {
            performSecondaryAttack(newRow, newCol, directionRow, directionCol);
        }

        if (i == 3) {
            performTertiaryAttack(newRow, newCol, directionRow, directionCol);
        }
    }

    private void renderEffects(int newRow, int newCol) {
        if (GameManager.getInstance().validMovesCache[newRow][newCol]) {
            EffectMaker.getInstance()
                    .renderEffect(EffectMaker.TYPE.ON_SELF,
                            GameManager.getInstance().player,
                            newRow, newCol,
                            EffectMaker.getInstance().createInPlaceEffects(26),
                            new EffectConfig(0, -16, 0, 1.2));


            EffectMaker.getInstance()
                    .renderEffect(EffectMaker.TYPE.ON_SELF,
                            GameManager.getInstance().player,
                            newRow, newCol,
                            EffectMaker.getInstance().createInPlaceEffects(23),
                            new EffectConfig(-16, -19, 0, 1));
        }
    }

    private void performSecondaryAttack(int newRow, int newCol, int directionRow, int directionCol) {
        //------------------- X -------------------
                if(directionCol != 0 && directionRow != 0) {
                    BasePiece piece2 = GameManager.getInstance().piecesPosition[newRow - directionRow][newCol];
                    BasePiece piece3 = GameManager.getInstance().piecesPosition[newRow][newCol - directionCol];
                    if (piece2 instanceof BaseMonsterPiece monsterPiece2) {
                        monsterPiece2.takeDamage(getAttack());
                    }
                    if (piece3 instanceof BaseMonsterPiece monsterPiece3) {
                        monsterPiece3.takeDamage(getAttack());
                    }
                    renderEffects(newRow - directionRow, newCol);
                    renderEffects(newRow, newCol - directionCol);
                    System.out.println((newRow - directionRow) + " " + newCol);
                    System.out.println(newRow + " " + (newCol - directionCol));
                }
                //-------------------- + --------------------
                else {
                    if (directionCol == 0){
                        BasePiece piece2 = GameManager.getInstance().piecesPosition[newRow][newCol+1];
                        BasePiece piece3 = GameManager.getInstance().piecesPosition[newRow][newCol-1];
                        if (piece2 instanceof BaseMonsterPiece monsterPiece2) {
                            monsterPiece2.takeDamage(getAttack());
                        }
                        if (piece3 instanceof BaseMonsterPiece monsterPiece3) {
                            monsterPiece3.takeDamage(getAttack());
                        }
                        renderEffects(newRow, newCol+1);
                        renderEffects(newRow, newCol-1);
                        System.out.println((newRow) + " " + (newCol+1));
                        System.out.println((newRow) + " " + (newCol-1));
                    }
                    else {
                        BasePiece piece2 = GameManager.getInstance().piecesPosition[newRow+1][newCol];
                        BasePiece piece3 = GameManager.getInstance().piecesPosition[newRow-1][newCol];
                        if (piece2 instanceof BaseMonsterPiece monsterPiece2) {
                            monsterPiece2.takeDamage(getAttack());
                        }
                        if (piece3 instanceof BaseMonsterPiece monsterPiece3) {
                            monsterPiece3.takeDamage(getAttack());
                        }
                        renderEffects(newRow+1, newCol);
                        renderEffects(newRow-1, newCol);
                        System.out.println((newRow+1) + " " + (newCol));
                        System.out.println((newRow-1) + " " + (newCol));
                    }
                }
    }

    private void performTertiaryAttack(int newRow, int newCol, int directionRow, int directionCol) {
        // Implementation of tertiary attack
        // ------------------------- x ---------------------------
                if(directionCol != 0 && directionRow != 0) {
                    BasePiece piece4 = GameManager.getInstance().piecesPosition[newRow - directionRow * 2][newCol];
                    BasePiece piece5 = GameManager.getInstance().piecesPosition[newRow][newCol - directionCol * 2];
                    BasePiece piece6 = GameManager.getInstance().piecesPosition[newRow - directionRow][newCol];
                    BasePiece piece7 = GameManager.getInstance().piecesPosition[newRow][newCol - directionCol];

                    if (piece4 instanceof BaseMonsterPiece monsterPiece4) {
                        monsterPiece4.takeDamage(getAttack());
                    }
                    if (piece5 instanceof BaseMonsterPiece monsterPiece5) {
                        monsterPiece5.takeDamage(getAttack());
                    }
                    if (piece6 instanceof BaseMonsterPiece monsterPiece6) {
                        monsterPiece6.takeDamage(getAttack());
                    }
                    if (piece7 instanceof BaseMonsterPiece monsterPiece7) {
                        monsterPiece7.takeDamage(getAttack());
                    }
                    renderEffects(newRow - directionRow, newCol);
                    renderEffects(newRow, newCol - directionCol);
                    renderEffects(newRow - directionRow * 2, newCol);
                    renderEffects(newRow, newCol - directionCol * 2);
                    System.out.println((newRow - directionRow) + " " + newCol);
                    System.out.println(newRow + " " + (newCol - directionCol));
                    System.out.println((newRow - directionRow * 2) + " " + newCol);
                    System.out.println(newRow + " " + (newCol - directionCol * 2));
                }
                //-------------------- + --------------------
                else {
                    if (directionCol == 0){
                        BasePiece piece4 = GameManager.getInstance().piecesPosition[newRow][newCol+2];
                        BasePiece piece5 = GameManager.getInstance().piecesPosition[newRow][newCol-2];
                        BasePiece piece6 = GameManager.getInstance().piecesPosition[newRow][newCol+1];
                        BasePiece piece7 = GameManager.getInstance().piecesPosition[newRow][newCol-1];
                        if (piece4 instanceof BaseMonsterPiece monsterPiece4) {
                            monsterPiece4.takeDamage(getAttack());
                        }
                        if (piece5 instanceof BaseMonsterPiece monsterPiece5) {
                            monsterPiece5.takeDamage(getAttack());
                        }
                        if (piece6 instanceof BaseMonsterPiece monsterPiece6) {
                            monsterPiece6.takeDamage(getAttack());
                        }
                        if (piece7 instanceof BaseMonsterPiece monsterPiece7) {
                            monsterPiece7.takeDamage(getAttack());
                        }
                        renderEffects(newRow, newCol + 1);
                        renderEffects(newRow, newCol - 1);
                        renderEffects(newRow, newCol + 2);
                        renderEffects(newRow, newCol - 2 );
                        System.out.println((newRow) + " " + (newCol + 1));
                        System.out.println(newRow + " " + (newCol - 1));
                        System.out.println((newRow) + " " + (newCol + 2));
                        System.out.println(newRow + " " + (newCol - 2 ));
                    }
                    else {
                        BasePiece piece4 = GameManager.getInstance().piecesPosition[newRow+2][newCol];
                        BasePiece piece5 = GameManager.getInstance().piecesPosition[newRow-2][newCol];
                        BasePiece piece6 = GameManager.getInstance().piecesPosition[newRow+1][newCol];
                        BasePiece piece7 = GameManager.getInstance().piecesPosition[newRow-1][newCol];

                        if (piece4 instanceof BaseMonsterPiece monsterPiece4) {
                            monsterPiece4.takeDamage(getAttack());
                        }
                        if (piece5 instanceof BaseMonsterPiece monsterPiece5) {
                            monsterPiece5.takeDamage(getAttack());
                        }
                        if (piece6 instanceof BaseMonsterPiece monsterPiece6) {
                            monsterPiece6.takeDamage(getAttack());
                        }
                        if (piece7 instanceof BaseMonsterPiece monsterPiece7) {
                            monsterPiece7.takeDamage(getAttack());
                        }
                        renderEffects(newRow+1, newCol);
                        renderEffects(newRow-1, newCol);
                        renderEffects(newRow+2, newCol);
                        renderEffects(newRow-2, newCol);
                        System.out.println((newRow+1) + " " + newCol );
                        System.out.println((newRow-1) + " " + (newCol ));
                        System.out.println((newRow+2) + " " + (newCol));
                        System.out.println((newRow-2) + " " + (newCol));
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


