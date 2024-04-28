package skills.universal;

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

public class StaticShock extends BaseSkill implements Attack {
    private BasePiece target;

    private final int DAMAGE = 7;
    public StaticShock() {
        super("Static Shock", Color.CADETBLUE, 3, 3,
                "AOE Damage with stun for 1 turn", Config.Rarity.RARE, Config.sfx_holyMagicSound);

        icon = new ImageView(Config.StaticShockPath);
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
                        //=========<STUN EFFECT>====================================================================
                        EffectManager.getInstance()
                                .renderEffect( EffectManager.TYPE.ON_TARGET,
                                        GameManager.getInstance().player ,
                                        target.getRow(), target.getCol(),
                                        EffectManager.getInstance().createInPlaceEffects(8) ,
                                        new EffectConfig(-9 , -16 , 0 , 1.8) );
                        //===========================================================================================
                        if (!monsterPiece.isAlive()) {
                            GameManager.getInstance().gameScene.removePiece(monsterPiece);
                        }
                    }
                    //=========<SKILL EFFECT>====================================================================
                    if (!(target instanceof BasePlayerPiece)){
                        EffectManager.getInstance()
                                .renderEffect( EffectManager.TYPE.AROUND_SELF ,
                                        GameManager.getInstance().player ,
                                        newRow, newCol,
                                        EffectManager.getInstance().createInPlaceEffects(3) ,
                                        new EffectConfig(0 , -6 , 38 , 1.1) );
                    }
                    //===========================================================================================
                }

        }
    }

    @Override
    public int getAttack() {
        return DAMAGE;
    }
}