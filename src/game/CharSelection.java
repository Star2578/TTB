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
import pieces.player.Archer;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import pieces.player.Wizard;
import skills.BaseSkill;
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
                new Archer(0,0,0),
                new Wizard(0,0,0)
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
            GameManager.getInstance().GameStart(selectedCard.charData);
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

class CharInfoBox extends Pane  {

    private Text charName; //display character name
    private VBox innerContainer; //contain character data (stat, desc, skill, etc.)

    private StatBox statBox;
    private SkillList skillContainer;

    public CharInfoBox(){
        this.setPrefSize(360,550);
        this.setBackground(new Background(new BackgroundImage(new Image(Config.ui_charInfo_box),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));
        this.setLayoutX(850);
        this.setLayoutY(70);


        //name on top of the box
        charName = new Text("");
        charName.setId("charNameText");


        //-----------<container on charInfoBox Pane>-----------------
        innerContainer = new VBox();
        innerContainer.setPrefSize(360,530);
        innerContainer.setLayoutY(15);
        innerContainer.setPadding(new Insets(20));
        innerContainer.setSpacing(20);

        Text placeholderText = new Text("INFO");

        statBox = new StatBox();
        statBox.setText(999,999,999 , 999);

        skillContainer = new SkillList();

        placeholderText.setStyle("-fx-font-family:'x16y32pxGridGazer';-fx-font-size:64px; -fx-fill:#666666");

        innerContainer.getChildren().addAll(statBox , skillContainer);
        //------------------------------------------------------------

        this.getChildren().addAll(innerContainer,charName);


    }

    public void changeCharInfo(BasePlayerPiece piece){
        //switch to selected character info page
        charName.setText(piece.getClass().getSimpleName());

        statBox.changeInfo(piece);
        skillContainer.changeInfo(piece);
    }


}



//===================================================
//this class contain text of character stat
class StatBox extends VBox{

    private HBox hp;
    private HBox mp;
    private HBox ap;
    private HBox atk;

    public StatBox(){

        hp = new HBox();
        hp.setSpacing(10);
        hp.setPrefSize(100,30);
        hp.setAlignment(Pos.CENTER_LEFT);
        hp.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_heart_icon),2) ) );
        hp.getChildren().add(new Text("0"));
        hp.getChildren().getFirst();
        hp.getChildren().getLast().setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");

        mp = new HBox();
        mp.setSpacing(10);
        mp.setPrefSize(100,30);
        mp.setAlignment(Pos.CENTER_LEFT);
        mp.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_mana_icon),2) ) );
        mp.getChildren().add(new Text("0"));
        mp.getChildren().getLast().setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");

        ap = new HBox();
        ap.setSpacing(10);
        ap.setPrefSize(100,30);
        ap.setAlignment(Pos.CENTER_LEFT);
        ap.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_stamina_icon),2) ) );
        ap.getChildren().add(new Text("0"));
        ap.getChildren().getLast().setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");

        atk = new HBox();
        atk.setSpacing(10);
        atk.setPrefSize(100,30);
        atk.setAlignment(Pos.CENTER_LEFT);
        atk.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_sword_icon),2) ) );
        atk.getChildren().add(new Text("0"));
        atk.getChildren().getLast().setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");


        this.getChildren().addAll(hp,mp,ap,atk);
    }

    public void setText(int hp, int mp, int ap , int atk){

        //set stat number
        ((Text) this.hp.getChildren().getLast()).setText(String.valueOf(hp));
        ((Text) this.mp.getChildren().getLast()).setText(String.valueOf(mp));
        ((Text) this.ap.getChildren().getLast()).setText(String.valueOf(ap));
        ((Text) this.atk.getChildren().getLast()).setText(String.valueOf(atk));
    }

    public void changeInfo(BasePlayerPiece piece){

        setText(piece.getMaxHealth(),
                piece.getMaxMana(),
                piece.getMaxActionPoint(),
                piece.getAttackDamage());
    }
}



//===================================================
class SkillList extends VBox{

    GridPane skillContainer; // display character's skill in here
    private final int SKILLBOX_WIDTH = 64;
    private final int SKILLBOX_HEIGHT = 64;

    private ArrayList<StackPane> skills = new ArrayList<>();
    private BaseSkill[] skillDatas;

    public SkillList(){

        Text boxLabel = new Text("Skills");
        this.setSpacing(10);
        boxLabel.setStyle("-fx-font-family:x12y16pxSolidLinker; -fx-font-size:20; -fx-fill:white;");

        skillContainer = createSkillContainer();

        this.getChildren().addAll(
                boxLabel,
                skillContainer
        );
    }

    //change displayed info
    public void changeInfo(BasePlayerPiece piece){
        //set skill icon to current character's (as reference)
        skillDatas = piece.getSkills();

        for(int i = 0; i<piece.getSkills().length ; i++){
            if(skillDatas[i] == null) break;

            //                                    we access this
            //                                         |
            //                                         V
            // skillContainer[ stackPane[ImageView skillIcon , ImageView frame] , stackPane[*,*] , stackPane[*,*] , ... ]
            ((ImageView)((StackPane) skillContainer.getChildren().get(i)).getChildren().getFirst())
                    .setImage(ImageScaler.resample(new Image(skillDatas[i].getIcon().getImage().getUrl()),2));
        }
    }

    //make initial skill container with frame image
    public GridPane createSkillContainer(){

        GridPane gridPane = new GridPane();

        //set container item's size
        gridPane.getColumnConstraints()
                .addAll(new ColumnConstraints(SKILLBOX_WIDTH),
                        new ColumnConstraints(SKILLBOX_WIDTH),
                        new ColumnConstraints(SKILLBOX_WIDTH),
                        new ColumnConstraints(SKILLBOX_WIDTH));
        gridPane.getRowConstraints().add(new RowConstraints(SKILLBOX_HEIGHT));
        //set container gap
        gridPane.setHgap(15);

        //add skill item to container
        for(int i = 0; i < 4 ; i++){
            StackPane skillBox = new StackPane();
            skillBox.setPrefSize(SKILLBOX_WIDTH,SKILLBOX_HEIGHT);

            ImageView frame = new ImageView(ImageScaler.resample(new Image(Config.FramePath),2));
            skillBox.getChildren().addAll(new ImageView(),frame);

            skills.add(skillBox);
        }

        for(int col =0 ; col<4 ; col++){
            gridPane.add(skills.get(col),col,0);
        }


        return gridPane;
    }
}