package skills;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import pieces.BasePiece;
import utils.Config;

public class LockedSlot extends BaseSkill{

    public LockedSlot() {
        super("Locked", Color.ALICEBLUE, 0, 0, "");
        icon = new ImageView(Config.LockedSkillIconPath);
    }

    @Override
    public void perform(BasePiece target) {
        System.out.println("Locked");
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
