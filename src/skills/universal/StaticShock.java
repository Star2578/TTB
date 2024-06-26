package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.Effect;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import pieces.Attackable;
import utils.Config;

public class StaticShock extends BaseSkill implements Attackable {
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
                        monsterPiece.addBuff(1,"Stun");
                        //=========<STUN EFFECT>====================================================================
                        Effect Stun = EffectMaker.getInstance().createInPlaceEffects(8);
                        Stun.setOwner(target);
                        EffectMaker.getInstance()
                                .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                        GameManager.getInstance().player ,
                                        target.getRow(), target.getCol(),
                                        Stun ,
                                        new EffectConfig(12 , -6 , 0 , 1.6) );
                        Stun.setTurnRemain(2);
                        //===========================================================================================
                        if (!monsterPiece.isAlive()) {
                            GameManager.getInstance().gameScene.removePiece(monsterPiece);
                            EffectMaker.getInstance().clearDeadEffect();
                        }
                    }
                    //=========<SKILL EFFECT>====================================================================
                    if (GameManager.getInstance().validMovesCache[newRow][newCol])
                        if (!(target instanceof BasePlayerPiece)){
                            EffectMaker.getInstance()
                                    .renderEffect( EffectMaker.TYPE.ON_SELF ,
                                            GameManager.getInstance().player ,
                                            newRow, newCol,
                                            EffectMaker.getInstance().createInPlaceEffects(32) ,
                                            new EffectConfig(-15 , -28 , 0 , 0.75) );
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
