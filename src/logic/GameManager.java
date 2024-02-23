package logic;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import skills.BaseSkill;
import utils.Config;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public TurnManager turnManager;
    public GUIManager guiManager;
    public BasePlayerPiece player; // Current player class in the game
    public GridPane boardPane; // The board pane to create the game's grid system
    public ImageView[][] dungeonFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Contain dungeon floor textures
    public ArrayList<Point2D> selectedTiles = new ArrayList<>(); // Contain the selected tile
    public boolean[][] validMovesCache = new boolean[Config.BOARD_SIZE][Config.BOARD_SIZE];
    public BasePiece[][] piecesPosition = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Where each entity located
    public List<BasePiece> environmentPieces = new ArrayList<>(); // Where each environment piece i.e. wall, trap located
    public List<BaseSkill> playerSkills; // List of skills player currently have

    // ----------- UI Status -----------
    public boolean isInAttackMode = false;
    public boolean isInInventoryMode = false;
    public boolean isInUseSkillMode = false;

    public GameManager() {
        player = new Knight(0, 0, 1);
        playerSkills = player.getSkills();
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
        return piecesPosition[row][col] == null;
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
    // TODO: Implement Save & Load System in here
}
