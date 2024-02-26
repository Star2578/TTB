package game;

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
import pieces.player.*;
import pieces.wall.*;
import utils.Config;

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
    private boolean[][] validMovesCache = gameManager.validMovesCache; // Valid moves without entity
    private ImageView[][] dungeonFloor = gameManager.dungeonFloor; // The dungeon floor texture
    private BasePiece[][] piecesPosition = GameManager.getInstance().piecesPosition; // Where each entity locate
    private List<BasePiece> environmentPieces = gameManager.environmentPieces; // List of all environment pieces (monsters and traps)
    private boolean isPieceSelected = false;
    private boolean autoCycle = false;

    //------------<UI>----------------------------------------------------

    private Scene scene;
    private Pane animationPane = gameManager.animationPane;
    private GridPane boardPane = gameManager.boardPane;
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
        initGrid(boardPane);
        boardPane.setMinSize(GAME_SIZE, GAME_SIZE);
        boardPane.setMaxSize(GAME_SIZE, GAME_SIZE);

        // Center the game board using a StackPane
        centerPane = new StackPane();
        centerPane.getChildren().addAll(boardPane , animationPane);

        boardPane.setBackground(Background.fill(Color.GOLD));
        root.setCenter(centerPane);

        gameStart();
        // Add game area and GUI panes to the root BorderPane
        root.setRight(rightPane);
        rightPane.getChildren().add(guiManager.getRightSideUI());

        root.setLeft(leftPane);
        leftPane.getChildren().addAll(guiManager.getTurnOrderDisplay(), guiManager.getPlayerOptionsMenu());
        leftPane.setPadding(new Insets(10));

        // Define update logic
        updateLogic = () -> {
            // Update game state
        };

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
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));
        environmentPieces.add(new Zombie(0, 0, validMovesCache, 1));

        for (BasePiece entity : environmentPieces) {
            entity.getTexture().setOnMouseClicked(mouseEvent -> handleSquareClick(entity.getRow(), entity.getCol()));
            placeEntityRandomly(entity);
        }
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

    private void initGrid(GridPane gridPane) {
        // Generate the dungeon floor according to BOARD_SIZE
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView square = new ImageView();
                square.setFitWidth(SQUARE_SIZE);
                square.setFitHeight(SQUARE_SIZE);
                square.setImage(imageScaler.resample(new Image(Config.FloorPath), 2)); // Set texture of dungeon floor
                gridPane.add(square, col, row);
                dungeonFloor[row][col] = square;
            }
        }
    }

    private void placeDungeon() {
        // Place the walls according to dungeon generated
        char[][] dungeonLayout = dungeonGenerator.getDungeonLayout();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (dungeonLayout[row][col] == '#') {
                    BaseWallPiece wall = new BaseWallPiece(row, col);
                    placePiece(wall);
                    piecesPosition[row][col] = wall;
                }
            }
        }
    }

    private void placePiece(BasePiece piece) {
        //this method is called after generated dungeon
        //place Piece to the board
        if(piece instanceof BasePlayerPiece){
            //setup player image size
            BasePlayerPiece p = ((BasePlayerPiece) piece);
            p.animationImage.setFitWidth(SQUARE_SIZE);
            //set position
            p.animationImage.setX(piece.getCol()*SQUARE_SIZE + p.offsetX);
            p.animationImage.setY(piece.getRow()*SQUARE_SIZE + p.offsetY);
            //add player sprite to animation pane
            animationPane.getChildren().add(p.animationImage);

            GameManager.getInstance().animationPane.getChildren().add(player.meleeAttackImage);
        }
        else if (piece instanceof BaseMonsterPiece) {
            //setup monster image size
            BaseMonsterPiece m = ((BaseMonsterPiece) piece);
            m.animationImage.setFitWidth(SQUARE_SIZE);
            //set position
            m.animationImage.setX(piece.getCol() * SQUARE_SIZE + m.offsetX);
            m.animationImage.setY(piece.getRow() * SQUARE_SIZE + m.offsetY);
            //add monster sprite to animation pane
            animationPane.getChildren().add(m.animationImage);
        }
        else{
            ImageView pieceView = piece.getTexture();
            pieceView.setImage(imageScaler.resample(piece.getTexture().getImage(), 2));
            pieceView.setFitWidth(SQUARE_SIZE);
            pieceView.setFitHeight(SQUARE_SIZE);
            GridPane.setRowIndex(pieceView, piece.getRow()); // Set row index
            GridPane.setColumnIndex(pieceView, piece.getCol()); // Set column index
            boardPane.getChildren().add(pieceView); // Add piece to board
        }
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
    }

    private void handleSquareClick(int row, int col) {
        System.out.println("Clicked on square (" + row + ", " + col + ")");
        if (!player.canAct()) {
            System.out.println("Not on your turn");
            return;
        }

        // ------------------------- Attack Mode -------------------------

        boolean isInAttackMode = GUIManager.getInstance().isInAttackMode;

        if (isInAttackMode) {
            System.out.println("Player Prepare to attack");
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

        boolean isInUseSkillMode = GUIManager.getInstance().isInUseSkillMode;

        if (isInUseSkillMode && gameManager.selectedSkill != null) {
            if (gameManager.selectedSkill.validRange(row, col)) {
                // Check if there is a monster on the clicked square
                if (piecesPosition[row][col] instanceof BaseMonsterPiece monsterPiece) {
                    // Perform the attack on the monster
                    gameManager.selectedSkill.perform(monsterPiece);
                    resetSelection(2);
                    GUIManager.getInstance().skillSelectDisplay.updateSelectedSkillInfo();
                    if (!monsterPiece.isAlive()) {
                        removePiece(monsterPiece);
                        environmentPieces.remove(monsterPiece);
                    }
                }
            } else {
                // Cancel skill selection
                resetSelection(2);
                GUIManager.getInstance().skillSelectDisplay.updateSelectedSkillInfo();
            }
            return;
        }

        // ------------------------- Movement Mode -------------------------

        if (player.getRow() == row && player.getCol() == col) {
            // toggle move selection mode by click on player's grid
            // Show valid moves by changing the color of adjacent squares
            isPieceSelected = !isPieceSelected;
            if(isPieceSelected){
                MovementHandler.showValidMoves(row, col);
            }
            else resetSelection(0);

        } else if (isPieceSelected) {
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
        // Iterate over all squares to compute valid moves and cache them
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                validMovesCache[row][col] = isValidMove(row, col);
            }
        }
    }


    public void resetSelection(int type) {
        isPieceSelected = false;

        if(type == 0){
            //reset move Selected Tiles
            for (int i = 0  ; i < selectedMoveTiles.size() ; i++){
                dungeonFloor[(int) selectedMoveTiles.get(i).getX()][(int) selectedMoveTiles.get(i).getY()]
                        .setImage(new Image(Config.FloorPath));

            }
            selectedMoveTiles.clear();

        }
        else if(type == 1){//reset attack selection
            //reset attack Selected Tiles
            for (int i = 0  ; i < selectedAttackTiles.size() ; i++){
                dungeonFloor[(int) selectedAttackTiles.get(i).getX()][(int) selectedAttackTiles.get(i).getY()]
                        .setImage(new Image(Config.FloorPath));
            }
            selectedAttackTiles.clear();

        }
        else if (type == 2) {
            //reset skill Selected Tiles
            for (int i = 0  ; i < selectedSkillTiles.size() ; i++){
                dungeonFloor[(int) selectedSkillTiles.get(i).getX()][(int) selectedSkillTiles.get(i).getY()]
                        .setImage(new Image(Config.FloorPath));
            }
            selectedSkillTiles.clear();
            gameManager.selectedSkill = null;

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
                    removeElements();
                    dungeonGenerator.generateDungeon();
                    placeDungeon();
                    placeEntityRandomly(player);
                    precomputeValidMoves();
                    for (BasePiece entity : environmentPieces) {
                        placeEntityRandomly(entity);
                    }
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
            }
        });
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
