package logic;

import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.player.BasePlayerPiece;

import java.util.List;

public class TurnManager {
    private BasePlayerPiece player;
    private List<BasePiece> environmentPieces;
    private int currentEnvironmentPieceIndex;

    public TurnManager(BasePlayerPiece player, List<BasePiece> environmentPieces) {
        this.player = player;
        this.environmentPieces = environmentPieces;
        this.currentEnvironmentPieceIndex = 0;
    }

    public void startTurn() {
        // Start the turn for the player
        player.startTurn();
    }

    public void endPlayerTurn() {
        // End the turn for the player
        player.endTurn();
    }

    public void startEnvironmentTurn() {
        // Start the turn for the current environment piece
        BasePiece currentPiece = environmentPieces.get(currentEnvironmentPieceIndex);
        if (currentPiece instanceof BaseMonsterPiece) {
            ((BaseMonsterPiece) currentPiece).performAction(); // Perform action for monsters
        }

        // Move to the next environment piece
        currentEnvironmentPieceIndex = (currentEnvironmentPieceIndex + 1) % environmentPieces.size();
    }

    public BasePlayerPiece getCurrentPlayer() {
        return player;
    }

    public BasePiece getCurrentEnvironmentPiece() {
        return environmentPieces.get(currentEnvironmentPieceIndex);
    }
}
