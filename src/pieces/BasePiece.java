package pieces;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.SpriteAnimation;
import utils.Config;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class BasePiece {
    protected Config.ENTITY_TYPE type;
    protected ImageView texture;
    protected int row;
    protected int col;
    protected SpriteAnimation spriteAnimation;
    public ImageView animationImage;
    protected TranslateTransition moveTransition;
    protected int offsetX=0;
    protected int offsetY=0;

    protected BasePiece(Config.ENTITY_TYPE type, ImageView texture, int row, int col) {
        this.type = type;
        this.texture = texture;
        this.row = row;
        this.col = col;
//        test
        this.animationImage = new ImageView();
    }

    // Getters and setters
    public Config.ENTITY_TYPE getType() {
        return type;
    }
    public ImageView getTexture() {
        return texture;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public int getOffsetX() {
        return offsetX;
    }
    public int getOffsetY() {
        return offsetY;
    }
    protected void setupAnimation(String imgPath, int offsetX, int offsetY, int width, int height , boolean loop) {
        //===================<animation section>==========================================
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        //sprite animations for piece
        animationImage = new ImageView(new Image(imgPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,width,height,5,loop);
        spriteAnimation.start();

        //setup moveTranslate behaviour
        moveTransition = new TranslateTransition();
        moveTransition.setNode(animationImage);
        moveTransition.setDuration(Duration.millis(600));
        moveTransition.setCycleCount(1);
        //================================================================================
    }
    public void moveWithTransition(int row, int col) {
        spriteAnimation.changeAnimation(4,  1);

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

            spriteAnimation.changeAnimation(4,  0);

            setRow(row);
            setCol(col);
        });

        moveTransition.play();
    }
}
