package logic.ui;

import items.BaseItem;
import items.EmptyFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import logic.GameManager;
import logic.ImageScaler;
import utils.Config;

import java.util.Stack;

public class InventoryDisplay implements Display {
    private VBox view;
    private ImageScaler imageScaler = new ImageScaler();
    private VBox armorSection;
    private VBox monsterDropsSection;
    private VBox keyItemsSection;
    private VBox usableItemsSection;

    private Text itemName;
    private Text itemDescription;
    private Button useItem;
    private Button throwAwayItem;

    public InventoryDisplay() {
        // Initialize the main layout
        view = new VBox();
        view.setAlignment(Pos.TOP_CENTER);
        view.setStyle("-fx-background-color: #2C3E50;");
        view.setPadding(new Insets(10));

        // Create sections for different types of items
        armorSection = createSection("Armor", Config.ITEM_TYPE.ARMOR);
        monsterDropsSection = createSection("Monster Drops", Config.ITEM_TYPE.DROPS);
        keyItemsSection = createSection("Key Items", Config.ITEM_TYPE.KEY_ITEM);
        usableItemsSection = createSection("Usable Items", Config.ITEM_TYPE.USABLE);

        // Add sections to the main layout
        view.getChildren().addAll(
                armorSection,
                monsterDropsSection,
                keyItemsSection,
                usableItemsSection
        );

        Label nameTitle = new Label("Name: ");
        nameTitle.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:18;" +
                "-fx-text-fill:'white';");
        Label descriptionTitle = new Label("Description: ");
        descriptionTitle.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:18;" +
                "-fx-text-fill:'white';");
        useItem = new Button("Use Item");
        throwAwayItem = new Button("Throw Away");

        itemName = new Text();
        itemName.setWrappingWidth(280);
        itemName.setTextAlignment(TextAlignment.CENTER);
        itemName.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-fill:'white';");

        itemDescription = new Text();
        itemDescription.setWrappingWidth(280);
        itemDescription.setTextAlignment(TextAlignment.CENTER);
        itemDescription.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-fill:'white';");

        VBox itemInfoBox = new VBox();
        itemInfoBox.setAlignment(Pos.CENTER);
        itemInfoBox.setSpacing(10);
        itemInfoBox.setPadding(new Insets(10, 0, 0, 0));

        itemInfoBox.getChildren().addAll(
                nameTitle, itemName, descriptionTitle, itemDescription, useItem, throwAwayItem
        );
        view.getChildren().add(itemInfoBox);

        view.setFillWidth(true);
        view.setMaxWidth(300);
        view.setMinHeight(710);
        view.setMaxHeight(720);
    }

    private VBox createSection(String sectionTitle, Config.ITEM_TYPE type) {
        VBox section = new VBox();
        section.setAlignment(Pos.CENTER);
        section.setSpacing(5);

        // Add section title
        Label titleLabel = new Label(sectionTitle);
        titleLabel.setTextFill(Color.WHITE);
        section.getChildren().add(titleLabel);

        // Create a GridPane to hold item frames
        GridPane itemGrid = new GridPane();
        itemGrid.setStyle("-fx-background-color: #2C3E50;");
        itemGrid.setAlignment(Pos.CENTER);
        itemGrid.setHgap(2);
        itemGrid.setVgap(2);

        int itemsPerRow = 4;
        int totalItems = 0;

        // Add items to the item grid
        for (BaseItem item : GameManager.getInstance().inventory) {
            if (item.getItemType() == type) {
                StackPane itemFrame = createItemFrame(item);
                int row = totalItems / itemsPerRow;
                int col = totalItems % itemsPerRow;
                itemGrid.add(itemFrame, col, row);
                totalItems++;
            }
        }

        // Add empty frames to fill the remaining space in the last row
        int emptyFrames = itemsPerRow - (totalItems % itemsPerRow);
        for (int i = 0; i < emptyFrames; i++) {
            StackPane emptyFrame = createItemFrame(new EmptyFrame());
            int row = totalItems / itemsPerRow;
            int col = totalItems % itemsPerRow;
            itemGrid.add(emptyFrame, col, row);
            totalItems++;
        }


        section.setStyle("-fx-background-color: #2C3E50;");

        // Make the section scrollable vertically
        ScrollPane scrollPane = new ScrollPane(itemGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(64);
        scrollPane.setStyle("-fx-background-color: #2C3E50;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        section.getChildren().add(scrollPane);
        return section;
    }

    private StackPane createItemFrame(BaseItem baseItem) {
        StackPane itemFrame = new StackPane();

        Image itemIcon = imageScaler.resample(baseItem.getIcon().getImage(), 2);
        ImageView frameView = baseItem.getFrame();

        itemFrame.setAlignment(Pos.CENTER);
        itemFrame.setPrefWidth(64);
        itemFrame.setPrefHeight(64);
        itemFrame.setStyle("-fx-background-color: #34495E;");
        itemFrame.getChildren().addAll(new ImageView(itemIcon), frameView);

        itemFrame.setOnMouseClicked(mouseEvent -> {
            if (GameManager.getInstance().selectedItem != null) {
                GameManager.getInstance().gameScene.resetSelection(3);
            }
            baseItem.getFrame().setImage(imageScaler.resample(new Image(Config.FrameSelectedPath), 2));
            GameManager.getInstance().selectedItem = baseItem;
            updateSelectedItemInfo();
        });

        return itemFrame;
    }

    private void updateSelectedItemInfo(BaseItem item) {
        if (item != null) {
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
        } else {
            // clear item if there's none selected
            itemName.setText("");
            itemDescription.setText("");
        }
    }

    private void updateSelectedItemInfo() {
        BaseItem selectedItem = GameManager.getInstance().selectedItem;
        updateSelectedItemInfo(selectedItem);
    }

    @Override
    public Node getView() {
        return view;
    }
}
