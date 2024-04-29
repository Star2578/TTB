package pieces.enemies;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpawnerManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.wall.BaseWallPiece;
import utils.Config;

import java.util.Map;

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
    private int Skill_CNT = 0;
    private final int Spilt_range = 12;
    private BasePiece[][] piecesPosition = GameManager.getInstance().piecesPosition;

    public SlimeBoss() {
        super(0, 0, 1);
        setMaxHealth(200); // First phase
        setCurrentHealth(getMaxHealth());

        this.currentPhase = Phase.FIRST;

        setupAnimation(Config.SlimePath4, 0, -10, 32, 46 , true);  }

    @Override
    public void performAction() {
        endAction = false;
        updateState();
        ATK_CNT = 0;
        switch (currentPhase) {
            case FIRST:
                if(Skill_CNT >= 4) {
                    // reset Skill_CNT
                    SplitMucilage();
                    Skill_CNT = 0;
                }else {
                    chasePlayer();
                    Skill_CNT++;
                }
                System.out.println("Skill count: " + Skill_CNT);
                break;
            case SECOND, THIRD:
                if(EffectBuffs != null) {
                    if(EffectBuffs.containsKey("Stun")) {
                        endAction = true;
                        System.out.println("Stunned");}
                }else {
                    chasePlayer();
                }
                break;
            case DEAD:
                // Handle the slime boss death, maybe some special effects or drops
                break;
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

    private void SplitMucilage() {
        for(int i = 0; i < Spilt_range; i++) {
            int row, col;
            do {
                row = (int) (Math.random() * BOARD_SIZE);
                col = (int) (Math.random() * BOARD_SIZE);
            } while (!isValidMoveSet(row, col) || piecesPosition[row][col] != null);

            SlimeMucilage slimeMucile = new SlimeMucilage();

            slimeMucile.setRow(row);
            slimeMucile.setCol(col);
            gameManager.piecesPosition[row][col] = slimeMucile;

            slimeMucile.animationImage.setFitWidth(SQUARE_SIZE);
            slimeMucile.animationImage.setX(col * SQUARE_SIZE + slimeMucile.getOffsetX());
            slimeMucile.animationImage.setY(row * SQUARE_SIZE + slimeMucile.getOffsetY());

            gameManager.environmentPieces.add(slimeMucile);
            gameManager.animationPane.getChildren().add(slimeMucile.animationImage);

            endAction = true;
        }
    }

    private void splitSlime(int hp, Phase nextPhase) {
        // Remove big slime
        if(currentPhase == Phase.FIRST || currentPhase == Phase.SECOND) {
            deadbomb();
        }
        GameManager.getInstance().gameScene.removePiece(this);
        gameManager.environmentPieces.remove(this);

        for(int i = 0; i < nextPhase.ordinal() + 1; i++) {
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

            //TODO : FIX ANIMATION PATH
            if (nextPhase == Phase.SECOND) {
                smallerSlime.setupAnimation(Config.WizardAnimationPath, 0, -10, 32, 32 , true);
            } else if (nextPhase == Phase.THIRD) {
                smallerSlime.setupAnimation(Config.SkeletonPath, 0, -10, 32, 32 , true);
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
    
    private void deadbomb() {
        BasePlayerPiece player = GameManager.getInstance().player;

        //=========<SKILL EFFECT>====================================================================
        EffectManager.getInstance()
                .renderEffect( EffectManager.TYPE.ON_SELF ,
                        GameManager.getInstance().player ,
                        getRow(), getCol(),
                        EffectManager.getInstance().createInPlaceEffects(6) ,
                        new EffectConfig(-9 , -16 , 0 , 1.1) );
        //===========================================================================================

        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = getRow() + dRow;
                int newCol = getCol() + dCol;
                if (GameManager.getInstance().piecesPosition[newRow][newCol] instanceof BaseWallPiece) {
                    System.out.println("wall obstacle");
                    continue;
                }

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF ,
                                GameManager.getInstance().player ,
                                newRow, newCol,
                                EffectManager.getInstance().createInPlaceEffects(6) ,
                                new EffectConfig(-9 , -16 , 0 , 1.1) );
                //===========================================================================================

                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalRow1 = newRow;
                int finalCol1 = newCol;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[finalRow1][finalCol1] instanceof BasePlayerPiece) {
                        player.takeDamage(10);
                        GUIManager.getInstance().updateGUI();
                    }
                });

                // Start the pause
                pause.play();

            }
        }
        
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
