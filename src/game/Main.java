package game;

import javafx.application.Application;
import javafx.scene.text.Font;

import javafx.stage.Stage;
import logic.*;


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
        SceneManager.getInstance().setSummaryScene(summary.getScene());

        primaryStage.setResizable(false);
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setTitle("Dungeon Crawler");
        primaryStage.show();
    }
}
