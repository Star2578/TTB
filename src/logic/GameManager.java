package logic;

import javafx.scene.layout.GridPane;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import utils.Config;

public class GameManager {
    private static GameManager instance;

    public BasePlayerPiece player;
    public GridPane boardPane;
    public BasePiece[][] pieces = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE];

    public GameManager() {
        player = new Knight(0, 0);
        boardPane = new GridPane();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // TODO: Implement Save & Load System in here
}
