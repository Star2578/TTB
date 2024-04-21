package pieces.player;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.SpriteAnimation;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import logic.ui.GUIManager;
import pieces.enemies.BaseMonsterPiece;
import skills.knight.Heal;
import skills.knight.Slash;
import utils.Config;

import java.util.ArrayList;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Wizard extends BasePlayerPiece{

    public Wizard(int row, int col, int defaultDirection) {
        super(row, col, defaultDirection);

        maxActionPoint = 10;
        currentActionPoint = maxActionPoint;

        maxMana = 20;
        currentMana = maxMana;

        maxHp = 12;
        currentHp = maxHp;

        attackDamage = 4; // Base attack for player

        //add skill
        skills[0] = new Slash();
        skills[1] = new Heal();
        //TODO===========

        //configs values for animation
//        setupAnimation(Config.WizardAnimationPath, 0, -15, 32, 56);
    }

//    @Override
//    public void moveWithTransition(int row , int col) {
//        //stop player from do other action
//        setCanAct(false);
//        spriteAnimation.changeAnimation(4, 2);
//        //slowly move to target col,row
//        moveTransition.setToX((col - getCol()) * SQUARE_SIZE + offsetX);
//        moveTransition.setToY((row - getRow()) * SQUARE_SIZE + offsetY);
//
//        moveTransition.setOnFinished(actionEvent -> {
//            //set image layering depend on row
//            animationImage.setViewOrder(BOARD_SIZE - row);
//            //move real coordinate to new col,row
//            animationImage.setX(col * SQUARE_SIZE + offsetX);
//            animationImage.setY(row * SQUARE_SIZE + offsetY);
//            //set translateProperty back to default
//            animationImage.translateXProperty().set(offsetX);
//            animationImage.translateYProperty().set(offsetY);
//            //now player can do actions
//            spriteAnimation.changeAnimation(4, 0);
//            setCanAct(true);
//            setRow(row);
//            setCol(col);
//        });
//        moveTransition.play();
//    }

    @Override
    public boolean validMove(int row, int col) {
        return Math.abs(row - getRow()) <= 1 && Math.abs(col - getCol()) <= 1;
    }

    @Override
    public boolean validAttack(int row, int col) {
        return Math.abs(row - getRow()) <= 1 && Math.abs(col - getCol()) <= 1;
    }

    @Override
    public void startTurn() {
        setCanAct(true);
        setCurrentMana(getCurrentMana() + 1); // Wizard restore 1 mana every turn
        setCurrentActionPoint(getMaxActionPoint());
    }

    @Override
    public void endTurn() {
        setCanAct(false);
    }

    @Override
    public void attack(BaseMonsterPiece monsterPiece) {
        if(ATTACK_COST > getCurrentActionPoint()) {
            System.out.println("Not enough Action Point to attack!");
            return;
        }

        decreaseActionPoint(ATTACK_COST);
        monsterPiece.takeDamage(getAttackDamage());

        changeDirection(Integer.compare(monsterPiece.getCol(), getCol()));
//        if(currentDirection == -1) {
//            meleeAttackImage.setScaleX(-1);
//        } else {
//            meleeAttackImage.setScaleX(1);
//        }

        EffectManager.getInstance()
                .renderEffect(EffectManager.TYPE.ON_TARGET,
                        this,
                        monsterPiece,
                        EffectManager.getInstance().createInPlaceEffects(0),
                        new EffectConfig(0, 8, 0, 1.25));

//        meleeAttackImage.setX( ( getCol() * SQUARE_SIZE) - ((double) SQUARE_SIZE / 2) - (meleeAttackImage.getFitWidth() / 2) );
//        meleeAttackImage.setY( ( getRow() * SQUARE_SIZE) - ((double) SQUARE_SIZE / 2) - (meleeAttackImage.getFitHeight() / 2) );
//        meleeAttackImage.toFront();

        System.out.println("Attack success");
        GUIManager.getInstance().updateGUI();
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        spriteAnimation.changeAnimation(1, 1);
        new Thread(() -> {
            try {
                Thread.sleep(400);
                spriteAnimation.changeAnimation(4, 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
