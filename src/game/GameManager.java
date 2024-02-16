package game;

public class GameManager {
    public static GameManager instance;

    public GameManager() {

    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
}
