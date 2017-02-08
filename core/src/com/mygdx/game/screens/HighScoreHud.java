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

public class HighScoreHud {

    private final Application app;
    public Stage stage;


    private Label title;
    public Label highScore;
    private TextButton mainMenu;
    private Table table;
    public int score;

    public HighScoreHud(final Application app) {
        score = app.prefs.getInteger("score");

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
        highScore = new Label("High score: " + score, new Label.LabelStyle(app.font, Color.WHITE));
        mainMenu = new TextButton("Main menu", buttonStyle);

        //Add buttons to table
        table.add(title).expandX();
        table.row();
        table.add(highScore).padLeft(20).align(Align.bottomLeft);
        table.row();
        table.add(mainMenu).padLeft(20).align(Align.bottomLeft);

        //Add table to stage
        stage.addActor(table);


        //Add listeners to Buttons
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
