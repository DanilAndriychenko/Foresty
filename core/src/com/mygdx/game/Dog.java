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
        super(Animal.TYPES.DOG.getStringAnimationHashMap() ,Animal.TYPES.DOG.getAnimalXVel(), Animal.TYPES.DOG.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.gameScreen = gameScreen;
    }


    @Override
    public void moveAndDrawAnimal() {
        if (animalCaught()) {
            this.setRandomLocation();
        } else {
            super.moveAndDrawAnimal();
        }
    }
}
