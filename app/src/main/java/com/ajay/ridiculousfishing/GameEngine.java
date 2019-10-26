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
    MainActivity mainActivity;

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

    Boolean playBtnTapped;
    Boolean moveDown = true;
    int nightBgTimer = 0;
    int fishesCaught = 0;
    int rareFishesCaught = 0;
    int updateCount = 1;
    int bgMoveCounter = 1;
    int skyBgYPos = 0;
    int bgYPosition;
    int bg2YPosition;
    int bgMoveSpeed = 30;
    int skyBgMoveSpeed = 30;

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
    Background background;
    Bitmap fisherMan;

    Hook hook;


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

        //this.playBtnTapped = false;
        this.mainActivity = new MainActivity();

        this.lineEndX = this.screenWidth/2;
        this.lineEndY = this.screenHeight/2;

        //this.spwnFish();
        this.skyBackground = BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.main_bg);
        System.out.println("sky image width is :"+this.skyBackground.getWidth()+"");

        this.skyBackground = Bitmap.createScaledBitmap(this.skyBackground, this.screenWidth, this.screenHeight, false);

        this.fisherMan = BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.fisherman);
        this.fisherMan = Bitmap.createScaledBitmap(this.fisherMan,this.screenWidth*3/4,this.screenHeight/3,false);

        this.hook = new Hook(this.getContext(),this.lineEndX,this.lineEndY);

        this.bgYPosition = this.skyBackground.getHeight();
        //setup background
        this.background = new Background(context,0,this.bgYPosition,R.drawable.bg_water);
        System.out.println("water BG image width is :"+this.background.getImage().getWidth()+"");

        this.background.setImage(Bitmap.createScaledBitmap(this.background.getImage(), this.screenWidth, this.screenHeight*2, false));

        this.bgYPosition = this.skyBackground.getHeight();
        this.bg2YPosition = this.bgYPosition + this.background.getImage().getHeight();

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
            if (this.playBtnTapped == true) {
                this.updatePositions();
                this.setFPS();
                this.redrawSprites();
            }
            System.out.println("playBtnValue : " +this.playBtnTapped.toString());
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

        this.caughtFishesCount();
        this.moveFishes();
        this.moveHook();
        this.catchFish();
        this.moveBackground();

        if(this.updateCount >= 70) {
            this.removeFish();
            this.spawnGoodFish();
            this.spawnBadFish();
            //this.spwnFish();
            //this.spawnRareFish();
        }

        this.showNightbackground();
        if (this.nightBgTimer>0){
            this.nightBgTimer = this.nightBgTimer - 1;
        }
        if (this.nightBgTimer == 1){
            this.showDayBackground();
        }
        this.spawnRareFish();
        this.movetoTop();
        this.updateCount = this.updateCount + 1;
        if (this.moveDown == true){
            this.bgMoveCounter = this.bgMoveCounter + 1;
        }
        if (this.moveDown == false){
            this.bgMoveCounter = bgMoveCounter - 1;
        }
        this.reachTop();
        System.out.println("BGMove Counter :" +this.bgMoveCounter);
        System.out.println("Update count :" +this.updateCount);
    }

    public void moveFishes(){
        if (this.goodFishesArray.size() != 0) {
            for (int i = 0; i < goodFishesArray.size(); i++) {
                Fish currentGoodFish = this.goodFishesArray.get(i);
                if (currentGoodFish.getFishStatus() != "caught") {
                    currentGoodFish.setxPosition(currentGoodFish.getxPosition() - 20);
                    currentGoodFish.setyPosition(currentGoodFish.getyPosition() - this.bgMoveSpeed);
                }
                if (currentGoodFish.getFishStatus() == "caught"){
                    currentGoodFish.setxPosition(this.hook.getxPosition());
                    currentGoodFish.setyPosition(this.hook.getyPosition());
                }
                if (this.goodFishesArray.get(i).getxPosition() <= (0 - this.goodFishesArray.get(i).getImage().getWidth())) {
                    currentGoodFish.setxPosition(this.screenWidth);
                }
                this.goodFishesArray.get(i).updateHitBox();
            }
        }
        if (this.badFishesArray.size() != 0) {
            for (int i = 0; i < badFishesArray.size(); i++) {
                Fish currentbadfish = this.badFishesArray.get(i);
                if (currentbadfish.getFishStatus() != "caught") {
                    currentbadfish.setxPosition(currentbadfish.getxPosition() + 10);
                    currentbadfish.setyPosition(currentbadfish.getyPosition() - bgMoveSpeed);
                }
                if (currentbadfish.getFishStatus() == "caught"){
                    currentbadfish.setxPosition(this.hook.getxPosition());
                    currentbadfish.setyPosition(this.hook.getyPosition());
                }
                if (this.badFishesArray.get(i).getxPosition() <= (0 - this.badFishesArray.get(i).getImage().getWidth())) {
                    currentbadfish.setxPosition(this.screenWidth);
                }

                this.badFishesArray.get(i).updateHitBox();
            }
        }
        if (this.rarefishesArray.size() != 0){
            for (int i=0;i<this.rarefishesArray.size();i++){
                Fish currentRareFish = this.rarefishesArray.get(i);
                if (currentRareFish.getFishStatus() == "rareCaught"){
                    currentRareFish.setxPosition(this.hook.getxPosition());
                    currentRareFish.setyPosition(this.hook.getyPosition());
                }
                else{
                    currentRareFish.setxPosition(currentRareFish.getxPosition() - 10);
                    currentRareFish.setyPosition(currentRareFish.getyPosition() - bgMoveSpeed);
                }
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

    public void catchFish(){
        //if (this.updateCount > 200) {
            //if (this.goodFishesArray.size() != 0) {
            for (int i = 0; i < goodFishesArray.size(); i++) {
                Fish currentGoodFish = this.goodFishesArray.get(i);
                if (this.hook.getHitbox().intersect(currentGoodFish.getHitbox())) {
                    currentGoodFish.setImage(BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.fish1_caught));
                    currentGoodFish.setxPosition(this.hook.getxPosition() - this.hook.getImage().getWidth());
                    currentGoodFish.setyPosition(this.hook.getyPosition() + this.hook.getImage().getHeight() - 20);
                    currentGoodFish.updateHitBox();
                    currentGoodFish.setFishStatus("caught");
                }
            }

            for (int i = 0; i < badFishesArray.size(); i++) {
                Fish currentBadFish = this.badFishesArray.get(i);
                if (this.hook.getHitbox().intersect(currentBadFish.getHitbox())) {
                    System.out.println(" bad fish Intersected");
                    this.badFishesArray.remove(i);
                }
            }
            for (int i = 0; i < this.rarefishesArray.size(); i++) {
                Fish currentrareFish = this.rarefishesArray.get(i);
                if (this.hook.getHitbox().intersect(currentrareFish.getHitbox())) {
                    currentrareFish.setImage(BitmapFactory.decodeResource(this.getContext().getResources(), R.drawable.rare_fish_caught));
                    currentrareFish.setxPosition(this.hook.getxPosition() - this.hook.getImage().getWidth());
                    currentrareFish.setyPosition(this.hook.getyPosition() + this.hook.getImage().getHeight() - 20);
                    currentrareFish.updateHitBox();
                    currentrareFish.setFishStatus("rareCaught");
                    Log.d("RARE FISH CAUGHT", "RARE FISH CAUGHT");
                }
            }
            //}
    }

    public void caughtFishesCount(){
            this.fishesCaught = 0;
            this.rareFishesCaught = 0;
            for (int i = 0; i < this.goodFishesArray.size(); i++) {
                if ((this.goodFishesArray.get(i).getFishStatus() == "caught")) {
                    this.fishesCaught = this.fishesCaught + 1;
                }
            }
            for (int i = 0; i< this.rarefishesArray.size();i++){
                if (this.rarefishesArray.get(i).getFishStatus() == "rareCaught"){
                    this.rareFishesCaught = this.rareFishesCaught + 1;
                }
            }
    }


    public void showNightbackground(){
        if (this.updateCount%250 == 0){
            this.nightBgTimer = 150;
            this.background.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.bg_water_night));
            this.background.setImage(Bitmap.createScaledBitmap(this.background.getImage(), this.screenWidth, this.screenHeight*2, false));
            this.background.setImagePath(R.drawable.bg_water_night);
        }
    }
    public void showDayBackground(){
        this.background.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.bg_water));
        this.background.setImage(Bitmap.createScaledBitmap(this.background.getImage(), this.screenWidth, this.screenHeight*2, false));
        this.background.setImagePath(R.drawable.bg_water);
    }
public void spawnGoodFish(){
        if (this.updateCount%20 == 0){
            for (int i = 0;i<3;i++){
                if (i == 0) {
                    this.goodFishesArray.add(new Fish(this.getContext(), this.screenWidth, this.screenHeight, R.drawable.fish1));
                }
                else{
                    Fish prevFish = this.goodFishesArray.get(i-1);
                    this.goodFishesArray.add(new Fish(this.getContext(),this.screenWidth,prevFish.getyPosition() - prevFish.getImage().getHeight()*4,R.drawable.fish1));
                }
            }
        }
}

public void spawnBadFish(){
    if (this.updateCount%60 == 0){
        for (int i = 0;i<2;i++){
            if (i == 0) {
                this.badFishesArray.add(new Fish(this.getContext(), 0 - 100, this.screenHeight - 500, R.drawable.fish3));
            }
            else{
                Fish prevFish = this.badFishesArray.get(i-1);
                this.badFishesArray.add(new Fish(this.getContext(),this.screenWidth,prevFish.getyPosition() - prevFish.getImage().getHeight()*4,R.drawable.fish3));
            }
        }
    }
}

    public void spawnRareFish(){
        if (this.background.getImagePath() == R.drawable.bg_water_night){
            if (this.rarefishesArray.size() < 1 ) {
                this.rarefishesArray.add(new Fish(this.getContext(), this.screenWidth, this.screenHeight - 100, R.drawable.rare_fish));
                System.out.println("Rare fish count: " + this.rarefishesArray.size());
                Log.d("RARE FISH ADDED", "RARE FISH COUNT " + this.rarefishesArray.size() + "");
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
        this.skyBgYPos = this.skyBgYPos - this.skyBgMoveSpeed;
        this.bgYPosition = this.bgYPosition - this.bgMoveSpeed;
        this.bg2YPosition = this.bg2YPosition - this.bgMoveSpeed;


        if (this.moveDown == true) {
            if ((this.bgYPosition + this.background.getImage().getHeight()) <= 0) {
                this.bgYPosition = this.bg2YPosition + this.background.getImage().getHeight();
            }
            if ((this.bg2YPosition + this.background.getImage().getHeight()) <= 0) {
                this.bg2YPosition = this.bgYPosition + this.background.getImage().getHeight();
            }
        }
        else if (this.moveDown == false){
            if((this.bg2YPosition >= this.screenHeight)){
                this.bg2YPosition = this.bgYPosition - this.background.getImage().getHeight();
            }
            if((this.bgYPosition >= this.screenHeight)){
                this.bgYPosition = this.bg2YPosition - this.background.getImage().getHeight();
            }
        }
    }


    public void movetoTop(){
        if (this.updateCount%350 == 0){
            this.bgMoveSpeed = -80;
            this.skyBgMoveSpeed = -80;
            this.moveDown = false;
        }
    }

    public void reachTop(){
        if (this.bgMoveCounter == 1 ){
            this.background.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),R.drawable.main_bg));
            this.background.setImage(Bitmap.createScaledBitmap(this.background.getImage(), this.screenWidth, this.screenHeight*2, false));
            this.background.setImagePath(R.drawable.main_bg);
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
            //if (this.moveDown = true) {
                canvas.drawBitmap(this.background.getImage(), 0, this.bgYPosition, paintbrush);
                canvas.drawBitmap(this.background.getImage(), 0, this.bg2YPosition, paintbrush);
            //}
//            else if (this.moveDown == false){
//                canvas.drawBitmap(this.background.getImage(), 0, this.bgYPosition, paintbrush);
//                canvas.drawBitmap(this.background.getImage(), 0, this.bg2YPosition, paintbrush);
//            }

            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLACK);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(3);

            int count = 0;
            for (int i = 0; i < this.goodFishesArray.size(); i++) {
                this.canvas.drawBitmap(this.goodFishesArray.get(i).getImage(), this.goodFishesArray.get(i).getxPosition(), this.goodFishesArray.get(i).getyPosition(), paintbrush);
                //this.canvas.drawRect(this.goodFishesArray.get(i).getHitbox(),paintbrush);
                count = count + 1;
            }
            for (int i = 0; i < this.badFishesArray.size(); i++) {
                this.canvas.drawBitmap(this.badFishesArray.get(i).getImage(), this.badFishesArray.get(i).getxPosition(), this.badFishesArray.get(i).getyPosition(), paintbrush);
                //this.canvas.drawRect(this.badFishesArray.get(i).getHitbox(),paintbrush);
                count = count + 1;
            }
            for (int i = 0;i<this.rarefishesArray.size();i++) {
                System.out.println("No.of rare fishes : " +this.rarefishesArray.size());
                canvas.drawBitmap(this.rarefishesArray.get(i).getImage(), this.rarefishesArray.get(i).getxPosition(), this.rarefishesArray.get(i).getyPosition(), paintbrush);
                //this.canvas.drawRect(this.rarefishesArray.get(i).getHitbox(),paintbrush);
                this.rarefishesArray.get(i).updateHitBox();
                count = count + 1;
            }
            System.out.println("no.of fishes: " +count);
           // canvas.drawBitmap(this.fisherMan,this.screenWidth - this.fisherMan.getWidth(),this.bgYPosition - this.fisherMan.getHeight(),paintbrush);
            canvas.drawLine(this.screenWidth/2,0,this.lineEndX,this.lineEndY,paintbrush);
            canvas.drawBitmap(this.hook.getImage(),this.hook.getxPosition() - this.hook.getImage().getWidth()/2 - 20,this.hook.getyPosition() - 10,paintbrush);

           //canvas.drawRect(this.hook.getHitbox(),paintbrush);
            paintbrush.setTextSize(60);
            paintbrush.setColor(Color.YELLOW);
            canvas.drawText("Fishes Caught :" +this.fishesCaught ,30,60,paintbrush);
            canvas.drawText("Rare Fishes Caught: "+this.rareFishesCaught,30,120,paintbrush);

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
