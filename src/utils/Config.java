package utils;

public class Config {
    public static String PlaceholderPath = "sprites/placeholder.png";
    public static String FramePath = "sprites/frame.png";
    public static String FrameSelectedPath = "sprites/frame_selected.png";

    // Game
    public static final int BOARD_SIZE = 20;
    public static final int SQUARE_SIZE = 32;
    public static final int GAME_SIZE = 640;

    public static final int MOVE_ACTIONPOINT = 1;

    // Cursor
    public static String DefaultCursor = "sprites/cursors/png/cursor_default.png";
    public static String UnavailableCursor = "";
    public static String AttackCursor = "sprites/cursors/png/cursor_select.png";
    public static String AimCursor = "";
    public static String QuestionCursor = "";
    public static String HandCursor = "sprites/cursors/png/cursor_select.png";
    public static String HandClickCursor = "sprites/cursors/png/cursor_select_tap.png";

    // Player
    public static String KnightLargePath = "sprites/player/Knight_Large.png";
    public static String KnightAnimationPath = "sprites/player/knight_sprite_sheet.png";

    // Enemies
    public static String TinyAnimationPath = "sprites/enemies/tiny_sprite_sheet.png";
    public static String ZombieAnimationPath = "sprites/enemies/zombie_sprite_sheet.png";
    public static String BomberAnimationPath = "sprites/enemies/bomber_sprite_sheet.png";
    public static String BombAnimationPath = "sprites/objects/bomb/bomb_f0.png";

    // NPC
    public static String DealerAnimationPath = "sprites/npc/dealer_sprite_sheet.png";

    // Dungeon
    public static String WallPath = "sprites/ground/wall_1.png";
    public static String WallOnFloorPath = "sprites/ground/wall_onfloor_tile_map.png";
    public static String WallTileMapPath = "sprites/ground/wall_tile_map.png";
    public static String FloorPath = "sprites/ground/floor_1.png";
    public static String DoorPath = "sprites/ground/floor_2.png";

    public static String ValidMovePath = "sprites/ground/selected_floor_1.png";
    public static String ValidAttackPath = "sprites/ground/attack_floor_1.png";
    public static String ValidSkillPath = "sprites/ground/skill_floor_1.png";
    public static String ValidItemPath = "sprites/ground/item_floor_1.png";

    // Skill Icons
    public static String LockedSkillIconPath = "sprites/skills/icons/skill_placeholder_locked.png";
    public static String UnlockedSkillIconPath = "sprites/skills/icons/skill_placeholder_unlocked.png";
    public static String SlashPath = "sprites/skills/icons/slash.png";
    public static String HealPath = "sprites/skills/icons/heal.png";

    // Potion Icons
    public static String BluePotionPath = "sprites/potions/blue_potion.png";

    // Attacks animation
    public static String meleeAttackPath = "sprites/attacks/testLocation.png";

    public enum ENTITY_TYPE {
        PLAYER,
        MONSTER,
        TRAP,
        NPC,
        WALL
    }
}
