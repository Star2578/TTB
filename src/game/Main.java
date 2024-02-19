package game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.*;
import pieces.BasePiece;
import pieces.enemies.*;
import pieces.player.*;
import pieces.wall.*;
import utils.Config;
import utils.GUIManager;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static final int BOARD_SIZE = Config.BOARD_SIZE;
    private static final int SQUARE_SIZE = Config.SQUARE_SIZE;
    private static final int GAME_SIZE = Config.GAME_SIZE;
    private GameManager gameManager = GameManager.getInstance();
    private GridPane boardPane = gameManager.boardPane;
    private ImageView[][] squares = new ImageView[BOARD_SIZE][BOARD_SIZE];
    private BasePiece[][] pieces = GameManager.getInstance().pieces;
    private boolean[][] validMovesCache = new boolean[BOARD_SIZE][BOARD_SIZE];
    private DungeonGenerator dungeonGenerator;
    private BasePlayerPiece player;
    private int playerRow = 0;
    private int playerCol = 0;
    private boolean isPieceSelected = false;
    private GUIManager guiManager;
    private List<BasePiece> environmentPieces = new ArrayList<>(); // List of all environment pieces (monsters and traps)
    private TurnManager turnManager;
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1c0a05;");
        VBox rightPane = new VBox(); // Pane for top area
        rightPane.setBackground(Background.fill(Color.DARKRED));
        VBox leftPane = new VBox(); // Pane for bottom GUI
        leftPane.setBackground(Background.fill(Color.DARKCYAN));

        // Create the main game area
        initGrid(boardPane);
        boardPane.setMinSize(GAME_SIZE, GAME_SIZE);
        boardPane.setMaxSize(GAME_SIZE, GAME_SIZE);

        // Center the game board using a StackPane
        StackPane centerPane = new StackPane();
        centerPane.getChildren().add(boardPane);
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
        Runnable updateLogic = () -> {
            // Update game state
        };

        // Define render logic
        Runnable renderLogic = () -> {
            // Render game graphics
        };

        // Create an instance of GameLoop with the update and render logic
        GameLoop gameLoop = new GameLoop(updateLogic, renderLogic, turnManager);
        gameLoop.start();

        // Set up the scene and stage
        Scene gameScene = new Scene(root, 1280, 720);
        SceneManager.getInstance().setGameScene(gameScene);
        setupMouseEvents();
        setupKeyEvents(gameScene); // Debug Tool
        primaryStage.setResizable(false);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Dungeon Crawler");
        primaryStage.show();
    }

    private void initializeEnvironment() {
        // Add environment pieces (monsters and traps) to the list
        environmentPieces.add(new Zombie(0, 0, validMovesCache));
        environmentPieces.add(new Zombie(0, 0, validMovesCache));
        environmentPieces.add(new Zombie(0, 0, validMovesCache));
        environmentPieces.add(new Zombie(0, 0, validMovesCache));
        environmentPieces.add(new Zombie(0, 0, validMovesCache));
        environmentPieces.add(new Zombie(0, 0, validMovesCache));
        environmentPieces.add(new Zombie(0, 0, validMovesCache));

        for (BasePiece entity : environmentPieces) {
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

        // Initialize TurnManager after setting up player and environment
        turnManager = new TurnManager(player, environmentPieces);

        guiManager = new GUIManager(turnManager);

        turnManager.startPlayerTurn();
    }

    private void initGrid(GridPane gridPane) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView square = new ImageView();
                square.setFitWidth(SQUARE_SIZE);
                square.setFitHeight(SQUARE_SIZE);
                square.setImage(new Image(Config.FloorPath)); // Set default texture
                gridPane.add(square, col, row);
                squares[row][col] = square;
            }
        }
    }

    private void placeDungeon() {
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
        ImageView pieceView = piece.getTexture();
        pieceView.setFitWidth(SQUARE_SIZE);
        pieceView.setFitHeight(SQUARE_SIZE);

        GridPane.setRowIndex(pieceView, piece.getRow()); // Set row index
        GridPane.setColumnIndex(pieceView, piece.getCol()); // Set column index
        boardPane.getChildren().add(pieceView); // Add piece to board
    }

    private void setupMouseEvents() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row; // Make row effectively final
                final int currentCol = col; // Make col effectively final
                ImageView square = squares[row][col];
                square.setOnMouseClicked(event -> handleSquareClick(currentRow, currentCol));
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        System.out.println("Clicked on square (" + row + ", " + col + ")");
        if (!player.canAct()) {
            System.out.println("Not on your turn");
            return;
        }

        if (!isPieceSelected && playerRow == row && playerCol == col) {
            isPieceSelected = true;
            // Show valid moves by changing the color of adjacent squares
            showValidMoves(row, col);
        } else if (isPieceSelected) {
            if (validMovesCache[row][col] && player.validMove(row, col) && (pieces[row][col] == null || pieces[row][col] == player)) {
                System.out.println("Moving player to square (" + row + ", " + col + ")");
                movePlayer(row, col);
            } else {
                System.out.println("Invalid move");
            }
            resetSelection();
        }
    }

    private void movePlayer(int row, int col) {
        // Update player position and move the piece on the board
        GridPane.setRowIndex(player.getTexture(), row);
        GridPane.setColumnIndex(player.getTexture(), col);

        pieces[player.getRow()][player.getCol()] = null;
        pieces[row][col] = player;
        playerRow = row;
        playerCol = col;
        player.setCol(playerCol);
        player.setRow(playerRow);
    }

    private boolean isValidMove(int row, int col) {
        if (pieces[row][col] instanceof BaseWallPiece) {
            return false; // Destination square contains a wall, invalid move
        }
        // For simplicity, consider all adjacent squares as valid moves
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
                        squares[newRow][newCol].setImage(new Image(Config.ValidMovePath)); // Set texture to indicate valid move
                    }
                }
            }
        }
    }

    private void resetSelection() {
        isPieceSelected = false;
        // Reset the texture of all squares to the default floor texture
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col].setImage(new Image(Config.FloorPath));
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private void placeEntityRandomly(BasePiece entity) {
        int row, col;
        do {
            row = (int) (Math.random() * BOARD_SIZE);
            col = (int) (Math.random() * BOARD_SIZE);
        } while (!isValidMove(row, col) || pieces[row][col] != null);

        if (entity instanceof BasePlayerPiece) {
            playerCol = col;
            playerRow = row;
            entity.getTexture().setOnMouseClicked(event -> handleSquareClick(entity.getRow(), entity.getCol()));
        }

        entity.setRow(row);
        entity.setCol(col);
        pieces[row][col] = entity; // Mark the position as occupied
        placePiece(entity);
    }

    private void removeElements() {
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
    }

    private void setupKeyEvents(Scene scene) {
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
            }
        });
    }
}
