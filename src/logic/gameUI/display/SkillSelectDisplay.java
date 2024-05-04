package logic.gameUI.display;

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
import utils.ImageScaler;
import logic.SceneManager;
import logic.SoundManager;
import logic.handlers.SkillHandler;
import logic.gameUI.GUIManager;
import logic.gameUI.overlay.InfoOverlay;
import pieces.*;
import pieces.players.BasePlayerPiece;
import skills.BaseSkill;
import skills.EmptySkill;
import skills.LockedSlot;
import utils.*;

public class SkillSelectDisplay implements Display{
    private VBox view;

    private GridPane skillSelectorGrid;
    private InfoOverlay infoOverlay = new InfoOverlay();
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

    public void updateSkillSelect() {
        skillSelectorGrid.getChildren().clear();
        BaseSkill[] playerSkills = GameManager.getInstance().playerSkills;
        // Add skill frames
        int row = 0;
        int col = 0;

        for (int i = 0; i < GameManager.getInstance().SKILL_SLOTS; i++) {
            StackPane skillFrame;
            if (playerSkills[i] == null) {
                if (i < GameManager.getInstance().skillUnlockedSlots) {
                    skillFrame = createSkillFrame(new EmptySkill());
                } else {
                    skillFrame = createSkillFrame(new LockedSlot());
                }
            } else {
                skillFrame = createSkillFrame(playerSkills[i]);
            }

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
        Image skillIcon = ImageScaler.resample(skill.getIcon().getImage(), 2);
        frameView = skill.getFrame();

        skillFrame.setAlignment(Pos.CENTER);
        skillFrame.setPrefWidth(64);
        skillFrame.setPrefHeight(64);
        skillFrame.getChildren().addAll(new ImageView(skillIcon), frameView);

        BasePlayerPiece player = GameManager.getInstance().player;

        // Make EmptySlot & Locked Slot can't be select
        if (!(skill instanceof EmptySkill) && !(skill instanceof LockedSlot)) {
            skillFrame.setOnMouseClicked(mouseEvent -> {
                GameManager.getInstance().gameScene.resetSelection(0);
                GameManager.getInstance().gameScene.resetSelection(1);
                GameManager.getInstance().gameScene.resetSelection(3);
                SoundManager.getInstance().playSoundEffect(Config.sfx_buttonSound);

                // Reset selection if other skill are selected
                if (GameManager.getInstance().selectedSkill != null) {
                    if (GameManager.getInstance().selectedSkill == skill && skill.castOnSelf() && GameManager.getInstance().fastUse) {
                        boolean enoughMana = player.getCurrentMana() >= GameManager.getInstance().selectedSkill.getManaCost();
                        boolean enoughActionPoint = player.getCurrentActionPoint() >= GameManager.getInstance().selectedSkill.getActionPointCost();

                        if (enoughMana && enoughActionPoint) {
                            GUIManager.getInstance().eventLogDisplay.addLog("Player use " + GameManager.getInstance().selectedSkill.getName());
                            GameManager.getInstance().selectedSkill.perform(GameManager.getInstance().player);
                        } else {
                            SoundManager.getInstance().playSoundEffect(Config.sfx_failedSound);
                            System.out.println("Not enough mana or action point");
                        }

                        GameManager.getInstance().gameScene.resetSelection(2);
                        return;
                    }
                    GameManager.getInstance().gameScene.resetSelection(2);
                }

                SkillHandler.showValidSkillRange(player.getRow(), player.getCol(), skill);
                GUIManager.getInstance().updateCursor(SceneManager.getInstance().getGameScene(), Config.HandCursor);
                GameManager.getInstance().selectedSkill = skill;
                skill.getFrame().setImage(ImageScaler.resample(new Image(Config.FrameSelectedPath), 2));
                System.out.println("Selected " + skill.getName() + " skill");
            });

            skillFrame.setOnMouseEntered(mouseEvent -> {
                infoOverlay.getView().setVisible(true);
                infoOverlay.getView().toFront();

                // Update overlay info
                infoOverlay.getTitle().setText(skill.getName());
                infoOverlay.getTitle().setTextFill(skill.getNameColor());
                infoOverlay.getDesc().setText(skill.getDescription());

                infoOverlay.getDataContainer().getChildren().clear();
                infoOverlay.newInfo("Mana", Color.DARKBLUE, String.valueOf(skill.getManaCost()));
                infoOverlay.newInfo("Action Point", Color.ORANGE, String.valueOf(skill.getActionPointCost()));

                // Other skill info base on type
                if (skill instanceof Attackable r) {
                    infoOverlay.newInfo("Attack", Color.DARKRED, String.valueOf(r.getAttack()));
                }if (skill instanceof Healable r) {
                    infoOverlay.newInfo("Heal", Color.DARKGREEN, String.valueOf(r.getHeal()));
                }if (skill instanceof ManaRefillable r) {
                    infoOverlay.newInfo("Mana Refill", Color.CYAN, "+" + r.getRefill());
                }if (skill instanceof AttackBuffable r) {
                    infoOverlay.newInfo("Attack Damage", Color.DARKRED, "+" + r.getBuffAttack());
                }if (skill instanceof ActionPointBuffable r) {
                    infoOverlay.newInfo("Max Action Point", Color.ORANGE, "+" + r.getBuffActionPoint());
                }if (skill instanceof HealthBuffable r) {
                    infoOverlay.newInfo("Max Health", Color.DARKGREEN, "+" + r.getBuffHealth());
                }
            });

            skillFrame.setOnMouseExited(mouseEvent -> {
                infoOverlay.getView().setVisible(false);
            });
        }

        return skillFrame;
    }

    // Method to update skill frame into other skill
    public void updateSkillFrame(int index, BaseSkill newSkill) {
        GameManager.getInstance().playerSkills[index] = newSkill;

        updateSkillSelect();
    }

    public InfoOverlay getInfoOverlay() {
        return infoOverlay;
    }
    public void enableFrame() {
        view.setDisable(false);
    }
    public void disableFrame() {
        view.setDisable(true);
    }

    @Override
    public Node getView() {
        return view;
    }
}
