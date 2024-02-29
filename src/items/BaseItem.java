package items;

import javafx.scene.image.ImageView;

public class BaseItem {
    protected String name;
    protected ImageView icon;
    protected String description;

    protected BaseItem(String name, String iconPath, String description) {
        this.name = name;
        this.icon = new ImageView(iconPath);
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
