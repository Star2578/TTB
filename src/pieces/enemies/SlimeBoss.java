package pieces.enemies;

import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.ui.GUIManager;
import pieces.player.BasePlayerPiece;
import utils.Config;

public class SlimeBoss extends BaseMonsterPiece {
    private enum Phase {
        FIRST, SECOND, THIRD, DEAD
    }

    private Phase currentPhase;
    private final int ATTACK_DAMAGE_FIRST_PHASE = 5;
    private final int ATTACK_DAMAGE_SECOND_PHASE = 5;
    private final int ATTACK_DAMAGE_THIRD_PHASE = 3;
    private final double ATTACK_RANGE = 1.5;
    private final int VISION_RANGE = 10;

    public SlimeBoss() {
        super(0, 0, 1);
        setMaxHealth(200); // First phase
        setCurrentHealth(getMaxHealth());

        this.currentPhase = Phase.FIRST;

        //TODO===============
        // Set the size of the slime
        setValidMovesCacheSize(3, 3);

        // Set the image for the slime boss
//        setTexture(new ImageView(Config.SlimeBossImagePath));
        setupAnimation(Config.SlimePath, 0, -4, 32, 32 , true);
    }

    @Override
    public void performAction() {
        endAction = false;

        switch (currentPhase) {
            case FIRST:
                roamRandomly();
                break;
            case SECOND:
            case THIRD:
                chasePlayer();
                break;
            case DEAD:
                // Handle the slime boss death, maybe some special effects or drops
                break;
        }
    }

    @Override
    public void updateState() {
        if (currentPhase == Phase.FIRST && getCurrentHealth() <= 0) {
            splitSlime(2, 2, 100, Phase.SECOND);
        } else if (currentPhase == Phase.SECOND && getCurrentHealth() <= 0) {
            splitSlime(1, 1, 50, Phase.THIRD);
        } else if (currentPhase == Phase.THIRD && getCurrentHealth() <= 0) {
            currentPhase = Phase.DEAD;
            onDeath();
        }
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

    private void splitSlime(int sizeRow, int sizeCol, int hp, Phase nextPhase) {
        //TODO===============
        // Logic to split the slime into smaller pieces
        // For demonstration, we can create new slime pieces here
        // But you can adjust this part based on your game's logic

        // Creating smaller slime pieces
        for (int i = 0; i < sizeRow; i++) {
            for (int j = 0; j < sizeCol; j++) {
                SlimeBoss smallerSlime = new SlimeBoss();
                smallerSlime.setRow(getRow() + i);
                smallerSlime.setCol(getCol() + j);
                smallerSlime.setMaxHealth(hp);
                smallerSlime.setCurrentHealth(hp);
                //TODO===============
                // Add to game manager
                GameManager.getInstance().addEnemy(smallerSlime);

            }
        }

        // Change the current phase to the next phase
        this.currentPhase = nextPhase;
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
        }

        endAction = true;
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
