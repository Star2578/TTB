package logic;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import pieces.BasePiece;
import pieces.enemies.Zombie;
import pieces.player.BasePlayerPiece;

import java.util.List;

public class TurnManager {
    private BasePlayerPiece player;
    private List<BasePiece> environmentPieces;
    private int currentEnvironmentPieceIndex;

    public boolean isPlayerTurn;
    private final double DELAY_BETWEEN_ENVIRONMENT = 0.25;

    public TurnManager(BasePlayerPiece player, List<BasePiece> environmentPieces) {
        this.player = player;
        this.environmentPieces = environmentPieces;
        this.currentEnvironmentPieceIndex = 0;
        this.isPlayerTurn = false;
    }

    public void startPlayerTurn() {
        // Start the turn for the player
        this.isPlayerTurn = true;
        player.startTurn();
        player.setCanAct(true);
        GameManager.getInstance().guiManager.updateGUI();
        System.out.println("Player Turn Start");
    }

    public void endPlayerTurn() {
        if (!this.isPlayerTurn) return; // Can't End turn on enemies turn

        this.isPlayerTurn = false;
        // End the turn for the player
        player.endTurn();
        player.setCanAct(false);
        System.out.println("Player Turn End");
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
                currentEnvironmentPieceIndex++;
                if (currentEnvironmentPieceIndex == environmentPieces.size()) {
                    currentEnvironmentPieceIndex = 0;
                    startPlayerTurn();
                    GameManager.getInstance().guiManager.enableEndTurnButton();
                } else {
                    startEnvironmentTurn();
                }
            }));
            timeline.play();
        }
    }
}
