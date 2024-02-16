package pieces.player;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pieces.BasePiece;

public class PlayerPiece extends BasePiece {
    public PlayerPiece(int row, int col) {
        super("Player", new ImageView("sprites/player/Knight.png"), row, col);
    }
}
