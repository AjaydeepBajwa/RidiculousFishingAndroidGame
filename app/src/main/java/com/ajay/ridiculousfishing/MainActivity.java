package com.ajay.ridiculousfishing;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    GameEngine ridiculousFishing;
    //Boolean playBtnTapped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //his.playBtnTapped = false;

        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
      ridiculousFishing = new GameEngine(this, size.x, size.y);
      ridiculousFishing.playBtnTapped = false;
//
//        // Make GameEngine the view of the Activity
        setContentView(R.layout.activity_main);
    }
    public void startMachineGame(View view){
//        ridiculousFishing = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
        this.ridiculousFishing.playBtnTapped = true;
        this.ridiculousFishing.gunSelected = "machine";
        setContentView(ridiculousFishing);
    }
    public void startSingleShotGame(View view){
//        ridiculousFishing = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
        this.ridiculousFishing.gunSelected = "single";
        this.ridiculousFishing.playBtnTapped = true;
        setContentView(ridiculousFishing);
    }

    // Android Lifecycle function
    @Override
    protected void onResume() {
        super.onResume();
        ridiculousFishing.startGame();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        ridiculousFishing.pauseGame();
    }
}

