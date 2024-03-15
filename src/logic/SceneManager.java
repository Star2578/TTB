package logic;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;

    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private Scene settingsScene;

    private final int screenHeight;
    private final int screenWidth;

    public SceneManager() {
        screenHeight = 1280;
        screenWidth = 720;
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public Stage getStage(){return stage;}

    public void setStage(Stage stage){this.stage=stage;}

    public Scene getGameScene() {
        return gameScene;
    }

    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public Scene getMenuScene() {
        return menuScene;
    }

    public void setMenuScene(Scene menuScene) {
        this.menuScene = menuScene;
    }

    public Scene getSettingsScene() {
        return settingsScene;
    }

    public void setSettingsScene(Scene settingsScene) {
        this.settingsScene = settingsScene;
    }
}
