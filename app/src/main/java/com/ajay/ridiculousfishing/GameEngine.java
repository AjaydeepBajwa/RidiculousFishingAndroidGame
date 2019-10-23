package com.ajay.ridiculousfishing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="RIDICULOUS-FISHING";

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------

    int skyBgYPos = 0;
    int bgYPosition;
    int bg2YPosition;
    //int fishXPos;
    //int fishYPos;
    Random random;
    public ArrayList<Fish> fishesArray = new ArrayList<Fish>();

    // ----------------------------
    // ## SPRITES
    // ----------------------------

    Bitmap skyBackground;
    Bitmap background;

    Fish goodFish;
    Fish rareFish;
    Fish badFish;

    // represent the TOP LEFT CORNER OF THE GRAPHIC

    // ----------------------------
    // ## GAME STATS
    // ----------------------------


    public GameEngine(Context context, int w, int h) {
        super(context);

        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        this.goodFish = new Fish(context,this.screenWidth,this.bgYPosition,R.drawable.fish1);

        this.skyBackground = BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.bg_sky);
        System.out.println("image widtth is :"+this.skyBackground.getWidth()+"");

        this.skyBackground = Bitmap.createScaledBitmap(this.skyBackground, this.screenWidth, this.screenHeight, false);


        this.bgYPosition = this.skyBackground.getHeight();
        //setup background
        this.background = BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.bg_water);
        System.out.println("image widtth is :"+this.background.getWidth()+"");

        this.background = Bitmap.createScaledBitmap(this.background, this.screenWidth, this.screenHeight*2, false);

        this.bgYPosition = this.skyBackground.getHeight();
        this.bg2YPosition = this.bgYPosition + this.background.getHeight();

        this.printScreenInfo();
    }



    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    public void updatePositions() {


        this.goodFish.setxPosition(this.goodFish.getxPosition()-20);

        if(this.goodFish.getxPosition()<=(0-this.goodFish.getImage().getWidth())){
            this.goodFish.setxPosition(this.screenWidth);
            this.goodFish.setyPosition(800);
        }

        this.moveBackground();
        System.out.println("position of good fish: " +this.goodFish.getyPosition() +","+this.goodFish.getxPosition());
    }

    public void moveBackground(){
        this.skyBgYPos = this.skyBgYPos - 10;
        this.bgYPosition = this.bgYPosition - 30;
        this.bg2YPosition = this.bg2YPosition - 30;


        if ((this.bgYPosition + this.background.getHeight())<=0){
            this.bgYPosition = this.bg2YPosition+this.background.getHeight();
        }
        if ((this.bg2YPosition + this.background.getHeight())<=0){
            this.bg2YPosition = this.bgYPosition + this.background.getHeight();
        }
    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);
            canvas.drawBitmap(this.skyBackground,0,skyBgYPos,paintbrush);
            canvas.drawBitmap(this.background,0,this.bgYPosition,paintbrush);
            canvas.drawBitmap(this.background,0,this.bg2YPosition,paintbrush);


            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);
            
            canvas.drawBitmap(goodFish.getImage(), this.goodFish.getxPosition(),this.goodFish.getyPosition(),paintbrush);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(1);
        }
        catch (Exception e) {
        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


    String fingerAction = "";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {

        }
        else if (userAction == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}
