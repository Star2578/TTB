package utils;

public class Config {
    public static String PlaceholderPath = "sprites/placeholder.png";

    // Game
    public static final int BOARD_SIZE = 20;
    public static final int SQUARE_SIZE = 32;
    public static final int GAME_SIZE = 640;

    public static final int MOVE_ACTIONPOINT = 1;

    // Cursor
    public static final String DefaultCursor = "sprites/cursors/png/cursor_default.png";
    public static final String UnavailableCursor = "";
    public static final String AttackCursor = "sprites/cursors/png/cursor_select.png";
    public static final String AimCursor = "";
    public static final String QuestionCursor = "";
    public static final String HandCursor = "sprites/cursors/png/cursor_select.png";
    public static final String HandClickCursor = "sprites/cursors/png/cursor_select_tap.png";

    // Player
    public static String KnightPath = "sprites/player/Knight_Small.png";
    public static String KnightLargePath = "sprites/player/Knight_Large.png";
    public static String knightIdlePath = "sprites/player/knight_idle.png";

    // Enemies
    public static String ZombiePath = "sprites/enemies/zombie.png";

    // Dungeon
    public static String WallPath = "sprites/ground/wall_1.png";
    public static String FloorPath = "sprites/ground/floor_1.png";

    public static String ValidMovePath = "sprites/ground/selected_floor_1.png";
    public static String ValidAttackPath = "sprites/ground/attack_floor_1.png";

    //animation
    public enum state{
        IDLE,
        MOVE,
        ATTACK
    }

}
