package pieces.enemies;

import logic.GameManager;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.Random;

public class Bomb extends BaseMonsterPiece{

    private enum State {
        COUNTDOWN, // Counting down each turn until explode
        EXPLODE // EXPLOOOOOOOOOOOOOOOOOOOODE!
    }
    private Bomb.State currentState;
    private boolean[][] validMovesCache; // Cache of valid moves for the entire board
    private final int VISION_RANGE = 5;
    private Random random;

    public Bomb() {
        super(0, 0, 1);
        setTextureByPath(Config.BomberPath);
        setMaxHealth(10);
        setCurrentHealth(getMaxHealth());
        currentState = State.COUNTDOWN; // Initially in the Neutral/Roaming State
        this.validMovesCache = GameManager.getInstance().validMovesCache;
        random = new Random();


        //configs values for animation
        setupAnimation();
    }

    @Override
    public void attack(BasePlayerPiece playerPiece) {

    }

    @Override
    public void performAction() {

    }

    @Override
    public void updateState() {

    }

    @Override
    protected boolean isValidMoveSet(int row, int col) {
        return false;
    }

    @Override
    public void moveWithTransition(int col, int row) {

    }

    @Override
    protected void setupAnimation() {

    }
}
