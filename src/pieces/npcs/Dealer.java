package pieces.npcs;

import items.BaseItem;
import items.potions.BluePotion;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.handlers.SkillHandler;
import logic.ui.GUIManager;
import logic.ui.display.NpcDisplay;
import logic.ui.overlay.ItemInfoOverlay;
import logic.ui.overlay.SkillInfoOverlay;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import skills.EmptySlot;
import skills.LockedSlot;
import skills.knight.Slash;
import utils.Attack;
import utils.Config;
import utils.Healing;
import utils.RefillMana;

import java.util.*;
import java.util.stream.Collectors;

public class Dealer extends BaseNpcPiece {
    private ImageScaler imageScaler = new ImageScaler();
    private static List<BaseItem> selectedItems = new ArrayList<>();
    private static List<BaseSkill> selectedSkills = new ArrayList<>();
    private SkillInfoOverlay skillInfoOverlay = new SkillInfoOverlay();
    private ItemInfoOverlay itemInfoOverlay = new ItemInfoOverlay();
    private VBox ShopOverlay;

    public Dealer() {
        super("Dealer", Config.DealerPortraitPath, 1);
        importDialogues("res/dialogues/dealer-dialogue.json");
        talk("greetings"); // setup starting dialogue

        setupAnimation(Config.DealerAnimationPath, 0, -10, 32, 48);
        setupShop();
    }

    @Override
    public void setDialogueOptions(NpcDisplay npcDisplay) {
        npcDisplay.addDialogueOption("Who are you?", new Runnable() {
            @Override
            public void run() {
                talk("questions", "who_are_you");
                npcDisplay.setDialogueText(getCurrentDialogue());
            }
        });
        npcDisplay.addDialogueOption("About the Dungeon", new Runnable() {
            @Override
            public void run() {
                talk("questions", "about_dungeon");
                npcDisplay.setDialogueText(getCurrentDialogue());
            }
        });
        npcDisplay.addDialogueOption("Ask for discount", new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int roll = random.nextInt(100);
                if (roll >= 70) {
                    // discount successful
                    talk("questions", "misc2");
                } else if (roll == 0) {
                    // extra fail, close shop
                    talk("questions", "misc4");
                } else {
                    // discount fail
                    talk("questions", "misc3");
                }
                npcDisplay.setDialogueText(getCurrentDialogue());
            }
        });
        npcDisplay.addDialogueOption("Shop", new Runnable() {
            @Override
            public void run() {
                talk("questions", "misc1");
                npcDisplay.setDialogueText(getCurrentDialogue());
                if (npcDisplay.getAdditionalOverlaySize() == 0) {
                    npcDisplay.newAdditionalOverlay(ShopOverlay);
                } else {
                    npcDisplay.clearAdditionalOverlay();
                }
            }
        });
    }

    private void setupShop() {
        ShopOverlay = new VBox();
        ShopOverlay.setMinHeight(700);
        ShopOverlay.setTranslateX(-300);

        // Add shop components
        GridPane itemShopGrid = new GridPane();
        itemShopGrid.setMinHeight(350);
        itemShopGrid.setBackground(Background.fill(Color.GOLD));
        itemShopGrid.setHgap(5);
        itemShopGrid.setVgap(5);
        itemShopGrid.setPadding(new Insets(10));
        int itemsPerRow = 4;

        for (int totalItems = 0; totalItems < 8; totalItems++) {
            StackPane item = createItemFrame(new BluePotion());
            int row = totalItems / itemsPerRow;
            int col = totalItems % itemsPerRow;
            itemShopGrid.add(item, col, row);
        }

        GridPane skillShopGrid = new GridPane();
        skillShopGrid.setMinHeight(350);
        skillShopGrid.setBackground(Background.fill(Color.CYAN));
        skillShopGrid.setHgap(5);
        skillShopGrid.setVgap(5);
        skillShopGrid.setPadding(new Insets(10));
        for (int totalSkills = 0; totalSkills < 8; totalSkills++) {
            StackPane item = createSkillFrame(new Slash());
            int row = totalSkills / itemsPerRow;
            int col = totalSkills % itemsPerRow;
            skillShopGrid.add(item, col, row);
        }

        // Setup child in pane
        StackPane itemShopContainer = new StackPane();
        itemShopContainer.getChildren().addAll(itemShopGrid, itemInfoOverlay.getView());
        StackPane skillShopContainer = new StackPane();
        skillShopContainer.getChildren().addAll(skillShopGrid, skillInfoOverlay.getView());

        itemShopContainer.setOnMouseMoved(event -> {
            // Update the position of the BoxOverlay to follow the mouse
            itemInfoOverlay.updatePosition(event.getX(), event.getY(), -160, -170);
        });

        skillShopContainer.setOnMouseMoved(event -> {
            // Update the position of the BoxOverlay to follow the mouse
            skillInfoOverlay.updatePosition(event.getX(), event.getY(), -160, -170);
        });

        ShopOverlay.getChildren().addAll(itemShopContainer, skillShopContainer);
    }

    private BaseItem randomItem() {
        BaseItem item;
        BaseItem[] pool = GameManager.getInstance().ITEM_POOL;
        Random random = new Random();

        // Create a list of items not already selected
        List<BaseItem> availableItems = Arrays.stream(pool)
                .filter(i -> !selectedItems.contains(i))
                .collect(Collectors.toList());

        // If all items have been selected, reset the selected items list
        if (availableItems.isEmpty()) {
            selectedItems.clear();
            availableItems.addAll(Arrays.asList(pool));
        }

        // Randomly select an item from the available items
        item = availableItems.get(random.nextInt(availableItems.size()));
        selectedItems.add(item);

        return  item;
    }
    private StackPane createItemFrame(BaseItem item) {
        StackPane itemFrame = new StackPane();

        Image itemIcon = imageScaler.resample(item.getIcon().getImage(), 2);
        ImageView frameView = item.getFrame();

        itemFrame.setAlignment(Pos.CENTER);
        itemFrame.setPrefWidth(64);
        itemFrame.setPrefHeight(64);
        itemFrame.getChildren().addAll(new ImageView(itemIcon), frameView);

        itemFrame.setOnMouseEntered(mouseEvent -> {
            itemInfoOverlay.getView().setVisible(true);

            // setup info
            itemInfoOverlay.getTitle().setText(item.getName());
            itemInfoOverlay.getTitle().setTextFill(item.getNameColor());
            itemInfoOverlay.getDesc().setText(item.getDescription());

            itemInfoOverlay.getDataContainer().getChildren().clear();
            if (item instanceof RefillMana r) {
                itemInfoOverlay.newInfo("Mana Refill", Color.CYAN, "+" + r.getRefill());
            }
        });

        itemFrame.setOnMouseExited(mouseEvent -> {
            itemInfoOverlay.getView().setVisible(false);
        });

        return itemFrame;
    }

    private BaseSkill randomSkill() {
        BaseSkill skill;
        List<BaseSkill> playerOwned = Arrays.stream(GameManager.getInstance().playerSkills).toList();
        BaseSkill[] pool = GameManager.getInstance().UNIVERSAL_SKILL_POOL;
        Random random = new Random();

        // Create a list of items not already selected
        List<BaseSkill> availableItems = Arrays.stream(pool)
                .filter(i -> !selectedSkills.contains(i) && !playerOwned.contains(i))
                .collect(Collectors.toList());

        // If all items have been selected, reset the selected items list
        if (availableItems.isEmpty()) {
            selectedSkills.clear();
            availableItems.addAll(Arrays.asList(pool));
        }

        // Randomly select an item from the available items
        skill = availableItems.get(random.nextInt(availableItems.size()));
        selectedSkills.add(skill);

        return skill;
    }
    private StackPane createSkillFrame(BaseSkill skill) {
        StackPane skillFrame = new StackPane();

        // Scale Skill Icon
        Image skillIcon = imageScaler.resample(skill.getIcon().getImage(), 2);
        ImageView frameView = skill.getFrame();

        skillFrame.setAlignment(Pos.CENTER);
        skillFrame.setPrefWidth(64);
        skillFrame.setPrefHeight(64);
        skillFrame.getChildren().addAll(new ImageView(skillIcon), frameView);

        skillFrame.setOnMouseEntered(mouseEvent -> {
            skillInfoOverlay.getView().setVisible(true);
            skillInfoOverlay.getView().toFront();

            // Update overlay info
            skillInfoOverlay.getTitle().setText(skill.getName());
            skillInfoOverlay.getTitle().setTextFill(skill.getNameColor());
            skillInfoOverlay.getDesc().setText(skill.getDescription());

            skillInfoOverlay.getDataContainer().getChildren().clear();
            skillInfoOverlay.newInfo("Mana", Color.DARKBLUE, String.valueOf(skill.getManaCost()));
            skillInfoOverlay.newInfo("Action Point", Color.ORANGE, String.valueOf(skill.getActionPointCost()));

            // Other skill info base on type
            if (skill instanceof Attack a) {
                skillInfoOverlay.newInfo("Attack", Color.DARKRED, String.valueOf(a.getAttack()));
            }if (skill instanceof Healing h) {
                skillInfoOverlay.newInfo("Heal", Color.DARKGREEN, String.valueOf(h.getHeal()));
            }if (skill instanceof RefillMana r) {
                skillInfoOverlay.newInfo("Mana Refill", Color.CYAN, "+" + r.getRefill());
            }
        });

        skillFrame.setOnMouseExited(mouseEvent -> {
            skillInfoOverlay.getView().setVisible(false);
        });

        return skillFrame;
    }
}
