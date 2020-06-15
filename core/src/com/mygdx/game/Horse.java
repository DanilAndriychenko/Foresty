package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Horse extends Animal{

    private GameScreen gameScreen;

    public Horse(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints, GameScreen gameScreen){
        super(Animal.TYPES.HORSE.getStringAnimationHashMap() ,Animal.TYPES.HORSE.getAnimalXVel(), Animal.TYPES.HORSE.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        this.gameScreen = gameScreen;
        caughtMusic = Gdx.audio.newMusic(Gdx.files.internal("AnimalSounds\\horse.mp3"));
    }

    @Override
    public void moveAndDrawAnimal() {
        if(this.animalCaught()){
            gameScreen.slowDownAnimals();
        }
        {
            super.moveAndDrawAnimal();
        }
    }
}