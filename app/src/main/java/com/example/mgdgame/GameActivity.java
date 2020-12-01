package com.example.mgdgame;

        import android.app.Activity;
        import android.content.pm.ActivityInfo;
        import android.os.Bundle;

//entryPoint
public class GameActivity extends Activity {

    private Game mGame;
    private Sensor_t mGyroscope;
    private Sensor_t mAccelerometer;
    private final float GYROSCOPE_THRESHOLD                 = 7.5f;
    private final float ACCELEROMETER_OUTPUT_MULTIPLIER     = 1000f; //Accelerometer ranges from -9.81 - 9.81

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mGame           = new Game(this);
        mGyroscope      = new Gyroscope(this);
        mAccelerometer  = new Accelerometer(this);

        setContentView(mGame);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mAccelerometer.setListener((argRx, argRy, argRz) -> {

            //Y rotation / the max value normalizes the range, and we multiply it to get whichever range we desire
            mGame.getPlayer().setMoveSpeed((argRy / 9.81f) * ACCELEROMETER_OUTPUT_MULTIPLIER);

        });

        mGyroscope.setListener((argRx, argRy, argRz) -> {

            float lsTotalMotion = Math.abs(argRx) + Math.abs(argRy) + Math.abs(argRz);

            if(lsTotalMotion >= GYROSCOPE_THRESHOLD) mGame.setShake(true);

        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {

        mGame.endGame();
        super.onStop();

    }
    @Override
    protected void onResume() {

        mGyroscope.register();
        mAccelerometer.register();
        super.onResume();

    }
    @Override
    protected void onPause() {

        mGyroscope.unregister();
        mAccelerometer.unregister();
        mGame.pause();
        super.onPause();

    }

}