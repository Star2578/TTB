package pieces.player;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.*;
import logic.ui.GUIManager;
import pieces.BasePiece;
import pieces.BaseStatus;
import pieces.enemies.BaseMonsterPiece;
import skills.BaseSkill;
import utils.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static utils.Config.*;

public abstract class BasePlayerPiece extends BasePiece implements BaseStatus {
    // Player stats
    protected int currentHp;
    protected int maxHp;
    protected int currentActionPoint;
    protected int maxActionPoint;
    protected int currentMana;
    protected int maxMana;
    protected int attackDamage;
    protected boolean animationFinished = true;

    protected boolean canAct; // status
    protected BaseSkill[] skills; // skill list
    protected final int ATTACK_COST = 1;
    protected int attackRange = 1;

    // Animations
    protected SpriteAnimation meleeAttackAnimation;
    public ImageView meleeAttackImage;

    // Buffs
    protected int buffturn;
    protected Map<String, Integer> EffectBuffs = new HashMap<>();

    public BasePlayerPiece(int row, int col, int defaultDirection) {
        super(Config.ENTITY_TYPE.PLAYER, new ImageView(Config.PlaceholderPath), row, col, defaultDirection);
        canAct = false;

        skills = new BaseSkill[8]; // Player can have up to 8 skills
    }


    /******************************************
     *            Abstract Fields
     ******************************************/
    public abstract boolean validAttack(int row, int col); // To set valid attack for each classes
    public abstract void attack(BaseMonsterPiece monsterPiece); // This will differ for each class of player
    public abstract boolean validMove(int row, int col); // To set valid move for each classes
    @Override
    protected void setupAnimation(String imgPath, int offsetX, int offsetY, int width, int height , boolean loop){
        //===================<animation section>==========================================
        this.offsetX=offsetX;
        this.offsetY=offsetY;
        //idle sprite animations for player
        animationImage = new ImageView(new Image(imgPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,width,height,6 , loop);
        spriteAnimation.start();

        //attack animation for player
        meleeAttackImage = new ImageView(new Image(Config.meleeAttackPath));
        meleeAttackImage.setPreserveRatio(true);
        meleeAttackImage.setFitWidth(50);
        meleeAttackImage.setDisable(true);
        meleeAttackImage.setVisible(true);
        meleeAttackAnimation = new SpriteAnimation(meleeAttackImage , 5 , 1 , 5 , 37 , 32 , 8 , false);

        //setup moveTranslate behaviour
        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }
    public abstract void startTurn();
    public abstract void endTurn();


    /******************************************
     *                  Utils
     ******************************************/
    @Override
    public void takeDamage(int damage) {
        setCurrentHealth(currentHp - damage);
        SoundManager.getInstance().playSoundEffect(sfx_hurtSound);
        GUIManager.getInstance().updateGUI();
    }
    public void decreaseMana(int decrease) {
        this.currentMana = Math.max(0, this.currentMana - decrease);
        GUIManager.getInstance().updateGUI();
    }
    public void decreaseActionPoint(int decrease) {
        this.currentActionPoint = Math.max(0, this.currentActionPoint - decrease);
        GUIManager.getInstance().updateGUI();

        // Auto End Turn when player is out of action point
        if (this.currentActionPoint == 0 && GameManager.getInstance().autoEndTurn && animationFinished) {
            if (TurnManager.getInstance().isPlayerTurn) TurnManager.getInstance().endPlayerTurn();
        }
    }
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

    @Override
    public void moveWithTransition(int row , int col){
        //stop player from do other action
        setCanAct(false);
        animationFinished = false;
        spriteAnimation.changeAnimation(4 , 2);
        //slowly move to target col,row
        moveTransition.setToX( (col-getCol()) * SQUARE_SIZE + offsetX);
        moveTransition.setToY( (row-getRow()) * SQUARE_SIZE + offsetY);
        GUIManager.getInstance().disableButton();

        moveTransition.setOnFinished(actionEvent->{
            //set image layering depend on row
            animationImage.setViewOrder(BOARD_SIZE - row);
            //move real coordinate to new col,row
            animationImage.setX(col*SQUARE_SIZE + offsetX);
            animationImage.setY(row*SQUARE_SIZE + offsetY);
            //set translateProperty back to default
            animationImage.translateXProperty().set(offsetX);
            animationImage.translateYProperty().set(offsetY);
            //now player can do actions
            spriteAnimation.changeAnimation(4 , 0);
            setCanAct(true);
            GUIManager.getInstance().enableButton();
            setRow(row);
            setCol(col);

            for (Point2D coordinate : GameManager.getInstance().doorAt) {
                if (coordinate.getX() == getRow() && coordinate.getY() == getCol()) {
                    GameManager.getInstance().gameScene.generateNewFloor();
                    break;
                }
            }

            animationFinished = true; // Finish animation
        });
        moveTransition.play();
    }

    /******************************************
     *             getter setter
     ******************************************/
    @Override
    public int getCurrentHealth() {
        return currentHp;
    }
    @Override
    public int getMaxHealth() {
        return maxHp;
    }
    @Override
    public void setCurrentHealth(int health) {
        this.currentHp = Math.max(health, 0);
        this.currentHp = Math.min(getMaxHealth(), currentHp);
        GUIManager.getInstance().updateGUI();

        if (currentHp == 0) onDeath();
    }
    @Override
    public void setMaxHealth(int maxHealth) {
        int maxHpBuffer = maxHp;
        this.maxHp = Math.max(maxHealth, 1);

        if (maxHp == maxHpBuffer) currentHp = maxHp;
        if (maxHp < currentHp) currentHp = maxHp;

        GUIManager.getInstance().updateGUI();
    }
    public int getAttackDamage() {
        return attackDamage;
    }
    public void setAttackDamage(int attackDamage) {
        this.attackDamage = Math.max(attackDamage, 0);
        GUIManager.getInstance().updateGUI();
    }
    public int getAttackRange() {
        return attackRange;
    }
    public int getCurrentMana() {
        return currentMana;
    }
    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(currentMana, 0);
        this.currentMana = Math.min(this.currentMana, getMaxMana());
        GUIManager.getInstance().updateGUI();
    }
    public int getMaxMana() {
        return maxMana;
    }
    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(1, maxMana);
        GUIManager.getInstance().updateGUI();
    }
    public void setCurrentActionPoint(int currentActionPoint) {
        this.currentActionPoint = Math.max(currentActionPoint, 0);
        this.currentActionPoint = Math.min(this.currentActionPoint, getMaxActionPoint());
        GUIManager.getInstance().updateGUI();
    }
    public void setCurrentActionPointForce(int currentActionPoint) {
        this.currentActionPoint = currentActionPoint;
        GUIManager.getInstance().updateGUI();
    }
    public int getCurrentActionPoint() {
        return currentActionPoint;
    }
    public void setMaxActionPoint(int maxActionPoint) {
        this.maxActionPoint = Math.max(maxActionPoint, 1);
        GUIManager.getInstance().updateGUI();
    }
    public int getMaxActionPoint() {
        return maxActionPoint;
    }
    public void setCanAct(boolean canAct) {
        this.canAct = canAct;
    }
    public boolean canAct() {
        if (canAct && GameManager.getInstance().gameScene != null) {
            GUIManager.getInstance().updateCursor(GameManager.getInstance().gameScene.getScene(), DefaultCursor);
            if (GameManager.getInstance().selectedSkill != null || GameManager.getInstance().selectedItem != null) {
                GUIManager.getInstance().updateCursor(GameManager.getInstance().gameScene.getScene(), HandCursor);
            } else if (GUIManager.getInstance().isInAttackMode) {
                GUIManager.getInstance().updateCursor(GameManager.getInstance().gameScene.getScene(), AttackCursor);
            }
        }
        return canAct;
    }
    public BaseSkill[] getSkills() {
        return skills;
    }
    @Override
    public boolean isAlive() {
        return currentHp > 0;
    }
    @Override
    public void onDeath() {
        System.out.println("Game Over! You are dead!");
        GameManager.getInstance().GameOver();
    }

    public void addBuff(int buff_duration, String buff_name) {
        EffectBuffs.put(buff_name, buff_duration);
        System.out.println(buff_name + " adding");
    }
}