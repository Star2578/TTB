package logic.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SkillSelectDisplay implements Display{
    private VBox view;

    public SkillSelectDisplay() {
        view = new VBox();
        Label label = new Label("Skill Select Display");
        label.setTextFill(Color.WHITE);
        view.getChildren().add(label);
    }

    @Override
    public void initialize() {

    }

    @Override
    public Node getView() {
        return view;
    }
}
