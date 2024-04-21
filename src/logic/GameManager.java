package logic;

import game.GameScene;
import items.BaseItem;
import items.potions.BluePotion;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.player.*;
import skills.*;
import skills.knight.Heal;
import skills.knight.Slash;
import utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameManager {
    private static GameManager instance;

    private final Properties settingProperties;
    private final Properties gameProperties;
    private static final String CONFIG_FILE_PATH = "config.properties";
    private static final String PROGRESSION_FILE_PATH = "config.progression";

    public GameScene gameScene;
    public Pane animationPane;

    public BasePlayerPiece player; // Current player class in the game
    public GridPane boardPane; // The board pane to create the game's grid system
    public ImageView[][] dungeonFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Contain dungeon floor textures
    public ImageView[][] selectionFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Contain selection floor separate to dungeonFloor

    public TileMap wallTileMap = new TileMap(new Image(Config.WallTileMapPath) , 4 , 4,Config.SQUARE_SIZE,Config.SQUARE_SIZE);

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
    public int itemSlots = 4;
    public BaseSkill[] playerSkills; // List of skills player currently have
    public BaseSkill selectedSkill;
    public BaseItem selectedItem;

    // -------------- Inventory --------------
    public List<BaseItem> inventory = new ArrayList<>();
    public int playerMoney = 0;
    public int dungeonLevel = 0;
    public List<Point2D> doorAt = new ArrayList<>(); // use to store where the door is at

    public final BaseSkill[] UNIVERSAL_SKILL_POOL = {
        new Slash(), new Heal()
    };
    public final BaseItem[] ITEM_POOL = {
        new BluePotion()
    };

    public GameManager() {
        player = new Knight(0, 0, 1);
        playerSkills = player.getSkills();
        for (int i = 0; i < SKILL_SLOTS; i++) {
            if (playerSkills[i] == null) {
                if (i < unlockedSlots) {
                    playerSkills[i] = new EmptySkill();
                } else {
                    playerSkills[i] = new LockedSlot();
                }
            }
        }
        boardPane = new GridPane();
        animationPane = new Pane();
        inventory.add(new BluePotion()); // this is for testing

        settingProperties = new Properties();
        gameProperties = new Properties();
        loadSettings();
        loadGame();
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

    public void GameStart(BasePlayerPiece playerClass) {
        // reset fields
        playerMoney = 0;
        dungeonLevel = 0;
        player = playerClass;
        for (int i = 0; i < SKILL_SLOTS; i++) {
            if (playerSkills[i] == null) {
                if (i < unlockedSlots) {
                    playerSkills[i] = new EmptySkill();
                } else {
                    playerSkills[i] = new LockedSlot();
                }
            }
        }
        selectedItemTiles.clear();
        selectedMoveTiles.clear();
        selectedAttackTiles.clear();
        selectedSkillTiles.clear();
        selectedSkill = null;
        selectedItem = null;
        inventory.clear();
        environmentPieces.clear();
        boardPane = new GridPane();
        animationPane = new Pane();
        dungeonFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE];
        selectionFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE];
        wallTileMap = new TileMap(new Image(Config.WallTileMapPath) , 4 , 4,Config.SQUARE_SIZE,Config.SQUARE_SIZE);
        piecesPosition = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE];
        validMovesCache = new boolean[Config.BOARD_SIZE][Config.BOARD_SIZE];

        inventory.add(new BluePotion()); // this is for testing

        TurnManager.getInstance().initialize();
        SpawnerManager.getInstance().initialize();
        GUIManager.getInstance().initialize();

        gameScene = new GameScene();
        // set new game scene
        SceneManager.getInstance().setGameScene(gameScene.getScene());
        SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getGameScene());
    }
    public void GameOver() {
        // save game progress
        saveGame();
        SoundManager.getInstance().playSoundEffect(Config.sfx_gameOverSound);

        // Send player back to main menu scene
        SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getMenuScene());
    }

    private void loadSettings() {
        // Implement loading settings from file
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            settingProperties.load(input);

            // Load settings from properties

        } catch (IOException e) {
            System.out.println("Error when loading game setting:" + e.getMessage());
        }
    }
    private void saveSettings() {
        // Implement saving settings to file
        try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
            // Save settings to properties

            // Save other settings...

            settingProperties.store(output, "Game Settings");

        } catch (IOException e) {
            System.out.println("Error when saving game settings:" + e.getMessage());
        }
    }

    public void saveGame() {
        // save the game progression
        try (OutputStream output = new FileOutputStream(PROGRESSION_FILE_PATH)) {
            // Progression


            gameProperties.store(output, "Game Progression");

        } catch (IOException e) {
            System.out.println("Error when saving game progress:" + e.getMessage());
        }
    }

    public void loadGame() {
        // load the game progression
        try (InputStream input = new FileInputStream(PROGRESSION_FILE_PATH)) {
            gameProperties.load(input);

            // Load progression from properties


        } catch (IOException e) {
            System.out.println("Error when loading game progression:" + e.getMessage());
        }
    }
}
