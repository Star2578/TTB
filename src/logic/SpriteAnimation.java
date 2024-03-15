package logic;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

public class SpriteAnimation extends AnimationTimer {

    private ImageView imageView; //Image view that will display our sprite

    private final int totalFrames; //Total number of frames in the sequence
    private final float fps; //frames per second I.E. 24

    private int cols; //Number of columns on the sprite sheet
    private int rows; //Number of rows on the sprite sheet

    private final int frameWidth; //Width of an individual frame
    private final int frameHeight; //Height of an individual frame

    private int currentCol = 0;
    private int currentRow = 0;

    private long lastFrame = 0;

    private boolean isPause = true;
    private boolean loop = true;

    public SpriteAnimation(ImageView imageView, int columns, int rows, int totalFrames, int frameWidth, int frameHeight, float framesPerSecond) {
        this.imageView = imageView;
        imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

        this.cols = columns;
        this.rows = rows;
        this.totalFrames = totalFrames;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        fps = framesPerSecond;
        lastFrame = System.nanoTime();
    }

    @Override
    public void handle(long now) {
        int frameJump = (int) Math.floor((now - lastFrame) / (1e9/ fps)); //Determine how many frames we need to advance to maintain frame rate independence

        if (frameJump >= 1) {
            lastFrame = now;
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

    public void setLoop(boolean b){
        this.loop=b;
    }

    public void changeAnimation(int totalCols , int targetRows ){
        //in our game Sprite sheet design : new row -> new sprite animation
        this.cols = totalCols;

        //go back to first frame
        currentCol = 0; currentRow = targetRows;

        imageView.setViewport(new Rectangle2D(currentCol*frameWidth,currentRow*frameHeight , frameWidth , frameHeight));
    }
}
