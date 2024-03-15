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
    private boolean[][] validMovesCache; // Cache of valid moves for the entire board
    private final int VISION_RANGE = 5; // How far this monster could spot player
    private final int MOVE = 3; // How far this monster could walk per turn
    private final int BOMB_EVERY = 2;
    private int counter = 0; // Counter for when to place bomb
    private Random random;

    public Bomber() {
        super(0, 0, 1);
        setTextureByPath(Config.BomberPath);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());
        currentState = Bomber.State.NEUTRAL_ROAMING; // Initially in the Neutral/Roaming State
        this.validMovesCache = GameManager.getInstance().validMovesCache;
        random = new Random();


        //configs values for animation
        setupAnimation();
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

            GameManager.getInstance().piecesPosition[bufferRow][bufferCol] = bomb;

            bomb.animationImage.setFitWidth(SQUARE_SIZE);
            bomb.animationImage.setX(bufferCol * SQUARE_SIZE + bomb.offsetX);
            bomb.animationImage.setY(bufferRow * SQUARE_SIZE + bomb.offsetY);

            GameManager.getInstance().environmentPieces.add(bomb); // Add the bomb to environment so it can take turn too

            GameManager.getInstance().animationPane.getChildren().add(bomb.animationImage);
        }
    }

    private void roamRandomly() {
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

    @Override
    protected boolean isValidMoveSet(int row, int col) {
        return row >= 0 && row < validMovesCache.length && col >= 0 && col < validMovesCache[0].length;
    }

    @Override
    public void moveWithTransition(int row, int col) {
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
        });

        moveTransition.play();
    }

    @Override
    protected void setupAnimation() {
        //===================<animation section>==========================================
        offsetX=0;
        offsetY=-10;
        //sprite animations for monster
        animationImage = new ImageView(new Image(Config.BomberAnimationPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,32,46,5);
        spriteAnimation.start();

        //setup moveTranslate behaviour
        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }
}
