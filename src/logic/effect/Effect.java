package logic.effect;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.SpriteAnimation;
import pieces.BasePiece;

public class Effect extends SpriteAnimation {

    public boolean canKill = false;
    private static final int defaultTimeout = 30;
    private int turnRemain = 1;
    private BasePiece owner;
    private EffectConfig config;
  

    //constructor with parameters
    public Effect(ImageView imageView, int columns, int rows, int totalFrames, int frameWidth, int frameHeight, float framesPerSecond, boolean loop) {
        super(imageView, columns, rows, totalFrames, frameWidth, frameHeight, framesPerSecond, loop);
    }

    //constructor using SpriteAnimation object
    public Effect(SpriteAnimation base){
        super(new ImageView(new Image( base.getImageView().getImage().getUrl()))
                ,base.getCols(),base.getRows(),base.getTotalFrames(),base.getFrameWidth(),base.getFrameHeight(),base.getFps(),base.isLoop());
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

    public void setConfig(EffectConfig config){this.config = config;}
    public EffectConfig getConfig(){ return config;}

    public void setOwner(BasePiece owner){
        this.owner = owner;
    }

    public BasePiece getOwner(){
        return owner;
    }

    public void bindToOwnerMovement(BasePiece owner){
        setOwner(owner);
        Effect thisEffect = this;

        DoubleBinding xTranslate = new DoubleBinding(){
            @Override
            protected double computeValue() {return owner.animationImage.getTranslateX() + thisEffect.getConfig().offsetX ;}
            {bind(owner.animationImage.translateXProperty(),  thisEffect.imageView.translateXProperty() );}
        };
        DoubleBinding xPosition = new DoubleBinding(){
            @Override
            protected double computeValue() {return owner.animationImage.getX() ;}
            {bind(owner.animationImage.xProperty(),  thisEffect.imageView.xProperty() );}
        };
        DoubleBinding yTranslate = new DoubleBinding(){
            @Override
            protected double computeValue() {return owner.animationImage.getTranslateY() + thisEffect.getConfig().offsetY ;}
            {bind(owner.animationImage.translateYProperty(),  thisEffect.imageView.translateYProperty() );}
        };
        DoubleBinding yPosition = new DoubleBinding(){
            @Override
            protected double computeValue() {return owner.animationImage.getY()  ;}
            {bind(owner.animationImage.yProperty(),  thisEffect.imageView.yProperty() );}
        };


        thisEffect.imageView.translateXProperty().bind(xTranslate);
        thisEffect.imageView.translateYProperty().bind(yTranslate);
        thisEffect.imageView.xProperty().bind(xPosition);
        thisEffect.imageView.yProperty().bind(yPosition);

    }
}
