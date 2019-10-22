package com.ajay.ridiculousfishing;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    GameEngine ridiculousFishing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
        ridiculousFishing = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
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

