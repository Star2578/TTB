package pieces.enemies;

import logic.GameManager;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Zombie extends BaseMonsterPiece{
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private State currentState;
    private boolean[][] validMovesCache; // Cache of valid moves for the entire board
    private final int ATTACK_RANGE = 1;
    private final int VISON_RANGE = 3;
    private Random random;

    public Zombie(int row, int col, boolean[][] validMovesCache) {
        super(row, col);
        setTextureByPath(Config.ZombiePath);
        currentState = State.NEUTRAL_ROAMING; // Initially in the Neutral/Roaming State
        this.validMovesCache = validMovesCache;
        random = new Random();
    }

    // Method to update the state of the Zombie based on the player's position
    @Override
    public void updateState(int playerRow, int playerCol) {
        // Calculate the distance between the Zombie and the player
        double distance = Math.sqrt(Math.pow(playerRow - getRow(), 2) + Math.pow(playerCol - getCol(), 2));

        // Check if the player is within the attack range
        if (distance <= VISON_RANGE) {
            currentState = State.AGGRESSIVE; // Transition to the Aggressive State
        } else {
            currentState = State.NEUTRAL_ROAMING; // Transition back to the Neutral/Roaming State
        }

        System.out.println("Zombie is in " + currentState);
    }

    // Method to perform actions based on the current state
    public void performAction() {
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

        // If the player is within attack range, attempt to attack
        if (distance <= ATTACK_RANGE) {
            // Attack the player
            attack(GameManager.getInstance().player);
        } else if (distance <= VISON_RANGE) {
            // If the player is within vision range but not attack range, move towards the player
            moveTowardsPlayer();
        } else {
            // If the player is out of vision range, transition back to roaming state
            currentState = State.NEUTRAL_ROAMING;
        }
    }

    private void attack(BasePlayerPiece playerPiece) {
        System.out.println("Attack Player at " + playerPiece.getCol() + " " + playerPiece.getRow());
        // TODO : Implement attack method
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
            move(randomMove[0], randomMove[1]);
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
}
