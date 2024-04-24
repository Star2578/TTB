package logic.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.*;
import logic.handlers.AttackHandler;
import logic.ui.display.*;
import pieces.player.BasePlayerPiece;
import utils.Config;

public class GUIManager {

    private static GUIManager instance;

    private TurnManager turnManager;
    private BasePlayerPiece player;
    private ImageScaler imageScaler;
    private VBox playerOptionsMenu;
    private VBox rightSideUI;
    private Display currentDisplay;

    // Status Display
    private Text displayActionPoint;
    private Text displayMoney;
    private VBox hpBox;
    private Text hpText;
    private ProgressBar hpBar;
    private VBox manaBox;
    private Text manaText;
    private ProgressBar manaBar;
    private Text actionPointDisplayText;

    // Buttons
    VBox playerOptionButtonBox;
    private Button endTurnButton;
    private Button attackButton;

    // Displays for each use
    public InventoryDisplay inventoryDisplay;
    public SkillSelectDisplay skillSelectDisplay;
    public NpcDisplay npcDisplay;
    public EventLogDisplay eventLogDisplay;

    // ----------- UI Status -----------
    public boolean isInAttackMode = false;


    public GUIManager() {
        initialize();
    }

    public void initialize() {
        this.turnManager = TurnManager.getInstance();
        this.player = GameManager.getInstance().player;
        this.imageScaler = new ImageScaler();

        inventoryDisplay = new InventoryDisplay();
        skillSelectDisplay = new SkillSelectDisplay();
        eventLogDisplay = new EventLogDisplay();
        npcDisplay = new NpcDisplay();

        actionPointDisplayText = new Text(player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
        actionPointDisplayText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        actionPointDisplayText.setDisable(true);
        actionPointDisplayText.setVisible(GameManager.getInstance().displayActionPointOnCursor);

        initializePlayerOptionsMenu();
        initializeRightSideUI();
    }

    public static GUIManager getInstance() {
        if (instance == null) {
            instance = new GUIManager();
        }
        return instance;
    }

    private void initializePlayerOptionsMenu() {
        playerOptionsMenu = new VBox();
        playerOptionsMenu.setId("playerOptionMenuBox");

        //apply css to playerOptionButtonBox
        playerOptionsMenu.getStylesheets().add(getClass().getResource("/CSSs/BottomLeftGUI.css").toExternalForm());

        // Player Character Frame
        HBox playerCharacterFrame = new HBox();
        playerCharacterFrame.setPadding(new Insets(10));
        playerCharacterFrame.setSpacing(10);
        playerCharacterFrame.setAlignment(Pos.CENTER);

        // Player Character Image
        ImageView playerCharacterImage = new ImageView(imageScaler.resample(new Image(Config.KnightLargePath), 2));
        playerCharacterImage.setPreserveRatio(true);
        playerCharacterImage.setFitWidth(70);

        //-------------<player status section>----------------------------------
        // HP Bar
        hpBar = new ProgressBar(1);
        hpBar.setId("hpBar");


        hpText = new Text(Integer.toString(player.getCurrentHealth()));
        hpText.setId("hpText");

        hpBox = new VBox();
        hpBox.setAlignment(Pos.CENTER_RIGHT);
        hpBox.getChildren().addAll(hpText , hpBar);

        // Mana Bar
        manaBar = new ProgressBar(1);
        manaBar.setId("manaBar");
        manaBar.setStyle("-fx-accent: blue;");


        manaText = new Text(Integer.toString(player.getCurrentMana()));
        manaText.setId("manaText");

        manaBox = new VBox();
        manaBox.setAlignment(Pos.CENTER_RIGHT);
        manaBox.getChildren().addAll(manaText , manaBar);

        playerCharacterFrame.getChildren().addAll(playerCharacterImage, hpBox, manaBox);


        displayActionPoint = new Text("Action Point: " + player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
        displayActionPoint.setStyle(
                        "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        displayMoney = new Text("Money: " + GameManager.getInstance().playerMoney);
        displayMoney.setStyle(
                        "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        playerOptionsMenu.getChildren().addAll(playerCharacterFrame , displayActionPoint, displayMoney);
        VBox.setMargin(displayActionPoint, new Insets(0 , 0 , 0 ,130));
        VBox.setMargin(displayMoney, new Insets(0 , 0 , 0 ,130));
        //----------------------------------------------------------------------


        //-------------<player button section>----------------------------------
        playerOptionButtonBox = new VBox();
        playerOptionButtonBox.setSpacing(15);
        playerOptionButtonBox.setAlignment(Pos.BOTTOM_CENTER);
        playerOptionButtonBox.setStyle("-fx-padding:0 0 20 20");

        attackButton = new Button("Attack");
        endTurnButton = new Button("End Turn");

        attackButton.setOnMouseClicked(mouseEvent -> {
            // Cancel skill selection if skill is selected
            if (GameManager.getInstance().selectedSkill != null) {
                GameManager.getInstance().gameScene.resetSelection(2);
            }
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);

            isInAttackMode = true;
            updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
            AttackHandler.showValidAttackRange(GameManager.getInstance().player.getRow() , GameManager.getInstance().player.getCol());
        });

        endTurnButton.setOnMouseClicked(mouseEvent -> {
            turnManager.endPlayerTurn();
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            GameManager.getInstance().gameScene.exitAttackMode();
            disableButton();
        });

        HBox buttonContainer = new HBox(attackButton, endTurnButton);
        buttonContainer.setSpacing(50);
        VBox.setVgrow(playerOptionButtonBox, Priority.ALWAYS);

        playerOptionButtonBox.getChildren().addAll(buttonContainer);
        //----------------------------------------------------------------------

        playerOptionsMenu.getChildren().addAll(inventoryDisplay.getView(), skillSelectDisplay.getView(), playerOptionButtonBox);

        playerOptionsMenu.setSpacing(10);

        playerOptionsMenu.setMinWidth(300);
        playerOptionsMenu.setMaxWidth(300);
        playerOptionsMenu.setMinHeight(700);
        playerOptionsMenu.setMaxHeight(700);
    }

    private void initializeRightSideUI() {
        rightSideUI = new VBox();
        rightSideUI.setBackground(Background.fill(Color.BLACK));

        // Create a border with a specific color and stroke width
        BorderStroke borderStroke = new BorderStroke(
                Color.WHITE,                   // Border color
                BorderStrokeStyle.SOLID,      // Border style
                CornerRadii.EMPTY,            // Border radii (none in this case)
                new BorderWidths(5)           // Border widths (adjust the thickness here)
        );

        // Set the border with the created border stroke
        rightSideUI.setBorder(new Border(borderStroke));

        switchToEventLog();

        // Set the minimum width and height of the VBox
        rightSideUI.setMinWidth(300);
        rightSideUI.setMaxWidth(300);
        rightSideUI.setMinHeight(720);
        rightSideUI.setMaxHeight(720);
    }

    private void setDisplay(Display display) {
        // Clear previous display
        rightSideUI.getChildren().clear();
        // Initialize and set the new display
        this.currentDisplay = display;
        rightSideUI.getChildren().add(this.currentDisplay.getView());
    }

    public VBox getPlayerOptionsMenu() {
        return playerOptionsMenu;
    }

    public VBox getRightSideUI() {
        return rightSideUI;
    }

    public void updateGUI() {
        updateText();
        updateStatusBar();
    }

    private void updateStatusBar() {
        double hp = (double) player.getCurrentHealth() / player.getMaxHealth();
        double mana = (double) player.getCurrentMana() / player.getMaxMana();

        this.hpText.setText(Integer.toString(player.getCurrentHealth()));
        this.manaText.setText(Integer.toString(player.getCurrentMana()));
        hpBar.setProgress(hp);
        manaBar.setProgress(mana);
    }

    private void updateText() {
        displayActionPoint.setText("Action Point: " + player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
        displayMoney.setText("Money: " + GameManager.getInstance().playerMoney);
        actionPointDisplayText.setText(player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
    }

    public void updateCursor(Scene currentScene, String cursorPath) {
        Image cursorImage = new Image(cursorPath);
        currentScene.setCursor(new ImageCursor(cursorImage));
    }
    public void updateCursor(Scene currentScene, String cursorPath, double delay) {
        Cursor bufferCursor = currentScene.getCursor();

        Image cursorImage = new Image(cursorPath);
        currentScene.setCursor(new ImageCursor(cursorImage));

        // Schedule a task to restore the original cursor after the delay
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delay), event -> {
            currentScene.setCursor(bufferCursor);
        }));
        timeline.play();
    }

    public Text getActionPointDisplayText() {
        return actionPointDisplayText;
    }

    public void switchToEventLog() {
        setDisplay(eventLogDisplay);
    }
    public void switchToNpcDisplay() {
        setDisplay(npcDisplay);
    }

    public NpcDisplay getNpcDisplay() {
        return npcDisplay;
    }

    public void enableButton(){
        attackButton.setDisable(false);
        endTurnButton.setDisable(false);
        inventoryDisplay.enableFrame();
        skillSelectDisplay.enableFrame();
    }
    public void disableButton(){
        attackButton.setDisable(true);
        endTurnButton.setDisable(true);
        inventoryDisplay.disableFrame();
        skillSelectDisplay.disableFrame();
    }
    public void deselectFrame(ImageView frameView) {
        frameView.setImage(imageScaler.resample(new Image(Config.FramePath), 2));
    }
}
