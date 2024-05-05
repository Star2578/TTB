package skills.universal;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.SoundManager;
import logic.effect.EffectConfig;
import logic.effect.EffectMaker;
import pieces.BasePiece;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import utils.Config;

import static utils.Config.BOARD_SIZE;
import static utils.Config.SQUARE_SIZE;

public class Teleport extends BaseSkill {
    private BasePiece target;
    public Teleport() {
        super("Teleport", Color.DARKRED,
                6, 2,
                "Teleport to a chosen location",
                Config.Rarity.EPIC, "SFX/skills/slash/PP_01.wav"
        );
        icon = new ImageView(Config.TeleportPath);
        range = 5; // Set the range to 5
    }

    public void teleport() {

        BasePlayerPiece player = GameManager.getInstance().player;

        player.decreaseActionPoint(actionPointCost);
        player.decreaseMana(manaCost);


        //=========<SKILL EFFECT PHASE 1>====================================================================
        EffectMaker.getInstance()
                .renderEffect( EffectMaker.TYPE.ON_SELF ,
                        player ,
                        player.getRow(), player.getCol() ,
                        EffectMaker.getInstance().createInPlaceEffects(21) ,
                        new EffectConfig(0 , -4 , 0 , 1.4) );
        //===========================================================================================);

        // Teleport the player to the target position
        BasePiece[][] pieces = GameManager.getInstance().piecesPosition;
        pieces[player.getRow()][player.getCol()] = null;
        pieces[target.getRow()][target.getCol()] = player;

        // Directly set the player's position without any transition
        player.setRow(target.getRow());
        player.setCol(target.getCol());
        ImageView animationImage = player.animationImage;
        //move real coordinate to new col,row
        animationImage.setX(target.getCol()*SQUARE_SIZE + player.getOffsetX());
        animationImage.setY(target.getRow()*SQUARE_SIZE + player.getOffsetY());
        animationImage.translateXProperty().set(player.getOffsetX());
        animationImage.translateYProperty().set(player.getOffsetY());
        animationImage.setViewOrder(BOARD_SIZE - target.getRow());
    }

    @Override
    public void perform(BasePiece target) {
        this.target = target;
        teleport();
        SoundManager.getInstance().playSoundEffect(sfxPath);
    }

    @Override
    public boolean validRange(int row, int col) {
        // Valid Range for the skill
        int currentRow = GameManager.getInstance().player.getRow();
        int currentCol = GameManager.getInstance().player.getCol();

        // Check if the target is within range
        return Math.abs(row - currentRow) <= range && Math.abs(col - currentCol) <= range;
    }

    @Override
    public boolean castOnSelf() {
        return false;
    }

    @Override
    public boolean castOnMonster() {
        return false;
    }
}