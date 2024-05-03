package pieces.enemies;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import pieces.wall.BaseWallPiece;
import utils.Config;

import java.util.Map;

public class Skeleton extends BaseMonsterPiece {
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private Skeleton.State currentState;
    private final double ATTACK_RANGE = 5.5; // Why it's .5? Because it's for diagonal
    private final int VISION_RANGE = 5;
    private final int ATTACK_DAMAGE = 2;

    public Skeleton() {
        super(0, 0, 1);

        maxHp = 10;
        currentHp = maxHp;

        // Initially in the Neutral/Roaming State
        currentState = Skeleton.State.NEUTRAL_ROAMING;

        //configs values for animation
        setupAnimation(Config.SkeletonPath, 0, -12, 32, 48, true);
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
        int currentRow = getRow();
        int currentCol = getCol();
        int dRow = playerPiece.getRow() - currentRow;
        int dCol = playerPiece.getCol() - currentCol;
        int directionRow = dRow;
        int directionCol = dCol;

        // Normalize the direction
        if (dRow != 0) directionRow /= Math.abs(dRow);
        if (dCol != 0) directionCol /= Math.abs(dCol);



        for (int i = 1; i <= VISION_RANGE; i++) {
            int newRow, newCol;
            int dAbsCol = Math.abs(dCol);
            int dAbsRow = Math.abs(dRow);

            if (dAbsCol >= dAbsRow) {
                int y = (int) Math.round(i * Math.tan(Math.atan2(dAbsRow, dAbsCol)));
                newRow = currentRow + directionRow * y;
                newCol = currentCol + directionCol * i;
            } else {
                int x = (int) Math.round(i * Math.tan(Math.atan2(dAbsCol, dAbsRow)));
                newRow = currentRow + directionRow * i;
                newCol = currentCol + directionCol * x;
            }

            BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];

            if (newRow < 0 || newRow >= Config.BOARD_SIZE || newCol < 0 || newCol >= Config.BOARD_SIZE ||
                piece instanceof BaseWallPiece) {
                roamRandomly();
                break;
            }

            if (piece instanceof BasePlayerPiece) {
                playerPiece.takeDamage(ATTACK_DAMAGE);
                //=========<ATTACK EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.BULLET_TO_TARGET_ENEMY ,
                                playerPiece ,
                                getRow(), getCol(),
                                EffectManager.getInstance().createInPlaceEffects(12) ,
                                new EffectConfig(-2 , -4 , -10 , 1.7) );
                //===========================================================================================

                // Create a PauseTransition with a duration of 0.45 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.45));

                // Set the action to perform after the pause
                pause.setOnFinished(event -> {
                    //=========<Blood EFFECT>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    playerPiece.getRow(), playerPiece.getCol(),
                                    EffectManager.getInstance().createInPlaceEffects(9) ,
                                    new EffectConfig(-34 , -52 , 0 , 1.5) );
                    //===========================================================================================
                });

                // Start the pause
                pause.play();

                break;
            }

            System.out.println("Attack" + " on " + newRow + " " + newCol);
        }
    }

}
