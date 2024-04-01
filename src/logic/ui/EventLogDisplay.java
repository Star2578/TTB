package logic.ui;

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
//        logContainer.setMaxWidth(300);

        scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: black;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        addLog("Testing message 1");
        addLog("Testing message 2");
        addLog("Testing message 3");
        addLog("My name is Yoshikage Kira. I'm 33 years old. My house is in the northeast section of Morioh, where all the villas are, and I am not married. I work as an employee for the Kame Yu department stores, and I get home every day by 8 PM at the latest. I don't smoke, but I occasionally drink. I'm in bed by 11 PM, and make sure I get eight hours of sleep, no matter what. After having a glass of warm milk and doing about twenty minutes of stretches before going to bed, I usually have no problems sleeping until morning. Just like a baby, I wake up without any fatigue or stress in the morning. I was told there were no issues at my last check-up. I'm trying to explain that I'm a person who wishes to live a very quiet life. I take care not to trouble myself with any enemies, like winning and losing, that would cause me to lose sleep at night. That is how I deal with society, and I know that is what brings me happiness. Although, if I were to fight I wouldn't lose to anyone.");

        view.getChildren().add(scrollPane);
    }

    public void addLog(String msg) {
        Text text = new Text(msg);
        text.setFill(Color.WHITE);
        text.setWrappingWidth(260);

        // Add new log text from the bottom
        logContainer.getChildren().add(text);

        scrollPane.setVvalue(1.0);
    }

    public void clearLog() {
        logContainer.getChildren().clear();
    }

    @Override
    public Node getView() {
        return view;
    }
}
