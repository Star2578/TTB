package game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.GameManager;
import pieces.player.Knight;

public class Summary {
    private Scene scene;
    private VBox root;

    public Summary() {
        // Create root pane
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.setPadding(new Insets(50));
        root.getStylesheets().add(getClass().getResource("/CSSs/BottomLeftGUI.css").toExternalForm());
        root.setBackground(Background.fill(Color.BLACK));

        // Create text nodes for player stats
        Text killedMonstersText = new Text("Monsters Killed: 10"); // Replace 10 with actual value
        killedMonstersText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-fill:'white';");
        Text moneyGatheredText = new Text("Money Gathered: $100"); // Replace $100 with actual value
        moneyGatheredText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-fill:'white';");
        Text levelDiedOnText = new Text("Level Died On: 5"); // Replace 5 with actual value
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
            GameManager.getInstance().GameStart(new Knight(0, 0, 1));
        });

        menuButton.setOnAction(e -> {
            // Add action to return to the menu
            System.out.println("Return to Menu button clicked");
        });

        // Add nodes to root pane
        root.getChildren().addAll(killedMonstersText, moneyGatheredText, levelDiedOnText, retryButton, menuButton);

        // Create scene
        scene = new Scene(root, 1280, 720); // Set scene size as needed
    }

    public Scene getScene() {
        return scene;
    }
}
