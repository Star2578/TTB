package game;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import logic.*;
import logic.ui.GUIManager;
import utils.Config;


public class Main extends Application {
    GameScene gameScene;
    MainMenu mainMenu;
    Summary summary;
    CharSelection charSelection;

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
        charSelection = new CharSelection();
        gameScene = new GameScene();
        summary = new Summary();
        GameManager.getInstance().gameScene = gameScene;

        // Save these scene for later use
        SceneManager.getInstance().setMenuScene(mainMenu.getScene());
        SceneManager.getInstance().setGameScene(gameScene.getScene());
        SceneManager.getInstance().setCharSelectionScene(charSelection.getScene());
        SceneManager.getInstance().setSummary(summary);


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            //terminate everything when close window
            @Override
            public void handle(WindowEvent t) {

                //TODO: do something about SAVING PROGRESS before exit
                //TODO: write your code here

                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setResizable(false);
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setTitle("Dungeon Crawler");

        primaryStage.getScene().setFill(Color.BLACK);

        // Create a FadeTransition when entering the game
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), mainMenu.getScene().getRoot());
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        SoundManager.getInstance().playBackgroundMusic(Config.bgm_8_bit_nostalgia); // Play background music after the fade-in

        GUIManager.getInstance().updateCursor(mainMenu.getScene(), Config.DefaultCursor);
        GUIManager.getInstance().updateCursor(gameScene.getScene(), Config.DefaultCursor);
        GUIManager.getInstance().updateCursor(summary.getScene(), Config.DefaultCursor);

        // Show the primaryStage after configuring the fade transition
        //primaryStage.setScene(mainMenu.getScene());
        fadeTransition.play(); // Start the fade-in animation
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> SoundManager.getInstance().stopBackgroundMusic());
    }
}
