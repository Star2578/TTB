package skills;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import utils.ImageScaler;
import pieces.BasePiece;
import utils.Config;

import java.util.Random;

public abstract class BaseSkill {
    protected String name; // Skill name
    protected Color nameColor; // Skill name's color
    protected int manaCost; // Skill mana cost
    protected int actionPointCost; // Skill action point cost
    protected String description; // Skill description
    protected ImageView icon; // Skill icon to display
    protected ImageView frame; // Skill frame
    protected int range; // Skill range, use to indicate the size of range needed
    protected boolean[][] areaRange; // For area skill

    protected int price;

    protected Config.Rarity rarity;

    protected String sfxPath;

    protected BaseSkill(String name, Color nameColor, int manaCost, int actionPointCost, String description, Config.Rarity rarity, String sfxPath) {

        this.name = name;
        this.nameColor = nameColor;
        this.manaCost = manaCost;
        this.actionPointCost = actionPointCost;
        this.description = description;
        this.frame = new ImageView(ImageScaler.resample(new Image(Config.FramePath), 2));
        this.rarity = rarity;
        this.price = priceGenerator();
        this.sfxPath = sfxPath;
    }

    private int priceGenerator() {
        Random random = new Random();
        int basePrice = 30; // Starting base price

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

    /******************************************
     *             Abstract fields
     ******************************************/
    public abstract void perform(BasePiece target);
    public abstract boolean validRange(int row, int col);
    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();


    /******************************************
     *             getter setter
     ******************************************/
    public String getName() {
        return name;
    }
    public Color getNameColor() {
        return nameColor;
    }
    public int getManaCost() {
        return manaCost;
    }
    public int getActionPointCost() {
        return actionPointCost;
    }
    public String getDescription() {
        return description;
    }
    public ImageView getIcon() {
        return icon;
    }
    public int getRange() {
        return range;
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
    public void setFrame(ImageView frame) {
        this.frame = frame;
    }
}
