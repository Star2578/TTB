package pieces.player;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.SpriteAnimation;
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

        //TODO this is animation testing
        animationImage = new ImageView(new Image(Config.knightIdlePath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateY(-8);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,1,4,30,40,5);
        spriteAnimation.start();
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
        int currentMonsterHp = monsterPiece.getCurrentHealth();
        monsterPiece.setCurrentHealth(currentMonsterHp - getAttackDamage());
    }
}
