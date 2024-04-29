package logic.effect;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;

public class Effect extends SpriteAnimation {

    public boolean canKill = false;
    private static final int defaultTimeout = 30;
    private int turnRemain = 1;
    private BasePiece owner;


    //constructor with parameters
    public Effect(ImageView imageView, int columns, int rows, int totalFrames, int frameWidth, int frameHeight, float framesPerSecond, boolean loop) {
        super(imageView, columns, rows, totalFrames, frameWidth, frameHeight, framesPerSecond, loop);
    }

    //constructor using SpriteAnimation object
    public Effect(SpriteAnimation base){
        super(new ImageView(new Image( base.imageView.getImage().getUrl()))
                ,base.cols,base.rows,base.totalFrames,base.frameWidth,base.frameHeight,base.fps,base.loop);
    }

    //clone itself and return
    public Effect clone(){
        Effect copy = new Effect(super.clone());
        copy.setTimeout(defaultTimeout);
        return copy;
    }

    public void setTimeout(double value){
        new Timeline(new KeyFrame(Duration.seconds(value), actionEvent ->canKill = true)).play();
    }

    public void setTurnRemain(int turnDuration){
        turnRemain = turnDuration;
    }

    public int getTurnRemain(){
        return turnRemain;
    }

    public void setOwner(BasePiece owner){
        this.owner = owner;
    }

    public BasePiece getOwner(){
        return owner;
    }

}
