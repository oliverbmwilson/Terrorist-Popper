package com.mygdx.game.screens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Application;


public class PlayScreenHud {
    public Stage stage;
    private final Application app;

    private Label scorer;
    private Label timer;
    private TextButton back;
    private TextButton pauseButon;
    public Label pauseLabel;
    private Table table1;
    private Table table2;
    private Table table3;

    public float time;
    public int score;
    public boolean paused = false;
    public float interval;

    public PlayScreenHud(final Application app) {
        //Instantiate stage
        this.app = app;
        stage = new Stage(new FitViewport(app.V_WIDTH, app.V_HEIGHT));

        //Set up button and label style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = app.font;
        buttonStyle.fontColor = Color.WHITE;
        Label.LabelStyle labelStyle = new Label.LabelStyle(app.font, Color.RED);

        //Set up layout tables
        table1 = new Table();
        table1.top();
        table1.setFillParent(true);

        //Set up layout tables
        table2 = new Table();
        table2.setFillParent(true);

        //Set up layout tables
        table3 = new Table();
        table3.bottom();
        table3.setFillParent(true);

        //Instantiate buttons and labels

        scorer = new Label("Score: " + score, labelStyle);
        timer = new Label("Time: " + time, labelStyle);
        pauseLabel = new Label("", labelStyle);
        pauseButon = new TextButton("Pause", buttonStyle);
        back = new TextButton("Back", buttonStyle);

        //Add buttons to table
        table1.add(scorer).padLeft(20).align(Align.topLeft).expand();
        table1.add(timer).padRight(20).align(Align.topRight);
        table2.add(pauseLabel).align(Align.center).expand();
        table3.add(back).padLeft(20).align(Align.bottomLeft).expand();
        table3.add(pauseButon).padLeft(20).align(Align.bottomRight);

        //Add table to stage
        stage.addActor(table1);
        stage.addActor(table2);
        stage.addActor(table3);
        //stage.addActor(buttonTable);

        //Add listeners to Buttons
        back.addListener( new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(!paused) paused = true;
                app.playScreen.gameMusic.pause();
                app.currentScreen = 'm';
                app.setScreen(app.mainMenuScreen);
                return true;
            }
        });
        pauseButon.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(app.playScreen.gameOver) return true;
                if(paused) {
                    paused = false;
                    pauseLabel.setText("");
                    app.playScreen.gameMusic.play();
                    app.playScreen.objectManager.unpause();
                } else {
                    paused = true;
                    pauseLabel.setText("Paused");
                    app.playScreen.gameMusic.pause();
                    app.playScreen.objectManager.pause();
                }
                return true;
            }
        });
    }

    public void update(float delta) {

        interval += delta;
        scorer.setText("Score: " + score);

        if(interval >= 1) {
            timer.setText("Time: " + String.format("%.0f", time));
            interval = 0;
        }
        stage.act(delta);

    }

    public void render() {
        stage.draw();

    }

    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
    }

    public void dispose() {
        if(stage != null) stage.dispose();
    }
}