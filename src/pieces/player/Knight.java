package pieces.player;

import logic.SoundManager;
import logic.ui.GUIManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.enemies.BaseMonsterPiece;
import skills.archer.Snipe;
import skills.knight.Dart;
import skills.knight.Heal;
import skills.knight.Slash;
import skills.knight.Stomp;
import utils.Config;

import java.util.ArrayList;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Knight extends BasePlayerPiece {
    public Knight(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);

        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;

        maxMana = 10;
        currentMana = maxMana;

        maxHp = 20;
        currentHp = maxHp;

        attackDamage = 3; // Base attack for player

        //add skill
        skills[0] = new Slash();
        skills[1] = new Heal();
        skills[2] = new Dart();
        skills[3] = new Stomp();

        //configs values for animation
        setupAnimation(Config.KnightAnimationPath, 0, -15, 32, 56 , true);
    }

    @Override
    public boolean validMove(int row, int col) {
        
        int currentRow = getRow();
        int currentCol = getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }

    @Override
    public boolean validAttack(int row, int col) {
        // For Knight, it's the same as his movement
        int currentRow = getRow();
        int currentCol = getCol();

        return Math.abs(row - currentRow) <= 1 && Math.abs(col - currentCol) <= 1;
    }

    @Override
    public void startTurn() {
        setCanAct(true);
        setCurrentMana(getCurrentMana() + 1); // Knight restore 1 mana every turn
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
            SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
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
        }).start();
    }
}
