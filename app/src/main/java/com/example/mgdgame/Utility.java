package com.example.mgdgame;

public class Utility {

    private static Utility util_instance = null;

    private Utility() {}
    public static Utility getInstance() {

        if(null == util_instance) util_instance = new Utility();

        return util_instance;

    }
    public boolean validateMovementInBounds(DrawableEntity argDE, float argF, float argRadius, float argBoundX, float argBoundY) {

        float lsPredictedPos = argDE.getPosX() + argF;
        //if(lsPredictedPos > mDisplayWidth - mPlayer.getRadius()||
        //        lsPredictedPos < mPlayer.getRadius()) return 0f;

        if(lsPredictedPos > argBoundX - argRadius) {

            argDE.setPosition(argBoundX - argRadius, argDE.getPosY());
            return false;

        }

        if(lsPredictedPos < argRadius) {

            argDE.setPosition(argRadius, argDE.getPosY());
            return false;

        }

        return true;

    }
}
