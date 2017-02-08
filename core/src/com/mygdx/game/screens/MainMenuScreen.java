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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;
import com.mygdx.game.gameObjects.Ground;
import com.mygdx.game.gameObjects.HostageMenu;
import com.mygdx.game.gameObjects.ObjectManager;
import com.mygdx.game.gameObjects.TerroristBlackMenu;
import com.mygdx.game.gameObjects.TerroristGreyMenu;

public class MainMenuScreen implements Screen {

    private final Application app;

    public Stage stage;

    //layouts for menu screens
    public MainMenuHud hud;
    public HighScoreHud highScoreHud;
    public SettingsHud settingsHud;

    private Sprite background;
    private Music gameMusic;
    private Sound gunShot;

    private ObjectManager objectManager;

    //Registers user input. The multiplexer
    //allows for user input to be layered so that
    //the hud buttons take priority
    private InputAdapter inputAdapter;
    public InputMultiplexer mainMultiplexer;
    public InputMultiplexer highScoreMultiplexer;
    public InputMultiplexer settingsMultiplexer;

    //Required to deal with Box2D physics responses
    //to user input
    private QueryCallback touchQueryCallback;
    private Body bodyThatWasTouched;
    private Vector3 touchPoint;

    public boolean cutsceneShown;

    public MainMenuScreen(final Application app) {
        this.app = app;

        hud = new MainMenuHud(app);
        highScoreHud = new HighScoreHud(app);
        settingsHud = new SettingsHud(app);

        stage = hud.stage;

        objectManager = new ObjectManager(app);

        //Only need to implement the input adapter's touch down
        //method for this game. Other methods can be implemented later
        //if more user input is required
        inputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                nonMenuTouchDown(x, y, pointer, button);
                return false;
            }
        };

        mainMultiplexer = new InputMultiplexer();
        mainMultiplexer.addProcessor(hud.stage);
        mainMultiplexer.addProcessor(inputAdapter);

        highScoreMultiplexer = new InputMultiplexer();
        highScoreMultiplexer.addProcessor(highScoreHud.stage);
        highScoreMultiplexer.addProcessor(inputAdapter);

        settingsMultiplexer = new InputMultiplexer();
        settingsMultiplexer.addProcessor(settingsHud.stage);
        settingsMultiplexer.addProcessor(inputAdapter);

        //The QueryCallback class takes care of the user input for Box2D
        //It checks over each fixture of each body in the physics world to
        //see if the user has touched it
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

        cutsceneShown = false;
    }

    //Updates all the objects in the menu,
    //updates the camera, updates the hud.
    public void update(float delta) {
        handleInput();

        app.world.step(1/60f, 6, 2);

        objectManager.update(delta);

        app.camera.update();
        if(!cutsceneShown) cameraZoom(delta);

        stage.act(delta);

    }

    //Clears the screen, projects the camera onto the screen,
    //draws the background, then the objects in the world,
    //then the hud and then the degug outlines
    @Override
    public void render(float delta) {
       // Gdx.gl.glClearColor(1, 160 / 255f, 72 / 255f, 1);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        app.batch.setProjectionMatrix(app.camera.combined);
        app.shapeRenderer.setProjectionMatrix(app.camera.combined);

        app.batch.begin();
        background.draw(app.batch);
        app.batch.end();

        objectManager.drawObjects();

        stage.draw();

        if(app.DEUBG) {
            app.dbr.render(app.world, app.camera.combined.scl(Constants.PPM));
            app.fps.log();
        }

    }

    /*
        This method is called every time the main menu is shown. It initialises all of the assets
        that are required for the screen, adds the screen's objects to it's object manager, adjusts
        the camera depending on whether or not the first cutscene has been shown, and tells the hud
        whether or not to display the resume button
     */
    @Override
    public void show() {
        //Take control of the back button and set up input processors
        Gdx.input.setInputProcessor(mainMultiplexer);

        background = new Sprite(app.assets.get("BackgroundMenu1080.png", Texture.class));

        gunShot = app.assets.get("gunShot.mp3", Sound.class);
        gameMusic = app.assets.get("menuMusic.mp3", Music.class);
        gameMusic.play();

        objectManager.addGameObject(new TerroristGreyMenu(app, app.V_WIDTH / 2, app.V_HEIGHT / 2 - 100));
        objectManager.addGameObject(new TerroristBlackMenu(app, app.V_WIDTH / 2 + 550, app.V_HEIGHT / 2 - 100));
        objectManager.addGameObject(new HostageMenu(app, app.V_WIDTH / 2 + 300, app.V_HEIGHT / 2 - 200));
        objectManager.addGameObject(new Ground(app));

        //Zooms the camera in if the game has just started up
        if (!cutsceneShown) {
            app.camera.position.set(app.V_WIDTH / 2, app.V_HEIGHT / 2 + 100, 0);
            app.camera.zoom = .2f;
            app.camera.update();
        }

        if(app.playScreen.hud.paused && !app.playScreen.gameOver) hud.resume.setVisible(true);
        else hud.resume.setVisible(false);
    }

    //Deals with differnt screen sizes
    @Override
    public void resize(int width, int height) {
        hud.resize(app.viewport.getScreenWidth(), app.viewport.getScreenHeight());
        highScoreHud.resize(app.viewport.getScreenWidth(), app.viewport.getScreenHeight());
        app.viewport.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    //Removes the main menu bodies so they aren't processed by the physics
    //engine when the gameplay starts
    @Override
    public void hide() {
        if(objectManager != null) objectManager.dispose();
        gameMusic.stop();
    }

    //cleans up unused assets
    @Override
    public void dispose() {
        hud.dispose();
        highScoreHud.dispose();
        if(gameMusic != null) gameMusic.dispose();
        if(gunShot != null) gunShot.dispose();
    }

    //This method takes care of the cutscene. It updates the camera depending on the timing of the
    //music
    private void cameraZoom(float delta) {
        float effectiveViewportWidth = app.camera.viewportWidth * app.camera.zoom;
        float effectiveViewportHeight = app.camera.viewportHeight * app.camera.zoom;

        if(gameMusic.getPosition() > 3.6 && effectiveViewportWidth < app.V_WIDTH) {
            app.camera.zoom += 4f * delta;
            app.camera.zoom = MathUtils.clamp(app.camera.zoom, 0.1f, app.V_WIDTH / app.camera.viewportWidth);
            app.camera.position.x = MathUtils.clamp(app.camera.position.x, effectiveViewportWidth / 2f, app.V_WIDTH - effectiveViewportWidth / 2f);
            app.camera.position.y = MathUtils.clamp(app.camera.position.y, effectiveViewportHeight / 2f, app.V_HEIGHT - effectiveViewportHeight / 2f);
        } else if(gameMusic.getPosition() > 3.6) {
            app.camera.zoom = 1;
            app.camera.setToOrtho(false, app.V_WIDTH, app.V_HEIGHT);
            app.camera.position.set(app.V_WIDTH / 2, app.V_HEIGHT / 2, 0);
            app.camera.update();
            cutsceneShown = true;
        }
    }

    //This method is called by the input adapter if the user touches the screen but did not touch
    //any buttons. The camera projects the touch coordinates into the game world coordinates, these
    //coordinates are then converted to box2D coordinates which are in meters. Then the QueryCallback
    //class is used to find which object was touched. That objects touched method is then called
    private void nonMenuTouchDown(int x, int y, int pointer, int button) {
        gunShot.play();
        touchPoint.set(x, y, 0);
        app.camera.unproject(touchPoint, app.viewport.getScreenX(), app.viewport.getScreenY(),
                app.viewport.getScreenWidth(), app.viewport.getScreenHeight());
        touchPoint.x = touchPoint.x / Constants.PPM;
        touchPoint.y = touchPoint.y / Constants.PPM;

        bodyThatWasTouched = null;
        app.world.QueryAABB(touchQueryCallback, touchPoint.x - 5f, touchPoint.y - 5f, touchPoint.x + 5f, touchPoint.y + 5f);
        if (bodyThatWasTouched != null) {
            objectManager.bodyTouched(bodyThatWasTouched);
        }
    }

    //Deals with the back button
    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) && app.currentScreen == 'h') {
            app.currentScreen = 'm';
            Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
            app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) && app.currentScreen == 's') {
            app.currentScreen = 'm';
            Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
            app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) Gdx.app.exit();
    }
}
