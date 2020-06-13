package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Rabbit extends Animal{


    public Rabbit(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints){
        super(AnimalsTypes.RABBIT.getStringAnimationHashMap(), AnimalsTypes.RABBIT.getAnimalXVel(), AnimalsTypes.RABBIT.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
    }

}
