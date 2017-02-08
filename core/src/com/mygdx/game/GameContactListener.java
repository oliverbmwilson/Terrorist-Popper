package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.gameObjects.GameObject;
import com.mygdx.game.gameObjects.GroundSensor;

//This class handles the cases when an object hits the ground. It is used to determine what kind of
//game object has collided with the ground object so that the correct methods can be called
public class GameContactListener implements ContactListener {

    private final Application app;

    public GameContactListener(final Application app) {
        this.app = app;
    }

    @Override
    public void beginContact(Contact contact) {
        GameObject object;

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA == null || fixtureB == null) return;
        if(fixtureA.getUserData() == null || fixtureB.getUserData() == null) return;
        if(!(fixtureA.getUserData() instanceof GroundSensor) && !(fixtureB.getUserData() instanceof GroundSensor)) return;

        if(fixtureA.getUserData() instanceof GroundSensor) {
            object = (GameObject) fixtureB.getUserData();
            object.groundCollision();
        } else {
            object = (GameObject) fixtureA.getUserData();
            object.groundCollision();
        }


    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
