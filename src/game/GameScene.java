package game;

import items.BaseItem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import logic.*;
import logic.effect.EffectManager;
import logic.handlers.AttackHandler;
import logic.handlers.MovementHandler;
import logic.handlers.SkillHandler;
import logic.ui.GUIManager;
import logic.ui.display.NpcDisplay;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.enemies.SlimeBoss;
import pieces.npcs.BaseNpcPiece;
import pieces.npcs.Dealer;
import pieces.player.BasePlayerPiece;
import pieces.player.Knight;
import pieces.wall.BaseWallPiece;
import skills.BaseSkill;
import utils.Config;
import utils.Usable;

import java.util.ArrayList;
import java.util.List;

public class GameScene {
    private static final int BOARD_SIZE = Config.BOARD_SIZE;
    private static final int SQUARE_SIZE = Config.SQUARE_SIZE;
    private static final int GAME_SIZE = Config.GAME_SIZE;

    private static final double MAX_FOG_OPACITY = 1.0;
    private static final double MAX_FOG_VIEW_DISTANT = 2;

    private GameManager gameManager = GameManager.getInstance();
    private EffectManager effectManager = EffectManager.getInstance();
    private BasePlayerPiece player;
    private GUIManager guiManager;
    private TurnManager turnManager;
    private DungeonGenerator dungeonGenerator;



    private ArrayList<Point2D> selectedAttackTiles = gameManager.selectedAttackTiles;
    private ArrayList<Point2D> selectedMoveTiles = gameManager.selectedMoveTiles;
    private ArrayList<Point2D> selectedSkillTiles = gameManager.selectedSkillTiles;
    private ArrayList<Point2D> selectedItemTiles = gameManager.selectedItemTiles;
    private boolean[][] validMovesCache = gameManager.validMovesCache; // Valid moves without entity
    private ImageView[][] dungeonFloor = gameManager.dungeonFloor; // The dungeon floor texture
    private ImageView[][] selectionFloor = gameManager.selectionFloor; // The selection floor texture
    private BasePiece[][] piecesPosition = GameManager.getInstance().piecesPosition; // Where each entity locate
    private List<BasePiece> environmentPieces = gameManager.environmentPieces; // List of all environment pieces (monsters and traps)
    private boolean isPlayerPieceSelected = false;


    //------------<Pane layers>----------------------------------------------------
    private Scene scene;
    private Pane animationPane = gameManager.animationPane;
    private GridPane boardPane = gameManager.boardPane;
    private GridPane fogPane = gameManager.fogPane;
    private GridPane tilePane = new GridPane();
    private BorderPane root;
    private VBox rightPane; // Contain right side UI
    private VBox leftPane; // Contain left side UI
    private StackPane centerPane; // Contain the game board
    private Text currentLevelText;


    public GameScene() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #1c0a05;");
        scene = new Scene(root, 1280, 720);
        scene.getStylesheets().addAll(
                getClass().getResource("/CSSs/BottomLeftGUI.css").toExternalForm() ,
                getClass().getResource("/CSSs/Global.css").toExternalForm());

        tilePane.setMinSize(GAME_SIZE,GAME_SIZE);
        tilePane.setMaxSize(GAME_SIZE,GAME_SIZE);
        tilePane.setDisable(true);


        //this pane contains all animation-related nodes
        //it's placed transparently over boardPane
        animationPane.setMaxWidth(SQUARE_SIZE*BOARD_SIZE);
        animationPane.setMaxHeight(SQUARE_SIZE*BOARD_SIZE);
        animationPane.setDisable(true);

        //this pane contains all effect animation node
        //placed transparently over boardPane
        effectManager.effectPane.setMaxWidth(SQUARE_SIZE*BOARD_SIZE);
        effectManager.effectPane.setMaxHeight(SQUARE_SIZE*BOARD_SIZE);
        effectManager.effectPane.setDisable(true);

        rightPane = new VBox(); // Pane for right area
        rightPane.setBackground(Background.fill(Color.DARKRED));

        leftPane = new VBox(); // Pane for left area
        leftPane.setBackground(Background.fill(Color.DARKCYAN));

        // Create the main game area
        initFloor(boardPane);
        boardPane.setMinSize(GAME_SIZE, GAME_SIZE);
        boardPane.setMaxSize(GAME_SIZE, GAME_SIZE);

        // Center the game board using a StackPane
        centerPane = new StackPane();
        centerPane.getChildren().addAll(boardPane , tilePane , animationPane , effectManager.effectPane);

        if (GameManager.getInstance().fogOfWar) centerPane.getChildren().addAll(fogPane);

        boardPane.setBackground(Background.fill(Color.GOLD));
        root.setCenter(centerPane);

        // this pane contain all fog
        // setup fogPane if needed
        if (GameManager.getInstance().fogOfWar) {
            initFog(fogPane);
            fogPane.setMinSize(GAME_SIZE, GAME_SIZE);
            fogPane.setMaxSize(GAME_SIZE, GAME_SIZE);
            fogPane.setDisable(true);
        }

        // Current Level Text
        currentLevelText = new Text(String.valueOf(GameManager.getInstance().dungeonLevel));
        currentLevelText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:50;" +
                        "-fx-fill:'white';");
        currentLevelText.setTranslateX(300);
        currentLevelText.setTranslateY(-290);
        centerPane.getChildren().add(currentLevelText);

        gameStart();
        // Add game area and GUI panes to the root BorderPane
        root.setRight(rightPane);
        rightPane.getChildren().add(guiManager.getRightSideUI());

        // add StackPane to display box overlays
        StackPane stackOverlay = new StackPane();
        root.setLeft(stackOverlay);
        stackOverlay.getChildren().addAll(leftPane, GUIManager.getInstance().skillSelectDisplay.getSkillInfoOverlay().getView(), GUIManager.getInstance().inventoryDisplay.getItemInfoOverlay().getView());

        root.getChildren().add(GUIManager.getInstance().getActionPointDisplayText());

        stackOverlay.setOnMouseMoved(event -> {
            // Update the position of the BoxOverlay to follow the mouse
            GUIManager.getInstance().skillSelectDisplay.getSkillInfoOverlay().updatePosition(event.getX(), event.getY(), -140, 15);
            GUIManager.getInstance().inventoryDisplay.getItemInfoOverlay().updatePosition(event.getX(), event.getY(), -140, 15);
        });
        leftPane.getChildren().addAll(guiManager.getPlayerOptionsMenu());
        leftPane.setPadding(new Insets(10));

        // move action point display text around mouse cursor
        scene.setOnMouseMoved(mouseEvent -> {
            GUIManager.getInstance().getActionPointDisplayText().setTranslateX(mouseEvent.getX() + 20);
            GUIManager.getInstance().getActionPointDisplayText().setTranslateY(mouseEvent.getY());
        });

        // Right click anywhere in the scene to cancel/deselect anything
        scene.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                resetSelectionAll();
            }
        });


        // Set up the scene and stage
        SceneManager.getInstance().setGameScene(scene); // Save this scene for later use
        setupMouseEvents();
        setupKeyEvents(scene); // Debug Tool
    }

    public Scene getScene(){
        return scene;
    }

    private void initializeEnvironment() {
        // Add environment pieces (monsters and traps) to the list
        BaseMonsterPiece[] monsterPool1 = SpawnerManager.getInstance().monsterPool_1;

        // clear environmentPieces
        environmentPieces.clear();

        // reset monsterCount = 0 every time we got to new floor
        SpawnerManager.getInstance().monsterCount = 0;

        SpawnerManager.getInstance().randomMonsterSpawnFromPool(monsterPool1, environmentPieces);

        for (BasePiece entity : environmentPieces) {
            placeEntityRandomly(entity);
        }
    }

    private void gameStart() {
        // Initialize player at starting position
        player = GameManager.getInstance().player;
        dungeonGenerator = new DungeonGenerator(); // Initialize DungeonGenerator
        dungeonGenerator.generateDungeon(); // Generate dungeon
        placeDungeon(dungeonGenerator.getDungeonLayout());
        placeEntityRandomly(player);
        precomputeValidMoves();
        initializeEnvironment();

        // fog of war only when play chose
        if (GameManager.getInstance().fogOfWar) initFog(fogPane);

        turnManager = TurnManager.getInstance();

        guiManager = GUIManager.getInstance();

        turnManager.startPlayerTurn();
    }

    private void initFog(GridPane gridPane) {
        // Generate fog for fogPane grid
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView fog = new ImageView();

                // set floor image into square size
                fog.setFitWidth(SQUARE_SIZE);
                fog.setFitHeight(SQUARE_SIZE);

                fog.setImage(ImageScaler.resample(new Image(Config.FogPath), 2)); // Set texture of dungeon floor
                gridPane.add(fog, col, row);

                // Start timeline to update fog visibility continuously
                startFogOpacityUpdate(fog);
            }
        }
    }

    // Method to start timeline for continuously updating fog opacity
    private void startFogOpacityUpdate(ImageView fog) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    updateFogOpacity(fog, player.getRow(), player.getCol());
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Method to update fog opacity based on distance from player
    private void updateFogOpacity(ImageView fog, int playerRow, int playerCol) {
        // Calculate distance between fog square and player
        double distance = Math.sqrt(Math.pow(playerRow - GridPane.getRowIndex(fog), 2) + Math.pow(playerCol - GridPane.getColumnIndex(fog), 2));

        // Calculate opacity based on distance
        double opacity = (distance / MAX_FOG_VIEW_DISTANT) - 1;

        // Ensure opacity is within valid range
        opacity = Math.max(0, Math.min(opacity, MAX_FOG_OPACITY));

        // Set fog opacity
            fog.setOpacity(opacity);
    }

    private void initFloor(GridPane gridPane) {
        // Generate the dungeon floor according to BOARD_SIZE
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView floor = new ImageView();

                // set floor image into square size
                floor.setFitWidth(SQUARE_SIZE);
                floor.setFitHeight(SQUARE_SIZE);

                floor.setImage(ImageScaler.resample(new Image(Config.FloorPath), 2)); // Set texture of dungeon floor
                gridPane.add(floor, col, row);
                dungeonFloor[row][col] = floor;

                // second layer for selector tile
                ImageView selectionFloorLayer = new ImageView();

                selectionFloorLayer.setFitWidth(SQUARE_SIZE);
                selectionFloorLayer.setFitHeight(SQUARE_SIZE);
                selectionFloorLayer.setMouseTransparent(true); // make mouse event ignore this image
                gridPane.add(selectionFloorLayer, col, row);

                selectionFloor[row][col] = selectionFloorLayer;
            }
        }
    }

    private void placeDungeon(char [][] dungeonLayout) {
        // Place the walls according to dungeon generated

        //make duplicate dungeonLayout of size + 1 , to prevent null border
        char[][] expandedLayout = new char[BOARD_SIZE+2][BOARD_SIZE+2];
        for(int i = 0 ; i < expandedLayout.length ; i++){
            for(int j = 0 ; j < expandedLayout[0].length ; j++){
                if(i==0 || i==expandedLayout.length-1 || j==0 || j==expandedLayout[0].length-1){
                    expandedLayout[i][j] = '#';
                }
                else{
                    expandedLayout[i][j] = dungeonLayout[i-1][j-1];
                }
            }
        }

        //=================<autotiling system>===============================================
        for (int row = 1; row < expandedLayout.length-1 ; row++) {
            for (int col = 1; col < expandedLayout[0].length-1 ; col++) {
                if (expandedLayout[row][col] == '#') {
                    // assign new wall object to piecesPosition & add texture to tilePane
                    piecesPosition[row-1][col-1] = new BaseWallPiece(row-1, col-1);
                    // |  1|  2|  4| - tile around has its bit value
                    // |  8|  X| 16| - bitMask -> adjacent
                    // | 32| 64|128| - **check corner when both adjacent is wall
                    int bitMask = 0;
                    int[] jumpList =  { 2,8,10,11,16,18,22,24,
                            26,27,30,31,64,66,72,74,
                            75,80,82,86,88,90,91,94,
                            95,104,106,107,120,122,123,126,
                            127,208,210,214,216,218,219,222,
                            223,248,250,251,254,255,0};

                    //adjacent
                    if(expandedLayout[row-1][col]== '#') bitMask+=2;
                    if(expandedLayout[row][col-1]== '#') bitMask+=8;
                    if(expandedLayout[row][col+1]== '#') bitMask+=16;
                    if(expandedLayout[row+1][col]== '#') bitMask+=64;
                    //corner
                    if(expandedLayout[row-1][col]== '#' && expandedLayout[row][col-1]== '#' && expandedLayout[row-1][col-1]== '#') bitMask+=1;
                    if(expandedLayout[row-1][col]== '#' && expandedLayout[row][col+1]== '#' && expandedLayout[row-1][col+1]== '#') bitMask+=4;
                    if(expandedLayout[row+1][col]== '#' && expandedLayout[row][col-1]== '#' && expandedLayout[row+1][col-1]== '#') bitMask+=32;
                    if(expandedLayout[row+1][col]== '#' && expandedLayout[row][col+1]== '#' && expandedLayout[row+1][col+1]== '#') bitMask+=128;

                    for(int i = 0 ; i < jumpList.length; i++){
                        if(bitMask == jumpList[i]){
                            tilePane.add( ((BaseWallPiece)(piecesPosition[row-1][col-1])).getTileMap().getTileAt((i+1)/8 , (i+1)%8) , col-1 , row-1);
                            break;
                        }
                    }
                }
            }
        }
        //==================================================================================
    }

    private void placePiece(BasePiece piece) {
        //this method is called after generated dungeon
        //place Piece to the board

        piece.animationImage.setFitWidth(SQUARE_SIZE);
        piece.animationImage.setX(piece.getCol()*SQUARE_SIZE + piece.getOffsetX());
        piece.animationImage.setY(piece.getRow()*SQUARE_SIZE + piece.getOffsetY());

        animationPane.getChildren().add(piece.animationImage);

        if (piece instanceof BasePlayerPiece) {
            // Place a text "You're here!" above the player
            Text here = new Text("You're here!\nV");
            here.setStyle(
                    "-fx-font-family:x16y32pxGridGazer;" +
                    "-fx-font-size:24;" +
                    "-fx-fill:'white';");
            here.setTextAlignment(TextAlignment.CENTER);
            here.setX(piece.getCol()*SQUARE_SIZE - 45);
            here.setY(piece.getRow()*SQUARE_SIZE - 30); // Adjust the Y position to place it above the player
            animationPane.getChildren().add(here);

            // Animation for the text
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), here);
            translateTransition.setToY(-10); // Move up by 10 pixels
            translateTransition.setAutoReverse(true); // Move back and forth
            translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Repeat indefinitely
            translateTransition.play();

            // Timeline to make both text and "V" part disappear after 1 second
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(5),
                    ae -> {
                        animationPane.getChildren().remove(here);
                        translateTransition.stop();
                    }));
            timeline.play();
        }
    }

    private void setupMouseEvents() {
        // Add mouse event for each square
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row; // Make row effectively final
                final int currentCol = col; // Make col effectively final
                ImageView square = dungeonFloor[row][col];
                int finalRow = row;
                int finalCol = col;
                square.setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.PRIMARY){
                        //left click for moving & attack
                        handleSquareClick(currentRow, currentCol);

                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        BasePiece target = piecesPosition[finalRow][finalCol];

                        if (target instanceof BaseMonsterPiece monsterPiece) {
                            GUIManager.getInstance().eventLogDisplay.addLog("--------------------------------");
                            GUIManager.getInstance().eventLogDisplay.addLog(monsterPiece.getClass().getSimpleName());
                            GUIManager.getInstance().eventLogDisplay.addLog("HP: "+ monsterPiece.getCurrentHealth() + "/" + monsterPiece.getMaxHealth());
                            // TODO : Maybe log brief description of the monster characteristic?
                            GUIManager.getInstance().eventLogDisplay.addLog("--------------------------------");
                        }
                        if (target instanceof BasePlayerPiece playerPiece) {
                            GUIManager.getInstance().eventLogDisplay.addLog("--------------------------------");
                            GUIManager.getInstance().eventLogDisplay.addLog(playerPiece.getClass().getSimpleName());
                            GUIManager.getInstance().eventLogDisplay.addLog("HP: "+ playerPiece.getCurrentHealth() + "/" + playerPiece.getMaxHealth());
                            GUIManager.getInstance().eventLogDisplay.addLog("Mana: "+ playerPiece.getCurrentMana() + "/" + playerPiece.getMaxMana());
                            GUIManager.getInstance().eventLogDisplay.addLog("Action Point: "+ playerPiece.getCurrentActionPoint() + "/" + playerPiece.getMaxActionPoint());
                            GUIManager.getInstance().eventLogDisplay.addLog("Attack: "+ playerPiece.getAttackDamage());
                            GUIManager.getInstance().eventLogDisplay.addLog("--------------------------------");
                        }
                    }
                });

                // detect mouse enter/exit for squares
                square.setOnMouseEntered(mouseEvent -> {
                    if (piecesPosition[finalRow][finalCol] instanceof Dealer) {
                        GUIManager.getInstance().updateCursor(this.getScene(), Config.QuestionCursor);
                    }
                    square.setImage(new Image(Config.FloorHoverPath));
                });
                square.setOnMouseExited(mouseEvent -> {
                    if (piecesPosition[finalRow][finalCol] instanceof Dealer) {
                        GUIManager.getInstance().updateCursor(this.getScene(), Config.DefaultCursor);
                    }
                    square.setImage(new Image(Config.FloorPath));

                    // Check for door
                    if (!gameManager.doorAt.isEmpty()) {
                        for (Point2D coordinate : gameManager.doorAt) {
                            int rowD = (int) coordinate.getX();
                            int colD = (int) coordinate.getY();

                            dungeonFloor[rowD][colD].setImage(ImageScaler.resample(new Image(Config.DoorPath), 2));
                        }
                    }
                });
            }
        }

        for(int i = 0 ; i < BOARD_SIZE; i++){
            for(int j = 0 ; j < BOARD_SIZE ; j++){
                if(piecesPosition[i][j] == null){
                    System.out.print(". ");
                }
                else if( piecesPosition[i][j] instanceof BaseWallPiece){
                    System.out.print("U ");
                }
                else if( piecesPosition[i][j] instanceof BasePlayerPiece){
                    System.out.print("P ");
                }
                else if( piecesPosition[i][j] instanceof BaseMonsterPiece){
                    System.out.print("M ");
                }
            }
            System.out.println();
        }
    }

    private void handleSquareClick(int row, int col) {
        System.out.println("Clicked on square (" + row + ", " + col + ")");
        if (!player.canAct()) {
            SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);

            resetSelectionAll();
            return;
        }

        // ------------------------- Attack Mode -------------------------

        boolean isInAttackMode = GUIManager.getInstance().isInAttackMode;

        if (isInAttackMode) {
            // Check if the clicked square is within showValid attack range
            if (player.validAttack(row, col)) {
                // Check if there is a monster on the clicked square
                if (piecesPosition[row][col] instanceof BaseMonsterPiece monsterPiece) {
                    // turn to face monster
                    player.changeDirection(Integer.compare(monsterPiece.getCol(), player.getCol()));
                    // Perform the attack on the monster
                    SoundManager.getInstance().playSoundEffect(Config.sfx_attackSound);
                    player.attack(monsterPiece);
                    GUIManager.getInstance().eventLogDisplay.addLog("Player attack " + monsterPiece.getClass().getSimpleName() + " at (" + row + ", " + col + ")");
                    if (!monsterPiece.isAlive()) {
                        removePiece(monsterPiece);
                        environmentPieces.remove(monsterPiece);
                        gameManager.totalKillThisRun++;
                    }
                }
            }
            resetSelection(1);

            return;
        }

        // ------------------------- Skill Mode -------------------------

        if (gameManager.selectedSkill != null) {
            boolean enoughMana = player.getCurrentMana() >= gameManager.selectedSkill.getManaCost();
            boolean enoughActionPoint = player.getCurrentActionPoint() >= gameManager.selectedSkill.getActionPointCost();

            if (gameManager.selectedSkill.validRange(row, col)) {
                // Check if there is a monster on the clicked square
                if (piecesPosition[row][col] instanceof BaseMonsterPiece monsterPiece) {
                    if (gameManager.selectedSkill.castOnMonster()) {
                        // turn to face monster
                        player.changeDirection(Integer.compare(monsterPiece.getCol(), player.getCol()));
                        // Perform the attack on the monster
                        if (enoughMana && enoughActionPoint) {
                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + GameManager.getInstance().selectedSkill.getName(), GameManager.getInstance().selectedSkill.getNameColor());
                            gameManager.selectedSkill.perform(monsterPiece);
                        } else {
                            SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                            System.out.println("Not enough mana or action point");
                        }
                        if (!monsterPiece.isAlive()) {
                            removePiece(monsterPiece);
                            environmentPieces.remove(monsterPiece);
                            gameManager.totalKillThisRun++;
                        }
                    }
                } else if (piecesPosition[row][col] instanceof BasePlayerPiece playerPiece) {
                    if (gameManager.selectedSkill.castOnSelf()) {

                        if (enoughMana && enoughActionPoint) {
                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + gameManager.selectedSkill.getName());
                            gameManager.selectedSkill.perform(playerPiece);
                        } else {
                            SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                            System.out.println("Not enough mana or action point");
                        }
                    }
                } else {
                    if (!gameManager.selectedSkill.castOnSelf() && !gameManager.selectedSkill.castOnMonster()) {
                        if (enoughMana && enoughActionPoint && piecesPosition[row][col] == null) {
                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + GameManager.getInstance().selectedSkill.getName(), GameManager.getInstance().selectedSkill.getNameColor());
                            gameManager.selectedSkill.perform(new Knight(row, col, 1)); // dummy target for row/col
                        } else {
                            SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                            System.out.println("Not enough mana or action point");
                        }
                    }
                }
            }
            resetSelection(2);

            return;
        }


        // ----------------------- Handle Item -----------------------

        BaseItem item = GameManager.getInstance().selectedItem;
        if (item != null) {
            if (item instanceof Usable usableItem) {
                if (usableItem.validRange(row, col)) {
                    BasePiece target = piecesPosition[row][col];

                    if (target instanceof BaseMonsterPiece monsterPiece) {
                        // use item on monster
                        if (((Usable) item).castOnMonster()) {

                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + gameManager.selectedItem.getName() + " on " + monsterPiece.getClass().getSimpleName());
                            usableItem.useItem(monsterPiece);

                            // throw away after use
                            GUIManager.getInstance().inventoryDisplay.throwAwayItem(item);
                        }
                    } else if (target instanceof BasePlayerPiece playerPiece) {
                        // use item on player
                        if (((Usable) item).castOnSelf()) {

                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + gameManager.selectedItem.getName());
                            usableItem.useItem(playerPiece);
                            // throw away after use
                            GUIManager.getInstance().inventoryDisplay.throwAwayItem(item);
                        }
                    }
                }
            }

            resetSelection(3);

            return;
        }

        NpcDisplay npcDisplay = GUIManager.getInstance().getNpcDisplay();
        // ------------------------- NPC Interact -------------------------
        if (piecesPosition[row][col] instanceof BaseNpcPiece npc) {
            double distance = Math.sqrt(Math.pow(GameManager.getInstance().player.getRow() - npc.getRow(), 2) + Math.pow(GameManager.getInstance().player.getCol() - npc.getCol(), 2));

            // if in range, can talk
            if (distance <= 1.5) {
                GUIManager.getInstance().switchToNpcDisplay();

                npcDisplay.setNpcPortrait(npc.getPortrait()); // get npc portrait
                npcDisplay.getNpcName().setText(npc.getName()); // get npc name
                npcDisplay.setDialogueText(npc.getCurrentDialogue()); // set initial dialogue

                npcDisplay.clearDialogueOption(); // clear all options before adding new ones
                npc.setDialogueOptions(npcDisplay); // dialogue options for each npc
                npcDisplay.addDialogueOption("Good bye", new Runnable() {
                    @Override
                    public void run() {
                        SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
                        GUIManager.getInstance().switchToEventLog();
                    }
                });

                npcDisplay.clearAdditionalOverlay();
            }
        } else {
            GUIManager.getInstance().switchToEventLog();
            npcDisplay.clearAdditionalOverlay();
        }

        // ------------------------- Movement Mode -------------------------
        if (player.getRow() == row && player.getCol() == col) {
            // Toggle move selection mode by clicking on player's grid
            isPlayerPieceSelected = !isPlayerPieceSelected;
            if (isPlayerPieceSelected) {
                MovementHandler.showValidMoves(row, col);
            } else {
                resetSelection(0);
            }
        } else if (isPlayerPieceSelected) {
            if (validMovesCache[row][col] && player.validMove(row, col) && piecesPosition[row][col] == null) {
                GUIManager.getInstance().eventLogDisplay.addLog("Moving player to square (" + row + ", " + col + ")");
                MovementHandler.movePlayer(row, col);
            } else {
                SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                System.out.println("Invalid move");
            }
            resetSelection(0);
        }
    }

    private boolean isValidMove(int row, int col) {
        return !(piecesPosition[row][col] instanceof BaseWallPiece); // Destination square contains a wall, invalid move
    }

    private void precomputeValidMoves() {
        SpawnerManager.getInstance().freeSquareCount = 0;
        // Iterate over all squares to compute valid moves and cache them
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                validMovesCache[row][col] = isValidMove(row, col);
                if (validMovesCache[row][col]) SpawnerManager.getInstance().freeSquareCount++;
            }
        }
    }

    public void resetSelection(int type) {
        isPlayerPieceSelected = false;

        /**************************************************
         *   type manual
         *   0 = movement remove selected tiles
         *   1 = attack remove selected tiles
         *   2 = skill remove selected tiles
         *   3 = item remove selected tiles
         ***************************************************/

        if(type == 0){
            //reset move Selected Tiles
            for (Point2D selectedMoveTile : selectedMoveTiles) {
                selectionFloor[(int) selectedMoveTile.getX()][(int) selectedMoveTile.getY()]
                        .setImage(null);

            }
            selectedMoveTiles.clear();

        }
        else if(type == 1){
            //reset attack Selected Tiles
            for (Point2D selectedAttackTile : selectedAttackTiles) {
                selectionFloor[(int) selectedAttackTile.getX()][(int) selectedAttackTile.getY()]
                        .setImage(null);
            }
            selectedAttackTiles.clear();
            GUIManager.getInstance().isInAttackMode = false;
//            GUIManager.getInstance().updateCursor(scene, Config.DefaultCursor);
        }
        else if (type == 2) {
            //reset skill Selected Tiles
            for (Point2D selectedSkillTile : selectedSkillTiles) {
                selectionFloor[(int) selectedSkillTile.getX()][(int) selectedSkillTile.getY()]
                        .setImage(null);
            }
            selectedSkillTiles.clear();
            if (gameManager.selectedSkill != null)
                guiManager.deselectFrame(gameManager.selectedSkill.getFrame());
            gameManager.selectedSkill = null;
        } else if (type == 3) {
            //reset item Selected Tiles
            for (Point2D selectedItemTile : selectedItemTiles) {
                selectionFloor[(int) selectedItemTile.getX()][(int) selectedItemTile.getY()]
                        .setImage(null);
            }
            // reset item selection
            if (gameManager.selectedItem != null)
                guiManager.deselectFrame(gameManager.selectedItem.getFrame());
            gameManager.selectedItem = null;
        }
        //set cursor back to normal
        GUIManager.getInstance().updateCursor(scene, Config.DefaultCursor);
    }

    private void placeEntityRandomly(BasePiece entity) {
        int row, col;
        do {
            row = (int) (Math.random() * BOARD_SIZE);
            col = (int) (Math.random() * BOARD_SIZE);
        } while (!isValidMove(row, col) || piecesPosition[row][col] != null);

        entity.setRow(row);
        entity.setCol(col);
        piecesPosition[row][col] = entity; // Mark the position as occupied
        placePiece(entity);
    }

    private void removeElements() {
        // Mostly for debugging purpose
        // to try re-generate the dungeon
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (piecesPosition[row][col] != null) {
                    boardPane.getChildren().remove(piecesPosition[row][col].getTexture());
                    piecesPosition[row][col] = null;
                }
            }
        }
        for (BasePiece entity : environmentPieces) {
            boardPane.getChildren().remove(entity.getTexture());
        }
        boardPane.getChildren().remove(player.getTexture());
        tilePane.getChildren().clear();
        animationPane.getChildren().clear();
    }

    public void removePiece(BasePiece toRemove) {
        int row = toRemove.getRow();
        int col = toRemove.getCol();

        // Remove the piece's ImageView from the boardPane
        boardPane.getChildren().remove(toRemove.getTexture());

        // Remove the piece's ImageView from the animationPane
        if (toRemove instanceof BaseMonsterPiece monsterPiece)
            animationPane.getChildren().remove(monsterPiece.animationImage);

        // Set the corresponding entry in the pieces array to null
        piecesPosition[row][col] = null;

        // remove from environment
        environmentPieces.remove(toRemove);
    }

    private void setupKeyEvents(Scene scene) {
        // Debug tool
        scene.setOnKeyPressed(event -> {


            switch (event.getCode()) {
                case ESCAPE:
                    SceneManager.getInstance().switchSceneTo(Setting.setting(SceneManager.getInstance().getStage(), this.scene));
                    break;
                case SHIFT:
                    System.out.println("Space pressed");
                    GameManager.getInstance().gameScene.resetSelectionAll();
                    turnManager.endPlayerTurn();
                    GUIManager.getInstance().disableButton();
                    break;
                case W:
                case A:
                case S:
                case D:
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:

                    if (player.canAct()) {
                        if (!isPlayerPieceSelected) {
                            resetSelectionAll();
                            isPlayerPieceSelected = true;
                            MovementHandler.showValidMoves(player.getRow(), player.getCol());
                        } else {
                            // Determine direction based on key pressed
                            int rowDelta = 0;
                            int colDelta = 0;
                            switch (event.getCode()) {
                                case W:
                                case UP:
                                    rowDelta = -1;
                                    break;
                                case A:
                                case LEFT:
                                    colDelta = -1;
                                    break;
                                case S:
                                case DOWN:
                                    rowDelta = 1;
                                    break;
                                case D:
                                case RIGHT:
                                    colDelta = 1;
                                    break;
                            }

                            int rowToMove = player.getRow() + rowDelta;
                            int colToMove = player.getCol() + colDelta;

                            resetSelection(0); // DON'T MOVE THIS LINE, it's for unavailable cursor to work properly
                            if (validMovesCache[rowToMove][colToMove] && player.validMove(rowToMove, colToMove) && piecesPosition[rowToMove][colToMove] == null) {
                                // Move the player
                                MovementHandler.movePlayer(player.getRow() + rowDelta, player.getCol() + colDelta);
                            } else {
                                SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                                System.out.println("Invalid move");
                            }
                        }
                    }
                    break;
                case V:
                    if (player.canAct() && !GUIManager.getInstance().isInAttackMode) {
                        // Cancel skill selection if skill is selected
                        resetSelectionAll();

                        GUIManager.getInstance().isInAttackMode = true;
                        GUIManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
                        AttackHandler.showValidAttackRange(GameManager.getInstance().player.getRow() , GameManager.getInstance().player.getCol());
                    }
                    break;
                case DIGIT1:
                    handleSkillShortcut(1);
                    break;
                case DIGIT2:
                    handleSkillShortcut(2);
                    break;
                case DIGIT3:
                    handleSkillShortcut(3);
                    break;
                case DIGIT4:
                    handleSkillShortcut(4);
                    break;
                case DIGIT5:
                    handleSkillShortcut(5);
                    break;
                case DIGIT6:
                    handleSkillShortcut(6);
                    break;
                case DIGIT7:
                    handleSkillShortcut(7);
                    break;
                case DIGIT8:
                    handleSkillShortcut(8);
                    break;
                case F1:
                    gameManager.playerMoney += 1000;
                    GUIManager.getInstance().updateGUI();
                    break;
                case F2:
                    generateNewFloor();
                    break;
                case F3:
                    for (int i = 0; i < Config.BOARD_SIZE; i++) {
                        for (int j = 0; j < Config.BOARD_SIZE; j++) {
                            if (piecesPosition[i][j] != null && !(piecesPosition[i][j] instanceof BaseWallPiece)) {
                                System.out.println("There is " + piecesPosition[i][j] + "at " + i + " " + j);
                                GUIManager.getInstance().eventLogDisplay.addLog("There is " + piecesPosition[i][j] + "at " + i + " " + j);
                            }
                        }
                    }
                    break;
                case F4:
                    GameManager.getInstance().player.setCurrentHealth(GameManager.getInstance().player.getMaxHealth());
                    GameManager.getInstance().player.setCurrentActionPoint(GameManager.getInstance().player.getMaxActionPoint());
                    GameManager.getInstance().player.setCurrentMana(GameManager.getInstance().player.getMaxMana());
                    break;
                case F5:
                    for (int i = 0; i < gameManager.inventory.size(); i++) {
                        System.out.println("Inventory[" + i + "] is " + gameManager.inventory.get(i).getName());
                        GUIManager.getInstance().eventLogDisplay.addLog("Inventory[" + i + "] is " + gameManager.inventory.get(i).getName());
                    }
                    break;
                case F6:
                    GameManager.getInstance().GameOver();
                    break;
            }
        });
    }

    public void generateNewFloor() {
        GameManager.getInstance().dungeonLevel += 1;
        currentLevelText.setText(String.valueOf(GameManager.getInstance().dungeonLevel));

        // switch the floor back to normal
        for (Point2D coordinate : gameManager.doorAt) {
            int row = (int) coordinate.getX();
            int col = (int) coordinate.getY();

            dungeonFloor[row][col].setImage(ImageScaler.resample(new Image(Config.FloorPath), 2));
        }
        gameManager.doorAt.clear();

        removeElements();

        if (GameManager.getInstance().dungeonLevel % 10 == 0) {
            BossRoom1();
        } else if (GameManager.getInstance().dungeonLevel % 5 == 0) {
            safeRoom();
        } else {
            normalRoom();
        }
    }

    private void normalRoom() {
        player.setCurrentActionPoint(player.getMaxActionPoint());
        dungeonGenerator.generateDungeon();
        placeDungeon(dungeonGenerator.getDungeonLayout());
        placeEntityRandomly(player);
        precomputeValidMoves();
        initializeEnvironment();
        initFog(fogPane);
    }

    private void safeRoom() {
        placeDungeon(Config.safeRoom);

        fogPane.getChildren().clear();

        Dealer dealer = new Dealer();
        dealer.setRow(7);
        dealer.setCol(9);

        player.setRow(10);
        player.setCol(6);
        player.setCurrentActionPointForce(999); // so player can move freely
        precomputeValidMoves();

        SpawnerManager.getInstance().spawnDoor(10, 13);

        placePiece(player);
        placePiece(dealer);
        piecesPosition[7][9] = dealer;

        // clear entity
        environmentPieces.clear();
    }

    private void BossRoom1() {
        placeDungeon(Config.BossRoom1);

        fogPane.getChildren().clear();


        SlimeBoss slimeBoss = new SlimeBoss();
        slimeBoss.setRow(9);
        slimeBoss.setCol(9);

        player.setRow(13);
        player.setCol(6);
        player.setCurrentActionPoint(5);
        player.setMaxActionPoint(5);

        precomputeValidMoves();

        placePiece(player);
        placePiece(slimeBoss);
        piecesPosition[9][9] = slimeBoss;

        // clear entity
        environmentPieces.clear();

        // set gameManager
        GameManager.getInstance().environmentPieces.add(slimeBoss);
    }

    public void resetSelectionAll() {
        resetSelection(0);
        resetSelection(1);
        resetSelection(2);
        resetSelection(3);
    }

    private void handleSkillShortcut(int slot) {
        BaseSkill toUse = player.getSkills()[slot-1];

        if (toUse != null) {
            GameManager.getInstance().gameScene.resetSelection(0);
            GameManager.getInstance().gameScene.resetSelection(1);
            GameManager.getInstance().gameScene.resetSelection(3);

            // trigger skill selection
            if (GameManager.getInstance().selectedSkill != null) {
                if (GameManager.getInstance().selectedSkill == toUse && toUse.castOnSelf() && GameManager.getInstance().fastUse) {
                    boolean enoughMana = player.getCurrentMana() >= GameManager.getInstance().selectedSkill.getManaCost();
                    boolean enoughActionPoint = player.getCurrentActionPoint() >= GameManager.getInstance().selectedSkill.getActionPointCost();

                    if (enoughMana && enoughActionPoint) {
                        GUIManager.getInstance().eventLogDisplay.addLog("Player use " + GameManager.getInstance().selectedSkill.getName(), GameManager.getInstance().selectedSkill.getNameColor());
                        GameManager.getInstance().selectedSkill.perform(GameManager.getInstance().player);
                    } else {
                        SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                        System.out.println("Not enough mana or action point");
                    }

                    GameManager.getInstance().gameScene.resetSelection(2);
                    return;
                }
                GameManager.getInstance().gameScene.resetSelection(2);
            }

            // show range
            SkillHandler.showValidSkillRange(player.getRow(), player.getCol(), toUse);
            GUIManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.HandCursor);
            gameManager.selectedSkill = toUse;
            toUse.getFrame().setImage(ImageScaler.resample(new Image(Config.FrameSelectedPath), 2));
            System.out.println("Selected " + toUse.getName() + " skill");
        }
    }
}
