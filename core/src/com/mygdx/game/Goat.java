package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Goat extends Animal {

    HashMap<Point, Texture> contentPoints;
    private final static int RADIUS_OF_CELLS_EATEN = 2;

    public Goat(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints, HashMap<Point, Texture> contentPoints) {
        super(Animal.TYPES.GOAT.getStringAnimationHashMap(), Animal.TYPES.GOAT.getAnimalXVel(), Animal.TYPES.GOAT.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.contentPoints = contentPoints;
    }


    @Override
    public void moveAndDrawAnimal() {
        if (this.animalCaught()) {
            int animalCurrGridX = 2 * this.getAnimalX() / ANIMAL_SIZE + 1;
            int animalCurrGridY = 2 * this.getAnimalY() / ANIMAL_SIZE + 1;
            for (int y = animalCurrGridY - RADIUS_OF_CELLS_EATEN; y <= animalCurrGridY + RADIUS_OF_CELLS_EATEN; y++) {
                for (int x = animalCurrGridX - RADIUS_OF_CELLS_EATEN; x <= animalCurrGridX + RADIUS_OF_CELLS_EATEN; x++) {
                    if (x < grid[0].length - 1 && x >= 0 && y < grid.length - 1 && y >= 0) {
                        contentPoints.remove(new Point(x * ANIMAL_SIZE / 2, y * ANIMAL_SIZE / 2));
                        grid[x][y] = '.';
                    }
                }
            }
            for (int y = animalCurrGridY - RADIUS_OF_CELLS_EATEN - 1; y <= animalCurrGridY + RADIUS_OF_CELLS_EATEN + 1; y++) {
                if (y < grid.length && y >= 0) {
                    borderPoints.add(new Point((animalCurrGridX - RADIUS_OF_CELLS_EATEN - 1) * ANIMAL_SIZE / 2, y * ANIMAL_SIZE / 2));
                    borderPoints.add(new Point((animalCurrGridX + RADIUS_OF_CELLS_EATEN + 1) * ANIMAL_SIZE / 2, y * ANIMAL_SIZE / 2));
                    grid[animalCurrGridX - 2][y] = 'B';
                    grid[animalCurrGridX + 2][y] = 'B';
                }
            }
            for (int x = animalCurrGridX - RADIUS_OF_CELLS_EATEN - 1; x <= animalCurrGridX + RADIUS_OF_CELLS_EATEN + 1; x++) {
                if (x < grid[0].length && x >= 0) {
                    borderPoints.add(new Point(x * ANIMAL_SIZE / 2, (animalCurrGridY - RADIUS_OF_CELLS_EATEN - 1) * ANIMAL_SIZE / 2));
                    borderPoints.add(new Point(x * ANIMAL_SIZE / 2, (animalCurrGridY + RADIUS_OF_CELLS_EATEN + 1) * ANIMAL_SIZE / 2));
                    grid[x][animalCurrGridY - 2] = 'B';
                    grid[x][animalCurrGridY + 2] = 'B';
                }
            }
        } else {
            super.moveAndDrawAnimal();
        }
    }

}