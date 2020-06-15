package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import java.awt.*;
import java.util.Queue;
import java.util.*;

public class GameScreen extends ScreenAdapter {

    private static final int RECT_SIZE = 16;
    private static final int VELOCITY = 4;
    private static final double PINK_FLOWER_TEXTURE_PROBABILITY = 0.02;
    private static final double BLUE_FLOWER_TEXTURE_PROBABILITY = 0.02;
    private static final double ROCKS_TEXTURE_PROBABILITY = 0.02;
    private static final int NUM_OF_RENDERS_OF_SLOWING_DOWN = 500;
    private static final int REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN = 1;
    private static final Random random = new Random();
    private final Foresty game;
    private final int secForOneStar;
    private final int secForTwoStars;
    private final int secForThreeStars;
    private final int percOfFillForWin;
    private long startTimeInMilliseconds;
    public ArrayList<Animal> animals;
    public char[][] grid;
    int rows, columns;
    int gameTime;
    int lastPressedKey;
    boolean clockwise;
    private int timeElapsedFromTheSlowDown = 0;
    SpriteBatch spriteBatch;
    Texture headTexture, traceTexture, borderTexture, backgroundTexture;
    Texture blueFlowersOnSand, grassOnSand, pinkFlowersOnSand, rocksOnSand;
    Texture pauseTexture;
    Texture rabbitTexture;
    ShapeRenderer shapeRenderer;
    LinkedHashSet<Point> tracePoints;
    HashSet<Point> borderPoints;
    HashMap<Point, Texture> contentPoints;
    boolean turnedBefore;
    private LevelsScreen.LevelsCompleted currentLevel;
    private HashMap<Animal.TYPES, Integer> typesIntegerHashMap;
    private Texture winScreenTheeStars, winScreenTwoStars, winScreenOneStars, gameOverScreen, winScreenZeroStars;
    private Texture lastLevelThreeStars, lastLevelTwoStars, lastLevelOneStars, lastLevelZeroStars;
    private Texture levelDescriptionTexture;
    private int currX, currY;
    private Point currPoint, prevPoint;
    private int shiftAfterTurn;
    private boolean pause;
    private boolean win, lose;
    private boolean levelDescriptionIsShown;
    private int invokeLaterKey, invokeLaterTimer;

    public GameScreen(Foresty game, HashMap<Animal.TYPES, Integer> typesIntegerHashMap, int secForOneStar, int secForTwoStars, int secForThreeStars, int percOfFillForWin, LevelsScreen.LevelsCompleted currentLevel) {
        this.game = game;
        this.typesIntegerHashMap = typesIntegerHashMap;
        this.secForOneStar = secForOneStar;
        this.secForTwoStars = secForTwoStars;
        this.secForThreeStars = secForThreeStars;
        this.percOfFillForWin = percOfFillForWin;
        this.currentLevel = currentLevel;
        startTimeInMilliseconds = System.currentTimeMillis();
        levelDescriptionIsShown = true;
    }

    private void initializeLevelDescriptionTexture(){
        if(currentLevel == LevelsScreen.LevelsCompleted.ONE)
            levelDescriptionTexture = new Texture(Gdx.files.internal("Story\\legend.png"));
        else if(currentLevel == LevelsScreen.LevelsCompleted.TWO)
            levelDescriptionTexture = new Texture(Gdx.files.internal("Story\\horseDescription.png"));
        else if(currentLevel == LevelsScreen.LevelsCompleted.THREE)
            levelDescriptionTexture = new Texture(Gdx.files.internal("Story\\dogDescription.png"));
        else if(currentLevel == LevelsScreen.LevelsCompleted.FOUR)
            levelDescriptionTexture = new Texture(Gdx.files.internal("Story\\goatDescription.png"));
        else if(currentLevel == LevelsScreen.LevelsCompleted.FIVE)
            levelDescriptionTexture = new Texture(Gdx.files.internal("Story\\babeGoatDescription.png"));
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();
        headTexture = new Texture(Gdx.files.internal("head4.png"));
        traceTexture = new Texture(Gdx.files.internal("trace3.png"));
        borderTexture = new Texture(Gdx.files.internal("border2.png"));
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        blueFlowersOnSand = new Texture(Gdx.files.internal("blueFlowersOnSand.png"));
        pinkFlowersOnSand = new Texture(Gdx.files.internal("pinkFlowersOnSand.png"));
        rocksOnSand = new Texture(Gdx.files.internal("rocksOnSand.png"));
        grassOnSand = new Texture(Gdx.files.internal("grassOnSand.png"));
        rabbitTexture = new Texture(Gdx.files.internal("rabbit.gif"));
        winScreenTheeStars = new Texture(Gdx.files.internal("finishLevel\\completedThree.png"));
        winScreenOneStars = new Texture(Gdx.files.internal("finishLevel\\completedOne.png"));
        winScreenTwoStars = new Texture(Gdx.files.internal("finishLevel\\completedTwo.png"));
        winScreenZeroStars = new Texture(Gdx.files.internal("finishLevel\\completedZero.png"));
        lastLevelThreeStars = new Texture(Gdx.files.internal("finishLevel\\completedLastThree.png"));
        lastLevelOneStars = new Texture(Gdx.files.internal("finishLevel\\completedLastOne.png"));
        lastLevelTwoStars = new Texture(Gdx.files.internal("finishLevel\\completedLastTwo.png"));
        lastLevelZeroStars = new Texture(Gdx.files.internal("finishLevel\\completedLastZero.png"));
        gameOverScreen = new Texture(Gdx.files.internal("finishLevel\\failed.png"));
        pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        initializeLevelDescriptionTexture();
        shapeRenderer = new ShapeRenderer();
        tracePoints = new LinkedHashSet<>();
        borderPoints = new HashSet<>();
        contentPoints = new HashMap<>();
        rows = Gdx.graphics.getHeight() / RECT_SIZE;
        columns = Gdx.graphics.getWidth() / RECT_SIZE;
        grid = new char[rows][columns];
        win = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = '.';
            }
        }
        for (int i = 0; i < Gdx.graphics.getHeight(); i += RECT_SIZE) {
            borderPoints.add(new Point(0, i));
            borderPoints.add(new Point(Gdx.graphics.getWidth() - RECT_SIZE, i));
            grid[i / RECT_SIZE][0] = 'B';
            grid[i / RECT_SIZE][columns - 1] = 'B';
        }
        for (int i = 0; i < Gdx.graphics.getWidth(); i += RECT_SIZE) {
            borderPoints.add(new Point(i, 0));
            borderPoints.add(new Point(i, Gdx.graphics.getHeight() - RECT_SIZE));
            grid[0][i / RECT_SIZE] = 'B';
            grid[rows - 1][i / RECT_SIZE] = 'B';
        }
        currX = 0;
        currY = RECT_SIZE;
        clockwise = true;
        lastPressedKey = Input.Keys.W;
        currPoint = new Point(currX, currY);
        prevPoint = null;
        shiftAfterTurn = 0;
        turnedBefore = false;
        pause = false;
        lose = false;
        invokeLaterKey = -1;
        invokeLaterTimer = 0;
        animals = new ArrayList<>();
        for (Map.Entry<Animal.TYPES, Integer> entry : typesIntegerHashMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                if (entry.getKey() == Animal.TYPES.DOG) {
                    animals.add(new Dog(grid, spriteBatch, borderPoints, tracePoints));
                } else if (entry.getKey() == Animal.TYPES.HORSE) {
                    animals.add(new Horse(grid, spriteBatch, borderPoints, tracePoints, this));
                } else if (entry.getKey() == Animal.TYPES.GOAT) {
                    animals.add(new Goat(grid, spriteBatch, borderPoints, tracePoints, contentPoints));
                } else if (entry.getKey() == Animal.TYPES.GOAT_BABY) {
                    animals.add(new GoatBaby(grid, spriteBatch, borderPoints, tracePoints, this));
                } else if (entry.getKey() == Animal.TYPES.SHEEP) {
                    animals.add(new Sheep(grid, spriteBatch, borderPoints, tracePoints));
                }
            }
        }

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (win && currentLevel.getNum() != 5) {
                    Circle home = new Circle(424, Gdx.graphics.getHeight() - 462, 40);
                    Circle restart = new Circle(483, Gdx.graphics.getHeight() - 462, 40);
                    Circle next = new Circle(582, Gdx.graphics.getHeight() - 462, 40);
                    if (home.contains(screenX, Gdx.graphics.getHeight() - screenY))
                    {
                        game.setScreen(game.levelsScreen, game.musicOnTitleAndLevelsScreens);
                    }
                    else if (restart.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                        if (currentLevel == LevelsScreen.LevelsCompleted.ONE)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlFirstAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.ONE), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.TWO)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlSecondAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.TWO), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.THREE)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlThirdAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.THREE), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.FOUR)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlFourthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FOUR), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.FIVE)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlFifthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FIVE), game.musicOnGameScreen);
                        }
                    } else if (next.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                        if (currentLevel == LevelsScreen.LevelsCompleted.ONE)

                            game.setScreen(new GameScreen(game, LevelsScreen.lvlSecondAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.TWO), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.TWO)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlThirdAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.THREE), game.musicOnGameScreen);
                        } else if (currentLevel == LevelsScreen.LevelsCompleted.THREE) {
                        game.setScreen(new GameScreen(game, LevelsScreen.lvlFourthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FOUR), game.musicOnGameScreen);
                    } else if (currentLevel == LevelsScreen.LevelsCompleted.FOUR) {
                        game.setScreen(new GameScreen(game, LevelsScreen.lvlFifthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FIVE), game.musicOnGameScreen);
                    }
                } else if (win && currentLevel.getNum() == 5) {
                    Circle home = new Circle(434, Gdx.graphics.getHeight() - 458, 40);
                    Circle restart = new Circle(528, Gdx.graphics.getHeight() - 462, 40);
                    if (home.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                        game.setScreen(game.levelsScreen, game.musicOnTitleAndLevelsScreens);
                    } else if (restart.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                        game.setScreen(new GameScreen(game, LevelsScreen.lvlFifthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FIVE), game.musicOnGameScreen);
                    }
                } else if (lose) {
                    Circle home = new Circle(424, Gdx.graphics.getHeight() - 466, 40);
                    Circle restart = new Circle(529, Gdx.graphics.getHeight() - 466, 40);
                    if (home.contains(screenX, Gdx.graphics.getHeight() - screenY))
                    {
                        game.setScreen(game.levelsScreen, game.musicOnTitleAndLevelsScreens);
                    }
                    else if (restart.contains(screenX, Gdx.graphics.getHeight() - screenY)) {
                        if (currentLevel == LevelsScreen.LevelsCompleted.ONE)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlFirstAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.ONE), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.TWO)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlSecondAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.TWO), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.THREE)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlThirdAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.THREE), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.FOUR)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlFourthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FOUR), game.musicOnGameScreen);
                        }
                        else if (currentLevel == LevelsScreen.LevelsCompleted.FIVE)
                        {
                            game.setScreen(new GameScreen(game, LevelsScreen.lvlFifthAnimalsHashMap, 45, 30, 15, 75, LevelsScreen.LevelsCompleted.FIVE), game.musicOnGameScreen);
                        }
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void render(float data) {

//        Pause game if space pressed on first time and resume on second press.
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && levelDescriptionIsShown == false) {
                if (pause) {
                    pause = false;
                    for (Animal animal : animals) animal.resumeMove();
                } else {
                    pause = true;
                    for (Animal animal : animals) animal.pauseMove();
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
                game.setScreen(game.levelsScreen, game.musicOnTitleAndLevelsScreens);
//        Determine if user press one of the following keys.
            ifKeyRecentlyPressed();
//        Move tile every render.
            moveHead();
//        Create point of current position of head tile.
            Point newPoint = new Point(currX / RECT_SIZE * RECT_SIZE, currY / RECT_SIZE * RECT_SIZE);
            addNewPoint(newPoint);
            spriteBatch.begin();
//        Draw background.
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    spriteBatch.draw(backgroundTexture, j * RECT_SIZE, i * RECT_SIZE, RECT_SIZE, RECT_SIZE);
                }
            }
//        Then draw content inside borders.
            for (Map.Entry<Point, Texture> entry : contentPoints.entrySet()) {
                spriteBatch.draw(entry.getValue(), entry.getKey().x, entry.getKey().y, RECT_SIZE, RECT_SIZE);
            }
//        Then draw borders of the map.
            for (Point point : borderPoints) {
                spriteBatch.draw(borderTexture, point.x, point.y, RECT_SIZE, RECT_SIZE);
            }
//        Draw trace after head.
            Iterator<Point> pointIterator = tracePoints.iterator();
            Point currPoint = null;
            if (pointIterator.hasNext()) currPoint = pointIterator.next();
            do {
                if (currPoint != null) spriteBatch.draw(traceTexture, currPoint.x, currPoint.y, RECT_SIZE, RECT_SIZE);
                if (pointIterator.hasNext()) currPoint = pointIterator.next();
            } while (pointIterator.hasNext());
//        And the last one, draw head tile.
            spriteBatch.draw(headTexture, this.currPoint.x, this.currPoint.y, RECT_SIZE, RECT_SIZE);
            spriteBatch.end();
//        Draw animal.
            for (Animal animal : animals) {
                animal.moveAndDrawAnimal();
            }

            if (pause && !win && !lose) {
                spriteBatch.begin();
                spriteBatch.draw(pauseTexture, Gdx.graphics.getWidth() / 2 - pauseTexture.getWidth() / 2, Gdx.graphics.getHeight() / 2 - pauseTexture.getHeight() / 2);
                spriteBatch.end();
                return;
            }
            if(levelDescriptionIsShown){
            spriteBatch.begin();
            spriteBatch.draw(levelDescriptionTexture, Gdx.graphics.getWidth()/2 - levelDescriptionTexture.getWidth()/2, Gdx.graphics.getHeight()/2 - levelDescriptionTexture.getHeight()/2);
            spriteBatch.end();
            for (Animal animal : animals) animal.pauseMove();

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                levelDescriptionIsShown = false;
                for (Animal animal : animals) animal.resumeMove();
            }
        }

            checkForWin();
            if (win) {
                if (gameTime <= secForThreeStars) {
                    if (currentLevel.getNum() != 5)
                        showGameEndScreen(winScreenTheeStars);
                    else
                        showGameEndScreen(lastLevelThreeStars);
                } else if (gameTime <= secForTwoStars) {
                    if (currentLevel.getNum() != 5)
                        showGameEndScreen(winScreenTwoStars);
                    else
                        showGameEndScreen(lastLevelTwoStars);
                } else if (gameTime <= secForOneStar) {
                    if (currentLevel.getNum() != 5)
                        showGameEndScreen(winScreenOneStars);
                    else
                        showGameEndScreen(lastLevelOneStars);
                } else {
                    if (currentLevel.getNum() != 5)
                        showGameEndScreen(winScreenZeroStars);
                    else
                        showGameEndScreen(lastLevelZeroStars);
                }
                return;
            }
            if (!lose)
                checkForLose();
            if (lose)
                showGameEndScreen(gameOverScreen);
        //}
    }

    /**
     * use when the game ends to show gameOverScreenTexture
     *
     * @param gameOverScreenTexture
     */

    private void showGameEndScreen(Texture gameOverScreenTexture) {
        for (Animal animal : animals) animal.pauseMove();
        pause = true;
        spriteBatch.begin();
        spriteBatch.draw(gameOverScreenTexture, Gdx.graphics.getWidth() / 2 - winScreenTheeStars.getWidth() / 2, Gdx.graphics.getHeight() / 2 - winScreenTheeStars.getHeight() / 2);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private void moveHead() {
        if (!pause) {
            if (turnedBefore) shiftAfterTurn += VELOCITY;
            if (shiftAfterTurn > RECT_SIZE) {
                shiftAfterTurn = 0;
                turnedBefore = false;
            }
            if (invokeLaterKey != -1) invokeLaterTimer += VELOCITY;
            if (invokeLaterTimer > RECT_SIZE) {
                lastPressedKey = invokeLaterKey;
                turnedBefore = true;
                invokeLaterTimer = 0;
                invokeLaterKey = -1;
            }
            if (lastPressedKey == Input.Keys.W) {
                currY += VELOCITY;
                if (!turnedBefore) {
                    if (clockwise) {
                        if (borderPoints.contains(new Point(currPoint.x + RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.D;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y + RECT_SIZE))) {
                        } else if (borderPoints.contains(new Point(currPoint.x - RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.A;
                            turnedBefore = true;
                        }
                    } else {
                        if (borderPoints.contains(new Point(currPoint.x - RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.A;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y + RECT_SIZE))) {
                        } else if (borderPoints.contains(new Point(currPoint.x + RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.D;
                            turnedBefore = true;
                        }
                    }
                }
            } else if (lastPressedKey == Input.Keys.A) {
                currX -= VELOCITY;
                if (!turnedBefore) {
                    if (clockwise) {
                        if (borderPoints.contains(new Point(currPoint.x, currPoint.y + RECT_SIZE))) {
                            lastPressedKey = Input.Keys.W;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x - RECT_SIZE, currPoint.y))) {
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y - RECT_SIZE))) {
                            lastPressedKey = Input.Keys.S;
                            turnedBefore = true;
                        }
                    } else {
                        if (borderPoints.contains(new Point(currPoint.x, currPoint.y - RECT_SIZE))) {
                            lastPressedKey = Input.Keys.S;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x - RECT_SIZE, currPoint.y))) {
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y + RECT_SIZE))) {
                            lastPressedKey = Input.Keys.W;
                            turnedBefore = true;
                        }
                    }
                }
            } else if (lastPressedKey == Input.Keys.S) {
                currY -= VELOCITY;
                if (!turnedBefore) {
                    if (clockwise) {
                        if (borderPoints.contains(new Point(currPoint.x - RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.A;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y - RECT_SIZE))) {
                        } else if (borderPoints.contains(new Point(currPoint.x + RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.D;
                            turnedBefore = true;
                        }
                    } else {
                        if (borderPoints.contains(new Point(currPoint.x + RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.D;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y - RECT_SIZE))) {
                        } else if (borderPoints.contains(new Point(currPoint.x - RECT_SIZE, currPoint.y))) {
                            lastPressedKey = Input.Keys.A;
                            turnedBefore = true;
                        }
                    }
                }
            } else if (lastPressedKey == Input.Keys.D) {
                currX += VELOCITY;
                if (!turnedBefore) {
                    if (clockwise) {
                        if (borderPoints.contains(new Point(currPoint.x, currPoint.y - RECT_SIZE))) {
                            lastPressedKey = Input.Keys.S;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x + RECT_SIZE, currPoint.y))) {
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y + RECT_SIZE))) {
                            lastPressedKey = Input.Keys.W;
                            turnedBefore = true;
                        }
                    } else {
                        if (borderPoints.contains(new Point(currPoint.x, currPoint.y + RECT_SIZE))) {
                            lastPressedKey = Input.Keys.W;
                            turnedBefore = true;
                        } else if (borderPoints.contains(new Point(currPoint.x + RECT_SIZE, currPoint.y))) {
                        } else if (borderPoints.contains(new Point(currPoint.x, currPoint.y - RECT_SIZE))) {
                            lastPressedKey = Input.Keys.S;
                            turnedBefore = true;
                        }
                    }
                }
            }
        }
    }

    private void ifKeyRecentlyPressed() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && !(tracePoints.contains(currPoint) && lastPressedKey == Input.Keys.S) && !turnedBefore) {
            if (lastPressedKey == Input.Keys.S) turnClockwise();
            lastPressedKey = Input.Keys.W;
            turnedBefore = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A) && !(tracePoints.contains(currPoint) && lastPressedKey == Input.Keys.D) && !turnedBefore) {
            if (lastPressedKey == Input.Keys.D) turnClockwise();
            lastPressedKey = Input.Keys.A;
            turnedBefore = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S) && !(tracePoints.contains(currPoint) && lastPressedKey == Input.Keys.W) && !turnedBefore) {
            if (lastPressedKey == Input.Keys.W) turnClockwise();
            lastPressedKey = Input.Keys.S;
            turnedBefore = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && !(tracePoints.contains(currPoint) && lastPressedKey == Input.Keys.A) && !turnedBefore) {
            if (lastPressedKey == Input.Keys.A) turnClockwise();
            lastPressedKey = Input.Keys.D;
            turnedBefore = true;
        }
    }

    private void turnClockwise() {
        if (clockwise) clockwise = false;
        else clockwise = true;
    }

    private void addNewPoint(Point newPoint) {
        if (!newPoint.equals(currPoint)) {
            prevPoint = currPoint;
            currPoint = newPoint;
            if (tracePoints.contains(currPoint) && !currPoint.equals(prevPoint)) {
                lose = true;
            }
            //end of capturing

            if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE] != 'B' && !tracePoints.isEmpty()
                    && (grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE] == 'B' || grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE] == 'B'
                    || grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE + 1] == 'B' || grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE - 1] == 'B')) {
                if (grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE] == 'B') lastPressedKey = Input.Keys.W;
                else if (grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE] == 'B')
                    lastPressedKey = Input.Keys.S;
                else if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE + 1] == 'B')
                    lastPressedKey = Input.Keys.D;
                else if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE - 1] == 'B')
                    lastPressedKey = Input.Keys.A;
                tracePoints.add(currPoint);
                for (Point point : tracePoints) {
                    grid[point.y / RECT_SIZE][point.x / RECT_SIZE] = 'T';
                }
                int red = 0, green = 0;
                /*
                Find first dot and flood fill from it.
                 */
                Point pointerOnDot = getDotInGrid();
                floodFill(pointerOnDot.x, pointerOnDot.y, 'R');
                /*
                Fill rest of cells in green and count amount of both colors.
                 */
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (grid[i][j] == '.') {
                            grid[i][j] = 'G';
                            green++;
                        }
                        if (grid[i][j] == 'R') red++;
                    }
                }
                /*
                Replace greater color on dots,
                lower color on content inside bounds,
                and trace on bounds
                //TODO: compare number of enemies first, and if they equals, then compare squares.
                 */
                if (red > green) fillGrid('G', 'R');
                else fillGrid('R', 'G');
                //Redefine tracePoints.
                tracePoints.clear();
                printFiveCells();
                if (lastPressedKey == Input.Keys.W) {
                    if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE + 1] == '.' && grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE + 1] == 'B') {
                        invokeLaterKey = Input.Keys.D;
                        clockwise = true;
                    } else if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE - 1] == '.' && grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE - 1] == 'B') {
                        invokeLaterKey = Input.Keys.A;
                        clockwise = false;
                    } else System.out.println("No turn, go straight on W key.");
                } else if (lastPressedKey == Input.Keys.A) {
                    if (grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE] == '.' && grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE - 1] == 'B') {
                        invokeLaterKey = Input.Keys.W;
                        clockwise = true;
                    } else if (grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE] == '.' && grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE - 1] == 'B') {
                        invokeLaterKey = Input.Keys.S;
                        clockwise = false;
                    } else System.out.println("Unexpected turn on A key.");
                } else if (lastPressedKey == Input.Keys.S) {
                    if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE + 1] == '.' && grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE + 1] == 'B') {
                        invokeLaterKey = Input.Keys.D;
                        clockwise = false;
                    } else if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE - 1] == '.' && grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE - 1] == 'B') {
                        invokeLaterKey = Input.Keys.A;
                        clockwise = true;
                    } else System.out.println("Unexpected turn on S key.");
                } else if (lastPressedKey == Input.Keys.D) {
                    if (grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE] == '.' && grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE + 1] == 'B') {
                        invokeLaterKey = Input.Keys.W;
                        clockwise = false;
                    } else if (grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE] == '.' && grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE + 1] == 'B') {
                        invokeLaterKey = Input.Keys.S;
                        clockwise = true;
                    } else System.out.println("Unexpected turn on D key.");
                }
                checkForWin();
            }

            if (!borderPoints.contains(currPoint)) tracePoints.add(newPoint);
            else tracePoints.clear();
        }
    }

    private void printGridInConsole() {
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = 0; j < columns; j++) {
                System.out.print(((grid[i][j] != 0) ? grid[i][j] : " ") + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printFiveCells() {
        System.out.println("Current: " + grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE] + ", coordinates: " + currPoint.y / RECT_SIZE + ", " + currPoint.x / RECT_SIZE);
        System.out.println("Upper: " + grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE] + ", coordinates: " + (currPoint.y / RECT_SIZE + 1) + ", " + currPoint.x / RECT_SIZE);
        System.out.println("Under: " + grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE] + ", coordinates: " + (currPoint.y / RECT_SIZE - 1) + ", " + currPoint.x / RECT_SIZE);
        System.out.println("Right: " + grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE + 1] + ", coordinates: " + currPoint.y / RECT_SIZE + ", " + (currPoint.x / RECT_SIZE + 1));
        System.out.println("Left: " + grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE - 1] + ", coordinates: " + currPoint.y / RECT_SIZE + ", " + (currPoint.x / RECT_SIZE - 1));
    }

    private Point getDotInGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == '.') {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    void floodFill(int dotI, int dotJ, char newSymbol) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(dotI, dotJ));

        while (!queue.isEmpty()) {
            Point point = queue.remove();
            if (grid[point.x][point.y] == '.') {
                grid[point.x][point.y] = newSymbol;
                queue.add(new Point(point.x - 1, point.y));
                queue.add(new Point(point.x + 1, point.y));
                queue.add(new Point(point.x, point.y - 1));
                queue.add(new Point(point.x, point.y + 1));
            }
        }
    }

    private void fillGrid(char lower, char greater) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == lower) {
                    grid[i][j] = 'C';
                    contentPoints.put(new Point(j * RECT_SIZE, i * RECT_SIZE), randContentInsideBorder());
                } else if (grid[i][j] == 'T') {
                    grid[i][j] = 'B';
                    borderPoints.add(new Point(j * RECT_SIZE, i * RECT_SIZE));
                } else if (grid[i][j] == greater) grid[i][j] = '.';
            }
        }
    }

    private Texture randContentInsideBorder() {
        double textureDouble = random.nextDouble();
        double lowerBound = 0;
        if (textureDouble >= lowerBound && textureDouble < lowerBound + PINK_FLOWER_TEXTURE_PROBABILITY)
            return pinkFlowersOnSand;
        lowerBound += PINK_FLOWER_TEXTURE_PROBABILITY;
        if (textureDouble >= lowerBound && textureDouble < lowerBound + BLUE_FLOWER_TEXTURE_PROBABILITY)
            return blueFlowersOnSand;
        lowerBound += BLUE_FLOWER_TEXTURE_PROBABILITY;
        if (textureDouble >= lowerBound && textureDouble < lowerBound + ROCKS_TEXTURE_PROBABILITY) return rocksOnSand;
        return grassOnSand;
    }

    private boolean allAnimalsAreCaught() {
        for (Animal animal : animals) {
            if (!animal.animalCaught()) {
                return false;
            }
        }
        return true;
    }

    private boolean fieldIsFilled() {
        double currFillCells = 0, totalCells = (rows - 2) * (columns - 2);
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                if (grid[i][j] != '.') currFillCells++;
            }
        }
        if (((int) ((currFillCells * 100) / totalCells)) >= percOfFillForWin) {
            return true;
        }
        return false;
    }

    private void checkForWin() {
        if (!win) {
            if (allAnimalsAreCaught() || fieldIsFilled()) {
                win = true;
                Music winMusic = Gdx.audio.newMusic(Gdx.files.internal("EndGameSounds\\win.mp3"));
                winMusic.setLooping(false);
                winMusic.play();
                if (game.levelsScreen.getLevel() == currentLevel)
                    game.levelsScreen.levelCompleted();

                gameTime = (int) (System.currentTimeMillis() - startTimeInMilliseconds) / 1000;
                System.out.println(gameTime);
                if (gameTime <= secForThreeStars) {
                    currentLevel.setNumOfStars(3);
                } else if (gameTime <= secForTwoStars) {
                    if (currentLevel.getNumOfStars() < 2)
                        currentLevel.setNumOfStars(2);
                } else if (gameTime <= secForOneStar) {
                    if (currentLevel.getNumOfStars() < 1)
                        currentLevel.setNumOfStars(1);
                } else {
                    currentLevel.setNumOfStars(0);
                }
            }
        }
    }

    private void checkForLose() {
        for (Animal animal : animals) {
            if (animal.crossesLine()) {
                Music winMusic = Gdx.audio.newMusic(Gdx.files.internal("EndGameSounds\\lose.mp3"));
                winMusic.setLooping(false);
                winMusic.play();
                lose = true;
                break;
            }
        }
    }

    public void slowDownAnimals() {
        if (timeElapsedFromTheSlowDown == 1) {
            double newXVel, newYVel;
            for (Animal animal : animals) {
                newXVel = (Math.signum(animal.getAnimalXVel())) * (Math.abs(animal.getAnimalXVel()) - REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                newYVel = (Math.signum(animal.getAnimalYVel())) * (Math.abs(animal.getAnimalYVel()) - REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                animal.setAnimalXVel(newXVel);
                animal.setAnimalYVel(newYVel);
            }
        } else if (timeElapsedFromTheSlowDown == NUM_OF_RENDERS_OF_SLOWING_DOWN) {
            double newXVel, newYVel;
            for (Animal animal : animals) {
                newXVel = (Math.signum(animal.getAnimalXVel())) * (Math.abs(animal.getAnimalXVel()) + REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                newYVel = (Math.signum(animal.getAnimalYVel())) * (Math.abs(animal.getAnimalYVel()) + REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                animal.setAnimalXVel(newXVel);
                animal.setAnimalYVel(newYVel);
            }
        }
        timeElapsedFromTheSlowDown++;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isLose() {
        return lose;
    }

    public void setLose(boolean lose) {
        this.lose = lose;
    }
}