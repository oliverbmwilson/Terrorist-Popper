package com.mygdx.game.gameObjects;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;


public class TerroristBombPlay extends GameObject {

    public float countDown;

    public Sound beep;
    private Sound explosionSound;

    public Animation explosionAnimation;
    private Texture terroristTexture;

    private float stateTimer;

    private boolean beeped = false, explosionFinished = false, exploded = false, newSprite = true;
    private float explosionX = 0, explosionY = 0;

    public TerroristBombPlay(Application app, int terroristX, int terroristY, float spriteGravity, float balloonGravity) {
        super(app);
        height = 217;
        countDown = random.nextInt(3) + 2;
        width = 66;
        stateTimer = 0;

        super.spriteBody = createBody(terroristX, terroristY, width, height, 1, Constants.BIT_TERRORIST, (short) (Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST), true, true, false, false);
        spriteBody.setGravityScale(spriteGravity);

        terroristTexture = app.assets.get("TerroristBombPlay1080.png", Texture.class);
        sprite = new Sprite(terroristTexture);

        //Extra constructor code for creating explosion animation
        Texture explosionImage = app.assets.get("explosionAnimation.png", Texture.class);
        Array<TextureRegion> explosionFrames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i ++) {
            for(int j = 0; j < 4; j++) {
                explosionFrames.add(new TextureRegion(explosionImage, j * 256, i * 256, 256, 256));
            }
        }
        explosionAnimation = new Animation(0.1f, explosionFrames);

        super.balloonBody = createBody(terroristX, terroristY + sprite.getHeight() / 2, 50, 50, 1, Constants.BIT_TERRORIST, (short) (Constants.BIT_HOSTAGE | Constants.BIT_TERRORIST), false, true, false, false);
        balloonBody.setGravityScale(balloonGravity);

        Texture balloonTextureBlue = app.assets.get("BalloonBluePlay1080.png", Texture.class);
        Texture balloonTextureRed = app.assets.get("BalloonRedPlay1080.png", Texture.class);
        Texture balloonTextureYellow = app.assets.get("BalloonYellowPlay1080.png", Texture.class);
        Texture balloonTextureGreen = app.assets.get("BalloonGreenPlay1080.png", Texture.class);
        beep = app.assets.get("beep.mp3", Sound.class);
        explosionSound = app.assets.get("explosion.mp3", Sound.class);

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

    //Along with how the other sprite are dealt with in the update phase, this sprite also has
    //it's explosion sequence monitored and executed when needed in this method
    public void update(float delta) {
        countDown -= delta;

        if(balloonBody != null && spriteBody != null & rope != null) constrainDistances();

        if(exploded && !explosionFinished) {
            sprite.setCenter(explosionX * Constants.PPM, explosionY * Constants.PPM);
        } else {
            sprite.setCenter(spriteBody.getPosition().x * Constants.PPM, spriteBody.getPosition().y * Constants.PPM);
            sprite.setRotation(spriteBody.getAngle() * MathUtils.radiansToDegrees);
        }

        if(balloonSprite != null) {
            balloonSprite.setCenter(balloonBody.getPosition().x * Constants.PPM, balloonBody.getPosition().y * Constants.PPM);
            balloonSprite.setRotation(balloonBody.getAngle());
        }

        if(balloonBody != null) {
            int hForce = random.nextInt(15);
            balloonBody.applyForceToCenter(hForce * hDirection * Constants.PPM, 0, false);
        }

        if(exploded && !explosionFinished) {
            sprite.setRegion(explosionAnimation.getKeyFrame(stateTimer));
            if(explosionAnimation.isAnimationFinished(stateTimer)) {
                explosionFinished = true;
           }
            stateTimer += delta;
        }

        if(isShot) {
            Texture terroristTexture = app.assets.get("DeadTerroristBombPlay1080.png", Texture.class);
            sprite.setRegion(terroristTexture);
        }

        if(countDown <= 0.25 && !isShot && !beeped) {
            beep.play();
            beeped = true;
        }

        if(countDown <= 0 && !isShot && !exploded) {
            stateTimer = 0;
            explosionX = spriteBody.getPosition().x;
            explosionY = spriteBody.getPosition().y;
            spriteBody.setActive(false);
            explode(1000, 10000, 500000, spriteBody.getPosition().x, spriteBody.getPosition().y);
            exploded = true;
            explosionSound.play();

        }

        if(explosionFinished) {
            balloonSprite = null;
            sprite = null;
            isDead = true;
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
        app.playScreen.hud.score += 10;
        Texture terroristTexture = app.assets.get("DeadTerroristBombPlay1080.png", Texture.class);
        sprite = new Sprite(terroristTexture);
        isShot = true;
    }

    @Override
    public void groundCollision() {
        if(balloonSprite == null) {
            app.playScreen.hud.score += 20;
            if(sprite !=null) app.playScreen.splatterEffect.splatter (sprite.getX(), sprite.getY());
            splat.play();
            balloonSprite = null;
            sprite = null;
            isDead = true;
        }
    }

    //Method for raycasting the explosion from the center of the sprite
    //Method taken from FinnSTR Game Development. Published May 30, 2016. Retrieved on October 26, 2016
    // from http://www.finnstr.com/tutorial-box2d-explosion-in-libgdx/
    public void explode(final int numRays, float blastRadius, final float blastPower, float posX, float posY) {
        final Vector2 center = new Vector2(posX, posY);
        Vector2 rayDir = new Vector2();
        Vector2 rayEnd = new Vector2();

        for(int i = 0; i < numRays; i++) {
            float angle = (i / (float) numRays) * 360 * MathUtils.degreesToRadians;
            rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
            rayEnd.set(center.x + blastRadius * rayDir.x, center.y + blastRadius * rayDir.y);

            RayCastCallback callback = new RayCastCallback() {
                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point,
                                              Vector2 normal, float fraction) {
                    applyBlastImpulse(fixture.getBody(), center, point, blastPower / (float)numRays);
                    return 0;
                }
            };
            app.world.rayCast(callback, center, rayEnd);
        }
    }

    //Method for applying the impulse to another game object when the explosion hits it. It will also destory
    // its balloonmost of this method is taken from FinnSTR Game Development. Published May 30, 2016. Retrieved on October 26, 2016
    // from http://www.finnstr.com/tutorial-box2d-explosion-in-libgdx/
    public void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        if(app.playScreen.objectManager.checkBalloons(body)) return;

        Vector2 blastDir = applyPoint.cpy().sub(blastCenter);
        float distance = blastDir.len();
        if(distance == 0) return;

        float invDistance = 1f / distance;
        float impulseMag = Math.min(blastPower * invDistance, blastPower * 0.5f);

        body.applyLinearImpulse(blastDir.nor().scl(impulseMag), applyPoint, true);
    }

}
