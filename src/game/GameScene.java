package game;

import items.BaseItem;
import items.EmptyFrame;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;


import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.util.Duration;

import logic.*;
import logic.handlers.*;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.enemies.*;
import pieces.npcs.BaseNpcPiece;
import pieces.npcs.Dealer;
import pieces.player.*;
import pieces.wall.*;
import utils.Config;
import utils.Usable;

import java.util.ArrayList;
import java.util.List;

public class GameScene {
    private static final int BOARD_SIZE = Config.BOARD_SIZE;
    private static final int SQUARE_SIZE = Config.SQUARE_SIZE;
    private static final int GAME_SIZE = Config.GAME_SIZE;

    private GameManager gameManager = GameManager.getInstance();
    private BasePlayerPiece player;
    private GUIManager guiManager;
    private GameLoop gameLoop;
    private TurnManager turnManager;
    private ImageScaler imageScaler = new ImageScaler();
    private DungeonGenerator dungeonGenerator;
    private Timeline autoCycleTurn;



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
    private boolean autoCycle = false;
    private TileMap wallOnFloorTileMap;



    //------------<UI>----------------------------------------------------
    private Scene scene;
    private Pane animationPane = gameManager.animationPane;
    private GridPane boardPane = gameManager.boardPane;
    private GridPane tilePane = new GridPane();
    private BorderPane root;
    private VBox rightPane; // Contain right side UI
    private VBox leftPane; // Contain left side UI
    private StackPane centerPane; // Contain the game board

    private Runnable renderLogic;
    private Runnable updateLogic;


    public GameScene() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #1c0a05;");
        scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/CSSs/BottomLeftGUI.css").toExternalForm());


        //TODO: test auto-tiling tilemap
        wallOnFloorTileMap = new TileMap(new Image(Config.WallOnFloorPath),4,8,32,32);
        tilePane.setMinSize(GAME_SIZE,GAME_SIZE);
        tilePane.setMaxSize(GAME_SIZE,GAME_SIZE);
        tilePane.setDisable(true);
        for(int i = 0 ; i < 20 ; i++){
            //init grid size for tilePane
            tilePane.getColumnConstraints().add(new ColumnConstraints(32));
            tilePane.getRowConstraints().add(new RowConstraints(32));
        }
        //TODO============================

        //this pane contains all animation-related nodes
        //it's placed transparently over boardPane
        animationPane.setMaxWidth(SQUARE_SIZE*BOARD_SIZE);
        animationPane.setMaxHeight(SQUARE_SIZE*BOARD_SIZE);
        animationPane.setDisable(true);


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
        centerPane.getChildren().addAll(boardPane , tilePane , animationPane);

        boardPane.setBackground(Background.fill(Color.GOLD));
        root.setCenter(centerPane);

        gameStart();
        // Add game area and GUI panes to the root BorderPane
        root.setRight(rightPane);
        rightPane.getChildren().add(guiManager.getRightSideUI());

        root.setLeft(leftPane);
        leftPane.getChildren().addAll(guiManager.getPlayerOptionsMenu());
        leftPane.setPadding(new Insets(10));

        // Define update logic
        updateLogic = () -> {
            // Update game state
            if (gameManager.doorAt != null) {
                if (player.getRow() == gameManager.doorAt.getX() && player.getCol() == gameManager.doorAt.getY()) {
                    System.out.println("New Floor");
                    generateNewFloor();

                    // switch the floor back to normal
                    dungeonFloor[(int) gameManager.doorAt.getX()][(int) gameManager.doorAt.getY()]
                            .setImage(new Image(Config.FloorPath));

                    // reset doorAt to null
                    gameManager.doorAt = null;
                }
            }
        };
        // Right click anywhere in the scene to cancel/deselect anything
        scene.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (GUIManager.getInstance().isInAttackMode) {
                    resetSelection(1);
                } else if (gameManager.selectedSkill != null) {
                    resetSelection(2);
                } else if (gameManager.selectedItem != null) {
                    resetSelection(3);
                } else {
                    resetSelection(0);
                }
            }
        });
        // Define render logic
        renderLogic = () -> {
            // Render game graphics
        };

        // Create an instance of GameLoop with the update and render logic
        gameLoop = new GameLoop(updateLogic, renderLogic);
        gameLoop.start();


        // Set up the scene and stage
        GUIManager.getInstance().updateCursor(scene, Config.DefaultCursor);
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

        SpawnerManager.getInstance().randomMonsterSpawnFromPool(monsterPool1, environmentPieces, 5);

        for (BasePiece entity : environmentPieces) {
            entity.getTexture().setOnMouseClicked(mouseEvent -> handleSquareClick(entity.getRow(), entity.getCol()));
            placeEntityRandomly(entity);
        }

        placeEntityRandomly(new Dealer());
    }

    private void gameStart() {
        // Initialize player at starting position
        player = GameManager.getInstance().player;
        dungeonGenerator = new DungeonGenerator(); // Initialize DungeonGenerator
        dungeonGenerator.generateDungeon(); // Generate dungeon
        placeDungeon();
        placeEntityRandomly(player);
        precomputeValidMoves();
        initializeEnvironment();

        turnManager = TurnManager.getInstance();

        guiManager = GUIManager.getInstance();

        turnManager.startPlayerTurn();
    }

    private void initFloor(GridPane gridPane) {
        // Generate the dungeon floor according to BOARD_SIZE
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView floor = new ImageView();

                // set floor image into square size
                floor.setFitWidth(SQUARE_SIZE);
                floor.setFitHeight(SQUARE_SIZE);

                floor.setImage(imageScaler.resample(new Image(Config.FloorPath), 2)); // Set texture of dungeon floor
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

    private void placeDungeon() {
        // Place the walls according to dungeon generated
        char[][] dungeonLayout = dungeonGenerator.getDungeonLayout();
        //make duplicate dungeonLayout of size + 1 , to take account of null border
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

        for (int row = 1; row < expandedLayout.length-1 ; row++) {
            for (int col = 1; col < expandedLayout[0].length-1 ; col++) {
                if (expandedLayout[row][col] == '#') {
                    //assign new wall object to piecesPosition & add texture to tilePane
                    piecesPosition[row-1][col-1] = new BaseWallPiece(row-1, col-1);
                    // |1|1|2| - tile around has its bit value
                    // |2|X|4| - bitMask1 -> adjacent
                    // |4|8|8| - bitMask2 -> corner
                    int bitMask1 = 0;
                    if(expandedLayout[row-1][col]== '#') bitMask1+=1;
                    if(expandedLayout[row][col-1]== '#') bitMask1+=2;
                    if(expandedLayout[row][col+1]== '#') bitMask1+=4;
                    if(expandedLayout[row+1][col]== '#') bitMask1+=8;

                    if(bitMask1 == 15){ //all adjacent is wall now check corner
                        int bitMask2 = 0;
                        if(expandedLayout[row-1][col-1] == '#')
                            bitMask2+=1;
                        if(expandedLayout[row-1][col+1]== '#')
                            bitMask2+=2;
                        if(expandedLayout[row+1][col-1]== '#')
                            bitMask2+=4;
                        if(expandedLayout[row+1][col+1]== '#')
                            bitMask2+=8;
                        tilePane.add( ((BaseWallPiece)(piecesPosition[row-1][col-1])).getTileMap().getTileAt(bitMask2/4 , 4 + bitMask2%4 ) , col-1 , row-1);
                        continue;
                    }
                    tilePane.add( ((BaseWallPiece)(piecesPosition[row-1][col-1])).getTileMap().getTileAt(bitMask1/4 , bitMask1%4) , col-1 , row-1);
                }
            }
        }
    }

    private void placePiece(BasePiece piece) {
        //this method is called after generated dungeon
        //place Piece to the board

        if(piece instanceof BasePlayerPiece playerPiece){
            //setup player image size
            playerPiece.animationImage.setFitWidth(SQUARE_SIZE);
            //set position
            playerPiece.animationImage.setX(piece.getCol()*SQUARE_SIZE + playerPiece.offsetX);
            playerPiece.animationImage.setY(piece.getRow()*SQUARE_SIZE + playerPiece.offsetY);
            //add player sprite to animation pane
            animationPane.getChildren().add(playerPiece.animationImage);

            GameManager.getInstance().animationPane.getChildren().add(this.player.meleeAttackImage);
        }
        else if (piece instanceof BaseMonsterPiece monsterPiece) {
            //setup monster image size
            monsterPiece.animationImage.setFitWidth(SQUARE_SIZE);
            //set position
            monsterPiece.animationImage.setX(piece.getCol() * SQUARE_SIZE + monsterPiece.getOffsetX());
            monsterPiece.animationImage.setY(piece.getRow() * SQUARE_SIZE + monsterPiece.getOffsetY());
            //add monster sprite to animation pane
            animationPane.getChildren().add(monsterPiece.animationImage);
        } else if (piece instanceof BaseNpcPiece npcPiece) {
            //setup npc image size
            npcPiece.animationImage.setFitWidth(SQUARE_SIZE);
            //set position
            npcPiece.animationImage.setX(piece.getCol() * SQUARE_SIZE + npcPiece.getOffsetX());
            npcPiece.animationImage.setY(piece.getRow() * SQUARE_SIZE + npcPiece.getOffsetY());
            //add npc sprite to animation pane
            animationPane.getChildren().add(npcPiece.animationImage);
        }
        //TODO: if piece an instance of Object

    }

    private void setupMouseEvents() {
        // Add mouse event for each square
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row; // Make row effectively final
                final int currentCol = col; // Make col effectively final
                ImageView square = dungeonFloor[row][col];
                square.setOnMouseClicked(event -> {
                    if(event.getButton() == MouseButton.PRIMARY){
                        //left click for moving & attack
                        handleSquareClick(currentRow, currentCol);

                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        //TODO : right click to inspect environment
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
            System.out.println("Not on your turn");
            if (GUIManager.getInstance().isInAttackMode)
                resetSelection(1);
            if (GameManager.getInstance().selectedSkill != null)
                resetSelection(2);
            if (GameManager.getInstance().selectedItem != null)
                resetSelection(3);
            return;
        }

        // ------------------------- Attack Mode -------------------------

        boolean isInAttackMode = GUIManager.getInstance().isInAttackMode;

        if (isInAttackMode) {
            System.out.println("Player Prepare to attack");
            boolean enoughActionPoint = player.getCurrentActionPoint() >= gameManager.selectedSkill.getActionPointCost();

            // Check if the clicked square is within showValid attack range
            if (player.validAttack(row, col)) {
                // Check if there is a monster on the clicked square
                if (piecesPosition[row][col] instanceof BaseMonsterPiece monsterPiece) {
                    // Perform the attack on the monster
                    System.out.println("Player attack " + monsterPiece.getClass().getSimpleName() + " @ " + row + " " + col);
                    player.attack(monsterPiece);
                    exitAttackMode();
                    if (!monsterPiece.isAlive()) {
                        removePiece(monsterPiece);
                        environmentPieces.remove(monsterPiece);
                    }
                }
            } else {
                // Player clicked outside valid attack range, exit attack mode
                exitAttackMode();
            }
            return;
        }

        // ------------------------- Skill Mode -------------------------

        if (gameManager.selectedSkill != null) {
            boolean enoughMana = player.getCurrentMana() >= gameManager.selectedSkill.getManaCost();
            boolean enoughActionPoint = player.getCurrentActionPoint() >= gameManager.selectedSkill.getActionPointCost();

            if (gameManager.selectedSkill.validRange(row, col)) {
                // Check if there is a monster on the clicked square
                if (piecesPosition[row][col] instanceof BaseMonsterPiece monsterPiece) {
                    // Perform the attack on the monster
                    if (enoughMana && enoughActionPoint) {
                        gameManager.selectedSkill.perform(monsterPiece);
                    } else {
                        System.out.println("Not enough mana or action point");
                    }
                    resetSelection(2);
                    if (!monsterPiece.isAlive()) {
                        removePiece(monsterPiece);
                        environmentPieces.remove(monsterPiece);
                    }
                } else if (piecesPosition[row][col] instanceof BasePlayerPiece playerPiece) {
                    if (gameManager.selectedSkill.castOnSelf()) {

                        if (enoughMana && enoughActionPoint) {
                            gameManager.selectedSkill.perform(playerPiece);
                        } else {
                            System.out.println("Not enough mana or action point");
                        }
                        resetSelection(2);
                    }
                }
            } else {
                // Cancel skill selection
                resetSelection(2);
            }
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

                            usableItem.useItem(monsterPiece);
                            resetSelection(3);

                            // throw away after use
                            GUIManager.getInstance().inventoryDisplay.throwAwayItem(item);
                        }
                    } else if (target instanceof BasePlayerPiece playerPiece) {
                        // use item on player
                        if (((Usable) item).castOnSelf()) {

                            usableItem.useItem(playerPiece);
                            resetSelection(3);
                            // throw away after use
                            GUIManager.getInstance().inventoryDisplay.throwAwayItem(item);
                        }
                    } else {
                        // cancel selection
                        resetSelection(3);
                    }
                } else {
                    // reset selection when not in valid range
                    resetSelection(3);
                }
            } else {
                // reset selection when clicked outside for other
                resetSelection(3);
            }

            return;
        }

        // ------------------------- Movement Mode -------------------------

        if (player.getRow() == row && player.getCol() == col) {
            // toggle move selection mode by click on player's grid
            // Show valid moves by changing the color of adjacent squares
            isPlayerPieceSelected = !isPlayerPieceSelected;
            if(isPlayerPieceSelected) MovementHandler.showValidMoves(row, col);
                else resetSelection(0);

        } else if (isPlayerPieceSelected) {
            if (validMovesCache[row][col] && player.validMove(row, col) && piecesPosition[row][col] == null) {
                System.out.println("Moving player to square (" + row + ", " + col + ")");
                MovementHandler.movePlayer(row, col);
            } else {
                System.out.println("Invalid move");
            }
            resetSelection(0);
        }
    }

    private boolean isValidMove(int row, int col) {
        if (piecesPosition[row][col] instanceof BaseWallPiece) {
            return false; // Destination square contains a wall, invalid move
        }
        return true;
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
            for (int i = 0  ; i < selectedMoveTiles.size() ; i++){
                selectionFloor[(int) selectedMoveTiles.get(i).getX()][(int) selectedMoveTiles.get(i).getY()]
                        .setImage(null);

            }
            selectedMoveTiles.clear();

        }
        else if(type == 1){
            //reset attack Selected Tiles
            for (int i = 0  ; i < selectedAttackTiles.size() ; i++){
                selectionFloor[(int) selectedAttackTiles.get(i).getX()][(int) selectedAttackTiles.get(i).getY()]
                        .setImage(null);
            }
            selectedAttackTiles.clear();

        }
        else if (type == 2) {
            //reset skill Selected Tiles
            for (int i = 0  ; i < selectedSkillTiles.size() ; i++){
                selectionFloor[(int) selectedSkillTiles.get(i).getX()][(int) selectedSkillTiles.get(i).getY()]
                        .setImage(null);
            }
            selectedSkillTiles.clear();
            if (gameManager.selectedSkill != null)
                guiManager.deselectFrame(gameManager.selectedSkill.getFrame());
            gameManager.selectedSkill = null;
        } else if (type == 3) {
            //reset item Selected Tiles
            for (int i = 0  ; i < selectedItemTiles.size() ; i++){
                selectionFloor[(int) selectedItemTiles.get(i).getX()][(int) selectedItemTiles.get(i).getY()]
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

        if (entity instanceof BasePlayerPiece) {
            entity.getTexture().setOnMouseClicked(event -> handleSquareClick(entity.getRow(), entity.getCol()));
        }

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

    private void removePiece(BasePiece toRemove) {
        int row = toRemove.getRow();
        int col = toRemove.getCol();

        // Remove the piece's ImageView from the boardPane
        boardPane.getChildren().remove(toRemove.getTexture());

        // Remove the piece's ImageView from the animationPane
        if (toRemove instanceof BaseMonsterPiece monsterPiece)
            animationPane.getChildren().remove(monsterPiece.animationImage);

        // TODO: When there are more types this may have to be added

        // Set the corresponding entry in the pieces array to null
        piecesPosition[row][col] = null;
    }

    private void setupKeyEvents(Scene scene) {
        // Debug tool
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F1:
                    removeElements();
                    break;
                case F2:
                    generateNewFloor();
                    break;
                case F3:
                    for (int i = 0; i < Config.BOARD_SIZE; i++) {
                        for (int j = 0; j < Config.BOARD_SIZE; j++) {
                            if (piecesPosition[i][j] != null && !(piecesPosition[i][j] instanceof BaseWallPiece)) {
                                System.out.println("There is " + piecesPosition[i][j] + "at " + i + " " + j);
                            }
                        }
                    }
                    break;
                case F4:
                    autoCycle = !autoCycle;
                    if (autoCycle) {
                        startAutoCycle();
                    } else {
                        stopAutoCycle();
                    }
                    break;
                case F5:
                    for (int i = 0; i < gameManager.inventory.size(); i++) {
                        System.out.println("Inventory[" + i + "] is " + gameManager.inventory.get(i).getName());
                    }
                    break;
            }
        });
    }

    private void generateNewFloor() {
        removeElements();
        dungeonGenerator.generateDungeon();
        placeDungeon();
        placeEntityRandomly(player);
        precomputeValidMoves();
        initializeEnvironment();
    }

    private void startAutoCycle() {
        double delay = 1;
        autoCycleTurn = new Timeline(new KeyFrame(Duration.seconds(delay), cycle -> {
            if (turnManager.isPlayerTurn) turnManager.endPlayerTurn();
        }));
        autoCycleTurn.setCycleCount(Timeline.INDEFINITE);
        autoCycleTurn.play();
    }

    private void stopAutoCycle() {
        if (autoCycleTurn != null) {
            autoCycleTurn.stop();
        }
    }

    public void exitAttackMode() {
        GUIManager.getInstance().isInAttackMode = false;
        resetSelection(1);
        GUIManager.getInstance().updateCursor(scene, Config.DefaultCursor);
    }

}
