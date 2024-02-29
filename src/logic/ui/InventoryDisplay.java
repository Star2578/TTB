package logic.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class InventoryDisplay implements Display {
    private VBox view;
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
        armorSection = createSection("Armor");
        monsterDropsSection = createSection("Monster Drops");
        keyItemsSection = createSection("Key Items");
        usableItemsSection = createSection("Usable Items");

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

    private VBox createSection(String sectionTitle) {
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
        for (int i = 0; i < 4; i++) {
            // Replace ItemFrame with your custom item frame class
            VBox itemFrame = createItemFrame(); // You need to implement this class
            itemRow.getChildren().add(itemFrame);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        section.getChildren().add(itemRow);

        return section;
    }

    private VBox createItemFrame() {
        return new VBox();
    }

    @Override
    public Node getView() {
        return view;
    }
}
