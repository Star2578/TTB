package pieces.player;

import logic.ui.GUIManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.enemies.BaseMonsterPiece;
import skills.archer.Halt;
import skills.archer.Snipe;
import skills.archer.Targetlock;
import skills.archer.Teleport;
import skills.knight.Heal;
import skills.knight.Slash;
import utils.Config;

import java.util.ArrayList;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Archer extends BasePlayerPiece {
    public Archer(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);

        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;

        maxMana = 15;
        currentMana = maxMana;

        maxHp = 15;
        currentHp = maxHp;

        attackDamage = 5; // Base attack for player

        //add skill
        skills[0] = new Targetlock();
        skills[1] = new Teleport();
        skills[2] = new Halt();
        skills[3] = new Snipe();

        //configs values for animation
        setupAnimation(Config.ArcherAnimationPath, 0, -12, 32, 48 , true);
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

        return Math.abs(row - currentRow) <= 4 && Math.abs(col - currentCol) <= 4;
    }

    @Override
    public void startTurn() {
        setCanAct(true);
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
        monsterPiece.takeDamage(getAttackDamage());

        //make player face to target
        changeDirection(Integer.compare(monsterPiece.getCol(), getCol()));

        //attack effect
        EffectManager.getInstance()
                .renderEffect( EffectManager.TYPE.ON_TARGET ,
                        this ,
                        monsterPiece ,
                        EffectManager.getInstance().createInPlaceEffects(0) ,
                        new EffectConfig(0 , 8 , 0 , 1.25) );

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
        }).start();;
    }
}
