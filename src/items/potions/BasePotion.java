package items.potions;

import items.BaseItem;
import pieces.BasePiece;

public abstract class BasePotion extends BaseItem {
    public BasePotion(String name, String iconPath, String description) {
        super(name, iconPath, description);
    }

    public abstract void usePotion(BasePiece target);

    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();
}
