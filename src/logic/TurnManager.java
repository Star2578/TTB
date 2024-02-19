package logic;

import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.enemies.Zombie;
import pieces.player.BasePlayerPiece;

import java.util.ArrayList;
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

    public void startPlayerTurn() {
        // Start the turn for the player
        player.startTurn();
        player.setCanAct(true);
        GameManager.getInstance().guiManager.updateGUI();
        System.out.println("Player Turn Start");
    }

    public void endPlayerTurn() {
        // End the turn for the player
        player.endTurn();
        player.setCanAct(false);
        System.out.println("Player Turn End");
        startEnvironmentTurn();
    }

    public void startEnvironmentTurn() {
        System.out.println("Environment Turn Start for " + currentEnvironmentPieceIndex);
        // Start the turn for the current environment piece
        BasePiece currentPiece = environmentPieces.get(currentEnvironmentPieceIndex);
        System.out.println(currentPiece.getClass().getSimpleName());
        if (currentPiece instanceof Zombie) {
            ((Zombie) currentPiece).updateState(player.getRow(),player.getCol());
            ((Zombie) currentPiece).performAction(); // Perform action for monsters
        }

        // Move to the next environment piece
        currentEnvironmentPieceIndex++;
        if (currentEnvironmentPieceIndex == environmentPieces.size()) {
            currentEnvironmentPieceIndex = 0;
            startPlayerTurn();
        } else {
            startEnvironmentTurn();
        }
    }

    public BasePlayerPiece getCurrentPlayer() {
        return player;
    }

    public BasePiece getCurrentEnvironmentPiece() {
        return environmentPieces.get(currentEnvironmentPieceIndex);
    }
}
