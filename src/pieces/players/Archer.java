package pieces.players;

import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.gameUI.GUIManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.enemies.BaseMonsterPiece;
import skills.archer.Halt;
import skills.archer.Rolling;
import skills.archer.Snipe;
import skills.archer.Targetlock;
import utils.Config;

import java.util.Map;

public class Archer extends BasePlayerPiece {
    public Archer(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);

        maxHp = 15;
        currentHp = maxHp;

        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;

        maxMana = 15;
        currentMana = maxMana;

        maxHp = 10;
        currentHp = maxHp;

        attackDamage = 3; // Base attack for player
        attackRange = 3;

        //add skill
        skills[0] = new Targetlock();
        skills[1] = new Halt();

        // class specifics skills
        classSpecifics[0] = new Targetlock();
        classSpecifics[1] = new Halt();
        classSpecifics[2] = new Snipe();
        classSpecifics[3] = new Rolling();

        //configs values for animation
        setTexture(new ImageView(new Image(Config.ArcherPath))); //static image for icon, ...
        setupAnimation(Config.ArcherAnimationPath, 0, -15, 32, 56 , true);
    }

    @Override
    public boolean validMove(int row, int col) {

        int currentRow = getRow();
        int currentCol = getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }

    @Override
    public boolean validAttack(int row, int col) {
        // For Archer, it's the same as his movement
        int currentRow = getRow();
        int currentCol = getCol();

        return Math.abs(row - currentRow) <= attackRange && Math.abs(col - currentCol) <= attackRange;
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
        setCurrentMana(getCurrentMana() + 1); // Archer restore 1 mana every turn
        setCurrentActionPoint(getMaxActionPoint());
    }

    @Override
    public void endTurn() {
        setCanAct(false);
    }

    @Override
    public void attack(BaseMonsterPiece monsterPiece) {
        if (ATTACK_COST > getCurrentActionPoint()) {
            System.out.println("Attack failed: Not enough Action Point");
            return;
        }

        decreaseActionPoint(ATTACK_COST);

        //make player face to target
        changeDirection(Integer.compare(monsterPiece.getCol(), getCol()));

        //=========<ARROW EFFECT>====================================================================
        EffectMaker.getInstance()
                .renderEffect( EffectMaker.TYPE.BULLET_TO_TARGET ,
                        this ,
                        monsterPiece.getRow(), monsterPiece.getCol(),
                        EffectMaker.getInstance().createInPlaceEffects(13) ,
                        new EffectConfig(-6 , 0 , -10 , 1.5) );
        //===========================================================================================
        PauseTransition pause = new PauseTransition(Duration.seconds(0.45));

        // Set the action to perform after the pause
        pause.setOnFinished(event -> {
            //=========<ATTACK EFFECT>====================================================================
            EffectMaker.getInstance()
                    .renderEffect( EffectMaker.TYPE.ON_TARGET ,
                            this ,
                            monsterPiece.getRow(), monsterPiece.getCol(),
                            EffectMaker.getInstance().createInPlaceEffects(14) ,
                            new EffectConfig(3 , 0 , 0 , 1.2) );
            monsterPiece.takeDamage(getAttackDamage());
            //===========================================================================================
        });

        // Start the pause
        pause.play();

        System.out.println("Attack success");
        GUIManager.getInstance().updateGUI();
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        //change to hit animation for 0.4 secs
        spriteAnimation.changeAnimation(1,1);
        new Thread(()-> {
            try {
                Thread.sleep(400);
                spriteAnimation.changeAnimation(4,0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
