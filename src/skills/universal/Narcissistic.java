package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import utils.*;

import java.util.Random;

public class Narcissistic extends BaseSkill implements BuffHealth, BuffAttack, BuffActionPoint, BuffMana {
    private BasePiece target;

    private final int MAX_HEALTH = 1;
    private final int ATTACK_BUFF = 1;
    private final int MAX_ACTION_POINT = 1;
    private final int MAX_MANA = 1;
    public Narcissistic() {
        super("Narcissistic", Color.LIGHTYELLOW, 3, 3,
                "Praise yourself, gain random stat buff", Config.Rarity.EPIC, Config.sfx_powerupSound);

        icon = new ImageView(Config.NarcissisticPath);
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;

        // Randomly apply one of the three buffs
        Random random = new Random();
        int randomBuff = random.nextInt(3);

        if (target instanceof BasePlayerPiece) {
            switch (randomBuff) {
                case 0 -> buffHealth();
                case 1 -> buffAttack();
                case 2 -> buffActionPoint();
            }
        }
        GameManager.getInstance().player.decreaseMana(manaCost);
        GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public boolean validRange(int row, int col) {
        // Only valid at player's square
        BasePlayerPiece player = GameManager.getInstance().player;

        return player.getRow() == row && player.getCol() == col;
    }

    @Override
    public boolean castOnSelf() {
        return true;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }

    @Override
    public void buffActionPoint() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMaxActionPoint = playerPiece.getMaxActionPoint();

                playerPiece.setMaxActionPoint(currentMaxActionPoint + MAX_ACTION_POINT);

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF,
                                GameManager.getInstance().player ,
                                GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(33) ,
                                new EffectConfig(9 , 0 , 0 , 1.6) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getBuffActionPoint() {
        return MAX_ACTION_POINT;
    }

    @Override
    public void buffAttack() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentAttack = playerPiece.getAttackDamage();

                playerPiece.setAttackDamage(currentAttack + ATTACK_BUFF);

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF,
                                GameManager.getInstance().player ,
                                GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(34) ,
                                new EffectConfig(9 , 0 , 0 , 1.6) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getBuffAttack() {
        return ATTACK_BUFF;
    }

    @Override
    public void buffHealth() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMaxHealth = playerPiece.getMaxHealth();

                playerPiece.setMaxHealth(currentMaxHealth + MAX_HEALTH);

                //=========<SKILL EFFECT>====================================================================
                EffectManager.getInstance()
                        .renderEffect( EffectManager.TYPE.ON_SELF,
                                GameManager.getInstance().player ,
                                GameManager.getInstance().player.getRow(), GameManager.getInstance().player.getCol(),
                                EffectManager.getInstance().createInPlaceEffects(35) ,
                                new EffectConfig(9 , 0 , 0 , 1.6) );
                //===========================================================================================
            }
        }
    }

    @Override
    public int getBuffHealth() {
        return MAX_HEALTH;
    }

    @Override
    public void buffMana() {
        if (target != null) {
            if (target instanceof BasePlayerPiece playerPiece) {
                int currentMaxMana = playerPiece.getMaxMana();

                playerPiece.setMaxMana(currentMaxMana + MAX_MANA);
            }
        }
    }

    @Override
    public int getBuffMana() {
        return MAX_MANA;
    }
}
