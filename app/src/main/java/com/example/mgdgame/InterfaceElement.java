package com.example.mgdgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

public class InterfaceElement extends DrawableEntity {

    InterfaceElement(Context argContext, float argPosX, float argPosY, float argRadius, int argColor) {

        this.mFaction   = eFaction.UI;
        this.mPosX      = argPosX;
        this.mPosY      = argPosY - argRadius - 1f; // -1f for any aliasing or rounding
        this.mRadius    = argRadius;
        mPaint          = new Paint();

        mPaint.setColor(argColor);

    }
    @Override
    public void draw(Canvas argCanvas) {

        argCanvas.drawCircle(this.mPosX, this.mPosY, this.mRadius, this.mPaint);

    }

    @Override
    public void update(float argDT) {

    }
}
