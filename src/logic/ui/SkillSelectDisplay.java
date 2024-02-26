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

public class SkillSelectDisplay implements Display{
    private VBox view;

    private VBox skillInfoBox;
    private GridPane skillSelectorGrid;
    private ImageScaler imageScaler = new ImageScaler();

    private Text skillName;
    private Text skillManaCost;
    private Text skillActionPointCost;
    private Text skillDescription;

    public SkillSelectDisplay() {
        view = new VBox();
        view.setPadding(new Insets(5));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #2C3E50;"); // Set background color

        // Initialize skill info box
        skillInfoBox = new VBox();
        skillInfoBox.setAlignment(Pos.CENTER);
        skillInfoBox.setSpacing(10);

        // TODO: Implement CSS so the fonts are works

        // Initialize titles
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
        Label manaTitle = new Label("Mana Cost: ");
        manaTitle.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:18;" +
                "-fx-text-fill:'white';");
        Label actionPointTitle = new Label("Action Point Cost: ");
        actionPointTitle.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:18;" +
                "-fx-text-fill:'white';");

        // Initialize skill info elements
        skillName = new Text();
        skillName.setWrappingWidth(280);
        skillName.setTextAlignment(TextAlignment.CENTER);
        skillName.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                "-fx-font-size:16;" +
                "-fx-fill:'white';");
        skillManaCost = new Text();
        skillManaCost.setWrappingWidth(280);
        skillManaCost.setTextAlignment(TextAlignment.CENTER);
        skillManaCost.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        skillActionPointCost = new Text();
        skillActionPointCost.setWrappingWidth(280);
        skillActionPointCost.setTextAlignment(TextAlignment.CENTER);
        skillActionPointCost.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");
        skillDescription = new Text();
        skillDescription.setWrappingWidth(280);
        skillDescription.setTextAlignment(TextAlignment.CENTER);
        skillDescription.setStyle(
                "-fx-font-family:x16y32pxGridGazer;" +
                        "-fx-font-size:16;" +
                        "-fx-fill:'white';");


        // Add skill info elements to the skill info box
        skillInfoBox.getChildren().addAll(
                nameTitle, skillName, manaTitle, skillManaCost, actionPointTitle, skillActionPointCost, descriptionTitle, skillDescription
        );
        skillInfoBox.setPadding(new Insets(0, 0, 50, 0));

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
            VBox skillFrame = createSkillFrame(skills[i]);;
            skillSelectorGrid.add(skillFrame, col, row); // Add to the grid
            col++;
            if (col == 4) { // Adjust column count as needed
                col = 0;
                row++;
            }
        }

        skillInfoBox.setAlignment(Pos.TOP_CENTER);
        skillSelectorGrid.setAlignment(Pos.BOTTOM_CENTER);
        // Add skill info box and skill selector box to main view
        view.getChildren().addAll(skillInfoBox, skillSelectorGrid);
        view.setMaxWidth(300);
        view.setMinHeight(710);
        view.setMaxHeight(720);
    }

    // This method create the skill frame from skill in parameter
    private VBox createSkillFrame(BaseSkill skill) {
        VBox skillFrame = new VBox();

        // Scale Skill Icon
        Image skillIcon = imageScaler.resample(skill.getIcon().getImage(), 2);

        skillFrame.setAlignment(Pos.CENTER);
        skillFrame.setPrefWidth(64);
        skillFrame.setPrefHeight(64);
        skillFrame.setStyle("-fx-background-color: #34495E;"); // Set frame background color
        skillFrame.getChildren().addAll(new ImageView(skillIcon));

        BasePlayerPiece player = GameManager.getInstance().player;

        // Make EmptySlot & Locked Slot can't be select
        if (!(skill instanceof EmptySlot) && !(skill instanceof LockedSlot)) {
            skillFrame.setOnMouseClicked(mouseEvent -> {
                // Exit attack mode if activated
                if (GameManager.getInstance().isInAttackMode) {
                    // TODO: Reset Selection
                    GameManager.getInstance().gameScene.exitAttackMode();
                }

                SkillHandler.showValidSkillRange(player.getRow(), player.getCol(), skill);
                GameManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
                GameManager.getInstance().selectedSkill = skill;
                updateSelectedSkillInfo();
                System.out.println("Selected " + skill.getName() + " skill");
            });
        }

        return skillFrame;
    }

    // Method to update the selected skill information
    private void updateSelectedSkillInfo(BaseSkill skill) {
        if (skill != null) {
            skillName.setText(skill.getName());
            skillManaCost.setText(String.valueOf(skill.getManaCost()));
            skillActionPointCost.setText(String.valueOf(skill.getActionPointCost()));
            skillDescription.setText(skill.getDescription());
        } else {
            // Clear the skill info if no skill is selected
            skillName.setText("");
            skillManaCost.setText("");
            skillActionPointCost.setText("");
            skillDescription.setText("");
        }
    }

    // Update method to update the selected skill information
    public void updateSelectedSkillInfo() {
        BaseSkill selectedSkill = GameManager.getInstance().selectedSkill;
        updateSelectedSkillInfo(selectedSkill);
    }

    // Method to update skill frame into other skill
    public void updateSkillFrame(int index, BaseSkill newSkill) {
        VBox skillFrame = createSkillFrame(newSkill);

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
