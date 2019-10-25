package com.ajay.ridiculousfishing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Background {

    // PROPERTIES:
    // Image
    // Hitbox
    private Bitmap image;

    private float xPosition;
    private float yPosition;
    private int imagePath;


    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public Background(Context context, float x, float y, int imagePath) {
        // 1. set up the initial position of the Enemy
        this.xPosition = x;
        this.yPosition = y;
        this.imagePath = imagePath;
        this.image = BitmapFactory.decodeResource(context.getResources(), imagePath);

        // Set the default hitbox - all fishes have same hitbox;
    }

    // Getter and setters
    // Autogenerate this by doing Right Click --> Generate --> Getter&Setter

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

}
