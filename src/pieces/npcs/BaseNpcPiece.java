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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseNpcPiece extends BasePiece {
    protected int offsetX=0;
    protected int offsetY=0;
    protected SpriteAnimation spriteAnimation;
    public ImageView animationImage;
    private Map<String, Object> dialogues;

    public BaseNpcPiece(int defaultDirection) {
        super(Config.ENTITY_TYPE.NPC, new ImageView(Config.PlaceholderPath), 0, 0);

        // insert 1 as default (image facing right)
        // insert -1 to flip
        if (defaultDirection == -1) {
            ImageView imageView = getTexture();
            imageView.setScaleX(-1); // Flipping the image horizontally
        }
    }

    protected void importDialogues(String jsonFilePath) {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Gson gson = new Gson();
            dialogues = gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            System.out.println("Error while parsing JSON : " + e);
        }
    }

    // talk with specific
    public void talk(String category, String key) {
        if (dialogues != null && dialogues.containsKey(category)) {
            Map<String, String> categoryDialogues = (Map<String, String>) dialogues.get(category);
            if (categoryDialogues.containsKey(key)) {
                String dialogue = categoryDialogues.get(key);
                System.out.println("Dialogue for category '" + category + "' and key '" + key + "': " + dialogue);
            } else {
                System.out.println("No dialogue available for key '" + key + "' in category: " + category);
            }
        } else {
            System.out.println("No dialogues available for category: " + category);
        }
    }

    // talk but random
    public void talk(String category) {
        if (dialogues != null && dialogues.containsKey(category)) {
            Map<String, String> categoryDialogues = (Map<String, String>) dialogues.get(category);
            List<String> keys = new ArrayList<>(categoryDialogues.keySet());
            Random random = new Random();
            String randomKey = keys.get(random.nextInt(keys.size()));
            String dialogue = categoryDialogues.get(randomKey);
            System.out.println("Random dialogue for category '" + category + "': " + dialogue);
        } else {
            System.out.println("No dialogues available for category: " + category);
        }
    }


    protected void setupAnimation(String imgPath, int offsetX, int offsetY, int width, int height) {
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
    }
}
