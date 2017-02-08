package com.mygdx.game.gameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;


public class TerroristBlackPlay extends GameObject {


    public TerroristBlackPlay(Application app, int terroristX, int terroristY, float spriteGravity, float ballonGravity) {
        super(app);
        height = 217;
        width = 66;

        super.spriteBody = createBody(terroristX, terroristY, width, height, 1, Constants.BIT_TERRORIST, (short) (Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST), true, true, false, false);
        spriteBody.setGravityScale(spriteGravity);

        sprite = new Sprite(app.assets.get("TerroristBlackPlay1080.png", Texture.class));

        super.balloonBody = createBody(terroristX, sprite.getHeight() / 2, 50, 50, 1, Constants.BIT_TERRORIST, (short) (Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST), false, true, false, false);
        balloonBody.setGravityScale(ballonGravity);

        Texture balloonTextureBlue = app.assets.get("BalloonBluePlay1080.png", Texture.class);
        Texture balloonTextureRed = app.assets.get("BalloonRedPlay1080.png", Texture.class);
        Texture balloonTextureYellow = app.assets.get("BalloonYellowPlay1080.png", Texture.class);
        Texture balloonTextureGreen = app.assets.get("BalloonGreenPlay1080.png", Texture.class);

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

        rope = createRope(12, terroristX, terroristY, 0.5f, 1);
        attachRopeToBalloon(rope, 1);
        attachRopeToSprite(rope, 1);

    }

    public void update(float delta) {
        if(balloonBody != null && spriteBody != null & rope != null) constrainDistances();

        sprite.setCenter(spriteBody.getPosition().x * Constants.PPM - 15, spriteBody.getPosition().y * Constants.PPM);
        sprite.setRotation(spriteBody.getAngle() * MathUtils.radiansToDegrees);


        if(balloonSprite != null) {
            balloonSprite.setCenter(balloonBody.getPosition().x * Constants.PPM, balloonBody.getPosition().y * Constants.PPM);
            balloonSprite.setRotation(balloonBody.getAngle());
        }

        if(balloonBody != null) {
            int hForce = random.nextInt(15);
            balloonBody.applyForceToCenter(hForce * hDirection * Constants.PPM, 0, false);
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
    void balloonTouched() {
        balloonSprite = null;
        if(balloonBody != null) app.world.destroyBody(balloonBody);
        balloonBody = null;
        spriteBody.setGravityScale(4);
        Filter filter = new Filter();
        filter.categoryBits = Constants.BIT_TERRORIST;
        filter.maskBits = (short) (Constants.BIT_GROUND | Constants.BIT_GROUNDSENSOR | Constants.BIT_TRUCK);
        filter.groupIndex = id;
        spriteBody.getFixtureList().peek().setFilterData(filter);
        for(int i = 0; i < rope.length; i++) {
            rope[i].getFixtureList().peek().setFilterData(filter);
        }
    }

    @Override
    void spriteTouched() {
        app.playScreen.hud.score += 20;
        Texture terroristTexture = app.assets.get("DeadTerroristBlackPlay1080.png", Texture.class);
        sprite = new Sprite(terroristTexture);
        isShot = true;
    }

    @Override
    public void groundCollision() {
        if(balloonSprite == null) {
            app.playScreen.hud.score += 20;
            if(sprite !=null) app.playScreen.splatterEffect.splatter (sprite.getX(), sprite.getY());
            balloonSprite = null;
            sprite = null;
            isDead = true;
        }
    }
}
