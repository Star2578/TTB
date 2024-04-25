package items;

import javafx.scene.paint.Color;
import utils.Config;

public class EmptyItem extends BaseItem {
    public EmptyItem() {
        super("", Color.DARKGRAY, Config.FramePath, "", Config.Rarity.COMMON, "", Color.TRANSPARENT);
    }
}
