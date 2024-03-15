package pieces.wall;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.TileMap;
import pieces.BasePiece;
import utils.Config;

public class BaseWallPiece extends BasePiece {
    private TileMap tileMap;

    public BaseWallPiece(int row, int col) {
        super(Config.ENTITY_TYPE.WALL, new ImageView(Config.WallPath), row, col);
        tileMap = GameManager.getInstance().wallTileMap;
        getTexture().setPreserveRatio(true);
    }

    public TileMap getTileMap(){ return this.tileMap;}

}
