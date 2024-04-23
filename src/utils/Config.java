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
    public static String ArcherAnimationPath = "sprites/player/Archer_sprite_sheet.png";
    public static String WizardAnimationPath = "sprites/player/Wizard_sprite_sheet2.png";

    // Enemies
    public static String DeadEffectPath = "sprites/enemies/dead_smoke.png";

    public static String TinyAnimationPath = "sprites/enemies/tiny_sprite_sheet.png";
    public static String ZombieAnimationPath = "sprites/enemies/zombie_sprite_sheet.png";
    public static String BomberAnimationPath = "sprites/enemies/bomber_sprite_sheet.png";
    public static String BombAnimationPath = "sprites/objects/bomb/bomb_f0.png";
    public  static  String NecromancerPath = "sprites/enemies/necromancer_sprite_sheet.png";
    public  static  String SkeletonPath = "sprites/enemies/skeleton_sprite_sheet.png";
    public  static  String VampirePath = "sprites/enemies/vampire_sprite_sheet.png";

    // NPC
    public static String DealerPortraitPath = "sprites/npc/dealer_portrait.png";
    public static String DealerAnimationPath = "sprites/npc/dealer_sprite_sheet.png";

    // Dungeon
    public static String WallPath = "sprites/ground/wall_1.png";
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
    // Knight Icons
    public static String SlashPath = "sprites/skills/icons/Knight/slash.png";
    public static String HealPath = "sprites/skills/icons/Knight/heal.png";
    public static String DartPath = "sprites/skills/icons/Knight/Dart.png";
    public static String StompPath = "sprites/skills/icons/Knight/Stomp.png";

    // Wizard Icons
    public static String FireballPath = "sprites/skills/icons/Wizard/Fireball.png";
    public static String IceShieldPath = "sprites/skills/icons/Wizard/IceShield.png";
    public static String RainOfFirePath = "sprites/skills/icons/Wizard/RainOfFire.png";
    public static String DragonFirePath = "sprites/skills/icons/Wizard/DragonFire.png";

    // Potion Icons
    public static String BluePotionPath = "sprites/potions/blue_potion.png";

    // Attacks animation
    public static String meleeAttackPath = "sprites/attacks/common_melee_attack.png";
    public static String skillSlashPath = "sprites/attacks/skill_slash.png";

    // BGM
    public static String bgm_8_bit_adventure = "res/BGM/8_Bit_Adventure.wav";
    public static String bgm_8_bit_nostalgia = "res/BGM/8_Bit_Nostalgia.wav";

    // SFX
    public static String sfx_attackSound = "res/SFX/attack/Swipe_2_converted.wav";
    public static String sfx_moveSound = "res/SFX/walk/jump2.wav";
    public static String sfx_hurtSound = "res/SFX/hurt/hit_003.wav";
    public static String sfx_gameOverSound = "res/SFX/gameover/Game Over.wav";
    public static String sfx_buttonSound = "res/SFX/click/click.wav";
    public static String sfx_failedSound = "res/SFX/failed/8bit-blip4.wav";
    public static String sfx_deadSound = "res/SFX/monster/dead/explosion_03.wav";

    public enum ENTITY_TYPE {
        PLAYER, MONSTER, TRAP, NPC, WALL
    }

    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    private static final char[][] customRoom = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };

    public static final char[][] safeRoom = {
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'},
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}
    };
}
