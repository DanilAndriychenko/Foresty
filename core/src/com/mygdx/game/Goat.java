package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Goat extends Animal {

    public Goat(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints) {
        super(AnimalsTypes.GOAT.getStringAnimationHashMap(), AnimalsTypes.GOAT.getAnimalXVel(), AnimalsTypes.GOAT.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
    }


    @Override
    public void moveAndDrawAnimal() {
        //TODO make goat destroy field after being caught
        /*if (this.animalCaught()) {
            int animalCurrGridX = (int) this.getAnimalX() / RECT_SIZE + 1;
            int animalCurrGridY = (int) this.getAnimalY() / RECT_SIZE + 1;
            for (int y = animalCurrGridY - 1; y < animalCurrGridY + 1; y++) {
                for (int x = animalCurrGridX - 1; x < animalCurrGridX + 1; x++) {
                    if (x < grid[0].length && x >= 0 && y < grid.length && y >= 0) {
                        grid[x][y] = '.';
                    }
                }
            }
            for (int y = animalCurrGridY - 2; y < animalCurrGridY + 2; y++) {
                if (y < grid.length && y >= 0) {
                    grid[animalCurrGridX - 2][y] = 'B';
                    grid[animalCurrGridX + 2][y] = 'B';
                }
            }
            for (int x = animalCurrGridX - 2; x < animalCurrGridX + 2; x++) {
                if (x < grid[0].length && x >= 0) {
                    grid[x][animalCurrGridY - 2] = 'B';
                    grid[x][animalCurrGridY + 2] = 'B';
                }
            }
        }*/
        {
            super.moveAndDrawAnimal();
        }
    }

}
