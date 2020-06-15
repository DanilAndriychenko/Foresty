package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Dog extends Animal {

    public Dog(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints) {
        super(Animal.TYPES.DOG.getStringAnimationHashMap(), Animal.TYPES.DOG.getAnimalXVel(), Animal.TYPES.DOG.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
        caughtMusic = Gdx.audio.newMusic(Gdx.files.internal("AnimalSounds\\dog.mp3"));
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
