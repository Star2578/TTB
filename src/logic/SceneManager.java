package logic;

import scenes.SummaryScene;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static SceneManager instance;

    private Stage stage;
    private Scene menuScene;
    private Scene charSelectionScene;
    private Scene gameScene;
    private SummaryScene summaryScene;

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

    public void switchSceneTo(Scene scene) {
        stage.setScene(scene);
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
    public Scene getSummaryScene() {
        return summaryScene.getScene();
    }
    public void setSummary(SummaryScene summaryScene) {
        this.summaryScene = summaryScene;
    }
    public Scene getCharSelectionScene() {
        return charSelectionScene;
    }
    public void setCharSelectionScene(Scene charSelectionScene) {
        this.charSelectionScene = charSelectionScene;
    }
}
