package pieces.npcs;

import com.google.gson.Gson;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.ImageScaler;
import logic.SpriteAnimation;
import logic.ui.display.NpcDisplay;
import pieces.BasePiece;
import utils.Config;

import java.io.Console;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class BaseNpcPiece extends BasePiece {
    private String name;
    private Image portrait;
    private String currentDialogue;
    private Map<String, Object> dialogues;

    public BaseNpcPiece(String name, String portraitPath, int defaultDirection) {
        super(Config.ENTITY_TYPE.NPC, new ImageView(Config.PlaceholderPath), 0, 0);
        ImageScaler imageScaler = new ImageScaler();

        this.name = name;
        this.portrait = imageScaler.resample(new Image(portraitPath), 2);

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
                currentDialogue = categoryDialogues.get(key);
            } else {
                currentDialogue = ("No dialogue available for key '" + key + "' in category: " + category);
            }
        } else {
            currentDialogue = ("No dialogues available for category: " + category);
        }
    }

    // talk but random
    public void talk(String category) {
        if (dialogues != null && dialogues.containsKey(category)) {
            Map<String, String> categoryDialogues = (Map<String, String>) dialogues.get(category);
            List<String> keys = new ArrayList<>(categoryDialogues.keySet());
            Random random = new Random();
            String randomKey = keys.get(random.nextInt(keys.size()));

            currentDialogue = categoryDialogues.get(randomKey);
        } else {
            currentDialogue = ("No dialogues available for category: " + category);
        }
    }


    @Override
    public void setupAnimation(String imgPath, int offsetX, int offsetY, int width, int height) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        animationImage = new ImageView(new Image(imgPath));
        animationImage.setPreserveRatio(true);
        animationImage.setTranslateX(offsetX);
        animationImage.setTranslateY(offsetY);
        animationImage.setDisable(true);
        spriteAnimation=new SpriteAnimation(animationImage,4,0,4,width,height,5);
        spriteAnimation.start();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getPortrait() {
        return portrait;
    }
    public String getName() {
        return name;
    }
    public String getCurrentDialogue() {
        return currentDialogue;
    }

    public abstract void setDialogueOptions(NpcDisplay npcDisplay);
}
