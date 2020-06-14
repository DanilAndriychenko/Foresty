package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Animal {
    private static final float FRAME_DURATION_MOVE = 0.025f, FRAME_DURATION_STAND = 0.5f;
    protected static int ANIMAL_SIZE = 32,
            RECT_SIZE = 16;
    private static HashMap<String, Animation<TextureRegion>> rabbitAnimationsHashMap, horseAnimationsHashMap,
            sheepAnimationsHashMap, goatAnimationsHashMap, goatBabyAnimationsHashMap;

    static {
        rabbitAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Dog Blonde.png",
                ANIMAL_SIZE, ANIMAL_SIZE, 4, 4, 6, false);
        horseAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Horse.png",
                ANIMAL_SIZE, ANIMAL_SIZE, 7, 4, 3, true);
        sheepAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Sheep.png",
                ANIMAL_SIZE, ANIMAL_SIZE, 4, 4, 4, true);
        goatAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Goat.png",
                ANIMAL_SIZE, ANIMAL_SIZE, 4, 4, 4, true);
        goatBabyAnimationsHashMap = fillAnimationHashMap("PC Computer - Stardew Valley - Goat Baby.png",
                ANIMAL_SIZE, ANIMAL_SIZE, 4, 4, 4, true);
    }

    protected HashMap<String, Animation<TextureRegion>> stringAnimationHashMap;
    protected double animalXVel, animalYVel;
    protected double animalX, animalY;
    protected char[][] grid;
    protected HashSet<Point> borderPoints;
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
    public Animal(HashMap<String, Animation<TextureRegion>> stringAnimationHashMap, double animalXVel, double animalYVel, char[][] grid, SpriteBatch spriteBatch, HashSet<Point> borderPoints, LinkedHashSet<Point> tracePoints) {
        this.stringAnimationHashMap = stringAnimationHashMap;
        this.animalXVel = animalXVel;
        this.animalYVel = animalYVel;
        this.grid = grid;
        setRandomLocation();
        this.spriteBatch = spriteBatch;
        this.borderPoints = borderPoints;
        this.tracePoints = tracePoints;
        if(new Random().nextBoolean()){
            this.animalXVel *= -1;
        }
        if(new Random().nextBoolean()){
            this.animalYVel *= -1;
        }
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
        if(new Random().nextBoolean()){
            animalXVel = -animalXVel;
        }
        if(new Random().nextBoolean()){
            animalYVel *= -animalYVel;
        }
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
        Animation moveSouth = new Animation<>(FRAME_DURATION_MOVE, south);
        Animation moveEast = new Animation<>(FRAME_DURATION_MOVE, east);
        Animation moveNorth = new Animation<>(FRAME_DURATION_MOVE, north);
        Animation moveWest = new Animation<>(FRAME_DURATION_MOVE, west);
        Animation standAnimation = new Animation<>(FRAME_DURATION_STAND, stand);
        stringAnimationHashMap.put("south", moveSouth);
        stringAnimationHashMap.put("east", moveEast);
        stringAnimationHashMap.put("north", moveNorth);
        stringAnimationHashMap.put("west", moveWest);
        stringAnimationHashMap.put("stand", standAnimation);
        return stringAnimationHashMap;
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
                caught = true;
                if (animalXVel>0) animalXVel=1;
                else animalXVel=-1;
                if (animalYVel>0) animalYVel=1;
                else animalYVel=-1;
            }
            elapsedTime += Gdx.graphics.getDeltaTime();
            spriteBatch.begin();
            if (animalXVel > 0 && animalYVel > 0)
                spriteBatch.draw(stringAnimationHashMap.get("north").getKeyFrame(elapsedTime, true), (int) animalX, (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
            else if (animalXVel > 0 && animalYVel < 0)
                spriteBatch.draw(stringAnimationHashMap.get("east").getKeyFrame(elapsedTime, true), (int) animalX, (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
            else if (animalXVel < 0 && animalYVel > 0)
                spriteBatch.draw(stringAnimationHashMap.get("west").getKeyFrame(elapsedTime, true), (int) animalX, (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
            else if (animalXVel < 0 && animalYVel < 0)
                spriteBatch.draw(stringAnimationHashMap.get("south").getKeyFrame(elapsedTime, true), (int) animalX, (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
            spriteBatch.end();
        }else {
            elapsedTime += Gdx.graphics.getDeltaTime();
            spriteBatch.begin();
            spriteBatch.draw(stringAnimationHashMap.get("stand").getKeyFrame(elapsedTime, true), (int) animalX, (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
            spriteBatch.end();
        }
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

    protected void setRandomLocation() {
        do {
            animalX = ThreadLocalRandom.current().nextInt(ANIMAL_SIZE, Gdx.graphics.getWidth() - 2 * ANIMAL_SIZE + 1);
            animalY = ThreadLocalRandom.current().nextInt(ANIMAL_SIZE, Gdx.graphics.getHeight() - 2 * ANIMAL_SIZE + 1);
        } while (grid[(int) animalY / RECT_SIZE][(int) animalX / RECT_SIZE] != '.');
    }

    public double getAnimalXVel() {
        return animalXVel;
    }

    public void setAnimalXVel(double animalXVel) {
        this.animalXVel = animalXVel;
    }

    public double getAnimalYVel() {
        return animalYVel;
    }

    public void setAnimalYVel(double animalYVel) {
        this.animalYVel = animalYVel;
    }

    public double getAnimalX() {
        return animalX;
    }

    public double getAnimalY() {
        return animalY;
    }

    private void reflectFromTheBorderIfNeeded() {
        Rectangle animalRectWest = new Rectangle((int) (animalX - animalXVel), (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
        Rectangle animalRectNorth = new Rectangle((int) animalX, (int) (animalY + animalYVel), ANIMAL_SIZE, ANIMAL_SIZE);
        Rectangle animalRectEast = new Rectangle((int) (animalX + animalXVel), (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
        Rectangle animalRectSouth = new Rectangle((int) animalX, (int) (animalY - animalYVel), ANIMAL_SIZE, ANIMAL_SIZE);
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
        animalRect = new Rectangle((int) animalX, (int) animalY, ANIMAL_SIZE, ANIMAL_SIZE);
        while (iterator.hasNext()) {
            point = iterator.next();
            traceRect = new Rectangle(point.x, point.y, RECT_SIZE, RECT_SIZE);
            if (animalRect.overlaps(traceRect)) {
                return true;
            }
        }
        return false;
    }

    public boolean animalCaught() {
        if (grid[(int) animalY / RECT_SIZE][(int) animalX / RECT_SIZE] == 'C' || grid[(int) animalY / RECT_SIZE][(int) animalX / RECT_SIZE] == 'B')
            return true;
        else return false;
    }

    public enum TYPES {
        DOG(rabbitAnimationsHashMap, 2.5, 3),
        HORSE(horseAnimationsHashMap, 2.5, 2),
        SHEEP(sheepAnimationsHashMap, 1.5, 2.5),
        GOAT(goatAnimationsHashMap, 2, 1.5),
        GOAT_BABY(goatBabyAnimationsHashMap, 1.5, 1.5);

        private HashMap<String, Animation<TextureRegion>> stringAnimationHashMap;
        private double animalXVel, animalYVel;

        TYPES(HashMap<String, Animation<TextureRegion>> stringAnimationHashMap, double animalXVel, double animalYVel) {
            this.stringAnimationHashMap = stringAnimationHashMap;
            this.animalXVel = animalXVel;
            this.animalYVel = animalYVel;
        }

        public HashMap<String, Animation<TextureRegion>> getStringAnimationHashMap() {
            return stringAnimationHashMap;
        }

        public double getAnimalYVel() {
            return animalYVel;
        }

        public double getAnimalXVel() {
            return animalXVel;
        }
    }
}
