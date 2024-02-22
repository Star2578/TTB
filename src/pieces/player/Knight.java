package pieces.player;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import pieces.enemies.BaseMonsterPiece;
import utils.Config;

import static utils.Config.SQUARE_SIZE;

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

        //===================<animation section>==========================================
        offsetX=3;
        offsetY=-8;
        //sprite animations for player
        animationImage = new ImageView(new Image(Config.knightIdlePath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,1,4,30,40,5);
        spriteAnimation.start();

        //setup moveTranslate behaviour

        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }

    public void moveWithTransition(int col , int row){
        //stop player from do other action
        setCanAct(false);
        //slowly move to target col,row
        moveTransition.setToX( (col-getCol()) * SQUARE_SIZE + offsetX);
        moveTransition.setToY( (row-getRow()) * SQUARE_SIZE + offsetY);

        moveTransition.setOnFinished(actionEvent->{
            //move real coordinate to new col,row
            animationImage.setX(col*SQUARE_SIZE);
            animationImage.setY(row*SQUARE_SIZE);
            //set translateProperty back to default
            animationImage.translateXProperty().set(offsetX);
            animationImage.translateYProperty().set(offsetY);
            //now player can do actions
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
