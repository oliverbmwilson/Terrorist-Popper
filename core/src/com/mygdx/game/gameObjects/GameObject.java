package com.mygdx.game.gameObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;

import java.util.Random;

//Each game object is given the following attributes and methods. Although the ground object does not
//use all of them
public abstract class GameObject {
    protected Application app;
    public short id;
    public boolean isDead = false;
    public boolean isShot = false;
    public Body spriteBody, balloonBody;
    public Body[] rope;
    public int width, height;
    public Random random;
    public int hDirection;
    public Sprite sprite;
    public Sprite balloonSprite;
    public Sound splat;


    public GameObject(Application app) {
        this.app = app;
        random = new Random();
        hDirection = random.nextInt(2) - 1;
        rope = new Body[1];
        splat = app.assets.get("splat.mp3", Sound.class);
    }

    abstract void update(float delta);
    abstract void balloonTouched();
    abstract void spriteTouched();
    abstract public void groundCollision();
    abstract public void dispose();

    //creates the main physics body that every sprite is attached to
    public Body createBody(float positionX, float positionY, float width, float height, float density, short cBits, short mBits, boolean polygon, boolean dynamic, boolean fixedRotation, boolean isSensor) {

        Body body;

        //Set up the physical properties of the body
        BodyDef bodyDef = new BodyDef();
        if(dynamic) bodyDef.type = BodyDef.BodyType.DynamicBody;
        else bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(positionX / Constants.PPM, positionY / Constants.PPM);
        if(fixedRotation) bodyDef.fixedRotation = true;
        else bodyDef.fixedRotation = false;

        //Instantiate the body and put it in the world
        body = app.world.createBody(bodyDef);

        //Creates a physical shape for the body
        PolygonShape polyShape = new PolygonShape();
        CircleShape circleShape = new CircleShape();

        polyShape.setAsBox(width / 2 / Constants.PPM, height / 2 / Constants.PPM);
        circleShape.setRadius(width / Constants.PPM);

        //Give the body its shape and density of the shape. Also tell the body which other type of
        //bodies it can collide with
        FixtureDef fixtureDef = new FixtureDef();
        if(polygon) fixtureDef.shape = polyShape;
        else fixtureDef.shape = circleShape;
        fixtureDef.density = density;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = mBits;
        fixtureDef.filter.groupIndex = id;

        body.createFixture(fixtureDef).setUserData(this);

        if(isSensor) body.getFixtureList().peek().isSensor();

        polyShape.dispose();
        circleShape.dispose();

        return body;
    }

    /*
    public Body[] createRope(int length, float positionX, float positionY, float width, float height) {
    */

    //This method creates the rope that connect a game object to its balloon. It uses small rectabngles
    //that are pinned together with revolute joints and flexible rope joints
    public Body[] createRope(int length, float positionX, float positionY, float width, float height) {
        Body[] segments = new Body[length];
        RevoluteJoint[] revoluteJoints = new RevoluteJoint[length - 1];
        RopeJoint[] ropeJoints = new RopeJoint[length - 1];

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(positionX / Constants.PPM, (positionY + sprite.getHeight() / 2) / Constants.PPM);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        for (int i = 0; i < segments.length; i++) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1.0f;
            fixtureDef.filter.categoryBits = spriteBody.getFixtureList().peek().getFilterData().categoryBits;
            fixtureDef.filter.maskBits = spriteBody.getFixtureList().peek().getFilterData().maskBits;
            fixtureDef.filter.groupIndex = id;
            segments[i] = app.world.createBody(bodyDef);
            segments[i].createFixture(fixtureDef).setUserData(this);
        }

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.localAnchorA.y = -height / 2;
        revoluteJointDef.localAnchorB.y = height / 2;

        for(int i = 0; i < revoluteJoints.length; i++) {
            revoluteJointDef.bodyA = segments[i];
            revoluteJointDef.bodyB = segments[i + 1];
            revoluteJoints[i] = (RevoluteJoint) app.world.createJoint(revoluteJointDef);
        }

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.localAnchorA.set(0, -height / 2);
        ropeJointDef.localAnchorB.set(0, height / 2);
        ropeJointDef.maxLength = height;

        for(int i = 0; i < ropeJoints.length; i++) {
            ropeJointDef.bodyA = segments[i];
            ropeJointDef.bodyB = segments[i + 1];
            ropeJoints[i] = (RopeJoint) app.world.createJoint(ropeJointDef);
        }
        return segments;
    }


    public void attachRopeToBalloon(Body[] segments, float segmentHeight) {

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.localAnchorA.y = 0;
        revoluteJointDef.localAnchorB.y = 0;

        revoluteJointDef.bodyA = balloonBody;
        revoluteJointDef.bodyB = segments[0];
        app.world.createJoint(revoluteJointDef);

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = segmentHeight / 2;

        ropeJointDef.bodyA = balloonBody;
        ropeJointDef.bodyB = segments[0];
        app.world.createJoint(ropeJointDef);

    }

    public void attachRopeToSprite(Body[] segments, float segmentHeight) {

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.localAnchorA.y = 0;
        revoluteJointDef.localAnchorB.y = 0;

        revoluteJointDef.bodyA = spriteBody;
        revoluteJointDef.bodyB = segments[segments.length - 1];
        app.world.createJoint(revoluteJointDef);

        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.localAnchorA.set(0, 0);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = segmentHeight / 2;

        ropeJointDef.bodyA = spriteBody;
        ropeJointDef.bodyB = segments[segments.length - 1];
        app.world.createJoint(ropeJointDef);

    }

    //prevents the balloon and the sprite from drifting too far apart
    public void constrainDistances() {

        if(Math.abs(balloonBody.getPosition().y - spriteBody.getPosition().y) > rope.length * 1.5) balloonBody.setLinearVelocity(balloonBody.getLinearVelocity().x, 0);
        if(Math.abs(balloonBody.getPosition().x - spriteBody.getPosition().x) > rope.length * 1.5) balloonBody.setLinearVelocity(0, balloonBody.getLinearVelocity().y);
    }

    //for rendering the rope
    public void drawShapes(ShapeRenderer renderer) {
        if(rope.length > 1) {
            app.shapeRenderer.setColor(Color.BROWN);
            for (int i = 0; i < rope.length; i++) {
                renderer.rect(rope[i].getPosition().x * Constants.PPM, rope[i].getPosition().y * Constants.PPM, .5f * Constants.PPM, 1 * Constants.PPM);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if(balloonSprite != null) balloonSprite.draw(batch);
        if (sprite != null) sprite.draw(batch);
    }

    //Need this method when dealing with comparing two game objects
    @Override
    public boolean equals(Object object) {
        if(!(object instanceof GameObject)) return false;
        GameObject otherGameObject = (GameObject) object;
        if(this.id != otherGameObject.id) return false;
        return true;
    }


}
