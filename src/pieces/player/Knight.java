package pieces.player;

import logic.GameManager;
import pieces.enemies.BaseMonsterPiece;
import utils.Config;

public class Knight extends BasePlayerPiece {
    public Knight(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);
        setMaxMana(10);
        setCurrentMana(getMaxMana());
        setMaxHealth(20);
        setCurrentHealth(getMaxHealth());
        setTextureByPath(Config.KnightPath);
        setCanAct(false);
        setAttackDamage(3);
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
        setCurrentMana(getMaxMana());
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
        int currentMonsterHp = monsterPiece.getCurrentHealth();
        monsterPiece.setCurrentHealth(currentMonsterHp - getAttackDamage());
        System.out.println("Attack success");
    }
}
