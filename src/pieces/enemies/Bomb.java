package pieces.enemies;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
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
        setTextureByPath(Config.BombPath);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());
        currentState = State.COUNTDOWN; // Initially in the Neutral/Roaming State


        //configs values for animation
        setupAnimation();
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

    @Override
    protected boolean isValidMoveSet(int row, int col) {
        return false;
    }

    @Override
    public void moveWithTransition(int row, int col) {

    }

    @Override
    protected void setupAnimation() {
        //===================<animation section>==========================================
        offsetX=0;
        offsetY=0;
        //sprite animations for monster
        animationImage = new ImageView(new Image(Config.BombAnimationPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,1,0,1,16,16,5);
        spriteAnimation.start();

        //setup moveTranslate behaviour
        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }
}
