package pieces.player;

import utils.Config;

public class Knight extends BasePlayerPiece {
    public Knight(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);
        setMaxMana(10);
        setCurrentMana(getMaxMana());
        setMaxHealth(20);
        setCurrentHealth(getMaxHealth());
        setTextureByPath(Config.KnightPath);
        setCanAct(false);
    }

    @Override
    public boolean validMove(int row, int col) {
        int currentRow = getRow();
        int currentCol = getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }

    @Override
    public void startTurn() {
        setCanAct(true);
        setCurrentMana(getMaxMana());
        setCurrentActionPoint(getMaxActionPoint());
    }

    @Override
    public void endTurn() {
        setCanAct(false);
    }
}
