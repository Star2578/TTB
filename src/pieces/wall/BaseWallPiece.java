package pieces.wall;

import javafx.scene.image.ImageView;
import pieces.BasePiece;
import utils.Config;

public class BaseWallPiece extends BasePiece {
    public BaseWallPiece(int row, int col) {
        super("Wall", new ImageView(Config.WallPath), row, col);
    }


}
