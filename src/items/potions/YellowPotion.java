package items.potions;

import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import pieces.ActionPointBuffable;
import utils.Config;

public class YellowPotion extends BasePotion implements ActionPointBuffable {
    private final int MAX_ACTION_POINT = 1;
    private BasePiece target;

    public YellowPotion() {
        super("Yellow Potion", Color.ORANGE, Config.YellowPotionPath,
                "You only hope it's not the other yellow liquid", Config.Rarity.LEGENDARY, "res/SFX/powerup/8bit-powerup2.wav", Color.DARKORANGE);
    }

    @Override
    public void usePotion(BasePiece target) {
        this.target = target;
        buffActionPoint();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public void buffActionPoint() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMaxActionPoint = playerPiece.getMaxActionPoint();

                playerPiece.setMaxActionPoint(currentMaxActionPoint + MAX_ACTION_POINT);
            }
        }
    }

    @Override
    public boolean castOnSelf() {
        return true;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public boolean validRange(int row, int col) {
        // Only valid at player's square
        BasePlayerPiece player = GameManager.getInstance().player;

        return player.getRow() == row && player.getCol() == col;
    }

    @Override
    public void useItem(BasePiece on) {
        usePotion(on);
    }

    @Override
    public int getBuffActionPoint() {
        return MAX_ACTION_POINT;
    }
}
