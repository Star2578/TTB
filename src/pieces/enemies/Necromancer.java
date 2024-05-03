package pieces.enemies;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpawnerManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import pieces.wall.BaseWallPiece;
import utils.Config;

import java.util.List;
import java.util.Map;

import static utils.Config.SQUARE_SIZE;

public class Necromancer extends BaseMonsterPiece{
    private enum State {
        NEUTRAL_ROAMING, // State when not actively chasing the player
        AGGRESSIVE // State when actively chasing the player
    }

    private Necromancer.State currentState;
    private final double ATTACK_RANGE = 3.5; // Why it's .5? Because it's for diagonal
    private final int VISION_RANGE = 3;
    private final int ATTACK_DAMAGE = 3;
    private int counter = 0;
    private final int SUMMON_ZOMBIE_EVERY = 3;

    public Necromancer() {
        super(0, 0, 1);

        maxHp = 10;
        currentHp = maxHp;

        // Initially in the Neutral/Roaming State
        currentState = Necromancer.State.NEUTRAL_ROAMING;

        //configs values for animation
        setupAnimation(Config.NecromancerPath, 0, -12, 32, 48, true);
    }

    // Method to update the state of the Necromancer based on the player's position
    @Override
    public void updateState() {
        int playerRow = GameManager.getInstance().player.getRow();
        int playerCol = GameManager.getInstance().player.getCol();

        // Calculate the distance between the Necromancer and the player
        double distance = Math.sqrt(Math.pow(playerRow - getRow(), 2) + Math.pow(playerCol - getCol(), 2));

        // Check if the player is within the attack range
        if (distance <= VISION_RANGE) {
            currentState = Necromancer.State.AGGRESSIVE; // Transition to the Aggressive State
        } else {

            currentState = Necromancer.State.NEUTRAL_ROAMING; // Transition back to the Neutral/Roaming State
        }

        System.out.println("Necromancer is in " + currentState);
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
        // Calculate the distance between the Necromancer and the player
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
            currentState = Necromancer.State.NEUTRAL_ROAMING;
        }

        endAction = true;
    }

    @Override
    protected void roamRandomly(){
        counter++;
        if (counter % SUMMON_ZOMBIE_EVERY == 0){
            positionSummonZombie();
        }else {
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

        endAction = true;
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {
//        System.out.println("Attack Player at " + playerPiece.getCol() + " " + playerPiece.getRow());
//        //=========<SKILL EFFECT>====================================================================
//        EffectManager.getInstance()
//                .renderEffect( EffectManager.TYPE.BULLET_TO_TARGET_ENEMY ,
//                        GameManager.getInstance().player ,
//                        getRow(), getCol(),
//                        EffectManager.getInstance().createInPlaceEffects(11) ,
//                        new EffectConfig(-2 , -5 , 0 , 1.3) );
//        //===========================================================================================
//
//        playerPiece.takeDamage(ATTACK_DAMAGE);
//        GUIManager.getInstance().updateGUI();
        //---------------------------------------------------------------------------------
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
                                GameManager.getInstance().player ,
                                getRow(), getCol(),
                                EffectManager.getInstance().createInPlaceEffects(11) ,
                                new EffectConfig(-2 , -5 , 0 , 1.3) );
                //===========================================================================================
                break;
            }

            System.out.println("Attack" + " on " + newRow + " " + newCol);
        }

    }

    public void summonZombie(int row, int col) {
        System.out.println("Summon zombie");


        SpawnerManager spawnerManager = SpawnerManager.getInstance();

        Zombie zombie = new Zombie();

        zombie.setRow(row);
        zombie.setCol(col);

        GameManager.getInstance().piecesPosition[row][col] = zombie;

        zombie.animationImage.setFitWidth(SQUARE_SIZE);
        zombie.animationImage.setX(col * SQUARE_SIZE + zombie.getOffsetX());
        zombie.animationImage.setY(row * SQUARE_SIZE + zombie.getOffsetX());

        GameManager.getInstance().environmentPieces.add(zombie);
        zombie.endAction = true;

        GameManager.getInstance().animationPane.getChildren().add(zombie.animationImage);

        spawnerManager.monsterCount++;
    }

    public void positionSummonZombie() {
        // Get the direction towards the player
        int dRow = Integer.compare(GameManager.getInstance().player.getRow(), getRow());
        int dCol = Integer.compare(GameManager.getInstance().player.getCol(), getCol());

        // Calculate the new position towards the player
        int newRow = getRow() + dRow;
        int newCol = getCol() + dCol;

        // If the new position is valid, summon the Zombie there
        if ((GameManager.getInstance().piecesPosition[newRow][newCol] == null) && (GameManager.getInstance().isEmptySquare(newRow, newCol))) {
            //=========<SKILL EFFECT>====================================================================
            EffectManager.getInstance()
                    .renderEffect( EffectManager.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            newRow, newCol,
                            EffectManager.getInstance().createInPlaceEffects(7) ,
                            new EffectConfig(-8 , -40 , 0 , 1.1) );
            //===========================================================================================

            // Create a PauseTransition with a duration of 0.8 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(0.8));

            // Set the action to perform after the pause
            pause.setOnFinished(event -> summonZombie(newRow, newCol));

            // Start the pause
            pause.play();
        }
    }



}
