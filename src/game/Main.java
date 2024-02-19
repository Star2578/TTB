package game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.*;
import pieces.BasePiece;
import pieces.enemies.*;
import pieces.player.*;
import pieces.wall.*;
import utils.Config;
import utils.GUIManager;

import java.util.List;

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
