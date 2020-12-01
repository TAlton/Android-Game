package com.example.mgdgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

public class Virus extends DrawableEntity {

    public boolean mLeft            = false;
    private boolean mDisabled       = false;
    private boolean mAlive          = true;
    private float mWaitTimer;
    public float mMoveSpeed;

    Virus(Context argContext, float argPosX, float argPosY, float argRadius, float argMoveSpeed) {

        this.mFaction   = eFaction.VIRUS;
        this.mPosX      = argPosX;
        this.mPosY      = argPosY - argRadius - 1f; // -1f for any aliasing or rounding
        this.mRadius    = argRadius;
        this.mMoveSpeed = argMoveSpeed;
        mPaint          = new Paint();
        int color       = ContextCompat.getColor(argContext, R.color.virus);
        mRect           = new RectF(mPosX - mRadius, mPosY - mRadius, mPosX + mRadius, mPosY + mRadius);

        mPaint.setColor(color);

    }
    @Override
    public void draw(Canvas argCanvas) {
        argCanvas.drawCircle(mPosX, mPosY, mRadius, mPaint);
    }
    @Override
    public void update(float argDT) {}
    public boolean move(float argF) {

        if (0f == argF) return false;
        if(mLeft) this.mPosX -= argF;
        if(!mLeft) this.mPosX += argF;

        updateRect();
        return true;

    }
    public boolean wait(float argDT) {

        float TIME_TO_WAIT = 1000f;

        if(mWaitTimer >= TIME_TO_WAIT) {

            mWaitTimer = 0f;
            return true;

        }

        mWaitTimer += argDT;

        return false;

    }
    public float getRadius()                    {return this.mRadius;}
    public boolean isDisabled()                 {return this.mDisabled;}
    public boolean isAlive()                    {return this.mAlive;}
    public float getMoveSpeed()                 {return this.mMoveSpeed;}
    public void kill()                          {this.mAlive = false;}
    public void disable()                       {this.mDisabled = true;}
    public void enable()                        {this.mDisabled = false;}
    public void setDirection(boolean argLeft)   {this.mLeft = argLeft;}
    public int getScoreValue() {

        //score value is only ever referenced here
        return 25;

    }
}
