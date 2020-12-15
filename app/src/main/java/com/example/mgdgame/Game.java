package com.example.mgdgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {

    private final RectF DISPLAY_RECT;
    private final Player PLAYER;
    private final int DISPLAY_WIDTH;
    private final int DISPLAY_HEIGHT;
    private AlertDialog mDialog;
    private float mVirusWaitTime;
    private GameLoop mGameLoop;
    private final List<Projectile> LIST_PROJECTILES                 = new ArrayList<>();
    private final List<InterfaceElement> LIST_INTERFACE_ELEMENTS    = new ArrayList<>();
    private final List<Virus> LIST_VIRUSES                          = new ArrayList<>();
    private final int MAX_PROJECTILES                               = 50;
    private boolean mVirusDirLeft                                   = false; //false = left, true = right
    private final float SHAKE_COOLDOWN                              = 25f; //this is the cooldown time of being able to shake
    private float mShakeCooldownTime                                = 0f; //this is the starting cooldown time
    private boolean mShake                                          = false; //is able top shake
    private float mScore                                            = 100;
    private final float RELOAD_FLING_THRESHOLD;
    private final GestureDetectorCompat GESTURE_DETECTOR;
    private final float DISPLAY_WIDTH_ONE_PERCENT;
    private final float DISPLAY_HEIGHT_ONE_PERCENT;
    public boolean mGameEnd                                        = false;

    public float mYaw;
    public float mPitch;
    public float mRoll;
    public float mPlayerLateralMovement;

    @SuppressLint("ClickableViewAccessibility")
    public Game(Context argContext) {

        super(argContext);

        SurfaceHolder surfaceHolder = getHolder();

        surfaceHolder.addCallback(this);

        GESTURE_DETECTOR = new GestureDetectorCompat(getContext(), this);

        this.setOnTouchListener((v, event) -> GESTURE_DETECTOR.onTouchEvent(event));

        this.setOnClickListener(v -> {

        });


        final int VIRUS_ROWS                = 3;
        final int VIRUS_ROW_COUNT           = 1;
        final int VIRUS_OFFSET_PERCENT_X    = 80; //between 0-100
        final float VIRUS_OFFSET_PERCENT_Y  = 30; //^
        DisplayMetrics mDisplay             = getResources().getDisplayMetrics();
        DISPLAY_HEIGHT                      = mDisplay.heightPixels;
        DISPLAY_WIDTH                       = mDisplay.widthPixels;
        DISPLAY_WIDTH_ONE_PERCENT           = (DISPLAY_WIDTH / 100f);
        DISPLAY_HEIGHT_ONE_PERCENT          = (DISPLAY_HEIGHT / 100f);
        DISPLAY_RECT                        = new RectF(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        mGameLoop                           = new GameLoop(this, surfaceHolder);
        PLAYER                              = new Player(getContext(), DISPLAY_WIDTH * 0.5f, DISPLAY_HEIGHT, DISPLAY_WIDTH_ONE_PERCENT * 1f);
        RELOAD_FLING_THRESHOLD              = DISPLAY_WIDTH_ONE_PERCENT * 25f;

        for (int i = 0; i < MAX_PROJECTILES; i++) { //populating the storage of projectile

            LIST_PROJECTILES.add(new Projectile(getContext(), 0, 0, DISPLAY_HEIGHT_ONE_PERCENT * 100f));

        }

        setupUI();

        float lsMaxWidth            = DISPLAY_WIDTH_ONE_PERCENT * VIRUS_OFFSET_PERCENT_X;
        float lsMaxHeight           = DISPLAY_HEIGHT_ONE_PERCENT * VIRUS_OFFSET_PERCENT_Y;
        float lsWidthIncrement      = lsMaxWidth / VIRUS_ROW_COUNT - 1; //-1 because aliens already are offset
        float lsHeightIncrement     = lsMaxHeight / VIRUS_ROWS - 1; //-1 because aliens already are offset
        float lsAlienPosX           = lsWidthIncrement;
        float lsAlienPosY           = lsHeightIncrement;

        for (int i = 0; i < VIRUS_ROWS; i++) {

            for (int j = 0; j < VIRUS_ROW_COUNT; j++) {

                LIST_VIRUSES.add(new Virus(getContext(), lsAlienPosX, lsAlienPosY, DISPLAY_WIDTH_ONE_PERCENT * 1f, DISPLAY_WIDTH_ONE_PERCENT * 7.5f));

                lsAlienPosX += lsWidthIncrement;

            }

            lsAlienPosX = lsWidthIncrement;
            lsAlienPosY += lsHeightIncrement;

        }

        setFocusable(true);

    }
    /*
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            GESTURE_DETECTOR.onTouchEvent(event);
            return super.onTouchEvent(event);
        }
    */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        //handles thread termination if the current activity is paused
        if (mGameLoop.getState().equals(Thread.State.TERMINATED)) {

            mGameLoop = new GameLoop(this, holder);

        }

        mGameLoop.startLoop();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas argCanvas) {

        super.draw(argCanvas);

        if(mGameEnd) return;

        //this can be changed into a function that takes a lambda
        for (int i = 0; i < LIST_PROJECTILES.size(); i++) {

            //list is sorted so as soon as we find an inactive projectile we don't need to draw the rest
            if (!LIST_PROJECTILES.get(i).isActive()) break;

            LIST_PROJECTILES.get(i).draw(argCanvas);

        }

        for (int i = 0; i < LIST_VIRUSES.size(); i++) {

            //list is sorted so as soon as we find an inactive projectile we don't need to draw the rest
            if (!LIST_VIRUSES.get(i).isAlive()) break;

            LIST_VIRUSES.get(i).draw(argCanvas);

        }

        PLAYER.draw(argCanvas);
        drawFPS(argCanvas);
        drawUPS(argCanvas);
        drawScore(argCanvas);
        drawAmmo(argCanvas);

    }

    @SuppressLint("DefaultLocale")
    public void drawScore(Canvas argCanvas) {

        Paint paint = new Paint();
        int lsColor = ContextCompat.getColor(getContext(), R.color.ScoreTextColor);

        paint.setColor(lsColor);
        paint.setTextSize(50);
        argCanvas.drawText("Score: " + String.format("%.0f", mScore), (DISPLAY_WIDTH * 0.5f) - (DISPLAY_WIDTH_ONE_PERCENT * 5f), 50, paint);

    }

    public void drawUPS(Canvas argCanvas) {

        String averageUPS   = Double.toString(mGameLoop.getUpdatesPerSecond());
        Paint paint         = new Paint();
        int lsColor         = ContextCompat.getColor(getContext(), R.color.pink);

        paint.setColor(lsColor);
        paint.setTextSize(30);
        argCanvas.drawText("UPS: " + averageUPS, 100, 50, paint);

    }

    public void drawFPS(Canvas argCanvas) {

        String averageFPS   = Double.toString(mGameLoop.getFramesPerSecond());
        Paint paint         = new Paint();
        int lsColor         = ContextCompat.getColor(getContext(), R.color.pink);

        paint.setColor(lsColor);
        paint.setTextSize(30);
        argCanvas.drawText("FPS: " + averageFPS, 100, 100, paint);

    }

    public void drawYaw(Canvas argCanvas) {

        String Yaw      = Float.toString(this.mYaw);
        Paint paint     = new Paint();
        int lsColor     = ContextCompat.getColor(getContext(), R.color.pink);

        paint.setColor(lsColor);
        paint.setTextSize(30);
        argCanvas.drawText("Yaw: " + Yaw, 600, 50, paint);

    }

    public void drawPitch(Canvas argCanvas) {

        String Pitch    = Float.toString(this.mPitch);
        Paint paint     = new Paint();
        int lsColor     = ContextCompat.getColor(getContext(), R.color.pink);

        paint.setColor(lsColor);
        paint.setTextSize(30);
        argCanvas.drawText("Pitch: " + Pitch, 600, 100, paint);

    }

    public void drawAmmo(Canvas argCanvas) {

        int currentCounterIndex = 1;

        for(int i = 0; i < LIST_INTERFACE_ELEMENTS.size(); i++){
            if(PLAYER.getCurrentAmmo() >= currentCounterIndex && PLAYER.getCurrentAmmo() > 0) {
                LIST_INTERFACE_ELEMENTS.get(i++).draw(argCanvas); //takes over UIElement
                currentCounterIndex++;
                continue;
            }
            LIST_INTERFACE_ELEMENTS.get(++i).draw(argCanvas); //increments to Under UIElement
            currentCounterIndex++;
        }

    }

    public void drawRoll(Canvas argCanvas) {

        String Roll = Float.toString(this.mRoll);
        Paint paint = new Paint();
        int lsColor = ContextCompat.getColor(getContext(), R.color.pink);

        paint.setColor(lsColor);
        paint.setTextSize(30);
        argCanvas.drawText("Roll: " + Roll, 600, 150, paint);

    }

    public void update(float argDT) {

        if(mGameEnd) return;

        mScore -= (1f * argDT);

        if(!LIST_VIRUSES.get(0).isAlive()) { //due to sorting if the first index virus is dead they are all dead

            //endGame();
            showDialog();

        }

        PLAYER.move(Utility.getInstance().validateMovementInBounds(PLAYER, PLAYER.getMoveSpeed() * argDT, PLAYER.getRadius(), DISPLAY_WIDTH, DISPLAY_HEIGHT) ?
                PLAYER.getMoveSpeed() * argDT :
                0f); //if the movement will move the entity offscreen dont allow

        PLAYER.update(argDT);

        //could make this into one loop, might stink
        for(int i = 0; i < LIST_PROJECTILES.size(); i++) { //this can be changed into a function that takes a lambda

            if(!LIST_PROJECTILES.get(i).isActive()) continue; //list is sorted so as soon as we find an inactive projectile we don't need to draw the rest
            //if(mListProj.get(i).IsOutsideDisplay(mDisplayWidth, mDisplayHeight)) { //if the object leaves the display bounds
            if(!DISPLAY_RECT.contains(LIST_PROJECTILES.get(i).getRect())){

                LIST_PROJECTILES.get(i).disable();

                LIST_PROJECTILES.sort((argP1, argP2) -> {
                    //moves the now inactive projectile to the index after the last active bullet,
                    //purpose of this is to break the collision loop when we find an inactive projectile as due to the sort all the other projectiles will be inactive
                    if (argP1.isActive() == argP2.isActive()) return 0;
                    if (argP1.isActive()) return -1;

                    return 1;

                });

                continue;

            }

            LIST_PROJECTILES.get(i).update(argDT); //if a projectile IsActive and is not outside of the display bounds we update it.

        }

        for(int i = 0; i < LIST_PROJECTILES.size(); i++) { //check each projectile

            if(!LIST_PROJECTILES.get(i).isActive()) continue;

            for(int j = 0; j < LIST_VIRUSES.size(); j++) { //against each alien
                //if the virus isnt alive then we dont need to collide

                if(!LIST_VIRUSES.get(j).isAlive()) continue;
                if(LIST_PROJECTILES.get(i).getFaction() == eFaction.VIRUS) continue; //if the projectile is on the same side as the alien dont collide

                if(LIST_PROJECTILES.get(i).getRect().intersect(LIST_VIRUSES.get(j).getRect())) {

                    LIST_VIRUSES.get(j).kill();
                    mScore += LIST_VIRUSES.get(j).getScoreValue();
                    LIST_PROJECTILES.get(i).disable();

                    LIST_VIRUSES.sort((argA1, argA2) -> { //sorting the list by the isAlive variable
                        if (argA1.isAlive() == argA2.isAlive()) return 0;
                        if (argA1.isAlive() && !argA2.isAlive()) return -1;

                        return 1;

                    });

                    //potential to move this into function that takes DrawableEntity
                    LIST_PROJECTILES.sort((argP1, argP2) -> {
                        //moves the now inactive projectile to the index after the last active bullet,
                        //purpose of this is to break the collision loop when we find an inactive projectile as due to the sort all the other projectiles will be inactive
                        if (argP1.isActive() == argP2.isActive()) return 0;
                        if (argP1.isActive()) return -1;

                        return 1;

                    });

                }
            }
        }

        if(mShake && mShakeCooldownTime >= SHAKE_COOLDOWN){ //if the device is rotated fast enough we pause movement for a duration = VIRUS_MOVE_WAIT_TIME

            float VIRUS_MOVE_WAIT_TIME  = 5f;
            mVirusWaitTime              = (mVirusWaitTime < VIRUS_MOVE_WAIT_TIME) ? (mVirusWaitTime + argDT) : 0f; //add dt to wait time till its cooldown time == current timer

            if(0f == mVirusWaitTime){

                mShake                  = false;
                mShakeCooldownTime      = 0f;

            }

        } else {

            if(mShakeCooldownTime < SHAKE_COOLDOWN) {

                mShakeCooldownTime += argDT;
                mShake = false;

            }

            for (int i = 0; i < LIST_VIRUSES.size(); i++) { //virus movement

                if (!LIST_VIRUSES.get(i).isAlive()) continue;
                //if the movement will move the entity offscreen dont allow
                if (!LIST_VIRUSES.get(i).move(Utility.getInstance().validateMovementInBounds(LIST_VIRUSES.get(i), LIST_VIRUSES.get(i).getMoveSpeed() * argDT, LIST_VIRUSES.get(i).getRadius(), DISPLAY_WIDTH, DISPLAY_HEIGHT) ?
                        LIST_VIRUSES.get(i).getMoveSpeed() * argDT :
                        0f)) { //checks if the movement is valid, if it is then it returns the movespeed into move() else it returns 0 into move and we run this body

                    this.mVirusDirLeft = !this.mVirusDirLeft;

                    for (int j = i; j < LIST_VIRUSES.size(); j++) {

                        LIST_VIRUSES.get(i).move(Utility.getInstance().validateMovementInBounds(LIST_VIRUSES.get(i), LIST_VIRUSES.get(i).getMoveSpeed() * argDT, LIST_VIRUSES.get(i).getRadius(), DISPLAY_WIDTH, DISPLAY_HEIGHT) ?
                                LIST_VIRUSES.get(i).getMoveSpeed() * argDT :
                                0f);

                    }
                    for (int k = 0; k < LIST_VIRUSES.size(); k++) {

                        float lsVirusYIncrement = (DISPLAY_HEIGHT / 100f) * 5f;

                        LIST_VIRUSES.get(k).setDirection(mVirusDirLeft);
                        LIST_VIRUSES.get(k).setPosition(mVirusDirLeft ? LIST_VIRUSES.get(k).getPosX() - 10f : LIST_VIRUSES.get(k).getPosX() + 10f, LIST_VIRUSES.get(k).getPosY() + lsVirusYIncrement);

                    }
                }

                if(LIST_VIRUSES.get(i).getRect().intersect(PLAYER.getRect())){ //virus collision with player to end game

                    //endGame();
                    showDialog();

                }

            }
        }

    }


    private void spawnProjectile(eFaction argFaction, float argPosX, float argPosY) {

        Optional<Projectile> lsOptProjectile = LIST_PROJECTILES.stream().filter(p -> !p.isActive()).findFirst();
        Projectile lsProjectile;

        if (!lsOptProjectile.isPresent()) { //if there are no inactive projectiles

            lsProjectile = LIST_PROJECTILES.get(MAX_PROJECTILES - 1);

            if (null == lsProjectile) return; //if getting the last element of mListProj fails

        } else { //if theres an inactive projectile

            lsProjectile = LIST_PROJECTILES.get(LIST_PROJECTILES.indexOf(lsOptProjectile.get()));

        }

        lsProjectile.setPosition(argPosX, argPosY);
        lsProjectile.setFaction(argFaction);
        lsProjectile.setDirection(argFaction == eFaction.PLAYER ? new Vector3(0, -1, 0) : new Vector3(0, 1, 0)); //faction dictates the direction of the projectile
        lsProjectile.Enable(getContext());
        //potential to override this function with a direction

    }

    public void pause() {
        mGameLoop.pause();
    }

    public Player getPlayer() {
        return PLAYER;
    }

    public void setShake(boolean argB) {
        this.mShake = argB;
    }

    public void endGame() {


        //db.addData(etUsername.getText().toString(), lsScoreString);
        //new Firebase_t().add(new Score("test", (int) mScore));

        mGameLoop.interrupt();
        Looper.myLooper().quit();
        ((GameActivity)getContext()).end();



    }

    private void showDialog() { //score submission dialog

        //pause();

        if(null == Looper.myLooper()) {

            Looper.prepare();

        }
        setupHighscoreDialog();
        setFocusable(true);
        mDialog.show();
        Looper.loop();
        //Looper.getMainLooper().quit();

        mGameEnd = true;

    }

    private void setupUI() {

        float lsAmmoOffset      = DISPLAY_WIDTH_ONE_PERCENT * 2f; //offset each element by x% of the screen
        float lsPosY            = DISPLAY_HEIGHT_ONE_PERCENT * 5f; //5% offset from the top of the screen
        int lsAmmoUnderColor    = ContextCompat.getColor(getContext(), R.color.UIAmmoUnder);
        int lsAmmoOverColor     = ContextCompat.getColor(getContext(), R.color.UIAmmoOver);
        float lsAmmoRadius      = DISPLAY_WIDTH_ONE_PERCENT;

        for(int i = 0; i < PLAYER.getMaxAmmo(); i++){

            //max width - (how much distance between each ammo to fill the screen) + offset * the element
            float lsPosX = DISPLAY_WIDTH - ((DISPLAY_WIDTH / (float)PLAYER.getMaxAmmo()) + ((lsAmmoOffset + lsAmmoRadius) * i));

            LIST_INTERFACE_ELEMENTS.add(new InterfaceElement(getContext(), lsPosX, lsPosY, lsAmmoRadius, lsAmmoOverColor));
            LIST_INTERFACE_ELEMENTS.add(new InterfaceElement(getContext(), lsPosX, lsPosY, lsAmmoRadius, lsAmmoUnderColor));

        }

    }

    private void setupHighscoreDialog() {

        LayoutInflater mInflater        = LayoutInflater.from(getContext());
        final View mDialogView          = mInflater.inflate(R.layout.dialog_score_submission, null);
        Button btnSubmit                = mDialogView.findViewById(R.id.btnSubmit);
        EditText etUsername             = mDialogView.findViewById(R.id.etUsername);
        TextView tvScore                = mDialogView.findViewById(R.id.tvScoreValue);
        final String lsScoreString      = Float.toString(mScore);
        tvScore.setText(lsScoreString);
        AlertDialog.Builder mBuilder    = new AlertDialog.Builder(getContext());

        mBuilder.setView(mDialogView);

        mDialog = mBuilder.create();

        mDialog.setCanceledOnTouchOutside(false);
        mDialogView.setFocusable(true);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                endGame();

            }

        });

        btnSubmit.setOnClickListener(view -> {

            Database db = new Database(getContext());

            db.addData(etUsername.getText().toString(), lsScoreString);
            new Firebase_t().add(new Score(etUsername.getText().toString(), (int) mScore));
            mDialog.dismiss();

        });

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        //endGame();
        PLAYER.fire();
        if(PLAYER.getCurrentAmmo() > 0) spawnProjectile(eFaction.PLAYER, PLAYER.getPosX(), PLAYER.getPosY());
        return false;

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
//        spawnProjectile(eFaction.PLAYER, PLAYER.getPosX(), PLAYER.getPosY());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(Math.abs(velocityX + velocityY) > RELOAD_FLING_THRESHOLD) PLAYER.reload();
        return true;
    }
}