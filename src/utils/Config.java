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
    public static String DefaultCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-pointer-1.png";
    public static String UnavailableCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-idle-2a.png";
    public static String AttackCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-pointer-25.png";
    public static String AimCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-target-10.png";
    public static String QuestionCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-question.png";
    public static String HandCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-pointer-18.png";
    public static String HandClickCursor = "sprites/cursors/Megabyte Games Mouse Cursor Pack/16x16/png/cursor-pointer-18-click.png";

    // Player
    public static String KnightLargePath = "sprites/player/Knight_Large.png";
    public static String KnightAnimationPath = "sprites/player/knight_sprite_sheet.png";
    public static String ArcherAnimationPath = "sprites/player/Archer_sprite_sheet.png";
    public static String WizardAnimationPath = "sprites/player/Wizard_sprite_sheet2.png";

    // Enemies
    public static String DeadEffectPath = "sprites/enemies/dead_smoke.png";
    public static String StunEffectPath = "sprites/enemies/stun_effect.png";

    public static String TinyAnimationPath = "sprites/enemies/tiny_sprite_sheet.png";
    public static String ZombieAnimationPath = "sprites/enemies/zombie_sprite_sheet.png";
    public static String BomberAnimationPath = "sprites/enemies/bomber_sprite_sheet.png";
    public static String BombAnimationPath = "sprites/objects/bomb/bomb_f0.png";
    public static String BombExplosionPath = "sprites/objects/bomb/bombExplosion.png";
    public  static  String NecromancerPath = "sprites/enemies/necromancer_sprite_sheet.png";
    public static String NecromancerSummonPath = "sprites/enemies/necromancer_summon_sprite_sheet.png";
    public  static  String SkeletonPath = "sprites/enemies/skeleton_sprite_sheet.png";
    public  static  String VampirePath = "sprites/enemies/vampire_sprite_sheet.png";

    // NPC
    public static String DealerPortraitPath = "sprites/npc/dealer_portrait.png";
    public static String DealerAnimationPath = "sprites/npc/dealer_sprite_sheet.png";

    // Dungeon
    public static String WallPath = "sprites/ground/wall_1.png";
    public static String WallTileMapPath = "sprites/ground/wall_tile_map.png";
    public static String FloorPath = "sprites/ground/floor_1.png";
    public static String FloorHoverPath = "sprites/ground/floor_hover.png";
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
    // Archer Icons
    public static String HaltPath = "sprites/skills/icons/Archer/Halt.png";
    public static String TargetLockPath = "sprites/skills/icons/Archer/Targetlock.png";
    public static String SnipePath = "sprites/skills/icons/Archer/Snipe.png";
    public static String TeleportPath = "sprites/skills/icons/Archer/Teleport.png";

    // Wizard Icons
    public static String FireballPath = "sprites/skills/icons/Wizard/Fireball.png";
    public static String IceShieldPath = "sprites/skills/icons/Wizard/IceShield.png";
    public static String RainOfFirePath = "sprites/skills/icons/Wizard/RainOfFire.png";
    public static String DragonFirePath = "sprites/skills/icons/Wizard/DragonFire.png";

    // Potion Icons
    public static String BluePotionPath = "sprites/potions/blue_potion.png";
    public static String RedPotionPath = "sprites/potions/red_potion.png";
    public static String PurplePotionPath = "sprites/potions/purple_potion.png";
    public static String GreenPotionPath = "sprites/potions/green_potion.png";
    public static String YellowPotionPath = "sprites/potions/yellow_potion.png";

    // Attacks animation
    public static String meleeAttackPath = "sprites/attacks/common_melee_attack.png";
    // Knight Skills animation
    public static String skillSlashPath = "sprites/skills/skill_knight/skill_slash.png";
    public static String skillStompPath = "sprites/skills/skill_knight/skill_stomp.png";
    public static String skillDartPath = "sprites/skills/skill_knight/skill_dart.png";
    public static String skillHealPath = "sprites/skills/skill_knight/skill_heal.png";

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
        PLAYER, MONSTER, TRAP, NPC, WALL, EMPTY
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
