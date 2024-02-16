package game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.DungeonGenerator;
import pieces.BasePiece;
import pieces.player.*;
import pieces.wall.*;

public class Main extends Application {
    private static final int BOARD_SIZE = 16;
    private static final int SQUARE_SIZE = 32;
    private static final int GAME_SIZE = 512;
    private GridPane boardPane = new GridPane();
    private ImageView[][] squares = new ImageView[BOARD_SIZE][BOARD_SIZE];
    private BasePiece[][] pieces = new BasePiece[BOARD_SIZE][BOARD_SIZE];
    private DungeonGenerator dungeonGenerator;
    private BasePlayerPiece player;
    private int playerRow = 0;
    private int playerCol = 0;
    private boolean isPieceSelected = false;
    private ImageView selectedPieceView = null;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setBackground(Background.fill(Color.DARKBLUE));
        VBox topPane = new VBox(); // Pane for top area
        topPane.setBackground(Background.fill(Color.DARKRED));
        VBox bottomPane = new VBox(); // Pane for bottom GUI

        // Create the main game area
        initGrid(boardPane);
        boardPane.setMaxSize(GAME_SIZE, GAME_SIZE);

        // Center the game board using a StackPane
        StackPane centerPane = new StackPane();
        centerPane.getChildren().add(boardPane);
        boardPane.setBackground(Background.fill(Color.GOLD));
        root.setCenter(centerPane);
        centerPane.setPadding(new Insets(0, 0, 200, 0));

        // Initialize player at starting position
        player = new BasePlayerPiece(0, 0); // Start at (0, 0) for now
        dungeonGenerator = new DungeonGenerator(); // Initialize DungeonGenerator
        dungeonGenerator.generateDungeon(); // Generate dungeon
        placeDungeon();
        placePlayerAtValidPosition();

        // Add game area and GUI panes to the root BorderPane
        root.setTop(topPane);
        root.setBottom(bottomPane);

        // Set up the scene and stage
        Scene gameScene = new Scene(root, 1280, 720);
        SceneManager.getInstance().setGameScene(gameScene);
        setupMouseEvents(gameScene);
        primaryStage.setResizable(false);
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Dungeon Crawler");
        primaryStage.show();
    }

    private void initGrid(GridPane gridPane) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                ImageView square = new ImageView();
                square.setFitWidth(SQUARE_SIZE);
                square.setFitHeight(SQUARE_SIZE);
                square.setImage(new Image("sprites/ground/floor_1.png")); // Set default texture
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
        if (piece instanceof BasePlayerPiece)
            pieceView.setOnMouseClicked(event -> handlePieceClick(piece));

        GridPane.setRowIndex(pieceView, piece.getRow()); // Set row index
        GridPane.setColumnIndex(pieceView, piece.getCol()); // Set column index
        boardPane.getChildren().add(pieceView); // Add piece to board
    }

    private void setupMouseEvents(Scene scene) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                final int currentRow = row; // Make row effectively final
                final int currentCol = col; // Make col effectively final
                ImageView square = squares[row][col];
                square.setOnMouseClicked(event -> handleSquareClick(currentRow, currentCol));
                square.setOnMouseDragged(event -> handleSquareDrag(event, currentRow, currentCol));
            }
        }
    }

    private void handlePieceClick(BasePiece piece) {
        if (piece instanceof BasePlayerPiece basePlayerPiece) {
            System.out.println("Clicked on player piece at (" + basePlayerPiece.getRow() + ", " + basePlayerPiece.getCol() + ")");
            handleSquareClick(basePlayerPiece.getRow(), basePlayerPiece.getCol());
        }
    }

    private void handleSquareClick(int row, int col) {
        System.out.println("Clicked on square (" + row + ", " + col + ")");
        if (!isPieceSelected && playerRow == row && playerCol == col) {
            isPieceSelected = true;
            selectedPieceView = squares[row][col];
            // Show valid moves by changing the color of adjacent squares
            showValidMoves(row, col);
        } else if (isPieceSelected) {
            if (isValidMove(row, col)) {
                System.out.println("Moving player to square (" + row + ", " + col + ")");
                movePlayer(row, col);
            } else {
                System.out.println("Invalid move");
            }
            resetSelection();
        }
    }

    private void handleSquareDrag(MouseEvent event, int row, int col) {
        if (isPieceSelected) {
            System.out.println("Dragging piece to square (" + row + ", " + col + ")");
            // Update the position of the selected piece
            selectedPieceView.relocate(event.getSceneX() - SQUARE_SIZE / 2, event.getSceneY() - SQUARE_SIZE / 2);
            // Check if the piece is dropped onto a valid square
            if (event.getEventType() == MouseEvent.MOUSE_RELEASED && isValidMove(row, col)) {
                System.out.println("Dropped piece on valid square");
                movePlayer(row, col);
                resetSelection();
            }
        }
    }

    private void movePlayer(int row, int col) {
        // Update player position and move the piece on the board
        GridPane.setRowIndex(player.getTexture(), row);
        GridPane.setColumnIndex(player.getTexture(), col);
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
        return Math.abs(row - playerRow) <= 1 && Math.abs(col - playerCol) <= 1;
    }

    private void resetSelection() {
        isPieceSelected = false;
        selectedPieceView = null;
        // Reset the texture of all squares to the default floor texture
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col].setImage(new Image("sprites/ground/floor_1.png"));
            }
        }
    }

    private void showValidMoves(int row, int col) {
        // Iterate over adjacent squares
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                // Check if the new position is within the board bounds and not the current position
                if (isValidPosition(newRow, newCol) && (newRow != row || newCol != col)) {
                    squares[newRow][newCol].setImage(new Image("sprites/ground/floor_2.png")); // Set texture to indicate valid move
                }
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private void placePlayerAtValidPosition() {
        // Find a valid starting position for the player
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (pieces[row][col] == null) {
                    // Found an empty space, place the player here
                    playerRow = row;
                    playerCol = col;
                    player.setRow(playerRow);
                    player.setCol(playerCol);
                    placePiece(player);
                    return; // Exit the loop
                }
            }
        }
    }
}
