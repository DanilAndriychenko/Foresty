package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Sheep extends Animal{

    public Sheep(char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints){
        super(Animal.TYPES.SHEEP.getStringAnimationHashMap() ,Animal.TYPES.SHEEP.getAnimalXVel(), Animal.TYPES.SHEEP.getAnimalYVel(), grid, spriteBatch, borderPoints, tracePoints);
    }

}
