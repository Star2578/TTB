package pieces.players;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import logic.gameUI.GUIManager;
import pieces.enemies.BaseMonsterPiece;
import skills.wizard.DragonFire;
import skills.wizard.Fireball;
import skills.wizard.IceShield;
import skills.wizard.RainOfFire;
import utils.Config;

import java.util.Map;

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
        attackRange = 2;

        //add skill
        skills[0] = new Fireball();
        skills[1] = new IceShield();
        // class specifics skills
        classSpecifics[0] = new Fireball();
        classSpecifics[1] = new IceShield();
        classSpecifics[2] = new DragonFire();
        classSpecifics[3] = new RainOfFire();


        //configs values for animation
        setTexture(new ImageView(new Image(Config.WizardPath))); //static image for icon, ...
        setupAnimation(Config.WizardAnimationPath, 0, -15, 32, 56, true);
    }

    @Override
    public boolean validMove(int row, int col) {
        return Math.abs(row - getRow()) <= 1 && Math.abs(col - getCol()) <= 1;
    }

    @Override
    public boolean validAttack(int row, int col) {
        return Math.abs(row - getRow()) <= attackRange && Math.abs(col - getCol()) <= attackRange;
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
        setCurrentMana(getCurrentMana() + 3); // Wizard restore 3 mana every turn
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

        changeDirection(Integer.compare(monsterPiece.getCol(), getCol()));
        // ----------------------Attack Animation----------------------
        EffectMaker.getInstance()
                .renderEffect(EffectMaker.TYPE.BULLET_TO_TARGET,
                        this,
                        monsterPiece.getRow(), monsterPiece.getCol(),
                        EffectMaker.getInstance().createInPlaceEffects(22),
                        new EffectConfig(8, 8, 32, 1.4));
        // -------------------------------------------------------------
        // Create a PauseTransition with a duration of 0.2 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(0.2));

        // Set the action to perform after the pause
        pause.setOnFinished(event -> {
            // ----------------------Attack Take Damage Animation----------------------
            EffectMaker.getInstance()
                    .renderEffect(EffectMaker.TYPE.ON_TARGET,
                            this,
                            monsterPiece.getRow(), monsterPiece.getCol(),
                            EffectMaker.getInstance().createInPlaceEffects(23),
                            new EffectConfig(-16, -19, 0, 1));
            monsterPiece.takeDamage(getAttackDamage());
            // -------------------------------------------------------------
        });

        // Start the pause
        pause.play();


        System.out.println("Attack success");
        GUIManager.getInstance().updateGUI();
    }

    @Override
    public void takeDamage(int damage) {
        System.out.println("Damage taken: " + damage);

        //Check if the player has any effect
//        if(EffectBuffs != null) {
//            if(EffectBuffs.containsKey("Ice Shield")) {
//                damage = (damage * 70) / 100;
//                System.out.println("Damage reduced by 30% : " + damage);
//            }
//            if(EffectBuffs.containsKey("Rho Aias")) {
//                damage = (damage * 20) / 100;
//                System.out.println("Damage reduced by 80% : " + damage);
//            }
//        }
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
