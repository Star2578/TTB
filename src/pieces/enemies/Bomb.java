package pieces.enemies;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import logic.ui.GUIManager;
import pieces.player.BasePlayerPiece;
import pieces.wall.BaseWallPiece;
import utils.Config;

import java.util.Random;

public class Bomb extends BaseMonsterPiece{

    private enum State {
        COUNTDOWN, // Counting down each turn until explode
        EXPLODE // EXPLOOOOOOOOOOOOOOOOOOOODE!
    }
    private Bomb.State currentState;
    private int timeLeft = 2;

    public Bomb() {
        super(0, 0, 1);

        currentState = State.COUNTDOWN; // Initially in the Neutral/Roaming State

        //configs values for animation
        setupAnimation(Config.BombAnimationPath, 0, 0, 16, 16 , false);
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {

    }

    @Override
    public void performAction() {
        endAction = false;
        updateState();
        switch (currentState) {
            case COUNTDOWN:
                countingDownTimer();
                break;
            case EXPLODE:
                explode();
                break;
        }
    }

    private void countingDownTimer() {
        if (timeLeft > 0) timeLeft--;

        endAction = true;
    }

    private void explode() {
        // TODO : Implement explosion, + shape

        System.out.println("Bomb exploded");

        BasePlayerPiece player = GameManager.getInstance().player;
//        if (player.getRow() == getRow() || player.getCol() == getCol()) {
//            player.takeDamage(player.getCurrentHealth());
//            GUIManager.getInstance().updateGUI();
//        }
//        ------------------------------------------------------------------------
        boolean left = true, right = true, up = true, down = true;
        int row = getRow();
        int row1 = getRow();
        int col = getCol();
        int col1 = getCol();
        while (left || right || up || down) {


            if (left) {
                System.out.println("left");
                col--;
                if (GameManager.getInstance().piecesPosition[getRow()][col] instanceof BasePlayerPiece) {
                    player.takeDamage(player.getCurrentHealth());
                    GUIManager.getInstance().updateGUI();
                }
                if (GameManager.getInstance().piecesPosition[getRow()][col] instanceof BaseWallPiece) {
                    left = false;
                    System.out.println("left wall");
                }
            }

            if (right) {
                System.out.println("right");
                col1++;
                if (GameManager.getInstance().piecesPosition[getRow()][col1] instanceof BasePlayerPiece) {
                    player.takeDamage(player.getCurrentHealth());
                    GUIManager.getInstance().updateGUI();
                }
                if (GameManager.getInstance().piecesPosition[getRow()][col1] instanceof BaseWallPiece) {
                    right = false;
                    System.out.println("right wall");
                }
            }

            if (up) {
                System.out.println("up");
                row--;
                if (GameManager.getInstance().piecesPosition[row][getCol()] instanceof BasePlayerPiece) {
                    player.takeDamage(player.getCurrentHealth());
                    GUIManager.getInstance().updateGUI();
                }
                if (GameManager.getInstance().piecesPosition[row][getCol()] instanceof BaseWallPiece) {
                    up = false;
                    System.out.println("up wall");

                }
            }

            if (down) {
                System.out.println("down");
                row1++;
                if (GameManager.getInstance().piecesPosition[row1][getCol()] instanceof BasePlayerPiece) {
                    player.takeDamage(player.getCurrentHealth());
                    GUIManager.getInstance().updateGUI();
                }
                if (GameManager.getInstance().piecesPosition[row1][getCol()] instanceof BaseWallPiece) {
                    down = false;
                    System.out.println("down wall");
                }
            }
        }

        endAction = true;

        // destroy itself after exploded
        GameManager.getInstance().environmentPieces.remove(this);
        GameManager.getInstance().piecesPosition[getRow()][getCol()] = null;
        GameManager.getInstance().animationPane.getChildren().remove(this.animationImage);
        GameManager.getInstance().boardPane.getChildren().remove(this.getTexture());
    }

    @Override
    public void updateState() {
        if (timeLeft > 0) {
            currentState = State.COUNTDOWN;
        } else {
            currentState = State.EXPLODE;
        }
    }
}

