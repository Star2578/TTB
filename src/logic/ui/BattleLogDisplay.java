package logic.ui;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class BattleLogDisplay implements Display{
    private VBox view;

    public BattleLogDisplay() {
        view = new VBox();

        /*
            TODO list
                - Scrollable
                - Have function to receive logs
                - Have function to update logs
                - Have function to clear logs
                - How to highlight specific words?
         */
    }

    @Override
    public Node getView() {
        return view;
    }
}
