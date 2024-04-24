package logic;

import game.Summary;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Config;

public class SceneManager {

    private static SceneManager instance;

    public GameManager gameManager = GameManager.getInstance();
    public SoundManager soundManager = SoundManager.getInstance();

    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private Summary summary;

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
    public Scene getSummaryScene() {
        return summary.getScene();
    }
    public void setSummary(Summary summary) {
        this.summary = summary;
    }
}
