package com.example.mgdgame;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;

//entryPoint
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }
    public void startGame(View argView){
        startActivity(new Intent(MainActivity.this, GameActivity.class));
    }
    public void highScores(View argView){
        startActivity(new Intent(MainActivity.this, HighscoreActivity.class));
    }

}