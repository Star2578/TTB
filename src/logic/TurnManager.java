package logic;

import game.GameScene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.enemies.Zombie;
import pieces.player.BasePlayerPiece;

import java.util.List;

public class TurnManager {
    public static TurnManager instance;
    private BasePlayerPiece player;
    private List<BasePiece> environmentPieces;
    private int currentEnvironmentPieceIndex;

    public boolean isPlayerTurn;
    private final double DELAY_BETWEEN_ENVIRONMENT = 0.25;

    public TurnManager() {
        this.player = GameManager.getInstance().player;
        this.environmentPieces = GameManager.getInstance().environmentPieces;
        this.currentEnvironmentPieceIndex = 0;
        this.isPlayerTurn = false;
    }

    public static TurnManager getInstance() {
        if (instance == null) {
            instance = new TurnManager();
        }
        return instance;
    }

    public void startPlayerTurn() {
        // Start the turn for the player
        this.isPlayerTurn = true;
        player.startTurn();
        player.setCanAct(true);
        GUIManager.getInstance().updateGUI();
        System.out.println("Player Turn Start");
    }

    public void endPlayerTurn() {
        if (!this.isPlayerTurn) return; // Can't End turn on enemies turn

        this.isPlayerTurn = false;
        // End the turn for the player
        player.setCanAct(false);
        System.out.println("Player Turn End");
        GameManager.getInstance().gameScene.resetSelection(0);
        GameManager.getInstance().gameScene.resetSelection(1);
        GameManager.getInstance().gameScene.resetSelection(2);
        startEnvironmentTurn();
    }

    public void startEnvironmentTurn() {
        // Start the turn for the current environment piece
        BasePiece currentPiece = environmentPieces.get(currentEnvironmentPieceIndex);
        System.out.println("Environment Turn Start for " + currentEnvironmentPieceIndex + " " + currentPiece.getClass().getSimpleName());

        if (currentPiece instanceof Zombie) {

            ((Zombie) currentPiece).updateState(player.getRow(), player.getCol());
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(DELAY_BETWEEN_ENVIRONMENT), event -> {
                ((Zombie) currentPiece).performAction(); // Perform action for monsters after a delay of 1 second
                // Move to the next environment piece
                cycleNextEnvironment();
            }));

            timeline.play();

        }
    }

    private void cycleNextEnvironment() {
        // Move to the next environment piece
        currentEnvironmentPieceIndex++;
        if (currentEnvironmentPieceIndex == environmentPieces.size()) {
            currentEnvironmentPieceIndex = 0;
            startPlayerTurn();
            GUIManager.getInstance().enableButton();
        } else {
            startEnvironmentTurn();
        }
    }
}
