package scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.GameManager;
import utils.ImageScaler;
import logic.SceneManager;
import logic.SoundManager;
import logic.gameUI.GUIManager;
import pieces.players.Archer;
import pieces.players.BasePlayerPiece;
import pieces.players.Knight;
import pieces.players.Wizard;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;
import pieces.Healable;

import java.util.ArrayList;

public class CharSelectionScene {
    private Scene scene;
    private Pane rootPane;

    private Button playBtn;
    private Button returnBtn;

    private StackPane scrollPaneContainer;
    private ScrollPane scrollPane;

    private static CharInfoBox charInfoBox;
    private static CharCard selectedCard;


    public CharSelectionScene(){


        rootPane = new Pane();
        rootPane.setStyle("-fx-background-color:#645050");
        rootPane.setPrefSize(1280,720);
        rootPane.getStylesheets().add(getClass().getResource("/CSSs/charSelection.css").toExternalForm());

        scene = new Scene(rootPane , 1280 , 720);

        //change cursor on entering scene
        GUIManager.getInstance().updateCursor(scene, Config.DefaultCursor);

        //-------------------<scroll container>---------------------
        scrollPaneContainer = new StackPane();
        scrollPaneContainer.setPrefSize(785,360);
        scrollPaneContainer.setLayoutX(20);
        scrollPaneContainer.setLayoutY(150);
        scrollPaneContainer.setBackground(new Background(new BackgroundImage(new Image(Config.ui_charSelect_box),BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT,BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));

        //will display all characters selectable
        scrollPane = new ScrollPane();
        scrollPane.setId("scrollPane");
        scrollPane.setPrefSize(785,360);
        BasePlayerPiece[] characterList = {
                new Knight(0,0,1),
                new Archer(0,0,1),
                new Wizard(0,0,1),
        };
        scrollPane.setContent(getCharContainer(characterList));
//        scrollPane.setLayoutX(20);
//        scrollPane.setLayoutY(150);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFocusTraversable(false);
        scrollPane.setStyle("-fx-background:transparent; -fx-background-color:transparent");
//        scrollPane.setBackground(new Background(new BackgroundImage(new Image(Config.ui_charSelect_box),BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT,BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));
        scrollPane.setPadding(new Insets(55,0,35,0));
//        scrollPane.setBackground(new Background(new BackgroundFill(Color.BLUE,CornerRadii.EMPTY,Insets.EMPTY)));
        scrollPaneContainer.getChildren().add(scrollPane);


        //----------------<character status container>------------------------
        charInfoBox = new CharInfoBox();


        //-------------------<play button>-----------------------------------------
        playBtn= new Button("PLAY");
        playBtn.setId("playBtn");
        playBtn.setLayoutX(940);
        playBtn.setLayoutY(600);
        playBtn.setOnAction(actionEvent -> {
            if (selectedCard != null) {
                SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
                // switch bgm
                SoundManager.getInstance().changeBackgroundMusic(Config.bgm_8_bit_adventure);
                //start and switch to the game scene
                GameManager.getInstance().GameStart(selectedCard.charData.createNewInstance());
                SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getGameScene());
            }
        });

        VBox optionContainer = new VBox();
        optionContainer.setLayoutX(630);
        optionContainer.setLayoutY(520);
        optionContainer.setSpacing(5);

        //-------------------fog of war option -----------------------------
        CheckBox fogOfWarCheckBox = new CheckBox("Fog of War");
        fogOfWarCheckBox.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-text-fill:'white';");
        fogOfWarCheckBox.setOnMouseClicked(mouseEvent -> {
            GameManager.getInstance().fogOfWar = fogOfWarCheckBox.isSelected();
        });
        CheckBox moreMonsterCheckBox = new CheckBox("More Monsters");
        moreMonsterCheckBox.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-text-fill:'white';");
        moreMonsterCheckBox.setOnMouseClicked(mouseEvent -> {
            GameManager.getInstance().moreMonster = moreMonsterCheckBox.isSelected();
        });

        // add options to optionContainer
        optionContainer.getChildren().addAll(fogOfWarCheckBox, moreMonsterCheckBox);

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
                scrollPaneContainer,
                charInfoBox,
                playBtn,
                optionContainer,
                returnBtn
        );
    }

    public GridPane getCharContainer(BasePlayerPiece ...characters){

        GridPane gridPane = new GridPane();
        gridPane.setHgap(40);
        gridPane.setVgap(35);
        gridPane.setPrefWidth(780);
        gridPane.setAlignment(Pos.CENTER);
//        gridPane.setBackground(new Background(new BackgroundFill(Color.web("#3f2832"),CornerRadii.EMPTY,Insets.EMPTY)));
        gridPane.setPadding(new Insets(20,0,20,0));

        int row = Math.floorDiv(characters.length,3);
        int col = characters.length%3;
        System.out.println(row + " " + col + ", " + characters.length);

        //add row constraint
        if(characters.length<=3){
            gridPane.getRowConstraints().add(new RowConstraints(200));
        }else{
            for(int i = 0 ; i < row ; i++){
                gridPane.getRowConstraints().add(new RowConstraints(200));
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

    public Scene getScene(){
        return scene;
    }


    //=========<Inner Class>======================================

    class CharCard extends VBox{

        public BasePlayerPiece charData;

        public CharCard(BasePlayerPiece piece){

            charData = piece;

            this.alignmentProperty().set(Pos.BOTTOM_CENTER);
            this.setPrefSize(200,200);
            this.setBackground(new Background(new BackgroundFill(Color.web("#e6dbc4") , new CornerRadii(5) , Insets.EMPTY)));
            this.setCursor(Cursor.HAND);

            StackPane charImageContainer = new StackPane();
            charImageContainer.setPrefWidth(200);
            charImageContainer.setPadding(new Insets(0,0,15,0));
            //character image

            ImageView charImage = new ImageView(ImageScaler.resample(piece.getTexture().getImage(),4));
            charImage.setPreserveRatio(true);
            charImageContainer.getChildren().addAll(charImage);

            this.getChildren().add(charImageContainer);

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
                if(CharSelectionScene.selectedCard != this){
                    if(CharSelectionScene.selectedCard != null) CharSelectionScene.selectedCard.setBackground(new Background(new BackgroundFill(Color.web("#e6dbc4") , new CornerRadii(5) , Insets.EMPTY)));
                    this.setBackground(new Background(new BackgroundFill(Color.GRAY,new CornerRadii(5),Insets.EMPTY)));
                    CharSelectionScene.selectedCard = this;

                    CharSelectionScene.charInfoBox.changeCharInfo(CharSelectionScene.selectedCard.charData);
                }
            });
            //---------------------------------------------------------------
        }

    }

    class CharInfoBox extends Pane  {

        private Text charName; //display character name
        private VBox innerContainer; //contain character data (stat, desc, skill, etc.)

        private StatBox statBox;
        private SkillList skillList;

        public CharInfoBox(){

            this.setPrefSize(360,550);
            this.setBackground(new Background(new BackgroundImage(new Image(Config.ui_charInfo_box),BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));
            this.setLayoutX(850);
            this.setLayoutY(70);

            //name on top of the box
            charName = new Text();
            charName.setId("charNameText");

            //-----------<container on charInfoBox Pane>-----------------
            innerContainer = new VBox();
            innerContainer.setPrefSize(360,530);
            innerContainer.setLayoutX(10);
            innerContainer.setLayoutY(15);
            innerContainer.setPadding(new Insets(20));
            innerContainer.setSpacing(20);
            //------------------------------------------------------------


            //add statBox,skillContainer to innerContainer
            statBox = new StatBox();
            statBox.setText(999,999,999 , 999);
            skillList = new SkillList();
            innerContainer.getChildren().addAll(statBox , skillList);
            innerContainer.setVisible(false); //hide at start

            this.getChildren().addAll(innerContainer,charName);

        }

        public void changeCharInfo(BasePlayerPiece piece){
            innerContainer.setVisible(true);

            //switch to selected character info page
            charName.setText(piece.getClass().getSimpleName());
            statBox.changeInfo(piece);
            skillList.changeInfo(piece);
            skillList.displaySkill(0);
        }


    }

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
            hp.getChildren().get(0);
            hp.getChildren().get(1).setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");

            mp = new HBox();
            mp.setSpacing(10);
            mp.setPrefSize(100,30);
            mp.setAlignment(Pos.CENTER_LEFT);
            mp.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_mana_icon),2) ) );
            mp.getChildren().add(new Text("0"));
            mp.getChildren().get(1).setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");

            ap = new HBox();
            ap.setSpacing(10);
            ap.setPrefSize(100,30);
            ap.setAlignment(Pos.CENTER_LEFT);
            ap.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_stamina_icon),2) ) );
            ap.getChildren().add(new Text("0"));
            ap.getChildren().get(1).setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");

            atk = new HBox();
            atk.setSpacing(10);
            atk.setPrefSize(100,30);
            atk.setAlignment(Pos.CENTER_LEFT);
            atk.getChildren().add(new ImageView( ImageScaler.resample( new Image(Config.ui_sword_icon),2) ) );
            atk.getChildren().add(new Text("0"));
            atk.getChildren().get(1).setStyle("-fx-font-family:x8y12pxTheStrongGamer; -fx-font-size:24; -fx-fill:white;");


            this.getChildren().addAll(hp,mp,ap,atk);
        }

        public void setText(int hp, int mp, int ap , int atk){

            //set stat number
            ((Text) this.hp.getChildren().get(1)).setText(String.valueOf(hp));
            ((Text) this.mp.getChildren().get(1)).setText(String.valueOf(mp));
            ((Text) this.ap.getChildren().get(1)).setText(String.valueOf(ap));
            ((Text) this.atk.getChildren().get(1)).setText(String.valueOf(atk));
        }

        public void changeInfo(BasePlayerPiece piece){

            setText(piece.getMaxHealth(),
                    piece.getMaxMana(),
                    piece.getMaxActionPoint(),
                    piece.getAttackDamage());
        }
    }

    class SkillList extends VBox{

        private GridPane skillContainer; // display character's skill in here
        private final int SKILLBOX_WIDTH = 64;
        private final int SKILLBOX_HEIGHT = 64;

        private ArrayList<StackPane> skills = new ArrayList<>();
        private BaseSkill[] skillDatas;

        //skill desc. component
        private Pane skillDescContainer;
        private VBox skillTextBox;
        private Text skillNameText;
        private Text skillText;
        protected SkillStat skillStat;

        public SkillList(){

            Text boxLabel = new Text("Skills");
            this.setSpacing(10);
            boxLabel.setStyle("-fx-font-family:x12y16pxSolidLinker; -fx-font-size:20; -fx-fill:white;");

            skillContainer = createSkillContainer();


            skillDescContainer = new VBox();
            skillDescContainer.setPrefSize(300,200);
            skillDescContainer.setMaxSize(300,200);

            Text skillContainerLabel = new Text("Description");
            skillContainerLabel.setStyle("-fx-font-family:x12y16pxSolidLinker; -fx-font-size:16; -fx-fill:white;");
            skillContainerLabel.setLayoutX(80);
            skillContainerLabel.setLayoutY(20);


            skillText = new Text("Click on skill to get details");
            skillText.setWrappingWidth(270);
            skillText.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:white;");

            skillNameText = new Text("");
            skillNameText.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:#00fff4;");
            skillNameText.setLayoutX(0);
            skillNameText.setLayoutY(0);

            skillTextBox = new VBox();
            skillTextBox.setPrefSize(280,120);
            skillTextBox.setLayoutX(10);
            skillTextBox.setLayoutY(25);
            skillTextBox.setStyle("-fx-background-color:#8f94a8");
            skillTextBox.setPadding(new Insets(5));
            skillTextBox.getChildren().addAll(skillNameText,skillText);
            skillTextBox.setSpacing(4);
            StackPane.setAlignment(skillText,Pos.TOP_LEFT);
            StackPane.setAlignment(skillNameText,Pos.TOP_LEFT);


            skillStat = new SkillStat();
            skillStat.setLayoutX(10);
            skillStat.setLayoutY(150);

            skillDescContainer.getChildren().addAll(skillContainerLabel,skillTextBox,skillStat);

            this.getChildren().addAll(
                    boxLabel,
                    skillContainer,
                    skillDescContainer
            );
        }

        //change displayed info
        public void changeInfo(BasePlayerPiece piece){
            //set skill icon to current character's (as reference)
            skillDatas = piece.getSkills();
            //                                    we access this
            //                                         |
            //                                         V
            // skillContainer[ stackPane[ImageView skillIcon , ImageView frame] , stackPane[*,*] , stackPane[*,*] , ... ]

            for(int i = 0; i<4 ; i++){
                if(skillDatas[i] == null) {

                    ((ImageView)((StackPane) skillContainer.getChildren().get(i)).getChildren().get(0))
                            .setImage(ImageScaler.resample(new Image(Config.LockedSkillIconPath),2));

                }
                else{
                    ((ImageView)((StackPane) skillContainer.getChildren().get(i)).getChildren().get(0))
                            .setImage(ImageScaler.resample(new Image(skillDatas[i].getIcon().getImage().getUrl()),2));
                }
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
                //assign eventHandler to each box
                skillBox.setOnMouseClicked(mouseEvent -> {
                    //which index is clicked
                    int index = skillContainer.getChildren().indexOf( (StackPane)mouseEvent.getSource() );
                    displaySkill(index);
                });

                ImageView frame = new ImageView(ImageScaler.resample(new Image(Config.FramePath),2));

                skillBox.getChildren().addAll(new ImageView(),frame);
                skills.add(skillBox);
            }

            for(int col =0 ; col<4 ; col++){
                gridPane.add(skills.get(col),col,0);
            }


            return gridPane;
        }



        //clicked skill will call this to display its description
        public void displaySkill(int index){


            if(skillDatas[index] == null) return;

            //reset selection frame
            for(int i = 0 ; i < 4 ; i++){
                if(i == index){
                    ((ImageView) ((StackPane) skillContainer.getChildren().get(i)).getChildren().get(1)).setImage(ImageScaler.resample(new Image(Config.FrameSelectedPath),2));
                }else{
                    ((ImageView) ((StackPane) skillContainer.getChildren().get(i)).getChildren().get(1)).setImage(ImageScaler.resample(new Image(Config.FramePath),2));
                }
            }

            BaseSkill skill = skillDatas[index];
            skillNameText.setText(skill.getName());
            skillText.setText(skill.getDescription());
            skillStat.changeSkillStat(skill);
        }

    }

    class SkillStat extends HBox{

        protected HBox dmg = new HBox();
        protected HBox mp = new HBox();
        protected HBox ap = new HBox();

        public SkillStat(){
            this.setPrefSize(280,30);
            this.setStyle("-fx-background-color:#242424;" +
                    "-fx-border-style:solid;" +
                    "-fx-border-top-width:5px;" +
                    "-fx-border-color:gray;"
            );
            this.setSpacing(45);
            this.setPadding(new Insets(5,10,5,10));

            Text dmgText = new Text("Damage");
            dmgText.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:#e62929;");
            Text dmgValue = new Text("999");
            dmgValue.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:white;");
            dmg.getChildren().addAll(dmgText,dmgValue);
            dmg.setPrefSize(80,30);
            dmg.setSpacing(5);

            Text mpText = new Text("Mana Cost");
            mpText.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:#0c4dcf;");
            Text mpValue = new Text("999");
            mpValue.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:white;");
            mp.getChildren().addAll(mpText,mpValue);
            mp.setPrefSize(80,30);
            mp.setSpacing(5);

            Text apText = new Text("Action Cost");
            apText.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:#dbcd00;");
            Text apValue = new Text("999");
            apValue.setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:white;");
            ap.getChildren().addAll(apText,apValue);
            ap.setPrefSize(80,30);
            ap.setSpacing(5);

            VBox MPnAPbox = new VBox();
            MPnAPbox.setSpacing(8);
            MPnAPbox.getChildren().addAll(mp,ap);

            this.getChildren().addAll(dmg,MPnAPbox);

        }

        public void changeSkillStat(BaseSkill skill){

            //dmg text depend on type of skill
            if(skill instanceof Attackable){
                int atk = ((Attackable) skill).getAttack();
                ((Text) this.dmg.getChildren().get(0)).setText("Damage");
                this.dmg.getChildren().get(0).setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:#e62929;");
                ((Text) this.dmg.getChildren().get(1)).setText( atk==0?"-":String.valueOf(atk) );
            }
            else if(skill instanceof Healable){
                int heal = ((Healable) skill).getHeal();
                ((Text) this.dmg.getChildren().get(0)).setText("Heal");
                this.dmg.getChildren().get(0).setStyle("-fx-font-family:x16y32pxGridGazer; -fx-font-size:18; -fx-fill:#22c406;");
                ((Text) this.dmg.getChildren().get(1)).setText( heal==0?"-":String.valueOf(heal) );
            }
            else{
                ((Text) this.dmg.getChildren().get(0)).setText("");
                ((Text) this.dmg.getChildren().get(1)).setText("");
            }

            ((Text) this.mp.getChildren().get(1)).setText( skill.getManaCost()==0?"-":String.valueOf(skill.getManaCost()) );
            ((Text) this.ap.getChildren().get(1)).setText( skill.getActionPointCost()==0?"-":String.valueOf(skill.getActionPointCost()) );
        }
    }

}