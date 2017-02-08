package com.mygdx.game.gameObjects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.Application;
import com.mygdx.game.Constants;

import java.util.ArrayList;
import java.util.List;

/*
    This class handles everything to do with the game objects. All game objects are stored in an
    array list and iterated over when they need to be updated / removed each cycle
 */
public class ObjectManager {
    Application app;

    private short idCount;

    public List<GameObject> gameObjects;
    public List<GameObject> toRemove;

    public ObjectManager(Application app) {
        this.app = app;
        idCount = 1;

        gameObjects = new ArrayList<GameObject>();
        toRemove = new ArrayList<GameObject>();
    }

    //removes any game objects that are outside the screen or that are "dead", otherwise it will
    //update them
    public void update(float delta) {
        for(GameObject gameObject : gameObjects) {
            if(gameObject.spriteBody != null) {
                Body body = gameObject.spriteBody;
                if (body.getPosition().y * Constants.PPM > app.V_HEIGHT + gameObject.height / 2)
                    gameObject.isDead = true;
                if (body.getPosition().y * Constants.PPM < 0 - gameObject.height / 2)
                    gameObject.isDead = true;
                if (body.getPosition().x * Constants.PPM > app.V_WIDTH) gameObject.isDead = true;
                if (body.getPosition().x * Constants.PPM < 0) gameObject.isDead = true;
            }
            if(gameObject.isDead) toRemove.add(gameObject);
            else gameObject.update(delta);
        }
        for(GameObject gameObject : toRemove) {
            if(gameObjects.contains(gameObject)) {
                for(int i = 0; i < gameObject.rope.length; i++) {
                    if(gameObject.rope[i] != null) app.world.destroyBody(gameObject.rope[i]);
                }
                if(gameObject.spriteBody != null) app.world.destroyBody(gameObject.spriteBody);
                if(gameObject.balloonBody != null) app.world.destroyBody(gameObject.balloonBody);
                gameObjects.remove(gameObject);
            }
        }
        toRemove.clear();
    }

    //renders all of the objects
    public void drawObjects() {
        if(app.mainMenuScreen.cutsceneShown) {
            app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (GameObject object : gameObjects) {
                object.drawShapes(app.shapeRenderer);
            }
            app.shapeRenderer.end();
        }

        app.batch.begin();
        for(GameObject object : gameObjects) {
            object.draw(app.batch);
        }
        app.batch.end();
    }

    public void addGameObject(GameObject gameObject) {
        gameObject.id = idCount;
        gameObjects.add(gameObject);
        idCount++;
    }

    //This method is called when the physics engine has located a body that has been touched. This
    //method finds the game object that the body belongs to and calls its method
    public void bodyTouched(Body body) {
        for(GameObject gameObject : gameObjects) {
            if(gameObject.spriteBody != null && gameObject.spriteBody == body) {
                gameObject.spriteTouched();
                return;
            }
            if(gameObject.balloonBody != null && gameObject.balloonBody == body) {
                gameObject.balloonTouched();
                return;
            }
        }
    }

    //This method is called when an explosion goes off. When the explosion hits a body, it is checked
    //to see if it is a balloon. If so, the balloon is removed
    public boolean checkBalloons(Body body) {
        for(GameObject gameObject : gameObjects) {
            if(gameObject.balloonBody != null && gameObject.balloonBody == body) {
                gameObject.balloonTouched();
                return true;
            }
        }
        return false;
    }

    //When the game is paused this method sets all of the bodies in the game to inactive so that they
    //are not updated
    public void pause() {
        for(GameObject gameObject : gameObjects) {
            if(gameObject.spriteBody != null) gameObject.spriteBody.setActive(false);
            if(gameObject.balloonBody != null) gameObject.balloonBody.setActive(false);
            if(gameObject.rope != null) {
                for(int i = 0; i < gameObject.rope.length; i++) {
                    if(gameObject.rope[i] != null) {
                        gameObject.rope[i].setActive(false);
                    }
                }
            }
        }
    }

    //sets the bodies active again
    public void unpause() {
        for(GameObject gameObject : gameObjects) {
            if(gameObject.spriteBody != null) gameObject.spriteBody.setActive(true);
            if(gameObject.balloonBody != null) gameObject.balloonBody.setActive(true);
            if(gameObject.rope != null) {
                for(int i = 0; i < gameObject.rope.length; i++) {
                    if(gameObject.rope[i] != null) {
                        gameObject.rope[i].setActive(true);
                    }
                }
            }
        }
    }

    //Removes all game objects from the game
    public void dispose() {
        for(GameObject gameObject : gameObjects) {
            gameObject.dispose();
        }
        for(GameObject gameObject : toRemove) {
            gameObject.dispose();
        }

        gameObjects.clear();
        toRemove.clear();
    }
}
