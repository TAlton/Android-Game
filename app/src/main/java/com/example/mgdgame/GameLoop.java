package com.example.mgdgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {

    private static final double MAX_UPS         = 60.0;
    private static final double UPS_PERIOD      = 1000 / MAX_UPS;
    private boolean mIsRunning                  = false;
    private final SurfaceHolder surfaceHolder;
    private final Game game;
    private double mUpdatesPerSecond;
    private double mFramesPerSecond;

    public GameLoop(Game argGame, SurfaceHolder argSurfaceHolder) {

        this.surfaceHolder  = argSurfaceHolder;
        this.game           = argGame;

    }

    public double getUpdatesPerSecond() {
        return mUpdatesPerSecond;
    }
    public double getFramesPerSecond() {
        return mFramesPerSecond;
    }

    public void startLoop() {

        mIsRunning = true;

        start();

    }
    @Override
    public void run() {

        super.run();

        int updateCount     = 0;
        int frameCount      = 0;
        Canvas canvas       = null;
        long mStartTime     = System.currentTimeMillis();

        while(mIsRunning) {


            try { //Update and Render Game

                canvas = surfaceHolder.lockCanvas(); //locks the canvas for draw calls

                synchronized (surfaceHolder) {

                    game.update((float)(0.001 * UPS_PERIOD));
                    updateCount++;
                    game.draw(canvas);

                }
            } catch (Exception e){

                e.printStackTrace();

            } finally {

                if(null != canvas) {

                    try {

                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;

                    } catch (IllegalArgumentException e) {

                        e.printStackTrace();

                    }
                }
            }

            //Capping our game updates a second
            long mElapsedTime   = System.currentTimeMillis() - mStartTime;
            long mSleepTime     = (long) (updateCount * UPS_PERIOD - mElapsedTime);

            if(mSleepTime > 0) {

                try {

                    sleep(mSleepTime);

                } catch (InterruptedException e) {

                    e.printStackTrace();

                }
            }

            //Skip render and update game to achieve our updates/s
            while(mSleepTime < 0  && updateCount < MAX_UPS - 1){

                game.update((float)(0.001 * UPS_PERIOD));
                updateCount++;

                mElapsedTime    = System.currentTimeMillis() - mStartTime;
                mSleepTime      = (long)(updateCount * UPS_PERIOD - mElapsedTime);

            }

            //calculating Updates/s and Frames/s
            if(mElapsedTime >= 1000) {

                mUpdatesPerSecond   = updateCount / (0.001 * mElapsedTime);
                mFramesPerSecond    = frameCount / (0.001 * mElapsedTime);
                updateCount         = 0;
                frameCount          = 0;
                mStartTime          = System.currentTimeMillis();

            }
        }
    }
    public void pause() {

        mIsRunning = false;
        //join the thread before returning, to prevent calling methods on a destroyed surface
        try {

            join();

        } catch(InterruptedException e) {

            e.printStackTrace();

        }
    }
}
