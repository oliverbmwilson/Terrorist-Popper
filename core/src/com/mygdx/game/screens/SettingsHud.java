package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Application;

/**
 * Created by wilsonoliv3 on 28/10/2016.
 */

public class SettingsHud {
    private final Application app;
    public Stage stage;


    private Label title;
    private TextButton easy;
    private TextButton medium;
    private TextButton hard;
    private TextButton chaos;
    private TextButton mainMenu;
    private Table table;
    public int difficulty;

    public SettingsHud(final Application app) {
        difficulty = app.prefs.getInteger("difficulty");

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
        easy = new TextButton("Easy", buttonStyle);
        medium = new TextButton("Medium", buttonStyle);
        hard = new TextButton("Hard", buttonStyle);
        chaos = new TextButton("Chaos", buttonStyle);
        mainMenu = new TextButton("Main menu", buttonStyle);

        //Add buttons to table
        table.add(title).expandX();
        table.row();
        table.add(easy).padLeft(20).align(Align.left);
        table.row();
        table.add(medium).padLeft(20).align(Align.left);
        table.row();
        table.add(hard).padLeft(20).align(Align.left);
        table.row();
        table.add(chaos).padLeft(20).align(Align.left);
        table.row();
        table.add(mainMenu).padLeft(20).align(Align.left);

        //Add table to stage
        stage.addActor(table);


        //Add listeners to Buttons
        easy.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                difficulty = 1;
                app.prefs.putInteger("difficulty", difficulty);
                app.prefs.flush();
                app.currentScreen = 'm';
                app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
                return true;
            }
        });

        medium.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                difficulty = 0;
                app.prefs.putInteger("difficulty", difficulty);
                app.prefs.flush();
                app.currentScreen = 'm';
                app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
                return true;
            }
        });

        hard.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                difficulty = -1;
                app.prefs.putInteger("difficulty", difficulty);
                app.prefs.flush();
                app.currentScreen = 'm';
                app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
                return true;
            }
        });

        chaos.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                difficulty = 10;
                app.prefs.putInteger("difficulty", difficulty);
                app.prefs.flush();
                app.currentScreen = 'm';
                app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
                return true;
            }
        });

        mainMenu.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                app.currentScreen = 'm';
                app.mainMenuScreen.stage = app.mainMenuScreen.hud.stage;
                Gdx.input.setInputProcessor(app.mainMenuScreen.mainMultiplexer);
                return true;
            }
        });
    }

    //Deals with different sized screens
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }

    public void dispose() {
        if(stage != null) stage.dispose();
    }
}
