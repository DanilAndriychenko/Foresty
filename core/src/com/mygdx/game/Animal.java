package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Animal {
    private static final float FRAME_DURATION = 0.025f;
    protected static int RECT_SIZE = 32;
    private static HashMap<String, Animation<TextureRegion>> rabbitAnimationsHashMap, horseAnimationsHashMap,
            sheepAnimationsHashMap, goatAnimationsHashMap, goatBabyAnimationsHashMap;

    static {
        rabbitAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Dog Blonde.png",
                32, 32, 4, 4, 6, false);
        horseAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Horse.png",
                32, 32, 7, 4, 3, true);
        sheepAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Sheep.png",
                32, 32, 4, 4, 4, true);
        goatAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Goat.png",
                32, 32, 4, 4, 4, true);
        goatBabyAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Goat Baby.png",
                32, 32, 4, 4, 4, true);
    }

    private HashMap<String, Animation<TextureRegion>> stringAnimationHashMap;
    private int animalXVel, animalYVel;
    private int animalX, animalY;
    protected char[][] grid;
    private HashSet<Point> borderPoints;
    private LinkedHashSet<Point> tracePoints;
    private SpriteBatch spriteBatch;
    private boolean caught = false, pause = false;
    private float elapsedTime = 0f;

    /**
     * Empty constructor.
     */
    public Animal() {
    }

    /**
     * Main constructor.
     *
     * @param stringAnimationHashMap
     * @param animalXVel
     * @param animalYVel
     */
    public Animal(HashMap<String, Animation<TextureRegion>> stringAnimationHashMap, int animalXVel, int animalYVel, char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints) {
        this.stringAnimationHashMap = stringAnimationHashMap;
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
     * @param stringAnimationHashMap
     * @param animalXVel
     * @param animalYVel
     * @param animalX
     * @param animalY
     */
    public Animal(HashMap<String, Animation<TextureRegion>> stringAnimationHashMap, int animalXVel, int animalYVel, int animalX, int animalY, char[][] grid, SpriteBatch spriteBatch, HashSet borderPoints, LinkedHashSet<Point> tracePoints) {
        this.stringAnimationHashMap = stringAnimationHashMap;
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

    private static HashMap<String, Animation<TextureRegion>> fillAnimationHashMap(String texturePath, int tileWidth, int tileHeight, int spritesInMoveRow,
                                                                                  int spritesInStandRow, int rowNumStandSprite, boolean secRowAsFourth) {
        HashMap<String, Animation<TextureRegion>> stringAnimationHashMap = new HashMap<>();
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] textureRegion = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] south = new TextureRegion[spritesInMoveRow];
        TextureRegion[] east = new TextureRegion[spritesInMoveRow];
        TextureRegion[] north = new TextureRegion[spritesInMoveRow];
        TextureRegion[] west = new TextureRegion[spritesInMoveRow];
        TextureRegion[] stand = new TextureRegion[spritesInStandRow];
        for (int i = 0; i < spritesInMoveRow; i++) {
            if (secRowAsFourth && i == 3) {
                for (int j = 0; j < spritesInMoveRow; j++) west[j] = new TextureRegion(east[j]);
            }
            for (int j = 0; j < spritesInMoveRow; j++) {
                if (i == 0) south[j] = textureRegion[i][j];
                else if (i == 1) east[j] = textureRegion[i][j];
                else if (i == 2) north[j] = textureRegion[i][j];
                else if (i == 3) {
                    if (secRowAsFourth) west[j].flip(true, false);
                    else west[j] = textureRegion[i][j];
                }
            }
        }
        for (int j = 0; j < spritesInStandRow; j++) stand[j] = textureRegion[rowNumStandSprite][j];
        //TODO: reduce frame animation time.
        Animation moveSouth = new Animation<>(FRAME_DURATION, south);
        Animation moveEast = new Animation<>(FRAME_DURATION, east);
        Animation moveNorth = new Animation<>(FRAME_DURATION, north);
        Animation moveWest = new Animation<>(FRAME_DURATION, west);
        Animation standAnimation = new Animation<>(FRAME_DURATION, stand);
        stringAnimationHashMap.put("south", moveSouth);
        stringAnimationHashMap.put("east", moveEast);
        stringAnimationHashMap.put("north", moveNorth);
        stringAnimationHashMap.put("west", moveWest);
        stringAnimationHashMap.put("stand", standAnimation);
        return stringAnimationHashMap;
    }

    public int getAnimalX() {
        return animalX;
    }

    public void setAnimalX(int animalX) {
        this.animalX = animalX;
    }

    public int getAnimalY() {
        return animalY;
    }

    public void setAnimalY(int animalY) {
        this.animalY = animalY;
    }

    /**
     * Moves animal and draw it using spriteBatch.
     */
    public void moveAndDrawAnimal() {
        if (!pause) {
            reflectFromTheBorderIfNeeded();
            if (crossesLine()) {
                System.out.println("Line crossed. End game.");

            }
            animalX += animalXVel;
            animalY += animalYVel;
        }
        if (!caught) {
            if (animalCaught()) {
                System.out.println("Animal has been caught.");
                caught = true;
            }
            elapsedTime += Gdx.graphics.getDeltaTime();
            spriteBatch.begin();
            if (animalXVel > 0 && animalYVel > 0)
                spriteBatch.draw(stringAnimationHashMap.get("north").getKeyFrame(elapsedTime, true), animalX, animalY, RECT_SIZE, RECT_SIZE);
            else if (animalXVel > 0 && animalYVel < 0)
                spriteBatch.draw(stringAnimationHashMap.get("east").getKeyFrame(elapsedTime, true), animalX, animalY, RECT_SIZE, RECT_SIZE);
            else if (animalXVel < 0 && animalYVel > 0)
                spriteBatch.draw(stringAnimationHashMap.get("west").getKeyFrame(elapsedTime, true), animalX, animalY, RECT_SIZE, RECT_SIZE);
            else if (animalXVel < 0 && animalYVel < 0)
                spriteBatch.draw(stringAnimationHashMap.get("south").getKeyFrame(elapsedTime, true), animalX, animalY, RECT_SIZE, RECT_SIZE);
            spriteBatch.end();
        } else{
            spriteBatch.begin();
            spriteBatch.draw(stringAnimationHashMap.get("stand").getKeyFrame(elapsedTime, true), animalX, animalY, RECT_SIZE, RECT_SIZE);
            spriteBatch.end();
        }
    }

    public void setAnimalXVel(int animalXVel) {
        this.animalXVel = animalXVel;
    }

    public void setAnimalYVel(int animalYVel) {
        this.animalYVel = animalYVel;
    }

    public void pauseMove() {
        pause = true;
    }

    public void resumeMove() {
        pause = false;
    }

    public boolean isMovePaused() {
        return pause;
    }

    private void setRandomLocation() {
        do {
            animalX = ThreadLocalRandom.current().nextInt(RECT_SIZE, Gdx.graphics.getWidth() - 2 * RECT_SIZE + 1);
            animalY = ThreadLocalRandom.current().nextInt(RECT_SIZE, Gdx.graphics.getHeight() - 2 * RECT_SIZE + 1);
        } while (grid[animalY / RECT_SIZE][animalX / RECT_SIZE] != '.');
    }

    private void reflectFromTheBorderIfNeeded() {
        Rectangle animalRectWest = new Rectangle(animalX - animalXVel, animalY, RECT_SIZE, RECT_SIZE);
        Rectangle animalRectNorth = new Rectangle(animalX, animalY + animalYVel, RECT_SIZE, RECT_SIZE);
        Rectangle animalRectEast = new Rectangle(animalX + animalXVel, animalY, RECT_SIZE, RECT_SIZE);
        Rectangle animalRectSouth = new Rectangle(animalX, animalY - animalYVel, RECT_SIZE, RECT_SIZE);
        Iterator<Point> pointIterator = borderPoints.iterator();
        Point point;
        boolean reflectX = false, reflectY = false;
        while (pointIterator.hasNext()) {
            point = pointIterator.next();
            Rectangle borderRect = new Rectangle(point.x, point.y, RECT_SIZE, RECT_SIZE);
            if (animalRectWest.overlaps(borderRect) || animalRectEast.overlaps(borderRect)) reflectX = true;
            if (animalRectNorth.overlaps(borderRect) || animalRectSouth.overlaps(borderRect)) reflectY = true;
        }
        if (reflectX) {
            animalXVel *= -1;
        }
        if (reflectY) {
            animalYVel *= -1;
        }
    }

    public boolean crossesLine() {
        Iterator<Point> iterator = tracePoints.iterator();
        Point point;
        Rectangle traceRect, animalRect;
        animalRect = new Rectangle(animalX, animalY, RECT_SIZE, RECT_SIZE);
        while (iterator.hasNext()) {
            point = iterator.next();
            traceRect = new Rectangle(point.x, point.y, RECT_SIZE, RECT_SIZE);
            if (animalRect.overlaps(traceRect)) {
                return true;
            }
        }
        return false;
    }

    protected boolean animalCaught() {
        if (grid[animalY / RECT_SIZE * 2][animalX / RECT_SIZE * 2] == 'C' || grid[animalY / RECT_SIZE * 2][animalX / RECT_SIZE * 2] == 'B')
            return true;
        else return false;
    }

    public int getAnimalXVel() {
        return animalXVel;
    }

    public int getAnimalYVel() {
        return animalYVel;
    }

    public HashMap<String, Animation<TextureRegion>> getStringAnimationHashMap() {
        return stringAnimationHashMap;
    }

    /*public enum TYPES {
        RABBIT(rabbitAnimationsHashMap, 2, 2),
        HORSE(horseAnimationsHashMap, 2, 2),
        SHEEP(sheepAnimationsHashMap, 2, 2),
        GOAT(goatAnimationsHashMap, 2, 2),
        GOAT_BABY(goatBabyAnimationsHashMap, 2, 2);

        private HashMap<String, Animation<TextureRegion>> stringAnimationHashMap;
        private int animalXVel, animalYVel;

        TYPES(HashMap<String, Animation<TextureRegion>> stringAnimationHashMap, int animalXVel, int animalYVel) {
            this.stringAnimationHashMap = stringAnimationHashMap;
            this.animalXVel = animalXVel;
            this.animalYVel = animalYVel;
        }

        public HashMap<String, Animation<TextureRegion>> getStringAnimationHashMap() {
            return stringAnimationHashMap;
        }

        public int getAnimalXVel() {
            return animalXVel;
        }

        public int getAnimalYVel() {
            return animalYVel;
        }
    }*/
}
