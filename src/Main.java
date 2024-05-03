import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import logic.*;
import logic.gameUI.GUIManager;
import scenes.CharSelectionScene;
import scenes.GameScene;
import scenes.MainMenuScene;
import scenes.SummaryScene;
import utils.Config;


public class Main extends Application {
    GameScene gameScene;
    MainMenuScene mainMenuScene;
    SummaryScene summaryScene;
    CharSelectionScene charSelectionScene;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        Font.loadFont(getClass().getResource("/font/x8y12pxTheStrongGamer.ttf").toExternalForm(),24);
        Font.loadFont(getClass().getResource("/font/x12y16pxSolidLinker.ttf").toExternalForm(),24);
        Font.loadFont(getClass().getResource("/font/x16y32pxGridGazer.ttf").toExternalForm(),24);

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.setStage(primaryStage);

        mainMenuScene = new MainMenuScene();
        charSelectionScene = new CharSelectionScene();
        gameScene = new GameScene();
        summaryScene = new SummaryScene();
        GameManager.getInstance().gameScene = gameScene;

        // Save these scene for later use
        SceneManager.getInstance().setMenuScene(mainMenuScene.getScene());
        SceneManager.getInstance().setGameScene(gameScene.getScene());
        SceneManager.getInstance().setCharSelectionScene(charSelectionScene.getScene());
        SceneManager.getInstance().setSummary(summaryScene);


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            //terminate everything when close window
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setResizable(false);
        primaryStage.setScene(mainMenuScene.getScene());
        primaryStage.setTitle("Dungeon Crawler");

        primaryStage.getScene().setFill(Color.BLACK);

        // Create a FadeTransition when entering the game
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), mainMenuScene.getScene().getRoot());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        SoundManager.getInstance().playBackgroundMusic(Config.bgm_8_bit_nostalgia); // Play background music after the fade-in

        GUIManager.getInstance().updateCursor(mainMenuScene.getScene(), Config.DefaultCursor);
        GUIManager.getInstance().updateCursor(gameScene.getScene(), Config.DefaultCursor);
        GUIManager.getInstance().updateCursor(summaryScene.getScene(), Config.DefaultCursor);

        // Show the primaryStage after configuring the fade transition
        fadeTransition.play(); // Start the fade-in animation
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> SoundManager.getInstance().stopBackgroundMusic());
    }
}
