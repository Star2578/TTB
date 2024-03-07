package logic;

import game.GameScene;
import items.BaseItem;
import items.potions.BluePotion;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import skills.BaseSkill;
import skills.EmptySlot;
import skills.LockedSlot;
import utils.Config;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;

    public GameScene gameScene;
    public Pane animationPane;

    public BasePlayerPiece player; // Current player class in the game
    public GridPane boardPane; // The board pane to create the game's grid system
    public ImageView[][] dungeonFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Contain dungeon floor textures

    public TileMap wallTileMap = new TileMap(new Image(Config.WallTileMapPath) , 4 , 4,32,32);;

    public ArrayList<Point2D> selectedMoveTiles = new ArrayList<>(); // Contain the selected move tile
    public ArrayList<Point2D> selectedAttackTiles = new ArrayList<>(); // Contain the selected attack tile
    public ArrayList<Point2D> selectedSkillTiles = new ArrayList<>(); // Contain the selected skill tile
    public ArrayList<Point2D> selectedItemTiles = new ArrayList<>(); // Contain the selected item tile

    public boolean[][] validMovesCache = new boolean[Config.BOARD_SIZE][Config.BOARD_SIZE];
    public BasePiece[][] piecesPosition = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Where each entity located
    public List<BasePiece> environmentPieces = new ArrayList<>(); // Where each environment piece i.e. wall, trap located

    // ----------- Skill -----------
    public final int SKILL_SLOTS = 8;
    public int unlockedSlots = 4;
    public BaseSkill[] playerSkills; // List of skills player currently have
    public BaseSkill selectedSkill;
    public BaseItem selectedItem;

    // -------------- Inventory --------------
    public List<BaseItem> inventory = new ArrayList<>();


    public GameManager() {
        player = new Knight(0, 0, 1);
        playerSkills = player.getSkills();
        for (int i = 0; i < SKILL_SLOTS; i++) {
            if (playerSkills[i] == null) {
                if (i < unlockedSlots) {
                    playerSkills[i] = new EmptySlot();
                } else {
                    playerSkills[i] = new LockedSlot();
                }
            }
        }
        boardPane = new GridPane();
        animationPane = new Pane();
        inventory.add(new BluePotion());
        inventory.add(new BluePotion());
        inventory.add(new BluePotion());
        inventory.add(new BluePotion());
        inventory.add(new BluePotion());
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
    // TODO: Implement Save & Load System in here
}
