package logic.effect;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import logic.GameManager;
import logic.SpriteAnimation;
import pieces.BasePiece;
import utils.Config;

import java.util.ArrayList;

import static utils.Config.SQUARE_SIZE;

public class EffectMaker {

    private SpriteAnimation[] environmentEffects;
    private ArrayList<Effect> effects = new ArrayList<>();

    public Pane effectPane = new Pane();
    public ArrayList<Effect> runningEffects = new ArrayList<>();

    public enum TYPE{
        AROUND_SELF,
        ON_TARGET,
        ON_SELF,
        AROUND_SELF_ENEMY,
        BULLET_TO_TARGET,
        BULLET_TO_TARGET_ENEMY

    }
    private static EffectMaker instance;

    public static EffectMaker getInstance() {
        if (instance == null) {
            instance = new EffectMaker();
        }
        return instance;
    }

    public EffectMaker(){

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
                new ImageView(new Image(Config.skillHealPath)) , 4 , 4 , 16 , 128 , 128 , 7 , false);
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
                new ImageView(new Image(Config.NecromancerAttackPath)) , 10 , 2 , 16 , 40 , 32 , 7 , false);
        effects.add(Necromancer_Attack);
        //Skeleton Attack 12
        Effect Skeleton_Attack = new Effect(
                new ImageView(new Image(Config.SkeletonAttackPath)) , 5 , 1 , 5 , 39 , 36 , 12 , false);
        effects.add(Skeleton_Attack);
        //Archer Attack (arrow) 13
        Effect Archer_Attack_Arrow = new Effect(
                new ImageView(new Image(Config.rangedAttackPath)) , 5 , 1 , 5 , 40 , 30 , 12 , false);
        effects.add(Archer_Attack_Arrow);
        //Archer Attack 14
        Effect Archer_Attack = new Effect(
                new ImageView(new Image(Config.rangedAttackTakeDamagePath)) , 4 , 1 , 4 , 31 , 28 , 8 , false);
        effects.add(Archer_Attack);
        //Archer Skill Snipe1 15
        Effect Archer_Skill_Snipe1 = new Effect(
                new ImageView(new Image(Config.skillSnipePath)) , 4 , 3 , 10 , 48 , 32 , 12 , false);
        effects.add(Archer_Skill_Snipe1);
        //Archer Skill Snipe2 16
        Effect Archer_Skill_Snipe2 = new Effect(
                new ImageView(new Image(Config.skillSnipePath2)) , 3 , 3 , 7 , 48 , 48 , 12 , false);
        effects.add(Archer_Skill_Snipe2);
        //Archer Skill Snipe3 17
        Effect Archer_Skill_Snipe3 = new Effect(
                new ImageView(new Image(Config.skillSnipePath3)) , 3 , 3 , 8 , 32 , 32 , 12 , false);
        effects.add(Archer_Skill_Snipe3);
        //Archer Skill Snipe4 18
        Effect Archer_Skill_Snipe4 = new Effect(
                new ImageView(new Image(Config.skillSnipePath4)) , 4 , 3 , 9 , 32 , 32 , 12 , false);
        effects.add(Archer_Skill_Snipe4);
        //Archer Skill Targetlock 19
        Effect Archer_Skill_Targetlock = new Effect(
                new ImageView(new Image(Config.skillTargetlockPath)) , 3 , 1 , 3 , 32 , 32 , 10 , false);
        effects.add(Archer_Skill_Targetlock);
        //Archer Skill Halt 20
        Effect Archer_Skill_Halt = new Effect(
                new ImageView(new Image(Config.skillHaltPath)) , 4 , 1 , 4 , 32 , 31 , 10 , false);
        effects.add(Archer_Skill_Halt);
        //Archer Skill Teleport 21
        Effect Archer_Skill_Teleport = new Effect(
                new ImageView(new Image(Config.skillTeleportPath)) , 4 , 1 , 4 , 32 , 32 , 10 , false);
        effects.add(Archer_Skill_Teleport);
        //Wizard Attack 22
        Effect Wizard_Attack = new Effect(
                new ImageView(new Image(Config.magicAttackPath)) , 2 , 1 , 2 , 15 , 11 , 8 , false);
        effects.add(Wizard_Attack);
        //Wizard Attack take damage 23
        Effect Wizard_Attack_TakeDamage = new Effect(
                new ImageView(new Image(Config.magicAttackTakeDamagePath)) , 4 , 4 , 14 , 64 , 64 , 8 , false);
        effects.add(Wizard_Attack_TakeDamage);
        //Wizard Skill Fireball 24
        Effect Wizard_Skill_Fireball = new Effect(
                new ImageView(new Image(Config.skillFireballPath)) , 4 , 1 , 4 , 33 , 30 , 10 , true);
        effects.add(Wizard_Skill_Fireball);
        //Wizard Skill Rain of Fire 25
        Effect Wizard_Skill_RainOfFire = new Effect(
                new ImageView(new Image(Config.skillRainOfFirePath)) , 3 , 1 , 3 , 32 , 46 , 10 , false);
        effects.add(Wizard_Skill_RainOfFire);
        //Wizard Skill Dragon Fire 26
        Effect Wizard_Skill_DragonFire = new Effect(
                new ImageView(new Image(Config.skillDragonFirePath)) , 4 , 1 , 4 , 32 , 31 , 10 , false);
        effects.add(Wizard_Skill_DragonFire);
        //Wizard Skill Ice Shield 27
        Effect Wizard_Skill_IceShield = new Effect(
                new ImageView(new Image(Config.skillIceShieldPath)) , 4 , 1 , 4 , 32 , 30 , 7 , false);
        effects.add(Wizard_Skill_IceShield);
        //Wizard Skill Ice Shield buff 28
        Effect Wizard_Skill_IceShield_Buff = new Effect(
                new ImageView(new Image(Config.skillIceShieldBuffPath)) , 6 , 1 , 6 , 16 , 22 , 7 , true);
        effects.add(Wizard_Skill_IceShield_Buff);
        //Universal Skill HammerFall 29
        Effect Universal_Skill_HammerFall = new Effect(
                new ImageView(new Image(Config.skillHammerFallPath)) , 6 , 1 , 6 , 16 , 15 , 10 , false);
        effects.add(Universal_Skill_HammerFall);
        //Universal Skill HolyLight 30
        Effect Universal_Skill_HolyLight = new Effect(
                new ImageView(new Image(Config.skillHolyLightPath)) , 16 , 1 , 16 , 48 , 48 , 10 , false);
        effects.add(Universal_Skill_HolyLight);
        //Universal Skill RhoAias 31
        Effect Universal_Skill_RhoAias = new Effect(
                new ImageView(new Image(Config.skillRhoAiasPath)) , 4 , 1 , 4 , 32 , 34 , 7 , false);
        effects.add(Universal_Skill_RhoAias);
        //Universal Skill StaticShock 32
        Effect Universal_Skill_StaticShock = new Effect(
                new ImageView(new Image(Config.skillStaticShockPath)) , 13 , 1 , 13 , 64 , 64 , 10 , false);
        effects.add(Universal_Skill_StaticShock);
        //Universal Skill Narcissistic Buff ActionPoint 33
        Effect Universal_Skill_Narcissistic_Buff_ActionPoint = new Effect(
                new ImageView(new Image(Config.skillNarcissisticBuffActionPointPath)) , 4 , 1 , 4 , 16 , 16 , 6 , false);
        effects.add(Universal_Skill_Narcissistic_Buff_ActionPoint);
        //Universal Skill Narcissistic Buff Attack 34
        Effect Universal_Skill_Narcissistic_Buff_Attack = new Effect(
                new ImageView(new Image(Config.skillNarcissisticBuffAttackPath)) , 4 , 1 , 4 , 16 , 16 , 6 , false);
        effects.add(Universal_Skill_Narcissistic_Buff_Attack);
        //Universal Skill Narcissistic Buff Health 35
        Effect Universal_Skill_Narcissistic_Buff_Health = new Effect(
                new ImageView(new Image(Config.skillNarcissisticBuffHealthPath)) , 4 , 1 , 4 , 16 , 16 , 6 , false);
        effects.add(Universal_Skill_Narcissistic_Buff_Health);
        //Universal Skill Punch 36
        Effect Universal_Skill_Punch = new Effect(
                new ImageView(new Image(Config.skillPunchPath)) , 4 , 1 , 4 , 24 , 40 , 8 , false);
        effects.add(Universal_Skill_Punch);
        //Universal Skill Punch 37
        Effect Universal_Skill_Kick = new Effect(
                new ImageView(new Image(Config.skillKickPath)) , 5 , 1 , 5 , 32 , 32 , 10 , false);
        effects.add(Universal_Skill_Kick);
        //Universal Skill Ambush 38
        Effect Universal_Skill_Ambush = new Effect(
                new ImageView(new Image(Config.skillAmbushPath)) , 6 , 1 , 6 , 110 , 93 , 13 , false);
        effects.add(Universal_Skill_Ambush);
        //Universal Skill Ambush 39
        Effect Universal_Skill_Ambush_Crit = new Effect(
                new ImageView(new Image(Config.skillAmbushCritPath)) , 14 , 1 , 14 , 110 , 93 , 13 , false);
        effects.add(Universal_Skill_Ambush_Crit);

    }

    public Effect createInPlaceEffects(int index){
        Effect copy = effects.get(index).clone();
        return copy;
    }
    
    public void renderEffect(Enum<TYPE> typeEnum, BasePiece piece , int row , int col, Effect effect , EffectConfig config){

        effect.setConfig(config);
        runningEffects.add(effect);

        if (typeEnum == TYPE.AROUND_SELF){
        //effect will occur around player, also rotate and face to target

            EffectMaker.getInstance().effectPane.getChildren().add( effect.getImageView() );

            //find angle toward enemy
            double x = col - GameManager.getInstance().player.getCol();
            double y = row - GameManager.getInstance().player.getRow();
            double angleRadian = Math.atan2(y , x);
            double angleDegree = Math.atan2(y , x) * (180.0 / Math.PI);

            //set effect position (angle is in account)
            effect.getImageView().setX(GameManager.getInstance().player.getCol()*SQUARE_SIZE
                                    + (config.distanceFromOrigin * Math.cos(angleRadian) )
                                    + config.offsetX);
            effect.getImageView().setY(GameManager.getInstance().player.getRow()*SQUARE_SIZE
                                    + (config.distanceFromOrigin * Math.sin(angleRadian) )
                                    + config.offsetY);

            //rotate effect
            effect.getImageView().setRotate(angleDegree);

            //scale effect size
            effect.getImageView().setScaleX(config.scale);
            effect.getImageView().setScaleY(config.scale);

            effect.getImageView().toFront();
            effect.getImageView().setDisable(true);

            effect.start();
        }
        else if(typeEnum == TYPE.ON_TARGET){
        //effect will occur on target position

            //add effect to pane
            EffectMaker.getInstance().effectPane.getChildren().add(effect.getImageView());

            //scale effect size
            effect.getImageView().setScaleX(config.scale);
            effect.getImageView().setScaleY(config.scale);

            //set effect on enemy position
            effect.getImageView().setX(col*SQUARE_SIZE + config.offsetX);
            effect.getImageView().setY(row*SQUARE_SIZE + config.offsetY);

            effect.getImageView().toFront();
            effect.getImageView().setDisable(true);

            effect.start();
        }
        else if(typeEnum == TYPE.ON_SELF){
            //effect will occur on target position

            //add effect to pane
            EffectMaker.getInstance().effectPane.getChildren().add(effect.getImageView());

            //scale effect size + direction
            effect.getImageView().setScaleX(config.scale);
            effect.getImageView().setScaleY(config.scale);

            //set effect on enemy position
            effect.getImageView().setX(col*SQUARE_SIZE + config.offsetX);
            effect.getImageView().setY(row*SQUARE_SIZE + config.offsetY);

            effect.getImageView().toFront();
            effect.getImageView().setDisable(true);

            effect.start();
        }
        else if (typeEnum == TYPE.AROUND_SELF_ENEMY){
            //effect will occur around enemy, also rotate and face to target

            EffectMaker.getInstance().effectPane.getChildren().add( effect.getImageView() );

            //find angle toward enemy
            double x = GameManager.getInstance().player.getCol() - col;
            double y = GameManager.getInstance().player.getRow() - row;
            double angleRadian = Math.atan2(y , x);
            double angleDegree = Math.atan2(y , x) * (180.0 / Math.PI);

            //set effect position (angle is in account)
            effect.getImageView().setX(col*SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.cos(angleRadian) )
                    + config.offsetX);
            effect.getImageView().setY(row*SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.sin(angleRadian) )
                    + config.offsetY);

            //rotate effect
            effect.getImageView().setRotate(angleDegree);

            //scale effect size
            effect.getImageView().setScaleX(config.scale);
            effect.getImageView().setScaleY(config.scale);

            effect.getImageView().toFront();
            effect.getImageView().setDisable(true);

            effect.start();
        }
        else if (typeEnum == TYPE.BULLET_TO_TARGET) {

            EffectMaker.getInstance().effectPane.getChildren().add(effect.getImageView());

            //find angle toward enemy
            double x = - GameManager.getInstance().player.getCol() + col;
            double y = - GameManager.getInstance().player.getRow() + row;
            double angleRadian = Math.atan2(y, x);
            double angleDegree = Math.atan2(y, x) * (180.0 / Math.PI);

            //set effect position (angle is in account)
            effect.getImageView().setX(piece.getCol() * SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.cos(angleRadian))
                    + config.offsetX);
            effect.getImageView().setY(piece.getRow() * SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.sin(angleRadian))
                    + config.offsetY);
            //scale effect size
            effect.getImageView().setScaleX(config.scale);
            effect.getImageView().setScaleY(config.scale);
            //rotate effect
            effect.getImageView().setRotate(angleDegree);

            //prepare transition behaviour for this effect
            TranslateTransition moveTransition = new TranslateTransition();
            moveTransition.setNode(effect.getImageView());
            moveTransition.setDuration(Duration.millis(400));
            moveTransition.setCycleCount(1);
            moveTransition.setInterpolator(Interpolator.EASE_IN);

            //slowly move to target col,row
            moveTransition.setToX((col - piece.getCol()) * SQUARE_SIZE + config.offsetX);
            moveTransition.setToY((row - piece.getRow()) * SQUARE_SIZE + config.offsetY);

            moveTransition.setOnFinished(actionEvent -> {
                effectPane.getChildren().remove(effect.getImageView());
            });

            effect.start();
            moveTransition.play();
        }
        else if (typeEnum == TYPE.BULLET_TO_TARGET_ENEMY) {

            EffectMaker.getInstance().effectPane.getChildren().add(effect.getImageView());

            //find angle toward enemy
            double x = GameManager.getInstance().player.getCol() - col;
            double y = GameManager.getInstance().player.getRow() - row;
            double angleRadian = Math.atan2(y , x);
            double angleDegree = Math.atan2(y , x) * (180.0 / Math.PI);

            //set effect position (angle is in account)
            effect.getImageView().setX(col*SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.cos(angleRadian) )
                    + config.offsetX);
            effect.getImageView().setY(row*SQUARE_SIZE
                    + (config.distanceFromOrigin * Math.sin(angleRadian) )
                    + config.offsetY);
            //scale effect size
            effect.getImageView().setScaleX(config.scale);
            effect.getImageView().setScaleY(config.scale);
            //rotate effect
            effect.getImageView().setRotate(angleDegree);

            //prepare transition behaviour for this effect
            TranslateTransition moveTransition = new TranslateTransition();
            moveTransition.setNode(effect.getImageView());
            moveTransition.setDuration(Duration.millis(400));
            moveTransition.setCycleCount(1);
            moveTransition.setInterpolator(Interpolator.EASE_IN);

            //slowly move to target col,row
            moveTransition.setToX((piece.getCol() - col) * SQUARE_SIZE + config.offsetX);
            moveTransition.setToY((piece.getRow() - row) * SQUARE_SIZE + config.offsetY);

            moveTransition.setOnFinished(actionEvent -> {
                effectPane.getChildren().remove(effect.getImageView());
            });

            effect.start();
            moveTransition.play();
        }

        effect.getImageView().toFront();
    }

    //this method will clear effect in array if the effect is timeout/remains turn is 0
    public void clearDeadEffect(){

        //clear effect display in effectPane
        for (Effect effect : runningEffects) {
            if (effect.canKill || effect.getTurnRemain() == 0 || (effect.getOwner() != null && (!(effect.getOwner()).isAlive()))) {
                //effect is timeout or reach turn limit
                effectPane.getChildren().remove(effect.getImageView());
            }
        }

        //also remove from runningEffects
        runningEffects.removeIf(effect -> (
                effect.canKill ||
                effect.getTurnRemain() == 0 ||
                ( effect.getOwner()!=null && ( !(effect.getOwner()).isAlive()) )
        ));
    }

    //update effect turn remained
    public void updateEffectTimer(){
        runningEffects.forEach(effect -> effect.setTurnRemain( Math.max( 0 , effect.getTurnRemain()-1 ) ));
    }


}
