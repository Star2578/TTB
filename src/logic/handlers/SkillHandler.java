package logic.handlers;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.ImageScaler;
import pieces.BasePiece;
import skills.BaseSkill;
import skills.SingleTargetSkill;
import utils.Config;

import java.util.ArrayList;

public class SkillHandler {
    private static GameManager gameManager = GameManager.getInstance();
    private static BasePiece[][] piecesPosition = gameManager.piecesPosition;
    private static ImageView[][] dungeonFloor = gameManager.dungeonFloor;
    private static ArrayList<Point2D> selectedTiles = gameManager.selectedTiles;
    private static ImageScaler imageScaler = new ImageScaler();
    private static final int BOARD_SIZE = Config.BOARD_SIZE;

    public static void showValidSkillRange(int playerCol, int playerRow, BaseSkill skillSelected) {
        int range = skillSelected.getRange();

        if (skillSelected instanceof SingleTargetSkill<?> singleTargetSkill) {
            for (int dRow = -range; dRow <= range; dRow++) {
                for (int dCol = -range; dCol <= range; dCol++) {
                    int newRow = playerCol + dRow;
                    int newCol = playerRow + dCol;
                    if (isInBoardPosition(newRow, newCol) && singleTargetSkill.validRange(newRow, newCol) && (newRow != playerRow || newCol != playerCol)) {
                        selectedTiles.add(new Point2D(newRow, newCol));
                        // Highlight or mark the square to indicate it's within the skill range
                        dungeonFloor[newRow][newCol].setImage(imageScaler.resample(new Image(Config.ValidSkillPath)));
                    }
                }
            }
        }

    }

    private static boolean isInBoardPosition(int row, int col) {
        // Check if the position is inside the board
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
}
