package items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.ImageScaler;
import utils.Config;

public class BaseItem {
    protected String name;
    protected Color nameColor;
    protected ImageView icon;
    protected ImageView frame;
    protected String description;

    protected BaseItem(String name, Color nameColor, String iconPath, String description) {
        ImageScaler imageScaler = new ImageScaler();

        this.name = name;
        this.nameColor = nameColor;
        this.icon = new ImageView(iconPath);
        this.frame = new ImageView(imageScaler.resample(new Image(Config.FramePath), 2));
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public Color getNameColor() {
        return nameColor;
    }
    public ImageView getIcon() {
        return icon;
    }
    public String getDescription() {
        return description;
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
    public void setFrame(ImageView frame) {
        this.frame = frame;
    }
}
