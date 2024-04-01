package items.potions;

import items.BaseItem;
import pieces.BasePiece;
import utils.Config;
import utils.Usable;

public abstract class BasePotion extends BaseItem implements Usable {
    public BasePotion(String name, String iconPath, String description) {
        super(name, iconPath, description);
    }

    public abstract void usePotion(BasePiece target);

    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();


    public abstract boolean validRange(int row, int col);
    public abstract void useItem(BasePiece on);
}
