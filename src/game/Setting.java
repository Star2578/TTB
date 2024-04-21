package game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.GameManager;
import logic.SoundManager;
import utils.Config;

public class Setting {
    public static GameManager gameManager = GameManager.getInstance();
    public static SoundManager soundManager = SoundManager.getInstance();

    public static Scene setting(Stage stage, Scene previousScene) {
        // Create UI elements for the settings scene
        Button backButton = new Button("Back");
        backButton.getStyleClass().add("menu-button-2");
        VBox settingsRoot = new VBox();
        settingsRoot.setAlignment(Pos.TOP_RIGHT);

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
        Label sfxLabel = new Label("SFX Volume:");

        VBox settingsContainer = new VBox(bgMusicLabel, bgMusicSlider, sfxLabel, sfxSlider);
        settingsContainer.setPadding(new Insets(40));

        // Add labels and components to the settingsRoot VBox
        settingsRoot.getChildren().addAll(backButton, settingsContainer);

        // Create a new scene for settings
        Scene settingsScene = new Scene(settingsRoot);

        // Handle action for the "Back" button to return to the main menu
        backButton.setOnAction(e -> {
            gameManager.saveSettings();
            soundManager.playSoundEffect(Config.sfx_buttonSound);
            stage.setScene(previousScene); // Return to the previous scene
        });

        return settingsScene;
    }
}
