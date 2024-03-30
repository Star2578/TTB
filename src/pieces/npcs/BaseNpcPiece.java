package pieces.npcs;

import com.google.gson.Gson;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.SpriteAnimation;
import pieces.BasePiece;
import utils.Config;

import java.io.Console;
import java.io.FileReader;
import java.util.Map;

public class BaseNpcPiece extends BasePiece {
    protected int offsetX=0;
    protected int offsetY=0;
    protected SpriteAnimation spriteAnimation;
    public ImageView animationImage;
    private Map<String, Object> dialogues;

    public BaseNpcPiece(ImageView texture, int row, int col) {
        super(Config.ENTITY_TYPE.NPC, texture, row, col);
    }

    private void importDialogues(String jsonFilePath) {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Gson gson = new Gson();
            dialogues = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            System.out.println("Error while parsing JSON : " + e);
        }
    }

    public void talk(String category) {
        if (dialogues != null && dialogues.containsKey(category)) {
            Object categoryDialogues = dialogues.get(category);
            System.out.println("Dialogues for category '" + category + "': " + categoryDialogues);
        } else {
            System.out.println("No dialogues available for category: " + category);
        }
    }

    protected void setupAnimation(String imgPath, int offsetX, int offsetY, int width, int height) {
        //===================<animation section>==========================================
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        //sprite animations for monster
        animationImage = new ImageView(new Image(imgPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,width,height,5);
        spriteAnimation.start();

//        //setup moveTranslate behaviour
//        moveTransition = new TranslateTransition();
//        moveTransition.setNode(animationImage);
//        moveTransition.setDuration(Duration.millis(600));
//        moveTransition.setCycleCount(1);
//        //================================================================================
    }
}
