package pieces.enemies;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import logic.gameUI.GUIManager;
import pieces.players.BasePlayerPiece;
import pieces.wall.BaseWallPiece;
import utils.Config;

public class Bomb extends BaseMonsterPiece{

    private enum State {
        COUNTDOWN, // Counting down each turn until explode
        EXPLODE // EXPLOOOOOOOOOOOOOOOOOOOODE!
    }
    private Bomb.State currentState;
    private int timeLeft = 2;
    private int damage = 7;

    public Bomb() {
        super(0, 0, 1);

        currentState = State.COUNTDOWN; // Initially in the Neutral/Roaming State

        maxHp = 1;
        currentHp = maxHp;
        
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
//            player.takeDamage(damage);
//            GUIManager.getInstance().updateGUI();
//        }
//        ------------------------------------------------------------------------
        // destroy itself after exploded


        //=========<SKILL EFFECT>====================================================================
            EffectMaker.getInstance()
                    .renderEffect( EffectMaker.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            getRow(), getCol(),
                            EffectMaker.getInstance().createInPlaceEffects(6) ,
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
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    getRow(), col,
                                    EffectMaker.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalCol1 = col;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[getRow()][finalCol1] instanceof BasePlayerPiece) {
                        player.takeDamage(damage);
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
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    getRow(), col1,
                                    EffectMaker.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalCol = col1;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[getRow()][finalCol] instanceof BasePlayerPiece) {
                        player.takeDamage(damage);
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
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    row, getCol(),
                                    EffectMaker.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalRow = row;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[finalRow][getCol()] instanceof BasePlayerPiece) {
                        player.takeDamage(damage);
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
                    EffectMaker.getInstance()
                            .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    row1, getCol(),
                                    EffectMaker.getInstance().createInPlaceEffects(6) ,
                                    new EffectConfig(-9 , -16 , 0 , 1.1) );
                    //===========================================================================================
                }
                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalRow1 = row1;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[finalRow1][getCol()] instanceof BasePlayerPiece) {
                        player.takeDamage(damage);
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

