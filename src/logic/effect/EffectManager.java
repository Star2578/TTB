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
        ON_SELF,
        AROUND_SELF_ENEMY


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
        //Knight Skill Slash 1
        Effect Skill_Slash = new Effect(
                new ImageView(new Image(Config.skillSlashPath)) , 3 ,1 ,3,32,64,15,false);
        effects.add(Skill_Slash);
        //Dead effect 2
        Effect Dead_Smoke = new Effect(
                new ImageView(new Image(Config.DeadEffectPath)) , 5 , 1 , 5 , 32 , 32 , 15 , false);
        effects.add(Dead_Smoke);
        //Knight Skill Stomp 3
        Effect Skill_Stomp = new Effect(
                new ImageView(new Image(Config.skillStompPath)) , 4 , 1 , 4 , 33 , 32 , 15 , false);
        effects.add(Skill_Stomp);
        //Knight Skill Dart 4
        Effect Skill_Dart = new Effect(
                new ImageView(new Image(Config.skillDartPath)) , 3 , 1 , 3 , 32 , 44 , 15 , false);
        effects.add(Skill_Dart);
        //Skill Heal 5
        Effect Skill_Heal = new Effect(
                new ImageView(new Image(Config.skillHealPath)) , 6 , 2 , 10 , 32 , 32 , 11 , false);
        effects.add(Skill_Heal);
        //Bomb Explosion 6
        Effect Bomb_Explosion = new Effect(
                new ImageView(new Image(Config.BombExplosionPath)) , 18 , 1 , 18 , 48 , 48 , 15 , false);
        effects.add(Bomb_Explosion);
        //Necromancer Summon 7
        Effect Necromancer_Summon = new Effect(
                new ImageView(new Image(Config.NecromancerSummonPath)) , 16 , 1 , 16 , 48 , 64 , 12 , false);
        effects.add(Necromancer_Summon);
        //Stun Effect 8
        Effect Stun_Effect = new Effect(
                new ImageView(new Image(Config.StunEffectPath)) , 3 , 2 , 6 , 17 , 16 , 5 , true);
        effects.add(Stun_Effect);
        //Enemies Normal Attack 9
        Effect Enemies_Normal_Attack = new Effect(
                new ImageView(new Image(Config.EnemiesNormalAttackPath)) , 6 , 2 , 12 , 100 , 103 , 12 , false);
        effects.add(Enemies_Normal_Attack);
        //Vampire Skill 10
        Effect Vampire_Skill_Effect = new Effect(
                new ImageView(new Image(Config.VampireSkillPath)) , 6 , 4 , 24 , 100 , 102 , 12 , false);
        effects.add(Vampire_Skill_Effect);
        //Necromancer Attack 11
        Effect Necromancer_Attack = new Effect(
                new ImageView(new Image(Config.NecromancerAttackPath)) , 10 , 2 , 16 , 40 , 32 , 12 , false);
        effects.add(Necromancer_Attack);
        //Skeleton Attack 12
        Effect Skeleton_Attack = new Effect(
                new ImageView(new Image(Config.SkeletonAttackPath)) , 5 , 1 , 5 , 39 , 36 , 12 , false);
        effects.add(Skeleton_Attack);
        //Archer Attack 13
        Effect Archer_Attack = new Effect(
                new ImageView(new Image(Config.rangedAttackPath)) , 5 , 1 , 5 , 40 , 30 , 12 , false);
        effects.add(Archer_Attack);

    }

    public Effect createInPlaceEffects(int index){
        Effect copy = effects.get(index).clone();
        return copy;
    }

    public void renderEffect(Enum<TYPE> typeEnum, BasePlayerPiece player , int row , int col, SpriteAnimation effect , EffectConfig config){

        if (typeEnum == TYPE.AROUND_SELF){
        //effect will occur around player, also rotate and face to target

            EffectManager.getInstance().effectPane.getChildren().add( effect.imageView );

            //find angle toward enemy
            double x = col - GameManager.getInstance().player.getCol();
            double y = row - GameManager.getInstance().player.getRow();
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
            //effect.imageView.setScaleX(config.scale * player.getCurrentDirection());
            effect.imageView.setScaleY(config.scale);

            //set effect on enemy position
            effect.imageView.setX(col*SQUARE_SIZE + config.offsetX);
            effect.imageView.setY(row*SQUARE_SIZE + config.offsetY);

            effect.imageView.toFront();
            effect.imageView.setDisable(true);

            effect.start();
        }else if(typeEnum == TYPE.ON_SELF){
            //effect will occur on target position

            //add effect to pane
            EffectManager.getInstance().effectPane.getChildren().add(effect.imageView);

            //scale effect size + direction
            effect.imageView.setScaleX(config.scale);
            effect.imageView.setScaleY(config.scale);

            //set effect on enemy position
            effect.imageView.setX(col*SQUARE_SIZE + config.offsetX);
            effect.imageView.setY(row*SQUARE_SIZE + config.offsetY);

            effect.imageView.toFront();
            effect.imageView.setDisable(true);

            effect.start();
        }else if (typeEnum == TYPE.AROUND_SELF_ENEMY){
            //effect will occur around player, also rotate and face to target

            EffectManager.getInstance().effectPane.getChildren().add( effect.imageView );

            //find angle toward enemy
            double x = GameManager.getInstance().player.getCol() - col;
            double y = GameManager.getInstance().player.getRow() - row;
            double angleRadian = Math.atan2(y , x);
            double angleDegree = Math.atan2(y , x) * (180.0 / Math.PI);

            //set effect position (angle is in account)
            effect.imageView.setX(col*SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.cos(angleRadian) )
                    + config.offsetX);
            effect.imageView.setY(row*SQUARE_SIZE
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

    }

    public void clearDeadEffect(){
        effects.removeIf(effect -> effect.canKill);
    }


}
