package logic;

import scenes.GameScene;
import items.BaseItem;
import items.potions.*;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import logic.gameUI.GUIManager;
import pieces.BasePiece;
import pieces.players.*;
import pieces.wall.TileMap;
import skills.*;
import skills.universal.*;
import utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameManager {
    private static GameManager instance;

    private final Properties settingProperties;
    private final Properties gameProperties;
    private static final String CONFIG_FILE = "config.properties";
    private static final String PROGRESSION_FILE = "config.progression";

    // -------- Settings ---------
    public boolean fastUse;
    public boolean autoEndTurn;
    public boolean displayDamageNumber;
    public boolean displayActionPointOnCursor;

    // options
    public boolean fogOfWar = false;
    public boolean moreMonster = false;

    // ------------ Progression --------------
    public int playerMoney = 0;
    public int dungeonLevel = 1;

    public int totalKill = 0;
    public int totalKillThisRun = 0;
    public int totalMoves = 0;
    public int totalMovesThisRun = 0;
    public int totalMoney = 0;
    public int totalMoneyThisRun = 0;
    public int farthestLevelReach = 0;
    public int currentLevelReach = 0;


    public GameScene gameScene;
    public Pane animationPane;
    public GridPane fogPane;

    public BasePlayerPiece player; // Current player class in the game
    public GridPane boardPane; // The board pane to create the game's grid system
    public ImageView[][] dungeonFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Contain dungeon floor textures
    public ImageView[][] selectionFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Contain selection floor separate to dungeonFloor

    public TileMap wallTileMap = new TileMap(new Image(Config.WallTileMapPath) , 4 , 4,Config.SQUARE_SIZE,Config.SQUARE_SIZE);

    public ArrayList<Point2D> availableMoveTiles = new ArrayList<>(); // Contain the selected move tile
    public ArrayList<Point2D> availableAttackTiles = new ArrayList<>(); // Contain the selected attack tile
    public ArrayList<Point2D> availableSkillTiles = new ArrayList<>(); // Contain the selected skill tile
    public ArrayList<Point2D> availableItemTiles = new ArrayList<>(); // Contain the selected item tile

    public boolean[][] validMovesCache = new boolean[Config.BOARD_SIZE][Config.BOARD_SIZE];
    public BasePiece[][] piecesPosition = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE]; // Where each entity located
    public List<BasePiece> environmentPieces = new ArrayList<>(); // Where each environment piece i.e. wall, trap located

    // ----------- Skill -----------
    public final int SKILL_SLOTS = 8;
    public int skillUnlockedSlots = 4;
    public int itemUnlockedSlots = 4;
    public BaseSkill[] playerSkills; // List of skills player currently have
    public BaseSkill selectedSkill;
    public BaseItem selectedItem;

    public List<BaseItem> inventory = new ArrayList<>();
    public List<Point2D> doorAt = new ArrayList<>(); // use to store where the door is at

    public final BaseSkill[] SKILL_POOL = {
            new HolyLight(), new LesserHeal(), new BloodPact(), new Teleport(), new HammerFall(), new Rho_Aias(),
            new Ambush(), new Bind(), new BloodLust(), new Kick(), new Narcissistic(), new Punch(), new StaticShock(),
            new TripleStrike()
    };
    public final BaseItem[] ITEM_POOL = {
        new BluePotion(), new GreenPotion(), new PurplePotion(), new RedPotion(), new YellowPotion()
    };

    public GameManager() {
        player = new Knight(0, 0, 1);
        playerSkills = player.getSkills();
        boardPane = new GridPane();
        fogPane = new GridPane();
        animationPane = new Pane();

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
        dungeonLevel = 1; // start at 1 bro not 0
        player = playerClass;
        playerSkills = player.getSkills();
        availableItemTiles.clear();
        availableMoveTiles.clear();
        availableAttackTiles.clear();
        availableSkillTiles.clear();
        selectedSkill = null;
        selectedItem = null;
        inventory.clear();
        inventory.add(new RedPotion());
        inventory.add(new RedPotion());
        inventory.add(new RedPotion());
        inventory.add(new RedPotion());
        environmentPieces.clear();
        boardPane = new GridPane();
        fogPane = new GridPane();
        animationPane = new Pane();
        dungeonFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE];
        selectionFloor = new ImageView[Config.BOARD_SIZE][Config.BOARD_SIZE];
        wallTileMap = new TileMap(new Image(Config.WallTileMapPath) , 4 , 4,Config.SQUARE_SIZE,Config.SQUARE_SIZE);
        piecesPosition = new BasePiece[Config.BOARD_SIZE][Config.BOARD_SIZE];
        validMovesCache = new boolean[Config.BOARD_SIZE][Config.BOARD_SIZE];

        TurnManager.getInstance().initialize();
        SpawnerManager.getInstance().initialize();
        GUIManager.getInstance().initialize();
        TurnManager.getInstance().startPlayerTurn();

        gameScene = new GameScene();
        // set new game scene
        SceneManager.getInstance().setGameScene(gameScene.getScene());
        GUIManager.getInstance().updateCursor(gameScene.getScene(), Config.DefaultCursor);
        SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getGameScene());
    }
    public void GameOver() {
        // save game progress
        SoundManager.getInstance().playSoundEffect(Config.sfx_gameOverSound);
        totalMoneyThisRun = playerMoney;
        currentLevelReach = dungeonLevel;

        totalKill += totalKillThisRun;
        totalMoney += totalMoneyThisRun;
        totalMoves += totalMovesThisRun;
        farthestLevelReach = Math.max(farthestLevelReach, currentLevelReach);

        saveGame();
        // Send player back to main menu scene
        SceneManager.getInstance().getStage().setScene(SceneManager.getInstance().getSummaryScene());
    }

    public void saveGame() {
        // save the game progression
        try (OutputStream output = new FileOutputStream(PROGRESSION_FILE)) {
            // Progression
            gameProperties.setProperty("s_totalKill", String.valueOf(totalKill));
            gameProperties.setProperty("s_totalMoney", String.valueOf(totalMoney));
            gameProperties.setProperty("s_totalMoves", String.valueOf(totalMoves));
            gameProperties.setProperty("s_farthestLevelReach", String.valueOf(farthestLevelReach));

            gameProperties.store(output, "Game Progression");

        } catch (IOException e) {
            System.out.println("Error when saving game progress:" + e.getMessage());
        }
    }
    public void loadGame() {
        // load the game progression
        try (InputStream input = new FileInputStream(PROGRESSION_FILE)) {
            gameProperties.load(input);

            // Load progression from properties
            totalKill = Integer.parseInt(gameProperties.getProperty("s_totalKill", "0"));
            totalMoney = Integer.parseInt(gameProperties.getProperty("s_totalMoney", "0"));
            totalMoves = Integer.parseInt(gameProperties.getProperty("s_totalMoves", "0"));
            farthestLevelReach = Integer.parseInt(gameProperties.getProperty("s_farthestLevelReach", "0"));


        } catch (IOException e) {
            System.out.println("Error when loading game progression:" + e.getMessage());
        }
    }

    public void loadSettings() {
        // Implement loading settings from file
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            settingProperties.load(input);

            // Load settings from properties
            SoundManager.getInstance().setBackgroundMusicSlider(Float.parseFloat(settingProperties.getProperty("backgroundMusicSlider", "50")));
            SoundManager.getInstance().setBackgroundMusicVolume(Float.parseFloat(settingProperties.getProperty("backgroundMusicVolume", String.valueOf(SoundManager.getMidVolume()))));
            SoundManager.getInstance().setSoundEffectSlider(Float.parseFloat(settingProperties.getProperty("soundEffectSlider", "50")));
            SoundManager.getInstance().setSoundEffectVolume(Float.parseFloat(settingProperties.getProperty("soundEffectVolume", String.valueOf(SoundManager.getMidVolume()))));
            fastUse = Boolean.parseBoolean(settingProperties.getProperty("_fastUse", String.valueOf(false)));
            autoEndTurn = Boolean.parseBoolean(settingProperties.getProperty("_autoEndTurn", String.valueOf(false)));
            displayDamageNumber = Boolean.parseBoolean(settingProperties.getProperty("_displayDamageNumber", String.valueOf(false)));
            displayActionPointOnCursor = Boolean.parseBoolean(settingProperties.getProperty("_displayActionPointOnCursor", String.valueOf(false)));

        } catch (IOException e) {
            System.out.println("Error when loading game setting:" + e.getMessage());
        }
    }
    public void saveSettings() {
        // Implement saving settings to file
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            // Save settings to properties
            settingProperties.setProperty("backgroundMusicVolume", String.valueOf(SoundManager.getInstance().getBackgroundMusicVolume()));
            settingProperties.setProperty("backgroundMusicSlider", String.valueOf(SoundManager.getInstance().getBackgroundMusicSlider()));
            settingProperties.setProperty("soundEffectVolume", String.valueOf(SoundManager.getInstance().getSoundEffectVolume()));
            settingProperties.setProperty("soundEffectSlider", String.valueOf(SoundManager.getInstance().getSoundEffectSlider()));
            settingProperties.setProperty("_fastUse", String.valueOf(fastUse));
            settingProperties.setProperty("_autoEndTurn", String.valueOf(autoEndTurn));
            settingProperties.setProperty("_displayDamageNumber", String.valueOf(displayDamageNumber));
            settingProperties.setProperty("_displayActionPointOnCursor", String.valueOf(displayActionPointOnCursor));
            // Save other settings...

            settingProperties.store(output, "Game Settings");

        } catch (IOException e) {
            System.out.println("Error when saving game settings:" + e.getMessage());
        }
    }
}
