package pieces.enemies;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import logic.GameManager;
import pieces.BasePiece;
import pieces.BaseStatus;
import pieces.player.BasePlayerPiece;
import utils.Config;

public abstract class BaseMonsterPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;
    private int currentDirection;

    public BaseMonsterPiece(int row, int col, int defaultDirection) {
        super("Monster", new ImageView(Config.PlaceholderPath), row, col);
        if (defaultDirection == -1) {
            ImageView imageView = getTexture();
            imageView.setScaleX(-1); // Flipping the image horizontally
        }
    }

    @Override
    public int getCurrentHealth() {
        return currentHp;
    }

    @Override
    public void setCurrentHealth(int health) {
        this.currentHp = Math.max(health, 0);
        if (currentHp == 0) onDeath();
    }

    @Override
    public int getMaxHealth() {
        return maxHp;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        int maxHpBuffer = maxHp;
        this.maxHp = Math.max(maxHealth, 1);

        if (maxHp == maxHpBuffer) currentHp = maxHp;
        if (maxHp < currentHp) currentHp = maxHp;
    }

    public abstract void attack(BasePlayerPiece playerPiece);

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void onDeath() {
        // To call when this monster died
        System.out.println(this.getClass().getSimpleName() + " is dead @" + getRow() + " " + getCol());
    }

    public abstract void performAction(); // To call when it's this monster turn
    public abstract void updateState(int playerRow, int playerCol); // Update the state of monster
    protected abstract boolean isValidMoveSet(int row, int col); // Each monster have unique move set

    protected void move(int newRow, int newCol) {
        if (!GameManager.getInstance().isEmptySquare(newRow, newCol)) return;

        // Update the position of the monster on the board
        GridPane.setRowIndex(getTexture(), newRow);
        GridPane.setColumnIndex(getTexture(), newCol);

        BasePiece[][] pieces = GameManager.getInstance().pieces;
        pieces[getRow()][getCol()] = null;
        pieces[newRow][newCol] = this;
        setRow(newRow);
        setCol(newCol);
    }

    public void changeDirection(int direction) {
        if (direction != 1 && direction != -1) {
            return;
        }
        if (currentDirection != direction) {
            currentDirection = direction;
            ImageView imageView = getTexture();
            imageView.setScaleX(direction); // Flipping the image horizontally if direction is -1
        }
    }
}
