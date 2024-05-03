package logic.gameUI.display;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class EventLogDisplay implements Display{
    private VBox view;

    private ScrollPane scrollPane;
    private VBox logContainer;
    public EventLogDisplay() {
        view = new VBox();

        Label header = new Label("Event Log");
        header.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");
        header.setPadding(new Insets(10, 10, 5, 10));
        view.getChildren().add(header);

        logContainer = new VBox();
        logContainer.setSpacing(10);
        logContainer.setStyle("-fx-background-color: black;");
        logContainer.setAlignment(Pos.BOTTOM_LEFT);
        logContainer.setPadding(new Insets(10));
        logContainer.setPrefHeight(700);

        scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        view.getChildren().add(scrollPane);
    }

    public void addLog(String msg) {
        Text text = new Text(msg);
        text.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");
        text.setFill(Color.WHITE);
        text.setWrappingWidth(260);

        // Add new log text from the bottom
        logContainer.getChildren().add(text);

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
    public void addLog(String msg, Color color) {
        Text text = new Text(msg);
        text.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");
        text.setFill(color);
        text.setWrappingWidth(260);

        // Add new log text from the bottom
        logContainer.getChildren().add(text);

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    public void clearLog() {
        logContainer.getChildren().clear();
    }

    @Override
    public Node getView() {
        return view;
    }
}
