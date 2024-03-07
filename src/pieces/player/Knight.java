package pieces.player;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.SpriteAnimation;
import pieces.enemies.BaseMonsterPiece;
import skills.knight.Heal;
import skills.knight.Slash;
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


        setTextureByPath(Config.KnightPath);

        //add skill
        skills[0] = new Slash();
        skills[1] = new Heal();

        //configs values for animation
        setupAnimation();

    }

    public void moveWithTransition(int row , int col){
        //stop player from do other action
        setCanAct(false);
        spriteAnimation.changeAnimation(4 , 2);
        //slowly move to target col,row
        moveTransition.setToX( (col-getCol()) * SQUARE_SIZE + offsetX);
        moveTransition.setToY( (row-getRow()) * SQUARE_SIZE + offsetY);

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
        });

        moveTransition.play();
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
            return;
        }

        decreaseActionPoint(ATTACK_COST);
        monsterPiece.takeDamage(getAttackDamage());

        //make player face to target
        changeDirection(Integer.compare(monsterPiece.getCol(), getCol()));
        if(currentDirection == -1)meleeAttackImage.setScaleX(-1);
            else meleeAttackImage.setScaleX(1);

//        //rotate effect to target direction
//        double xDist = monsterPiece.getCol() - getCol();
//        double yDist = monsterPiece.getRow() - getRow();
//        double degree = (Math.acos( xDist / Math.sqrt( xDist*xDist + yDist*yDist) )) * (180.0 / Math.PI);
//        degree = yDist>0? degree*-1 : degree;
//        meleeAttackImage.setRotate( yDist<0? 360-degree : -degree);

        //play effect on monster position
        meleeAttackImage.setX( ( getCol() * SQUARE_SIZE) - (SQUARE_SIZE / 2) - (meleeAttackImage.getFitWidth() / 2) );
        meleeAttackImage.setY( ( getRow() * SQUARE_SIZE) - (SQUARE_SIZE / 2) - (meleeAttackImage.getFitHeight() / 2) );
        meleeAttackImage.toFront();
        //meleeAttackAnimation.start();
        //TODO=================================


        System.out.println("Attack success");
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

    @Override
    protected void setupAnimation(){
        //===================<animation section>==========================================
        offsetX=0;
        offsetY=-15;
        //idle sprite animations for player
        animationImage = new ImageView(new Image(Config.KnightAnimationPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,32,56,6);
        spriteAnimation.start();

        //attack animation for player
        meleeAttackImage = new ImageView(new Image(Config.meleeAttackPath));
        meleeAttackImage.setPreserveRatio(true);
        meleeAttackImage.setFitWidth(50);
        meleeAttackImage.setDisable(true);
        meleeAttackImage.setVisible(true);
        meleeAttackAnimation = new SpriteAnimation(meleeAttackImage , 5 , 1 , 5 , 37 , 32 , 8);
        meleeAttackAnimation.setLoop(false);

        //setup moveTranslate behaviour
        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }
}
