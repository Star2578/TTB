package logic.ui;

import game.GameScene;
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
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.TurnManager;
import logic.handlers.AttackHandler;
import pieces.player.BasePlayerPiece;
import utils.Config;

public class GUIManager {

    public static GUIManager instance;

    private TurnManager turnManager;
    private BasePlayerPiece player;
    private ImageScaler imageScaler;
    private VBox turnOrderDisplay;
    private VBox playerOptionsMenu;
    private VBox rightSideUI;
    private Display currentDisplay;

    // Status Display
    private Text displayActionPoint;
    private VBox hpBox;
    private Text hpText;
    private ProgressBar hpBar;
    private VBox manaBox;
    private Text manaText;
    private ProgressBar manaBar;


    // Buttons
    VBox playerOptionButtonBox;
    private Button inventoryButton;
    private Button useItemButton;
    private Button useSkillsButton;
    private Button endTurnButton;
    private Button attackButton;

    // Displays for each use
    public InventoryDisplay inventoryDisplay;
    public SkillSelectDisplay skillSelectDisplay;
    public ItemSelectDisplay itemSelectDisplay;

    // ----------- UI Status -----------
    public boolean isInAttackMode = false;
    public boolean isInInventoryMode = false;
    public boolean isInUseSkillMode = false;


    public GUIManager() {
        this.turnManager = TurnManager.getInstance();
        this.player = GameManager.getInstance().player;
        this.imageScaler = new ImageScaler();

        inventoryDisplay = new InventoryDisplay();
        skillSelectDisplay = new SkillSelectDisplay();
        itemSelectDisplay = new ItemSelectDisplay();

        initializeTurnOrderDisplay();
        initializePlayerOptionsMenu();
        initializeRightSideUI();
    }

    public static GUIManager getInstance() {
        if (instance == null) {
            instance = new GUIManager();
        }
        return instance;
    }

    private void initializeTurnOrderDisplay() {
        turnOrderDisplay = new VBox();

        // Create a border with a specific color and stroke width
        BorderStroke borderStroke = new BorderStroke(
                Color.WHITE,                   // Border color
                BorderStrokeStyle.SOLID,      // Border style
                CornerRadii.EMPTY,            // Border radii (none in this case)
                new BorderWidths(5)           // Border widths (adjust the thickness here)
        );
        // Set the border with the created border stroke
        turnOrderDisplay.setBorder(new Border(borderStroke));

        turnOrderDisplay.setMinWidth(300);
        turnOrderDisplay.setMaxWidth(300);
        turnOrderDisplay.setMinHeight(300);
        turnOrderDisplay.setMaxHeight(300);
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
//       playerCharacterImage.setPreserveRatio(true);

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
        playerOptionsMenu.getChildren().addAll(playerCharacterFrame , displayActionPoint);
        VBox.setMargin(displayActionPoint, new Insets(0 , 0 , 20 ,130));
        //----------------------------------------------------------------------


        //-------------<player button section>----------------------------------
        playerOptionButtonBox = new VBox();
        playerOptionButtonBox.setSpacing(15);
        playerOptionButtonBox.setAlignment(Pos.CENTER_LEFT);
        playerOptionButtonBox.setStyle("-fx-padding:0 0 0 20");

        inventoryButton = new Button("Inventory");
        useItemButton = new Button("Use Item");
        useSkillsButton = new Button("Use Skills");
        attackButton = new Button("Attack");
        endTurnButton = new Button("End Turn");

        inventoryButton.setOnMouseClicked(mouseEvent -> switchToInventoryDisplay());
        useSkillsButton.setOnMouseClicked(mouseEvent -> {
            switchToSkillSelectDisplay();
            isInUseSkillMode = true;
        });
        useItemButton.setOnMouseClicked(mouseEvent -> switchToItemSelectDisplay());

        attackButton.setOnMouseClicked(mouseEvent -> {
            // Cancel skill selection if skill is selected
            if (GameManager.getInstance().selectedSkill != null) {
                GameManager.getInstance().gameScene.resetSelection(2);
            }

            isInAttackMode = true;
            updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
            AttackHandler.showValidAttackRange(GameManager.getInstance().player.getRow() , GameManager.getInstance().player.getCol());
        });

        endTurnButton.setOnMouseClicked(mouseEvent -> {
            turnManager.endPlayerTurn();
            GameManager.getInstance().gameScene.exitAttackMode();
            disableButton();
        });

        playerOptionButtonBox.getChildren().addAll(inventoryButton, useItemButton, attackButton, useSkillsButton, endTurnButton);
        //----------------------------------------------------------------------

        playerOptionsMenu.getChildren().addAll(playerOptionButtonBox);

        playerOptionsMenu.setMinWidth(300);
        playerOptionsMenu.setMaxWidth(300);
        playerOptionsMenu.setMinHeight(400);
        playerOptionsMenu.setMaxHeight(400);
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

        // Set the minimum width and height of the VBox
        rightSideUI.setMinWidth(300);
        rightSideUI.setMaxWidth(300);
        rightSideUI.setMinHeight(720);
        rightSideUI.setMaxHeight(720);
    }

    public void switchToInventoryDisplay() {
        // set InventoryDisplay as the current display
        setDisplay(inventoryDisplay);
    }

    public void switchToSkillSelectDisplay() {
        // set SkillSelectDisplay as the current display
        setDisplay(skillSelectDisplay);
    }

    public void switchToItemSelectDisplay() {
        // set ItemSelectDisplay as the current display
        setDisplay(itemSelectDisplay);
    }

    private void setDisplay(Display display) {
        // Clear previous display
        rightSideUI.getChildren().clear();
        // Initialize and set the new display
        this.currentDisplay = display;
        rightSideUI.getChildren().add(this.currentDisplay.getView());
    }

    public VBox getTurnOrderDisplay() {
        return turnOrderDisplay;
    }

    public VBox getPlayerOptionsMenu() {
        return playerOptionsMenu;
    }

    public VBox getRightSideUI() {
        return rightSideUI;
    }

    public void updateGUI() {
        updateActionPointDisplay();
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

    private void updateActionPointDisplay() {
        displayActionPoint.setText("Action Point: " + player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
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

    public void enableButton(){
        useItemButton.setDisable(false);
        useSkillsButton.setDisable(false);
        attackButton.setDisable(false);
        endTurnButton.setDisable(false);
    }

    public void disableButton(){
        useItemButton.setDisable(true);
        useSkillsButton.setDisable(true);
        attackButton.setDisable(true);
        endTurnButton.setDisable(true);
    }
}
