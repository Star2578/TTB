package pieces.enemies;

import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import logic.gameUI.GUIManager;
import pieces.players.BasePlayerPiece;
import utils.Config;

import java.util.*;

public class Zombie extends BaseMonsterPiece{
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private State currentState;
    private final double ATTACK_RANGE = 1.5; // Why it's .5? Because it's for diagonal
    private final int VISION_RANGE = 5;
    private final int ATTACK_DAMAGE = 5;

    public Zombie() {
        super(0, 0, 1);

        maxHp = 10;
        currentHp = maxHp;
        // Initially in the Neutral/Roaming State
        currentState = State.NEUTRAL_ROAMING;

        //configs values for animation
        setupAnimation(Config.ZombieAnimationPath, 0, -4, 32, 32 , true);
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

        if(EffectBuffs != null) {
            if(EffectBuffs.containsKey("Stun")) {
                endAction = true;
                System.out.println("Stunned");
            }else {
                switch (currentState) {
                    case NEUTRAL_ROAMING:
                        roamRandomly();
                        break;
                    case AGGRESSIVE:
                        chasePlayer();
                        break;
                }
            }
        }

        // Check if the player has any effect
        for(Map.Entry<String, Integer> entry : EffectBuffs.entrySet()) {
            String BuffName = entry.getKey();
            int duration = EffectBuffs.get(BuffName);
            if (duration > 0) {
                duration--; // Decrement the duration
                EffectBuffs.put(BuffName, duration);
            }
            if (duration == 0) {
                EffectBuffs.remove(BuffName);
            }
            System.out.println(BuffName + " " + duration);
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

        endAction = true;
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {
        GUIManager.getInstance().eventLogDisplay.addLog("Attack Player at " + ATTACK_DAMAGE + " damage!");
        //=========<NORMAL ATTACK EFFECT>====================================================================
        EffectMaker.getInstance()
                .renderEffect( EffectMaker.TYPE.ON_SELF ,
                        GameManager.getInstance().player ,
                        playerPiece.getRow(), playerPiece.getCol(),
                        EffectMaker.getInstance().createInPlaceEffects(9) ,
                        new EffectConfig(-34 , -52 , 0 , 1.5) );
        //===========================================================================================

        playerPiece.takeDamage(ATTACK_DAMAGE);
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
