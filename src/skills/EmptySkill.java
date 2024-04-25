package skills;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import pieces.BasePiece;
import utils.Config;

public class EmptySkill extends BaseSkill{
    public EmptySkill() {
        super("Empty", Color.ALICEBLUE, 0, 0, "This slot is empty, you can add a skill here", Config.Rarity.COMMON, "");
        icon = new ImageView(Config.UnlockedSkillIconPath);
    }

    @Override
    public void perform(BasePiece target) {
        System.out.println("Nothing happen");
    }

    @Override
    public boolean validRange(int row, int col) {
        return false;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }
}
