package logic;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pieces.BasePiece;
import pieces.enemies.*;
import utils.Config;
import utils.ImageScaler;

import java.util.List;
import java.util.Random;

public class SpawnerManager {
    public static SpawnerManager instance;
    private GameManager gameManager = GameManager.getInstance();
    private ImageView[][] dungeonFloor;
    private double doorProbability = 5.0; // probability that the door to next dungeon floor would spawn
    public int monsterCount;
    public int freeSquareCount;
    public BaseMonsterPiece[] monsterPool; // monster pool for area 1
    public BaseMonsterPiece[] bossPool; // Boss monster pool for Boss area

    public SpawnerManager() {
        initialize();
        monsterPool = new BaseMonsterPiece[]{
                new Bomber(), new Tiny(), new Zombie(), new Skeleton(), new Vampire(), new Necromancer()
        };
        bossPool = new BaseMonsterPiece[]{
            new SlimeBoss()
        };
    }

    public void initialize() {
        dungeonFloor = gameManager.dungeonFloor;
        monsterCount = 0;
        freeSquareCount = 0;
        doorProbability = 5.0;
    }

    public static SpawnerManager getInstance() {
        if (instance == null) {
            instance = new SpawnerManager();
        }
        return instance;
    }

    public void trySpawnDoor(int row, int col) {
        Random random = new Random();
        double spawnChance = random.nextDouble() * 100; // Random number between 0 and 100

        if (spawnChance <= doorProbability || monsterCount == 0) {
            spawnDoor(row, col);
        } else {
            increaseDoorChance();
        }
    }

    public void spawnDoor(int row, int col) {
        // decrease doorProbability to 0
        doorProbability = 0;

        // Door spawn successful
        dungeonFloor[row][col].setImage(ImageScaler.resample(new Image(Config.DoorPath), 2));
        gameManager.doorAt.add(new Point2D(row, col));
    }

    public void increaseDoorChance() {
        // Increase the door spawn probability by a certain amount
        doorProbability += 2.0; // Increase by 2%
        System.out.println("Door spawn probability increased to " + doorProbability + "%");
    }

    public void randomMonsterSpawnFromPool(BaseMonsterPiece[] pool, List<BasePiece> toAdd) {
        Random random = new Random();

        int calculateMonster = freeSquareCount / 12;
        if (GameManager.getInstance().moreMonster) calculateMonster = freeSquareCount / 9; // more monster

        System.out.println("Free square = " + freeSquareCount);

        // Add monsters from the pool to toAdd list
        for (int i = 0; i < calculateMonster; i++) {
            int index = random.nextInt(pool.length);
            BaseMonsterPiece newMonster = null;
            try {
                newMonster = (BaseMonsterPiece) pool[index].getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                System.out.println("Failed to create new instance of monster : " + e.getMessage());
            }
            if (newMonster != null) {
                toAdd.add(newMonster);
            }
        }
        monsterCount += calculateMonster;
    }
}
