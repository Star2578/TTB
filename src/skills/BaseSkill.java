package skills;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.ImageScaler;
import pieces.BasePiece;
import utils.Config;

public abstract class BaseSkill {
    protected String name; // Skill name
    protected int manaCost; // Skill mana cost
    protected int actionPointCost; // Skill action point cost
    protected String description; // Skill description
    protected ImageView icon; // Skill icon to display
    protected ImageView frame; // Skill frame
    protected int range; // Skill range, use to indicate the size of range needed
    protected boolean[][] areaRange; // For area skill

    protected BaseSkill(String name, int manaCost, int actionPointCost, String description) {
        ImageScaler imageScaler = new ImageScaler();

        this.name = name;
        this.manaCost = manaCost;
        this.actionPointCost = actionPointCost;
        this.description = description;
        this.frame = new ImageView(imageScaler.resample(new Image(Config.FramePath), 2));
    }

    // Abstract method to perform the skill
    public abstract void perform(BasePiece target);

    public String getName() {
        return name;
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


    public void setFrame(ImageView frame) {
        this.frame = frame;
    }


    public abstract boolean validRange(int row, int col);

    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();
}
