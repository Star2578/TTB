package pieces.enemies;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import logic.ui.GUIManager;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Zombie extends BaseMonsterPiece{
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private State currentState;
    private boolean[][] validMovesCache; // Cache of valid moves for the entire board
    private final double ATTACK_RANGE = 1.5; // Why it's .5? Because it's for diagonal
    private final int VISION_RANGE = 5;
    private final int ATTACK_DAMAGE = 5;
    private Random random;

    public Zombie() {
        super(0, 0, 1);
        setTextureByPath(Config.ZombiePath);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());
        currentState = State.NEUTRAL_ROAMING; // Initially in the Neutral/Roaming State
        this.validMovesCache = GameManager.getInstance().validMovesCache;
        random = new Random();


        //configs values for animation
        setupAnimation();
    }

    // Method to update the state of the Zombie based on the player's position
    @Override
    public void updateState() {
        int playerRow = GameManager.getInstance().player.getRow();
        int playerCol = GameManager.getInstance().player.getCol();

        // Calculate the distance between the Zombie and the player
        double distance = Math.sqrt(Math.pow(playerRow - getRow(), 2) + Math.pow(playerCol - getCol(), 2));

        // Check if the player is within the attack range
        if (distance <= VISION_RANGE) {
            currentState = State.AGGRESSIVE; // Transition to the Aggressive State
        } else {
            currentState = State.NEUTRAL_ROAMING; // Transition back to the Neutral/Roaming State
        }

        System.out.println("Zombie is in " + currentState);
    }

    // Method to perform actions based on the current state
    public void performAction() {
        endAction = false;
        updateState();
        switch (currentState) {
            case NEUTRAL_ROAMING:
                roamRandomly();
                break;
            case AGGRESSIVE:
                chasePlayer();
                break;
        }
    }

    private void chasePlayer() {
        // Calculate the distance between the Zombie and the player
        double distance = Math.sqrt(Math.pow(GameManager.getInstance().player.getRow() - getRow(), 2) + Math.pow(GameManager.getInstance().player.getCol() - getCol(), 2));

        // Get the direction towards the player
//        int dRow = Integer.compare(GameManager.getInstance().player.getRow(), getRow());
        int dCol = Integer.compare(GameManager.getInstance().player.getCol(), getCol());

        // If the player is within attack range, attempt to attack
        if (distance <= ATTACK_RANGE) {
            // Turn to face the player
            changeDirection(dCol);

            // Attack the player
            attack(GameManager.getInstance().player);
        } else if (distance <= VISION_RANGE) {
            // If the player is within vision range but not attack range, move towards the player
            moveTowardsPlayer();
        } else {
            // If the player is out of vision range, transition back to roaming state
            currentState = State.NEUTRAL_ROAMING;
        }
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {
        System.out.println("Attack Player at " + playerPiece.getCol() + " " + playerPiece.getRow());

        playerPiece.takeDamage(ATTACK_DAMAGE);
        GUIManager.getInstance().updateGUI();
    }

    public void moveWithTransition(int row , int col){
        spriteAnimation.changeAnimation(4,  1);

        //slowly move to target col,row
        moveTransition.setToX( (col-getCol()) * SQUARE_SIZE + offsetX);
        moveTransition.setToY( (row-getRow()) * SQUARE_SIZE + offsetY);

        moveTransition.setOnFinished(actionEvent->{
            //set image layering depend on row
            animationImage.setViewOrder(BOARD_SIZE - row);
            //move real coordinate to new col,row
            animationImage.setX(col*SQUARE_SIZE + offsetX);
            animationImage.setY(row*SQUARE_SIZE + offsetY);
            //set translateProperty back to default
            animationImage.translateXProperty().set(offsetX);
            animationImage.translateYProperty().set(offsetY);

            spriteAnimation.changeAnimation(4,  0);

            setRow(row);
            setCol(col);
            endAction = true;
        });

        moveTransition.play();
    }

    private void moveTowardsPlayer() {
        // Get the direction towards the player
        int dRow = Integer.compare(GameManager.getInstance().player.getRow(), getRow());
        int dCol = Integer.compare(GameManager.getInstance().player.getCol(), getCol());

        // Calculate the new position towards the player
        int newRow = getRow() + dRow;
        int newCol = getCol() + dCol;

        // If the new position is valid, move the Zombie there
        if (isValidMoveSet(newRow, newCol)) {
            // Determine the new direction and call changeDirection
            int newDirection = dCol == 1 ? 1 : -1; // Assuming positive direction is right
            changeDirection(newDirection);
            move(newRow, newCol);
        }
    }

    // Method to roam around randomly
    private void roamRandomly() {
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
    }

    // Method to get the list of valid moves from the cache
    private List<int[]> getValidMoves(int row, int col) {
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

    // Method to check if a position is valid on the board
    @Override
    public boolean isValidMoveSet(int row, int col) {
        return row >= 0 && row < validMovesCache.length && col >= 0 && col < validMovesCache[0].length;
    }

    protected void setupAnimation(){
        //===================<animation section>==========================================
        offsetX=0;
        offsetY=-4;
        //sprite animations for monster
        animationImage = new ImageView(new Image(Config.ZombieAnimationPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,32,32,5);
        spriteAnimation.start();

        //setup moveTranslate behaviour
        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }
}
