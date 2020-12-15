package com.example.mgdgame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer extends Sensor_t {

    private Listener mListener;
    private static final int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;

    Accelerometer(Context argContext) {

        super(argContext, SENSOR_TYPE);

        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent argEvent) {

                if(mListener != null) {

                    mListener.onRotation(argEvent.values[0],
                            argEvent.values[1],
                            argEvent.values[2]);

                }

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };

    }
    public void setListener(Listener argL) { mListener = argL; }
}
