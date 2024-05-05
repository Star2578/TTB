package items.potions;

import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import pieces.AttackBuffable;
import utils.Config;

public class PurplePotion extends BasePotion implements AttackBuffable {
    private final int ATTACK_BUFF = 1;
    private BasePiece target;

    public PurplePotion() {
        super("Purple Potion", Color.MAGENTA, Config.PurplePotionPath,
                "You could feel immense aura coming from the bottle (it's smelly)", Config.Rarity.LEGENDARY, "SFX/powerup/8bit-powerup2.wav", Color.DARKMAGENTA);
    }

    @Override
    public void usePotion(BasePiece target) {
        this.target = target;
        buffAttack();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public void buffAttack() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentAttack = playerPiece.getAttackDamage();

                playerPiece.setAttackDamage(currentAttack + ATTACK_BUFF);
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
    public int getBuffAttack() {
        return ATTACK_BUFF;
    }
}
