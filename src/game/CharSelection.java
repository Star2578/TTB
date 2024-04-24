package game;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.SoundManager;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import utils.Config;

import java.util.ArrayList;

public class CharSelection {
    Scene scene;
    Pane rootPane;

    Button playBtn;
    Button returnBtn;

    ScrollPane scrollPane;

    static CharInfoBox charInfoBox;
    static CharCard selectedCard;

    public CharSelection(){

        rootPane = new Pane();
        rootPane.setStyle("-fx-background-color:#645050");
        rootPane.setPrefSize(1280,720);
        rootPane.getStylesheets().add(getClass().getResource("/CSSs/charSelection.css").toExternalForm());

        scene = new Scene(rootPane , 1280 , 720);


        //-------------------<scroll container>---------------------
        //will display all characters selectable
        scrollPane = new ScrollPane();
        scrollPane.setId("scrollPane");
        scrollPane.setPrefSize(780,350);
        BasePlayerPiece[] characterList = {
                new Knight(0,0,0),
                new Knight(0,0,0),
                new Knight(0,0,0),
                new Knight(0,0,0)
        };
        scrollPane.setContent(getCharContainer(characterList));
        scrollPane.setLayoutX(20);
        scrollPane.setLayoutY(150);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFocusTraversable(false);


        //----------------<character status container>------------------------
        charInfoBox = new CharInfoBox();


        //-------------------<play button>-----------------------------------------
        playBtn= new Button("PLAY");
        playBtn.getStyleClass().add("btn");
        playBtn.setPrefWidth(250);
        playBtn.setLayoutX(920);
        playBtn.setLayoutY(600);
        playBtn.setOnAction(actionEvent -> {
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            // switch bgm
            SoundManager.getInstance().changeBackgroundMusic(Config.bgm_8_bit_adventure);
            //start and switch to the game scene
            GameManager.getInstance().GameStart(new Knight(0, 0, 1));
            SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getGameScene());
        });


        //-------------------<return button>-----------------------------------------
        returnBtn= new Button("Back");
        returnBtn.setPrefWidth(70);
        returnBtn.setPrefHeight(40);
        returnBtn.setLayoutX(30);
        returnBtn.setLayoutY(20);
        returnBtn.setOnAction(actionEvent -> {
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            // switch bgm
            SoundManager.getInstance().changeBackgroundMusic(Config.bgm_8_bit_nostalgia);
            //start and switch to the MainMenu scene
            SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getMenuScene());
        });

        //-------------------<put everything on scene>-----------------------------------------

        rootPane.getChildren().addAll(
                scrollPane,
                charInfoBox,
                playBtn,
                returnBtn
        );
    }

    public GridPane getCharContainer(BasePlayerPiece ...characters){

        GridPane gridPane = new GridPane();
        gridPane.setHgap(45);
        gridPane.setVgap(35);
        gridPane.setPrefWidth(780);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setBackground(new Background(new BackgroundFill(Color.web("#3f2832"),CornerRadii.EMPTY,Insets.EMPTY)));
        gridPane.setPadding(new Insets(20,0,20,0));

        int row = Math.floorDiv(characters.length,3);
        int col = characters.length%3;
        System.out.println(row + " " + col + ", " + characters.length);

        //add row constraint
        if(characters.length<=3){
            gridPane.getRowConstraints().add(new RowConstraints(300));
        }else{
            for(int i = 0 ; i < row ; i++){
                gridPane.getRowConstraints().add(new RowConstraints(300));
            }
        }

        //add 3 col constraint
        gridPane.getColumnConstraints().add(new ColumnConstraints(200));
        gridPane.getColumnConstraints().add(new ColumnConstraints(200));
        gridPane.getColumnConstraints().add(new ColumnConstraints(200));

        //add all character card to grid
        int index = 0; //keep track position in characters Array
        int r = 0 , c = 0;
        while(index < characters.length){
            CharCard newCard = new CharCard(characters[index]);
            gridPane.add( newCard , c , r);

            if(c==2){
                c = 0;
                r++;
            }else c++;
            index++;
        }

        return gridPane;
    }

    public VBox makeCharacterCard(BasePlayerPiece piece){
        VBox card = new VBox();
        card.alignmentProperty().set(Pos.BOTTOM_CENTER);
        card.setPrefSize(200,300);
        card.setBackground(new Background(new BackgroundFill(Color.web("#e6dbc4") , new CornerRadii(5) , Insets.EMPTY)));

        StackPane charImageContainer = new StackPane();
        charImageContainer.setPrefWidth(200);
        charImageContainer.setPadding(new Insets(0,0,15,0));
        //character image
        ImageView charImage = new ImageView(ImageScaler.resample(piece.getTexture().getImage(),4));
        charImage.setPreserveRatio(true);
        charImageContainer.getChildren().addAll(charImage);

        card.getChildren().addAll(charImageContainer , new Rectangle(200 , 72 , Color.CHOCOLATE));

        //setup hover/click event
        card.setOnMouseEntered(mouseEvent -> {
            card.setStyle(
                    "-fx-scale-x:1.02; " +
                    "-fx-scale-y:1.02;"
            );
        });
        card.setOnMouseExited(mouseEvent -> {
            card.setStyle("-fx-border:none");
        });



        return card;
    }

    public Scene getScene(){
        return scene;
    }


}

//===================================================

class CharCard extends VBox{

    public BasePlayerPiece charData;

    public CharCard(BasePlayerPiece piece){

        charData = piece;

        this.alignmentProperty().set(Pos.BOTTOM_CENTER);
        this.setPrefSize(200,300);
        this.setBackground(new Background(new BackgroundFill(Color.web("#e6dbc4") , new CornerRadii(5) , Insets.EMPTY)));
        this.setCursor(Cursor.HAND);

        StackPane charImageContainer = new StackPane();
        charImageContainer.setPrefWidth(200);
        charImageContainer.setPadding(new Insets(0,0,15,0));
        //character image

        ImageView charImage = new ImageView(ImageScaler.resample(piece.getTexture().getImage(),4));
        charImage.setPreserveRatio(true);
        charImageContainer.getChildren().addAll(charImage);

        this.getChildren().addAll(charImageContainer , new Rectangle(200 , 72 , Color.CHOCOLATE));

        //----------------<setup hover/click event>----------------------
        this.setOnMouseEntered(mouseEvent -> {
            this.setStyle(
                    "-fx-scale-x:1.02; " +
                            "-fx-scale-y:1.02;"
            );
        });

        this.setOnMouseExited(mouseEvent -> {
            this.setStyle("-fx-border:none");
        });

        this.setOnMouseClicked(mouseEvent -> {
            //card will being focused on clicked, assign clicked in selectedCard
            if(CharSelection.selectedCard != this){
                if(CharSelection.selectedCard != null) CharSelection.selectedCard.setBackground(new Background(new BackgroundFill(Color.web("#e6dbc4") , new CornerRadii(5) , Insets.EMPTY)));
                this.setBackground(new Background(new BackgroundFill(Color.GRAY,new CornerRadii(5),Insets.EMPTY)));
                CharSelection.selectedCard = this;

                CharSelection.charInfoBox.changeCharInfo(CharSelection.selectedCard.charData);
            }
        });
        //---------------------------------------------------------------
    }


}

//===================================================

class CharInfoBox extends Pane{

    private Text charName;
    private Text charDesc;

    public CharInfoBox(){
        this.setPrefSize(360,550);
        this.setBackground(new Background(new BackgroundFill(Color.web("#262b44"),CornerRadii.EMPTY,Insets.EMPTY)));
        this.setLayoutX(850);
        this.setLayoutY(70);

        charName = new Text("");
        charName.setId("charNameText");

        this.getChildren().addAll(charName);
    }

    public void changeCharInfo(BasePlayerPiece piece){
        charName.setText(piece.getClass().getSimpleName());
    }


}