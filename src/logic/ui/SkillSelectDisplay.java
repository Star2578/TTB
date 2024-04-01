package logic.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.handlers.SkillHandler;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import skills.EmptySlot;
import skills.LockedSlot;
import utils.Config;

import java.util.List;
import java.util.Stack;

public class SkillSelectDisplay implements Display{
    private VBox view;

    private GridPane skillSelectorGrid;
    private ImageScaler imageScaler = new ImageScaler();
    private ImageView frameView;


    public SkillSelectDisplay() {
        view = new VBox();
        view.setPadding(new Insets(5));
        view.setAlignment(Pos.CENTER);

        // Initialize skill selector box
        skillSelectorGrid = new GridPane();
        skillSelectorGrid.setAlignment(Pos.CENTER);
        skillSelectorGrid.setHgap(5);
        skillSelectorGrid.setVgap(5);

        BaseSkill[] skills = GameManager.getInstance().playerSkills;

        // Add skill frames
        int row = 0;
        int col = 0;
        for (int i = 0; i < GameManager.getInstance().SKILL_SLOTS; i++) {
            StackPane skillFrame = createSkillFrame(skills[i]);;
            skillSelectorGrid.add(skillFrame, col, row); // Add to the grid
            col++;
            if (col == 4) { // Adjust column count as needed
                col = 0;
                row++;
            }
        }

        skillSelectorGrid.setAlignment(Pos.BOTTOM_CENTER);
        // Add skill info box and skill selector box to main view
        view.getChildren().addAll(skillSelectorGrid);
    }

    // This method create the skill frame from skill in parameter
    private StackPane createSkillFrame(BaseSkill skill) {
        StackPane skillFrame = new StackPane();

        // Scale Skill Icon
        Image skillIcon = imageScaler.resample(skill.getIcon().getImage(), 2);
        frameView = skill.getFrame();

        skillFrame.setAlignment(Pos.CENTER);
        skillFrame.setPrefWidth(64);
        skillFrame.setPrefHeight(64);
        skillFrame.getChildren().addAll(new ImageView(skillIcon), frameView);

        BasePlayerPiece player = GameManager.getInstance().player;

        // Make EmptySlot & Locked Slot can't be select
        if (!(skill instanceof EmptySlot) && !(skill instanceof LockedSlot)) {
            skillFrame.setOnMouseClicked(mouseEvent -> {
                // Exit attack mode if activated
                if (GUIManager.getInstance().isInAttackMode) {
                    GameManager.getInstance().gameScene.exitAttackMode();
                }
                // Reset selection if other skill are selected
                if (GameManager.getInstance().selectedSkill != null) {
                    GameManager.getInstance().gameScene.resetSelection(2);
                }

                SkillHandler.showValidSkillRange(player.getRow(), player.getCol(), skill);
                GUIManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
                GameManager.getInstance().selectedSkill = skill;
                skill.getFrame().setImage(imageScaler.resample(new Image(Config.FrameSelectedPath), 2));
                System.out.println("Selected " + skill.getName() + " skill");
            });
        }

        return skillFrame;
    }

    // Method to update skill frame into other skill
    public void updateSkillFrame(int index, BaseSkill newSkill) {
        StackPane skillFrame = createSkillFrame(newSkill);

        int skillCols = 4; // 4 columns

        // Update the skill frame in the grid
        int row = index / skillCols;
        int col = index % skillCols;
        skillSelectorGrid.getChildren().remove(index); // Remove the old skill frame
        skillSelectorGrid.add(skillFrame, col, row); // Add the updated skill frame
    }

    @Override
    public Node getView() {
        return view;
    }
}
