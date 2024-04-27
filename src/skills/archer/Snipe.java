package skills.archer;

import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logic.GameManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class Snipe extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 1;
    public Snipe() {

        super("Snipe", Color.DARKRED,
                1, 1,
                "Costly but Deadly"
                , Config.Rarity.LEGENDARY, "res/SFX/skills/slash/PP_01.wav"
        );
        icon = new ImageView(Config.SnipePath);
        range = 7;
    }

    @Override
    public void attack() {
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();
        int dRow = target.getRow() - currentRow;
        int dCol = target.getCol() - currentCol;
        int directionRow = dRow;
        int directionCol = dCol;

        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
        GameManager.getInstance().player.decreaseMana(manaCost);

        // Normalize the direction
        if (dRow != 0) directionRow /= Math.abs(dRow);
        if (dCol != 0) directionCol /= Math.abs(dCol);

        //=========<SKILL EFFECT PHASE 1>====================================================================
        EffectManager.getInstance()
                .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                        GameManager.getInstance().player ,
                        target.getRow(), target.getCol(),
                        EffectManager.getInstance().createInPlaceEffects(14) ,
                        new EffectConfig(-12 , -2 , 56 , 1.4) );
        //===========================================================================================

        // Create a PauseTransition with a duration of 0.8 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(0.88));

        // Set the action to perform after the pause
        pause.setOnFinished(event ->
                //=========<SKILL EFFECT PHASE 2>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(15) ,
                                new EffectConfig(-12 , -8 , 56 , 1.4) ));
                //===========================================================================================);

        // Start the pause
        pause.play();


        for (int i = 1; i <= range; i++) {
            int newRow, newCol;
            int dAbsCol = Math.abs(dCol);
            int dAbsRow = Math.abs(dRow);

            if (dAbsCol >= dAbsRow) {
                int y = (int) Math.round(i * Math.tan(Math.atan2(dAbsRow, dAbsCol)));
                newRow = currentRow + directionRow * y;
                newCol = currentCol + directionCol * i;
            } else {
                int x = (int) Math.round(i * Math.tan(Math.atan2(dAbsCol, dAbsRow)));
                newRow = currentRow + directionRow * i;
                newCol = currentCol + directionCol * x;
            }

            if (newRow < 0 || newRow >= Config.BOARD_SIZE || newCol < 0 || newCol >= Config.BOARD_SIZE) {
                break;
            }

            // Create a PauseTransition with a duration of 1.4 seconds
            PauseTransition pause1 = new PauseTransition(Duration.seconds(1.4));

            // Set the action to perform after the pause
            pause1.setOnFinished(event ->

            //=========<SKILL EFFECT PHASE 3>====================================================================
            EffectManager.getInstance()
                    .renderEffect( EffectManager.TYPE.ON_SELF ,
                            GameManager.getInstance().player ,
                            newRow, newCol,
                            EffectManager.getInstance().createInPlaceEffects(16) ,
                            new EffectConfig(0 , -4 , 0 , 1.4) ));
            //===========================================================================================);
            // Start the pause
            pause1.play();

            // Create a PauseTransition with a duration of 2.4 seconds
            PauseTransition pause2 = new PauseTransition(Duration.seconds(2.4));

            // Set the action to perform after the pause
            pause2.setOnFinished(event ->
            //=========<SKILL EFFECT PHASE 4>====================================================================
                    EffectManager.getInstance()
                            .renderEffect( EffectManager.TYPE.ON_SELF ,
                                    GameManager.getInstance().player ,
                                    newRow, newCol,
                                    EffectManager.getInstance().createInPlaceEffects(17) ,
                                    new EffectConfig(0 , -4 , 0 , 1.4) ));
            //===========================================================================================);

            // Start the pause
            pause2.play();

            BasePiece piece = GameManager.getInstance().piecesPosition[newRow][newCol];

            if (piece instanceof BaseMonsterPiece monsterPiece) {
                monsterPiece.takeDamage(getAttack());
            }

            System.out.println("Use " + name + " on " + newRow + " " + newCol);
        }
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();
        return Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return true;
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }



}
