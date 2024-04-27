package pieces.enemies;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpawnerManager;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.wall.BaseWallPiece;
import utils.Config;

import static game.Setting.gameManager;
import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class SlimeBoss extends BaseMonsterPiece {
    private enum Phase {
        FIRST, SECOND, THIRD, DEAD
    }

    private Phase currentPhase;
    private final int ATTACK_DAMAGE_FIRST_PHASE = 5;
    private final int ATTACK_DAMAGE_SECOND_PHASE = 4;
    private final int ATTACK_DAMAGE_THIRD_PHASE = 3;
    private final double ATTACK_RANGE = 1.5;
    private final int MOVE = 2;
    private int ATK_CNT = 0;
    private final int VISION_RANGE = 10;
    private BasePiece[][] piecesPosition = GameManager.getInstance().piecesPosition;

    public SlimeBoss() {
        super(0, 0, 1);
        setMaxHealth(200); // First phase
        setCurrentHealth(getMaxHealth());

        this.currentPhase = Phase.FIRST;

        if(currentPhase == Phase.FIRST) {
            setupAnimation(Config.SlimePath4, 0, -10, 32, 32 , true);
        } else if (currentPhase == Phase.SECOND) {

        }
    }

    @Override
    public void performAction() {
        endAction = false;
        updateState();
        ATK_CNT = 0;
        switch (currentPhase) {
            case FIRST, SECOND, THIRD:
                chasePlayer();
                break;
            case DEAD:
                // Handle the slime boss death, maybe some special effects or drops
                break;
        }
    }

    @Override
    public void updateState() {
        if (currentPhase == Phase.FIRST && getCurrentHealth() <= 20) {
            splitSlime( 100, Phase.SECOND);
        } else if (currentPhase == Phase.SECOND && getCurrentHealth() <= 15) {
            splitSlime(50, Phase.THIRD);
        } else if (currentPhase == Phase.THIRD && getCurrentHealth() <= 0) {
            currentPhase = Phase.DEAD;
            onDeath();
        }
        System.out.println("Slime Boss is in " + currentPhase);
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {
        switch (currentPhase) {
            case FIRST:
                playerPiece.takeDamage(ATTACK_DAMAGE_FIRST_PHASE);
                break;
            case SECOND:
                playerPiece.takeDamage(ATTACK_DAMAGE_SECOND_PHASE);
                break;
            case THIRD:
                playerPiece.takeDamage(ATTACK_DAMAGE_THIRD_PHASE);
                break;
        }
        GUIManager.getInstance().eventLogDisplay.addLog("Slime Boss dealt " + ATTACK_DAMAGE_FIRST_PHASE);
    }

    private void splitSlime(int hp, Phase nextPhase) {
        // Remove big slime
        // test test test
        removePiece(this);
        gameManager.environmentPieces.remove(this);

        for(int i = 0; i < 2; i++) {
            int row, col;
            do {
                row = (int) (Math.random() * BOARD_SIZE);
                col = (int) (Math.random() * BOARD_SIZE);
            } while (!isValidMoveSet(row, col) || piecesPosition[row][col] != null);

            SpawnerManager spawnerManager = SpawnerManager.getInstance();

            SlimeBoss smallerSlime = new SlimeBoss();
            smallerSlime.setRow(row);
            smallerSlime.setCol(col);
            smallerSlime.setMaxHealth(hp);
            smallerSlime.setCurrentHealth(hp);
            smallerSlime.currentPhase = nextPhase;

            if (nextPhase == Phase.SECOND) {
                setupAnimation(Config.WizardAnimationPath, 0, -10, 32, 32 , true);
            }

            piecesPosition[row][col] = smallerSlime;

            smallerSlime.animationImage.setFitWidth(SQUARE_SIZE);
            smallerSlime.animationImage.setX(col * SQUARE_SIZE + smallerSlime.getOffsetX());
            smallerSlime.animationImage.setY(row * SQUARE_SIZE + smallerSlime.getOffsetY());

            GameManager.getInstance().environmentPieces.add(smallerSlime);
            GameManager.getInstance().animationPane.getChildren().add(smallerSlime.animationImage);
            spawnerManager.monsterCount++;
        }
        // Change the current phase to the next phase
        this.currentPhase = nextPhase;
    }

    private void removePiece(BasePiece toRemove) {
        int row = toRemove.getRow();
        int col = toRemove.getCol();

        // Remove the piece's ImageView from the boardPane
        gameManager.boardPane.getChildren().remove(toRemove.getTexture());

        // Remove the piece's ImageView from the animationPane
        if (toRemove instanceof BaseMonsterPiece monsterPiece)
            gameManager.animationPane.getChildren().remove(monsterPiece.animationImage);

        // Set the corresponding entry in the pieces array to null
        piecesPosition[row][col] = null;
    }

    private void chasePlayer() {
        Timeline timeline = new Timeline();
        for(int i = 0; i < MOVE; i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i), event -> {
                // Calculate the distance between the Slime and the player
                double distance = Math.sqrt(Math.pow(GameManager.getInstance().player.getRow() - getRow(), 2) + Math.pow(GameManager.getInstance().player.getCol() - getCol(), 2));

                // Get the direction towards the player
//            int dRow = Integer.compare(GameManager.getInstance().player.getRow(), getRow());
                int dCol = Integer.compare(GameManager.getInstance().player.getCol(), getCol());

                // If Boss already attacked, don't move
                if (ATK_CNT == 0) {
                    // If the player is within attack range, attempt to attack
                    if (distance <= ATTACK_RANGE) {
                        // Turn to face the player
                        changeDirection(dCol);

                        // Attack the player
                        attack(GameManager.getInstance().player);
                        ATK_CNT++;
                    } else {
                        moveTowardsPlayer();
                    }
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setCycleCount(1);
        timeline.play();

        timeline.setOnFinished(event -> endAction = true);
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
}
