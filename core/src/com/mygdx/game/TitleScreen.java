package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TitleScreen extends ScreenAdapter {

    private static final int FRAME_COLS = 4, FRAME_ROWS = 1;
    private final static int STARTING_X = 50;
    private final static int STARTING_Y = 50;
    Animation<TextureRegion> rabbitAnimation;
    Texture rabbitTexture;
    TextureRegion reg;
    float stateTime;
    private Foresty game;
    private SpriteBatch batch;
    private Texture startTexture;
    private Texture startTextureWithLabel;
    private Texture backgroundTexture;
    private Texture rabbit;
    private boolean startIsBig = false;
    private boolean rabbitIsDrawn = false;
    private int numOfRenders = 0;
    private int rabbitX, rabbitY = 10;
    private int rabbitSpeed = 5;

    public TitleScreen(Foresty game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (screenX >= Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 3
                        && screenX <= Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 3 + Gdx.graphics.getWidth() / 1.5
                        && (Gdx.graphics.getHeight() - screenY) >= Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 11 + Gdx.graphics.getHeight() / 6
                        && (Gdx.graphics.getHeight() - screenY) <= Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 11 + Gdx.graphics.getHeight() / 6 + Gdx.graphics.getHeight() / 5)
                    game.setScreen(game.levelsScreen);
                return true;
            }

            @Override
            public boolean keyTyped(char character) {
                if(character == ' ')
                    game.setScreen(game.levelsScreen);
                return true;
            }
        });


        game.music = Gdx.audio.newMusic(Gdx.files.internal("TitleScreenMusic.mp3"));
        game.music.setLooping(true);
        game.music.play();

        rabbitTexture = new Texture(Gdx.files.internal("Rabbit.png"));

        TextureRegion[][] tmp = TextureRegion.split(rabbitTexture,
                rabbitTexture.getWidth() / FRAME_COLS,
                rabbitTexture.getHeight() / FRAME_ROWS);

        TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        rabbitAnimation = new Animation<>(0.04f, walkFrames);
        stateTime = 0f;

        batch = new SpriteBatch();
        startTexture = new Texture(Gdx.files.internal("start.png"));
        startTextureWithLabel = new Texture(Gdx.files.internal("startWithLabel.png"));
        backgroundTexture = new Texture(Gdx.files.internal("titleScreenBackground.jpg"));
        rabbit = new Texture(Gdx.files.internal("rabbit.gif"));
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();
        reg = rabbitAnimation.getKeyFrame(stateTime, true);


        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        //background
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (numOfRenders == 350) {
            numOfRenders = 0;
            rabbitIsDrawn = false;
            rabbitX = 10;
            rabbitY = 10;
        }
        //small start label
        if (numOfRenders % 50 <= 25) {
            if (stateTime >= 2.5 && stateTime <= 50)
                batch.draw(startTextureWithLabel, Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 11 + Gdx.graphics.getHeight() / 6, (float) (Gdx.graphics.getWidth() / 1.5), Gdx.graphics.getHeight() / 5);
            else
                batch.draw(startTexture, Gdx.graphics.getWidth() / 2 - Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 11 + Gdx.graphics.getHeight() / 6, (float) (Gdx.graphics.getWidth() / 1.5), Gdx.graphics.getHeight() / 5);

        }
        //big start label
        else if (numOfRenders % 50 >= 25) {
            if (stateTime >= 2.5 && stateTime <= 50)
                batch.draw(startTextureWithLabel, (float) (Gdx.graphics.getWidth() / 2 - (Gdx.graphics.getWidth() / 3.4)), Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 13 + Gdx.graphics.getHeight() / 6, (float) (Gdx.graphics.getWidth() / 1.7), Gdx.graphics.getHeight() / 6);
            else
                batch.draw(startTexture, (float) (Gdx.graphics.getWidth() / 2 - (Gdx.graphics.getWidth() / 3.4)), Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 13 + Gdx.graphics.getHeight() / 6, (float) (Gdx.graphics.getWidth() / 1.7), Gdx.graphics.getHeight() / 6);
        }
        //rabbit appears
        if (numOfRenders == 30)
            rabbitIsDrawn = true;
        //rabbit moves
        if (rabbitIsDrawn) {
            batch.draw(reg, rabbitX, rabbitY, Gdx.graphics.getWidth() / 25, Gdx.graphics.getHeight() / 25);
            rabbitX += rabbitSpeed;
        }
        batch.end();

        startIsBig = !startIsBig;
        numOfRenders++;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        batch.dispose();
        startTexture.dispose();
    }

}
