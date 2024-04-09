package pieces.enemies;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpawnerManager;
import logic.SpriteAnimation;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.BaseStatus;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public abstract class BaseMonsterPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;
    private int currentDirection;
    private boolean isAlive = true;
    protected boolean endAction = false;
    protected boolean[][] validMovesCache; // Cache of valid moves for the entire board
    protected Random random;

    public BaseMonsterPiece(int row, int col, int defaultDirection) {
        super(Config.ENTITY_TYPE.MONSTER, new ImageView(Config.PlaceholderPath), row, col);
        this.validMovesCache = GameManager.getInstance().validMovesCache;
        this.random = new Random();

        // insert 1 as default (image facing right)
        // insert -1 to flip
        if (defaultDirection == -1) {
            ImageView imageView = getTexture();
            imageView.setScaleX(-1); // Flipping the image horizontally
        }
    }

    /******************************************
    *             Abstract fields
    ******************************************/
    public abstract void performAction(); // To call when it's this monster turn (in TurnManager)
    public abstract void updateState(); // Update the state of monster
    public abstract void attack(BasePlayerPiece playerPiece);

    /******************************************
     *                  Utils
     ******************************************/

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
    // Method to roam around randomly
    protected void roamRandomly() {
        // Get the list of valid moves from the cache
        List<int[]> validMoves = getValidMoves(getRow(), getCol());

        // If there are valid moves, randomly choose one and move to that position
        if (!validMoves.isEmpty()) {
            int[] randomMove = validMoves.get(random.nextInt(validMoves.size()));
            int newRow = randomMove[0];
            int newCol = randomMove[1];

            // Determine the direction of movement
            int newDirection = Integer.compare(newCol, getCol());

            // Call changeDirection with the new direction
            changeDirection(newDirection);

            // Move the zombie to the new position
            move(newRow, newCol);
        }

        endAction = true;
    }
    // Get valid moves by checking for empty squares, no other entity, no wall and isValidMoveSet
    protected List<int[]> getValidMoves(int row, int col) {
        List<int[]> validMoves = new ArrayList<>();
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                if (isValidMoveSet(newRow, newCol) && validMovesCache[newRow][newCol] && GameManager.getInstance().isEmptySquare(newRow, newCol)) {
                    validMoves.add(new int[]{newRow, newCol});
                }
            }
        }
        return validMoves;
    }

    // Default valid moves are 8 directions around the monster
    protected boolean isValidMoveSet(int row, int col) {
        return row >= 0 && row < validMovesCache.length && col >= 0 && col < validMovesCache[0].length;
    }

    /******************************************
     *             getter setter
     ******************************************/
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
        //=====<dead effect>=========================================
        new Thread(()->{
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(()->{
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_TARGET ,
                                GameManager.getInstance().player ,
                                this ,
                                EffectManager.getInstance().createInPlaceEffects(2) ,
                                new EffectConfig(0 , 0 , 0 , 1.25) );
            });
        }).start();

        //=============================================================


        System.out.println(this.getClass().getSimpleName() + " is dead @" + getRow() + " " + getCol());
    }
    public boolean isEndAction() {
        return endAction;
    }
}
