package logic;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import logic.effect.Effect;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import logic.gameUI.GUIManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.players.BasePlayerPiece;

import java.util.List;

public class TurnManager {
    private static TurnManager instance;
    private BasePlayerPiece player;
    private List<BasePiece> environmentPieces;
    private int currentEnvironmentPieceIndex;
    private Timeline waitTimeline;

    private Effect whoseTurnArrow;

    public boolean isPlayerTurn;
    private final double DELAY_BETWEEN_ENVIRONMENT = 0.05;

    public TurnManager() {
        initialize();
    }

    public static TurnManager getInstance() {
        if (instance == null) {
            instance = new TurnManager();
        }
        return instance;
    }

    public void initialize() {
        this.player = GameManager.getInstance().player;
        this.environmentPieces = GameManager.getInstance().environmentPieces;
        this.currentEnvironmentPieceIndex = 0;
        this.isPlayerTurn = false;
        this.whoseTurnArrow = EffectMaker.getInstance().createInPlaceEffects(42);
    }

    public void startPlayerTurn() {
        // Start the turn for the player
        this.isPlayerTurn = true;
        pointArrowTo(player); //point to piece that currently in action
        player.startTurn();
        player.setCanAct(true);
        GUIManager.getInstance().updateGUI();
        System.out.println("Player Turn Start");
    }

    public void endPlayerTurn() {
        if (!this.isPlayerTurn) return; // Can't End turn on enemies turn

        this.isPlayerTurn = false;
        // End the turn for the player
        System.out.println("Player Turn End");
        GameManager.getInstance().gameScene.resetSelectionAll();
        player.setCanAct(false);
        currentEnvironmentPieceIndex = 0;

        EffectMaker.getInstance().updateEffectTimer(); //update effect lifetime
        EffectMaker.getInstance().clearDeadEffect(); // remove timeout effect every turn

        startEnvironmentTurn();

    }

    public void startEnvironmentTurn() {

        if (!environmentPieces.isEmpty()) {
            // Start the turn for the current environment piece
            BasePiece currentPiece = environmentPieces.get(currentEnvironmentPieceIndex);
            System.out.println("Environment Turn Start for " + currentEnvironmentPieceIndex + " " + currentPiece.getClass().getSimpleName());

            if (currentPiece instanceof BaseMonsterPiece) {


                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(DELAY_BETWEEN_ENVIRONMENT), event -> {

                    ((BaseMonsterPiece) currentPiece).performAction(); // Perform action for monsters after a delay


                    pointArrowTo(currentPiece); //point to piece that currently in action

                    // waiting for the currentPiece to finished it actions
                    waitTimeline = new Timeline(new KeyFrame(Duration.millis(800), evt -> {

                        if (!((BaseMonsterPiece) currentPiece).isEndAction()) {
                            // Continue waiting
                            return;
                        }
                        // Move to the next environment piece
                        if(player.isAlive()) cycleNextEnvironment();
                        else {
                            waitTimeline.stop();
                            EffectMaker.getInstance().effectPane.getChildren().remove(whoseTurnArrow.getImageView());
                            EffectMaker.getInstance().runningEffects.remove(whoseTurnArrow);
                        }
                    }));
                    waitTimeline.setCycleCount(Timeline.INDEFINITE);
                    waitTimeline.play();
                }));

                timeline.play();
            }
        } else {
            EffectMaker.getInstance().clearDeadEffect(); // remove unused effect
            currentEnvironmentPieceIndex = 0;
            startPlayerTurn();
            GUIManager.getInstance().enableButton();
        }
    }

    private void cycleNextEnvironment() {
        // stop waitTimeline from checking
        waitTimeline.stop();

        // Move to the next environment piece
        currentEnvironmentPieceIndex++;
        if (currentEnvironmentPieceIndex >= environmentPieces.size()) {
            currentEnvironmentPieceIndex = 0;
            startPlayerTurn();
            GUIManager.getInstance().enableButton();
        } else {
            startEnvironmentTurn();
        }
    }

    private void pointArrowTo(BasePiece piece){
        if (GameManager.getInstance().fogOfWar) {
            return;
        }
        //remove arrow and create new one on other piece
        EffectMaker.getInstance().effectPane.getChildren().remove(whoseTurnArrow.getImageView());
        EffectMaker.getInstance().runningEffects.remove(whoseTurnArrow);
        whoseTurnArrow = EffectMaker.getInstance().createInPlaceEffects(42);
        EffectMaker.getInstance()
                .renderEffect(
                        EffectMaker.TYPE.ON_TARGET ,
                        piece ,
                        piece.getRow() , piece.getCol() ,
                        whoseTurnArrow ,
                        new EffectConfig(0 , -30 , 0 , 0.8)
                );
        whoseTurnArrow.bindToOwnerMovement(piece);
    }
}
