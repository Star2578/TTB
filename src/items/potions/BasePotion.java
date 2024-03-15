package items.potions;

import items.BaseItem;
import pieces.BasePiece;
import utils.Config;
import utils.Usable;

public abstract class BasePotion extends BaseItem implements Usable {
    public BasePotion(String name, String iconPath, Config.ITEM_TYPE itemType, String description) {
        super(name, iconPath, itemType, description);
    }

    public abstract void usePotion(BasePiece target);

    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();


    public abstract boolean validRange(int row, int col);
    public abstract void useItem(BasePiece on);
}
