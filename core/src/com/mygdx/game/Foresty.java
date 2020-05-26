package com.mygdx.game;

import com.badlogic.gdx.Game;

public class Foresty extends Game {

    public LevelsScreen levelsScreen;

    @Override
    public void create() {
        levelsScreen = new LevelsScreen(this);
        setScreen(new TitleScreen(this));
    }
}
