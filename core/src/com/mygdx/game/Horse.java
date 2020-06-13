package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Horse extends Animal{

    private GameScreen gameScreen;

    public Horse(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints, GameScreen gameScreen){
        super(AnimalsTypes.HORSE.getStringAnimationHashMap() ,AnimalsTypes.HORSE.getAnimalXVel(), AnimalsTypes.HORSE.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.gameScreen = gameScreen;
    }

    @Override
    public void moveAndDrawAnimal() {
        if(this.animalCaught()){
            gameScreen.slowDownAnimals(System.currentTimeMillis());
        }
        {
            super.moveAndDrawAnimal();
        }
    }
}
