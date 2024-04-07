package logic.ui.display;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.GameManager;
import logic.ImageScaler;
import logic.SceneManager;
import logic.handlers.SkillHandler;
import logic.ui.GUIManager;
import logic.ui.overlay.SkillInfoOverlay;
import pieces.player.BasePlayerPiece;
import skills.BaseSkill;
import skills.EmptySkill;
import skills.LockedSlot;
import utils.*;

public class SkillSelectDisplay implements Display{
    private VBox view;

    private GridPane skillSelectorGrid;
    private ImageScaler imageScaler = new ImageScaler();
    private SkillInfoOverlay skillInfoOverlay = new SkillInfoOverlay();
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

        updateSkillSelect();

        skillSelectorGrid.setAlignment(Pos.BOTTOM_CENTER);
        // Add skill info box and skill selector box to main view
        view.getChildren().addAll(skillSelectorGrid);
    }

    private void updateSkillSelect() {
        skillSelectorGrid.getChildren().clear();
        BaseSkill[] playerSkills = GameManager.getInstance().playerSkills;
        // Add skill frames
        int row = 0;
        int col = 0;

        for (int i = 0; i < GameManager.getInstance().SKILL_SLOTS; i++) {
            if (playerSkills[i] == null) {
                if (i < GameManager.getInstance().unlockedSlots) {
                    playerSkills[i] = new EmptySkill();
                } else {
                    playerSkills[i] = new LockedSlot();
                }
            }

            StackPane skillFrame = createSkillFrame(playerSkills[i]);;
            skillSelectorGrid.add(skillFrame, col, row); // Add to the grid
            col++;
            if (col == 4) { // Adjust column count as needed
                col = 0;
                row++;
            }
        }
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
        if (!(skill instanceof EmptySkill) && !(skill instanceof LockedSlot)) {
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

            skillFrame.setOnMouseEntered(mouseEvent -> {
                skillInfoOverlay.getView().setVisible(true);
                skillInfoOverlay.getView().toFront();

                // Update overlay info
                skillInfoOverlay.getTitle().setText(skill.getName());
                skillInfoOverlay.getTitle().setTextFill(skill.getNameColor());
                skillInfoOverlay.getDesc().setText(skill.getDescription());

                skillInfoOverlay.getDataContainer().getChildren().clear();
                skillInfoOverlay.newInfo("Mana", Color.DARKBLUE, String.valueOf(skill.getManaCost()));
                skillInfoOverlay.newInfo("Action Point", Color.ORANGE, String.valueOf(skill.getActionPointCost()));

                // Other skill info base on type
                if (skill instanceof Attack a) {
                    skillInfoOverlay.newInfo("Attack", Color.DARKRED, String.valueOf(a.getAttack()));
                }if (skill instanceof Healing h) {
                    skillInfoOverlay.newInfo("Heal", Color.DARKGREEN, String.valueOf(h.getHeal()));
                }if (skill instanceof RefillMana r) {
                    skillInfoOverlay.newInfo("Mana Refill", Color.CYAN, "+" + r.getRefill());
                }
            });

            skillFrame.setOnMouseExited(mouseEvent -> {
                skillInfoOverlay.getView().setVisible(false);
            });
        }

        return skillFrame;
    }

    // Method to update skill frame into other skill
    public void updateSkillFrame(int index, BaseSkill newSkill) {
        GameManager.getInstance().playerSkills[index] = newSkill;

        updateSkillSelect();
    }

    public SkillInfoOverlay getSkillInfoOverlay() {
        return skillInfoOverlay;
    }

    @Override
    public Node getView() {
        return view;
    }
}
