package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

//A class for determining splatter effects for when the objects hit the ground. Becuase game objects
//are rendered after the foreground, these splatter effects cannot simply be handled by the objects
//themselves because the splatter needs to be rendered after the foreground. An array list is used to
//to store all the currently running splatter animations and their current run time. They are updated
//every cycle
public class SplatterEffect {

    private Application app;
    private List<SplatterPair> splatterEffects;
    private List<SplatterPair> toRemove;
    private int idCounter;

    public SplatterEffect(Application app) {
        this.app = app;
        splatterEffects = new ArrayList<SplatterPair>();
        toRemove = new ArrayList<SplatterPair>();
        idCounter = 0;

    }

    public void splatter(float splatterX, float splatterY) {

        splatterEffects.add(new SplatterPair(app, splatterX, splatterY, idCounter));
        idCounter++;

    }

    public void update(float delta) {
        for(SplatterPair splatterPair : splatterEffects) {
            splatterPair.update(delta);
            if (splatterPair.splatter.isAnimationFinished(splatterPair.stateTimer))
                toRemove.add(splatterPair);
        }
        for(SplatterPair splatterPair : toRemove) {
            if(splatterEffects.contains(splatterPair)) {
                splatterEffects.remove(splatterPair);
            }
        }
        toRemove.clear();
    }

    public void render(SpriteBatch batch) {
        for(SplatterPair splatterPair : splatterEffects) {
            splatterPair.draw(batch);
        }
    }
}
