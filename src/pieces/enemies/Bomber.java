package pieces.enemies;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Bomber extends BaseMonsterPiece{

    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        RUNNING_AWAY // State when it placed the bomb, it will running away from the position
    }
    private Bomber.State currentState;
    private final int VISION_RANGE = 5; // How far this monster could spot player
    private final int MOVE = 3; // How far this monster could walk per turn
    private final int BOMB_EVERY = 2;
    private int counter = 0; // Counter for when to place bomb

    public Bomber() {
        super(0, 0, 1);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());

        currentState = Bomber.State.NEUTRAL_ROAMING; // Initially in the Neutral/Roaming State

        //configs values for animation
        setupAnimation(Config.BomberAnimationPath, 0, -10, 32, 46 , true);
    }
    @Override
    public void attack(BasePlayerPiece playerPiece) {
        // empty, no need to use on this monster
    }

    @Override
    public void performAction() {
        endAction = false;
        updateState();
        switch (currentState) {
            case NEUTRAL_ROAMING:
                roamRandomly();
                break;
            case RUNNING_AWAY:
                runAwayFromPlayer();
                break;
        }
    }

    @Override
    public void updateState() {
        int playerRow = GameManager.getInstance().player.getRow();
        int playerCol = GameManager.getInstance().player.getCol();

        // Calculate the distance between the Bomber and the player
        double distance = Math.sqrt(Math.pow(playerRow - getRow(), 2) + Math.pow(playerCol - getCol(), 2));

        // Check if the player is within the vision range
        if (distance <= VISION_RANGE) {
            // Player is within vision range, change state to running away
            currentState = Bomber.State.RUNNING_AWAY;
        } else {
            // Player is not within vision range, change state to neutral roaming
            currentState = Bomber.State.NEUTRAL_ROAMING;
        }

        System.out.println("Bomber is in " + currentState);
    }

    @Override
    protected void move(int newRow, int newCol) {
        int bufferRow = getRow();
        int bufferCol = getCol();

        super.move(newRow, newCol);

        // countdown up with every move
        counter++;

        if (counter % BOMB_EVERY == 0) {
            System.out.println("Place bomb at " + bufferRow + " " + bufferCol);
            Bomb bomb = new Bomb();

            bomb.setRow(bufferRow);
            bomb.setCol(bufferCol);
            GameManager.getInstance().piecesPosition[bufferRow][bufferCol] = bomb;

            bomb.animationImage.setFitWidth(SQUARE_SIZE);
            bomb.animationImage.setX(bufferCol * SQUARE_SIZE + bomb.getOffsetX());
            bomb.animationImage.setY(bufferRow * SQUARE_SIZE + bomb.getOffsetX());

            GameManager.getInstance().environmentPieces.add(bomb); // Add the bomb to environment so it can take turn too

            GameManager.getInstance().animationPane.getChildren().add(bomb.animationImage);
        }
    }

    @Override
    protected void roamRandomly() {
        Timeline timeline = new Timeline();
        for (int i = 0; i < MOVE; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i), e -> {
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

                    // Move the bomber to the new position
                    move(newRow, newCol);
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(1);
        timeline.play();

        // finished
        timeline.setOnFinished(actionEvent -> {
            endAction = true;
        });
    }

    private void runAwayFromPlayer() {
        Timeline timeline = new Timeline();
        for (int i = 0; i < MOVE; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i), e -> {
                System.out.println("Running Away from Player!!");
                int playerRow = GameManager.getInstance().player.getRow();
                int playerCol = GameManager.getInstance().player.getCol();

                // Calculate the direction in which the bomber should run away
                int deltaRow = getRow() - playerRow;
                int deltaCol = getCol() - playerCol;

                int newRow = getRow();
                int newCol = getCol();

                // Determine the new row and column based on the direction from the player
                if (Math.abs(deltaRow) > Math.abs(deltaCol)) {
                    newRow = getRow() + (deltaRow > 0 ? 1 : -1);
                } else {
                    newCol = getCol() + (deltaCol > 0 ? 1 : -1);
                }

                // Move the bomber to the new position if it's valid
                if (isValidMoveSet(newRow, newCol) && validMovesCache[newRow][newCol] && GameManager.getInstance().isEmptySquare(newRow, newCol)) {
                    move(newRow, newCol);
                } else {
                    // If the new position is not valid, try to find a valid move
                    List<int[]> validMoves = getValidMoves(getRow(), getCol());
                    if (!validMoves.isEmpty()) {
                        // Randomly choose one of the valid moves and move to that position
                        int[] randomMove = validMoves.get(random.nextInt(validMoves.size()));
                        newRow = randomMove[0];
                        newCol = randomMove[1];
                        move(newRow, newCol);
                    } else {
                        // If there are no valid moves, the bomber cannot move and must stay in place
                        System.out.println("No valid moves for the bomber!");
                    }
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(1);
        timeline.play();

        // end action of this piece
        timeline.setOnFinished(actionEvent -> {
            endAction = true;
        });
    }
}
