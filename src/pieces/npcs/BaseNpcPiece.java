package pieces.npcs;

import javafx.scene.image.ImageView;
import pieces.BasePiece;
import utils.Config;

public class BaseNpcPiece extends BasePiece {
    public BaseNpcPiece(ImageView texture, int row, int col) {
        super(Config.ENTITY_TYPE.NPC, texture, row, col);
    }

    
}
