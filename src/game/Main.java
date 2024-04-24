package game;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;
import utils.Config;


public class Main extends Application {
    GameScene gameScene;
    MainMenu mainMenu;
    Summary summary;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        Font.loadFont(getClass().getResource("/font/x8y12pxTheStrongGamer.ttf").toExternalForm(),24);
        Font.loadFont(getClass().getResource("/font/x12y16pxSolidLinker.ttf").toExternalForm(),24);
        Font.loadFont(getClass().getResource("/font/x16y32pxGridGazer.ttf").toExternalForm(),24);

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.setStage(primaryStage);

        mainMenu = new MainMenu();
        gameScene = new GameScene();
        summary = new Summary();
        GameManager.getInstance().gameScene = gameScene;

        SceneManager.getInstance().setGameScene(gameScene.getScene()); // Save this scene for later use
        SceneManager.getInstance().setMenuScene(mainMenu.getScene());
        SceneManager.getInstance().setSummary(summary);

        primaryStage.setResizable(false);
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setTitle("Dungeon Crawler");

        primaryStage.getScene().setFill(Color.BLACK);

        // Create a FadeTransition when entering the game
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), mainMenu.getScene().getRoot());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        SoundManager.getInstance().playBackgroundMusic(Config.bgm_8_bit_nostalgia); // Play background music after the fade-in

        // Show the primaryStage after configuring the fade transition
        primaryStage.setScene(mainMenu.getScene());
        fadeTransition.play(); // Start the fade-in animation
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> SoundManager.getInstance().stopBackgroundMusic());
    }
}
