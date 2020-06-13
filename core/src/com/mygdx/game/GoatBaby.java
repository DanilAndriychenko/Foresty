package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class GoatBaby extends Animal{

    private GameScreen gameScreen;
    private ArrayList<Animal> uncaughtAnimals;

    public GoatBaby(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints, GameScreen gameScreen){
        super(AnimalsTypes.GOAT_BABY.getStringAnimationHashMap(), AnimalsTypes.GOAT_BABY.getAnimalXVel(), AnimalsTypes.GOAT_BABY.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.gameScreen = gameScreen;
        uncaughtAnimals = new ArrayList<>();
    }

    @Override
    public void moveAndDrawAnimal() {
        if(this.animalCaught()){
            for(Animal animal : uncaughtAnimals){
                if(animal.animalCaught())
                    return;
            }
            gameScreen.setLose(true);
        }
        {
            super.moveAndDrawAnimal();
        }
        for(Animal animal : gameScreen.animals){
            if(!animal.animalCaught()){
                uncaughtAnimals.add(animal);
            }
        }
    }

}
