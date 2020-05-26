package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Animal {
    private Texture animalTexture;
    private int RECT_SIZE;
    private int animalXVel, animalYVel;
    private int animalX, animalY;
    private char[][] grid;
    private HashSet<Point> borderPoints;
    private LinkedHashSet<Point> tracePoints;
    private SpriteBatch spriteBatch;
    private boolean caught = false, pause = false;

    /**
     * Empty constructor.
     */
    public Animal() {
    }

    /**
     * Main constructor.
     *
     * @param animalTexture
     * @param RECT_SIZE
     * @param animalXVel
     * @param animalYVel
     */
    public Animal(Texture animalTexture, int RECT_SIZE, int animalXVel, int animalYVel, char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints) {
        this.animalTexture = animalTexture;
        this.RECT_SIZE = RECT_SIZE;
        this.animalXVel = animalXVel;
        this.animalYVel = animalYVel;
        this.grid = grid;
        setRandomLocation();
        this.spriteBatch = spriteBatch;
        this.borderPoints = borderPoints;
        this.tracePoints = tracePoints;
    }

    /**
     * Constructor if you want to specify start position of monster by defining X and Y.
     *
     * @param animalTexture
     * @param RECT_SIZE
     * @param animalXVel
     * @param animalYVel
     * @param animalX
     * @param animalY
     */
    public Animal(Texture animalTexture, int RECT_SIZE, int animalXVel, int animalYVel, int animalX, int animalY, char[][] grid, SpriteBatch spriteBatch, HashSet borderPoints, LinkedHashSet<Point> tracePoints) {
        this.animalTexture = animalTexture;
        this.RECT_SIZE = RECT_SIZE;
        this.animalXVel = animalXVel;
        this.animalYVel = animalYVel;
        this.animalX = animalX;
        this.animalY = animalY;
        this.grid = grid;
        if (grid[animalY / RECT_SIZE][animalX / RECT_SIZE] != '.') setRandomLocation();
        this.spriteBatch = spriteBatch;
        this.borderPoints = borderPoints;
        this.tracePoints = tracePoints;
    }

    /**
     *  Moves animal and draw it using spriteBatch.
     */
    public void moveAndDrawAnimal() {
        if (!pause){
            reflectFromTheBorderIfNeeded();
            if (lineCrossed()) {
                System.out.println("Line crossed. End game.");

            }
            animalX += animalXVel;
            animalY += animalYVel;
        }
        if (!caught){
            if (animalCaught()){
                System.out.println("Animal has been caught.");
                caught = true;
            }
            spriteBatch.begin();
            spriteBatch.draw(animalTexture, animalX, animalY, RECT_SIZE, RECT_SIZE);
            spriteBatch.end();
        }
    }

    public void pauseMove(){
        pause = true;
    }

    public void resumeMove(){
        pause = false;
    }

    public boolean isMovePaused(){
        return pause;
    }

    private void setRandomLocation() {
        do {
            animalX = ThreadLocalRandom.current().nextInt(RECT_SIZE, Gdx.graphics.getWidth() - 2*RECT_SIZE + 1);
            animalY = ThreadLocalRandom.current().nextInt(RECT_SIZE, Gdx.graphics.getHeight() - 2*RECT_SIZE + 1);
        } while (grid[animalY / RECT_SIZE][animalX / RECT_SIZE] != '.');
    }


    private void reflectFromTheBorderIfNeeded(){
        Rectangle animalRectWest = new Rectangle(animalX-animalXVel, animalY, RECT_SIZE, RECT_SIZE);
        Rectangle animalRectNorth = new Rectangle(animalX, animalY+animalYVel, RECT_SIZE, RECT_SIZE);
        Rectangle animalRectEast = new Rectangle(animalX+animalXVel, animalY, RECT_SIZE, RECT_SIZE);
        Rectangle animalRectSouth = new Rectangle(animalX, animalY-animalYVel, RECT_SIZE, RECT_SIZE);
        Iterator<Point> pointIterator = borderPoints.iterator();
        Point point;
        boolean reflectX = false, reflectY = false;
        while (pointIterator.hasNext()){
            point = pointIterator.next();
            Rectangle borderRect = new Rectangle(point.x, point.y, RECT_SIZE, RECT_SIZE);
            if (animalRectWest.overlaps(borderRect) || animalRectEast.overlaps(borderRect)) reflectX=true;
            if (animalRectNorth.overlaps(borderRect) || animalRectSouth.overlaps(borderRect)) reflectY=true;
        }
        if (reflectX) {
            animalXVel*=-1;
        }
        if (reflectY) {
            animalYVel*=-1;
        }
    }

    private boolean lineCrossed(){
        Iterator<Point> iterator = tracePoints.iterator();
        Point point;
        Rectangle traceRect, animalRect;
        animalRect = new Rectangle(animalX, animalY, RECT_SIZE, RECT_SIZE);
        while (iterator.hasNext()) {
            point = iterator.next();
            traceRect = new Rectangle(point.x, point.y, RECT_SIZE, RECT_SIZE);
            if (animalRect.overlaps(traceRect)){
                return true;
            }
        }
        return false;
    }

    private boolean animalCaught(){
        if (grid[animalY/RECT_SIZE][animalX/RECT_SIZE]=='C' || grid[animalY/RECT_SIZE][animalX/RECT_SIZE]=='B') return true;
        else return false;
    }

    public enum TYPES{
        TYPE_ONE(new Texture(Gdx.files.internal("rabbit.gif")), 2, 4), TYPE_TWO(new Texture(Gdx.files.internal("bird.png")), 4, 2),
        TYPE_THREE(new Texture(Gdx.files.internal("rabbit.gif")), 1, 1), TYPE_FOUR(new Texture(Gdx.files.internal("bird.png")), 6, 6),
        TYPE_FIVE(new Texture(Gdx.files.internal("rabbit.gif")), 2, 2);

        private Texture texture;
        private int animalXVel, animalYVel;

        TYPES(Texture texture, int animalXVel, int animalYVel){
            this.texture = texture;
            this.animalXVel = animalXVel;
            this.animalYVel = animalYVel;
        }

        public Texture getTexture() {
            return texture;
        }

        public int getAnimalXVel() {
            return animalXVel;
        }

        public int getAnimalYVel() {
            return animalYVel;
        }
    }
}
