package game;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logic.*;

import utils.Config;


public class Main extends Application {
    private static final int BOARD_SIZE = Config.BOARD_SIZE;
    private static final int SQUARE_SIZE = Config.SQUARE_SIZE;
    private static final int GAME_SIZE = Config.GAME_SIZE;

    GameScene gameScene;
    MainMenu mainMenu;

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

        SceneManager.getInstance().setGameScene(gameScene.getScene()); // Save this scene for later use
        SceneManager.getInstance().setMenuScene(mainMenu.getScene());

        primaryStage.setResizable(false);
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setTitle("Dungeon Crawler");
        primaryStage.show();
    }


}
