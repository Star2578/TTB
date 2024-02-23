package skills;

import javafx.scene.image.ImageView;

public abstract class BaseSkill {
    protected String name;
    protected int manaCost;
    protected int actionPointCost;
    protected String description;
    protected ImageView icon;

    protected BaseSkill(String name, int manaCost, int actionPointCost, String description) {
        this.name = name;
        this.manaCost = manaCost;
        this.actionPointCost = actionPointCost;
        this.description = description;
    }

    // Abstract method to perform the skill
    public abstract void perform();

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
}
