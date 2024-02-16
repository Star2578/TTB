package pieces.wall;

import javafx.scene.image.ImageView;
import pieces.BasePiece;

public class BaseWallPiece extends BasePiece {
    public BaseWallPiece(int row, int col) {
        super("Wall", new ImageView("sprites/ground/wall_1.png"), row, col);
    }


}
