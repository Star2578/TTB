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

public class Tiny extends BaseMonsterPiece{
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private State currentState;
    private final double ATTACK_RANGE = 1.5; // Why it's .5? Because it's for diagonal
    private final int VISION_RANGE = 3;
    private final int ATTACK_DAMAGE = 3;

    public Tiny() {
        super(0, 0, 1);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());

        currentState = State.NEUTRAL_ROAMING; // Initially in the Neutral/Roaming State

        //configs values for animation
        setupAnimation(Config.TinyAnimationPath, 0, -4, 32, 32 , true);
    }

    // Method to update the state of the Tiny Zombie based on the player's position
    @Override
    public void updateState() {
        int playerRow = GameManager.getInstance().player.getRow();
        int playerCol = GameManager.getInstance().player.getCol();

        // Calculate the distance between the Tiny Zombie and the player
        double distance = Math.sqrt(Math.pow(playerRow - getRow(), 2) + Math.pow(playerCol - getCol(), 2));

        // Check if the player is within the attack range
        if (distance <= VISION_RANGE) {
            currentState = State.AGGRESSIVE; // Transition to the Aggressive State
        } else {
            currentState = State.NEUTRAL_ROAMING; // Transition back to the Neutral/Roaming State
        }

        System.out.println("Tiny is in " + currentState);
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
        // Calculate the distance between the Tiny Zombie and the player
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

        endAction = true;
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {
        System.out.println("Attack Player at " + playerPiece.getCol() + " " + playerPiece.getRow());

        playerPiece.takeDamage(ATTACK_DAMAGE);
        GUIManager.getInstance().updateGUI();
    }

    private void moveTowardsPlayer() {
        // Get the direction towards the player
        int dRow = Integer.compare(GameManager.getInstance().player.getRow(), getRow());
        int dCol = Integer.compare(GameManager.getInstance().player.getCol(), getCol());

        // Calculate the new position towards the player
        int newRow = getRow() + dRow;
        int newCol = getCol() + dCol;

        // If the new position is valid, move the Tiny Zombie there
        if (isValidMoveSet(newRow, newCol)) {
            // Determine the new direction and call changeDirection
            int newDirection = dCol == 1 ? 1 : -1; // Assuming positive direction is right
            changeDirection(newDirection);
            move(newRow, newCol);
        }
    }
}
