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
    Texture locked;

    float rectWidth;
    float rectHeight;
    float moveSpeed = 300;

    public enum numOfLevelsCompleted {
        ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4);
        private int num;

        numOfLevelsCompleted(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }
    }

    private numOfLevelsCompleted level;

    LevelsScreen(Foresty game) {
        this.game = game;
        level = numOfLevelsCompleted.FOUR;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        camera.update();

        rectWidth = Gdx.graphics.getWidth();
        rectHeight = Gdx.graphics.getHeight();
        map = new Texture(Gdx.files.internal("MMap.png"));
        send = new Texture(Gdx.files.internal("send.png"));
        firstLevel = new Texture(Gdx.files.internal("1level.png"));
        secondLevel = new Texture(Gdx.files.internal("2level.png"));
        thirdLevel = new Texture(Gdx.files.internal("3level.png"));
        fourthLevel = new Texture(Gdx.files.internal("4level.png"));
        fifthLevel = new Texture(Gdx.files.internal("5level.png"));
        locked = new Texture("locked.png");
        batch = new SpriteBatch();

        final HashMap<Animal.TYPES, Integer> levelFirstAnimals = new HashMap<>();
        levelFirstAnimals.put(Animal.TYPES.TYPE_ONE, 1);
        levelFirstAnimals.put(Animal.TYPES.TYPE_TWO, 2);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (screenX >= map.getWidth() / 2 + 105
                        && screenX <= map.getWidth() / 2 + 105 + firstLevel.getWidth()
                        && screenY >= map.getHeight() / 10 + 45
                        && screenY <= map.getHeight() / 10 + 45 + firstLevel.getHeight())
                    game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                else if (screenX >= map.getWidth() / 2 + 80
                        && screenX <= map.getWidth() / 2 + 80 + secondLevel.getWidth()
                        && screenY >= map.getHeight() / 10 + 45
                        && screenY <= map.getHeight() / 10 + 45 + secondLevel.getHeight()
                        && level.getNum() + 1 >= 2)
                    //TODO: set correct parameters to new screen in all cases below.
                    game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                else if (screenX >= map.getWidth() / 2 + 50
                        && screenX <= map.getWidth() / 2 + 50 + thirdLevel.getWidth()
                        && screenY >= 2 * map.getHeight() / 5 + map.getHeight() / 10 + 100
                        && screenY <= 2 * map.getHeight() / 5 + map.getHeight() / 10 + 100 + thirdLevel.getHeight()
                        && level.getNum() + 1 >= 3)
                    game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                else if (screenX >= map.getWidth() / 2 + 20
                        && screenX <= map.getWidth() / 2 + 20 + fourthLevel.getWidth()
                        && screenY >= 3 * map.getHeight() / 5 + map.getHeight() / 10
                        && screenY <= 3 * map.getHeight() / 5 + map.getHeight() / 10 + fourthLevel.getHeight()
                        && level.getNum() + 1 >= 4)
                    game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                else if (screenX >= map.getWidth() / 2 + secondLevel.getWidth() - 40
                        && screenX <= map.getWidth() / 2 + secondLevel.getWidth() - 40 + fifthLevel.getWidth()
                        && screenY >= 4 * map.getHeight() / 5 + map.getHeight() / 10
                        && screenY <= 4 * map.getHeight() / 5 + map.getHeight() / 10 + fifthLevel.getHeight()
                        && level.getNum() + 1 >= 5)
                    game.setScreen(new GameScreen(game, levelFirstAnimals, 15, 30, 45, 75));
                return true;
            }
        });

    }

    @Override
    public void render(float data) {
        if (Gdx.input.isKeyPressed(Input.Keys.W) && camera.position.y < map.getHeight() - Gdx.graphics.getHeight() / 2) {
            camera.translate(0, moveSpeed * Gdx.graphics.getDeltaTime());
        } else if (Gdx.input.isKeyPressed(Input.Keys.S) && camera.position.y > Gdx.graphics.getHeight() / 2) {
            camera.translate(0, -moveSpeed * Gdx.graphics.getDeltaTime());
        }

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(map, 0, 0, Gdx.graphics.getWidth(), map.getHeight());
        batch.draw(firstLevel, map.getWidth() / 2 + 105, map.getHeight() / 10 + 45);
        batch.draw(secondLevel, map.getWidth() / 2 + 80, map.getHeight() / 5 + map.getHeight() / 10);
        if (level.getNum() + 1 <= 2)
            batch.draw(locked, map.getWidth() / 2 + secondLevel.getWidth() + 45, map.getHeight() / 5 + map.getHeight() / 10, 50, 50);
        batch.draw(thirdLevel, map.getWidth() / 2 + 50, 2 * map.getHeight() / 5 + map.getHeight() / 10 + 100);
        if (level.getNum() + 1 <= 3)
            batch.draw(locked, map.getWidth() / 2 + secondLevel.getWidth() + 10, 2 * map.getHeight() / 5 + map.getHeight() / 10 + 100, 50, 50);
        batch.draw(fourthLevel, map.getWidth() / 2 + 20, 3 * map.getHeight() / 5 + map.getHeight() / 10);
        if (level.getNum() + 1 <= 4)
            batch.draw(locked, map.getWidth() / 2 + secondLevel.getWidth() - 20, 3 * map.getHeight() / 5 + map.getHeight() / 10, 50, 50);
        batch.draw(fifthLevel, map.getWidth() / 2, 4 * map.getHeight() / 5 + map.getHeight() / 10);
        if (level.getNum() + 1 <= 5)
            batch.draw(locked, map.getWidth() / 2 + secondLevel.getWidth() - 40, 4 * map.getHeight() / 5 + map.getHeight() / 10, 50, 50);
        batch.end();

    }

    @Override
    public void dispose() {
    }

}
