package pieces.npcs;

import javafx.scene.image.ImageView;
import pieces.npcs.BaseNpcPiece;
import utils.Config;

public class Dealer extends BaseNpcPiece {
    public Dealer() {
        super(1);
        importDialogues("res/dialogues/dealer-dialogue.json");
        talk("greetings", "hello");
        talk("greetings");

        setupAnimation(Config.DealerAnimationPath, 0, -20, 32, 48);
    }
}
