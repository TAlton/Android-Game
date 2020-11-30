package com.example.mgdgame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

abstract public class Sensor_t {

    protected final SensorManager SENSOR_MANAGER;
    protected SensorEventListener mSensorEventListener = null;
    protected Sensor mSensor;

    abstract public void setListener(Listener argL);

    protected interface Listener { void onRotation(float argRx, float argRy, float argRz); }

    Sensor_t(Context argContext, int argSensorType) {

        SENSOR_MANAGER = (SensorManager) argContext.getSystemService(Context.SENSOR_SERVICE);

        if (SENSOR_MANAGER.getDefaultSensor(argSensorType) != null) {

            mSensor = SENSOR_MANAGER.getDefaultSensor(argSensorType);

        } else {
            //use rotation sensor
        }
    }
    public void register() { SENSOR_MANAGER.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL); }
    public void unregister() {
        SENSOR_MANAGER.unregisterListener(mSensorEventListener);
    }

}
