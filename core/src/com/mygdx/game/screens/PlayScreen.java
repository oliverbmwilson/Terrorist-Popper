package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;
import com.mygdx.game.SplatterEffect;
import com.mygdx.game.gameObjects.Ground;
import com.mygdx.game.gameObjects.GroundSensor;
import com.mygdx.game.gameObjects.HostagePlay;
import com.mygdx.game.gameObjects.ObjectManager;
import com.mygdx.game.gameObjects.TerroristBlackPlay;
import com.mygdx.game.gameObjects.TerroristBombPlay;
import com.mygdx.game.gameObjects.TerroristGreyPlay;

import java.util.Random;

public class PlayScreen implements Screen {

    private final Application app;
    public PlayScreenHud hud;
    public SplatterEffect splatterEffect;
    private Texture backgroundImage;
    private Texture foregroundImage;
    private Sprite foreground;
    private Sprite background;
    public Music gameMusic;
    public Sound gunShot;
    public ObjectManager objectManager;

    private InputAdapter inputAdapter;
    private InputMultiplexer multiplexer;

    //Required to deal with Box2D touches
    private QueryCallback touchQueryCallback;
    private Body bodyThatWasTouched;
    private Vector3 touchPoint;

    public boolean gameOver;
    private float interval;
    private Random random;

    public PlayScreen(final Application app) {
        this.app = app;
        hud = new PlayScreenHud(app);
        splatterEffect = new SplatterEffect(app);
        objectManager = new ObjectManager(app);
        random = new Random();
        interval = 0;

        //This input adapter is called upon when the hud did not take care of a touch event
        inputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                if(!hud.paused) nonMenuTouchDown(x, y, pointer, button);
                return false;
            }
        };

        //Multiplexr to pioritise input to the hud
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hud.stage);
        multiplexer.addProcessor(inputAdapter);

        //This query is called to search for the physics body that the user has touched
        touchQueryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if(fixture.testPoint(touchPoint.x, touchPoint.y)) {
                    bodyThatWasTouched = fixture.getBody();
                    return false;
                } else return true;
            }
        };

        touchPoint = new Vector3();
    }

    //Unpauses the game and sets up the assets
    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);

        gameOver = false;

        backgroundImage = app.assets.get("Background1080.png", Texture.class);
        background = new Sprite(backgroundImage);

        gameMusic = app.assets.get("gameMusic.mp3", Music.class);
        gunShot = app.assets.get("gunShot.mp3", Sound.class);

        foregroundImage = app.assets.get("Foreground1080.png", Texture.class);
        foreground = new Sprite(foregroundImage);

        if(!hud.paused) {
            objectManager.addGameObject(new Ground(app));
            objectManager.addGameObject(new GroundSensor(app, 300));
        } else {
            hud.paused = false;
            objectManager.unpause();
        }

        gameMusic.play();

    }

    //Computes one step of the game loop update phase. Here the user input is updated, the gameover
    // //timer is incremented and checked (if the game isn't paused), the interval timer is
    // incremented and used to create new game objects at a semi random interval. The physics world
    // is checked, the the game objects are updated and removed if need be. Any blood splatter
    // animation are then incremented
    public void update(float delta) {
        handleInput();
        hud.update(delta);

        if(!hud.paused) {
            interval += delta;
            hud.time -= delta;
        }

        if(hud.time <= 0) {
            gameOver();
        }

        if(!hud.paused) createGameObjects();

        app.world.step(1 / 60f, 6, 2);

        objectManager.update(delta);
        splatterEffect.update(delta);
    }

    //After the update, the world is rendered. The cameras are projected onto the world, then the
    //background is drawn, followed by the game objects, then the foreground, then the blood splatter
    // and finally, the hud.
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        app.batch.setProjectionMatrix(app.camera.combined);
        app.shapeRenderer.setProjectionMatrix(app.camera.combined);

        app.batch.begin();
        background.draw(app.batch);
        app.batch.end();

        objectManager.drawObjects();

        app.batch.begin();
        foreground.draw(app.batch);;
        splatterEffect.render(app.batch);
        app.batch.end();

        hud.render();

        if(app.DEUBG) {
            app.dbr.render(app.world, app.camera.combined.scl(Constants.PPM));
            app.fps.log();
        }
    }

    //Deals with different sized screens
    @Override
    public void resize(int width, int height) {
        hud.resize(app.viewport.getScreenWidth(), app.viewport.getScreenHeight());
        app.viewport.update(width, height);
    }

    //Music is paused and the game objects are not updated while the game is paused
    @Override
    public void pause() {
        gameMusic.pause();
        objectManager.pause();
    }


    //Music and object updates resume when the game is unpaused
    @Override
    public void resume() {
        gameMusic.play();
        objectManager.unpause();
    }

    //Game objects are not updated when the screen is hidden and the music is paused
    @Override
    public void hide() {
        gameMusic.pause();
        objectManager.pause();
    }

    @Override
    public void dispose() {
        hud.dispose();
        if(gameMusic != null) gameMusic.dispose();
        if( gunShot != null) gunShot.dispose();
    }

    //This method is called whenever the hud does not handle a touch event. It unprojects the camera
    //and the view port so that the correct touch coordinates can be used relative to the game world
    //It the queries all the physics objects in the world to see if they have been touched. The body's
    //game object is then updated with this event
    public void nonMenuTouchDown(int x, int y, int pointer, int button) {
        gunShot.play();
        touchPoint.set(x ,y, 0);
        app.camera.unproject(touchPoint, app.viewport.getScreenX(), app.viewport.getScreenY(),
                app.viewport.getScreenWidth(), app.viewport.getScreenHeight());
        touchPoint.x = touchPoint.x / Constants.PPM;
        touchPoint.y = touchPoint.y / Constants.PPM;

        bodyThatWasTouched = null;
        app.world.QueryAABB(touchQueryCallback, Math.min(touchPoint.x - 5f, touchPoint.x + 5f), Math.min(touchPoint.y - 5f, touchPoint.y + 5f), Math.max(touchPoint.x - 5f, touchPoint.x + 5f), Math.max(touchPoint.y - 5f, touchPoint.y + 5f));
        if(bodyThatWasTouched != null) {
            objectManager.bodyTouched(bodyThatWasTouched);
        }
    }

    //Resets the game state for a new game
    public void newGame() {
        if(gameMusic != null) gameMusic.stop();
        if(objectManager != null) objectManager.dispose();
        hud.paused = false;
        hud.time = 120;
        hud.score = 0;
        hud.pauseLabel.setText("");
        interval = .25f;
    }

    //Uses the interval (updated every loop) and the number of game objects in the world
    // to determine when another game object is created. A random type is then selected and
    //added into the world
    public void createGameObjects() {
        if (interval < .25f || objectManager.gameObjects.size() > 20) return;

        int difficulty = app.mainMenuScreen.settingsHud.difficulty;

        int create = random.nextInt(2);

        if(create == 0) {
            interval = 0f;
            return;
        }

        if(difficulty == 10) {

            int objectType = random.nextInt(6);
            int position = random.nextInt(app.V_WIDTH);

            switch (objectType) {
                case 0:
                    objectManager.addGameObject(new TerroristGreyPlay(app, position, 200, -1, -4));
                    break;
                case 2:
                    objectManager.addGameObject(new TerroristBlackPlay(app, position, 200, -2, -6));
                    break;
                case 3:
                    objectManager.addGameObject(new HostagePlay(app, position, 200, -1, -4));
                    break;
                default :
                    objectManager.addGameObject(new TerroristBombPlay(app, position, 200, -1, -4));
                    break;
            }
        } else {

            int objectType = random.nextInt(4);
            int position = random.nextInt(app.V_WIDTH);

            switch (objectType) {
                case 0:
                    objectManager.addGameObject(new TerroristGreyPlay(app, position, 200, -1 + difficulty, -4 + difficulty));
                    break;
                case 1:
                    objectManager.addGameObject(new TerroristBombPlay(app, position, 200, -1 + difficulty, -4 + difficulty));
                    break;
                case 2:
                    objectManager.addGameObject(new TerroristBlackPlay(app, position, 200, -2 + difficulty, -6 + difficulty));
                    break;
                case 3:
                    objectManager.addGameObject(new HostagePlay(app, position, 200, -1 + difficulty, -4 + difficulty));
                    break;
            }
        }

        interval = 0f;
    }

    //Stops the game and recors the score
    public void gameOver() {
        gameOver = true;
        hud.paused = true;
        hud.pauseLabel.setText("GameOver");
        app.playScreen.objectManager.pause();
        if(app.mainMenuScreen.highScoreHud.score < hud.score) {
            app.mainMenuScreen.highScoreHud.highScore.setText("High score: " + hud.score);
            app.mainMenuScreen.highScoreHud.score = hud.score;
            app.prefs.putInteger("score", hud.score);
            app.prefs.flush();
        }
    }

    //Handles the back button
    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) && app.currentScreen == 'p') {
            if (!hud.paused) hud.paused = true;
            app.playScreen.gameMusic.pause();
            app.currentScreen = 'm';
            app.setScreen(app.mainMenuScreen);
        }
    }
}
