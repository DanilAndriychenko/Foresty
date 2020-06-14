package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Goat extends Animal {

    HashMap<Point, Texture> contentPoints;

    public Goat(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints, HashMap<Point, Texture> contentPoints) {
        super(Animal.TYPES.GOAT.getStringAnimationHashMap(), Animal.TYPES.GOAT.getAnimalXVel(), Animal.TYPES.GOAT.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.contentPoints = contentPoints;
    }


    @Override
    public void moveAndDrawAnimal() {
        if (this.animalCaught()) {
            int animalCurrGridX = this.getAnimalX() / ANIMAL_SIZE + 1;
            int animalCurrGridY = this.getAnimalY() / ANIMAL_SIZE + 1;
            for (int y = animalCurrGridY - 1; y < animalCurrGridY + 1; y++) {
                for (int x = animalCurrGridX - 1; x < animalCurrGridX + 1; x++) {
                    if (x < grid[0].length && x >= 0 && y < grid.length && y >= 0) {
                        contentPoints.remove(new Point(x * ANIMAL_SIZE / 2, y * ANIMAL_SIZE / 2));
                        grid[x][y] = '.';
                    }
                }
            }
            for (int y = animalCurrGridY - 2; y < animalCurrGridY + 2; y++) {
                if (y < grid.length && y >= 0) {
                    borderPoints.add(new Point((animalCurrGridX - 2) * ANIMAL_SIZE / 2, y * ANIMAL_SIZE / 2));
                    borderPoints.add(new Point((animalCurrGridX + 2) * ANIMAL_SIZE / 2, y * ANIMAL_SIZE / 2));
                    grid[animalCurrGridX - 2][y] = 'B';
                    grid[animalCurrGridX + 2][y] = 'B';
                }
            }
            for (int x = animalCurrGridX - 2; x < animalCurrGridX + 2; x++) {
                if (x < grid[0].length && x >= 0) {
                    borderPoints.add(new Point(x * ANIMAL_SIZE / 2, (animalCurrGridY - 2) * ANIMAL_SIZE / 2));
                    borderPoints.add(new Point(x * ANIMAL_SIZE / 2, (animalCurrGridY + 2) * ANIMAL_SIZE / 2));
                    grid[x][animalCurrGridY - 2] = 'B';
                    grid[x][animalCurrGridY + 2] = 'B';
                }
            }
        } else {
            super.moveAndDrawAnimal();
        }
    }

}