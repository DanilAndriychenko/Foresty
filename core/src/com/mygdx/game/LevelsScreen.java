package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import java.util.Scanner;

import java.io.*;
import java.util.HashMap;

public class LevelsScreen extends ScreenAdapter {

    private static final float SCREEN_WIDTH = Gdx.graphics.getWidth(),
            SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private static final OrthographicCamera camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static final Texture mapBackground = new Texture(Gdx.files.internal("map.png")),
            send = new Texture(Gdx.files.internal("send.png")),
            firstFarmTexture = new Texture(Gdx.files.internal("farm1.png")),
            secondFarmTexture = new Texture(Gdx.files.internal("farm2.png")),
            thirdFarmTexture = new Texture(Gdx.files.internal("farm3.png")),
            fourthFarmTexture = new Texture(Gdx.files.internal("farm4.png")),
            fifthFarmTexture = new Texture(Gdx.files.internal("farm5.png")),
            star = new Texture("star.png"),
            locked = new Texture("locked.png");
    private static final HashMap<Animal.TYPES, Integer> lvlFirstAnimalsHashMap = new HashMap<>(),
            lvlSecondAnimalsHashMap = new HashMap<>(),
            lvlThirdAnimalsHashMap = new HashMap<>(),
            lvlFourthAnimalsHashMap = new HashMap<>(),
            lvlFifthAnimalsHashMap = new HashMap<>();
    private static final float INCREASE_FARM_SIZE_COEFFICIENT = 2;
    private static final Rectangle rectFirstFarm = getFarmRect(firstFarmTexture, 1),
            rectSecondFarm = getFarmRect(secondFarmTexture, 2),
            rectThirdFarm = getFarmRect(thirdFarmTexture, 3),
            rectFourthFarm = getFarmRect(fourthFarmTexture, 4),
            rectFifthFarm = getFarmRect(fifthFarmTexture, 5);
    private static Foresty game;
    private static LevelsCompleted level;
    private float cameraSpeed = 600;
    private float cameraX = 0;

    static {
        // Add animals to levels hashMaps.
        lvlFirstAnimalsHashMap.put(Animal.TYPES.SHEEP, 1);

        lvlSecondAnimalsHashMap.put(Animal.TYPES.SHEEP, 1);
        lvlSecondAnimalsHashMap.put(Animal.TYPES.DOG, 1);

        lvlThirdAnimalsHashMap.put(Animal.TYPES.SHEEP, 1);
        lvlThirdAnimalsHashMap.put(Animal.TYPES.DOG, 1);
        lvlThirdAnimalsHashMap.put(Animal.TYPES.HORSE, 1);

        lvlFourthAnimalsHashMap.put(Animal.TYPES.SHEEP, 1);
        lvlFourthAnimalsHashMap.put(Animal.TYPES.DOG, 1);
        lvlFourthAnimalsHashMap.put(Animal.TYPES.HORSE, 1);
        lvlFourthAnimalsHashMap.put(Animal.TYPES.GOAT, 1);

        lvlFifthAnimalsHashMap.put(Animal.TYPES.SHEEP, 1);
        lvlFifthAnimalsHashMap.put(Animal.TYPES.DOG, 1);
        lvlFifthAnimalsHashMap.put(Animal.TYPES.HORSE, 1);
        lvlFifthAnimalsHashMap.put(Animal.TYPES.GOAT, 1);
        lvlFifthAnimalsHashMap.put(Animal.TYPES.GOAT_BABY, 1);
    }

    LevelsScreen(Foresty game) {
        this.game = game;
        level = LevelsCompleted.ONE;
    }

    LevelsScreen(Foresty game, LevelsCompleted level){
        this.game = game;
        this.level = level;
    }

    private static Rectangle getFarmRect(Texture texture, int numInRow) {
        System.out.println(texture.getWidth() + ", " + texture.getHeight());
        return new Rectangle(mapBackground.getWidth() * ((numInRow * 2 - 1) / 10f) - texture.getWidth() / 2, mapBackground.getHeight() / 2 - texture.getHeight() / 2,
                texture.getWidth() * INCREASE_FARM_SIZE_COEFFICIENT, texture.getHeight() * INCREASE_FARM_SIZE_COEFFICIENT);
    }

    private void handleUsersClick(int screenX, int screenY) {
        if (rectFirstFarm.contains(screenX+cameraX, screenY)){
            game.setScreen(new GameScreen(game, lvlFirstAnimalsHashMap, 45, 30, 15, 75, LevelsCompleted.ONE));
            cameraX=0;
        }else if (rectSecondFarm.contains(screenX+cameraX, screenY) && level.getNum() + 1 >= 2){
            game.setScreen(new GameScreen(game, lvlSecondAnimalsHashMap, 45, 30, 15, 75, LevelsCompleted.TWO));
            cameraX=0;
        }else if (rectThirdFarm.contains(screenX+cameraX, screenY) && level.getNum() + 1 >= 3){
            game.setScreen(new GameScreen(game, lvlThirdAnimalsHashMap, 45, 30, 15, 75, LevelsCompleted.THREE));
            cameraX=0;
        }else if (rectFourthFarm.contains(screenX+cameraX, screenY) && level.getNum() + 1 >= 4){
            game.setScreen(new GameScreen(game, lvlFourthAnimalsHashMap, 45, 30, 15, 75, LevelsCompleted.FOUR));
            cameraX=0;
        }else if (rectFifthFarm.contains(screenX+cameraX, screenY) && level.getNum() + 1 >= 5){
            game.setScreen(new GameScreen(game, lvlFifthAnimalsHashMap, 45, 30, 15, 75, LevelsCompleted.FIVE));
            cameraX=0;
        }
        System.out.println("screenX: " + screenX + ", screenY: " + screenY + "cameraX: " + cameraX + ", cameraX+screenX: " + cameraX+screenX);
    }

    @Override
    public void show() {
        // Tune camera.
        camera.position.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
        camera.update();

        //Launch level if user clicks of one of the farms.
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                handleUsersClick(screenX, screenY);
                return true;
            }
        });

    }

    @Override
    public void render(float data) {
        if (Gdx.input.isKeyPressed(Input.Keys.D) && camera.position.x < mapBackground.getWidth() - Gdx.graphics.getWidth() / 2) {
            camera.translate(cameraSpeed * Gdx.graphics.getDeltaTime(), 0);
            cameraX+=cameraSpeed*Gdx.graphics.getDeltaTime();
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && camera.position.x > Gdx.graphics.getWidth() / 2) {
            camera.translate(-cameraSpeed * Gdx.graphics.getDeltaTime(), 0);
            cameraX-=cameraSpeed*Gdx.graphics.getDeltaTime();
        }

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined);

        game.spriteBatch.begin();

        // Draw mapBackground
        game.spriteBatch.draw(mapBackground, 0, 0, mapBackground.getWidth(), SCREEN_HEIGHT);

        //firstFarm
        game.spriteBatch.draw(firstFarmTexture, rectFirstFarm.x, rectFirstFarm.y, rectFirstFarm.width, rectFirstFarm.height);
        for (int i = 0; i < LevelsCompleted.ONE.getNumOfStars(); i++) {
            game.spriteBatch.draw(star, (float) (mapBackground.getWidth() / 10 - firstFarmTexture.getWidth() / 2 + 20 / 1.5 + i * (20)), mapBackground.getHeight() / 2 - firstFarmTexture.getHeight() / 2, 20, 20);
        }

        //secondFarm
        game.spriteBatch.draw(secondFarmTexture, rectSecondFarm.x, rectSecondFarm.y, rectSecondFarm.width, rectSecondFarm.height);
        if (level.getNum() + 1 <= 2) {
            game.spriteBatch.draw(locked, mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - secondFarmTexture.getWidth() / 2 + secondFarmTexture.getWidth() + 45,
                    mapBackground.getHeight() / 2 - secondFarmTexture.getHeight() / 2, 50, 50);
        }
        for (int i = 0; i < LevelsCompleted.TWO.getNumOfStars(); i++) {
            game.spriteBatch.draw(star, (float) (mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - secondFarmTexture.getWidth() / 2 + 20 / 1.5 + i * (20)), mapBackground.getHeight() / 2 - secondFarmTexture.getHeight() / 2, 20, 20);
        }


        //thirdFarm
        game.spriteBatch.draw(thirdFarmTexture, rectThirdFarm.x, rectThirdFarm.y, rectThirdFarm.width, rectThirdFarm.height);
        if (level.getNum() + 1 <= 3)
            game.spriteBatch.draw(locked, 2 * mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - thirdFarmTexture.getWidth() / 2 + thirdFarmTexture.getWidth() + 100,
                    mapBackground.getHeight() / 2 - thirdFarmTexture.getHeight() / 2, 50, 50);
        for (int i = 0; i < LevelsCompleted.THREE.getNumOfStars(); i++) {
            game.spriteBatch.draw(star, (float) (2 * mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - thirdFarmTexture.getWidth() / 2 + 20 / 1.5 + i * (20)), mapBackground.getHeight() / 2 - thirdFarmTexture.getHeight() / 2, 20, 20);
        }

        //fourthFarm
        game.spriteBatch.draw(fourthFarmTexture, rectFourthFarm.x, rectFourthFarm.y, rectFourthFarm.width, rectFourthFarm.height);
        if (level.getNum() + 1 <= 4)
            game.spriteBatch.draw(locked, 3 * mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - fourthFarmTexture.getWidth() / 2 + fourthFarmTexture.getWidth() + 100,
                    mapBackground.getHeight() / 2 - fourthFarmTexture.getHeight() / 2, 50, 50);
        for (int i = 0; i < LevelsCompleted.FOUR.getNumOfStars(); i++) {
            game.spriteBatch.draw(star, (float) (3 * mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - fourthFarmTexture.getWidth() / 2 + 20 / 1.5 + i * (20)), mapBackground.getHeight() / 2 - fourthFarmTexture.getHeight() / 2, 20, 20);
        }


        //fifthFarm
        game.spriteBatch.draw(fifthFarmTexture, rectFifthFarm.x, rectFifthFarm.y, rectFifthFarm.width, rectFifthFarm.height);
        if (level.getNum() + 1 <= 5)
            game.spriteBatch.draw(locked, 4 * mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - fifthFarmTexture.getWidth() / 2 + fifthFarmTexture.getWidth() + 100,
                    mapBackground.getHeight() / 2 - fifthFarmTexture.getHeight() / 2, 50, 50);
        for (int i = 0; i < LevelsCompleted.FIVE.getNumOfStars(); i++) {
            game.spriteBatch.draw(star, (float) (4 * mapBackground.getWidth() / 5 + mapBackground.getWidth() / 10 - fifthFarmTexture.getWidth() / 2 + 20 / 1.5 + i * (20)), mapBackground.getHeight() / 2 - fourthFarmTexture.getHeight() / 2, 20, 20);
        }

        game.spriteBatch.end();

    }

    /**
     * Sets the value of the current level to the next one.
     */
    public void levelCompleted() {
        if (level.getNum() == 0)
            level = LevelsCompleted.ONE;
        else if (level.getNum() == 1)
            level = LevelsCompleted.TWO;
        else if (level.getNum() == 2)
            level = LevelsCompleted.THREE;
        else if (level.getNum() == 3)
            level = LevelsCompleted.FOUR;
        else if (level.getNum() == 4)
            level = LevelsCompleted.FIVE;
        saveLevelInfoToFile();
    }

    private void saveLevelInfoToFile(){
        File file = new File("core/assets/Savings/LevelsData");
        FileWriter fr = null;
        String data = "";
        data+=LevelsCompleted.ONE.getNumOfStars() + "$" +
              LevelsCompleted.TWO.getNumOfStars() + "$" +
              LevelsCompleted.THREE.getNumOfStars() + "$" +
              LevelsCompleted.FOUR.getNumOfStars() + "$" +
              LevelsCompleted.FIVE.getNumOfStars() + "$" +
              level.getNum();
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSavedInfo() {
        //boolean
        File myObj = new File("core/assets/Savings/LevelsData");
        Scanner myReader = null;
        String data = "";
        try {
            myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        myReader.close();
        String[] info = data.split("$");
        if(info.length >= 3){
            System.out.println("Inside");
            LevelsCompleted.ONE.setNumOfStars(Integer.parseInt(info[0]));
            LevelsCompleted.TWO.setNumOfStars(Integer.parseInt(info[1]));
            LevelsCompleted.THREE.setNumOfStars(Integer.parseInt(info[2]));
            LevelsCompleted.FOUR.setNumOfStars(Integer.parseInt(info[3]));
            LevelsCompleted.FIVE.setNumOfStars(Integer.parseInt(info[4]));
            if(info[5] == "1")
                level = LevelsCompleted.ONE;
            if(info[5] == "2")
                level = LevelsCompleted.TWO;
            if(info[5] == "3")
                level = LevelsCompleted.THREE;
            if(info[5] == "4")
                level = LevelsCompleted.FOUR;
            if(info[5] == "5")
                level = LevelsCompleted.FIVE;
    }
}

    enum LevelsCompleted {
        ONE(1, 0), TWO(2, 0), THREE(3, 0), FOUR(4, 0), FIVE(5, 0);
        private int num;
        private int numOfStars;

        LevelsCompleted(int num, int numOfStars) {
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


