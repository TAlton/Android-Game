package com.example.mgdgame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class TutorialActivity extends Activity {

    VideoView mVideoView;
    MediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mVideoView = findViewById(R.id.videoView);
        mMediaController = new MediaController(this);

        Uri lsURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mgd_tut);

        mVideoView.setVideoURI(lsURI);
        mVideoView.setMediaController(mMediaController);

        mVideoView.start();

    }

    public void mainMenu(View argView){

        startActivity(new Intent(TutorialActivity.this, MainActivity.class));
        finish();

    }

}