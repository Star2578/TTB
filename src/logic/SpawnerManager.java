package logic;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pieces.BasePiece;
import pieces.enemies.BaseMonsterPiece;
import pieces.enemies.Bomber;
import pieces.enemies.Tiny;
import pieces.enemies.Zombie;
import utils.Config;

import java.util.List;
import java.util.Random;

public class SpawnerManager {
    public static SpawnerManager instance;
    private GameManager gameManager = GameManager.getInstance();
    private ImageView[][] dungeonFloor = gameManager.dungeonFloor;
    private ImageScaler imageScaler = new ImageScaler();
    private double doorProbability = 5.0; // probability that the door to next dungeon floor would spawn
    public int monsterCount = 0;
    public int freeSquareCount = 0;
    public BaseMonsterPiece[] monsterPool_1; // monster pool for area 1

    public SpawnerManager() {
        monsterPool_1 = new BaseMonsterPiece[]{
                new Bomber(), new Tiny(), new Zombie()
        };
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
            // Door spawn successful
            System.out.println("Door spawned at row " + row + ", col " + col);
            dungeonFloor[row][col].setImage(imageScaler.resample(new Image(Config.DoorPath), 2));
            gameManager.doorAt = new Point2D(row, col);
        } else {
            System.out.println("Door spawn failed at row " + row + ", col " + col);
            increaseDoorChance();
        }
    }

    public void increaseDoorChance() {
        // Increase the door spawn probability by a certain amount
        doorProbability += 2.0; // Increase by 2%
        System.out.println("Door spawn probability increased to " + doorProbability + "%");
    }

    public void randomMonsterSpawnFromPool(BaseMonsterPiece[] pool, List<BasePiece> toAdd) {
        Random random = new Random();

        int calculateMonster = freeSquareCount / 12;
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
