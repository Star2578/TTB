package logic;

import javafx.scene.layout.GridPane;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import utils.Config;
import utils.GUIManager;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public BasePlayerPiece player;
    public GridPane boardPane;
    public BasePiece[][] pieces = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE];
    public List<BasePiece> environmentPieces = new ArrayList<>();
    public TurnManager turnManager ;
    public GUIManager guiManager;

    public GameManager() {
        player = new Knight(0, 0);
        boardPane = new GridPane();
        turnManager = new TurnManager(player, environmentPieces);
        guiManager = new GUIManager(turnManager, player);
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public boolean isEmptySquare(int row, int col) {
        return pieces[row][col] == null;
    }

    // TODO: Implement Save & Load System in here
}
