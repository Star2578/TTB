package pieces.player;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.SpriteAnimation;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import logic.ui.GUIManager;
import pieces.enemies.BaseMonsterPiece;
import skills.knight.Heal;
import skills.knight.Slash;
import skills.wizard.DragonFire;
import skills.wizard.Fireball;
import skills.wizard.IceShield;
import skills.wizard.RainOfFire;
import utils.Config;

import java.util.ArrayList;
import java.util.Map;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Wizard extends BasePlayerPiece{

    public Wizard(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);
        buffturn = 0;

        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;

        maxMana = 20;
        currentMana = maxMana;

        maxHp = 12;
        currentHp = maxHp;

        attackDamage = 4; // Base attack for player

        //add skill
        skills[0] = new Fireball();
        skills[1] = new IceShield();
        skills[2] = new RainOfFire();
        skills[3] = new DragonFire();
        //TODO===========

        //configs values for animation
        setupAnimation(Config.WizardAnimationPath, 0, -12, 32, 56, true);
    }

    @Override
    public boolean validMove(int row, int col) {
        return Math.abs(row - getRow()) <= 1 && Math.abs(col - getCol()) <= 1;
    }

    @Override
    public boolean validAttack(int row, int col) {
        return Math.abs(row - getRow()) <= 1 && Math.abs(col - getCol()) <= 1;
    }

    @Override
    public void startTurn() {
        setCanAct(true);
        // Check if the player has any effect
        for(Map.Entry<String, Integer> entry : EffectBuffs.entrySet()) {
            String BuffName = entry.getKey();
            int duration = EffectBuffs.get(BuffName);
            if (duration > 0) {
                duration--; // Decrement the duration
                EffectBuffs.put(BuffName, duration);
            }
            if (duration == 0) {
                EffectBuffs.remove(BuffName);
            }
            System.out.println(BuffName + " " + duration);
        }
        setCurrentMana(getCurrentMana() + 1); // Wizard restore 1 mana every turn
        setCurrentActionPoint(getMaxActionPoint());
    }

    @Override
    public void endTurn() {
        setCanAct(false);
    }

    @Override
    public void attack(BaseMonsterPiece monsterPiece) {
        if(ATTACK_COST > getCurrentActionPoint()) {
            System.out.println("Not enough Action Point to attack!");
            return;
        }

        decreaseActionPoint(ATTACK_COST);
        monsterPiece.takeDamage(getAttackDamage());

        changeDirection(Integer.compare(monsterPiece.getCol(), getCol()));
        EffectManager.getInstance()
                .renderEffect(EffectManager.TYPE.AROUND_SELF,
                        this,
                        monsterPiece.getRow(), monsterPiece.getCol(),
                        EffectManager.getInstance().createInPlaceEffects(0),
                        new EffectConfig(0, 8, 0, 1.25));

        System.out.println("Attack success");
        GUIManager.getInstance().updateGUI();
    }

    @Override
    public void takeDamage(int damage) {
        System.out.println("Damage taken: " + damage);

        //Check if the player has any effect
        if(EffectBuffs != null) {
            if(EffectBuffs.containsKey("Ice Shield")) {
                damage = (damage * 70) / 100;
                System.out.println("Damage reduced by 30% : " + damage);
            }
        }
        super.takeDamage(damage);


        spriteAnimation.changeAnimation(1, 1);
        new Thread(() -> {
            try {
                Thread.sleep(400);
                spriteAnimation.changeAnimation(4, 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
