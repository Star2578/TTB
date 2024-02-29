package items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.ImageScaler;
import utils.Config;

public class BaseItem {
    protected String name;
    protected ImageView icon;
    protected ImageView frame;
    protected String description;
    protected Config.ITEM_TYPE itemType;

    protected BaseItem(String name, String iconPath, Config.ITEM_TYPE itemType, String description) {
        ImageScaler imageScaler = new ImageScaler();

        this.name = name;
        this.icon = new ImageView(iconPath);
        this.frame = new ImageView(imageScaler.resample(new Image(Config.FramePath), 2));
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
    public ImageView getFrame() {
        return frame;
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
    public void setItemType(Config.ITEM_TYPE itemType) {
        this.itemType = itemType;
    }
    public void setFrame(ImageView frame) {
        this.frame = frame;
    }
}
