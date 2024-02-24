package game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.util.Duration;

import javafx.util.Pair;
import logic.*;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.enemies.*;
import pieces.player.*;
import pieces.wall.*;
import utils.Config;

import java.awt.event.MouseEvent;
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

    private ImageView[][] squares = new ImageView[BOARD_SIZE][BOARD_SIZE]; // The dungeon floor texture
    private ArrayList<Point2D> selectedAttackTiles = new ArrayList<>();
    private ArrayList<Point2D> selectedMoveTiles = new ArrayList<>();
    private boolean[][] validMovesCache = new boolean[BOARD_SIZE][BOARD_SIZE]; // Valid moves without entity
    private BasePiece[][] pieces = GameManager.getInstance().pieces; // Where each entity locate
    private List<BasePiece> environmentPieces = gameManager.environmentPieces; // List of all environment pieces (monsters and traps)
    private int newDirection = 1;
    private int bufferDirection = newDirection;
    private boolean isPieceSelected = false;
    private boolean autoCycle = false;

    //------------<UI>----------------------------------------------------

    private Scene scene;
    private Pane animationPane = gameManager.animationPane;
    private GridPane boardPane = gameManager.boardPane;
    private BorderPane root;
    private VBox rightPane;
    private VBox leftPane;
    private StackPane centerPane;

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
            if (gameManager.isInAttackMode) {
                showValidAttackRange(player.getRow(), player.getCol());
            }
        };

        // Create an instance of GameLoop with the update and render logic
        gameLoop = new GameLoop(updateLogic, renderLogic);
        gameLoop.start();


        // Set up the scene and stage
        gameManager.updateCursor(scene, Config.DefaultCursor);
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

        turnManager = gameManager.turnManager;

        guiManager = gameManager.guiManager;

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
                squares[row][col] = square;
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
                    pieces[row][col] = wall;
                }
            }
        }
    }

    private void placePiece(BasePiece piece) {
        //this method is called after generated dungeon
        //place Piece to the board
        if(piece instanceof BasePlayerPiece){
            //setup player image size
            ImageView pieceView = ((BasePlayerPiece) piece).animationImage;
            pieceView.setFitWidth(SQUARE_SIZE);
            pieceView.setFitHeight(SQUARE_SIZE);
            //set position
            ((BasePlayerPiece) piece).animationImage.setX(piece.getCol()*SQUARE_SIZE);
            ((BasePlayerPiece) piece).animationImage.setY(piece.getRow()*SQUARE_SIZE);
            //add player sprite to animation pane
            animationPane.getChildren().add(((BasePlayerPiece) piece).animationImage);
            GameManager.getInstance().animationPane.getChildren().add(player.meleeAttackImage);

        }
        else if (piece instanceof BaseMonsterPiece) {
            //setup monster image size
            ImageView pieceView = ((BaseMonsterPiece) piece).animationImage;
            pieceView.setFitWidth(SQUARE_SIZE);
            pieceView.setFitHeight(SQUARE_SIZE);
            //set position
            ((BaseMonsterPiece) piece).animationImage.setX(piece.getCol() * SQUARE_SIZE);
            ((BaseMonsterPiece) piece).animationImage.setY(piece.getRow() * SQUARE_SIZE);
            //add monster sprite to animation pane
            animationPane.getChildren().add(((BaseMonsterPiece) piece).animationImage);
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
                ImageView square = squares[row][col];
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

        boolean isInAttackMode = gameManager.isInAttackMode;

        if (isInAttackMode) {
            System.out.println("Player Prepare to attack");
            // Check if the clicked square is within showValid attack range
            if (player.validAttack(row, col)) {
                // Check if there is a monster on the clicked square
                if (pieces[row][col] instanceof BaseMonsterPiece monsterPiece) {
                    // Perform the attack on the monster
                    System.out.println("Player attack " + monsterPiece.getClass().getSimpleName() + " @ " + row + " " + col);
                    player.attack(monsterPiece);
                    exitAttackMode();
                    if (!monsterPiece.isAlive()) removePiece(monsterPiece);
                }
            } else {
                // Player clicked outside valid attack range, exit attack mode
                exitAttackMode();
            }

            return;
        }

        if (player.getRow() == row && player.getCol() == col) {
            // toggle move selection mode by click on player's grid
            // Show valid moves by changing the color of adjacent squares
            isPieceSelected = !isPieceSelected;
            if(isPieceSelected) showValidMoves(row, col);
            else resetSelection(0);

        } else if (isPieceSelected) {
            if (validMovesCache[row][col] && player.validMove(row, col) && pieces[row][col] == null) {
                System.out.println("Moving player to square (" + row + ", " + col + ")");
                movePlayer(row, col);
            } else {
                System.out.println("Invalid move");
            }
            resetSelection(0);
        }
    }

    private void movePlayer(int row, int col) {
        if (Config.MOVE_ACTIONPOINT > player.getCurrentActionPoint()) {
            System.out.println("Not enough Action Point");
            return;
        }

        player.decreaseActionPoint(Config.MOVE_ACTIONPOINT);

        newDirection = Integer.compare(col, player.getCol());
        if (bufferDirection != newDirection) {
            player.changeDirection(newDirection);
            bufferDirection = newDirection;
        }

        //move player across tiles
        player.moveWithTransition(row , col);

        pieces[player.getRow()][player.getCol()] = null;
        pieces[row][col] = player;

        player.setRow(row);
        player.setCol(col);
    }

    private boolean isValidMove(int row, int col) {
        if (pieces[row][col] instanceof BaseWallPiece) {
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

    private void showValidMoves(int row, int col) {
        // Iterate over adjacent squares and update images based on cached valid moves
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                // Check if the new position is within the board bounds and not the current position
                if (isValidPosition(newRow, newCol) && (newRow != row || newCol != col)) {
                    if (validMovesCache[newRow][newCol] && pieces[newRow][newCol] == null) {
                        selectedMoveTiles.add(new Point2D(newRow , newCol));
                        squares[newRow][newCol].setImage(imageScaler.resample(new Image(Config.ValidMovePath), 2)); // Set texture to indicate valid move
                    }
                }
            }
        }
    }

    private void showValidAttackRange(int row, int col) {
        int attackRange = 1; // Change this according to the player's attack range

        for (int dRow = -attackRange; dRow <= attackRange; dRow++) {
            for (int dCol = -attackRange; dCol <= attackRange; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                // Check if the new position is within the board bounds and not the current position
                if (isValidPosition(newRow, newCol) && (newRow != row || newCol != col)) {
                    // Check if the square is within the attack range using the player's validAttack method
                    if (player.validAttack(newRow, newCol)) {
                        // Highlight or mark the square to indicate it's within the attack range
                        selectedAttackTiles.add(new Point2D(newRow , newCol));
                        squares[newRow][newCol].setImage(imageScaler.resample(new Image(Config.ValidAttackPath), 2)); // Set texture to indicate valid attack
                    }
                }
            }
        }
    }


    public void resetSelection(int type) {
    //reset selection indicator for each type
        isPieceSelected = false;

        if(type == 0){//reset move selection
            // Reset the texture of all squares to the default floor texture
            for (int i = 0  ; i < selectedMoveTiles.size() ; i++){
                squares[(int) selectedMoveTiles.get(i).getX()][(int) selectedMoveTiles.get(i).getY()]
                        .setImage(new Image(Config.FloorPath));
            }
            selectedMoveTiles.clear();
        }
        else if(type == 1){//reset attack selection
            // Reset the texture of all squares to the default floor texture
            for (int i = 0  ; i < selectedAttackTiles.size() ; i++){
                squares[(int) selectedAttackTiles.get(i).getX()][(int) selectedAttackTiles.get(i).getY()]
                        .setImage(new Image(Config.FloorPath));
            }
            selectedAttackTiles.clear();
        }

    }

    private boolean isValidPosition(int row, int col) {
        // Check if the position is inside the board
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private void placeEntityRandomly(BasePiece entity) {
        int row, col;
        do {
            row = (int) (Math.random() * BOARD_SIZE);
            col = (int) (Math.random() * BOARD_SIZE);
        } while (!isValidMove(row, col) || pieces[row][col] != null);

        if (entity instanceof BasePlayerPiece) {
            entity.getTexture().setOnMouseClicked(event -> handleSquareClick(entity.getRow(), entity.getCol()));
        }

        entity.setRow(row);
        entity.setCol(col);
        pieces[row][col] = entity; // Mark the position as occupied
        placePiece(entity);
    }

    private void removeElements() {
        // Mostly for debugging purpose
        // to try re-generate the dungeon
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (pieces[row][col] != null) {
                    boardPane.getChildren().remove(pieces[row][col].getTexture());
                    pieces[row][col] = null;
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
        pieces[row][col] = null;
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
                            if (pieces[i][j] != null && !(pieces[i][j] instanceof BaseWallPiece)) {
                                System.out.println("There is " + pieces[i][j] + "at " + i + " " + j);
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

    private void exitAttackMode() {
        gameManager.isInAttackMode = false;
        resetSelection(1);
        gameManager.updateCursor(scene, Config.DefaultCursor);
    }

    private void exitInventoryMode() {
        gameManager.isInInventoryMode = false;
    }

    private void exitSkillMode() {
        gameManager.isInUseSkillMode = false;
    }
}
