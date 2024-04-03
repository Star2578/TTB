package logic.ui.overlay;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SkillInfoOverlay extends Overlay {

    private Label title;
    private Text desc;
    private VBox dataContainer;

    public SkillInfoOverlay() {
        super(180, 220);
        view.setVisible(false);


        title = new Label("Placeholder");
        title.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:22;" +
                "-fx-text-fill:'white'; " +
                "-fx-font-weight: bold;");
        title.setWrapText(true);

        desc = new Text("lorem lorem lorem lorem lorem");
        desc.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-fill:'white';");
        desc.setWrappingWidth(view.getMaxWidth());

        dataContainer = new VBox();
        dataContainer.setAlignment(Pos.BOTTOM_LEFT);
        dataContainer.setSpacing(3);

        // Make dataContainer fill the height
        VBox.setVgrow(dataContainer, Priority.ALWAYS);

        view.getChildren().addAll(title, desc, dataContainer);
        view.setSpacing(5);

        // Style the VBox to give it a border and background color
        view.setStyle("-fx-border-color: white; " +
                "-fx-border-width: 5px; " +
                "-fx-border-style: solid; " +
                "-fx-background-color: black;");

        // Set padding to give some space between content and border
        view.setPadding(new Insets(5));
    }

    public void newInfo(String title, Color titleColor, String value) {
        Text titleText = new Text("Mana");
        titleText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-font-weight: bold;");
        titleText.setText(title);
        titleText.setFill(titleColor);
        Text colon = new Text(" : ");
        colon.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        Text valueText = new Text("5");
        valueText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        valueText.setText(value);
        HBox container = new HBox(titleText, colon, valueText);

        dataContainer.getChildren().add(container);
    }

    public Label getTitle() {
        return title;
    }
    public Text getDesc() {
        return desc;
    }
    public VBox getDataContainer() {
        return dataContainer;
    }
}
