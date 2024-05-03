package logic.gameUI.display;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NpcDisplay implements Display{
    private VBox view;
    private ImageView npcPortrait;
    private Text npcName;
    private Text dialogueText;
    private VBox optionContainer;
    private BorderPane additionalOverlay;

    public NpcDisplay() {
        view = new VBox();
        VBox.setVgrow(view, Priority.ALWAYS);
        view.setMaxWidth(300);
        view.setSpacing(15);
        view.setPadding(new Insets(0, 10, 0, 10));

        // Set dimensions for the NPC portrait
        npcPortrait = new ImageView();
        npcPortrait.setFitWidth(64);
        npcPortrait.setPreserveRatio(true);

        optionContainer = new VBox();
        optionContainer.setAlignment(Pos.BOTTOM_CENTER);
        optionContainer.setSpacing(10);
        optionContainer.setPadding(new Insets(0, 0, 15, 0));
        VBox.setVgrow(optionContainer, Priority.ALWAYS);

        npcName = new Text("Placeholder");
        npcName.setFill(Color.WHITE);
        npcName.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:20;" +
                "-fx-text-fill:'white';");

        dialogueText = new Text();
        dialogueText.setText("Well, let me tell you a tale that spans across the vast expanse of time, where heroes rise and fall like the ebb and flow of the tides, where the whispers of ancient gods echo through the chambers of destiny, and where the very fabric of reality is but a fragile thread in the hands of fate. It all began in a small village nestled amidst the emerald hills, where the sun kissed the earth with its golden rays each dawn, and the moon cast its silver glow upon the land each night.");
        dialogueText.setWrappingWidth(240);
        dialogueText.setFill(Color.WHITE);
        dialogueText.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-text-fill:'white';");

        VBox portraitContainer = new VBox(npcPortrait, npcName);
        portraitContainer.setAlignment(Pos.CENTER);

        ScrollPane dialogueScrollPane = getScrollPane();

        VBox dialogueContainer = new VBox(dialogueScrollPane);
        dialogueContainer.setStyle(
                "-fx-border-width: 5px;" +
                " -fx-border-color: white;");
        dialogueContainer.setMaxWidth(280);
        dialogueContainer.setPadding(new Insets(8));
        dialogueContainer.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(dialogueContainer, Priority.ALWAYS);


        StackPane stackPane = new StackPane();
        stackPane.setPrefHeight(720);
        additionalOverlay = new BorderPane(); // This pane is for additional overlay i.e. Shop
        VBox mainContainer = new VBox();

        // Add nodes to the view
        mainContainer.getChildren().addAll(portraitContainer, dialogueContainer, optionContainer);
        stackPane.getChildren().addAll(mainContainer, additionalOverlay);
        stackPane.setPickOnBounds(false);
        additionalOverlay.setPickOnBounds(false);

        view.getChildren().add(stackPane);
    }

    private ScrollPane getScrollPane() {
        ScrollPane dialogueScrollPane = new ScrollPane();
        dialogueScrollPane.setContent(dialogueText);
        dialogueScrollPane.setFitToWidth(true);
        dialogueScrollPane.setFitToHeight(true);
        dialogueScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogueScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogueScrollPane.setStyle(
                "-fx-background: transparent; " +
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent;");
        return dialogueScrollPane;
    }

    public void setNpcPortrait(Image image) {
        npcPortrait.setImage(image);
    }
    public void setDialogueText(String text) {
        dialogueText.setText(text);
    }
    public void addDialogueOption(String optionText, Runnable action) {
        Button optionButton = new Button(optionText);
        optionButton.setMinWidth(240);
        optionButton.setOnAction(event -> action.run());
        optionContainer.getChildren().add(optionButton);
    }
    public void clearDialogueOption() {
        optionContainer.getChildren().clear();
    }
    public Text getNpcName() {
        return npcName;
    }
    public void newAdditionalOverlay(Node node) {
        additionalOverlay.getChildren().add(node);
    }
    public void clearAdditionalOverlay() {
        additionalOverlay.getChildren().clear();
    }
    public int getAdditionalOverlaySize() {
        return additionalOverlay.getChildren().size();
    }
    @Override
    public Node getView() {
        return view;
    }
}
