package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
    private static final int REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN = 2;
    private final Foresty game;
    private final int secForOneStar;
    private final int secForTwoStars;
    private final int secForThreeStars;
    private final int percOfFillForWin;
    private final long startTimeInMilliseconds;
    public ArrayList<Animal> animals;
    char[][] grid;
    int rows, columns;
    int lastPressedKey;
    boolean clockwise;
    SpriteBatch spriteBatch;
    Texture headTexture, traceTexture, borderTexture, backgroundTexture;
    Texture blueFlowersOnSand, grassOnSand, pinkFlowersOnSand, rocksOnSand;
    Texture rabbitTexture;
    ShapeRenderer shapeRenderer;
    LinkedHashSet<Point> tracePoints;
    HashSet<Point> borderPoints;
    HashMap<Point, Texture> contentPoints;
    Random random;
    boolean turnedBefore;
    private HashMap<AnimalsTypes, Integer> typesIntegerHashMap;
    private Texture winScreenTheeStars, winScreenTwoStars, winScreenOneStars, gameOverScreen;
    private int currX, currY;
    private Point currPoint, prevPoint;
    private int shiftAfterTurn;
    private boolean pause;
    private boolean win;
    private boolean lose;
    private int invokeLaterKey, invokeLaterTimer;
    private int timeElapsedFromTheSlowDown = 0;
    private LevelsScreen.LEVELS level;

    public GameScreen(Foresty game, HashMap<AnimalsTypes, Integer> typesIntegerHashMap, int secForOneStar, int secForTwoStars, int secForThreeStars, int percOfFillForWin, LevelsScreen.LEVELS level) {
        this.game = game;
        this.typesIntegerHashMap = typesIntegerHashMap;
        this.secForOneStar = secForOneStar;
        this.secForTwoStars = secForTwoStars;
        this.secForThreeStars = secForThreeStars;
        this.percOfFillForWin = percOfFillForWin;
        startTimeInMilliseconds = System.currentTimeMillis();
        this.level = level;
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
        winScreenTheeStars = new Texture(Gdx.files.internal("win3stars.png"));
        gameOverScreen = new Texture(Gdx.files.internal("gameover.png"));
        shapeRenderer = new ShapeRenderer();
        tracePoints = new LinkedHashSet<>();
        borderPoints = new HashSet<>();
        contentPoints = new HashMap<>();
        rows = Gdx.graphics.getHeight() / RECT_SIZE;
        columns = Gdx.graphics.getWidth() / RECT_SIZE;
        grid = new char[rows][columns];
        win = true;
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
        random = new Random();
        shiftAfterTurn = 0;
        turnedBefore = false;
        pause = false;
        lose = false;
        invokeLaterKey = -1;
        invokeLaterTimer = 0;
        animals = new ArrayList<>();
        for (Map.Entry<AnimalsTypes, Integer> entry : typesIntegerHashMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                if (entry.getKey() == AnimalsTypes.RABBIT)
                    animals.add(new Rabbit(grid, spriteBatch, borderPoints, tracePoints));
                else if (entry.getKey() == AnimalsTypes.HORSE) {
                    animals.add(new Horse(grid, spriteBatch, borderPoints, tracePoints, this));
                } else if (entry.getKey() == AnimalsTypes.GOAT_BABY) {
                    animals.add(new GoatBaby(grid, spriteBatch, borderPoints, tracePoints, this));
                } else if (entry.getKey() == AnimalsTypes.GOAT) {
                    animals.add(new Goat(grid, spriteBatch, borderPoints, tracePoints));
                }
            }
        }

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if(screenX>= 374 && screenX <= 450 && screenY>= 460 && screenY <= 535){
                    game.setScreen(game.levelsScreen);
                }
                else if(screenX>= 505 && screenX <= 580 && screenY>= 460 && screenY <= 535){
                    // TODO: switch to next level
                    game.levelsScreen.levelCompleted();
                }
                return true;
            }
        });

    }

    @Override
    public void render(float data) {


//        Pause game if space pressed on first time and resume on second press.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (pause) pause = false;
            else pause = true;
        }
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
//        Close batch.
        spriteBatch.end();
//        Draw animal.
        for (Animal animal : animals) {
            if (!animal.isMovePaused()) animal.moveAndDrawAnimal();
        }
        if (win) {
            int gameTime = (int)(System.currentTimeMillis() - startTimeInMilliseconds) / 1000;
            if (gameTime <= secForThreeStars){
                showGameEndScreen(winScreenTheeStars);
                level.setNumOfStars(3);
            }
            else if(gameTime <= secForTwoStars){
                showGameEndScreen(winScreenTwoStars);
                if(level.getNumOfStars() < 2)
                level.setNumOfStars(2);}
            else if(gameTime <= secForThreeStars){
                showGameEndScreen(winScreenTheeStars);
                if(level.getNumOfStars() < 1)
                level.setNumOfStars(1);}
            else{
                //TODO create win screen with 0 stars
                showGameEndScreen(winScreenTheeStars);
                level.setNumOfStars(0);
            }
            game.levelsScreen.levelCompleted();
            return;
        }
        checkForLose();
        if(lose){
            showGameEndScreen(gameOverScreen);
        }
    }

    /**
     * use when the game ends to show gameOverScreenTexture
     * @param gameOverScreenTexture
     */

    private void showGameEndScreen(Texture gameOverScreenTexture){
        for (Animal animal : animals) {
            animal.setAnimalXVel(0);
            animal.setAnimalYVel(0);
        }
        spriteBatch.begin();
        spriteBatch.draw(gameOverScreenTexture, Gdx.graphics.getWidth() / 2 - winScreenTheeStars.getWidth() / 2, Gdx.graphics.getHeight() / 2 - winScreenTheeStars.getHeight() / 2);
        spriteBatch.end();
        //todo: add listeners for lose, stop the game
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
//                System.out.println("W in dot (" + currX + ", " + currY + ").");
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
//                System.out.println("A in dot (" + currX + ", " + currY + ").");
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
//                System.out.println("S in dot (" + currX + ", " + currY + ").");
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
//                System.out.println("D in dot (" + currX + ", " + currY + ").");
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
        if (!win && !lose) {
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
                    if (grid[currPoint.y / RECT_SIZE + 1][currPoint.x / RECT_SIZE] == 'B')
                        lastPressedKey = Input.Keys.W;
                    else if (grid[currPoint.y / RECT_SIZE - 1][currPoint.x / RECT_SIZE] == 'B')
                        lastPressedKey = Input.Keys.S;
                    else if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE + 1] == 'B')
                        lastPressedKey = Input.Keys.D;
                    else if (grid[currPoint.y / RECT_SIZE][currPoint.x / RECT_SIZE - 1] == 'B')
                        lastPressedKey = Input.Keys.A;
                    tracePoints.add(currPoint);
                    for (Point point : tracePoints) {
                        //TODO: very often OutOfBoundException is thrown on line below
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

    private void checkForWin() {
        double currFillCells = 0, totalCells = (rows - 2) * (columns - 2);
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                if (grid[i][j] != '.') currFillCells++;
            }
        }
        System.out.println((int) ((currFillCells * 100) / totalCells));
        if (((int) ((currFillCells * 100) / totalCells)) >= percOfFillForWin) {
            win = true;
            return;
        }
        for (Animal animal : animals) {
            if (!animal.animalCaught()) {
                win = false;
                return;
            }
        }
        win = true;
    }

    private void checkForLose() {
        for (Animal animal : animals) {
            if (animal.crossesLine()) {
                lose = true;
                pause();
            }
        }
    }


    public void slowDownAnimals(long time) {
        if (timeElapsedFromTheSlowDown == 1) {
            int newXVel, newYVel;
            for (Animal animal : animals) {
                newXVel = ((int) Math.signum(animal.getAnimalXVel())) * (Math.abs(animal.getAnimalXVel()) - REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                newYVel = ((int) Math.signum(animal.getAnimalYVel())) * (Math.abs(animal.getAnimalYVel()) - REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                animal.setAnimalXVel(newXVel);
                animal.setAnimalYVel(newYVel);
            }
        } else if (timeElapsedFromTheSlowDown == NUM_OF_RENDERS_OF_SLOWING_DOWN) {
            int newXVel, newYVel;
            for (Animal animal : animals) {
                newXVel = ((int) Math.signum(animal.getAnimalXVel())) * (Math.abs(animal.getAnimalXVel()) + REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                newYVel = ((int) Math.signum(animal.getAnimalYVel())) * (Math.abs(animal.getAnimalYVel()) + REDUCTION_OF_SPEED_WHILE_SLOWING_DOWN);
                animal.setAnimalXVel(newXVel);
                animal.setAnimalYVel(newYVel);
            }
        }
        for (Animal animal : animals) {
            System.out.println(animal.getAnimalXVel());
            System.out.println(animal.getAnimalYVel());
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