package pieces.player;

import javafx.animation.TranslateTransition;
import javafx.beans.Observable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.SpriteAnimation;
import logic.GameManager;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.BaseStatus;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Config;

import java.util.List;

public abstract class BasePlayerPiece extends BasePiece implements BaseStatus {
    private int currentHp;
    private int maxHp;
    private int currentActionPoint;
    private int maxActionPoint;
    private int currentMana;
    private int maxMana;
    private boolean canAct;
    protected int currentDirection;
    private int attackDamage;
    protected BaseSkill[] skills;
    protected final int ATTACK_COST = 1;

    protected SpriteAnimation spriteAnimation;
    public ImageView animationImage;
    protected SpriteAnimation meleeAttackAnimation;
    public ImageView meleeAttackImage;
    protected TranslateTransition moveTransition;

    //offset for image
    public int offsetX=0;
    public int offsetY=0;

    public BasePlayerPiece(int row, int col, int defaultDirection) {
        super("Player", new ImageView(Config.PlaceholderPath), row, col);
        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;
        canAct = false;
        if (defaultDirection == -1) {
            ImageView imageView = getTexture();
            imageView.setScaleX(-1); // Flipping the image horizontally
        }
        skills = new BaseSkill[8];
    }

    @Override
    public int getCurrentHealth() {
        return currentHp;
    }

    @Override
    public void setCurrentHealth(int health) {
        this.currentHp = Math.max(health, 0);
        if (currentHp == 0) onDeath();
    }

    public void takeDamage(int damage) {
        setCurrentHealth(currentHp - damage);
    }

    @Override
    public int getMaxHealth() {
        return maxHp;
    }

    public void decreaseActionPoint(int decrease) {
        this.currentActionPoint = Math.max(0, this.currentActionPoint - decrease);
        GUIManager.getInstance().updateGUI();
        if (currentActionPoint == 0) setCanAct(false);
    }

    public void setCurrentActionPoint(int currentActionPoint) {
        this.currentActionPoint = Math.max(currentActionPoint, 0);
    }

    public int getCurrentActionPoint() {
        return currentActionPoint;
    }

    public void setMaxActionPoint(int maxActionPoint) {
        this.maxActionPoint = Math.max(maxActionPoint, 1);
    }

    public int getMaxActionPoint() {
        return maxActionPoint;
    }

    public void setCanAct(boolean canAct) {
        this.canAct = canAct;
    }

    public boolean canAct() {
        return canAct;
    }

    public void decreaseMana(int decrease) {
        this.currentMana = Math.max(0, this.currentMana - decrease);
        GUIManager.getInstance().updateGUI();
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(currentMana, 0);
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(1, maxMana);
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = Math.max(attackDamage, 0);
    }

    public BaseSkill[] getSkills() {
        return skills;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        int maxHpBuffer = maxHp;
        this.maxHp = Math.max(maxHealth, 1);

        if (maxHp == maxHpBuffer) currentHp = maxHp;
        if (maxHp < currentHp) currentHp = maxHp;
    }

    @Override
    public boolean isAlive() {
        return currentHp > 0;
    }

    @Override
    public void onDeath() {
        // TODO: Call Game Over
        System.out.println("Game Over! You are dead");
    }

    public abstract void moveWithTransition(int row , int col);

    public abstract void startTurn();

    public abstract void endTurn();

    public abstract boolean validMove(int row, int col); // To set valid move for each classes

    public abstract boolean validAttack(int row, int col); // To set valid attack for each classes

    public void changeDirection(int direction) {

        if (direction != 1 && direction != -1) {
            return;
        }
        if (currentDirection != direction) {
            currentDirection = direction;
            ImageView imageView = animationImage;
            imageView.setScaleX(direction); // Flipping the image horizontally if direction is -1
        }
    }

    public abstract void attack(BaseMonsterPiece monsterPiece); // This will differ for each class of player

    protected abstract void setupAnimation();
}
