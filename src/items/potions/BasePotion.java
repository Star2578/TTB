package items.potions;

import items.BaseItem;
import javafx.scene.paint.Color;
import pieces.BasePiece;
import utils.Config;
import utils.Usable;

public abstract class BasePotion extends BaseItem implements Usable {
    public BasePotion(String name, Color nameColor, String iconPath, String description, Config.Rarity rarity, String sfxPath, Color backgroundColor) {
        super(name, nameColor, iconPath, description, rarity, sfxPath, backgroundColor);
    }

    public abstract void usePotion(BasePiece target);

    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();


    public abstract boolean validRange(int row, int col);
    public abstract void useItem(BasePiece on);
}
