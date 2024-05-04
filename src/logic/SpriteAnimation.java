package logic;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpriteAnimation extends AnimationTimer {

    protected ImageView imageView; //Image view that will display our sprite

    private final int totalFrames; //Total number of frames in the sequence
    private final float fps; //frames per second I.E. 24

    private int cols; //Number of columns on the sprite sheet
    private int rows; //Number of rows on the sprite sheet

    private final int frameWidth; //Width of an individual frame
    private final int frameHeight; //Height of an individual frame

    private int currentCol = 0;
    private int currentRow = 0;

    private long lastFrame;

    private boolean isPause = true;
    private boolean loop;

    public SpriteAnimation(ImageView imageView, int columns, int rows, int totalFrames, int frameWidth, int frameHeight, float framesPerSecond , boolean loop) {
        this.imageView = imageView;
        imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

        this.cols = columns;
        this.rows = rows;
        this.totalFrames = totalFrames;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.loop = loop;
        fps = framesPerSecond;
        lastFrame = System.nanoTime();

    }

    @Override
    public void handle(long now) {
        int frameJump = (int) Math.floor((now - lastFrame) / (1e9/ fps)); //Determine how many frames we need to advance to maintain frame rate independence

        if (frameJump >= 1) {
            lastFrame = now;
            if( (currentCol + 1) % cols < currentCol && !loop) {
                imageView.setVisible(false);
                stop();
            }
            currentCol = (currentCol + 1) % cols;
            imageView.setViewport(new Rectangle2D(currentCol * frameWidth, currentRow * frameHeight, frameWidth, frameHeight));
        }
    }

    @Override
    public void stop(){
        if(!this.isPause){
            imageView.setVisible(false);
            super.stop();
            this.isPause=true;
            currentCol=0;
        }
    }

    @Override
    public void start(){
        if(this.isPause) {
            imageView.setVisible(true);
            super.start();
            this.isPause=false;
        }
    }

    public void changeAnimation(int totalCols , int targetRows ){
        //in our game Sprite sheet design : new row -> new sprite animation
        this.cols = totalCols;

        //go back to first frame
        currentCol = 0; currentRow = targetRows;

        imageView.setViewport(new Rectangle2D(0,currentRow*frameHeight , frameWidth , frameHeight));
    }

    @Override
    public SpriteAnimation clone(){
        SpriteAnimation clone = new SpriteAnimation(new ImageView(new Image( imageView.getImage().getUrl()))
                ,cols,rows,totalFrames,frameWidth,frameHeight,fps,loop);
        return clone;
    }

    public ImageView getImageView() {
        return imageView;
    }
    public int getTotalFrames() {
        return totalFrames;
    }
    public float getFps() {
        return fps;
    }
    public int getCols() {
        return cols;
    }
    public int getRows() {
        return rows;
    }
    public int getCurrentCol() {
        return currentCol;
    }
    public int getCurrentRow() {
        return currentRow;
    }
    public int getFrameHeight() {
        return frameHeight;
    }
    public int getFrameWidth() {
        return frameWidth;
    }
    public boolean isLoop() {
        return loop;
    }
}