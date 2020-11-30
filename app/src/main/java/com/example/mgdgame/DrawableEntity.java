package com.example.mgdgame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class DrawableEntity
{

    protected float mPosX, mPosY;
    protected eFaction mFaction;
    protected Paint mPaint;
    protected RectF mRect;
    protected float mRadius;

    DrawableEntity() {}

    abstract public void draw(Canvas argCanvas);
    abstract public void update(float argDT);

    //get
    public final eFaction getFaction() {return this.mFaction;}
    public final float getPosX() {return this.mPosX;}
    public final float getPosY() {return this.mPosY;}
    public final RectF getRect() {return this.mRect;}
    //set
    public void setPosition(float argX, float argY) {

        this.mPosX = argX;
        this.mPosY = argY;

    }

    public void setFaction(eFaction argFaction) {
        this.mFaction = argFaction;
    }

    protected void updateRect() {
        mRect.offsetTo(mPosX - mRadius, mPosY - mRadius);
    }

}

enum eFaction {DEFAULT, PLAYER, VIRUS}