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

public class SlimeMucilage extends BaseMonsterPiece{

    private enum State {
        COUNTDOWN, // Counting down each turn until explode
        EXPIRED // Slime has been removed
    }
    private SlimeMucilage.State currentState;
    private int timeLeft = 2;

    public SlimeMucilage() {
        super(0, 0, 1);

        currentState = State.COUNTDOWN; // Initially in the Neutral/Roaming State

        maxHp = 1;
        currentHp = maxHp;
        //TODO : Change the animation path
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
            case EXPIRED:
                removing();
                break;
        }
    }

    private void countingDownTimer() {
        if (timeLeft > 0) timeLeft--;
        if (timeLeft == 1) spriteAnimation.changeAnimation(4, 1);
        if (timeLeft == 0) spriteAnimation.changeAnimation(4, 2);

        endAction = true;
    }

    public void removing() {
        System.out.println("Bomb exploded");

        BasePlayerPiece player = GameManager.getInstance().player;

        //=========<SKILL EFFECT>====================================================================
        EffectMaker.getInstance()
                .renderEffect( EffectMaker.TYPE.ON_SELF ,
                        GameManager.getInstance().player ,
                        getRow(), getCol(),
                        EffectMaker.getInstance().createInPlaceEffects(6) ,
                        new EffectConfig(-9 , -16 , 0 , 1.1) );
        //===========================================================================================

        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = getRow() + dRow;
                int newCol = getCol() + dCol;
                if (GameManager.getInstance().piecesPosition[newRow][newCol] instanceof BaseWallPiece) {
                    System.out.println("wall obstacle");
                    continue;
                }

                //=========<SKILL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                GameManager.getInstance().player ,
                                newRow, newCol,
                                EffectMaker.getInstance().createInPlaceEffects(6) ,
                                new EffectConfig(-9 , -16 , 0 , 1.1) );
                //===========================================================================================

                // Create a PauseTransition with a duration of 0.7 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));

                // Set the action to perform after the pause
                int finalRow1 = newRow;
                int finalCol1 = newCol;
                pause.setOnFinished(event -> {
                    if (GameManager.getInstance().piecesPosition[finalRow1][finalCol1] instanceof BasePlayerPiece) {
                        player.takeDamage(10);
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
            currentState = State.EXPIRED;
        }
    }
}

