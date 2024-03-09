package logic.handlers;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.ImageScaler;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;

public class MovementHandler {
    private static GameManager gameManager = GameManager.getInstance();
    private static BasePlayerPiece player = gameManager.player;
    private static final int BOARD_SIZE = Config.BOARD_SIZE;
    private static BasePiece[][] piecesPosition = gameManager.piecesPosition;
    private static boolean[][] validMovesCache = gameManager.validMovesCache;
    private static ArrayList<Point2D> selectedTiles = gameManager.selectedMoveTiles;
    private static ImageView[][] selectionFloor = gameManager.selectionFloor;
    private static ImageScaler imageScaler = new ImageScaler();

    private static int newDirection = 1;
    private static int bufferDirection = newDirection;

    public static void showValidMoves(int playerRow, int playerCol) {
        // Iterate over adjacent squares and update images based on cached valid moves
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = playerRow + dRow;
                int newCol = playerCol + dCol;
                // Check if the new position is within the board bounds and not the current position
                if (isInBoardPosition(newRow, newCol) && (newRow != playerRow || newCol != playerCol)) {
                    if (validMovesCache[newRow][newCol] && piecesPosition[newRow][newCol] == null) {
                        //add tile pos to be remove later to a list
                        selectedTiles.add(new Point2D(newRow , newCol));
                        // Highlight or mark the square to indicate it's within the movement range
                        selectionFloor[newRow][newCol].setImage(imageScaler.resample(new Image(Config.ValidMovePath), 2)); // Set texture to indicate valid move
                    }
                }
            }
        }
    }

    public static void movePlayer(int row, int col) {
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

        piecesPosition[player.getRow()][player.getCol()] = null;
        piecesPosition[row][col] = player;

        player.setRow(row);
        player.setCol(col);
    }

    private static boolean isInBoardPosition(int row, int col) {
        // Check if the position is inside the board
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
}
