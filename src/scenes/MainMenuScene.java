package scenes;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import logic.GameManager;
import logic.SceneManager;
import logic.SoundManager;
import utils.Config;

public class MainMenuScene {
    Scene scene;
    Pane rootPane;

    Button playBtn;
    Button settingBtn;
    Button quitBtn;

    Text titleText;
    Rectangle titleRect1;
    Rectangle titleRect2;

    public MainMenuScene(){
        rootPane = new Pane();
        rootPane.setStyle("-fx-background-color:#645050");
        rootPane.setPrefSize(1280,720);
        rootPane.getStylesheets().add(getClass().getResource("/CSSs/MainMenu.css").toExternalForm());

        scene = new Scene(rootPane , 1280 , 720);


        //-------------------<title text>-----------------------------------------
        titleText = new Text("Dungeon Crawler");
        titleText.setStyle("-fx-font-family:'x12y16pxSolidLinker';" +
                "-fx-font-size:64;");
        titleText.setY(203 + 64);
        titleText.setX(308);

        titleRect1=new Rectangle(737,107);
        titleRect1.setFill(Color.web("F0F0F0"));
        titleRect1.setStroke(Color.BLACK);
        titleRect1.setStrokeType(StrokeType.OUTSIDE);
        titleRect1.setStrokeWidth(5);
        titleRect1.setX(272);
        titleRect1.setY(182);

        titleRect2=new Rectangle(737,10);
        titleRect2.setFill(Color.web("989898"));
        titleRect2.setStroke(Color.BLACK);
        titleRect2.setStrokeType(StrokeType.OUTSIDE);
        titleRect2.setStrokeWidth(5);
        titleRect2.setX(272);
        titleRect2.setY(289);


        //-------------------<play button>-----------------------------------------
        playBtn= new Button("NEW GAME");
        playBtn.getStyleClass().add("btn");
        playBtn.setPrefWidth(250);
        playBtn.setLayoutX(515);
        playBtn.setLayoutY(360);


        playBtn.setOnAction(actionEvent -> {
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            // switch bgm
            SoundManager.getInstance().changeBackgroundMusic(Config.bgm_8_bit_adventure);
            //start and switch to the game scene
            SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getCharSelectionScene());
        });

        //-------------------<setting button>-----------------------------------------
        settingBtn = new Button("SETTINGS");
        settingBtn.getStyleClass().add("btn");
        settingBtn.setPrefWidth(250);
        settingBtn.setLayoutX(515);
        settingBtn.setLayoutY(360 + 80);


        settingBtn.setOnAction(mouseEvent -> {
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            //will do scene event handler on this one
            SceneManager.getInstance().switchSceneTo(SettingScene.setting(SceneManager.getInstance().getStage(), this.scene));
        });

        quitBtn = new Button("QUIT");
        quitBtn.getStyleClass().add("btn");
        quitBtn.setPrefWidth(250);
        quitBtn.setLayoutX(515);
        quitBtn.setLayoutY(440 + 80);

        quitBtn.setOnAction(mouseEvent -> {
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            GameManager.getInstance().saveGame();
            SoundManager.getInstance().stopBackgroundMusic();
            Platform.exit();
            System.exit(0);
        });

        //-------------------<put everything on scene>-----------------------------------------

        rootPane.getChildren().addAll(titleRect1,
                                      titleRect2,
                                      titleText,
                                      playBtn,
                                      settingBtn,
                                      quitBtn
        );
    }


    public Scene getScene(){
        return scene;
    }


}
