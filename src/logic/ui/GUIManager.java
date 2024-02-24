package logic.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.TurnManager;
import pieces.player.BasePlayerPiece;
import utils.Config;

public class GUIManager {
    private TurnManager turnManager;
    private BasePlayerPiece player;
    private ImageScaler imageScaler;
    private VBox turnOrderDisplay;
    private VBox playerOptionsMenu;
    private VBox rightSideUI;
    private Display currentDisplay;

    private Text displayActionPoint;
    private VBox hpBox;
    private Text hpText;
    private ProgressBar hpBar;
    private VBox manaBox;
    private Text manaText;
    private ProgressBar manaBar;


    VBox playerOptionButtonBox;
    private Button inventoryButton;
    private Button useItemButton;
    private Button useSkillsButton;
    private Button endTurnButton;
    private Button attackButton;


    public GUIManager(TurnManager turnManager, BasePlayerPiece player) {
        this.turnManager = turnManager;
        this.player = player;
        this.imageScaler = new ImageScaler();
        initializeTurnOrderDisplay();
        initializePlayerOptionsMenu();
        initializeRightSideUI();
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
        playerCharacterImage.setFitWidth(80);
        playerCharacterImage.setFitHeight(80);
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
        useSkillsButton.setOnMouseClicked(mouseEvent -> switchToSkillSelectDisplay());
        useItemButton.setOnMouseClicked(mouseEvent -> switchToItemSelectDisplay());

        attackButton.setOnMouseClicked(mouseEvent -> {
            GameManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
            GameManager.getInstance().isInAttackMode = !GameManager.getInstance().isInAttackMode;
        });

        endTurnButton.setOnMouseClicked(mouseEvent -> {
            turnManager.endPlayerTurn();
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
        // Create and set InventoryDisplay as the current display
        InventoryDisplay inventoryDisplay = new InventoryDisplay();
        setDisplay(inventoryDisplay);
    }

    public void switchToSkillSelectDisplay() {
        SkillSelectDisplay skillSelectDisplay = new SkillSelectDisplay();
        setDisplay(skillSelectDisplay);
    }

    public void switchToItemSelectDisplay() {
        ItemSelectDisplay itemSelectDisplay = new ItemSelectDisplay();
        setDisplay(itemSelectDisplay);
    }

    // Implement methods to switch to other displays similarly

    private void setDisplay(Display display) {
        // Clear previous display
        rightSideUI.getChildren().clear();
        // Initialize and set the new display
        this.currentDisplay = display;
        this.currentDisplay.initialize();
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
