package logic;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private static SceneManager instance;

    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private Scene settingsScene;
    private Scene summaryScene; // Game Over

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

    public void switchSceneWithFade(Scene scene) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            stage.setScene(scene);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), stage.getScene().getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
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
    public Scene getSummaryScene() {
        return summaryScene;
    }
    public void setSummaryScene(Scene summaryScene) {
        this.summaryScene = summaryScene;
    }
}
