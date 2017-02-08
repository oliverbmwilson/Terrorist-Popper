package com.mygdx.game.gameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;


public class GroundSensor extends GameObject {

    //Class for the ground sensor. It only needs a body, no textures
    public GroundSensor(Application app, int height) {
        super(app);
        this.height = height;
        width = app.V_WIDTH;

        super.spriteBody = createBody(app.V_WIDTH / 2, 0, width, height, 1, Constants.BIT_GROUNDSENSOR, (short) (Constants.BIT_HOSTAGE | Constants.BIT_BALLOON | Constants.BIT_ROPE | Constants.BIT_TERRORIST), true, false, true, true);

    }

    @Override
    void update(float delta) {

    }

    @Override
    public void dispose() {
        if(spriteBody != null) {
            app.world.destroyBody(spriteBody);
            spriteBody = null;
        }
    }

    @Override
    void balloonTouched() {

    }

    @Override
    void spriteTouched() {

    }

    @Override
    public void groundCollision() {

    }
}
