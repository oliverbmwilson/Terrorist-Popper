package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

//A class for pairing splatter animations with their timing (where they are in their animation cycle)
public class SplatterPair {

    public float stateTimer;
    public Sprite sprite;
    public Animation splatter;
    public int id;
    public boolean splatterStarted;
    public float positionX, positionY;

    public SplatterPair(Application app, float x, float y, int id) {
        this.stateTimer = 0;
        this.id = id;
        this.splatterStarted = false;
        this.positionX = x;
        this.positionY = y;

        Texture splatterImage = app.assets.get("dropsplash.png", Texture.class);
        Array<TextureRegion> splatterFrames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i ++) {
            for(int j = 0; j < 3; j++) {
                splatterFrames.add(new TextureRegion(splatterImage, j * 320, i * 400, 320, 400));
            }
        }
        splatter = new Animation(.1f, splatterFrames);
        this.sprite = new Sprite(splatter.getKeyFrame(stateTimer));
        this.sprite.setCenter(positionX, 440);
    }


    public void update(float delta) {
        //this.sprite.setPosition(positionX, positionY);
        sprite.setRegion(splatter.getKeyFrame(stateTimer));
        stateTimer += delta;
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof SplatterPair)) return false;
        SplatterPair otherPair = (SplatterPair) object;
        if(this.id != otherPair.id) return false;
        return true;
    }
}
