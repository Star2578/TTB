package logic;

import java.util.*;

public class DungeonGenerator {
    private static final int BOARD_SIZE = 20;
    private static final int ROOM_MIN_SIZE = 3;
    private static final int ROOM_MAX_SIZE = 5;
    private static final int MIN_ROOM_DISTANCE = 4; // Minimum distance between room centers

    private char[][] dungeon;

    public DungeonGenerator() {
        dungeon = new char[BOARD_SIZE][BOARD_SIZE];
        generateDungeon();
    }

    public void generateDungeon() {
        // Initialize the dungeon with walls
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                dungeon[i][j] = '#'; // '#' represents walls
            }
        }

        // Generate rooms
        List<Room> rooms = new ArrayList<>();
        Random random = new Random();
        Random randomNumberOfRooms = new Random();
        int numberOfRooms = randomNumberOfRooms.nextInt(5, 8);
        int attempt = 1000;
        int roomPassed = 0;
        for (int i = 0; i < attempt; i++) { // Generate 5 - 8 rooms
            int roomWidth = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE + 1) + ROOM_MIN_SIZE;
            int roomHeight = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE + 1) + ROOM_MIN_SIZE;
            int startX = random.nextInt(BOARD_SIZE - roomWidth - 1) + 1;
            int startY = random.nextInt(BOARD_SIZE - roomHeight - 1) + 1;
            Room room = new Room(startX, startY, roomWidth, roomHeight);
            if (isValidRoomPlacement(room, rooms)) {
                rooms.add(room);
                roomPassed++;
                // Carve out the room
                for (int x = startX; x < startX + roomWidth; x++) {
                    for (int y = startY; y < startY + roomHeight; y++) {
                        dungeon[x][y] = '.';
                    }
                }
            }
            if (roomPassed == numberOfRooms) break;
        }

        // Connect rooms using corridors (Recursive Backtracking)
        connectRooms(rooms.get(0), rooms, new HashSet<>());

        // Print the generated dungeon
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(dungeon[i][j] + " ");
            }
            System.out.println();
        }
    }

    private boolean isValidRoomPlacement(Room newRoom, List<Room> existingRooms) {
        for (Room room : existingRooms) {
            int distanceSquared = (room.centerX() - newRoom.centerX()) * (room.centerX() - newRoom.centerX()) +
                    (room.centerY() - newRoom.centerY()) * (room.centerY() - newRoom.centerY());
            int minDistanceSquared = MIN_ROOM_DISTANCE * MIN_ROOM_DISTANCE;
            if (distanceSquared < minDistanceSquared) {
                return false; // Rooms are too close
            }
        }
        return true;
    }

    private void connectRooms(Room currentRoom, List<Room> rooms, Set<Room> visited) {
        visited.add(currentRoom);
        Collections.shuffle(rooms); // Shuffle the rooms for randomness
        for (Room nextRoom : rooms) {
            if (!visited.contains(nextRoom)) {
                // Connect current room to the next room
                int startX = currentRoom.centerX();
                int startY = currentRoom.centerY();
                int endX = nextRoom.centerX();
                int endY = nextRoom.centerY();
                int x = startX;
                int y = startY;
                while (x != endX || y != endY) {
                    dungeon[x][y] = '.';
                    if (x < endX) x++;
                    else if (x > endX) x--;
                    else if (y < endY) y++;
                    else if (y > endY) y--;
                }
                connectRooms(nextRoom, rooms, visited); // Recursive call to connect next room
            }
        }
    }

    // Represents a room in the dungeon
    private static class Room {
        int startX, startY, width, height;

        public Room(int startX, int startY, int width, int height) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
        }

        public int centerX() {
            return startX + width / 2;
        }

        public int centerY() {
            return startY + height / 2;
        }
    }

    public char[][] getDungeonLayout() {
        return dungeon;
    }
}
