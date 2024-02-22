package logic;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import utils.Config;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public TurnManager turnManager ;
    public GUIManager guiManager;
    public BasePlayerPiece player;
    public GridPane boardPane;

    public BasePiece[][] pieces = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE];
    public List<BasePiece> environmentPieces = new ArrayList<>();

    public boolean isInAttackMode = false;
    public boolean isInInventoryMode = false;
    public boolean isInUseSkillMode = false;

    public GameManager() {
        player = new Knight(0, 0, 1);
        boardPane = new GridPane();
        turnManager = new TurnManager(player, environmentPieces);
        guiManager = new GUIManager(turnManager, player);
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public boolean isEmptySquare(int row, int col) {
        return pieces[row][col] == null;
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

        }));
        timeline.play();
    }
    // TODO: Implement Save & Load System in here
}
