package scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.GameManager;
import logic.SceneManager;
import logic.SoundManager;
import utils.Config;

public class SummaryScene {
    private Scene scene;
    private VBox root;

    private Text killedMonstersText;
    private Text moneyGatheredText;
    private Text moveCountText;
    private Text levelDiedOnText;

    public SummaryScene() {
        // Create root pane
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.setPadding(new Insets(50));
        root.getStylesheets().add(getClass().getResource("/CSSs/BottomLeftGUI.css").toExternalForm());
        root.setBackground(Background.fill(Color.BLACK));

        // Create text nodes for player stats
        killedMonstersText = new Text("Monsters Killed: " + GameManager.getInstance().totalKillThisRun);
        killedMonstersText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-fill:'white';");
        moneyGatheredText = new Text("Money Gathered: $" + + GameManager.getInstance().totalMoneyThisRun);
        moneyGatheredText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-fill:'white';");
        moveCountText = new Text("Move Count: " + GameManager.getInstance().totalMovesThisRun);
        moveCountText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-fill:'white';");
        levelDiedOnText = new Text("Level Died On: " + GameManager.getInstance().currentLevelReach);
        levelDiedOnText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-fill:'white';");

        // Create buttons
        Button retryButton = new Button("Retry");
        Button menuButton = new Button("To Menu");

        // Set actions for buttons
        retryButton.setOnAction(e -> {
            // Add action to retry the game
            System.out.println("Retry button clicked");
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            GameManager.getInstance().GameStart(GameManager.getInstance().player.createNewInstance());
        });

        menuButton.setOnAction(e -> {
            // Add action to return to the menu
            System.out.println("Return to Menu button clicked");
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            SceneManager.getInstance().switchSceneTo(SceneManager.getInstance().getMenuScene());
            SoundManager.getInstance().changeBackgroundMusic(Config.bgm_8_bit_nostalgia);
        });

        // Add nodes to root pane
        root.getChildren().addAll(killedMonstersText, moneyGatheredText, moveCountText, levelDiedOnText, retryButton, menuButton);

        // Create scene
        scene = new Scene(root, 1280, 720); // Set scene size as needed
    }

    public void updateText() {
        System.out.println("Monsters Killed: " + GameManager.getInstance().totalKillThisRun);
        System.out.println("Money Gathered: $" + + GameManager.getInstance().totalMoneyThisRun);
        System.out.println("Move Count: " + GameManager.getInstance().totalMovesThisRun);
        System.out.println("Level Died On: " + GameManager.getInstance().currentLevelReach);

        killedMonstersText.setText("Monsters Killed: " + GameManager.getInstance().totalKillThisRun);
        moveCountText.setText("Money Gathered: $" + + GameManager.getInstance().totalMoneyThisRun);
        levelDiedOnText.setText("Move Count: " + GameManager.getInstance().totalMovesThisRun);
        moneyGatheredText.setText("Level Died On: " + GameManager.getInstance().currentLevelReach);
    }

    public Scene getScene() {
        updateText();
        return scene;
    }
}
