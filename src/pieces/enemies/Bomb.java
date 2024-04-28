package pieces.enemies;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import logic.ui.GUIManager;
import pieces.BasePiece;
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

        setMaxHealth(1);
        setCurrentHealth(getMaxHealth());

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
//        if (player.getRow() == getRow() || player.getCol() == getCol()) {
//            player.takeDamage(player.getCurrentHealth());
//            GUIManager.getInstance().updateGUI();
//        }
//        ------------------------------------------------------------------------
        // destroy itself after exploded


        //=========<SKILL EFFECT>====================================================================
            EffectManager.getInstance()
                    .renderEffect( EffectManager.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            getRow(), getCol(),
                            EffectManager.getInstance().createInPlaceEffects(6) ,
                            new EffectConfig(-9 , -16 , 0 , 1.1) );
        //===========================================================================================

        boolean left = true, right = true, up = true, down = true;
        int row = getRow();
        int row1 = getRow();
        int col = getCol();
        int col1 = getCol();
        while (left || right || up || down) {


            if (left) {
                System.out.println("left");
                col--;
                if (GameManager.getInstance().piecesPosition[getRow()][col] instanceof BaseWallPiece) {
                    left = false;
                    System.out.println("left wall");
                }
                if (left){
                    //=========<SKILL EFFECT>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    getRow(), col,
                                    EffectManager.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalCol1 = col;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[getRow()][finalCol1] instanceof BasePlayerPiece) {
                        player.takeDamage(player.getCurrentHealth());
                        GUIManager.getInstance().updateGUI();
                    }
                });

                // Start the pause
                pause.play();

            }

            if (right) {
                System.out.println("right");
                col1++;

                if (GameManager.getInstance().piecesPosition[getRow()][col1] instanceof BaseWallPiece) {
                    right = false;
                    System.out.println("right wall");
                }
                if (right){
                    //=========<SKILL EFFECT>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    getRow(), col1,
                                    EffectManager.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalCol = col1;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[getRow()][finalCol] instanceof BasePlayerPiece) {
                        player.takeDamage(player.getCurrentHealth());
                        GUIManager.getInstance().updateGUI();
                    }
                });

                // Start the pause
                pause.play();

            }

            if (up) {
                System.out.println("up");
                row--;
                if (GameManager.getInstance().piecesPosition[row][getCol()] instanceof BaseWallPiece) {
                    up = false;
                    System.out.println("up wall");

                }
                if (up){
                    //=========<SKILL EFFECT>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    row, getCol(),
                                    EffectManager.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalRow = row;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[finalRow][getCol()] instanceof BasePlayerPiece) {
                        player.takeDamage(player.getCurrentHealth());
                        GUIManager.getInstance().updateGUI();
                    }
                });

                // Start the pause
                pause.play();

            }

            if (down) {
                System.out.println("down");
                row1++;
                if (GameManager.getInstance().piecesPosition[row1][getCol()] instanceof BaseWallPiece) {
                    down = false;
                    System.out.println("down wall");
                }
                if (down){
                    //=========<SKILL EFFECT>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    row1, getCol(),
                                    EffectManager.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalRow1 = row1;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[finalRow1][getCol()] instanceof BasePlayerPiece) {
                        player.takeDamage(player.getCurrentHealth());
                        GUIManager.getInstance().updateGUI();
                    }
                });

                // Start the pause
                pause.play();

            }
        }
        GameManager.getInstance().environmentPieces.remove(this);
        GameManager.getInstance().piecesPosition[getRow()][getCol()] = null;
        GameManager.getInstance().animationPane.getChildren().remove(this.animationImage);
        GameManager.getInstance().boardPane.getChildren().remove(this.getTexture());


        endAction = true;

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

