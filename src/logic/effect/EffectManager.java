package logic.effect;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import logic.GameManager;
import logic.SpriteAnimation;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;

import static utils.Config.SQUARE_SIZE;

public class EffectManager {

    private SpriteAnimation[] environmentEffects;
    private ArrayList<Effect> effects = new ArrayList<>();
    public Pane effectPane = new Pane();

    public enum TYPE{
        AROUND_SELF,
        ON_TARGET,

    }
    private static EffectManager instance;

    public static EffectManager getInstance() {
        if (instance == null) {
            instance = new EffectManager();
        }
        return instance;
    }

    public EffectManager(){

        //!!effect ordering matter!!

        //Knight attack animation 0
        Effect Knight_Attack = new Effect(
                new ImageView(new Image(Config.meleeAttackPath)),5,1,5,35,37,8,false);
        effects.add(Knight_Attack);

        Effect Skill_Slash = new Effect(
                new ImageView(new Image(Config.skillSlashPath)) , 3 ,1 ,3,32,64,15,false);
        effects.add(Skill_Slash);

        Effect Dead_Smoke = new Effect(
                new ImageView(new Image(Config.DeadEffectPath)) , 5 , 1 , 5 , 32 , 32 , 10 , false);
        effects.add(Dead_Smoke);
    }

    public Effect createInPlaceEffects(int index){
        Effect copy = effects.get(index).clone();
        return copy;
    }

    public void renderEffect(Enum<TYPE> typeEnum, BasePlayerPiece player , BasePiece target , SpriteAnimation effect , EffectConfig config){

        if (typeEnum == TYPE.AROUND_SELF){
        //effect will occur around player, also rotate and face to target

            EffectManager.getInstance().effectPane.getChildren().add( effect.imageView );

            //find angle toward enemy
            double x =  target.getCol() - GameManager.getInstance().player.getCol();
            double y = target.getRow() - GameManager.getInstance().player.getRow();
            double angleRadian = Math.atan2(y , x);
            double angleDegree = Math.atan2(y , x) * (180.0 / Math.PI);

            //set effect position (angle is in account)
            effect.imageView.setX(GameManager.getInstance().player.getCol()*SQUARE_SIZE
                                    + (config.distanceFromOrigin * Math.cos(angleRadian) )
                                    + config.offsetX);
            effect.imageView.setY(GameManager.getInstance().player.getRow()*SQUARE_SIZE
                                    + (config.distanceFromOrigin * Math.sin(angleRadian) )
                                    + config.offsetY);

            //rotate effect
            effect.imageView.setRotate(angleDegree);

            //scale effect size
            effect.imageView.setScaleX(config.scale);
            effect.imageView.setScaleY(config.scale);

            effect.imageView.toFront();
            effect.imageView.setDisable(true);

            effect.start();
        }
        else if(typeEnum == TYPE.ON_TARGET){
        //effect will occur on target position

            //add effect to pane
            EffectManager.getInstance().effectPane.getChildren().add(effect.imageView);

            //scale effect size + direction
            effect.imageView.setScaleX(config.scale * player.getCurrentDirection());
            effect.imageView.setScaleY(config.scale);

            //set effect on enemy position
            effect.imageView.setX(target.getCol()*SQUARE_SIZE + config.offsetX);
            effect.imageView.setY(target.getRow()*SQUARE_SIZE + config.offsetY);

            effect.imageView.toFront();
            effect.imageView.setDisable(true);

            effect.start();
        }

    }

    public void clearDeadEffect(){
        effects.removeIf(effect -> effect.canKill);
    }
}