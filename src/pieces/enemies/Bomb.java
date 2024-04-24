package pieces.enemies;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import logic.ui.GUIManager;
import pieces.player.BasePlayerPiece;
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
        setupAnimation(Config.BombAnimationPath, 0, 0, 32, 32 , true);
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

        if (timeLeft == 1) spriteAnimation.changeAnimation(4, 1);
        if (timeLeft == 0) spriteAnimation.changeAnimation(4, 2);

        endAction = true;
    }

    private void explode() {
        // TODO : Implement explosion, + shape

        System.out.println("Bomb exploded");

        BasePlayerPiece player = GameManager.getInstance().player;
        if (player.getRow() == getRow() || player.getCol() == getCol()) {
            player.takeDamage(player.getCurrentHealth());
            GUIManager.getInstance().updateGUI();
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
