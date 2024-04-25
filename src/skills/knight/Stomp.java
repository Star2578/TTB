package skills.knight;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectManager;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import utils.Attack;
import utils.Config;

public class Stomp extends BaseSkill implements Attack {
    private BasePiece target;
    private final int DAMAGE = 10;
    public Stomp() {
        super("Stomp", Color.DARKRED, 2, 2, "The Knight brings their armored boot crashing down, sending shockwaves rippling through the ground.", Config.Rarity.COMMON, "res/SFX/skills/slash/PP_01.wav");
        icon = new ImageView(Config.StompPath);
        range = 0;
    }

    @Override
    public void attack() {
        if (target != null && target instanceof BasePlayerPiece) {
            // Perform Attack
            GameManager.getInstance().player.decreaseActionPoint(actionPointCost);
            GameManager.getInstance().player.decreaseMana(manaCost);
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++) {
                    int newRow = this.target.getRow() + i;
                    int newCol = this.target.getCol() + j;
                    BasePiece target = GameManager.getInstance().piecesPosition[newRow][newCol];
                    if (target instanceof BaseMonsterPiece monsterPiece) {
                        monsterPiece.takeDamage(DAMAGE);
                        monsterPiece.setStun(monsterPiece.getStun() + 1);
                    }
                    System.out.println("Use " + name + " on " + newRow + " " + newCol);
                }

            //=========<SKILL EFFECT>====================================================================
            EffectManager.getInstance()
                    .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                            GameManager.getInstance().player ,
                            target ,
                            EffectManager.getInstance().createInPlaceEffects(1) ,
                            new EffectConfig(0 , -16 , 24 , 1.1) );
            //===========================================================================================
        }
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        attack();
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
    public int getAttack() {
        return DAMAGE;
    }
}

