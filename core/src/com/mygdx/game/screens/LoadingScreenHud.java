package com.mygdx.game.screens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Application;

/*
    This class takes care of the layout of the loading screen
 */

public class LoadingScreenHud {

    private final Application app;

    private Label label;
    private Table table;
    private Stage stage;

    public LoadingScreenHud(final Application app) {
        this.app = app;
        stage = new Stage(new FitViewport(app.V_WIDTH, app.V_HEIGHT));

        label = new Label("Loading Game Assets", new Label.LabelStyle(app.font, Color.RED));
        table = new Table();
        table.setFillParent(true);
        table.add(label).padLeft(20).expand().align(Align.bottomLeft);

        stage.addActor(table);
    }

    public void update(float delta) {
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
