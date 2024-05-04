package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import logic.gameUI.GUIManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;

public class HammerFall extends BaseSkill implements Attackable {
    private BasePiece target;

    private final int DAMAGE = 10;
    public HammerFall() {
        super("Hammer Fall", Color.DARKGRAY, 5, 3,
                "Have 20% chance to crit with 300% damage and 1% to crit 1000%", Config.Rarity.LEGENDARY, "");

        icon = new ImageView(Config.HammerFallPath);
        range = 1;
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        return Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return true;
    }

    @Override
    public void attack() {
        if (target != null && target != GameManager.getInstance().player) {
            // Perform Attack
            if (target instanceof BaseMonsterPiece monsterPiece) {
                double damageMultiplier = 1.0; // Default damage multiplier

                // Check for critical hit
                double critChance = Math.random() * 100; // Generate a random number between 0 and 100
                if (critChance <= 1) { // 1% chance for 1000% damage
                    damageMultiplier = 10.0;
                    GUIManager.getInstance().eventLogDisplay.addLog("Critical Hit! 1000% damage dealt!", Color.DARKVIOLET);
                } else if (critChance <= 20) { // 20% chance for 300% damage
                    damageMultiplier = 3.0;
                    GUIManager.getInstance().eventLogDisplay.addLog("Critical Hit! 300% damage dealt!", Color.MEDIUMPURPLE);
                }

                // Calculate damage based on the damage multiplier
                double totalDamage = DAMAGE * damageMultiplier;

                // Apply damage to the target
                monsterPiece.takeDamage((int) totalDamage);
                GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
                GameManager.getInstance().player.decreaseMana(manaCost);
                System.out.println("Use " + name + " on " + monsterPiece.getClass().getSimpleName());

                //=========<SKILL EFFECT>====================================================================
                EffectMaker.getInstance()
                        .renderEffect( EffectMaker.TYPE.AROUND_SELF ,
                                GameManager.getInstance().player ,
                                target.getRow(), target.getCol(),
                                EffectMaker.getInstance().createInPlaceEffects(29) ,
                                new EffectConfig(6 , 4 , 42 , 3) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}
