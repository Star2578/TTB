package game;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import logic.SceneManager;

public class MainMenu {
    Scene scene;
    Pane rootPane;

    Button playBtn;
    Text playBtnText;

    Button settingBtn;
    Text settingBtnText;

    Text titleText;
    Rectangle titleRect1;
    Rectangle titleRect2;

    public MainMenu(){
        rootPane = new Pane();
        rootPane.setStyle("-fx-background-color:#645050");
        rootPane.setPrefSize(1280,720);


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
        playBtn= new Button();
        playBtn.setId("playBtn");
        playBtn.setStyle(
                "-fx-font-size:24;" +
                "-fx-border-style:SOLID;" +
                "-fx-border-color:BLACK;" +
                "-fx-border-width:5;" +
                "-fx-background-color:#843939");
        playBtn.setPrefWidth(250);
        playBtn.setLayoutX(515);
        playBtn.setLayoutY(360);

        playBtnText = new Text("START");
        playBtnText.setStyle("-fx-font-family:'x8y12pxTheStrongGamer';" +
                "-fx-font-size:32;");
        playBtnText.setFill(Color.web("EEEEEE"));
        playBtnText.setX(587);
        playBtnText.setY(368 + 32);
        playBtnText.setDisable(true);

        playBtn.setOnMouseEntered(mouseEvent -> buttonGetHover(playBtn,playBtnText));
        playBtn.setOnMouseExited(mouseEvent -> buttonSetDefault(playBtn , playBtnText));

        playBtn.setOnMouseClicked(mouseEvent -> {
            //will do scene event handler on this one
            buttonGetClicked(playBtn,playBtnText);
            SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getGameScene());

        });
        playBtn.setOnMouseReleased(mouseEvent -> buttonSetDefault(playBtn,playBtnText));

        playBtn.setOnAction(actionEvent -> buttonGetClicked(playBtn , playBtnText));
        //-------------------<setting button>-----------------------------------------
        settingBtn = new Button();
        settingBtn.setId("settingBtn");
        settingBtn.setStyle(
                "-fx-font-size:24;" +
                "-fx-border-style:SOLID;" +
                "-fx-border-color:BLACK;" +
                "-fx-border-width:5;" +
                "-fx-background-color:#843939");
        settingBtn.setPrefWidth(250);
        settingBtn.setLayoutX(515);
        settingBtn.setLayoutY(453);

        settingBtnText = new Text("SETTINGS");
        settingBtnText.setStyle("-fx-font-family:'x8y12pxTheStrongGamer';" +
                "-fx-font-size:32;");
        settingBtnText.setFill(Color.web("EEEEEE"));
        settingBtnText.setX(555);
        settingBtnText.setY(461 + 32);
        settingBtnText.setDisable(true);

        settingBtn.setOnMouseEntered(mouseEvent -> buttonGetHover(settingBtn,settingBtnText));
        settingBtn.setOnMouseExited(mouseEvent -> buttonSetDefault(settingBtn , settingBtnText));

        settingBtn.setOnMouseClicked(mouseEvent -> {
            //will do scene event handler on this one
            buttonGetClicked(settingBtn,settingBtnText);
        });
        settingBtn.setOnMouseReleased(mouseEvent -> buttonSetDefault(settingBtn,settingBtnText));

        settingBtn.setOnAction(actionEvent -> buttonGetClicked(settingBtn , settingBtnText));


        //-------------------<put everything on scene>-----------------------------------------
        scene = new Scene(rootPane , 1280 , 720);
        scene.getStylesheets().add(getClass().getResource("/style/mainMenuSheet.css").toExternalForm());

        rootPane.getChildren().addAll(titleRect1,
                                      titleRect2,
                                      titleText,
                                      playBtn,
                                      playBtnText,
                                      settingBtn,
                                      settingBtnText);
    }


    public Scene getScene(){
        return scene;
    }

    public void buttonSetDefault(Button b, Text t){
        b.setStyle(     "-fx-font-size:24;" +
                        "-fx-border-style:SOLID;" +
                        "-fx-border-color:BLACK;" +
                        "-fx-border-width:5;" +
                        "-fx-background-color:#843939");
        t.setFill(Color.web("EEEEEE"));
    }

    public void buttonGetHover(Button b, Text t){
        b.setStyle(
                        "-fx-font-size:24;" +
                        "-fx-border-style:SOLID;" +
                        "-fx-border-color:BLACK;" +
                        "-fx-border-width:5;" +
                        "-fx-background-color:#733333");
        t.setFill(Color.web("BBBBBB"));
    }

    public void buttonGetClicked(Button b, Text t){
        b.setStyle(
                        "-fx-font-size:24;" +
                        "-fx-border-style:SOLID;" +
                        "-fx-border-color:#461E1E;" +
                        "-fx-border-width:5;" +
                        "-fx-background-color:#512525");
        t.setFill(Color.web("737373"));
    }
}
