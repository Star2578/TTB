package pieces.player;

public class Knight extends BasePlayerPiece {
    private int currentMana;
    private int maxMana;
    public Knight(int row, int col) {
        super(row, col);
        maxMana = 10;
        currentMana = maxMana;
    }

}
