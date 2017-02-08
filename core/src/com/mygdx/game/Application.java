package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.*;

public class Application extends Game {
    public static boolean DEUBG = false;
    public static boolean GRAVITY = true;
    public static FPSLogger fps;

    public static int V_WIDTH;
    public static int V_HEIGHT;

    public Preferences prefs;

    public OrthographicCamera camera;
    public Viewport viewport;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public BitmapFont font;

    public AssetManager assets;

    private LoadingScreen loadingScreen;
    public MainMenuScreen mainMenuScreen;
    public PlayScreen playScreen;

    public World world;
    public GameContactListener contactListener;
    public Box2DDebugRenderer dbr;

    public char currentScreen;

    @Override
    public void create() {
        //Get saved preferences and catch back button
        Gdx.input.setCatchBackKey(true);
        prefs = Gdx.app.getPreferences("prefs");

        //Set up physcics world and add a physics debug renderer
        if(GRAVITY) world = new World(new Vector2(0, -9.8f), false);
        else world = new World(new Vector2(0, 0f), false);
        contactListener = new GameContactListener(this);
        world.setContactListener(contactListener);
        dbr = new Box2DDebugRenderer();
        dbr.SHAPE_AWAKE.set(Color.BLACK);

        //Set the virtual screen height/width and set up fps logger
        fps = new FPSLogger();
        V_WIDTH = 1780;
        V_HEIGHT = 1080;

        //Create asset manager
        assets = new AssetManager();

        //Set up camera with the virtual width and height/
        //The FitViewport will scale up or down assets
        //depending on resolution and letterbox the screen
        //if it is a different aspect ratio to the virtual
        // width/height
        camera = new OrthographicCamera();
        camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camera);
        viewport.apply();

        //Set up renderers and font
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = createFont();

        //Create the three screens
        loadingScreen = new LoadingScreen(this);
        mainMenuScreen = new MainMenuScreen(this);
        playScreen = new PlayScreen(this);

        //Start the loading screen to load all assets
        this.setScreen(loadingScreen);
        currentScreen = 'l';

    }

    //Dispose method for preventing memory leeks
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        assets.dispose();
        world.dispose();
        shapeRenderer.dispose();
        loadingScreen.dispose();
        mainMenuScreen.dispose();
        playScreen.dispose();
    }

    //method for creating the font used in the game.
    //This has to be loaded here because it is used in
    //the loading screen
    private BitmapFont createFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("psuedoSaudi.TTF"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 130;
        parameter.color = Color.WHITE;
        BitmapFont newFont = generator.generateFont(parameter);
        generator.dispose();
        return newFont;
    }
}
