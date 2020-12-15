package com.example.mgdgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

public class Projectile extends DrawableEntity {

    private boolean mActive             = false;
    private float mProjSpeed            = 0f;
    private final int WIDTH             = 5;
    private final int HEIGHT            = 15;
    private Vector3 mDirection;

    Projectile(Context argContext, float argPosX, float argPosY, float argProjSpeed){

        this.mPosX      = argPosX;
        this.mPosY      = argPosY;
        this.mProjSpeed = argProjSpeed;
        this.mPaint     = new Paint();
        this.mFaction   = eFaction.DEFAULT;
        this.mRect      = new RectF(mPosX - WIDTH, mPosY - HEIGHT, mPosX + WIDTH, mPosY + HEIGHT);

    }
    @Override
    public void draw(Canvas argCanvas) {

        argCanvas.drawOval(this.mRect, mPaint);
        //argCanvas.drawRect(mRect, mPaint);

    }
    @Override
    public void update(float argDT) {

        this.mPosY += mDirection.y * (mProjSpeed * argDT);
        updateRect();

    }
    @Override
    protected void updateRect() {

        this.mRect = new RectF(mPosX - WIDTH, mPosY - HEIGHT, mPosX + WIDTH, mPosY + HEIGHT);

    }
    public void Enable(Context argContext){ //always enable after initialising

        this.mActive    = true;
        int lsColor     = mFaction == eFaction.PLAYER ? //if
                            ContextCompat.getColor(argContext, R.color.ProjectilePlayer) : //then
                            ContextCompat.getColor(argContext, R.color.ProjectileVirus); //else

        mPaint.setColor(lsColor);
        updateRect();

    }
    public void disable() {

        this.mActive    = false;
        this.mFaction   = eFaction.DEFAULT;

        this.setPosition(0, 0);
        this.setDirection(new Vector3(0,0,0));

    }
    public final boolean isActive()             {return this.mActive;}
    public void setDirection(Vector3 argV3)     {this.mDirection = argV3;}
    public boolean isOutsideDisplay(int argWidth, int argHeight) {

        return (this.mPosY - HEIGHT) >= argHeight ||
                (this.mPosY + HEIGHT) <= 0;

    }
}


