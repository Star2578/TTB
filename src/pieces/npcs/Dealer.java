package pieces.npcs;

import items.BaseItem;
import items.EmptyItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import logic.GameManager;
import logic.ImageScaler;
import logic.ui.GUIManager;
import logic.ui.display.NpcDisplay;
import logic.ui.overlay.ItemInfoOverlay;
import logic.ui.overlay.SkillInfoOverlay;
import skills.BaseSkill;
import skills.EmptySkill;
import utils.Attack;
import utils.Config;
import utils.Healing;
import utils.RefillMana;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Dealer extends BaseNpcPiece {
    private ImageScaler imageScaler = new ImageScaler();
    private List<BaseItem> items_noDuplicate = new ArrayList<>(); // contain items that already on the shop, so there won't be any duplicate
    private List<BaseSkill> skills_noDuplicate = new ArrayList<>(); // contain skills that already on the shop, so there won't be any duplicate
    private SkillInfoOverlay skillInfoOverlay = new SkillInfoOverlay();
    private ItemInfoOverlay itemInfoOverlay = new ItemInfoOverlay();
    private VBox shopLayout; // layout
    private StackPane overlayPane; // contain shop grid & info overlay
    private GridPane skillShopGrid;
    private GridPane itemShopGrid;
    private Text priceTag;
    private int buySkillIndex = 0;

    public Dealer() {
        super("Dealer", Config.DealerPortraitPath, 1);
        importDialogues("res/dialogues/dealer-dialogue.json");
        talk("greetings"); // setup starting dialogue

        setupAnimation(Config.DealerAnimationPath, 0, -10, 32, 48 , true);
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
                    npcDisplay.newAdditionalOverlay(shopLayout);
                } else {
                    npcDisplay.clearAdditionalOverlay();
                }
            }
        });
    }

    private void setupShop() {
        shopLayout = new VBox();
        shopLayout.setMinHeight(720);
        shopLayout.setTranslateX(-300);

        priceTag = new Text("Placeholder");
        priceTag.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:30;" +
                "-fx-fill:'gold';" +
                "-fx-stroke:black;" +
                "-fx-stroke-width:2px;");
        priceTag.setVisible(false);

        // Add shop components
        itemShopGrid = new GridPane();
        itemShopGrid.setBackground(Background.fill(Color.GOLD));
        itemShopGrid.setHgap(5);
        itemShopGrid.setVgap(5);
        itemShopGrid.setPadding(new Insets(10));

        skillShopGrid = new GridPane();
        skillShopGrid.setBackground(Background.fill(Color.CYAN));
        skillShopGrid.setHgap(5);
        skillShopGrid.setVgap(5);
        skillShopGrid.setPadding(new Insets(10));

        // setup item/skill in shop
        items_noDuplicate = getRandomItems(8);
        skills_noDuplicate = getRandomSkills(8);

        updateShop();

        // Setup child in pane
        overlayPane = new StackPane();
        VBox shopContainer = new VBox();
        shopContainer.getChildren().addAll(itemShopGrid, skillShopGrid);
        overlayPane.getChildren().addAll(shopContainer, itemInfoOverlay.getView(), skillInfoOverlay.getView(), priceTag);

        overlayPane.setOnMouseMoved(event -> {
            // Update the position of the BoxOverlay to follow the mouse
            itemInfoOverlay.updatePosition(event.getX(), event.getY(), -160, -190);
            skillInfoOverlay.updatePosition(event.getX(), event.getY(), -160, -190);
            priceTagPosition(event.getX(), event.getY(), 180, 150);
        });

        shopLayout.getChildren().addAll(overlayPane);
    }

    private List<BaseItem> getRandomItems(int count) {
        List<BaseItem> randomItems = new ArrayList<>();
        BaseItem[] pool = GameManager.getInstance().ITEM_POOL;
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            // Create a list of items not already selected
            List<BaseItem> availableItems = Arrays.stream(pool)
                    .filter(item -> !items_noDuplicate.contains(item))
                    .toList();

//            // If all items have been selected, reset the selected items list
//            if (availableItems.isEmpty()) {
//                items_noDuplicate.clear();
//                availableItems.addAll(Arrays.asList(pool));
//            }

            // Randomly select a skill from the available skills
            BaseItem randomItem = availableItems.get(random.nextInt(availableItems.size()));

            // Create a new instance of the randomly selected skill
            BaseItem skill = createNewInstance(randomItem);

            // Add the new skill instance to the list of random skills
            randomItems.add(skill);
        }

        return randomItems;
    }
    private StackPane createItemFrame(BaseItem item) {
        StackPane itemFrame = new StackPane();

        Image itemIcon = imageScaler.resample(item.getIcon().getImage(), 2);
        ImageView frameView = item.getFrame();

        itemFrame.setAlignment(Pos.CENTER);
        itemFrame.setPrefWidth(64);
        itemFrame.setPrefHeight(64);
        itemFrame.getChildren().addAll(new ImageView(itemIcon), frameView);

        if (!(item instanceof EmptyItem)) {
            itemFrame.setOnMouseClicked(mouseEvent -> {
                GameManager.getInstance().inventory.add(item);
                GUIManager.getInstance().inventoryDisplay.updateInventoryUI();

                // turn item, in items_noDuplicate, that got click into new instance of EmptyItem
                int index = items_noDuplicate.indexOf(item);
                items_noDuplicate.set(index, new EmptyItem());

                updateShop();
            });

            itemFrame.setOnMouseEntered(mouseEvent -> {
                itemInfoOverlay.getView().setVisible(true);

                // show price
                priceTag.setText("$" + item.getPrice());
                priceTag.setVisible(true);

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
                priceTag.setVisible(false);
            });
        }

        return itemFrame;
    }

    private List<BaseSkill> getRandomSkills(int count) {
        List<BaseSkill> randomSkills = new ArrayList<>();
        List<BaseSkill> playerOwned = Arrays.asList(GameManager.getInstance().playerSkills);
        BaseSkill[] pool = GameManager.getInstance().UNIVERSAL_SKILL_POOL;
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            List<BaseSkill> availableSkills = Arrays.stream(pool)
                    .filter(skill -> !skills_noDuplicate.contains(skill) && !playerOwned.contains(skill))
                    .toList();

//            // If all skills have been selected, reset the selected skills list
//            if (availableSkills.isEmpty()) {
//                skills_noDuplicate.clear();
//                availableSkills.addAll(Arrays.asList(pool));
//            }

            // Randomly select a skill from the available skills
            BaseSkill randomSkill = availableSkills.get(random.nextInt(availableSkills.size()));

            // Create a new instance of the randomly selected skill
            BaseSkill skill = createNewInstance(randomSkill);

            // Add the new skill instance to the list of random skills
            randomSkills.add(skill);
        }

        return randomSkills;
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

        if (!(skill instanceof EmptySkill)) {
            skillFrame.setOnMouseClicked(mouseEvent -> {
                // TODO : Buy Skill Logic
                GUIManager.getInstance().skillSelectDisplay.updateSkillFrame(buySkillIndex, skill);

                // turn skill, in skills_noDuplicate, that got click into new instance of EmptySkill
                int index = skills_noDuplicate.indexOf(skill);
                skills_noDuplicate.set(index, new EmptySkill());

                updateShop();
            });

            skillFrame.setOnMouseEntered(mouseEvent -> {
                skillInfoOverlay.getView().setVisible(true);
                skillInfoOverlay.getView().toFront();

                // show price
                priceTag.setText("$" + skill.getPrice());
                priceTag.setVisible(true);

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
                priceTag.setVisible(false);
            });
        }

        return skillFrame;
    }

    public void priceTagPosition(double x, double y, double offsetX, double offsetY) {
        // Adjust the layout parameters of the VBox to position it at (x, y)
        priceTag.setTranslateX(x - offsetX);
        priceTag.setTranslateY(y - offsetY);
    }

    private BaseSkill createNewInstance(BaseSkill skill) {
        try {
            // Get the class of the skill
            Class<? extends BaseSkill> skillClass = skill.getClass();

            // Get the constructor of the skill class
            Constructor<? extends BaseSkill> constructor = skillClass.getDeclaredConstructor();

            // Make the constructor accessible, as it may be private
            constructor.setAccessible(true);

            // Instantiate a new instance of the skill class using the constructor
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            System.out.println("Error when creating new instance @Dealer :" + e.getMessage());; // Handle the exception appropriately
        }
        return null;
    }
    private BaseItem createNewInstance(BaseItem item) {
        try {
            // Get the class of the item
            Class<? extends BaseItem> itemClass = item.getClass();

            // Get the constructor of the item class
            Constructor<? extends BaseItem> constructor = itemClass.getDeclaredConstructor();

            // Make the constructor accessible, as it may be private
            constructor.setAccessible(true);

            // Instantiate a new instance of the item class using the constructor
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            System.out.println("Error when creating new instance @Dealer :" + e.getMessage());; // Handle the exception appropriately
        }
        return null;
    }


    // Method to update item/skill in shop
    private void updateShop() {
        itemShopGrid.getChildren().clear();
        skillShopGrid.getChildren().clear();
        int itemsPerRow = 4;

        // Add items to the item shop grid
        for (int i = 0; i < items_noDuplicate.size(); i++) {
            StackPane item = createItemFrame(items_noDuplicate.get(i));
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            itemShopGrid.add(item, col, row);
        }

        // Add skills to the skill shop grid
        for (int i = 0; i < skills_noDuplicate.size(); i++) {
            StackPane item = createSkillFrame(skills_noDuplicate.get(i));
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            skillShopGrid.add(item, col, row);
        }

        for (int i = 0; i < GameManager.getInstance().unlockedSlots; i++) {
            Button button = new Button(String.valueOf(i));
            if (buySkillIndex == i) {
                button.setStyle(
                        "-fx-border-color:#f56f42;" +
                        "-fx-background-color:#f7ca7c;" +
                        "-fx-color: #f56f42");
            }
            int finalIndex = i;
            button.setOnMouseClicked(mouseEvent -> {
                buySkillIndex = finalIndex;
                updateShop();
            });
            int row = (skills_noDuplicate.size() + i) / itemsPerRow;
            int col = (skills_noDuplicate.size() + i) % itemsPerRow;
            skillShopGrid.add(button, col, row);
        }
    }
}
