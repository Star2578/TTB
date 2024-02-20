package logic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.GameManager;
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

    private Text displayActionPoint;
    private ProgressBar hpBar;
    private ProgressBar manaBar;

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
        playerOptionsMenu.setBackground(Background.fill(Color.BLACK));

        // Create a border with a specific color and stroke width
        BorderStroke borderStroke = new BorderStroke(
                Color.WHITE,                   // Border color
                BorderStrokeStyle.SOLID,      // Border style
                CornerRadii.EMPTY,            // Border radii (none in this case)
                new BorderWidths(5)           // Border widths (adjust the thickness here)
        );
        // Set the border with the created border stroke
        playerOptionsMenu.setBorder(new Border(borderStroke));

        // Player Character Frame
        HBox playerCharacterFrame = new HBox();
        playerCharacterFrame.setPadding(new Insets(10));
        playerCharacterFrame.setSpacing(10);
        playerCharacterFrame.setAlignment(Pos.CENTER_LEFT);

        // Player Character Image
        ImageView playerCharacterImage = new ImageView(imageScaler.resample(new Image(Config.KnightPath), 2));
        playerCharacterImage.setFitWidth(80);
        playerCharacterImage.setFitHeight(80);
//        playerCharacterImage.setPreserveRatio(true);

        // HP Bar
        hpBar = new ProgressBar(1);
        hpBar.setStyle("-fx-accent: green;");
        hpBar.setPrefWidth(200);

        // Mana Bar
        manaBar = new ProgressBar(1);
        manaBar.setStyle("-fx-accent: blue;");
        manaBar.setPrefWidth(200);

        playerCharacterFrame.getChildren().addAll(playerCharacterImage, hpBar, manaBar);
        playerOptionsMenu.getChildren().add(playerCharacterFrame);

        displayActionPoint = new Text("Action Point: " + player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
        displayActionPoint.setFill(Color.WHITE);

        // Buttons for Player Options
        Button inventoryButton = new Button("Inventory");
        Button useItemButton = new Button("Use Item");
        Button useSkillsButton = new Button("Use Skills");
        Button attackButton = new Button("Attack");
        Button endTurnButton = new Button("End Turn");

        endTurnButton.setOnMouseClicked(mouseEvent -> {
            turnManager.endPlayerTurn();
        });

        // Add spacing between buttons
        VBox.setMargin(inventoryButton, new Insets(10, 0, 0, 0));
        VBox.setMargin(useItemButton, new Insets(10, 0, 0, 0));
        VBox.setMargin(attackButton, new Insets(10, 0, 0, 0));
        VBox.setMargin(useSkillsButton, new Insets(10, 0, 0, 0));
        VBox.setMargin(displayActionPoint, new Insets(10, 0, 0, 0));
        VBox.setMargin(endTurnButton, new Insets(10, 0, 0, 0));

        playerOptionsMenu.getChildren().addAll(inventoryButton, useItemButton, attackButton, useSkillsButton, displayActionPoint, endTurnButton);

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

        hpBar.setProgress(hp);
        manaBar.setProgress(mana);
    }

    private void updateActionPointDisplay() {
        displayActionPoint.setText("Action Point: " + player.getCurrentActionPoint() + "/" + player.getMaxActionPoint());
    }
}
