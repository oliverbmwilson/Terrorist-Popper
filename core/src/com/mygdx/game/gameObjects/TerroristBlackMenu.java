package com.mygdx.game.gameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;


public class TerroristBlackMenu extends GameObject {


    public TerroristBlackMenu(Application app, int terroristX, int terroristY) {
        super(app);
        height = 650;
        width = 200;

        super.spriteBody = createBody(terroristX, terroristY, width, height, 1, Constants.BIT_TERRORIST, (short) (Constants.BIT_GROUND | Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST), true, false, true, false);

        Texture terroristTexture = app.assets.get("TerroristBlackMenu1080.png", Texture.class);
        sprite = new Sprite(terroristTexture);

        super.balloonBody = createBody(terroristX, terroristY + sprite.getHeight() / 2, 100, 100, 1, Constants.BIT_TERRORIST, (short) (Constants.BIT_GROUND | Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST), false, true, false, false);
        balloonBody.setGravityScale(-.115f);

        Texture balloonTextureBlue = app.assets.get("BalloonBlueMenu1080.png", Texture.class);
        Texture balloonTextureRed = app.assets.get("BalloonRedMenu1080.png", Texture.class);
        Texture balloonTextureYellow = app.assets.get("BalloonYellowMenu1080.png", Texture.class);
        Texture balloonTextureGreen = app.assets.get("BalloonGreenMenu1080.png", Texture.class);

        int balloonColour = random.nextInt(4);

        switch(balloonColour) {
            case 0: balloonSprite = new Sprite(balloonTextureBlue);
                break;
            case 1: balloonSprite = new Sprite(balloonTextureRed);
                break;
            case 2: balloonSprite = new Sprite(balloonTextureYellow);
                break;
            case 3: balloonSprite = new Sprite(balloonTextureGreen);
                break;
        }

        rope = createRope(25, terroristX, terroristY, 0.5f, 1);
        attachRopeToBalloon(rope, 1);
        attachRopeToSprite(rope, 1);

    }

    public void update(float delta) {
        if(balloonBody != null && spriteBody != null & rope != null) constrainDistances();

        sprite.setCenter(spriteBody.getPosition().x * Constants.PPM -30, spriteBody.getPosition().y * Constants.PPM);
        sprite.setRotation(spriteBody.getAngle());

        if(balloonSprite != null) {
            balloonSprite.setCenter(balloonBody.getPosition().x * Constants.PPM, balloonBody.getPosition().y * Constants.PPM);
            balloonSprite.setRotation(balloonBody.getAngle());
        }
    }

    public void dispose() {
        for(int i = 0; i < rope.length; i++) {
            if(rope[i] != null) {
                app.world.destroyBody(rope[i]);
                rope[i] = null;
            }
        }
        if(spriteBody != null) {
            app.world.destroyBody(spriteBody);
            spriteBody = null;
        }
        if(balloonBody != null) {
            app.world.destroyBody(balloonBody);
            balloonBody = null;
        }
    }

    @Override
    public void balloonTouched() {
        balloonSprite = null;
        if(balloonBody != null) app.world.destroyBody(balloonBody);
        balloonBody = null;
        spriteBody.setGravityScale(2);

    }

    @Override
    void spriteTouched() {
        Texture terroristTexture = app.assets.get("DeadTerroristBlackMenu1080.png", Texture.class);
        sprite = new Sprite(terroristTexture);
    }

    @Override
    public void groundCollision() {

    }
}
