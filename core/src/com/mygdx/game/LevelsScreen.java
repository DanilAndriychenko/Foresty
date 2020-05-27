package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class LevelsScreen extends ScreenAdapter {

    Foresty game;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture map;
    Texture send;
    Texture firstLevel, secondLevel, thirdLevel, fourthLevel, fifthLevel;
    Texture star;
    Texture locked;

    float rectWidth;
    float rectHeight;
    float moveSpeed = 600;
    private numOfLevelsCompleted level;

    LevelsScreen(Foresty game) {
        this.game = game;
        level = numOfLevelsCompleted.THREE;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        camera.update();

        rectWidth = Gdx.graphics.getWidth();
        rectHeight = Gdx.graphics.getHeight();
        map = new Texture(Gdx.files.internal("map.png"));
        send = new Texture(Gdx.files.internal("send.png"));
        firstLevel = new Texture(Gdx.files.internal("farm1.png"));
        secondLevel = new Texture(Gdx.files.internal("farm2.png"));
        thirdLevel = new Texture(Gdx.files.internal("farm3.png"));
        fourthLevel = new Texture(Gdx.files.internal("farm4.png"));
        fifthLevel = new Texture(Gdx.files.internal("farm5.png"));
        locked = new Texture("locked.png");
        star = new Texture("star.png");
        batch = new SpriteBatch();

        final HashMap<Animal.TYPES, Integer> levelFirstAnimals = new HashMap<>();
        levelFirstAnimals.put(Animal.TYPES.RABBIT, 1);
        levelFirstAnimals.put(Animal.TYPES.HORSE, 1);
        levelFirstAnimals.put(Animal.TYPES.SHEEP, 1);
        levelFirstAnimals.put(Animal.TYPES.GOAT, 1);
        levelFirstAnimals.put(Animal.TYPES.GOAT_BABY, 1);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                //mouse listener for first level
                if (screenX >= map.getWidth() / 10 - firstLevel.getWidth() / 2
                        && screenX <= map.getWidth() / 10 - firstLevel.getWidth() / 2 + firstLevel.getWidth() * 2
                        && (Gdx.graphics.getHeight() - screenY) >= map.getHeight() / 2 - firstLevel.getHeight() / 2
                        && (Gdx.graphics.getHeight() - screenY) <= map.getHeight() / 2 - firstLevel.getHeight() / 2 + 2*firstLevel.getHeight()){
                    game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                }
                //mouse listener for second level
                else if (screenX >= map.getWidth() / 5 + map.getWidth() / 10 - secondLevel.getWidth() / 2
                        && screenX <= map.getWidth() / 5 + map.getWidth() / 10 - secondLevel.getWidth() / 2 + 2*(secondLevel.getWidth())
                        && (Gdx.graphics.getHeight() - screenY) >= map.getHeight() / 2 - secondLevel.getHeight() / 2
                        && (Gdx.graphics.getHeight() - screenY) <= map.getHeight() / 2 - firstLevel.getHeight() / 2 + 2*(secondLevel.getHeight())
                        && level.getNum() + 1 >= 2)
                    System.out.println(2);
                    //TODO: set correct parameters to new screen in all cases below.
                    //game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));

                //mouse listener for third level
                else if (screenX >=2 * map.getWidth() / 5 + map.getWidth() / 10 - thirdLevel.getWidth() / 2
                        && screenX <= 2 * map.getWidth() / 5 + map.getWidth() / 10 - thirdLevel.getWidth() / 2 + 2*thirdLevel.getWidth()
                        && (Gdx.graphics.getHeight() - screenY) >= map.getHeight() / 2 - thirdLevel.getHeight() / 2
                        && (Gdx.graphics.getHeight() - screenY) <= map.getHeight() / 2 - firstLevel.getHeight() / 2 + 2*thirdLevel.getHeight()
                        && level.getNum() + 1 >= 3)
                    System.out.println(3);
                    //game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));

                //mouse listener for fourth level
                else if (screenX >= 3 * map.getWidth() / 5 + map.getWidth() / 10 - fourthLevel.getWidth() / 2
                        && screenX <= 3 * map.getWidth() / 5 + map.getWidth() / 10 - fourthLevel.getWidth() / 2 + 2*fourthLevel.getWidth()
                        && (Gdx.graphics.getHeight() - screenY) >= map.getHeight() / 2 - fourthLevel.getHeight() / 2
                        && (Gdx.graphics.getHeight() - screenY) <= map.getHeight() / 2 - fourthLevel.getHeight() / 2 + 2*fourthLevel.getHeight()
                        && level.getNum() + 1 >= 4)
                    System.out.println(4);
                    //game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));

                //mouse listener for fifth level
                else if (screenX >= 4 * map.getWidth() / 5 + map.getWidth() / 10 - fifthLevel.getWidth() / 2
                        && screenX <= 4 * map.getWidth() / 5 + map.getWidth() / 10 - fifthLevel.getWidth() / 2 + 2*fifthLevel.getWidth()
                        && (Gdx.graphics.getHeight() - screenY) >= map.getHeight() / 2 - fifthLevel.getHeight() / 2
                        && (Gdx.graphics.getHeight() - screenY) <= map.getHeight() / 2 - fifthLevel.getHeight() / 2 + 2*fifthLevel.getHeight()
                        && level.getNum() + 1 >= 5)
                    System.out.println(5);
                    //game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                return true;
            }
        });

    }

    @Override
    public void render(float data) {
        if (Gdx.input.isKeyPressed(Input.Keys.D) && camera.position.x < map.getWidth() - Gdx.graphics.getWidth() / 2 - 30) {
            camera.translate(moveSpeed * Gdx.graphics.getDeltaTime(), 0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && camera.position.x > Gdx.graphics.getWidth() / 2 + 30) {
            camera.translate(-moveSpeed * Gdx.graphics.getDeltaTime(), 0);
        }

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(map, 0, 0, map.getWidth(), Gdx.graphics.getHeight());

        //firstFarm
        batch.draw(firstLevel, map.getWidth() / 10 - firstLevel.getWidth() / 2, map.getHeight() / 2 - firstLevel.getHeight() / 2,
                firstLevel.getWidth() * 2, firstLevel.getHeight() * 2);
        for (int i = 0; i < numOfLevelsCompleted.ONE.getNumOfStars(); i++) {
            batch.draw(star, (float) (map.getWidth() / 10 - firstLevel.getWidth() / 2 + 20 / 1.5 + i * (20)), map.getHeight() / 2 - firstLevel.getHeight() / 2, 20, 20);
        }

        //secondFarm
        batch.draw(secondLevel, map.getWidth() / 5 + map.getWidth() / 10 - secondLevel.getWidth() / 2, map.getHeight() / 2 - secondLevel.getHeight() / 2,
                secondLevel.getWidth() * 2, secondLevel.getHeight() * 2);
        if (level.getNum() + 1 <= 2) {
            batch.draw(locked, map.getWidth() / 5 + map.getWidth() / 10 - secondLevel.getWidth() / 2 + secondLevel.getWidth() + 45,
                    map.getHeight() / 2 - secondLevel.getHeight() / 2, 50, 50);
        }
        for (int i = 0; i < numOfLevelsCompleted.TWO.getNumOfStars(); i++) {
            batch.draw(star, (float) (map.getWidth() / 5 + map.getWidth() / 10 - secondLevel.getWidth() / 2 + 20 / 1.5 + i * (20)), map.getHeight() / 2 - secondLevel.getHeight() / 2, 20, 20);
        }


        //thirdFarm
        batch.draw(thirdLevel, 2 * map.getWidth() / 5 + map.getWidth() / 10 - thirdLevel.getWidth() / 2, map.getHeight() / 2 - thirdLevel.getHeight() / 2,
                thirdLevel.getWidth() * 2, thirdLevel.getHeight() * 2);
        if (level.getNum() + 1 <= 3)
            batch.draw(locked, 2 * map.getWidth() / 5 + map.getWidth() / 10 - thirdLevel.getWidth() / 2 + thirdLevel.getWidth() + 100,
                    map.getHeight() / 2 - thirdLevel.getHeight() / 2, 50, 50);
        for (int i = 0; i < numOfLevelsCompleted.THREE.getNumOfStars(); i++) {
            batch.draw(star, (float) (2 * map.getWidth() / 5 + map.getWidth() / 10 - thirdLevel.getWidth() / 2 + 20 / 1.5 + i * (20)), map.getHeight() / 2 - thirdLevel.getHeight() / 2, 20, 20);
        }

        //fourthFarm
        batch.draw(fourthLevel, 3 * map.getWidth() / 5 + map.getWidth() / 10 - fourthLevel.getWidth() / 2, map.getHeight() / 2 - fourthLevel.getHeight() / 2,
                fourthLevel.getWidth() * 2, fourthLevel.getHeight() * 2);
        if (level.getNum() + 1 <= 4)
            batch.draw(locked, 3 * map.getWidth() / 5 + map.getWidth() / 10 - fourthLevel.getWidth() / 2 + fourthLevel.getWidth() + 100,
                    map.getHeight() / 2 - fourthLevel.getHeight() / 2, 50, 50);
        for (int i = 0; i < numOfLevelsCompleted.FOUR.getNumOfStars(); i++) {
            batch.draw(star, (float) (3 * map.getWidth() / 5 + map.getWidth() / 10 - fourthLevel.getWidth() / 2 + 20 / 1.5 + i * (20)), map.getHeight() / 2 - fourthLevel.getHeight() / 2, 20, 20);
        }


        //fifthFarm
        batch.draw(fifthLevel, 4 * map.getWidth() / 5 + map.getWidth() / 10 - fifthLevel.getWidth() / 2, map.getHeight() / 2 - fifthLevel.getHeight() / 2,
                fifthLevel.getWidth() * 2, fifthLevel.getHeight() * 2);
        if (level.getNum() + 1 <= 5)
            batch.draw(locked, 4 * map.getWidth() / 5 + map.getWidth() / 10 - fifthLevel.getWidth() / 2 + fifthLevel.getWidth() + 100,
                    map.getHeight() / 2 - fifthLevel.getHeight() / 2, 50, 50);
        for (int i = 0; i < numOfLevelsCompleted.FIVE.getNumOfStars(); i++) {
            batch.draw(star, (float) (4 * map.getWidth() / 5 + map.getWidth() / 10 - fifthLevel.getWidth() / 2 + 20 / 1.5 + i * (20)), map.getHeight() / 2 - fourthLevel.getHeight() / 2, 20, 20);
        }

        batch.end();

    }


    /**
     * sets the value of the current level to the next one
     */
    public void levelCompleted(){
        if(level.getNum() == 0)
            level = numOfLevelsCompleted.ONE;
        else if(level.getNum() == 1)
            level = numOfLevelsCompleted.TWO;
        else if(level.getNum() == 2)
            level = numOfLevelsCompleted.THREE;
        else if(level.getNum() == 3)
            level = numOfLevelsCompleted.FOUR;
        else if(level.getNum() == 4)
            level = numOfLevelsCompleted.FIVE;
    }

    @Override
    public void dispose() {
        game.music.dispose();
    }

    enum numOfLevelsCompleted {
        ZERO(0, 3), ONE(1, 2), TWO(2, 1), THREE(3, 0), FOUR(4, 0), FIVE(5, 0);
        private int num;
        private int numOfStars;

        numOfLevelsCompleted(int num, int numOfStars) {
            this.num = num;
            this.numOfStars = numOfStars;
        }

        public int getNumOfStars() {
            return numOfStars;
        }

        public void setNumOfStars(int numOfStars) {
            this.numOfStars = numOfStars;
        }

        public int getNum() {
            return num;
        }
    }

}


