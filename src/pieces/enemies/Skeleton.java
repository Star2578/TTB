package pieces.enemies;

import logic.GameManager;
import logic.SpawnerManager;
import logic.ui.GUIManager;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.Arrays;
import java.util.List;

public class Skeleton extends BaseMonsterPiece {
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private Skeleton.State currentState;
    private final double ATTACK_RANGE = 5.5; // Why it's .5? Because it's for diagonal
    private final int VISION_RANGE = 5;
    private final int ATTACK_DAMAGE = 5;

    public Skeleton() {
        super(0, 0, 1);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());

        // Initially in the Neutral/Roaming State
        currentState = Skeleton.State.NEUTRAL_ROAMING;

        //configs values for animation
        setupAnimation(Config.SkeletonPath, 0, -4, 32, 32);
    }

    // Method to update the state of the Skeleton based on the player's position
    @Override
    public void updateState() {
        int playerRow = GameManager.getInstance().player.getRow();
        int playerCol = GameManager.getInstance().player.getCol();

        // Calculate the distance between the Skeleton and the player
        double distance = Math.sqrt(Math.pow(playerRow - getRow(), 2) + Math.pow(playerCol - getCol(), 2));

        // Check if the player is within the attack range
        if (distance <= VISION_RANGE) {
            currentState = Skeleton.State.AGGRESSIVE; // Transition to the Aggressive State
        } else {

            currentState = Skeleton.State.NEUTRAL_ROAMING; // Transition back to the Neutral/Roaming State
        }

        System.out.println("Skeleton is in " + currentState);
    }

    // Method to perform actions based on the current state
    public void performAction() {
        endAction = false;
        updateState();
        switch (currentState) {
            case NEUTRAL_ROAMING: {
                roamRandomly();
                break;
            }
            case AGGRESSIVE:
                chasePlayer();
                break;
        }
    }

    private void chasePlayer() {
        // Calculate the distance between the Skeleton and the player
        double distance = Math.sqrt(Math.pow(GameManager.getInstance().player.getRow() - getRow(), 2) + Math.pow(GameManager.getInstance().player.getCol() - getCol(), 2));

        // Get the direction towards the player
//        int dRow = Integer.compare(GameManager.getInstance().player.getRow(), getRow());
        int dCol = Integer.compare(GameManager.getInstance().player.getCol(), getCol());

        // If the player is within attack range, attempt to attack
        if (distance <= VISION_RANGE) {
            // Turn to face the player
            changeDirection(dCol);

            // Attack the player
            attack(GameManager.getInstance().player);
        } else {
            // If the player is out of vision range, transition back to roaming state
            currentState = Skeleton.State.NEUTRAL_ROAMING;
        }

        endAction = true;
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {
        System.out.println("Attack Player at " + playerPiece.getCol() + " " + playerPiece.getRow());

        playerPiece.takeDamage(ATTACK_DAMAGE);
        GUIManager.getInstance().updateGUI();
    }

}
