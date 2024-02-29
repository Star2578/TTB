package logic.ui;

import items.BaseItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.ImageScaler;
import utils.Config;

public class InventoryDisplay implements Display {
    private VBox view;
    private ImageScaler imageScaler = new ImageScaler();
    private VBox armorSection;
    private VBox monsterDropsSection;
    private VBox keyItemsSection;
    private VBox usableItemsSection;

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

        // Make the main layout scrollable
        ScrollPane scrollPane = new ScrollPane(view);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(720);
        scrollPane.setStyle("-fx-background: transparent;");
        view.setFillWidth(true);
    }

    private VBox createSection(String sectionTitle, Config.ITEM_TYPE type) {
        VBox section = new VBox();
        section.setAlignment(Pos.CENTER);
        section.setSpacing(10);

        // Add section title
        Label titleLabel = new Label(sectionTitle);
        titleLabel.setTextFill(Color.WHITE);
        section.getChildren().add(titleLabel);

        // Add HBox to hold item frames
        GridPane itemRow = new GridPane();
        itemRow.setAlignment(Pos.CENTER);
        itemRow.setHgap(5);
        itemRow.setVgap(5);

        int row = 0;
        int col = 0;
        // Add item frames to the item row
        for (int i = 0; i < GameManager.getInstance().inventory.size(); i++) {
            BaseItem item = GameManager.getInstance().inventory.get(i);

            // check if it's the same type as the label
            if (item.getItemType() == type) {
                VBox itemFrame = createItemFrame(item);
                itemRow.getChildren().add(itemFrame);
                col++;
                if (col == 4) {
                    col = 0;
                    row++;
                }
            }
        }

        section.getChildren().add(itemRow);

        return section;
    }

    private VBox createItemFrame(BaseItem baseItem) {
        VBox itemFrame = new VBox();

        Image itemIcon = imageScaler.resample(baseItem.getIcon().getImage(), 2);

        itemFrame.setAlignment(Pos.CENTER);
        itemFrame.setPrefWidth(64);
        itemFrame.setPrefHeight(64);
        itemFrame.setStyle("-fx-background-color: #34495E;");
        itemFrame.getChildren().addAll(new ImageView(itemIcon));

        return itemFrame;
    }

    @Override
    public Node getView() {
        return view;
    }
}
