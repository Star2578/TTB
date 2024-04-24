package logic.ui.display;

import items.BaseItem;
import items.EmptyItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.SoundManager;
import logic.handlers.ItemHandler;
import logic.ui.GUIManager;
import logic.ui.overlay.ItemInfoOverlay;
import pieces.player.BasePlayerPiece;
import utils.Config;
import utils.RefillMana;
import utils.Usable;

public class InventoryDisplay implements Display {
    private VBox view;
    private VBox usableItemsSection;
    private ImageScaler imageScaler = new ImageScaler();
    private ItemInfoOverlay itemInfoOverlay = new ItemInfoOverlay();

    private Button useItem;
    private Button throwAwayItem;

    public InventoryDisplay() {
        // Initialize the main layout
        view = new VBox();
        view.setAlignment(Pos.TOP_CENTER);
        view.setPadding(new Insets(10));

        // Create sections for different types of items
        usableItemsSection = createSection("Items");

        // Add sections to the main layout
        view.getChildren().addAll(
                usableItemsSection
        );
        useItem = new Button("Use Item");
        throwAwayItem = new Button("Throw Away");


        useItem.setOnMouseClicked(mouseEvent -> {
            BaseItem currentItem = GameManager.getInstance().selectedItem;
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);
            if (currentItem != null && !(currentItem instanceof EmptyItem)) {
                System.out.println("Use " + currentItem.getName());
                GameManager.getInstance().gameScene.resetSelectionAll();

                BasePlayerPiece player = GameManager.getInstance().player;

                ItemHandler.showValidItemRange(player.getRow(), player.getCol(), currentItem);
                GUIManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
            }
        });

        throwAwayItem.setOnMouseClicked(mouseEvent -> {
            BaseItem currentItem = GameManager.getInstance().selectedItem;
            SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);

            // throw item away
            throwAwayItem(currentItem);
        });

        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(50);
        buttonContainer.getChildren().addAll(useItem, throwAwayItem);

        VBox itemInfoBox = new VBox();
        itemInfoBox.setAlignment(Pos.CENTER);
        itemInfoBox.setSpacing(10);
        itemInfoBox.setPadding(new Insets(10, 0, 0, 0));

        itemInfoBox.getChildren().addAll(
                buttonContainer
        );
        view.getChildren().add(itemInfoBox);
    }

    private VBox createSection(String sectionTitle) {
        VBox section = new VBox();
        section.setAlignment(Pos.CENTER);
        section.setSpacing(5);

        // Add section title
        Label titleLabel = new Label(sectionTitle);
        titleLabel.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");
        section.getChildren().add(titleLabel);

        // Create a GridPane to hold item frames
        GridPane itemGrid = new GridPane();
        itemGrid.setStyle("-fx-background-color: #434343;");
        itemGrid.setAlignment(Pos.CENTER);
        itemGrid.setHgap(2);
        itemGrid.setVgap(2);

        int itemsPerRow = 4;
        int totalItems = 0;
        int maxItems = GameManager.getInstance().itemSlots;

        // Add items to the item grid
        for (BaseItem item : GameManager.getInstance().inventory) {
            StackPane itemFrame = createItemFrame(item);
            int row = totalItems / itemsPerRow;
            int col = totalItems % itemsPerRow;
            itemGrid.add(itemFrame, col, row);
            totalItems++;
        }

        // Calculate the number of empty frames to add
        int emptyFrames = itemsPerRow - (totalItems % itemsPerRow);
        if (totalItems % itemsPerRow == 0 && totalItems > 0 || totalItems >= maxItems) {
            emptyFrames = 0;
        }

        for (int i = 0; i < emptyFrames; i++) {
            StackPane emptyFrame = createItemFrame(new EmptyItem());
            int row = totalItems / itemsPerRow;
            int col = totalItems % itemsPerRow;
            itemGrid.add(emptyFrame, col, row);
            totalItems++;
        }

        // Make the section scrollable vertically
        ScrollPane scrollPane = new ScrollPane(itemGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: #434343;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        section.getChildren().add(scrollPane);
        return section;
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
                SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);

                if (GameManager.getInstance().selectedItem != null) {
                    if (GameManager.getInstance().selectedItem == item && item instanceof Usable usable) {
                        if (usable.castOnSelf()) {
                            // Use item
                            usable.useItem(GameManager.getInstance().player);
                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + GameManager.getInstance().selectedItem.getName());

                            GameManager.getInstance().gameScene.resetSelection(3);
                            GUIManager.getInstance().inventoryDisplay.throwAwayItem(item);
                            return;
                        }
                    }
                    GameManager.getInstance().gameScene.resetSelection(3);
                }
                item.getFrame().setImage(imageScaler.resample(new Image(Config.FrameSelectedPath), 2));
                GameManager.getInstance().selectedItem = item;
            });

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
        }

        return itemFrame;
    }

    public void updateInventoryUI() {
        System.out.println("update inventory ui");
        usableItemsSection.getChildren().clear();

        usableItemsSection.getChildren().add(createSection("Items"));
    }

    public void throwAwayItem(BaseItem toThrow) {
        if (toThrow != null && !(toThrow instanceof EmptyItem)) {
            System.out.println("Throw " + toThrow.getName() + " away");

            // remove item from inventory
            GameManager.getInstance().inventory.remove(toThrow);

            // update inventory ui
            updateInventoryUI();
        }
    }

    public ItemInfoOverlay getItemInfoOverlay() {
        return itemInfoOverlay;
    }
    public void enableFrame() {
        useItem.setDisable(false);
        throwAwayItem.setDisable(false);
        usableItemsSection.setDisable(false);
    }
    public void disableFrame() {
        useItem.setDisable(true);
        throwAwayItem.setDisable(true);
        usableItemsSection.setDisable(true);
    }
    @Override
    public Node getView() {
        return view;
    }
}
