package items;

import utils.Config;

public class EmptyFrame extends BaseItem {
    public EmptyFrame() {
        super("", Config.FramePath, Config.ITEM_TYPE.KEY_ITEM, "");
    }
}
