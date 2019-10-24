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

import static java.lang.Integer.parseInt;

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

    int caughtFishesCount = 0;
    int updateCount = 0;
    int skyBgYPos = 0;
    int bgYPosition;
    int bg2YPosition;
    //int fishXPos;
    //int fishYPos;
    Random random;
    public ArrayList<Fish> fishesArray = new ArrayList<Fish>();

    public ArrayList<Fish> rarefishesArray = new ArrayList<Fish>();

    public ArrayList<Fish> goodFishesArray = new ArrayList<Fish>();

    public ArrayList<Fish> badFishesArray = new ArrayList<Fish>();

    float lineEndX;
    float lineEndY;

    float mouseX;
    float mouseY;

    // ----------------------------
    // ## SPRITES
    // ----------------------------

    Bitmap skyBackground;
    Bitmap background;
    Bitmap fisherMan;
    //Bitmap hook;

    Hook hook;

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

        this.lineEndX = this.screenWidth/2;
        this.lineEndY = this.screenHeight/2;


        this.spwnFish();
        this.skyBackground = BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.bg_sky);
        System.out.println("image widtth is :"+this.skyBackground.getWidth()+"");

        this.skyBackground = Bitmap.createScaledBitmap(this.skyBackground, this.screenWidth, this.screenHeight, false);

        this.fisherMan = BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.fisherman);
        this.fisherMan = Bitmap.createScaledBitmap(this.fisherMan,this.screenWidth*3/4,this.screenHeight/3,false);

        this.hook = new Hook(this.getContext(),this.lineEndX,this.lineEndY);
        //this.hook = Bitmap.createScaledBitmap(this.hook)

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
            this.redrawSprites();
            this.updatePositions();
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

//        if (this.gameStarted == false){
//            this.startScreen();
//        }
//        else{

        //int goodFishYPos = this.bgYPosition;


        this.moveFishes();
        this.moveHook();
        this.catchFish();
        this.moveBackground();

        if(this.updateCount >= 70) {
            this.removeFish();
            this.spwnFish();
//        System.out.println("position of good fish: " +this.goodFish.getyPosition() +","+this.goodFish.getxPosition());
        }
        this.updateCount = this.updateCount + 1;
    }

    public void moveFishes(){
        if (this.goodFishesArray.size() != 0) {
            for (int i = 0; i < goodFishesArray.size(); i++) {
                Fish currentGoodFish = this.goodFishesArray.get(i);
                if (currentGoodFish.getFishStatus() != "caught") {
                    currentGoodFish.setxPosition(currentGoodFish.getxPosition() - 20);
                    //this.goodFishesArray.get(i).setyPosition(this.goodFishesArray.get(i).getyPosition() - 30);
                    //this.badFishesArray.get(i).setxPosition(this.goodFishesArray.get(i).getxPosition() - 25);
                }
                if (currentGoodFish.getFishStatus() == "caught"){
                    currentGoodFish.setxPosition(this.hook.getxPosition());
                    currentGoodFish.setyPosition(this.hook.getyPosition());
                }
                if (this.goodFishesArray.get(i).getxPosition() <= (0 - this.goodFishesArray.get(i).getImage().getWidth())) {
                    currentGoodFish.setxPosition(this.screenWidth);
                    //this.goodFishesArray.get(i).setyPosition(800);
                }
                this.goodFishesArray.get(i).updateHitBox();
            }
        }
        if (this.badFishesArray.size() != 0) {
            for (int i = 0; i < badFishesArray.size(); i++) {
                Fish currentbadfish = this.badFishesArray.get(i);
                if (currentbadfish.getFishStatus() != "caught") {
                    currentbadfish.setxPosition(currentbadfish.getxPosition() - 10);
                }
                if (currentbadfish.getFishStatus() == "caught"){
                    currentbadfish.setxPosition(this.hook.getxPosition());
                    currentbadfish.setyPosition(this.hook.getyPosition());
                }
                //this.badFishesArray.get(i).setyPosition(this.badFishesArray.get(i).getyPosition() - 30);
                if (this.badFishesArray.get(i).getxPosition() <= (0 - this.badFishesArray.get(i).getImage().getWidth())) {
                    currentbadfish.setxPosition(this.screenWidth);
                    //this.badFishesArray.get(i).setyPosition(1500);
                }

                this.badFishesArray.get(i).updateHitBox();
            }
        }
    }

    public void moveHook(){
        if ((this.fingerAction == "tapped")||(this.fingerAction == "moving")){
            this.lineEndX = this.mouseX;
            this.hook.setxPosition(this.mouseX);
            this.hook.setyPosition(this.mouseY);
            this.lineEndY = this.mouseY;
            //this.hook.getxPosition() = this.mouseX;
            //this.lineEndY = this.mouseY;
            this.hook.updateHitBox();
        }
    }

    String fishStatus = "";
    public void catchFish(){
        if (this.goodFishesArray.size() != 0) {
            for (int i = 0; i < goodFishesArray.size(); i++) {
                Fish currentGoodFish = this.goodFishesArray.get(i);
                if (this.hook.getHitbox().intersect(currentGoodFish.getHitbox())) {
                    currentGoodFish.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.fish1_caught));
                    currentGoodFish.setxPosition(this.hook.getxPosition());
                    currentGoodFish.setyPosition(this.hook.getyPosition());
                    currentGoodFish.setFishStatus("caught");
                    this.caughtFishesCount = this.caughtFishesCount + 1;
                }
            }

            for (int i = 0; i < badFishesArray.size(); i++) {
                Fish currentBadFish = this.badFishesArray.get(i);
                if (this.hook.getHitbox().intersect(currentBadFish.getHitbox())) {
                    System.out.println(" bad fish Intersected");
                    this.badFishesArray.remove(i);
                    //currentBadFish.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.fish3_caught));
                    //currentBadFish.setFishStatus("caught");
                }
            }
        }

    }

    public void spwnFish(){
        if (this.goodFishesArray.size() + this.badFishesArray.size() < 8){
            float fishXPos = 0 - 200;
            float goodFishYPos = this.bgYPosition;
            for (int i = 0; i < 6; i++) {
                this.goodFishesArray.add(new Fish(this.getContext(), fishXPos, goodFishYPos,R.drawable.fish1));
                if (fishXPos == 0-200) {
                    fishXPos = this.screenWidth - this.goodFishesArray.get(i).getImage().getWidth();
                } else if (fishXPos != 0) {
                    fishXPos = 0 - 200;
                }
                goodFishYPos = goodFishYPos + this.screenHeight / 5;

                if (i % 2 == 0) {
                    float badFishYPos = goodFishYPos + this.goodFishesArray.get(i).getImage().getHeight()*2;
                    this.badFishesArray.add(new Fish(this.getContext(), fishXPos, badFishYPos,R.drawable.fish3));
                }

                System.out.println("Total arrays(both) size : " + (this.goodFishesArray.size() + this.badFishesArray.size()));

            }
            int rareFishX = -200;
            if (this.updateCount%100 == 0){
                this.rarefishesArray.add(new Fish(this.getContext(), rareFishX, goodFishYPos,R.drawable.rare_fish));
            }
        }
    }

    public void spawnFishhhh() {

        if (this.fishesArray.size() < 3) {

            this.random = new Random();

            //creating an array of X coordinates where the fish would be randomly positioned/located.
            int[] intArray = {this.screenWidth, 0};

            //getting a random number from intArray
            int rnd = random.nextInt(intArray.length);

            //creating a array of Fish objects from which a random object would be selected
            Fish[] objectArray = {this.goodFish, this.badFish};

            //getting a random number from 0 to 2
            int rand = this.random.nextInt(objectArray.length);

            // adding a random object to Array List of Objects
            this.fishesArray.add(objectArray[rand]);

            // setting X and Y Coordinates of random object.
            for (int i = 0; i < fishesArray.size(); i++) {
                if (i == 1) {
                    this.fishesArray.get(i).setxPosition(intArray[rnd]);
                    this.fishesArray.get(i).setyPosition(400);
                } else if ((i != 1)&&(i != 0)) {
                    Fish prevFish = fishesArray.get(i-1);
                    this.fishesArray.get(i).setxPosition(intArray[rnd]);
                    this.fishesArray.get(i).setyPosition(prevFish.getyPosition() + 100);
                }
            }

        }
    }

    public void removeFish(){
        //remove fish after it hits edges
        for (int i = 0;i < this.goodFishesArray.size();i++) {
            if (this.goodFishesArray.get(i).getyPosition() <= (0 - this.goodFishesArray.get(i).getImage().getHeight())){
//            ||(this.goodFishesArray.get(i).getxPosition() <= (0 - this.goodFishesArray.get(i).getImage().getWidth()))){
                this.goodFishesArray.remove(i);
            }
        }
        for (int i = 0;i < this.badFishesArray.size();i++) {
            if (this.badFishesArray.get(i).getyPosition() <= (0 - this.badFishesArray.get(i).getImage().getHeight())){
//                ||(this.badFishesArray.get(i).getxPosition() <= (0 - this.badFishesArray.get(i).getImage().getWidth()))) {
                this.badFishesArray.remove(i);
            }
        }

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
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(3);

            //canvas.drawBitmap(goodFish.getImage(), this.goodFish.getxPosition(),this.goodFish.getyPosition(),paintbrush);
            //canvas.drawBitmap(badFish.getImage(), this.badFish.getxPosition(),this.badFish.getyPosition(),paintbrush);

            int count = 0;
            for (int i = 0; i < this.goodFishesArray.size(); i++) {
                this.canvas.drawBitmap(this.goodFishesArray.get(i).getImage(), this.goodFishesArray.get(i).getxPosition(), this.goodFishesArray.get(i).getyPosition(), paintbrush);
//                this.canvas.drawBitmap(this.badFishesArray.get(i).getImage(), this.badFishesArray.get(i).getxPosition(), this.badFishesArray.get(i).getyPosition(), paintbrush);
                this.canvas.drawRect(this.goodFishesArray.get(i).getHitbox(),paintbrush);
                count = count + 1;
            }
            for (int i = 0; i < this.badFishesArray.size(); i++) {
                this.canvas.drawBitmap(this.badFishesArray.get(i).getImage(), this.badFishesArray.get(i).getxPosition(), this.badFishesArray.get(i).getyPosition(), paintbrush);
                this.canvas.drawRect(this.badFishesArray.get(i).getHitbox(),paintbrush);
                count = count + 1;
            }
            System.out.println("no.of fishes: " +count);
           // canvas.drawBitmap(this.fisherMan,this.screenWidth - this.fisherMan.getWidth(),this.bgYPosition - this.fisherMan.getHeight(),paintbrush);
            canvas.drawLine(this.screenWidth/2,0,this.lineEndX,this.lineEndY,paintbrush);
            canvas.drawBitmap(this.hook.getImage(),this.hook.getxPosition() - this.hook.getImage().getWidth()/2 - 20,this.hook.getyPosition() - 10,paintbrush);

            canvas.drawRect(this.hook.getHitbox(),paintbrush);
            //canvas.drawRect(this.badFish.getHitbox(),paintbrush);
            //canvas.drawRect(this.goodFish.getHitbox(),paintbrush);
            

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(2);
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
            this.fingerAction = "tapped";
            this.mouseX = event.getX();
            this.mouseY = event.getY();
            System.out.println(" "+event.getX() +"  "+event.getY());

        }
        else if (userAction == MotionEvent.ACTION_MOVE){
            this.fingerAction = "moving";
                this.mouseX = event.getX();
                this.mouseY = event.getY();

        }
        else if (userAction == MotionEvent.ACTION_UP) {
            this.fingerAction = "untapped";
        }

        return true;
    }
}
