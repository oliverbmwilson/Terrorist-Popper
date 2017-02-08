package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Application;

/*
    The loading screen contains an asset manager which
    loads all of the assets before the game begins so that
    they do not have to be loaded mid-game and slow performance
 */
public class LoadingScreen implements Screen {

    private final Application app;
    private ShapeRenderer shapeRenderer;
    private float progress;
    private LoadingScreenHud hud;

    public LoadingScreen(final Application app) {
        this.app = app;
        shapeRenderer = new ShapeRenderer();
        hud = new LoadingScreenHud(app);
    }


    //This method is called when the loading screen starts
    //It loads all of the assets into an asset manager
    private void queueAssets() {
        app.assets.load("Background1080.png", Texture.class);
        app.assets.load("BackgroundMenu1080.png", Texture.class);
        app.assets.load("TerroristGreyMenu1080.png", Texture.class);
        app.assets.load("HostageMenu1080.png", Texture.class);
        app.assets.load("TerroristBlackMenu1080.png", Texture.class);
        app.assets.load("BalloonGreenMenu1080.png", Texture.class);
        app.assets.load("BalloonBlueMenu1080.png", Texture.class);
        app.assets.load("BalloonYellowMenu1080.png", Texture.class);
        app.assets.load("BalloonRedMenu1080.png", Texture.class);
        app.assets.load("Foreground1080.png", Texture.class);
        app.assets.load("BalloonBluePlay1080.png", Texture.class);
        app.assets.load("BalloonRedPlay1080.png", Texture.class);
        app.assets.load("BalloonGreenPlay1080.png", Texture.class);
        app.assets.load("BalloonYellowPlay1080.png", Texture.class);
        app.assets.load("HostagePlay1080.png", Texture.class);
        app.assets.load("TerroristBombPlay1080.png", Texture.class);
        app.assets.load("TerroristGreyPlay1080.png", Texture.class);
        app.assets.load("TerroristBlackPlay1080.png", Texture.class);
        app.assets.load("DeadTerroristGreyMenu1080.png", Texture.class);
        app.assets.load("DeadTerroristBlackMenu1080.png", Texture.class);
        app.assets.load("DeadHostageMenu1080.png", Texture.class);
        app.assets.load("DeadTerroristGreyPlay1080.png", Texture.class);
        app.assets.load("DeadTerroristBlackPlay1080.png", Texture.class);
        app.assets.load("DeadHostagePlay1080.png", Texture.class);
        app.assets.load("DeadTerroristBombPlay1080.png", Texture.class);
        app.assets.load("TerroristBombRedPlay1080.png", Texture.class);
        app.assets.load("explosionAnimation.png", Texture.class);
        app.assets.load("dropsplash.png", Texture.class);
        app.assets.load("gameMusic.mp3", Music.class);
        app.assets.load("menuMusic.mp3", Music.class);
        app.assets.load("gunShot.mp3", Sound.class);
        app.assets.load("explosion.mp3", Sound.class);
        app.assets.load("allahAkbar.mp3", Sound.class);
        app.assets.load("beep.mp3", Sound.class);
        app.assets.load("splat.mp3", Sound.class);
    }

    //Method is called everytime the loading screen is displayed
    //It sets the progress bar to zero and loads the assets (only
    //called once at beginning of game)
    @Override
    public void show() {
        progress = 0f;
        queueAssets();
    }

    //Every cycle update is called. This will increase the
    //progress variable by the amount of progress the asset manager has
    //loaded. This is lerp so that it doesn't go too fast. The hud is also updated.
    //Once the progess bar is full, the screen is set to the mainMenuScreen
    private void update(float delta) {

        progress = MathUtils.lerp(progress, app.assets.getProgress(), .05f);
        if(app.assets.update() && progress >= app.assets.getProgress() - .001f) {
            app.setScreen(app.mainMenuScreen);
        }

        hud.update(delta);
    }

    //This method is called every cycle. It clears the screen and
    //sets it to the clear colour. The camera is projected onto the
    //screen and then the progress bar is drawn with the shapeRenderer
    //The hud is then drawn on top
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 206 / 255f, 98 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        app.shapeRenderer.setProjectionMatrix(app.camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(32, app.V_HEIGHT / 2 - 8, app.V_WIDTH - 64, 32);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(32, app.V_HEIGHT / 2 - 8, progress * (app.V_WIDTH - 64), 32);
        shapeRenderer.end();

        hud.render();
    }

    //Method for resizing the hud and screen viewports when a device with a different
    //screen size is used
    @Override
    public void resize(int width, int height) {
        hud.resize(app.viewport.getScreenWidth(), app.viewport.getScreenHeight());
        app.viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    //Cleans up unused assets
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        hud.dispose();
    }


}
