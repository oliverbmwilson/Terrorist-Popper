package com.mygdx.game;

//These constants are used for the physics engine. The PPM is pixels per meter and is used to
//convert pixels into meters (as this is what the physics engine deals with). The BIT shorts are
//catergories that are used when the physics engine computes bit shifts to determine which objects
//should collide with one another and which shouldn't
public class Constants {

    public static final float PPM = 20f;
    public static final short BIT_TERRORIST = 2;
    public static final short BIT_HOSTAGE = 4;
    public static final short BIT_GROUND = 8;
    public static final short BIT_GROUNDSENSOR = 16;
    public static final short BIT_TRUCK = 32;
    public static final short BIT_ROPE = 64;
    public static final short BIT_BALLOON = 128;


}
