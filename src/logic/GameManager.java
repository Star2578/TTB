package logic;

import pieces.player.BasePlayerPiece;

public class GameManager {
    public static GameManager instance;

    public BasePlayerPiece currentPlayerClass;

    public GameManager() {

    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // TODO: Implement Save & Load System in here
}
