package utils;

import pieces.BasePiece;

public interface Usable {
    int getRange();
    boolean validRange(int row, int col);
    boolean castOnSelf();
    boolean castOnMonster();
    void useItem(BasePiece on);
}
