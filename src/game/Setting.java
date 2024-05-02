package game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameManager;
import logic.SoundManager;
import logic.ui.GUIManager;
import utils.Config;

import java.util.Objects;
import java.util.Random;

public class Setting {
    public static GameManager gameManager = GameManager.getInstance();
    public static SoundManager soundManager = SoundManager.getInstance();

    public static Scene setting(Stage stage, Scene previousScene) {
        // Create UI elements for the settings scene
        Button backButton = new Button("Back");
        VBox.setMargin(backButton, new Insets(10));

        ScrollPane settingsRoot = new ScrollPane();
        settingsRoot.setMaxHeight(720);
        settingsRoot.setMaxWidth(1280);
        settingsRoot.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        settingsRoot.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        settingsRoot.setStyle("-fx-background-color:transparent;");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_RIGHT);
        vBox.setBackground(Background.fill(Paint.valueOf("#300c17")));
        vBox.setPrefWidth(1280);
        vBox.setPrefHeight(720);

        // Volume sliders for background music and SFX
        Slider bgMusicSlider = new Slider();
        Slider sfxSlider = new Slider();

        System.out.println("Set bg:" + soundManager.getBackgroundMusicSlider());
        System.out.println("Set sfx:" + soundManager.getSoundEffectSlider());
        bgMusicSlider.setValue(soundManager.getBackgroundMusicSlider());
        sfxSlider.setValue(soundManager.getSoundEffectSlider());

        // Event listener for background music volume slider
        bgMusicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Map the slider value to the decibel range
            float decibelValue = soundManager.mapToDecibelRange(newValue.floatValue());

            // Adjust the background music volume in real-time
            soundManager.setBackgroundMusicVolume(decibelValue);
            soundManager.setBackgroundMusicSlider(newValue.floatValue());
            soundManager.adjustBackgroundMusicVolume(soundManager.getBackgroundMusicVolume());
            System.out.println("new bg value(" + newValue + ", " + decibelValue + " dcb)");
        });

        // Event listener for SFX volume slider
        sfxSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Map the slider value to the decibel range
            float decibelValue = soundManager.mapToDecibelRange(newValue.floatValue());

            // Adjust the sound effect volume in real-time
            soundManager.setSoundEffectVolume(decibelValue);
            soundManager.setSoundEffectSlider(newValue.floatValue());
            System.out.println("new sfx value(" + newValue + ", " + decibelValue + " dcb)");
        });

        // Text labels for sliders and combo box
        Label bgMusicLabel = createLabel("Background Music Volume:");
        Label sfxLabel = createLabel("SFX Volume:");

        VBox settingsContainer = new VBox(bgMusicLabel, bgMusicSlider, sfxLabel, sfxSlider);
        settingsContainer.setPadding(new Insets(40));
        settingsContainer.setSpacing(10);

        // Add labels and components to the VBox
        vBox.getChildren().addAll(backButton, settingsContainer);

        settingsRoot.setContent(vBox);

        // Create a new scene for settings
        Scene settingsScene = new Scene(settingsRoot);
        vBox.getStylesheets().add(Setting.class.getResource("/CSSs/BottomLeftGUI.css").toExternalForm());

        // Handle action for the "Back" button to return to the main menu
        backButton.setOnAction(e -> {
            gameManager.saveSettings();
            soundManager.playSoundEffect(Config.sfx_buttonSound);
            stage.setScene(previousScene); // Return to the previous scene
        });

        CheckBox fastUseCheckbox = new CheckBox("Fast Use on Self");
        fastUseCheckbox.setStyle(
                        "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-text-fill:'white';");
        fastUseCheckbox.setSelected(GameManager.getInstance().fastUse);
        CheckBox autoEndTurnCheckbox = new CheckBox("Auto End Turn");
        autoEndTurnCheckbox.setStyle(
                        "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-text-fill:'white';");
        autoEndTurnCheckbox.setSelected(GameManager.getInstance().autoEndTurn);
        CheckBox displayDamageNumbersCheckbox = new CheckBox("Display Damage Numbers");
        displayDamageNumbersCheckbox.setStyle(
                        "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-text-fill:'white';");
        displayDamageNumbersCheckbox.setSelected(GameManager.getInstance().displayDamageNumber);
        CheckBox displayActionPointOnCursorCheckbox = new CheckBox("Display Action Point on Cursor");
        displayActionPointOnCursorCheckbox.setStyle(
                        "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-text-fill:'white';");
        displayActionPointOnCursorCheckbox.setSelected(GameManager.getInstance().displayActionPointOnCursor);

        // Event listeners for option checkboxes
        fastUseCheckbox.setOnAction(event -> {
            // Handle the fast use on self option
            GameManager.getInstance().fastUse = fastUseCheckbox.isSelected();
        });

        autoEndTurnCheckbox.setOnAction(event -> {
            // Handle the auto end turn option
            GameManager.getInstance().autoEndTurn = autoEndTurnCheckbox.isSelected();
        });

        displayDamageNumbersCheckbox.setOnAction(event -> {
            // Handle the display damage numbers option
            GameManager.getInstance().displayDamageNumber = displayDamageNumbersCheckbox.isSelected();
        });

        displayActionPointOnCursorCheckbox.setOnAction(event -> {
            // Handle the display damage numbers option
            GameManager.getInstance().displayActionPointOnCursor = displayActionPointOnCursorCheckbox.isSelected();
            GUIManager.getInstance().getActionPointDisplayText().setVisible(GameManager.getInstance().displayActionPointOnCursor);
        });

        vBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                gameManager.saveSettings();
                soundManager.playSoundEffect(Config.sfx_buttonSound);
                stage.setScene(previousScene); // Return to the previous scene
            }
        });

        // Add checkboxes to the settingsContainer
        settingsContainer.getChildren().addAll(fastUseCheckbox, autoEndTurnCheckbox, displayDamageNumbersCheckbox, displayActionPointOnCursorCheckbox);

        // How to play section
        VBox howToPlayContainer = createHowToPlayContainer();
        howToPlayContainer.setPadding(new Insets(20));

        howToPlayContainer.setMaxWidth(1000);
        howToPlayContainer.setBackground(Background.fill(Paint.valueOf("#000000")));
        howToPlayContainer.setBorder(Border.stroke(Paint.valueOf("#FFFFFF")));
        howToPlayContainer.setPadding(new Insets(20));
        howToPlayContainer.setSpacing(10);
        howToPlayContainer.setAlignment(Pos.CENTER);
        howToPlayContainer.setTranslateX(-140);

        vBox.getChildren().add(howToPlayContainer);

        GUIManager.getInstance().updateCursor(settingsScene, Config.DefaultCursor);

        return settingsScene;
    }

    private static Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("x16y32pxGridGazer", 16));
        label.setTextFill(Color.WHITE);
        return label;
    }

    // Helper method to create the How to Play container
    private static VBox createHowToPlayContainer() {
        Label howToPlayLabel = new Label("How to Play");
        howToPlayLabel.setFont(Font.font("x16y32pxGridGazer", 28));
        howToPlayLabel.setTextFill(Color.WHITE);
        addChangingDropShadow(howToPlayLabel);

        // Text for How to Play instructions
        String movementText = "Click on your character, then click where you want to move, or use WASD/Arrow keys.";
        String actionsText = "View and select character actions on the left sidebar. This includes health, mana, action points (like stamina), money, item selection slots, and skill selection slots.";
        String combatText = "Click 'Attack' or press V to enter attack mode. Click the target to attack. Press spacebar or click 'End Turn' to end your turn.";
        String objectiveText = "Explore the dungeon, defeating monsters for money drops. Every 5 levels, encounter a dealer to spend money. The goal is to progress as far as possible. Money is lost upon game over.";

        VBox howToPlayContainer = new VBox(
                howToPlayLabel,
                createHowToPlaySubtitle("Movement:", movementText),
                createHowToPlaySubtitle("Actions:", actionsText),
                createHowToPlaySubtitle("Combat:", combatText),
                createHowToPlaySubtitle("Objective:", objectiveText)
        );
        howToPlayContainer.setBackground(Background.fill(Paint.valueOf("#000000")));
        howToPlayContainer.setBorder(Border.stroke(Paint.valueOf("#FFFFFF")));
        howToPlayContainer.setSpacing(10);
        howToPlayContainer.setAlignment(Pos.CENTER_LEFT);
        return howToPlayContainer;
    }

    // Helper method to create How to Play subtitles
    private static VBox createHowToPlaySubtitle(String title, String text) {
        Label subtitleLabel = new Label(title);
        subtitleLabel.setFont(Font.font("x16y32pxGridGazer", 20));
        subtitleLabel.setTextFill(Color.web("#FFD700")); // Gold color

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("x16y32pxGridGazer", 18));
        textLabel.setTextFill(Color.WHITE);
        textLabel.setWrapText(true);

        return new VBox(subtitleLabel, textLabel);
    }

    private static void addChangingDropShadow(Label text) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(0);
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        text.setEffect(dropShadow);

        // Create a Timeline to change the drop shadow color randomly
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.25), event -> {
                    dropShadow.setColor(generateRandomColor());
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private static Color generateRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
