package com.mygdx.game.gameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;


public class Ground extends GameObject {


    //class for ground. Not currently being used but might implement it in the future so trucks can
    //drive along the ground
    public Ground(Application app) {
        super(app);
        height = 200;
        width = app.V_WIDTH;

        super.spriteBody = createBody(app.V_WIDTH / 2, 0, width, height, 1, Constants.BIT_GROUND, (short) (Constants.BIT_GROUND | Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST | Constants.BIT_TRUCK), true, false, true, false);
        super.balloonBody = null;
    }

    @Override
    void update(float delta) {

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

    @Override
    public void dispose() {
        if(spriteBody != null) {
            app.world.destroyBody(spriteBody);
            spriteBody = null;
        }
    }
}
