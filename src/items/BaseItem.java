package items;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import utils.ImageScaler;
import utils.Config;

import java.util.Random;

public class BaseItem {
    protected String name;
    protected Color nameColor;
    protected ImageView icon;
    protected ImageView frame;
    protected String description;
    protected Color backgroundColor;

    protected int price;

    protected Config.Rarity rarity;

    protected String sfxPath;


    protected BaseItem(String name, Color nameColor, String iconPath, String description, Config.Rarity rarity, String sfxPath, Color backgroundColor) {

        this.name = name;
        this.nameColor = nameColor;
        this.icon = new ImageView(iconPath);
        this.frame = new ImageView(ImageScaler.resample(new Image(Config.FramePath), 2));
        this.description = description;
        this.rarity = rarity;
        this.price = priceGenerator();
        this.sfxPath = sfxPath;
        this.backgroundColor = backgroundColor;
    }

    private int priceGenerator() {
        Random random = new Random();
        int basePrice = 10; // Starting base price

        // Adjust base price based on rarity
        return switch (rarity) {
            case COMMON -> basePrice + random.nextInt(10); // Randomize a bit for common items
            case UNCOMMON -> basePrice * 2 + random.nextInt(20); // Uncommon items are a bit pricier
            case RARE -> basePrice * 5 + random.nextInt(50); // Rares are more valuable
            case EPIC -> basePrice * 10 + random.nextInt(100); // Epics are quite valuable
            case LEGENDARY -> basePrice * 20 + random.nextInt(200); // Legendary items are super rare and expensive
            default -> basePrice; // Default to base price if rarity is unrecognized
        };
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
    public int getPrice() {
        return price;
    }
    public Config.Rarity getRarity() {
        return rarity;
    }
    public Color getBackgroundColor() {
        return backgroundColor;
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
