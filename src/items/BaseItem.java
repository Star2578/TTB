package items;

import javafx.scene.image.ImageView;
import utils.Config;

public class BaseItem {
    protected String name;
    protected ImageView icon;
    protected String description;
    protected Config.ITEM_TYPE itemType;

    protected BaseItem(String name, String iconPath, Config.ITEM_TYPE itemType, String description) {
        this.name = name;
        this.icon = new ImageView(iconPath);
        this.itemType = itemType;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public ImageView getIcon() {
        return icon;
    }
    public String getDescription() {
        return description;
    }
    public Config.ITEM_TYPE getItemType() {
        return itemType;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setIcon(ImageView icon) {
        this.icon = icon;
    }
    public void setIconByPath(String iconPath) {
        this.icon = new ImageView(iconPath);
    }

}
