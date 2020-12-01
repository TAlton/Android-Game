package com.example.mgdgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

public class Player extends DrawableEntity {

    private float mMoveSpeed = 0;
    private static final int MAX_AMMO = 10;
    private int mCurrentAmmoCount = MAX_AMMO;

    public Player(Context argContext, float argPosX, float argPosY, float argRadius) {

        this.mFaction   = eFaction.PLAYER;
        this.mPosX      = argPosX;
        this.mPosY      = argPosY - argRadius - 1f; // -1f for any aliasing or rounding
        this.mRadius    = argRadius;
        int color       = ContextCompat.getColor(argContext, R.color.player);
        mPaint          = new Paint();
        mRect           = new RectF(mPosX - mRadius, mPosY - mRadius, mPosX + mRadius, mPosY + mRadius);

        mPaint.setColor(color);

    }

    @Override
    final public void draw(Canvas argCanvas) {

        argCanvas.drawCircle(mPosX, mPosY, mRadius, mPaint);
        //argCanvas.drawRect(mRect, mPaint);

    }
    @Override
    final public void update(float argDT) {
    }
    public void move(float argX) {

        this.mPosX += argX;
        updateRect();

    }
    public void fire(){}
    public void setMoveSpeed(float argF)    {this.mMoveSpeed = argF;}
    public float getRadius()                {return this.mRadius;}
    public float getMoveSpeed()             {return this.mMoveSpeed;}
    public int getMaxAmmo()                 {return MAX_AMMO;}

}
