package pieces.enemies;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.SpawnerManager;
import logic.SpriteAnimation;
import pieces.BasePiece;
import pieces.BaseStatus;
import pieces.player.BasePlayerPiece;
import utils.Config;

public abstract class BaseMonsterPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;
    private int currentDirection;
    private boolean isAlive = true;

    protected SpriteAnimation spriteAnimation;
    public ImageView animationImage;
    protected TranslateTransition moveTransition;
    //offset for image
    public int offsetX=0;
    public int offsetY=0;


    public BaseMonsterPiece(int row, int col, int defaultDirection) {
        super(Config.ENTITY_TYPE.MONSTER, new ImageView(Config.PlaceholderPath), row, col);
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
        return isAlive;
    }

    @Override
    public void onDeath() {
        isAlive = false;
        SpawnerManager.getInstance().monsterCount--;
        SpawnerManager.getInstance().trySpawnDoor(getRow(), getCol());
        // To call when this monster died
        System.out.println(this.getClass().getSimpleName() + " is dead @" + getRow() + " " + getCol());
    }

    public abstract void performAction(); // To call when it's this monster turn
    public abstract void updateState(int playerRow, int playerCol); // Update the state of monster
    protected abstract boolean isValidMoveSet(int row, int col); // Each monster have unique move set
    public abstract void moveWithTransition(int col , int row);

    protected void move(int newRow, int newCol) {
        if (!GameManager.getInstance().isEmptySquare(newRow, newCol)) return;

        moveWithTransition(newRow , newCol);

        BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
        pieces[getRow()][getCol()] = null;
        pieces[newRow][newCol] = this;
    }

    @Override
    public void takeDamage(int damage) {
        setCurrentHealth(currentHp - damage);
    }

    public void changeDirection(int direction) {
        if (direction != 1 && direction != -1) {
            return;
        }
        if (currentDirection != direction) {
            currentDirection = direction;
            ImageView imageView = animationImage;
            imageView.setScaleX(direction); // Flipping the image horizontally if direction is -1
        }
    }

    protected abstract void setupAnimation();
}
