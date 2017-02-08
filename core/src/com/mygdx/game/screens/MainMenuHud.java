package com.mygdx.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Application;


public class MainMenuHud {
    public Stage stage;
    private final Application app;

    private Label title;
    private TextButton newGame;
    private TextButton highScore;
    private TextButton settings;
    private TextButton exit;
    public TextButton resume;
    private Table table;

    public MainMenuHud(final Application app) {
        //Instantiate stage
        this.app = app;
        stage = new Stage(new FitViewport(app.V_WIDTH, app.V_HEIGHT));

        //Set up button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = app.font;

        //Set up layout table
        table = new Table();
        table.top();
        table.setFillParent(true);

        //Instantiate buttons
        title = new Label("Terrorist Popper", new Label.LabelStyle(app.font, Color.RED));
        newGame = new TextButton("New Game", buttonStyle);
        highScore = new TextButton("High Score", buttonStyle);
        settings = new TextButton("Difficulty", buttonStyle);
        exit = new TextButton("Exit", buttonStyle);
        resume = new TextButton("Resume", buttonStyle);
        resume.setVisible(false);

        //Add buttons to table
        table.add(title).expandX();
        table.row();
        table.add(resume).padLeft(20).align(Align.left);
        table.row();
        table.add(newGame).padLeft(20).align(Align.left);
        table.row();
        table.add(highScore).padLeft(20).align(Align.left);
        table.row();
        table.add(settings).padLeft(20).align(Align.left);
        table.row();
        table.add(exit).padLeft(20).align(Align.left);

        //Add table to stage
        stage.addActor(table);
        table.setVisible(false);
        table.addAction(Actions.delay(4f,Actions.visible(true)));


        //Add listeners to Buttons
        resume.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                app.currentScreen = 'p';
                app.setScreen(app.playScreen);
                return true;
            }
        });
        newGame.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                app.currentScreen = 'p';
                app.playScreen.newGame();
                app.setScreen(app.playScreen);
                return true;
            }
        });
        highScore.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                app.currentScreen = 'h';
                app.mainMenuScreen.stage = app.mainMenuScreen.highScoreHud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.highScoreMultiplexer);
                return true;
            }
        });
        settings.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                app.currentScreen = 's';
                app.mainMenuScreen.stage = app.mainMenuScreen.settingsHud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.settingsMultiplexer);
                return true;
            }
        });
        exit.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });
    }

    //Deals with differnet sized screens
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }

    //Cleans up unused assets
    public void dispose() {
        if(stage != null) stage.dispose();
    }
}
