package game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.GameManager;
import logic.SoundManager;
import logic.ui.GUIManager;
import utils.Config;

import java.util.Objects;

public class Setting {
    public static GameManager gameManager = GameManager.getInstance();
    public static SoundManager soundManager = SoundManager.getInstance();

    public static Scene setting(Stage stage, Scene previousScene) {
        // Create UI elements for the settings scene
        Button backButton = new Button("Back");
        VBox.setMargin(backButton, new Insets(10));

        VBox settingsRoot = new VBox();
        settingsRoot.setAlignment(Pos.TOP_RIGHT);
        settingsRoot.setBackground(Background.fill(Paint.valueOf("#300c17")));
        settingsRoot.setPrefWidth(1280);
        settingsRoot.setPrefHeight(720);

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
        Label bgMusicLabel = new Label("Background Music Volume:");
        bgMusicLabel.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");
        Label sfxLabel = new Label("SFX Volume:");
        sfxLabel.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");

        VBox settingsContainer = new VBox(bgMusicLabel, bgMusicSlider, sfxLabel, sfxSlider);
        settingsContainer.setPadding(new Insets(40));
        settingsContainer.setSpacing(10);

        // Add labels and components to the settingsRoot VBox
        settingsRoot.getChildren().addAll(backButton, settingsContainer);

        // Create a new scene for settings
        Scene settingsScene = new Scene(settingsRoot);
        settingsRoot.getStylesheets().add(Setting.class.getResource("/CSSs/BottomLeftGUI.css").toExternalForm());

        // Handle action for the "Back" button to return to the main menu
        backButton.setOnAction(e -> {
            gameManager.saveSettings();
            soundManager.playSoundEffect(Config.sfx_buttonSound);
            stage.setScene(previousScene); // Return to the previous scene
        });

//        ComboBox<Music> bgMusicSelector = new ComboBox<>();
//        bgMusicSelector.getItems().addAll(
//                new Music("8 Bit Adventure", "res/BGM/8_Bit_Adventure.wav"),
//                new Music("8 Bit Nostalgia", "res/BGM/8_Bit_Nostalgia.wav")
//        );
//        bgMusicSelector.setValue(new Music("8 Bit Nostalgia", "res/BGM/8_Bit_Nostalgia.wav")); // Set default value
//
//        // Event listener for background music selector
//        bgMusicSelector.setOnAction(event -> {
//            Music selectedMusic = bgMusicSelector.getValue();
//            soundManager.changeBackgroundMusic(selectedMusic.getPath());
//        });
//
//        // Add the background music selector to the settingsContainer
//        settingsContainer.getChildren().add(bgMusicSelector);

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
            // TODO
            GameManager.getInstance().fastUse = fastUseCheckbox.isSelected();
        });

        autoEndTurnCheckbox.setOnAction(event -> {
            // Handle the auto end turn option
            // TODO
            GameManager.getInstance().autoEndTurn = autoEndTurnCheckbox.isSelected();
        });

        displayDamageNumbersCheckbox.setOnAction(event -> {
            // Handle the display damage numbers option
            // TODO
            GameManager.getInstance().displayDamageNumber = displayDamageNumbersCheckbox.isSelected();
        });

        displayActionPointOnCursorCheckbox.setOnAction(event -> {
            // Handle the display damage numbers option
            GameManager.getInstance().displayActionPointOnCursor = displayActionPointOnCursorCheckbox.isSelected();
            GUIManager.getInstance().getActionPointDisplayText().setVisible(GameManager.getInstance().displayActionPointOnCursor);
        });

        settingsRoot.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                gameManager.saveSettings();
                soundManager.playSoundEffect(Config.sfx_buttonSound);
                stage.setScene(previousScene); // Return to the previous scene
            }
        });

        // Add checkboxes to the settingsContainer
        settingsContainer.getChildren().addAll(fastUseCheckbox, autoEndTurnCheckbox, displayDamageNumbersCheckbox, displayActionPointOnCursorCheckbox);

        return settingsScene;
    }

    public static class Music {
        private final String name;
        private final String path;

        public Music(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
