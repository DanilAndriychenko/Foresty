package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Dog extends Animal{

    private GameScreen gameScreen;

    public Dog(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints, GameScreen gameScreen){
        super(Animal.TYPES.HORSE.getStringAnimationHashMap() ,Animal.TYPES.HORSE.getAnimalXVel(), Animal.TYPES.HORSE.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.gameScreen = gameScreen;
    }

    int num = 0;

    @Override
    public void moveAndDrawAnimal() {
        System.out.println("Change");
        if(true){
            do {
                animalX = ThreadLocalRandom.current().nextInt(RECT_SIZE, Gdx.graphics.getWidth() - 2 * RECT_SIZE + 1);
                animalY = ThreadLocalRandom.current().nextInt(RECT_SIZE, Gdx.graphics.getHeight() - 2 * RECT_SIZE + 1);
            } while (grid[animalY / RECT_SIZE][animalX / RECT_SIZE] != '.');
        }
        else{
            super.moveAndDrawAnimal();
        }//num++;
    }
}
