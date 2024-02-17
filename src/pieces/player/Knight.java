package pieces.player;

public class Knight extends BasePlayerPiece {
    private int currentMana;
    private int maxMana;
    public Knight(int row, int col) {
        super(row, col);
        maxMana = 10;
        currentMana = maxMana;
        setTextureByPath("sprites/player/Knight.png");
    }

    @Override
    public boolean validMove(int row, int col) {
        int currentRow = getRow();
        int currentCol = getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }
}
