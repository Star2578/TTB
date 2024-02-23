package skills;

import javafx.scene.image.ImageView;
import pieces.BasePiece;

public abstract class BaseSkill {
    protected String name; // Skill name
    protected int manaCost; // Skill mana cost
    protected int actionPointCost; // Skill action point cost
    protected String description; // Skill description
    protected ImageView icon; // Skill icon to display
    protected int range; // Skill range, use to indicate the size of range needed
    protected boolean[][] areaRange; // For area skill

    protected BaseSkill(String name, int manaCost, int actionPointCost, String description) {
        this.name = name;
        this.manaCost = manaCost;
        this.actionPointCost = actionPointCost;
        this.description = description;
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

    public abstract boolean validRange(int row, int col);

    public abstract boolean castOnSelf();
    public abstract boolean castOnMonster();
}
