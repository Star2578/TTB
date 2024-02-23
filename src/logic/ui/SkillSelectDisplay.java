package logic.ui;

import game.GameScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.handlers.SkillHandler;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import utils.Config;

import java.util.List;

public class SkillSelectDisplay implements Display{
    private VBox view;

    private VBox skillInfoBox;
    private HBox skillSelectorBox;
    private ImageScaler imageScaler = new ImageScaler();

    public SkillSelectDisplay() {
        view = new VBox();
        view.setPadding(new Insets(5));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #2C3E50;"); // Set background color

        // Initialize skill info box
        skillInfoBox = new VBox();
        skillInfoBox.setAlignment(Pos.CENTER);
        skillInfoBox.setSpacing(10);
        Label titleLabel = new Label("Skill Name / Description / Costs");
        titleLabel.setTextFill(Color.WHITE);
        skillInfoBox.getChildren().add(titleLabel);

        // Initialize skill selector box
        skillSelectorBox = new HBox();
        skillSelectorBox.setAlignment(Pos.CENTER);
        skillSelectorBox.setSpacing(5);

        List<BaseSkill> skills = GameManager.getInstance().playerSkills;

        // Add skill frames (example)
        for (int i = 0; i < 4; i++) {
            VBox skillFrame = createSkillFrame(skills.get(0));
            skillSelectorBox.getChildren().add(skillFrame);
        }

        // Add skill info box and skill selector box to main view
        view.getChildren().addAll(skillInfoBox, skillSelectorBox);
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

        skillFrame.setOnMouseClicked(mouseEvent -> {
            SkillHandler.showValidSkillRange(player.getRow(), player.getCol(), skill);
            GameManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.AttackCursor);
            GameManager.getInstance().selectedSkill = skill;
            System.out.println("Selected " + skill.getName() + " skill");
        });

        return skillFrame;
    }

    @Override
    public Node getView() {
        return view;
    }
}
